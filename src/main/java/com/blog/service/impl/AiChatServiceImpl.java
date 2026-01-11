package com.blog.service.impl;

import com.blog.pojo.dto.AiChatDTO;
import com.blog.pojo.vo.AiChatVO;
import com.blog.service.AiChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * AI 对话服务实现 - 调用硅基流动 API
 * 
 * @author worlt
 */
@Service
@Slf4j
public class AiChatServiceImpl implements AiChatService {

    @Value("${siliconflow.api-key}")
    private String apiKey;

    @Value("${siliconflow.base-url}")
    private String baseUrl;

    @Value("${siliconflow.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 用于存储会话历史（简单实现，生产环境建议使用Redis）
    private final Map<String, List<Map<String, Object>>> conversationHistory = new HashMap<>();

    @Override
    public AiChatVO chat(AiChatDTO aiChatDTO) {
        String message = aiChatDTO.getMessage();
        String conversationId = aiChatDTO.getConversationId();
        String image = aiChatDTO.getImage();

        // 如果没有会话ID，生成一个新的
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = UUID.randomUUID().toString();
        }

        log.info("AI对话请求 - 会话ID: {}, 消息: {}, 含图片: {}", conversationId, message, image != null);

        try {
            // 获取或创建会话历史
            List<Map<String, Object>> history = conversationHistory.computeIfAbsent(
                    conversationId, k -> new ArrayList<>());

            // 添加用户消息到历史
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");

            if (image != null && !image.isEmpty()) {
                // 多模态消息
                List<Map<String, Object>> contentList = new ArrayList<>();

                Map<String, Object> textPart = new HashMap<>();
                textPart.put("type", "text");
                textPart.put("text", message);
                contentList.add(textPart);

                Map<String, Object> imagePart = new HashMap<>();
                imagePart.put("type", "image_url");
                Map<String, String> imageUrl = new HashMap<>();
                imageUrl.put("url", image); // image is expected to be full data URI
                imagePart.put("image_url", imageUrl);
                contentList.add(imagePart);

                userMessage.put("content", contentList);
            } else {
                userMessage.put("content", message);
            }

            history.add(userMessage);

            // 限制历史记录长度（最多保留10轮对话）
            if (history.size() > 20) {
                history = new ArrayList<>(history.subList(history.size() - 20, history.size()));
                conversationHistory.put(conversationId, history);
            }

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", history);
            requestBody.put("max_tokens", 2048);
            requestBody.put("temperature", 0.7);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 调用硅基流动 API
            String url = baseUrl + "/chat/completions";
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class);

            // 解析响应
            Map<String, String> parsedResponse = parseResponse(response.getBody());
            String reply = parsedResponse.get("content");
            String thinking = parsedResponse.get("thinking");

            // 添加AI回复到历史
            Map<String, Object> assistantMessage = new HashMap<>();
            assistantMessage.put("role", "assistant");
            assistantMessage.put("content", reply);
            history.add(assistantMessage);

            log.info("AI对话响应 - 会话ID: {}, 回复长度: {}", conversationId, reply.length());

            return AiChatVO.builder()
                    .reply(reply)
                    .thinking(thinking) // 设置思考内容
                    .conversationId(conversationId)
                    .timestamp(System.currentTimeMillis())
                    .build();

        } catch (Exception e) {
            log.error("AI对话失败 - 会话ID: {}, 错误: {}", conversationId, e.getMessage(), e);

            return AiChatVO.builder()
                    .reply("抱歉，AI 助手暂时无法响应，请稍后再试。错误信息：" + e.getMessage())
                    .conversationId(conversationId)
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }

    @Override
    public void streamChat(AiChatDTO aiChatDTO, SseEmitter emitter) {
        new Thread(() -> {
            String conversationId = aiChatDTO.getConversationId();
            try {
                String message = aiChatDTO.getMessage();
                // 如果没有会话ID，生成一个新的
                String finalConversationId = (conversationId == null || conversationId.isEmpty())
                        ? UUID.randomUUID().toString()
                        : conversationId;

                log.info("AI流式对话请求 - 会话ID: {}, 消息: {}", finalConversationId, message);

                // 获取或创建会话历史
                List<Map<String, Object>> history = conversationHistory.computeIfAbsent(
                        finalConversationId, k -> new ArrayList<>());

                // 添加用户消息到历史
                Map<String, Object> userMessage = new HashMap<>();
                userMessage.put("role", "user");

                String image = aiChatDTO.getImage();
                if (image != null && !image.isEmpty()) {
                    List<Map<String, Object>> contentList = new ArrayList<>();

                    Map<String, Object> textPart = new HashMap<>();
                    textPart.put("type", "text");
                    textPart.put("text", message);
                    contentList.add(textPart);

                    Map<String, Object> imagePart = new HashMap<>();
                    imagePart.put("type", "image_url");
                    Map<String, String> imageUrl = new HashMap<>();
                    imageUrl.put("url", image);
                    imagePart.put("image_url", imageUrl);
                    contentList.add(imagePart);

                    userMessage.put("content", contentList);
                } else {
                    userMessage.put("content", message);
                }

                history.add(userMessage);

                // 限制历史
                if (history.size() > 20) {
                    history = new ArrayList<>(history.subList(history.size() - 20, history.size()));
                    conversationHistory.put(finalConversationId, history);
                }

                // Initial response with conversationId
                try {
                    Map<String, String> initEvent = new HashMap<>();
                    initEvent.put("type", "meta");
                    initEvent.put("conversationId", finalConversationId);
                    emitter.send(initEvent);
                } catch (Exception e) {
                    log.error("发送meta失败", e);
                }

                // 构建请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", model);
                requestBody.put("messages", history);
                requestBody.put("max_tokens", 2048);
                requestBody.put("temperature", 0.7);
                requestBody.put("stream", true);

                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(apiKey);

                StringBuilder fullContent = new StringBuilder();

                // 执行请求
                restTemplate.execute(
                        baseUrl + "/chat/completions",
                        HttpMethod.POST,
                        request -> {
                            request.getHeaders().addAll(headers);
                            new ObjectMapper().writeValue(request.getBody(), requestBody);
                        },
                        response -> {
                            try (java.io.InputStream body = response.getBody();
                                    BufferedReader reader = new BufferedReader(
                                            new InputStreamReader(body, StandardCharsets.UTF_8))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    if (line.trim().isEmpty())
                                        continue;
                                    if (line.startsWith("data: ")) {
                                        String json = line.substring(6);
                                        if ("[DONE]".equals(json.trim()))
                                            break;

                                        try {
                                            JsonNode root = objectMapper.readTree(json);
                                            JsonNode choices = root.get("choices");
                                            if (choices != null && choices.size() > 0) {
                                                JsonNode delta = choices.get(0).get("delta");
                                                if (delta != null) {
                                                    // 处理思考过程
                                                    if (delta.has("reasoning_content")) {
                                                        JsonNode reasoningNode = delta.get("reasoning_content");
                                                        if (reasoningNode != null && !reasoningNode.isNull()) {
                                                            String thinking = reasoningNode.asText("");
                                                            if (!thinking.isEmpty()) {
                                                                Map<String, String> event = new HashMap<>();
                                                                event.put("type", "thinking");
                                                                event.put("text", thinking);
                                                                emitter.send(event);
                                                            }
                                                        }
                                                    }
                                                    // 处理正文内容
                                                    if (delta.has("content")) {
                                                        JsonNode contentNode = delta.get("content");
                                                        if (contentNode != null && !contentNode.isNull()) {
                                                            String content = contentNode.asText("");
                                                            if (!content.isEmpty()) {
                                                                fullContent.append(content);
                                                                Map<String, String> event = new HashMap<>();
                                                                event.put("type", "content");
                                                                event.put("text", content);
                                                                emitter.send(event);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            log.warn("Parsing SSE event failed: " + line, e);
                                        }
                                    }
                                }
                            }
                            return null;
                        });

                // Save full response to history
                Map<String, Object> assistantMessage = new HashMap<>();
                assistantMessage.put("role", "assistant");
                assistantMessage.put("content", fullContent.toString());
                history.add(assistantMessage);

                emitter.complete();

            } catch (Exception e) {
                log.error("流式对话失败", e);
                emitter.completeWithError(e);
            }
        }).start();
    }

    /**
     * 解析硅基流动 API 响应
     */
    private Map<String, String> parseResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode choices = root.get("choices");

        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode firstChoice = choices.get(0);
            JsonNode message = firstChoice.get("message");
            if (message != null) {
                Map<String, String> result = new HashMap<>();

                // 获取内容
                JsonNode content = message.get("content");
                if (content != null) {
                    result.put("content", content.asText());
                } else {
                    result.put("content", "");
                }

                // 获取思考过程
                JsonNode reasoning = message.get("reasoning_content");
                if (reasoning != null) {
                    result.put("thinking", reasoning.asText());
                }

                return result;
            }
        }

        throw new RuntimeException("无法解析AI响应");
    }

    @Override
    public com.blog.pojo.vo.AiCompletionVO completion(com.blog.pojo.dto.AiCompletionDTO aiCompletionDTO) {
        long startTime = System.currentTimeMillis();

        // 1. 准备请求数据
        String textBefore = aiCompletionDTO.getTextBeforeCursor();
        String textAfter = aiCompletionDTO.getTextAfterCursor();
        String content = aiCompletionDTO.getContent();

        // 如果前后文为空，尝试使用content（假设光标在最后）
        if ((textBefore == null || textBefore.isEmpty()) && content != null) {
            textBefore = content;
        }

        if (textBefore == null || textBefore.isEmpty()) {
            return com.blog.pojo.vo.AiCompletionVO.builder()
                    .completion("")
                    .timestamp(System.currentTimeMillis())
                    .build();
        }

        // 截取最近的上下文（例如最后2000字符），减少Token消耗
        if (textBefore.length() > 2000) {
            textBefore = textBefore.substring(textBefore.length() - 2000);
        }

        // 2. 构建 Prompt
        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content",
                "你是一个智能文本补全助手。请根据用户提供的上文（以及可选的下文），续写补全句子或段落。要求：\n1. 只输出补全的内容，不要包含任何解释、前缀或后缀。\n2. 补全内容应自然流畅，符合上下文逻辑。\n3. 如果上文已经完整或无需补全，输出空字符串。\n4. 不要重复上文的内容。");
        messages.add(systemMsg);

        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        StringBuilder userContent = new StringBuilder();
        if (aiCompletionDTO.getTitle() != null && !aiCompletionDTO.getTitle().isEmpty()) {
            userContent.append("文章标题：").append(aiCompletionDTO.getTitle()).append("\n\n");
        }
        userContent.append("上文：\n").append(textBefore);
        if (textAfter != null && !textAfter.isEmpty()) {
            // 截取下文前500字符
            if (textAfter.length() > 500)
                textAfter = textAfter.substring(0, 500);
            userContent.append("\n\n下文：\n").append(textAfter);
        }
        userContent.append("\n\n请续写：");

        userMsg.put("content", userContent.toString());
        messages.add(userMsg);

        // 3. 构建API请求
        Map<String, Object> requestBody = new HashMap<>();
        // 强制使用非CoT模型以加快速度，如果配置中没有区分，这里硬编码一个常用的非推理模型用于补全，
        // 或者沿用配置但依赖prompt消除思考。考虑到用户需求，尝试使用 deepseek-chat (V3)
        // 如果 siliconflow.model 配置的就是 V3，则直接用。如果是 R1，这里最好覆盖。
        // 为了安全，先使用配置的模型。如果是 R1，它通常会输出 reasoning_content，我们在 parseResponse 里处理了，但补全只要
        // content。
        // 关键是速度。R1 慢。
        // 尝试使用 "deepseek-ai/DeepSeek-V3"
        requestBody.put("model", "deepseek-ai/DeepSeek-V3");
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 100); // 补全不需要太长
        requestBody.put("temperature", 0.3); // 降低随机性
        requestBody.put("stream", false); // 非流式

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String url = baseUrl + "/chat/completions";
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class);

            Map<String, String> parsed = parseResponse(response.getBody());
            String completion = parsed.get("content");

            // 简单的后处理：去除可能的首尾空白
            if (completion != null) {
                // 如果补全内容以原句结尾开头，可能需要去重（简单处理暂时不做）
                // 某些模型可能会重复输入的最后几个字，这里假设 prompt 足够好
            }

            log.info("AI补全完成 - 耗时: {}ms, 补全长度: {}", System.currentTimeMillis() - startTime,
                    completion != null ? completion.length() : 0);

            return com.blog.pojo.vo.AiCompletionVO.builder()
                    .completion(completion)
                    .timestamp(System.currentTimeMillis())
                    .build();

        } catch (Exception e) {
            log.error("AI补全失败", e);
            // 降级处理：尝试使用配置的模型重试？或者直接返回空。
            // 为了不卡顿，直接返回空
            return com.blog.pojo.vo.AiCompletionVO.builder()
                    .completion("")
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }
}

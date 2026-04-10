package com.blog.task;

import com.blog.mapper.BlogMapper;
import com.blog.service.impl.BloomFilterServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 缓存预热任务
 * 在系统启动后自动执行，将数据库中的存量数据加载到布隆过滤器中
 * 
 * @author worlt
 * @date 2026/04/10
 */
@Component
@Slf4j
public class CacheWarmupTask implements ApplicationRunner {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private BloomFilterServiceImpl bloomFilterService;

    // 缓存键前缀，必须与 BlogCacheServiceImpl 中的保持一致
    private static final String BLOG_KEY_PREFIX = "blog:";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始执行布隆过滤器缓存预热...");
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 获取所有文章ID
            List<Long> blogIds = blogMapper.getAllBlogIds();
            
            if (blogIds != null && !blogIds.isEmpty()) {
                // 2. 批量注入布隆过滤器
                for (Long id : blogIds) {
                    String key = BLOG_KEY_PREFIX + id;
                    bloomFilterService.add(key);
                }
                
                long endTime = System.currentTimeMillis();
                log.info("布隆过滤器预热完成！共加载文章ID数量: {}, 耗时: {}ms", 
                        blogIds.size(), (endTime - startTime));
            } else {
                log.info("无可加载的文章数据，布隆过滤器预热跳过。");
            }
            
        } catch (Exception e) {
            log.error("布隆过滤器预热执行过程中出现异常: ", e);
            // 预热失败不应影响系统主流程启动，所以仅记录错误日志
        }
    }
}

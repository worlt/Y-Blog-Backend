package com.blog.controller.auditor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.AuditorLoginDTO;
import com.blog.pojo.entity.Auditor;
import com.blog.pojo.vo.AuditorLoginVO;
import com.blog.properties.JwtProperties;
import com.blog.result.Result;
import com.blog.service.AuditorService;
import com.blog.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author worlt
 * @Date 2025/6/2 下午12:53
 */
@RestController("auditorController")
@RequestMapping("/auditor/auditor")
@Api(tags = "审核员相关接口")
@Slf4j
public class AuditorController {

    @Autowired
    private AuditorService auditorService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 审核员登录
     * @param auditorLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("审核员登录")
    public Result<AuditorLoginVO> login(AuditorLoginDTO auditorLoginDTO) {
        log.info("审核员登录：{}", auditorLoginDTO);

        Auditor auditor = auditorService.login(auditorLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.AUDITOR_ID, auditor.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAuditorSecretKey(),
                jwtProperties.getAuditorTtl(),
                claims
        );

        AuditorLoginVO auditorLoginVO = AuditorLoginVO.builder()
                .id(auditor.getId())
                .username(auditor.getUsername())
                .name(auditor.getName())
                .token(token)
                .build();
        return Result.success(auditorLoginVO);
    }


}

package com.blog.controller.auditor;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.dto.AuditorDTO;
import com.blog.pojo.dto.AuditorLoginDTO;
import com.blog.pojo.entity.Auditor;
import com.blog.pojo.vo.AuditorLoginVO;
import com.blog.pojo.vo.AuditorVO;
import com.blog.properties.JwtProperties;
import com.blog.result.Result;
import com.blog.service.AuditorService;
import com.blog.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
                .avatar(auditor.getAvatar())
                .role(auditor.getRole())
                .token(token)
                .build();
        return Result.success(auditorLoginVO);
    }

    /**
     * 根据id查询审核员
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询审核员")
    public Result<AuditorVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询审核员：{}", id);
        AuditorVO auditorVO = auditorService.getById(id);
        return Result.success(auditorVO);
    }

    /**
     * 编辑审核员
     * @param auditorDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("编辑审核员")
    public Result update(@RequestBody AuditorDTO auditorDTO) {
        log.info("编辑审核员：{}", auditorDTO);
        auditorService.update(auditorDTO);
        return Result.success();
    }


}

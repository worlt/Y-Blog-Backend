package com.blog.controller.auditor;

import com.blog.service.AuditorService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}

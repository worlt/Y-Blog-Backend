package com.blog.controller.admin;

import com.blog.constant.JwtClaimsConstant;
import com.blog.pojo.vo.AdminLoginVO;
import com.blog.pojo.dto.AdminDTO;
import com.blog.pojo.dto.AdminLoginDTO;
import com.blog.pojo.dto.AdminPageQueryDTO;
import com.blog.pojo.entity.Admin;
import com.blog.pojo.vo.AdminVO;
import com.blog.properties.JwtProperties;
import com.blog.result.PageResult;
import com.blog.result.Result;
import com.blog.service.AdminService;
import com.blog.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员管理
 *
 * @Author worlt
 * @Date 2025/5/18 下午7:46
 */
@RestController
@RequestMapping("/admin/admin")
@Slf4j
@Api(tags = "管理员相关接口")
public class AdminController {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private AdminService adminService;

    /**
     * 登录
     *
     * @param adminLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("管理员登录")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        log.info("管理员登录：{}", adminLoginDTO);

        Admin admin = adminService.login(adminLoginDTO);

        // 登录成功后生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ADMIN_ID, admin.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims
        );

        AdminLoginVO adminLoginVO = AdminLoginVO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .name(admin.getName())
                .avatar(admin.getAvatar())
                .role(admin.getRole())
                .token(token)
                .build();
        return Result.success(adminLoginVO);
    }


    /**
     * 退出
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("管理员退出")
    public Result<String> logout() {
        log.info("管理员退出");
        return Result.success();
    }

    /**
     * 新增管理员
     * @param adminDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("新增管理员")
    public Result add(@RequestBody AdminDTO adminDTO) {
        log.info("新增管理员：{}", adminDTO);
        adminService.add(adminDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param adminPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("管理员分页查询")
    public Result<PageResult> page(AdminPageQueryDTO adminPageQueryDTO) {
        log.info("管理员分页查询：{}", adminPageQueryDTO);
        PageResult pageResult = adminService.pageQuery(adminPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用管理员
     * @param map
     * @param status
     * @return
     */
    @PutMapping("/status/{status}")
    @ApiOperation("启用禁用管理员")
    public Result setStatus(@RequestBody Map<String, Long> map,@PathVariable("status") Integer status) {
        Long id = map.get("id");
        log.info("启用禁用管理员：id：{}，status：{}", id, status);
        adminService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 编辑管理员信息
     * @param adminDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("编辑管理员信息")
    public Result update(@RequestBody AdminDTO adminDTO) {
        log.info("编辑管理员：{}", adminDTO);
        adminService.update(adminDTO);
        return Result.success();
    }

    /**
     * 根据id删除管理员
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @ApiOperation("根据id删除管理员")
    public Result deleteById(@PathVariable("id") Long id) {
        log.info("根据id删除管理员：{}", id);
        adminService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据id查询管理员
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @ApiOperation("根据id查询管理员")
    public Result<AdminVO> getById(@PathVariable("id") Long id) {
        log.info("根据id查询管理员：{}", id);
        AdminVO adminVO = adminService.getById(id);
        return Result.success(adminVO);
    }

}

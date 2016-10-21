package com.hunt.controller;

import com.hunt.model.dto.PageInfo;
import com.hunt.model.entity.SysRole;
import com.hunt.service.SysRoleService;
import com.hunt.service.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import system.Result;

/**
 * @Author ouyangan
 * @Date 2016/10/14/14:46
 * @Description
 */

@RequestMapping("role")
@Controller
public class RoleController extends BaseController {
    private static Logger log = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private SysRoleService sysRoleService;

    @RequestMapping(value = "toRole", method = RequestMethod.GET)
    public String toRole() {
        return "system/role";
    }

    @ResponseBody
    @RequestMapping(value = "insert", method = RequestMethod.POST)
    public Result insert(@RequestParam String name,
                         @RequestParam String description,
                         @RequestParam String permissionIds,
                         @RequestParam(defaultValue = "1") int isFinal) {
        boolean isExsitRoleName = sysRoleService.isExsitRoleName(name);
        if (isExsitRoleName) {
            return Result.error("角色名称已存在");
        }
        SysRole sysRole = new SysRole();
        sysRole.setName(name);
        sysRole.setDescription(description);
        sysRole.setIsFinal(isFinal);
        long id = sysRoleService.insertRole(sysRole, permissionIds);
        return Result.success(id);
    }

    @Autowired
    private SystemService systemService;
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public Result update(@RequestParam long id,
                         @RequestParam String name,
                         @RequestParam String description,
                         @RequestParam String permissionIds) {
        System.out.println("id = [" + id + "], name = [" + name + "], description = [" + description + "], permissionIds = [" + permissionIds + "]");
        boolean isExsitRoleNameExcludeId = sysRoleService.isExsitRoleNameExcludeId(id, name);
        if (isExsitRoleNameExcludeId) {
            return Result.error("角色名称已存在");
        }
        SysRole sysRole = sysRoleService.selectById(id);
        if (sysRole == null) {
            return Result.error("记录不存在");
        }
        if (sysRole.getIsFinal() == 2) {
            return Result.error("该条记录不能被编辑");
        }
        sysRole.setId(id);
        sysRole.setName(name);
        sysRole.setDescription(description);
        sysRoleService.updateRole(sysRole, permissionIds);
        systemService.clearAuthorizationInfoByRoleId(id);
        return Result.success();
    }

    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public Result delete(@RequestParam long id) {
        SysRole sysRole = sysRoleService.selectById(id);
        if (sysRole == null) {
            return Result.error("记录不存在");
        }
        if (sysRole.getIsFinal() == 2) {
            return Result.error("该条记录不能被删除");
        }
        sysRole.setStatus(2);
        sysRoleService.deleteRole(sysRole);
        systemService.clearAuthorizationInfoByRoleId(id);
        return Result.success();
    }

    @ResponseBody
    @RequestMapping(value = "select", method = RequestMethod.GET)
    public PageInfo select(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "15") int rows) {
        PageInfo pageInfo = sysRoleService.selectPage(page, rows);
        return pageInfo;
    }
}
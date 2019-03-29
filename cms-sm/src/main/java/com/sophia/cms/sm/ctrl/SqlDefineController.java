package com.sophia.cms.sm.ctrl;

import com.sophia.cms.framework.response.Response;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.sm.model.SqlDefineSearchModel;
import com.sophia.cms.sm.model.SqlDefineEditModel;
import com.sophia.cms.sm.model.SqlDefineFetchModel;
import com.sophia.cms.sm.service.SqlDefineService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by lenovo on 2017/8/30.
 */
@RestController
@RequestMapping("/sm/sqldefine")
public class SqlDefineController {

    @Autowired
    SqlDefineService sqlDefineService;

    @ApiModelProperty(value = "编辑")
    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_sqldefine_edit')")
    @ResponseBody
    @PostMapping(value = "/edit")
    public Response persistent(@RequestBody @Valid SqlDefineEditModel sqlDefineEditModel) {
        return sqlDefineService.edit(sqlDefineEditModel);
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_sqldefine_delete')")
    @ApiModelProperty(value = "删除")
    @GetMapping(value = "/delete")
    public Response delete(Long id) {
        return sqlDefineService.delete(id);
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_sqldefine_fetch')")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/fetch")
    public Response<SqlDefineFetchModel> fetch(Long id) {
        return sqlDefineService.fetch(id);
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_sqldefine_list')")
    @ApiOperation(value = "查询列表")
    @GetMapping(value = "/list")
    public Response<Pager<SqlDefineFetchModel>> list(SqlDefineSearchModel sqlDefineSearchModel) {
        return Response.SUCCESS(sqlDefineService.list(sqlDefineSearchModel));
    }
}

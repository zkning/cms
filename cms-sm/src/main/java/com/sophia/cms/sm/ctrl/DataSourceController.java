package com.sophia.cms.sm.ctrl;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.orm.model.Pager;
import com.sophia.cms.sm.model.DataSourceFetchModel;
import com.sophia.cms.sm.model.DataSourceSearchModel;
import com.sophia.cms.sm.domain.DataSource;
import com.sophia.cms.sm.model.DataSourceEditModel;
import com.sophia.cms.sm.service.DataSourceService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/dataSource")
public class DataSourceController {

    @Autowired
    DataSourceService dataSourceService;

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('dataSource_edit')")
    @ApiModelProperty(value = "编辑")
    @PostMapping(value = "/edit")
    public Response edit(@RequestBody @Valid DataSourceEditModel dataSourceEditModel) {
        return dataSourceService.edit(dataSourceEditModel);
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('dataSource_delete')")
    @ApiModelProperty(value = "删除")
    @GetMapping(value = "/delete")
    public Response delete(Long id) {
        return dataSourceService.delete(id);
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('dataSource_fetch')")
    @ApiOperation(value = "查询")
    @GetMapping(value = "/fetch")
    public Response<DataSourceFetchModel> fetch(Long id) {
        return dataSourceService.fetch(id);
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('dataSource_list')")
    @ApiOperation(value = "查询列表")
    @GetMapping(value = "/list")
    public Response<Pager<DataSourceFetchModel>> list(DataSourceSearchModel dataSourceSearchModel) {
        return Response.SUCCESS(dataSourceService.list(dataSourceSearchModel));
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('dataSource_findAll')")
    @ApiOperation(value = "查询全部")
    @GetMapping(value = "/findAll")
    public Response<List<DataSource>> findAll() {
        return Response.SUCCESS(dataSourceService.findAll());
    }
}

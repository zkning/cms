package com.fast.admin.sm.ctrl;

import com.fast.admin.framework.response.Response;
import com.fast.admin.orm.idworker.SnowflakeId;
import com.fast.admin.orm.model.Pager;
import com.fast.admin.sm.model.*;
import com.fast.admin.sm.service.DataViewService;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 视图服务
 * Created by lenovo on 2017/8/13.
 */
@Slf4j
@RestController
@RequestMapping("/sm/dataView")
public class DataViewController {

    @Autowired
    DataViewService dataViewService;

    /**
     * 创建视图
     *
     * @param request
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_dataView_insert')")
    @ResponseBody
    @PostMapping(value = "/insert")
    public Response insert(@RequestBody @Valid DataViewEditModel request) {
        return dataViewService.edit(request, false);
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_dataView_update')")
    @ResponseBody
    @PostMapping(value = "/update")
    public Response update(@RequestBody @Valid DataViewEditModel request) {
        return dataViewService.edit(request, true);
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_dataView_delete')")
    @ResponseBody
    @GetMapping(value = "/delete")
    public Response delete(Long id) {
        return dataViewService.delete(id);
    }

    /**
     * 根据视图编号查询
     */
    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_dataView_fetch')")
    @ResponseBody
    @RequestMapping(value = "/fetch/{id}", method = RequestMethod.POST)
    public Response<DataViewFetchModel> fetch(@ApiParam(value = "视图id") @PathVariable Long id) {
        return Response.SUCCESS(dataViewService.fetch(id));
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER')")
    @ResponseBody
    @GetMapping(value = "/list")
    public Response<Pager<DataViewModel>> list(DataViewSearchModel dataViewSearchModel) {
        return Response.SUCCESS(dataViewService.list(dataViewSearchModel));
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_dataView_toRes')")
    @ResponseBody
    @PostMapping(value = "/toRes")
    public Response toRes(@RequestBody @Valid ToResModel toResModel) {
        return dataViewService.toRes(toResModel);
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_dataView_refreshId')")
    @ResponseBody
    @GetMapping(value = "/refreshId")
    public Response refreshId() {
        return Response.SUCCESS(String.valueOf(SnowflakeId.getId()));
    }
}

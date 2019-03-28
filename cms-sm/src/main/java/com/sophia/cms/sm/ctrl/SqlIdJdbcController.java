package com.sophia.cms.sm.ctrl;


import com.alibaba.fastjson.JSONObject;
import com.sophia.cms.framework.response.Response;
import com.sophia.cms.sm.model.*;
import com.sophia.cms.sm.service.SqlIdJdbcService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sm/sqlId")
public class SqlIdJdbcController {

    @Autowired
    SqlIdJdbcService sqlIdJdbcService;

    @Resource
    HttpServletRequest httpServletRequest;

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_sqlId_getSchemaTable')")
    @ApiOperation(value = "查询所有表信息")
    @ResponseBody
    @RequestMapping(value = "/getSchemaTable", method = RequestMethod.POST)
    public Response<List<SchemaTableModel>> queryTableschemas(@RequestBody @Valid SchemaTableSearchModel schemaTableSearchModel) throws Exception {
        return Response.SUCCESS(sqlIdJdbcService.getInformationSchemaTable(schemaTableSearchModel));
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_sqlId_preview')")
    @ApiOperation(value = "sql定义预览")
    @ResponseBody
    @RequestMapping(value = "/preview", method = RequestMethod.POST)
    public Response preview(@RequestBody @Valid PreviewModel previewModel) throws Exception {
        return sqlIdJdbcService.preview(previewModel);
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority('sm_sqlId_showFullColumns')")
    @ResponseBody
    @RequestMapping(value = "/showFullColumns/{sqlId}", method = RequestMethod.POST)
    public Response createColumnList(@PathVariable Long sqlId) throws Exception {
        List<FieldModel> list = sqlIdJdbcService.showFullColumnsBySqlId(sqlId);
        return Response.SUCCESS(list);
    }

    /**
     * 保存视图数据
     * 权限 = DATAVIEWID + FUNCID
     */
    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority(#dataViewId + '_' + #funcId)")
    @ResponseBody
    @RequestMapping(value = "/create/{dataViewId}/{funcId}", method = RequestMethod.POST)
    public Object createView(@PathVariable Long dataViewId, @PathVariable Long funcId, @RequestBody JSONObject formParam) {
        return sqlIdJdbcService.createByDataViewId(dataViewId, formParam);
    }

    /**
     * 修改视图的数据
     */
    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority(#dataViewId + '_' + #funcId)")
    @ResponseBody
    @RequestMapping(value = "/modfity/{dataViewId}/{funcId}", method = RequestMethod.POST)
    public Object modfityView(@PathVariable Long dataViewId, @PathVariable Long funcId, @RequestBody JSONObject formParam) {
        return sqlIdJdbcService.modifyByDataViewId(dataViewId, formParam);
    }

    /**
     * 删除视图的数据
     */
    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority(#dataViewId + '_' + #funcId)")
    @ResponseBody
    @RequestMapping(value = "/delete/{dataViewId}/{funcId}", method = RequestMethod.POST)
    public Object deleteView(@PathVariable Long dataViewId, @PathVariable Long funcId, @RequestBody JSONObject record) {
        return sqlIdJdbcService.deleteByDataViewId(dataViewId, record);
    }

    /**
     * 具备当前视图权限及可操作
     *
     * @param sqlId
     * @param id
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority(#dataViewId)")
    @ResponseBody
    @GetMapping(value = "/fetch/{dataViewId}")
    public Response fetch(@PathVariable Long dataViewId, String id) {
        Response response = sqlIdJdbcService.fetch(dataViewId, id);
        return response;
    }

    /**
     * dataViewCode  根据SQLID返回bootstrapTable数据格式
     */
    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority(#dataViewId)")
    @ResponseBody
    @RequestMapping(value = "/list/{dataViewId}", method = RequestMethod.POST)
    public Response<BootstrapPageResult> getBootatrapTableResponse(@PathVariable Long dataViewId,
                                                                   @RequestBody BootstrapSearchParam bootstrapSearchParam) {
        return sqlIdJdbcService.getBootstrapTableResponse(bootstrapSearchParam, dataViewId);
    }

    /**
     * Ztree
     *
     * @return
     */
    @ResponseBody
    @GetMapping(value = "/getTree")
    public Response getTree(ZtreeModel ztreeModel) {
        Response response = sqlIdJdbcService.getTree(ztreeModel);
        return response;
    }
}

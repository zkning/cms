package com.fast.admin.sm.ctrl;


import com.alibaba.fastjson.JSONObject;
import com.fast.admin.framework.response.Response;
import com.fast.admin.sm.model.*;
import com.fast.admin.sm.service.SqlIdJdbcService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
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
     * 保存SQLVIEW记录
     *
     * @param formParam
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority(#dataViewId)")
    @ResponseBody
    @RequestMapping(value = "/create/{dataViewId}", method = RequestMethod.POST)
    public Object createView(@PathVariable Long dataViewId, @RequestBody JSONObject formParam) {
        return sqlIdJdbcService.createByDataViewId(dataViewId, formParam);
    }


    /**
     * 修改视图的数据
     */
    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority(#dataViewId)")
    @ResponseBody
    @RequestMapping(value = "/modfity/{dataViewId}", method = RequestMethod.POST)
    public Object modfityView(@PathVariable Long dataViewId, @RequestBody JSONObject formParam) {
        return sqlIdJdbcService.modifyByDataViewId(dataViewId, formParam);
    }

    /**
     * 删除视图的数据
     *
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority(#dataViewId)")
    @ResponseBody
    @RequestMapping(value = "/delete/{dataViewId}", method = RequestMethod.POST)
    public Object deleteView(@PathVariable Long dataViewId, @RequestBody JSONObject record) {
        return sqlIdJdbcService.deleteByDataViewId(dataViewId, record);
    }

    @PreAuthorize("hasRole('ROLE_ENGINEER') or hasAuthority(#dataViewId)")
    @ResponseBody
    @GetMapping(value = "/fetch/{sqlId}")
    public Response fetch(@PathVariable Long sqlId, Long id) {
        Response response = sqlIdJdbcService.fetch(sqlId, id);
        return response;
    }

    /**
     * dataViewCode  根据SQLID返回bootstrapTable数据格式
     *
     * @return
     */
//    @PreAuthorize("hasRole('ROLE_ENGINEER')")
    @ResponseBody
    @RequestMapping(value = "/list/{sqlId}", method = RequestMethod.POST)
    public Response<BootstrapPageResult> getBootatrapTableResponse(@PathVariable Long sqlId,
                                                                   Integer pageSize, Integer pageNumber, String searchText, String sortName, String sortOrder,
                                                                   @RequestBody BootstrapSearchParam bootstrapSearchParam) throws UnsupportedEncodingException {
        if (RequestMethod.GET.name().equals(httpServletRequest.getMethod())) {
            //当查询条件中包含中文时，get请求默认会使用ISO-8859-1编码请求参数，在服务端需要对其解码
            if (!StringUtils.isEmpty(searchText)) {
                searchText = new String(searchText.getBytes("ISO-8859-1"), "UTF-8");
            }
            return sqlIdJdbcService.
                    getBootstrapTableResponse(pageSize, pageNumber, searchText, sortName, sortOrder, sqlId, bootstrapSearchParam);
        }
        return sqlIdJdbcService.getBootstrapTableResponse(bootstrapSearchParam, sqlId);
    }

    /**
     * Ztree
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/ztree", method = RequestMethod.POST)
    public Object ztree(ZtreeModel ztreeModel) {
        Response response = sqlIdJdbcService.ztree(ztreeModel);
        return response.getResult();
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

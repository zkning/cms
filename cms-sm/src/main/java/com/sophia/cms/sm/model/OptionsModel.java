package com.sophia.cms.sm.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by zkning on 2017/8/13.
 */
@Data
public class OptionsModel implements Serializable {

    @NotBlank(message = "url不能为空")
    @ApiModelProperty(value = "链接")
    private String url;

    @NotBlank(message = "method不能为空")
    @ApiModelProperty(value = "GET,POST")
    private String method;

    @ApiModelProperty(value = "高度")
    private String height;

    @ApiModelProperty(value = "无数据默认提示")
    private String undefinedText;

    @ApiModelProperty(value = "是否可排序")
    private boolean sortStable;

    @ApiModelProperty(value = "是否显示分页")
    private boolean pagination;

    @NotNull
    @ApiModelProperty(value = "默认页码")
    private int pageNumber;

    @NotNull
    @ApiModelProperty(value = "默认分页数")
    private int pageSize;

    @ApiModelProperty(value = "页码集合")
    private String pageList;

    @NotNull
    @ApiModelProperty(value = "id字段")
    private String idField;

    @ApiModelProperty(value = "导出")
    private boolean showExport;

    @ApiModelProperty(value = "导出类型")
    private String exportDataType;

    @ApiModelProperty(value = "版本字段")
    private String version;

    @ApiModelProperty(value = "自动加载")
    private boolean autoload;

    private String classes = "table table-hover";
    private String sortClass;

    // 条纹
    private boolean striped;
    private String sortOrder;
    private String iconsPrefix;
    private String iconSize;
    private String buttonsClass;
    //    private List data = new ArrayList();
    private String dataField;
    private String totalField;
    private String ajax;
    private boolean cache;
    private String contentType;
    private String dataType;
    private String ajaxOptions;
    private String queryParamsType;
    private boolean paginationLoop;
    private boolean onlyInfoPagination;
    private String sidePagination;
    private String selectItemName;
    private boolean smartDisplay;
    private boolean escape;
    private boolean search;
    private boolean searchOnEnterKey;
    private boolean strictSearch;
    private String searchText;
    private int searchTimeOut;
    private int trimOnSearch;
    private boolean showHeader;
    private boolean showFooter;
    private boolean showColumns;
    private boolean showRefresh;
    private boolean showToggle;
    private boolean showPaginationSwitch;
    private int minimumCountColumns;
    private boolean editView;
    private String uniqueId;
    private boolean cardView;
    private boolean detailView;
    private String searchAlign;
    private String buttonsAlign;
    private String toolbarAlign;
    private String paginationVAlign;
    private String paginationHAlign;
    private String paginationDetailHAlign;
    private String paginationPreText;
    private String paginationNextText;
    private boolean clickToSelect;
    private boolean singleSelect;
    private String toolbar;
    private boolean checkboxHeader;
    private boolean maintainSelected;
    private boolean sortable;
    private boolean silentSort;
    private String locale;
    //    private String rowStyle;
//    private String rowAttributes;
//    private String customSearch;
//    private String customSort;
//    private String footerStyle;
//    private String detailFormatter;
//    private String sortName;
//    private String icons;
    private String queryParams;
    //    private String responseHandler;
}

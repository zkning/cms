import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef, NzMessageService } from 'ng-zorro-antd';
import { _HttpClient, ModalHelper } from '@delon/theme';
import { SFSchema, SFUISchema } from '@delon/form';
import { DataViewEditModel } from '../../model/dataviewedit.model';
import { IResponse } from '@core/net/model/IResponse';
import { SmServerConstant } from '../../constants/smserver.constants';
import { FormGroup, FormBuilder, Validators, FormArray } from '@angular/forms';
import { DataFilterModel } from '../../model/datafilter.model';
import { ButtonModel } from '../../model/button.model';
import { SmBtneditComponent } from '../../btnedit/btnedit.component';
import { ActivatedRoute, Router } from '@angular/router';
import { FormVerifiyService } from '../../service/formVerifiy.service';
import { GUID } from '../../utils/guid.util';
import { GoldbalConstant } from '../../constants/goldbal.constant';
import { PatternComponent } from '../../pattern/pattern.component';
import { SqlSelectorComponent } from '../../sqlselector/sqlselector.component';
import { FieldModel } from '../../model/field.model';
import { DictConstant } from '../../constants/dict.constant';

// 编辑视图配置 ningzuokun
@Component({
  selector: 'app-sm-dataview-edit',
  templateUrl: './edit.component.html',
  styleUrls: ['./edit.component.css'],
})
export class SmDataviewEditComponent implements OnInit {
  ngbForm: FormGroup;
  formData = new DataViewEditModel();
  formGroup: any;
  treeFromGroup: FormGroup;
  optionsFormGroup: FormGroup;

  // manipulateValue;
  // manipulateQuery = GoldbalConstant.manipulate.QUERY;

  // error info
  formErrors: Array<string>;
  updateTypes = DictConstant.createUpdateTypes();
  fieldTypes = DictConstant.createfieldTypes();
  buttons = DictConstant.createButtons();
  aligns = DictConstant.createAligns();
  valigns = DictConstant.createValigns();
  scopes = DictConstant.createScopes();
  expressions = DictConstant.createExpressions();
  methods = DictConstant.createMethods();
  locations = DictConstant.createLocation();
  sidePaginations = DictConstant.createSidePagination();
  createQueryParamsTypes = DictConstant.createQueryParamsType();
  exportDataType = DictConstant.createExportDataType();
  orderList = DictConstant.createOrderBy();

  // SQL 定义
  sqlDefines: Array<any> = new Array<any>();

  // ztree sqldefine fields
  ztreeSqlDefineFields: Array<any>;

  // 当前对应SQLDEFINE,relationId字段
  currentSqlDefineFields: Array<any>;
  constructor(
    private httpService: _HttpClient,
    private modalService: ModalHelper,
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private msgService: NzMessageService,
    private modal: NzModalRef,
  ) {
    // private formVerifiyService: FormVerifiyService
  }
  ngOnInit() {
    this.buildTreeGroup();
    this.buildOptionsFormGroup();
    this.formGroup = {
      id: [this.formData.id],
      dataViewName: [
        this.formData.dataViewName,
        [Validators.required, Validators.maxLength(32)],
      ],
      sqlId: [
        this.formData.sqlId,
        [Validators.required, Validators.maxLength(50)],
      ],
      manipulate: [this.formData.manipulate, Validators.required],
      remark: [this.formData.remark, Validators.maxLength(250)],
      version: [this.formData.version],
      options: this.optionsFormGroup,
      fields: this.fb.array(new Array<any>()),
      treeOptions: this.treeFromGroup,
      buttons: this.fb.array(new Array<any>()),
      dataFilters: this.fb.array(new Array<any>()),
    };
    this.ngbForm = this.fb.group(this.formGroup);
    this.ngbForm.valueChanges.subscribe(data => this.onValueChanged(data));

    // 字段列表
    const fieldControls = this.ngbForm.controls.fields as FormArray;
    this.buildFieldFormArray(fieldControls, this.formData.fields);

    // tree外键字段集合
    this.currentSqlDefineFields = this.formData.fields;

    // // 按钮列表
    const buttonControls = this.ngbForm.controls.buttons as FormArray;
    this.buildButtonFormArray(buttonControls, this.formData.buttons);

    // 过滤列表
    const filterControls = this.ngbForm.controls.dataFilters as FormArray;
    this.buildFilterFormArray(filterControls, this.formData.dataFilters);
  }

  isManipulateQuery() {
    return (
      this.ngbForm.controls.manipulate.value ===
      GoldbalConstant.manipulate.QUERY
    );
  }

  refreshId($element) {
    if ($element.value) {
      return;
    }
    this.httpService
      .get<IResponse<Array<any>>>(SmServerConstant.refreshId, {})
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.ngbForm.controls.id.setValue(resp.result);
        } else {
          this.msgService.error('获取ID失败');
        }
      });
  }

  // 基础选项
  buildItems() {
    const instance = this;
    this.getItems('update-Item').then(function(value) {
      instance.updateTypes = value;
    });
    this.getItems('fieldType-item').then(function(value) {
      instance.fieldTypes = value;
    });
    this.getItems('align-item').then(function(value) {
      instance.aligns = value;
    });
    this.getItems('option-item').then(function(value) {
      instance.buttons = value;
    });
    this.getItems('valigns-item').then(function(value) {
      instance.valigns = value;
    });
    this.getItems('scopes-item').then(function(value) {
      instance.scopes = value;
    });
    this.getItems('order-item').then(function(value) {
      instance.orderList = value;
    });
    this.getItems('expression-item').then(function(value) {
      instance.expressions = value;
    });
    this.getItems('methods-item').then(function(value) {
      instance.methods = value;
    });
    this.getItems('position-item').then(function(value) {
      instance.locations = value;
    });
    this.getItems('side-pagination-item').then(function(value) {
      instance.sidePaginations = value;
    });
    this.getItems('export-data-type').then(function(value) {
      instance.exportDataType = value;
    });
  }

  // 构建 field
  buildFieldFormArray(formArray, dataList: Array<FieldModel>) {
    if (!formArray || !dataList) {
      return;
    }
    dataList.forEach(item => {
      formArray.push(this.buildFieldGroup(item));
    });
  }

  // 构建 button
  buildButtonFormArray(formArray, dataList: Array<ButtonModel>) {
    if (!formArray || !dataList) {
      return;
    }
    dataList.forEach(item => {
      formArray.push(this.buildBtnFormGroup(item));
    });
  }

  // 构建 过滤条件
  buildFilterFormArray(formArray, dataList: Array<DataFilterModel>) {
    if (!formArray || !dataList) {
      return;
    }
    dataList.forEach(item => {
      formArray.push(this.buildDataFilter(item));
    });
  }

  getItems(value: string): any {
    return new Promise((resolve, reject) => {
      this.httpService
        .get<IResponse<Array<any>>>(SmServerConstant.findByPValue, {
          value: value,
        })
        .subscribe(resp => {
          if (IResponse.statuscode.SUCCESS === resp.code) {
            resolve(resp.result);
          } else {
            this.msgService.error(resp.message);
            reject(null);
          }
        });
    });
  }

  // 添加按钮
  openAdd(content?) {
    // 弹出组件
    this.modalService
      .static(SmBtneditComponent, { form: content })
      .subscribe((button: ButtonModel) => {
        if (button) {
          // 追加到按钮列表
          const controls = this.ngbForm.controls.buttons as FormArray;
          if (button.id === '') {
            button.id = GUID.createGUIDString();
            controls.push(this.buildBtnFormGroup(button));
          }
        }
      });
  }

  buildBtnFormGroup(button) {
    return this.fb.group({
      id: [button.id, [Validators.required]],
      option: [button.option, [Validators.required]],
      modal: [button.modal],
      size: [button.size],
      icon: [button.icon],
      title: [button.title, [Validators.required, Validators.maxLength(10)]],
      url: [button.url],
      position: [button.position, [Validators.required]],
      color: [button.color],
      sort: [button.sort],
      curd: [button.curd],
      verify: [button.verify],
    });
  }

  // 选中功能按钮
  defaultBtn(curdType) {
    const formArray = this.ngbForm.controls.buttons as FormArray;
    for (let index = 0; index < formArray.length; index++) {
      const element = <FormGroup>formArray.controls[index];
      if (element.controls.curd.value === curdType) {
        return;
      }
    }
    const btn = new ButtonModel();
    btn.id = new Date().getTime().toString();
    btn.curd = curdType;
    switch (curdType) {
      case GoldbalConstant.CRUD.create:
        btn.title = '增加';
        btn.position = GoldbalConstant.POSTION.NAV;
        btn.option = GoldbalConstant.OPTION.modal;
        btn.modal = 'DataViewEditComponent';
        btn.url =
          SmServerConstant.sqlId_create + this.ngbForm.value.id + '/' + btn.id;
        break;
      case GoldbalConstant.CRUD.delete:
        btn.title = '删除';
        btn.position = GoldbalConstant.POSTION.ROW;
        btn.option = GoldbalConstant.OPTION.service;
        btn.url =
          SmServerConstant.sqlId_delete + this.ngbForm.value.id + '/' + btn.id;
        btn.verify = true;
        break;
      case GoldbalConstant.CRUD.update:
        btn.title = '修改';
        btn.position = GoldbalConstant.POSTION.ROW;
        btn.option = GoldbalConstant.OPTION.modal;
        btn.modal = 'DataViewEditComponent';
        btn.verify = true;
        btn.url =
          SmServerConstant.sqlId_modfity + this.ngbForm.value.id + '/' + btn.id;
        break;
      case GoldbalConstant.CRUD.retrieve:
        btn.title = '查看';
        btn.position = GoldbalConstant.POSTION.ROW;
        btn.option = GoldbalConstant.OPTION.modal;
        btn.modal = 'DataViewEditComponent';
        btn.url = SmServerConstant.sqlId_fetch + this.ngbForm.value.id;
        btn.verify = true;
        break;
      default:
        break;
    }
    formArray.push(this.buildBtnFormGroup(btn));
  }

  // 删除行
  removeControls(controls, idx) {
    controls.removeAt(idx);
  }

  // 列更多设置
  openMore(content) {
    // 弹出组件
    this.modalService
      .open(content, { form: content })
      .subscribe((button: ButtonModel) => {});
  }

  // ztree 数据源
  openZtreeSqlDefine() {
    // 数据源选择器
    this.modalService.static(SqlSelectorComponent, {}).subscribe(result => {
      if (!result) {
        return;
      }
      const _treeFormGroup = this.ngbForm.controls.treeOptions as FormGroup;
      const _selectedSqlId = result[0].id;

      if (_selectedSqlId === _treeFormGroup.controls.sqlId.value) {
        return;
      }
      _treeFormGroup.controls.sqlId.setValue(_selectedSqlId);

      // clear idKey,pIdkey name field
      _treeFormGroup.controls.idKey.setValue('');
      _treeFormGroup.controls.pidKey.setValue('');
      _treeFormGroup.controls.name.setValue('');

      // 刷新sqldefine fields
      this.refreshZtreeSqlIdFields(_selectedSqlId);
    });
  }

  // ztree sqldefine
  refreshZtreeSqlIdFields(sqlId) {
    this.httpService
      .post<IResponse<any>>(
        SmServerConstant.showFullColumns + '/' + sqlId,
        null,
      )
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          // 刷新ztree关系字段
          this.ztreeSqlDefineFields = resp.result;
        } else {
          this.msgService.error(resp.message);
        }
      });
  }

  // 创建树
  buildTreeGroup() {
    this.treeFromGroup = this.fb.group({
      visible: [this.formData.treeOptions.visible],
      sqlId: [this.formData.treeOptions.sqlId],
      idKey: [this.formData.treeOptions.idKey],
      name: [this.formData.treeOptions.name],
      pidKey: [this.formData.treeOptions.pidKey],
      scope: [this.formData.treeOptions.scope],
      width: [this.formData.treeOptions.width],
      enable: [this.formData.treeOptions.enable],
      foreignKey: [this.formData.treeOptions.foreignKey],
    });

    // refresh ztree
    if (this.formData.treeOptions.sqlId) {
      this.refreshZtreeSqlIdFields(this.formData.treeOptions.sqlId);
    }
  }

  buildOptionsFormGroup() {
    // options fromGroup
    this.optionsFormGroup = this.fb.group({
      url: [this.formData.options.url, [Validators.maxLength(200)]],
      method: [
        this.formData.options.method,
        [Validators.required, Validators.maxLength(6)],
      ],
      pagination: [this.formData.options.pagination],
      pageSize: [
        this.formData.options.pageSize,
        [Validators.required, Validators.maxLength(3)],
      ],
      pageNumber: [this.formData.options.pageNumber],
      showExport: [this.formData.options.showExport],
      exportDataType: [this.formData.options.exportDataType],
      undefinedText: [this.formData.options.undefinedText],
      searchText: [this.formData.options.searchText],
      sortable: [this.formData.options.sortable],
      sortStable: [this.formData.options.sortStable],
      dataField: [this.formData.options.dataField],
      totalField: [this.formData.options.totalField],
      selectItemName: [this.formData.options.selectItemName],
      smartDisplay: [this.formData.options.smartDisplay],
      escape: [this.formData.options.escape],
      search: [this.formData.options.search],
      searchOnEnterKey: [this.formData.options.searchOnEnterKey],
      strictSearch: [this.formData.options.strictSearch],
      searchTimeOut: [this.formData.options.searchTimeOut],
      trimOnSearch: [this.formData.options.trimOnSearch],
      showHeader: [this.formData.options.showHeader],
      showFooter: [this.formData.options.showFooter],
      cardView: [this.formData.options.cardView],
      showColumns: [this.formData.options.showColumns],
      showRefresh: [this.formData.options.showRefresh],
      showPaginationSwitch: [this.formData.options.showPaginationSwitch],
      idField: [this.formData.options.idField],
      version: [this.formData.options.version],
      uniqueId: [this.formData.options.uniqueId],
      detailView: [this.formData.options.detailView],
      clickToSelect: [this.formData.options.clickToSelect],
      singleSelect: [this.formData.options.singleSelect],
      showToggle: [this.formData.options.showToggle],
      sidePagination: [this.formData.options.sidePagination],
      queryParamsType: [this.formData.options.queryParamsType],
      checkboxHeader: [this.formData.options.checkboxHeader],
      maintainSelected: [this.formData.options.maintainSelected],
      height: [this.formData.options.height],
      pageList: [this.formData.options.pageList],
      autoload: [this.formData.options.autoload],
      classes: [this.formData.options.classes],
      sortClass: [this.formData.options.sortClass],
      striped: [this.formData.options.striped],
      sortOrder: [this.formData.options.sortOrder],
      iconsPrefix: [this.formData.options.iconsPrefix],
      iconSize: [this.formData.options.iconSize],
      buttonsClass: [this.formData.options.buttonsClass],
      ajax: [this.formData.options.ajax],
      contentType: [this.formData.options.contentType],
      dataType: [this.formData.options.dataType],
      ajaxOptions: [this.formData.options.ajaxOptions],
      queryParams: [this.formData.options.queryParams],
      paginationLoop: [this.formData.options.paginationLoop],
      onlyInfoPagination: [this.formData.options.onlyInfoPagination],
      minimumCountColumns: [this.formData.options.minimumCountColumns],
      paginationVAlign: [this.formData.options.paginationVAlign],
      paginationHAlign: [this.formData.options.paginationHAlign],
      paginationDetailHAlign: [this.formData.options.paginationDetailHAlign],
      paginationPreText: [this.formData.options.paginationPreText],
      paginationNextText: [this.formData.options.paginationNextText],
      toolbar: [this.formData.options.toolbar],
      locale: [this.formData.options.locale],
      cache: [this.formData.options.cache],
      editView: [this.formData.options.editView],
      searchAlign: [this.formData.options.searchAlign],
      buttonsAlign: [this.formData.options.buttonsAlign],
      toolbarAlign: [this.formData.options.toolbarAlign],
      silentSort: [this.formData.options.silentSort],
    });
  }

  buildDataFilter(dataFilter: DataFilterModel) {
    return this.fb.group({
      title: [dataFilter.title],
      field: [dataFilter.field],
      fieldType: [dataFilter.fieldType],
      expression: [dataFilter.expression, [Validators.required]],
      extra: [dataFilter.extra],
      sort: [dataFilter.sort],
    });
  }

  // 构建字段
  buildFieldGroup(fieldModel: FieldModel) {
    return this.fb.group({
      field: [
        fieldModel.field,
        [Validators.required, Validators.maxLength(50)],
      ],
      title: [fieldModel.title, [Validators.required]],
      updateType: [fieldModel.updateType, [Validators.maxLength(30)]],
      view: [fieldModel.view],
      insert: [fieldModel.insert],
      visible: [fieldModel.visible],
      dataType: [fieldModel.dataType, [Validators.maxLength(30)]],
      fieldType: [fieldModel.fieldType, [Validators.maxLength(30)]],
      maxlength: [fieldModel.maxlength],
      sort: [fieldModel.sort, [Validators.maxLength(10)]],
      align: [fieldModel.align, [Validators.maxLength(10)]],
      halign: [fieldModel.halign, [Validators.maxLength(10)]],
      falign: [fieldModel.falign, [Validators.maxLength(10)]],
      radio: [fieldModel.radio],
      checkbox: [fieldModel.checkbox],
      valign: [fieldModel.valign],
      width: [fieldModel.width],
      sortable: [fieldModel.sortable],
      order: [fieldModel.order],
      cardVisible: [fieldModel.cardVisible],
      switchable: [fieldModel.switchable],
      duplicated: [fieldModel.duplicated],
      clickToSelect: [fieldModel.clickToSelect],
      formatter: [fieldModel.formatter],
      sortName: [fieldModel.sortName],
      searchable: [fieldModel.searchable],
      pattern: [fieldModel.pattern],
      titleTooltip: [fieldModel.titleTooltip],
      rowspan: [fieldModel.rowspan],
      colspan: [fieldModel.colspan],
      searchFormatter: [fieldModel.searchFormatter],
      escape: [fieldModel.escape],
      dataFormat: [fieldModel.dataFormat],
    });
  }

  // 根据SQLID生成列
  createColumnList() {
    if (!this.ngbForm.value.sqlId) {
      this.msgService.warning('请先设置sqlId!');
      return;
    }
    this.httpService
      .post<IResponse<Array<any>>>(
        SmServerConstant.showFullColumns + '/' + this.ngbForm.value.sqlId,
      )
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          const dataList = resp.result;

          // 刷新ztree关系字段
          this.currentSqlDefineFields = resp.result;

          // 刷新过滤器列,其实可以不用刷新
          this.formData.fields = resp.result;

          // 清空数据过滤
          this.clearFormArray(this.ngbForm.controls.dataFilters as FormArray);

          // 清空现在有的视图字段列表
          const fieldcontrols = this.ngbForm.controls.fields as FormArray;
          this.clearFormArray(fieldcontrols);
          this.buildFieldFormArray(fieldcontrols, dataList);

          this.msgService.success(resp.message);
        } else {
          this.msgService.error(resp.message);
        }
      });
  }

  showTreeCheck() {
    this.formData = this.ngbForm.value;
    if (!this.formData.treeOptions.visible) {
      for (const key in this.treeFromGroup.controls) {
        this.treeFromGroup.controls[key].setValidators(Validators.required);
        this.treeFromGroup.controls[key].updateValueAndValidity();
      }
    } else {
      for (const key in this.treeFromGroup.controls) {
        this.treeFromGroup.controls[key].clearValidators();
        this.treeFromGroup.controls[key].updateValueAndValidity();
      }
    }
  }

  // 过滤条件选中
  filterSelected(column) {
    const datafilter = new DataFilterModel();
    const control = column.controls;
    datafilter.fieldType = control.fieldType.value;
    datafilter.field = control.field.value;
    datafilter.title = control.title.value;
    datafilter.expression = '=';
    datafilter.sort = control.sort.value;
    const controls = this.ngbForm.controls.dataFilters as FormArray;
    controls.push(
      this.fb.group({
        title: [datafilter.title, [Validators.required]],
        field: [datafilter.field],
        fieldType: [
          datafilter.fieldType,
          [Validators.required, Validators.maxLength(50)],
        ],
        expression: [datafilter.expression],
        extra: [datafilter.extra],
        sort: [datafilter.sort],
      }),
    );
  }

  // 清空
  clearFormArray(formArray) {
    for (let idx = formArray.length; idx >= 0; idx--) {
      formArray.removeAt(idx);
    }
  }

  // 查询sql数据源选择器
  openSelector() {
    if (this.formData.id !== '' || this.ngbForm.controls.id.value === '') {
      return;
    }

    // 查询sqldefine
    // SelectorComponent
    this.modalService.static(SqlSelectorComponent, {}).subscribe(result => {
      if (!result) {
        return;
      }

      const _selectedValue = result[0];
      if (_selectedValue.id === this.ngbForm.controls.sqlId.value) {
        return;
      }

      // 设置查询类型
      this.ngbForm.controls.manipulate.setValue(_selectedValue.manipulate);
      if (GoldbalConstant.manipulate.QUERY === _selectedValue.manipulate) {
        this.optionsFormGroup.controls.idField.setValue(null);
        this.optionsFormGroup.controls.idField.clearValidators();
      } else {
        this.optionsFormGroup.controls.idField.setValidators(
          Validators.required,
        );
        this.ngbForm.controls.buttons.setValue(new Array<ButtonModel>());
      }
      this.optionsFormGroup.controls.idField.updateValueAndValidity();

      // 返回的sqlId
      this.ngbForm.controls.sqlId.setValue(_selectedValue.id);

      // 更新默认数据请求地址:当前视图id
      this.optionsFormGroup.controls.url.setValue(
        // SmServerConstant.sqlId_list + _selectedValue.id,
        SmServerConstant.sqlId_list + this.ngbForm.controls.id.value,
      );

      // 设置默认主键
      if (_selectedValue.pri) {
        this.optionsFormGroup.controls.idField.setValue(_selectedValue.pri);
        this.optionsFormGroup.controls.uniqueId.setValue(_selectedValue.pri);
      }

      // 清空数据过滤
      this.clearFormArray(this.ngbForm.controls.dataFilters as FormArray);

      // 清空现在有的视图字段列表
      this.clearFormArray(this.ngbForm.controls.fields as FormArray);
    });
  }

  // 提交表单
  onSubmit() {
    let url = SmServerConstant.insert;
    if (this.formData.id) {
      url = SmServerConstant.update;
    }
    this.formData = this.ngbForm.value;
    this.httpService
      .post<IResponse<any>>(url, this.formData)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.msgService.success(resp.message);
          this.cancel();
        } else {
          this.msgService.error(resp.message);
        }
      });
  }

  cancel() {
    this.modal.close(false);
  }

  // 变更
  onValueChanged(data?: any) {
    if (!this.ngbForm) {
      return;
    }
    // this.formErrors = this.formVerifiyService.formVerifiy(this.ngbForm, data);
  }

  // 表单校验设置
  formCheckRule(fieldControl) {
    this.modalService
      .static(PatternComponent, { formGroup: fieldControl })
      .subscribe(result => {
        if (result) {
          fieldControl.controls.pattern.setValue(result);
        }
      });
  }

  get fieldsControls() {
    const formArray = this.ngbForm.controls.fields as FormArray;
    return formArray.controls;
  }
  get dataFiltersControls() {
    const formArray = this.ngbForm.controls.dataFilters as FormArray;
    return formArray.controls;
  }
  get buttonsControls() {
    const formArray = this.ngbForm.controls.buttons as FormArray;
    return formArray.controls;
  }
}

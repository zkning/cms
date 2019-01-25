import {
  Component,
  OnInit,
  ViewChild,
  Type,
  ComponentFactoryResolver,
} from '@angular/core';
import { _HttpClient, ModalHelper, TitleService } from '@delon/theme';
import {
  SimpleTableColumn,
  SimpleTableComponent,
  ReuseTabService,
} from '@delon/abc';
import { SFSchema } from '@delon/form';
import { FormGroup, FormBuilder, FormArray, Validators } from '@angular/forms';
import { DataViewEditModel } from '../model/dataviewedit.model';
import { DataFilterModel } from '../model/datafilter.model';
import { Router, ActivatedRoute } from '@angular/router';
import { NzMessageService, NzTreeNode, NzModalService } from 'ng-zorro-antd';
import { DataViewListResolver } from '../resolver/dataviewlist.resolver';
import { TreeConfig } from '../../rbac/model/treeconfig.model';
import { IResponse } from '@core/net/model/IResponse';
import { SmServerConstant } from '../constants/smserver.constants';
import { FilterModel } from '../model/filter.model';
import { GoldbalConstant } from '../constants/goldbal.constant';
import { ButtonModel } from '../model/button.model';
import { BootstrapPageResult } from '../model/bootstrapPageResult.model';
import { DataViewEditComponent } from './dataViewEdit.component';
import { Title } from '@angular/platform-browser';

// 视图列表页 ningzuokun
@Component({
  selector: 'app-sm-dataviewlist',
  templateUrl: './dataviewlist.component.html',
  styles: [
    `
      .datafilter {
        margin-top: 5px;
      }
    `,
  ],
})
export class SmDataviewlistComponent implements OnInit {
  // 数据model
  dataViewEditModel: DataViewEditModel;

  // 条件过滤表单
  searchForm: FormGroup;

  // 视图查询参数
  bootstrapPageResult = new BootstrapPageResult();

  // 导航按钮
  navButtons = new Array<ButtonModel>();

  // 内置按钮
  rowButtons = new Array<ButtonModel>();

  // 构建tree
  treeConfig = new TreeConfig();

  constructor(
    public http: _HttpClient,
    private modal: ModalHelper,
    private dataViewListResolver: DataViewListResolver,
    private route: ActivatedRoute,
    private msgService: NzMessageService,
    private fb: FormBuilder,
    public modalService: NzModalService,
    private titleService: Title,
    private reuseTabService: ReuseTabService,
    public componentFactoryResolver: ComponentFactoryResolver,
  ) {}

  params: any;
  ngOnInit() {
    // 监控路由守卫获取初始化数据
    this.route.data.subscribe(resp => {
      if (resp.dataViewListResolver) {
        if (!this.dataViewEditModel) {
          this.dataViewEditModel = resp.dataViewListResolver.result;
          this.titleService.setTitle(this.dataViewEditModel.dataViewName);
          this.reuseTabService.title = this.dataViewEditModel.dataViewName;
          this.styleRate();
          // console.info(this.dataViewEditModel);
        }
      }
    });
    this.searchForm = this.fb.group({
      dataFilters: this.fb.array(new Array<any>()),
    });
    const dataFilters = this.searchForm.controls.dataFilters as FormArray;
    this.buildFilterFormArray(dataFilters, this.dataViewEditModel.dataFilters);
    this.getDataSet();
    this.buttonfilter();
    this.refreshNode();
  }

  // 构建 过滤条件
  buildFilterFormArray(formArray, dataList: Array<DataFilterModel>) {
    if (!formArray || !dataList) {
      return;
    }
    dataList.forEach(item => {
      const filter = new FilterModel();
      filter.title = item.title;
      filter.field = item.field;
      filter.fieldType = item.fieldType;
      filter.expression = item.expression;
      filter.sort = item.sort;
      filter.extra = item.extra;
      formArray.push(this.buildDataFilter(filter));
    });
  }

  buildDataFilter(dataFilter: FilterModel) {
    return this.fb.group({
      title: [dataFilter.title],
      field: [dataFilter.field],
      fieldType: [dataFilter.fieldType],
      expression: [dataFilter.expression, [Validators.required]],
      extra: [dataFilter.extra],
      sort: [dataFilter.sort],
      value: [dataFilter.value],
    });
  }

  reset() {
    const dataFilters = this.searchForm.controls.dataFilters as FormArray;
    dataFilters.controls.forEach(element => {
      const filter = element.value;
      filter.value = null;
      element.setValue(filter);
    });
  }

  get dataFiltersControls() {
    const formArray = this.searchForm.controls.dataFilters as FormArray;
    return formArray.controls;
  }

  refreshNode() {
    if (!this.dataViewEditModel.treeOptions.visible) {
      return;
    }
    this.http
      .get<IResponse<Array<any>>>(
        SmServerConstant.sqlId_getTree,
        this.dataViewEditModel.treeOptions,
      )
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.treeConfig.ngModel = [];
          resp.result.forEach(tnm => {
            this.treeConfig.ngModel.push(new NzTreeNode(tnm));
          });
        } else {
          this.msgService.error(resp.message);
        }
      });
  }

  // tree节点点击事件
  nzTreeClick(e) {
    // this.dataViewEditModel.treeOptions.nodeValue = e.node.origin.id;
    this.dataViewEditModel.treeOptions.nodeValue = e.node.key;
    // 分页重置为1
    this.dataViewEditModel.options.pageNumber = 1;
    this.getDataSet([]);
  }

  labelFormat(title) {
    if (title && title.length > 4) {
      return title.substr(0, 3) + '..';
    }
    return title;
  }

  reload() {
    this.dataViewEditModel.options.pageNumber = 1;
    this.getDataSet();
    if (this.dataViewEditModel.treeOptions.visible) {
      // this.refreshNode();
    }
  }

  curd(button: ButtonModel, item?: any) {
    switch (button.curd) {
      case GoldbalConstant.CRUD.create:
        // DataViewCreateComponent 编辑
        this.modal
          .static(
            this.componentFactory(button.modal),
            {
              dataViewEditModel: this.dataViewEditModel,
              buttonOpts: button,
              record: {},
            },
            button.size,
          )
          .subscribe(result => {
            if (result) {
              this.reload();
            }
          });
        return;
      case GoldbalConstant.CRUD.update:
      case GoldbalConstant.CRUD.retrieve:
        // 根据sqlid查询出要修改的数据
        this.http
          .get<IResponse<any>>(
            SmServerConstant.sqlId_fetch + this.dataViewEditModel.id,
            {
              id: item[this.dataViewEditModel.options.idField],
            },
          )
          .subscribe(resp => {
            if (IResponse.statuscode.SUCCESS === resp.code) {
              // DataViewCreateComponent 编辑
              this.modal
                .static(
                  this.componentFactory(button.modal),
                  {
                    dataViewEditModel: this.dataViewEditModel,
                    record: resp.result,
                    buttonOpts: button,
                  },
                  button.size,
                )
                .subscribe(result => {
                  if (result) {
                    this.reload();
                  }
                });
            } else {
              this.msgService.error(resp.message);
            }
          });
        return;
      case GoldbalConstant.CRUD.delete:
        this.modalService.confirm({
          nzTitle: '<i>确定要删除吗?</i>',
          nzContent: '<b></b>',
          nzOnOk: () => {
            // 默认删除接口
            const url = button.url;
            // if (!url) {
            //   url = SmServerConstant.sqlId_delete + this.dataViewEditModel.id;
            // }
            this.http.post<IResponse<any>>(url, item).subscribe(resp => {
              if (IResponse.statuscode.SUCCESS === resp.code) {
                this.reload();
              } else {
                this.msgService.error(resp.message);
              }
            });
          },
        });
        return;
      default:
        break;
    }
  }

  // 行内置按钮
  rowButtonClick(button: ButtonModel, record: any) {
    const priValue = record[this.dataViewEditModel.options.idField];
    if (!priValue) {
      this.msgService.error('主键错误!');
      return;
    }

    // 是否为CURD操作
    if (button.curd) {
      this.curd(button, record);
      return;
    }

    this.notCurdClick(button, record);
  }

  //  导航按钮
  navButtonClick(button: ButtonModel) {
    if (button.curd) {
      this.curd(button);
      return;
    }
    let record = [];

    // 获取checkbox行数据
    if (this.dataViewEditModel.options.checkboxHeader) {
      record = this.getCheckedItems();
    }

    // 判断是否需要校验数据
    if (button.verify && record.length < 1) {
      this.msgService.warning('请选择要操作的记录!');
      return;
    }
    this.notCurdClick(button, record);
  }

  // 非CURD
  notCurdClick(button: ButtonModel, record: any) {
    switch (button.option) {
      // 服务
      case GoldbalConstant.OPTION.service:
        this.http
          .post<IResponse<any>>(button.url, {
            // sqlId: this.dataViewEditModel.sqlId,
            record: record,
          })
          .subscribe(resp => {
            if (IResponse.statuscode.SUCCESS === resp.code) {
              this.msgService.success(resp.message);
              this.reload();
            } else {
              this.msgService.error(resp.message);
            }
          });
        break;
      case GoldbalConstant.OPTION.modal:
        // 控件传id指定组件
        this.modal
          .static(
            this.componentFactory(button.modal),
            {
              // dataViewEditModel: this.dataViewEditModel,
              record: record,
              curd: button.curd,
            },
            button.size,
          )
          .subscribe(result => {
            if (result) {
              this.reload();
            }
          });
        break;
      case GoldbalConstant.OPTION.window:
        // TODO..
        break;
      default:
        break;
    }
  }

  componentFactory(componentName) {
    const factories = Array.from(
      this.componentFactoryResolver['_factories'].keys(),
    );
    const factoryClass = <Type<any>>(
      factories.find((x: any) => x.name === componentName)
    );
    return factoryClass;
  }

  // 按钮分配
  buttonfilter() {
    if (!this.dataViewEditModel.buttons) {
      return;
    }
    this.dataViewEditModel.buttons.forEach(element => {
      if (element.position === GoldbalConstant.POSTION.ROW) {
        this.rowButtons.push(element);
      } else if (element.position === GoldbalConstant.POSTION.NAV) {
        this.navButtons.push(element);
      }
    });
  }

  // 可见列表列
  get visibleFields() {
    const visibleItems = new Array<any>();
    this.dataViewEditModel.fields.forEach(item => {
      if (item.visible) {
        visibleItems.push(item);
      }
    });
    return visibleItems;
  }

  getDataSet(dataFilters?) {
    const params = {
      searchArray: dataFilters || this.searchForm.value.dataFilters,
      treeOptions: this.dataViewEditModel.treeOptions,
      pageSize: this.dataViewEditModel.options.pageSize,
      pageNumber: this.dataViewEditModel.options.pageNumber,
      sortName: this.sortName,
      sortOrder: this.sortValue,
    };
    let url = SmServerConstant.sqlId_list + this.dataViewEditModel.id;
    if (!this.dataViewEditModel.options.url) {
      url = this.dataViewEditModel.options.url;
    }

    // TODO 添加查询参数
    if (this.dataViewEditModel.options.method === GoldbalConstant.METHOD.GET) {
      this.http
        .get<IResponse<BootstrapPageResult>>(url, params)
        .subscribe(resp => this.subscribe(resp));
      return;
    }

    this.http
      .post<IResponse<BootstrapPageResult>>(url, params)
      .subscribe(resp => this.subscribe(resp));
  }

  subscribe(resp) {
    if (IResponse.statuscode.SUCCESS === resp.code) {
      this.bootstrapPageResult = resp.result;
    } else {
      this.msgService.error(resp.message);
    }
  }

  toJson(jsonstr) {
    return JSON.parse(jsonstr);
  }

  // 获取选中记录
  getCheckedItems() {
    const checkedItems = new Array<any>();
    this.displayData.forEach(item => {
      if (item.checked) {
        checkedItems.push(item);
      }
    });
    return checkedItems;
  }

  // nz-table start --------------------------------------------------------------------------------------------------------------------------------------------------
  nzPageIndexChange(pageNumber) {
    this.dataViewEditModel.options.pageNumber = pageNumber;
    this.getDataSet();
  }

  nzPageSizeChange(pageSize) {
    this.dataViewEditModel.options.pageSize = pageSize;
    this.getDataSet();
  }

  // 当前显示数据
  displayData: Array<any>;
  allChecked = false;
  indeterminate = false;
  currentPageDataChange(
    $event: Array<{
      name: string;
      age: number;
      address: string;
      checked: boolean;
      expand: boolean;
      description: string;
    }>,
  ): void {
    this.displayData = $event;
    this.refreshStatus();
  }

  checkAll(value: boolean): void {
    this.displayData.forEach(data => {
      if (!data.disabled) {
        data.checked = value;
      }
    });
    this.refreshStatus();
  }

  refreshStatus(): void {
    const validData = this.displayData.filter(value => !value.disabled);
    const allChecked =
      validData.length > 0 && validData.every(value => value.checked === true);
    const allUnChecked = validData.every(value => !value.checked);
    this.allChecked = allChecked;
    this.indeterminate = !allChecked && !allUnChecked;
  }

  // 排序
  sortName = null;
  sortValue = null;
  sort(sort: { key: string; value: string }): void {
    this.sortName = sort.key;
    if ('descend' === sort.value) {
      this.sortValue = 'desc';
    } else if ('ascend' === sort.value) {
      this.sortValue = 'asc';
    } else {
      this.sortValue = null;
    }
    this.getDataSet();
  }
  // nz-table end --------------------------------------------------------------------------------------------------------------------------------------------------

  treeStyle: any;
  gridStyle: any;

  styleRate() {
    this.gridStyle = { width: '100%' };
    if (!this.dataViewEditModel.treeOptions.visible) {
      return;
    }
    this.treeStyle = {
      width: this.dataViewEditModel.treeOptions.width + '%',
      float: 'left',
    };
    this.gridStyle = {
      'margin-left': '10px',
      width: 99 - this.dataViewEditModel.treeOptions.width + '%',
      float: 'left',
    };
  }
}

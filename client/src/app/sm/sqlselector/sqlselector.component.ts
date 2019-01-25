import { Component, OnInit, ViewChild } from '@angular/core';
import {
  NzModalRef,
  NzMessageService,
  NzModalService,
  NzTreeNode,
} from 'ng-zorro-antd';
import { _HttpClient, ModalHelper } from '@delon/theme';
import { SFSchema, SFUISchema } from '@delon/form';
import { ActivatedRoute, Router } from '@angular/router';
import {
  ReuseTabService,
  SimpleTableColumn,
  SimpleTableComponent,
} from '@delon/abc';
import { FormBuilder } from '@angular/forms';
import { IResponse } from '@core/net/model/IResponse';
import { SmServerConstant } from '../constants/smserver.constants';

@Component({
  selector: 'app-sqllist',
  templateUrl: './sqlselector.component.html',
})
export class SqlSelectorComponent implements OnInit {
  record: any = {};
  ngOnInit(): void {}

  constructor(
    public msgSrv: NzMessageService,
    public http: _HttpClient,
    public activeRoute: ActivatedRoute,
    private router: Router,
    public modalService: NzModalService,
    private reuseTabService: ReuseTabService,
    private fb: FormBuilder,
    private modal: ModalHelper,
    private nzModalRef: NzModalRef,
  ) {}
  params: any = {};
  url = SmServerConstant.sqldefine_list;

  searchSchema: SFSchema = {
    properties: {
      id: {
        type: 'string',
        title: 'ID',
      },
      sqlName: {
        type: 'string',
        title: '别名',
      },
      tableName: {
        type: 'string',
        title: '对象',
      },
    },
  };

  @ViewChild('st') st: SimpleTableComponent;
  columns: SimpleTableColumn[] = [
    { title: '选择', type: 'radio', index: 'id' },
    { title: 'ID', index: 'id' },
    { title: '别名', index: 'sqlName' },
    { title: '数据源', index: 'datasource' },
    { title: '对象', index: 'tableName' },
    { title: '主键', index: 'pri' },
    // { title: '是否缓存', index: 'isCache' },
    { title: '状态', index: 'state' },
  ];

  // 多选
  checkedRows = [];
  checkboxChange(list: any[]) {
    this.checkedRows = list;
  }

  // 单选
  radioChange(item: any) {
    this.checkedRows = [];
    this.checkedRows.push(item);
  }

  save() {
    if (this.checkedRows.length <= 0) {
      this.msgSrv.warning('请选择SQL');
      return;
    }

    const checkedIds = [];
    this.checkedRows.forEach(item => {
      checkedIds.push(item.id);
    });

    this.nzModalRef.close(this.checkedRows);
  }

  close() {
    this.nzModalRef.destroy();
  }
}

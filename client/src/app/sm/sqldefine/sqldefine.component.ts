import { Component, OnInit, ViewChild } from '@angular/core';
import { _HttpClient, ModalHelper } from '@delon/theme';
import { SimpleTableColumn, SimpleTableComponent } from '@delon/abc';
import { SFSchema } from '@delon/form';
import { ACLService } from '@delon/acl';
import { NzModalService, NzMessageService } from 'ng-zorro-antd';
import { Router } from '@angular/router';
import { SmServerConstant } from '../constants/smserver.constants';
import { SmDataviewEditComponent } from '../dataview/edit/edit.component';
import { IResponse } from '@core/net/model/IResponse';
import { SmSqldefineEditComponent } from './edit/edit.component';

@Component({
  selector: 'app-sm-sqldefine',
  templateUrl: './sqldefine.component.html',
})
export class SmSqldefineComponent implements OnInit {
  params: any = {};
  url = SmServerConstant.sqldefine_list;

  constructor(
    private http: _HttpClient,
    public msg: NzMessageService,
    private modal: ModalHelper,
    private router: Router,
    private aclService: ACLService,
    public modalService: NzModalService,
  ) {}

  ngOnInit() {}

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
    { title: '序号', index: 'no' },
    { title: 'ID', index: 'id' },
    { title: '别名', index: 'sqlName' },
    { title: '数据源', index: 'datasourceText' },
    { title: '对象', index: 'tableName' },
    { title: '主键', index: 'pri' },
    // { title: '是否缓存', index: 'isCache' },
    { title: '状态', index: 'stateText' },
    {
      title: '操作',
      buttons: [
        {
          text: '编辑',
          click: (item: any) => {
            this.modal
              .static(SmSqldefineEditComponent, { record: item })
              .subscribe(() => {
                this.st.load();
              });
          },
        },
        {
          text: '删除',
          click: (item: any) => {
            this.modalService.confirm({
              nzTitle: '<i>确定要删除吗?</i>',
              nzContent: '<b>删除后引用此SQL定义的视图将无法工作</b>',
              nzOnOk: () => {
                this.http
                  .get<IResponse<any>>(SmServerConstant.sqldefine_delete, item)
                  .subscribe(resp => {
                    if (IResponse.statuscode.SUCCESS === resp.code) {
                      this.st.load();
                    } else {
                      this.msg.info(resp.message);
                    }
                  });
              },
            });
          },
        },
      ],
    },
  ];

  add() {
    this.modal
      .static(SmSqldefineEditComponent, { record: {} }, 800)
      .subscribe(() => {
        this.st.load();
      });
  }

  preDataChange(data) {
    for (let index = 0; index < data.length; index++) {
      data[index]['no'] = index + 1;
    }
    return data;
  }
}

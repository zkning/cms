import { Component, OnInit, ViewChild } from '@angular/core';
import { _HttpClient, ModalHelper } from '@delon/theme';
import {
  SimpleTableColumn,
  SimpleTableComponent,
  ReuseTabService,
} from '@delon/abc';
import { SFSchema } from '@delon/form';
import { ACLService } from '@delon/acl';
import { Router } from '@angular/router';
import { NzMessageService, NzModalService, NzTreeNode } from 'ng-zorro-antd';
import { RbacDataSourceEditComponent } from './edit/edit.component';
import { IResponse } from '@core/net/model/IResponse';
import { SmServerConstant } from '../constants/smserver.constants';

@Component({
  selector: 'app-datasource',
  templateUrl: './datasource.component.html',
})
export class RbacDataSourceComponent implements OnInit {
  params: any = {};
  url = SmServerConstant.ds_list;

  constructor(
    private http: _HttpClient,
    public msg: NzMessageService,
    private modal: ModalHelper,
    private router: Router,
    private reuseTabService: ReuseTabService,
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
      name: {
        type: 'string',
        title: '别名',
      },
      dbUsername: {
        type: 'string',
        title: '用户名',
      },
      url: {
        type: 'string',
        title: 'url',
      },
    },
  };

  @ViewChild('st') st: SimpleTableComponent;
  columns: SimpleTableColumn[] = [
    { title: '序号', index: 'no' },
    { title: 'ID', index: 'id' },
    { title: '别名', index: 'name' },
    { title: 'url', index: 'url' },
    { title: '用户名', index: 'dbUsername' },
    { title: '密码', index: 'dbPassword' },
    { title: '描述', index: 'remark' },
    {
      title: '操作',
      buttons: [
        {
          text: '编辑',
          click: (item: any) => {
            this.modal
              .static(RbacDataSourceEditComponent, { record: item })
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
              nzContent: '<b>子节点也会同时删除</b>',
              nzOnOk: () => {
                this.http
                  .get<IResponse<any>>(SmServerConstant.ds_delete, item)
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
      .static(RbacDataSourceEditComponent, { record: {} })
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

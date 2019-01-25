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
import { RbacDictEditComponent } from './edit/edit.component';
import { IResponse } from '@core/net/model/IResponse';
import { RbacApplication } from '../constants/rbacapplication.model';
import { TreeConfig } from '../model/treeconfig.model';

@Component({
  selector: 'app-dict',
  templateUrl: './dict.component.html',
})
export class RbacDictComponent implements OnInit {
  params: any = {};
  url = RbacApplication.dict_list;

  constructor(
    private http: _HttpClient,
    public msg: NzMessageService,
    private modal: ModalHelper,
    private router: Router,
    private reuseTabService: ReuseTabService,
    private aclService: ACLService,
    public modalService: NzModalService,
  ) {}

  ngOnInit() {
    this.loadTreeData();
  }

  searchSchema: SFSchema = {
    properties: {
      text: {
        type: 'string',
        title: '文本值',
      },
      value: {
        type: 'string',
        title: '值',
      },
      pidText: {
        type: 'string',
        title: '父级节点',
      },
    },
  };

  @ViewChild('st') st: SimpleTableComponent;
  columns: SimpleTableColumn[] = [
    { title: '序号', index: 'no' },
    { title: '文本值', index: 'text' },
    { title: '值', index: 'value' },
    { title: '所属字典', index: 'pidText' },
    { title: '排序', index: 'sort' },
    {
      title: '操作',
      buttons: [
        {
          text: '编辑',
          click: (item: any) => {
            this.modal
              .static(RbacDictEditComponent, { record: item })
              .subscribe(() => {
                this.st.load();
                this.loadTreeData();
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
                  .get<IResponse<any>>(RbacApplication.dict_delete, item)
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
    this.modal.static(RbacDictEditComponent, { record: {} }).subscribe(() => {
      this.st.load();
      this.loadTreeData();
    });
  }

  groupConfig = new TreeConfig();
  loadTreeData() {
    this.http
      .get<IResponse<Array<any>>>(RbacApplication.dict_tree)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.groupConfig.ngModel = [];
          resp.result.forEach(tnm => {
            this.groupConfig.ngModel.push(new NzTreeNode(tnm));
          });
        } else {
          this.msg.error(resp.message);
        }
      });
  }

  nzTreeClick(e) {
    this.params.pid = e.node.key;
    this.st.reload(this.params);
  }

  preDataChange(data) {
    for (let index = 0; index < data.length; index++) {
      data[index]['no'] = index + 1;
    }
    return data;
  }
}

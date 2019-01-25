import { Component, OnInit, ViewChild } from '@angular/core';
import { _HttpClient, ModalHelper } from '@delon/theme';
import {
  SimpleTableColumn,
  SimpleTableComponent,
  ReuseTabService,
  SimpleTableMultiSort,
} from '@delon/abc';
import { SFSchema, SFUISchema } from '@delon/form';
import { NzTreeNode, NzMessageService, NzModalService } from 'ng-zorro-antd';
import { ActivatedRoute, Router } from '@angular/router';
import { Application } from '@env/application';
import { IResponse } from '@core/net/model/IResponse';
import { TreeNodeModel } from '../model/treeNode.model';
import { FormGroup, Validators, FormBuilder } from '@angular/forms';
import { GroupInfo } from '../model/groupForm.model';
import { RbacApplication } from '../constants/rbacapplication.model';
import { RbacUsereditComponent } from '../useredit/useredit.component';
import { TreeConfig } from '../model/treeconfig.model';
import * as _ from 'lodash';
import { DistConstant } from '../constants/dist.constant';
import { SimpleUtils } from '../utils/simple.util';
import { RbacGroupeditComponent } from './groupedit.component';
@Component({
  selector: 'app-grouplist',
  templateUrl: './grouplist.component.html',
})
export class RbacGrouplistComponent implements OnInit {
  constructor(
    public msgSrv: NzMessageService,
    public http: _HttpClient,
    public activeRoute: ActivatedRoute,
    private router: Router,
    public modalService: NzModalService,
    private reuseTabService: ReuseTabService,
    private fb: FormBuilder,
    private modal: ModalHelper,
  ) {}

  // 批量案件数
  checkedUser: any;

  // selected node
  selectedNode;
  groupTreeConfig = new TreeConfig();

  ngOnInit() {
    this.getGroupTree();
  }

  getGroupTree() {
    this.http
      .get<IResponse<Array<any>>>(RbacApplication.groupTree)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.groupTreeConfig.ngModel = [];
          resp.result.forEach(tnm => {
            this.groupTreeConfig.ngModel.push(new NzTreeNode(tnm));
          });
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  clickAction(e: any) {
    // console.log(name, e);
    this.selectedNode = e.node.key;

    // 刷新成员
    this.params.groupId = e.node.key;
    this.st.reload(this.params);
  }

  groupEdit() {
    const selectedNodes = SimpleUtils.getSelectedNodeList(
      this.groupTreeConfig.ngModel,
    );
    const selectedId = (selectedNodes[0] && selectedNodes[0].key) || '';
    this.modal
      .static(RbacGroupeditComponent, { record: { id: selectedId } })
      .subscribe(result => {
        if (result) {
          this.getGroupTree();
        }
      });
  }

  // 删除用户
  userDelete() {
    if (!this.checkedUser || !this.checkedUser.id) {
      this.msgSrv.warning('请选择需要删除的记录');
      return;
    }

    this.modalService.confirm({
      nzTitle: '<i>确定要删除吗?</i>',
      nzContent: '<b></b>',
      nzOnOk: () => {
        this.http
          .get<IResponse<any>>(RbacApplication.userDelete, this.checkedUser)
          .subscribe(resp => {
            if (IResponse.statuscode.SUCCESS === resp.code) {
              this.msgSrv.info(resp.message);
              this.st.load();
            } else {
              this.msgSrv.error(resp.message);
            }
          });
      },
    });
  }

  // 根据部门获取部门下员工(包括下级机构)
  params: any = {};
  url = RbacApplication.findAllUserByGroupId;
  searchSchema: SFSchema = {
    properties: {
      userName: {
        type: 'string',
        title: '账号',
      },
      name: {
        type: 'string',
        title: '姓名',
      },
      mobile: {
        type: 'string',
        title: '手机号',
      },
      createTime: {
        type: 'string',
        title: '创建时间',
        ui: { widget: 'date', showTime: true },
      },
    },
  };

  @ViewChild('st') st: SimpleTableComponent;
  columns: SimpleTableColumn[] = [
    { title: '选择', type: 'radio', index: 'id' },
    { title: '账号', index: 'userName' },
    { title: '姓名', index: 'name' },
    { title: '所属部门', index: 'groupName' },
    { title: '手机号', index: 'mobile' },
    { title: '创建时间', index: 'createTime' },
    { title: '创建人', index: 'createUser' },
  ];

  // 创建成员
  userEdit() {
    this.modal
      .static(RbacUsereditComponent, { record: this.checkedUser || {} })
      .subscribe(() => {
        this.st.load();
      });
    this.checkedUser = [];
  }

  radioChange(selected: any) {
    this.checkedUser = selected;
  }

  submit(value: any) {
    if (this.params.groupId) {
      value.groupId = this.params.groupId;
    }
    this.st.reload(value);
  }
}

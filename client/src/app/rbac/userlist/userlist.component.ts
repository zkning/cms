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
import { RbacRoleInfo } from '../model/role.model';
import { TreeConfig } from '../model/treeconfig.model';
import { RbacApplication } from '../constants/rbacapplication.model';
import { IResponse } from '@core/net/model/IResponse';

@Component({
  selector: 'app-userlist',
  templateUrl: './userlist.component.html',
})
export class RbacUserlistComponent implements OnInit {
  record: any = {};
  ngOnInit(): void {
    this.params.notInRoleId = this.record.roleId;
    this.getGroupTree();
  }

  groupConfig = new TreeConfig();
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

  // 根据部门获取部门下员工(包括下级机构)
  params: any = {};
  url = RbacApplication.findAllUserByGroupId;
  searchSchema: SFSchema = {
    properties: {
      name: {
        type: 'string',
        title: '姓名',
      },
      mobile: {
        type: 'string',
        title: '手机号',
      },
    },
  };

  nzTreeClick(e) {
    this.params.groupId = e.node.key;
    this.params.notInRoleId = this.record.roleId;
    this.st.reload(this.params);
  }

  @ViewChild('st') st: SimpleTableComponent;
  columns: SimpleTableColumn[] = [
    { title: '选择', type: 'checkbox', index: 'id' },
    // { title: '序号', index: 'no' },
    { title: '账号', index: 'userName' },
    { title: '姓名', index: 'name' },
    { title: '所属部门', index: 'groupName' },
    { title: '手机号', index: 'mobile' },
    { title: '创建时间', index: 'createTime' },
    { title: '创建人', index: 'createUser' },
  ];

  getGroupTree() {
    this.http
      .get<IResponse<Array<any>>>(RbacApplication.groupTree)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.groupConfig.ngModel = [];
          resp.result.forEach(tnm => {
            this.groupConfig.ngModel.push(new NzTreeNode(tnm));
          });
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  checkedUsers = [];
  checkboxChange(list: any[]) {
    this.checkedUsers = list;
  }

  save() {
    if (this.checkedUsers.length <= 0) {
      this.msgSrv.warning('请选择人员');
      return;
    }

    const userIds = [];
    this.checkedUsers.forEach(item => {
      userIds.push(item.id);
    });

    this.http
      .post<IResponse<Array<any>>>(RbacApplication.res_saveRoleUser, {
        roleId: this.record.roleId,
        userIds: userIds,
      })
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.msgSrv.info(resp.message);
          this.nzModalRef.close(true);
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  close() {
    this.nzModalRef.destroy();
  }
}

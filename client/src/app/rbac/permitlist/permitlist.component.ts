import { Component, OnInit, ViewChild } from '@angular/core';
import { _HttpClient, ModalHelper } from '@delon/theme';
import {
  SimpleTableColumn,
  SimpleTableComponent,
  ReuseTabService,
} from '@delon/abc';
import { SFSchema, SFUISchema } from '@delon/form';
import {
  NzMessageService,
  NzModalService,
  NzTreeNode,
  NzTreeComponent,
} from 'ng-zorro-antd';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { RbacApplication } from '../constants/rbacapplication.model';
import { TreeConfig } from '../model/treeconfig.model';
import { IResponse } from '@core/net/model/IResponse';
import { RbacRoleeditComponent } from '../roleedit/roleedit.component';
import { RbacResourceseditComponent } from '../resourcesedit/resourcesedit.component';
import { RbacUserlistComponent } from '../userlist/userlist.component';
import { SimpleUtils } from '../utils/simple.util';

@Component({
  selector: 'app-permitlist',
  templateUrl: './permitlist.component.html',
  // styles: [`
  //     table,
  //     table tr th,
  //     table tr td {
  //       border: 1px solid #e8e8e8;
  //     }
  // `]
})
export class RbacPermitlistComponent implements OnInit {
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

  // 资源树
  resConfig = new TreeConfig();

  // 角色树
  roleDataSet: any;

  // 角色树选中值
  roleSelectedValue: string;

  @ViewChild('resTree') nzTree: NzTreeComponent;
  ngOnInit() {
    this.resConfig.nzCheckable = true;
    this.getResTree();
    this.getRoleList();
    this.dictfindByPValue();
  }

  editRole(item?) {
    this.modal.static(RbacRoleeditComponent, { record: item }).subscribe(() => {
      // this.st.load();
      this.getRoleList();
    });
  }

  deleteRole(item) {
    this.modalService.confirm({
      nzTitle: '<i>确定要删除吗?</i>',
      nzContent: '<b></b>',
      nzOnOk: () => {
        this.http
          .get<IResponse<any>>(RbacApplication.role_delete, item)
          .subscribe(resp => {
            if (IResponse.statuscode.SUCCESS === resp.code) {
              this.msgSrv.success(resp.message);
              this.getRoleList();
            } else {
              this.msgSrv.error(resp.message);
            }
          });
      },
    });
  }

  roleLog(value: { label: string; value: string; age: number }): void {
    this.getRoleList({ roleType: value || '' });
  }

  editRes() {
    const selectedNodes = SimpleUtils.getSelectedNodeList(
      this.resConfig.ngModel,
    );
    const selectedId = (selectedNodes[0] && selectedNodes[0].key) || '';
    this.modal
      .static(RbacResourceseditComponent, { record: { id: selectedId } })
      .subscribe(result => {
        if (result) {
          this.getResTree((this.roleItem && this.roleItem.id) || '');
        }
      });
  }

  deleteRes(item) {
    this.modalService.confirm({
      nzTitle: '<i>确定要删除吗?</i>',
      nzContent: '<b></b>',
      nzOnOk: () => {
        this.http
          .get<IResponse<any>>(RbacApplication.res_delete, item)
          .subscribe(resp => {
            if (IResponse.statuscode.SUCCESS === resp.code) {
              this.msgSrv.success(resp.message);
              this.getResTree();
            } else {
              this.msgSrv.error(resp.message);
            }
          });
      },
    });
  }

  setRoleRes() {
    if (!this.roleItem) {
      this.msgSrv.warning('请选择需要关联的角色');
      return;
    }
    // console.log('checkedNodes: %o', this.nzTree.getCheckedNodeList());
    // console.log('selectedNodes: %o', this.nzTree.getSelectedNodeList());
    // console.log('halfCheckedNodes: %o', this.nzTree.getHalfCheckedNodeList());
    const selectedNodes = SimpleUtils.getCheckedNodes(
      this.nzTree.getCheckedNodeList(),
    );
    const halfCheckedNodeList = this.nzTree.getHalfCheckedNodeList();
    if (halfCheckedNodeList) {
      halfCheckedNodeList.forEach(item => {
        selectedNodes.push(item);
      });
    }
    // console.log('selectedNodes: %o', selectedNodes);

    const checkedIds = [];
    selectedNodes.forEach(item => {
      checkedIds.push(item.key);
    });

    this.http
      .post<IResponse<any>>(RbacApplication.res_saveRoleRes, {
        roleId: this.roleItem.id,
        resourceIds: checkedIds,
      })
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.msgSrv.success(resp.message);
          this.getResTree(this.roleItem.id);
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  addRoleUser() {
    if (!this.roleItem) {
      this.msgSrv.warning('请选择需要关联的角色');
      return;
    }

    this.modal
      .static(
        RbacUserlistComponent,
        { record: { roleId: this.roleItem.id } },
        1200,
      )
      .subscribe(() => {
        this.params.roleId = this.roleItem.id;
        this.st.reload(this.params);
      });
  }

  checkedUsers = [];
  checkboxChange(list: any[]) {
    this.checkedUsers = list;
  }

  removeRoleUser() {
    if (!this.roleItem) {
      this.msgSrv.warning('请选择角色');
      return;
    }

    if (this.checkedUsers.length <= 0) {
      this.msgSrv.warning('请选择需要移除的人员');
      return;
    }

    const userIds = [];
    this.checkedUsers.forEach(item => {
      userIds.push(item.id);
    });

    this.modalService.confirm({
      nzTitle: '<i>确定要移除吗?</i>',
      nzContent: '<b></b>',
      nzOnOk: () => {
        //  获取选中用户
        this.http
          .post<IResponse<any>>(RbacApplication.res_removeRoleUser, {
            roleId: this.roleItem.id,
            userIds: userIds,
          })
          .subscribe(resp => {
            if (IResponse.statuscode.SUCCESS === resp.code) {
              this.msgSrv.success(resp.message);
              this.params.roleId = this.roleItem.id;
              this.st.reload(this.params);

              // 清空选择的人员
              this.checkedUsers = [];
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
      beginDate: {
        type: 'string',
        title: '创建时间',
        ui: { widget: 'date', showTime: true },
      },
    },
  };

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
  nzExpandChange(name: string, e: any): void {}

  roleItem: any;
  roleClick(item) {
    // 清空选择的人员
    this.checkedUsers = [];
    this.roleItem = item;
    this.getResTree(item.id);
    this.params.roleId = item.id;
    this.st.reload(this.params);
  }
  getRoleList(params?) {
    this.http
      .get<IResponse<Array<any>>>(RbacApplication.role_list, params)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.roleDataSet = resp.result;
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }
  getResTree(roleId?) {
    this.http
      .get<IResponse<Array<any>>>(RbacApplication.res_getRoleResTree, {
        roleId: roleId || 0,
      })
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.resConfig.ngModel = [];
          resp.result.forEach(tnm => {
            this.resConfig.ngModel.push(new NzTreeNode(tnm));
          });
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  dictList: any;
  dictfindByPValue() {
    this.http
      .get<IResponse<Array<any>>>(RbacApplication.dict_findByPValue, {
        value: 'JUESEFENLEI',
      })
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.dictList = resp.result;
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  submit(value: any) {
    if (this.params.roleId) {
      value.roleId = this.params.roleId;
    }
    this.st.reload(value);
  }
}

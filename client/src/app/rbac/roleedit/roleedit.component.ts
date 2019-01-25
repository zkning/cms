import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef, NzMessageService, NzTreeNode } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { SFSchema, SFUISchema } from '@delon/form';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { IResponse } from '@core/net/model/IResponse';
import { RbacApplication } from '../constants/rbacapplication.model';
import { RbacRoleInfo } from '../model/role.model';

@Component({
  selector: 'app-roleedit',
  templateUrl: './roleedit.component.html',
})
export class RbacRoleeditComponent implements OnInit {
  record: any = {};
  form: FormGroup;

  roleInfo = new RbacRoleInfo();
  constructor(
    private modal: NzModalRef,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      roleName: [this.roleInfo.roleName, [Validators.required]],
      roleCode: [this.roleInfo.roleCode, [Validators.required]],
      groupId: [this.roleInfo.groupId],
      roleType: [this.roleInfo.roleType, [Validators.required]],
      remark: [this.roleInfo.remark],
      version: [this.roleInfo.version],
      id: [this.roleInfo.id],
    });

    if (this.record && this.record.id) {
      this.http
        .get<IResponse<RbacRoleInfo>>(RbacApplication.role_fetch, {
          id: this.record.id,
        })
        .subscribe(resp => {
          if (IResponse.statuscode.SUCCESS === resp.code) {
            this.roleInfo = resp.result;
            this.form.setValue(this.roleInfo);
          } else {
            this.msgSrv.error(resp.message);
          }
        });
    }
    this.getFormGroupTree();
    this.dictfindByPValue();
  }
  formNodes = [];
  getFormGroupTree() {
    this.http
      .get<IResponse<Array<any>>>(RbacApplication.groupTree)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.formNodes = [];
          resp.result.forEach(tnm => {
            this.formNodes.push(new NzTreeNode(tnm));
          });
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  submit() {
    this.http
      .post<IResponse<any>>(RbacApplication.role_edit, this.form.value)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.msgSrv.success(resp.message);
          this.modal.close(true);
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  close() {
    this.modal.destroy();
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
}

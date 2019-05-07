import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef, NzMessageService, NzTreeNode } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { SFSchema, SFUISchema } from '@delon/form';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { RbacUserInfo } from '../model/user.model';
import { RbacApplication } from '../constants/rbacapplication.model';
import { IResponse } from '@core/net/model/IResponse';

@Component({
  selector: 'app-useredit',
  templateUrl: './useredit.component.html',
})
export class RbacUsereditComponent implements OnInit {
  record: any = {};
  form: FormGroup;
  listOfRole = [];
  userInfo = new RbacUserInfo();
  constructor(
    private modal: NzModalRef,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      userName: [this.userInfo.userName, [Validators.required]],
      name: [this.userInfo.name, [Validators.required]],
      groupId: [this.userInfo.groupId, [Validators.required]],
      mobile: [this.userInfo.mobile],
      email: [this.userInfo.email],
      version: [this.userInfo.version],
      id: [this.userInfo.id],
      roles: [this.userInfo.roles],
    });
    this.getFormGroupTree();
    this.getRoleList();
  }

  fetch() {
    if (this.record.id) {
      this.http
        .get<IResponse<RbacUserInfo>>(RbacApplication.findById, {
          id: this.record.id,
        })
        .subscribe(resp => {
          if (IResponse.statuscode.SUCCESS === resp.code) {
            this.userInfo.userName = resp.result.userName;
            this.userInfo.name = resp.result.name;
            this.userInfo.groupId = resp.result.groupId;
            this.userInfo.mobile = resp.result.mobile;
            this.userInfo.email = resp.result.email;
            this.userInfo.version = resp.result.version;
            this.userInfo.id = resp.result.id;
            this.userInfo.roles = resp.result.roles;
            this.form.setValue(this.userInfo);
          } else {
            this.msgSrv.error(resp.message);
          }
        });
    }
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
          this.fetch();
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  submit() {
    this.http
      .post<IResponse<any>>(RbacApplication.userSaveOrUpdate, this.form.value)
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

  getRoleList(params?) {
    this.http
      .get<IResponse<Array<any>>>(RbacApplication.role_list, params)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.listOfRole = resp.result;
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }
}

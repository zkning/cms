import { Component, OnInit, ViewChild } from '@angular/core';
import {
  NzModalRef,
  NzMessageService,
  NzTreeNode,
  NzModalService,
} from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { SFSchema, SFUISchema } from '@delon/form';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { IResponse } from '@core/net/model/IResponse';
import { RbacApplication } from '../constants/rbacapplication.model';
import { RbacRoleInfo } from '../model/role.model';
import { GroupInfo } from '../model/groupForm.model';
import { DistConstant } from '../constants/dist.constant';

@Component({
  selector: 'app-roleedit',
  templateUrl: './groupedit.component.html',
})
export class RbacGroupeditComponent implements OnInit {
  record: any = {};
  form: FormGroup;
  isValidList = DistConstant.valid;
  groupInfo = new GroupInfo();

  // 表单树控件
  formGroupTreeNgModel;
  constructor(
    private modal: NzModalRef,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
    private fb: FormBuilder,
    public modalService: NzModalService,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      groupName: [this.groupInfo.groupName, [Validators.required]],
      pid: [this.groupInfo.pid, [Validators.required]],
      groupType: [this.groupInfo.groupType, [Validators.required]],
      remark: [this.groupInfo.remark],
      extra: [this.groupInfo.extra],
      isValid: [this.groupInfo.isValid],
      version: [this.groupInfo.version],
      id: [this.groupInfo.id],
    });

    this.http
      .get<IResponse<Array<any>>>(RbacApplication.groupTree)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.formGroupTreeNgModel = [];
          resp.result.forEach(tnm => {
            this.formGroupTreeNgModel.push(new NzTreeNode(tnm));

            if (this.record && this.record.id) {
              this.http
                .get<IResponse<GroupInfo>>(RbacApplication.findGroupById, {
                  id: this.record.id,
                })
                .subscribe(response => {
                  if (IResponse.statuscode.SUCCESS === response.code) {
                    this.groupInfo = response.result;
                    this.form.setValue(this.groupInfo);
                  } else {
                    this.msgSrv.error(response.message);
                  }
                });
            }
          });
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  submit() {
    // for (const i in this.form.controls) {
    //   this.form.controls[i].markAsDirty();
    //   this.form.controls[i].updateValueAndValidity();
    // }
    // if (this.form.invalid) {
    //   return;
    // }
    this.http
      .post<IResponse<any>>(RbacApplication.saveOrUpdate, this.form.value)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.msgSrv.success(resp.message);
          this.modal.close(true);
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  delete() {
    this.modalService.confirm({
      nzTitle: '<i>确定要删除吗?</i>',
      nzContent: '<b>连同子节点都会同时删除</b>',
      nzOnOk: () => {
        this.http
          .get<IResponse<any>>(RbacApplication.delete, this.form.value)
          .subscribe(resp => {
            if (IResponse.statuscode.SUCCESS === resp.code) {
              this.msgSrv.success(resp.message);
              this.modal.close(true);
              return;
            }
            this.msgSrv.info(resp.message);
          });
      },
    });
  }

  close() {
    this.modal.destroy();
  }
}

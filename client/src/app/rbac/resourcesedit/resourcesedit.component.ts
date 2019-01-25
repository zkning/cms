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
import { RbacResInfo } from '../model/resource.model';

@Component({
  selector: 'app-resourcesedit',
  templateUrl: './resourcesedit.component.html',
})
export class RbacResourceseditComponent implements OnInit {
  record: any = {};
  form: FormGroup;

  resInfo = new RbacResInfo();
  constructor(
    private modal: NzModalRef,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
    private fb: FormBuilder,
    public modalService: NzModalService,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      text: [this.resInfo.text, [Validators.required]],
      link: [this.resInfo.link],
      externalLink: [this.resInfo.externalLink],
      icon: [this.resInfo.icon],
      pid: [this.resInfo.pid],
      resourceType: [this.resInfo.resourceType, [Validators.required]],
      extra: [this.resInfo.extra],
      version: [this.resInfo.version],
      id: [this.resInfo.id],
      code: [this.resInfo.code],
    });

    if (this.record.id) {
      this.http
        .get<IResponse<RbacResInfo>>(RbacApplication.res_fetch, {
          id: this.record.id,
        })
        .subscribe(resp => {
          if (IResponse.statuscode.SUCCESS === resp.code) {
            this.form.setValue(resp.result);
          } else {
            this.msgSrv.error(resp.message);
          }
        });
    }
    this.getFormGroupTree();
  }

  // 资源树
  formNodes = [];
  getFormGroupTree() {
    this.http
      .get<IResponse<Array<any>>>(RbacApplication.res_getRoleResTree, {
        pid: 0,
      })
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
      .post<IResponse<any>>(RbacApplication.res_edit, this.form.value)
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

  deleteRes() {
    this.modalService.confirm({
      nzTitle: '<i>确定要删除吗?</i>',
      nzContent: '<b>下级资源也会同时删除</b>',
      nzOnOk: () => {
        this.http
          .get<IResponse<any>>(RbacApplication.res_delete, {
            id: this.record.id,
          })
          .subscribe(resp => {
            if (IResponse.statuscode.SUCCESS === resp.code) {
              this.msgSrv.success(resp.message);
              this.modal.close(true);
            } else {
              this.msgSrv.error(resp.message);
            }
          });
      },
    });
  }
}

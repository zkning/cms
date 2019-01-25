import { Component, OnInit } from '@angular/core';
import { NzModalRef, NzMessageService, NzTreeNode } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { RbacApplication } from '../../../rbac/constants/rbacapplication.model';
import { IResponse } from '@core/net/model/IResponse';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { SmServerConstant } from '../../constants/smserver.constants';

@Component({
  selector: 'app-sm-dataview-tores',
  templateUrl: './tores.component.html',
})
export class SmDataviewToresComponent implements OnInit {
  record: any = {};

  // 资源树
  formNodes = [];
  form: FormGroup;

  constructor(
    private modal: NzModalRef,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      pid: ['', [Validators.required]],
    });
    this.getFormGroupTree();
  }

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

  close() {
    this.modal.destroy();
  }

  submit() {
    // this.modalService.confirm({
    //         nzTitle: '<i>确定要添加吗?</i>',
    //         nzContent: '<b></b>',
    //         nzOnOk: () => {

    //         },
    //       });
    this.http
      .post<IResponse<any>>(SmServerConstant.toRes, {
        dataViewId: this.record.id,
        resPid: this.form.value.pid,
      })
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.modal.destroy();
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }
}

import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef, NzMessageService, NzTreeNode } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { SFSchema, SFUISchema } from '@delon/form';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { RbacApplication } from '../../constants/rbacapplication.model';
import { IResponse } from '@core/net/model/IResponse';
import { DictFetchModel } from '../../model/dictFetch.model';

@Component({
  selector: 'app-dict-edit',
  templateUrl: './edit.component.html',
})
export class RbacDictEditComponent implements OnInit {
  record: any = {};
  form: FormGroup;
  formNodes;
  dictFetchModel = new DictFetchModel();
  constructor(
    private modal: NzModalRef,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      text: [this.dictFetchModel.text, [Validators.required]],
      value: [this.dictFetchModel.value, [Validators.required]],
      pid: [this.dictFetchModel.pid, [Validators.required]],
      sort: [this.dictFetchModel.sort],
      remark: [this.dictFetchModel.remark],
      version: [this.dictFetchModel.version],
      id: [this.dictFetchModel.id],
    });
    this.getFormGroupTree();

    if (this.record.id) {
      this.http
        .get<IResponse<DictFetchModel>>(RbacApplication.dict_fetch, {
          id: this.record.id,
        })
        .subscribe(resp => {
          if (IResponse.statuscode.SUCCESS === resp.code) {
            this.dictFetchModel.pid = resp.result.pid;
            this.dictFetchModel.value = resp.result.value;
            this.dictFetchModel.text = resp.result.text;
            this.dictFetchModel.version = resp.result.version;
            this.dictFetchModel.remark = resp.result.remark;
            this.dictFetchModel.id = resp.result.id;
            this.dictFetchModel.sort = resp.result.sort;
            this.form.setValue(this.dictFetchModel);
          } else {
            this.msgSrv.error(resp.message);
          }
        });
    }
  }

  getFormGroupTree() {
    this.http
      .get<IResponse<Array<any>>>(RbacApplication.dict_tree, { pid: 0 })
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
      .post<IResponse<any>>(RbacApplication.dict_edit, this.form.value)
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
}

import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef, NzMessageService, NzTreeNode } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { SFSchema, SFUISchema } from '@delon/form';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { IResponse } from '@core/net/model/IResponse';
import { DataSourceFetchModel } from '../../model/datasourceFetch.model';
import { SmServerConstant } from '../../constants/smserver.constants';

@Component({
  selector: 'app-datasource-edit',
  templateUrl: './edit.component.html',
})
export class RbacDataSourceEditComponent implements OnInit {
  record: any = {};
  form: FormGroup;
  fetchModel = new DataSourceFetchModel();
  constructor(
    private modal: NzModalRef,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      id: [this.fetchModel.id],
      driverClassName: [this.fetchModel.driverClassName, [Validators.required]],
      name: [this.fetchModel.name, [Validators.required]],
      url: [this.fetchModel.url, [Validators.required]],
      dbPassword: [this.fetchModel.dbPassword, [Validators.required]],
      dbUsername: [this.fetchModel.dbUsername, [Validators.required]],
      remark: [this.fetchModel.remark],
      version: [this.fetchModel.version],
    });

    if (this.record.id) {
      this.http
        .get<IResponse<DataSourceFetchModel>>(SmServerConstant.ds_fetch, {
          id: this.record.id,
        })
        .subscribe(resp => {
          if (IResponse.statuscode.SUCCESS === resp.code) {
            this.fetchModel.dbPassword = resp.result.dbPassword;
            this.fetchModel.driverClassName = resp.result.driverClassName;
            this.fetchModel.name = resp.result.name;
            this.fetchModel.url = resp.result.url;
            this.fetchModel.version = resp.result.version;
            this.fetchModel.remark = resp.result.remark;
            this.fetchModel.id = resp.result.id;
            this.fetchModel.dbUsername = resp.result.dbUsername;
            this.form.setValue(this.fetchModel);
          } else {
            this.msgSrv.error(resp.message);
          }
        });
    }
  }

  submit() {
    this.http
      .post<IResponse<any>>(SmServerConstant.ds_edit, this.form.value)
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

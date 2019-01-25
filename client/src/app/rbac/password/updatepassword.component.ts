import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef, NzMessageService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { SFSchema, SFUISchema } from '@delon/form';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { ReuseTabService } from '@delon/abc';
import { PassWord } from '../model/password.model';
import { IResponse } from '@core/net/model/IResponse';
import { RbacApplication } from '../constants/rbacapplication.model';
import { Md5 } from 'ts-md5/dist/md5';
@Component({
  selector: 'app-updatepassword',
  templateUrl: './updatepassword.component.html',
})
export class UpdatePasswordComponent implements OnInit {
  form: FormGroup;
  submitting = false;
  password: PassWord;
  constructor(
    private fb: FormBuilder,
    private msg: NzMessageService,
    private titleService: Title,
    private reuseTabService: ReuseTabService,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
  ) {}

  ngOnInit(): void {
    this.reuseTabService.title = '修改密码';
    this.titleService.setTitle('修改密码');
    this.password = new PassWord();
    this.form = this.fb.group({
      password: [this.password.password, [Validators.required]],
      newPassword: [this.password.newPassword, [Validators.required]],
      newPassword2: [this.password.newPassword2, [Validators.required]],
    });
  }

  submit() {
    for (const i in this.form.controls) {
      this.form.controls[i].markAsDirty();
      this.form.controls[i].updateValueAndValidity();
    }
    if (this.form.invalid) return;
    this.submitting = true;

    this.form.value.password = Md5.hashStr(this.form.value.password).toString();
    this.form.value.newPassword = Md5.hashStr(
      this.form.value.newPassword,
    ).toString();
    this.form.value.newPassword2 = Md5.hashStr(
      this.form.value.newPassword2,
    ).toString();
    this.http
      .post<IResponse<any>>(RbacApplication.user_updatePwd, this.form.value)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.msgSrv.success(resp.message);
          this.submitting = false;
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }
}

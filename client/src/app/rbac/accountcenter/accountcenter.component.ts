import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef, NzMessageService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { SFSchema, SFUISchema } from '@delon/form';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { RbacUserInfo } from '../model/user.model';
import { Title } from '@angular/platform-browser';
import { ReuseTabService } from '@delon/abc';
import { IResponse } from '@core/net/model/IResponse';
import { RbacApplication } from '../constants/rbacapplication.model';

@Component({
  selector: 'app-accountcenter',
  templateUrl: './accountcenter.component.html',
  styleUrls: ['./accountcenter.component.less'],
})
export class AccountcenterComponent implements OnInit {
  form: FormGroup;
  submitting = false;
  user: RbacUserInfo;
  constructor(
    private fb: FormBuilder,
    private msg: NzMessageService,
    private titleService: Title,
    private reuseTabService: ReuseTabService,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
  ) {}

  ngOnInit(): void {
    this.titleService.setTitle('用户中心');
    this.reuseTabService.title = '用户中心';
    this.user = new RbacUserInfo();
    this.http.get<IResponse<any>>(RbacApplication.user_me).subscribe(resp => {
      if (IResponse.statuscode.SUCCESS === resp.code) {
        this.user = resp.result;
        this.form.setValue(this.user);
      } else {
        this.msgSrv.error(resp.message);
      }
    });

    this.form = this.fb.group({
      name: [this.user.name, [Validators.required]],
      userName: [this.user.userName, [Validators.required]],
      mobile: [this.user.mobile, [Validators.required]],
      email: [this.user.email, [Validators.required, Validators.email]],
      avatar: [this.user.avatar],
      groupId: [this.user.groupId],
      groupName: [this.user.groupName],
      id: [this.user.id],
      version: [this.user.version, [Validators.min(1), Validators.max(100)]],
      createTime: [this.user.createTime],
    });
  }

  submit() {
    for (const i in this.form.controls) {
      this.form.controls[i].markAsDirty();
      this.form.controls[i].updateValueAndValidity();
    }
    if (this.form.invalid) return;
    this.submitting = true;

    this.http
      .post<IResponse<any>>(RbacApplication.user_updateInfo, this.form.value)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.msgSrv.success(resp.message);
          this.submitting = false;
        } else {
          this.submitting = false;
          this.msgSrv.error(resp.message);
        }
      });
  }
}

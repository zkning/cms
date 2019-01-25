import { Router } from '@angular/router';
import { Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { SettingsService, _HttpClient } from '@delon/theme';
import { IResponse } from '@core/net/model/IResponse';
import { Application } from '@env/application';
import { NzMessageService } from 'ng-zorro-antd';
import { RbacApplication } from '../../../rbac/constants/rbacapplication.model';
import { Md5 } from 'ts-md5/dist/md5';
@Component({
  selector: 'passport-lock',
  templateUrl: './lock.component.html',
})
export class UserLockComponent {
  unlock = false;
  f: FormGroup;

  constructor(
    public settings: SettingsService,
    fb: FormBuilder,
    private router: Router,
    public http: _HttpClient,
    public msgSrv: NzMessageService,
  ) {
    this.f = fb.group({
      password: [null, Validators.required],
    });
  }

  submit() {
    // tslint:disable-next-line:forin
    for (const i in this.f.controls) {
      this.f.controls[i].markAsDirty();
      this.f.controls[i].updateValueAndValidity();
    }
    if (this.f.valid) {
      // console.log('Valid!');
      // console.log(this.f.value);
      this.f.value.password = Md5.hashStr(this.f.value.password).toString();
      this.http
        .post<IResponse<any>>(RbacApplication.unlock, this.f.value)
        .subscribe(resp => {
          if (IResponse.statuscode.SUCCESS === resp.code) {
            this.unlock = true;
            this.router.navigate(['home']);
          } else {
            this.msgSrv.error('密码错误，解锁失败！');
          }
        });
    }
  }
}

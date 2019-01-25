import { SettingsService, _HttpClient } from '@delon/theme';
import { Component, OnDestroy, Inject, Optional } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import {
  SocialService,
  SocialOpenType,
  TokenService,
  DA_SERVICE_TOKEN,
} from '@delon/auth';
import { ReuseTabService } from '@delon/abc';
import { environment } from '@env/environment';
import { StartupService } from '@core/startup/startup.service';
import { CredentialsModel } from './model/CredentialsModel';
import { Application } from '@env/application';
import { IResponse } from '@core/net/model/IResponse';
import { RbacApplication } from '../../../rbac/constants/rbacapplication.model';
import { OauthTokenModel } from './model/oauthTokenModel';
import { Md5 } from 'ts-md5/dist/md5';
@Component({
  selector: 'passport-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.less'],
  providers: [SocialService],
})
export class UserLoginComponent implements OnDestroy {
  form: FormGroup;
  error = '';
  type = 0;
  loading = false;

  constructor(
    fb: FormBuilder,
    private router: Router,
    public msg: NzMessageService,
    private modalSrv: NzModalService,
    private settingsService: SettingsService,
    private socialService: SocialService,
    @Optional()
    @Inject(ReuseTabService)
    private reuseTabService: ReuseTabService,
    @Inject(DA_SERVICE_TOKEN) private tokenService: TokenService,
    private startupService: StartupService,
    private http: _HttpClient,
  ) {
    this.form = fb.group({
      userName: [null, [Validators.required, Validators.minLength(5)]],
      password: [null, Validators.required],
      mobile: [null, [Validators.required, Validators.pattern(/^1\d{10}$/)]],
      captcha: [null, [Validators.required]],
      remember: [true],
    });
    modalSrv.closeAll();
  }

  // region: fields

  get userName() {
    return this.form.controls.userName;
  }
  get password() {
    return this.form.controls.password;
  }
  get mobile() {
    return this.form.controls.mobile;
  }
  get captcha() {
    return this.form.controls.captcha;
  }

  // endregion

  switch(ret: any) {
    this.type = ret.index;
  }

  // region: get captcha

  count = 0;
  interval$: any;

  getCaptcha() {
    this.count = 59;
    this.interval$ = setInterval(() => {
      this.count -= 1;
      if (this.count <= 0) clearInterval(this.interval$);
    }, 1000);
  }

  // endregion

  submit() {
    this.error = '';
    if (this.type === 0) {
      this.userName.markAsDirty();
      this.userName.updateValueAndValidity();
      this.password.markAsDirty();
      this.password.updateValueAndValidity();
      if (this.userName.invalid || this.password.invalid) return;
    } else {
      this.mobile.markAsDirty();
      this.mobile.updateValueAndValidity();
      this.captcha.markAsDirty();
      this.captcha.updateValueAndValidity();
      if (this.mobile.invalid || this.captcha.invalid) return;
    }

    const headers = new Headers({ 'Content-Type': 'application/json' });
    const data = new FormData();
    data.append('username', this.userName.value);
    data.append('password', Md5.hashStr(this.password.value).toString());
    data.append('grant_type', 'password');
    data.append('scope', 'read');
    data.append('client_id', 'client_x');
    data.append('client_secret', '123456');
    this.http
      .post<OauthTokenModel>(
        RbacApplication.oauth2Token,
        data,
        // { username: this.userName.value, password: this.password.value },
        // { username: this.userName.value, password: this.password.value ,
        //   grant_type: 'password', scope: 'read', client_id: 'client_x', client_secret: '123456'},
        // { headers: headers },
      )
      .subscribe(resp => {
        // if (IResponse.statuscode.SUCCESS === resp.code) {
        if (resp.error) {
          this.error = resp.error_description;
          return;
        }
        // const credentialsModel = resp.result;

        // 清空路由复用信息
        this.reuseTabService.clear();

        // 设置Token信息
        this.tokenService.set({
          token: resp.access_token,
          // name: resp.user.name,
          // email: resp.user.email,
          // id: resp.user.id,
          time: +new Date(),
        });

        // 重新获取 StartupService 内容，若其包括 User 有关的信息的话
        this.startupService.load().then(() => this.router.navigate(['/']));
        // } else this.error = `账户或密码错误`;
        return;
      });
  }

  // region: social
  open(type: string, openType: SocialOpenType = 'href') {
    let url = ``;
    let callback = ``;
    if (environment.production)
      callback = 'https://cipchk.github.io/ng-alain/callback/' + type;
    else callback = 'http://localhost:4200/callback/' + type;
    switch (type) {
      case 'auth0':
        url = `//cipchk.auth0.com/login?client=8gcNydIDzGBYxzqV0Vm1CX_RXH-wsWo5&redirect_uri=${decodeURIComponent(
          callback,
        )}`;
        break;
      case 'github':
        url = `//github.com/login/oauth/authorize?client_id=9d6baae4b04a23fcafa2&response_type=code&redirect_uri=${decodeURIComponent(
          callback,
        )}`;
        break;
      case 'weibo':
        url = `https://api.weibo.com/oauth2/authorize?client_id=1239507802&response_type=code&redirect_uri=${decodeURIComponent(
          callback,
        )}`;
        break;
    }
    if (openType === 'window') {
      this.socialService
        .login(url, '/', {
          type: 'window',
        })
        .subscribe(res => {
          if (res) {
            this.settingsService.setUser(res);
            this.router.navigateByUrl('/');
          }
        });
    } else {
      this.socialService.login(url, '/', {
        type: 'href',
      });
    }
  }

  // endregion

  ngOnDestroy(): void {
    if (this.interval$) clearInterval(this.interval$);
  }
}

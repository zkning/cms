import { Component, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { SettingsService } from '@delon/theme';
import { DA_SERVICE_TOKEN, ITokenService } from '@delon/auth';
import { NzMessageService } from 'ng-zorro-antd';

@Component({
  selector: 'header-user',
  template: `
  <nz-dropdown nzPlacement="bottomRight">
    <div class="item d-flex align-items-center px-sm" nz-dropdown>
      <nz-avatar [nzSrc]="settings.user.avatar" nzSize="small" class="mr-sm"></nz-avatar>
      {{settings.user.name}}
    </div>
    <div nz-menu class="width-sm">
      <li nz-menu-divider></li>
      <div nz-menu-item (click)="usermgr()"><i class="anticon anticon-user mr-sm"></i>个人中心</div>
      <div nz-menu-item (click)="updatePassword()"><i class="anticon anticon-user mr-sm"></i>修改密码</div>
      <div nz-menu-item (click)="logout()"><i class="anticon anticon-setting mr-sm"></i>退出登录</div>
    </div>
  </nz-dropdown>
  `,
})
// zkning
// <div nz-menu-item [nzDisabled]="true"><i class="anticon anticon-user mr-sm"></i>个人中心</div>
// <div nz-menu-item [nzDisabled]="true"><i class="anticon anticon-setting mr-sm"></i>设置</div>
export class HeaderUserComponent {
  constructor(
    public settings: SettingsService,
    private router: Router,
    private msg: NzMessageService,
    @Inject(DA_SERVICE_TOKEN) private tokenService: ITokenService,
  ) {}

  logout() {
    this.tokenService.clear();
    this.router.navigateByUrl(this.tokenService.login_url);
  }

  usermgr() {
    this.router.navigate(['rbac', 'accountcenter']);
    // this.msg.warning('功能未开放,请联系管理员');
  }

  updatePassword() {
    this.router.navigate(['rbac', 'updatePassword']);
    // this.msg.warning('功能未开放,请联系管理员');
  }
}

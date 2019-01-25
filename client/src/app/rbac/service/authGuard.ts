import {
  CanActivate,
  Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';
import { Injectable } from '@angular/core';
import { TokenService } from '@delon/auth';
import { ACLService, ACLCanType } from '@delon/acl';
import { NzMessageService } from 'ng-zorro-antd';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(
    private aclService: ACLService,
    private router: Router,
    private msgSrvice: NzMessageService,
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): boolean {
    const url = state.url;
    const canActivate = this.aclService.canAbility(url);
    if (!canActivate) {
      this.msgSrvice.warning('您没有权限操作！');
    }
    return canActivate;
  }
}

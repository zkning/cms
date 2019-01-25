import { CanDeactivate } from '@angular/router';
import { UserLockComponent } from 'app/routes/passport/lock/lock.component';
import { Injectable } from '@angular/core';

// 路由守卫
@Injectable()
export class UnLockGuard implements CanDeactivate<UserLockComponent> {
  canDeactivate(component: UserLockComponent) {
    return component.unlock;
  }
}

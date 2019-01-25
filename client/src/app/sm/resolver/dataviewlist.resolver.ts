import { Injectable } from '@angular/core';
import {
  ActivatedRoute,
  Resolve,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';
import { DataViewEditModel } from '../model/dataviewedit.model';
import { _HttpClient } from '@delon/theme';
import { Observable } from 'rxjs';
import { IResponse } from '@core/net/model/IResponse';
import { SmServerConstant } from '../constants/smserver.constants';
import { NzMessageService } from 'ng-zorro-antd';

@Injectable()
export class DataViewListResolver implements Resolve<DataViewEditModel> {
  constructor(
    private httpService: _HttpClient,
    private activatedRoute: ActivatedRoute,
  ) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<any> | Promise<any> | any {
    // 获取当前选中视图
    return this.httpService.post<IResponse<DataViewEditModel>>(
      SmServerConstant.fetch + '/' + route.params.dataViewId,
      {},
    );
  }
}

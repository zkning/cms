import { NzMessageService } from 'ng-zorro-antd';
import { Component, OnInit } from '@angular/core';
import { _HttpClient } from '@delon/theme';
import { IResponse } from '@core/net/model/IResponse';
import { Application } from '@env/application';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
})
export class HomeComponent implements OnInit {
  constructor(private http: _HttpClient, public msg: NzMessageService) {}

  webSite: any[] = [];
  salesData: any[] = [];
  offlineChartData: any[] = [];

  ngOnInit() {
    // this.http.get('/chart').subscribe((res: any) => {
    //   this.webSite = res.visitData.slice(0, 10);
    //   this.salesData = res.salesData;
    //   this.offlineChartData = res.offlineChartData;
    // });
  }
}

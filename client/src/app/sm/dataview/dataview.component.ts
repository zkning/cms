import { Component, OnInit, ViewChild } from '@angular/core';
import { _HttpClient, ModalHelper } from '@delon/theme';
import { SimpleTableColumn, SimpleTableComponent } from '@delon/abc';
import { SFSchema } from '@delon/form';
import { NzMessageService, NzModalService } from 'ng-zorro-antd';
import { Router } from '@angular/router';
import { ACLService } from '@delon/acl';
import { SmDataviewEditComponent } from './edit/edit.component';
import { SmServerConstant } from '../constants/smserver.constants';
import { TreeConfig } from '../../rbac/model/treeconfig.model';
import { IResponse } from '@core/net/model/IResponse';
import { DataViewEditModel } from '../model/dataviewedit.model';
import { SmDataviewToresComponent } from './res/tores.component';

@Component({
  selector: 'app-sm-dataview',
  templateUrl: './dataview.component.html',
})
export class SmDataviewComponent implements OnInit {
  params: any = {};
  url = SmServerConstant.list;

  constructor(
    private http: _HttpClient,
    public msg: NzMessageService,
    private modal: ModalHelper,
    private router: Router,
    private aclService: ACLService,
    public modalService: NzModalService,
  ) {}

  ngOnInit() {}

  searchSchema: SFSchema = {
    properties: {
      dataViewName: {
        type: 'string',
        title: '视图名称',
      },
      sqlId: {
        type: 'string',
        title: 'sqlId',
      },
    },
  };

  @ViewChild('st') st: SimpleTableComponent;
  columns: SimpleTableColumn[] = [
    { title: '序号', index: 'no' },
    { title: 'ID', index: 'id' },
    { title: '视图名称', index: 'dataViewName' },
    { title: 'sqlId', index: 'sqlId' },
    { title: '创建时间', index: 'createTime' },
    { title: '创建人', index: 'createUser' },
    {
      title: '操作',
      buttons: [
        {
          text: '编辑',
          click: (item: any) => {
            // 获取当前选中视图
            this.http
              .post<IResponse<DataViewEditModel>>(
                SmServerConstant.fetch + '/' + item.id,
                {},
              )
              .subscribe(resp => {
                if (IResponse.statuscode.SUCCESS === resp.code) {
                  this.modal
                    .static(
                      SmDataviewEditComponent,
                      { formData: resp.result },
                      1600,
                    )
                    .subscribe(result => {
                      if (result) {
                        this.st.load();
                      }
                    });
                } else {
                  this.msg.error(resp.message);
                }
              });
          },
        },
        {
          text: '预览',
          click: (item: any) => {
            // <a target="_blank" [routerLink]="['/article-detail', article.id]">{{article.title}}</a>
            this.router.navigateByUrl('/sm/dataviewlist/' + item.id);
          },
        },
        {
          text: '删除',
          click: (item: any) => {
            this.modalService.confirm({
              nzTitle: '<i>确定要删除吗?</i>',
              nzContent: '<b>当前视图将被删除</b>',
              nzOnOk: () => {
                this.http
                  .get<IResponse<any>>(SmServerConstant.delete, { id: item.id })
                  .subscribe(resp => {
                    if (IResponse.statuscode.SUCCESS === resp.code) {
                      this.st.load();
                    } else {
                      this.msg.error(resp.message);
                    }
                  });
              },
            });
          },
        },
        {
          text: '添至菜单',
          click: (item: any) => {
            this.modal
              .static(SmDataviewToresComponent, { record: item }, 600)
              .subscribe(() => {});
          },
        },
      ],
    },
  ];

  add() {
    this.modal
      .static(SmDataviewEditComponent, { record: {} }, 1600)
      .subscribe(() => {
        this.st.load();
      });
  }

  preDataChange(data) {
    for (let index = 0; index < data.length; index++) {
      data[index]['no'] = index + 1;
    }
    return data;
  }
}

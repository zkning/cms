import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef, NzMessageService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { SFSchema, SFUISchema } from '@delon/form';
import { IResponse } from '@core/net/model/IResponse';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { SqlDefineFetchModel } from '../../model/sqldefine.model';
import { SmServerConstant } from '../../constants/smserver.constants';
import { promise } from 'protractor';
import { SchemaTableModel } from '../../model/schematable.model';
import * as _ from 'lodash';
import { DictConstant } from '../../constants/dict.constant';
import { GoldbalConstant } from '../../constants/goldbal.constant';
@Component({
  selector: 'app-sm-sqldefine-edit',
  templateUrl: './edit.component.html',
  styles: [
    `
      table,
      table tr th,
      table tr td {
        border: 1px solid #e8e8e8;
      }
      table tr th {
        text-align: center;
      }
      table {
        width: 100%;
      }

      .tr-st td,
      .tr-st th {
        text-align: center;
      }

      .tr-center td {
        text-align: center;
      }

      select {
        font-family: 'Monospaced Number', 'Chinese Quote', -apple-system,
          BlinkMacSystemFont, 'Segoe UI', Roboto, 'PingFang SC',
          'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica,
          Arial, sans-serif;
        box-sizing: border-box;
        position: relative;
        padding: 4px 11px;
        width: 100%;
        font-size: 14px;
        line-height: 1.5;
        border: 1px solid #d9d9d9;
        border-radius: 4px;
        transition: all 0.3s;
      }
    `,
  ],
})
export class SmSqldefineEditComponent implements OnInit {
  record: any = {};
  form: FormGroup;
  formNodes;
  sqlDefineFetchModel = new SqlDefineFetchModel();
  constructor(
    private modal: NzModalRef,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
    private fb: FormBuilder,
  ) {}

  defineTypeList = DictConstant.defineType();
  datasourceList = [];
  stateList = [];
  cacheList = [];
  schemaTableModelList = new Array<SchemaTableModel>();

  // 操纵类型：QUERY
  manipulationQuery = GoldbalConstant.manipulate.QUERY;

  sqlReadonly = false;
  ngOnInit(): void {
    this.form = this.fb.group({
      id: [this.sqlDefineFetchModel.id],
      sqlName: [this.sqlDefineFetchModel.sqlName, [Validators.required]],
      selectSql: [this.sqlDefineFetchModel.selectSql, [Validators.required]],
      sqlExtra: [this.sqlDefineFetchModel.sqlExtra],
      remark: [this.sqlDefineFetchModel.remark],
      // datasource: [{value: this.sqlDefineFetchModel.datasource, disabled: this.record.id}, [Validators.required]],
      datasource: [this.sqlDefineFetchModel.datasource, [Validators.required]],
      version: [this.sqlDefineFetchModel.version],
      pri: [this.sqlDefineFetchModel.pri],
      tableName: [this.sqlDefineFetchModel.tableName],
      state: [this.sqlDefineFetchModel.state],
      manipulate: [this.sqlDefineFetchModel.manipulate, [Validators.required]],
      isCache: [this.sqlDefineFetchModel.isCache, [Validators.required]],
    });

    if (this.record.id) {
      this.http
        .get<IResponse<SqlDefineFetchModel>>(SmServerConstant.sqldefine_fetch, {
          id: this.record.id,
        })
        .subscribe(resp => {
          if (IResponse.statuscode.SUCCESS === resp.code) {
            this.form.setValue(resp.result);
          } else {
            this.msgSrv.error(resp.message);
          }
        });
    }

    const instance = this;
    this.getItems('sqldefine-cache').then(function(value) {
      instance.cacheList = value;
    });
    this.getDataSource();
  }

  isManipulationValue() {
    return (
      this.form.controls.manipulate.value === GoldbalConstant.manipulate.QUERY
    );
  }

  getDataSource() {
    this.http
      .get<IResponse<any>>(SmServerConstant.ds_findAll, {
        tablename: this.form.value.tableName,
        datasource: this.form.value.datasource,
      })
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.datasourceList = resp.result;
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  // 模糊搜索表信息
  queryTable() {
    if (!this.form.value.tableName || !this.form.value.datasource) {
      return;
    }
    this.http
      .post<IResponse<any>>(SmServerConstant.sqlId_getSchemaTable, {
        tablename: this.form.value.tableName,
        datasource: this.form.value.datasource,
      })
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.schemaTableModelList = resp.result;
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  rowSelected(schemaTable) {
    if (!this.form.value.datasource) {
      return;
    }
    this.http
      .post<IResponse<any>>(SmServerConstant.sqlId_preview, {
        tablename: schemaTable.tableName,
        datasource: this.form.value.datasource,
      })
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          const pri = this.form.controls['pri'];
          const selectSql = this.form.controls['selectSql'];
          const remark = this.form.controls['remark'];
          const sqlName = this.form.controls['sqlName'];
          const tableName = this.form.controls['tableName'];

          pri.setValue(resp.result.pri);
          selectSql.setValue(resp.result.selectSql);
          remark.setValue(schemaTable.tableComment);
          if (_.isEmpty(this.form.value.sqlName)) {
            sqlName.setValue(schemaTable.tableComment);
          }
          tableName.setValue(schemaTable.tableName);
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  getItems(value: string): any {
    return new Promise((resolve, reject) => {
      this.http
        .get<IResponse<Array<any>>>(SmServerConstant.findByPValue, {
          value: value,
        })
        .subscribe(resp => {
          if (IResponse.statuscode.SUCCESS === resp.code) {
            resolve(resp.result);
          } else {
            this.msgSrv.error(resp.message);
            reject(null);
          }
        });
    });
  }

  submit() {
    this.http
      .post<IResponse<any>>(SmServerConstant.sqldefine_edit, this.form.value)
      .subscribe(resp => {
        if (IResponse.statuscode.SUCCESS === resp.code) {
          this.msgSrv.success(resp.message);
          this.modal.close(true);
        } else {
          this.msgSrv.error(resp.message);
        }
      });
  }

  close() {
    this.modal.destroy();
  }

  typeChange($event) {
    // QUERY隐藏 对象，主键
    // console.info($event);
    this.validatorsChange($event.target.value);
    this.form.controls.selectSql.setValue(null);
  }

  validatorsChange(type) {
    if (this.manipulationQuery === type) {
      // 清空所填写表单
      this.form.controls.tableName.setValue(null);
      this.form.controls.pri.setValue(null);
      this.form.controls.tableName.clearValidators();
      this.form.controls.pri.clearValidators();
    } else {
      this.form.controls.tableName.setValidators(Validators.required);
      this.form.controls.pri.setValidators(Validators.required);
    }
    this.sqlReadonly = this.manipulationQuery !== type;
    this.form.controls.tableName.updateValueAndValidity();
    this.form.controls.pri.updateValueAndValidity();
  }
}

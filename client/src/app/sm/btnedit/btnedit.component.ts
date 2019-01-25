import { Component, OnInit, ViewChild } from '@angular/core';
import { NzModalRef, NzMessageService } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { SFSchema, SFUISchema } from '@delon/form';
import { FormGroup, Validators, FormBuilder } from '@angular/forms';
import { IResponse } from '@core/net/model/IResponse';
import { SmServerConstant } from '../constants/smserver.constants';
import { ButtonModel } from '../model/button.model';
import { DictConstant } from '../constants/dict.constant';
import { GoldbalConstant } from '../constants/goldbal.constant';

@Component({
  selector: 'app-sm-btnedit',
  templateUrl: './btnedit.component.html',
  styleUrls: ['../dataview/edit/edit.component.css'],
})
export class SmBtneditComponent implements OnInit {
  form: FormGroup;
  constructor(
    private modal: NzModalRef,
    public msgSrv: NzMessageService,
    public http: _HttpClient,
    private fb: FormBuilder,
  ) {}
  optionList = DictConstant.createButtons();
  positionList = DictConstant.createLocation();

  ngOnInit(): void {
    if (!this.form) {
      const button = new ButtonModel();
      this.form = this.fb.group({
        id: [button.id],
        option: [button.option],
        modal: [button.modal],
        size: [button.size],
        icon: [button.icon],
        title: [button.title, [Validators.required, Validators.maxLength(10)]],
        url: [button.url, [Validators.required]],
        position: [button.position, [Validators.required]],
        color: [button.color],
        sort: [button.sort],
        verify: [button.verify],
      });
    }
    this.optionSelected();
  }

  optionSelected() {
    if (this.form.controls.option.value === GoldbalConstant.OPTION.service) {
      // this.form.controls.url.setValidators([Validators.required]);
      this.form.controls.modal.setValidators([]);
      this.form.controls.size.setValidators([]);
    } else if (
      this.form.controls.option.value === GoldbalConstant.OPTION.modal
    ) {
      this.form.controls.url.setValidators([]);
      this.form.controls.modal.setValidators([Validators.required]);
      this.form.controls.size.setValidators([Validators.required]);
    } else {
      // this.form.controls.url.setValidators([]);
      this.form.controls.modal.setValidators([]);
      this.form.controls.size.setValidators([]);
    }
    // this.form.controls.url.updateValueAndValidity();
    this.form.controls.modal.updateValueAndValidity();
    this.form.controls.size.updateValueAndValidity();
  }

  showProperty(option) {
    return this.form.controls.option.value === option;
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
    this.modal.close(this.form.value);
  }

  close() {
    this.modal.destroy();
  }
}

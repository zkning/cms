import {
  FormBuilder,
  FormGroup,
  FormControl,
  Validators,
} from '@angular/forms';
import { NzMessageService, NzModalRef } from 'ng-zorro-antd';
import { _HttpClient } from '@delon/theme';
import { ActivatedRoute } from '@angular/router';
import { DataViewEditModel } from '../model/dataviewedit.model';
import { FieldModel } from '../model/field.model';
import { ButtonModel } from '../model/button.model';
import { GoldbalConstant } from '../constants/goldbal.constant';
import { SmServerConstant } from '../constants/smserver.constants';
import { IResponse } from '@core/net/model/IResponse';
import { OnInit, Component } from '@angular/core';

// zkning
@Component({
  selector: 'app-sm-dataviewedit',
  templateUrl: './dataViewEdit.component.html',
})
// dataView create/update
export class DataViewEditComponent implements OnInit {
  dataViewEditModel: DataViewEditModel;
  buttonOpts: ButtonModel;
  record: any;
  ngbForm: FormGroup;

  // 操作列
  displayData = new Array<FieldModel>();
  formGroup = {};
  constructor(
    public activeModal: NzModalRef,
    private fb: FormBuilder,
    private httpService: _HttpClient,
    private toastr: NzMessageService,
  ) {}

  ngOnInit() {
    this.displayData = this.dataViewEditModel.fields;
    this.buildFormGroup();
    this.ngbForm = new FormGroup(this.formGroup);
    // this.ngbForm.valueChanges.subscribe(data => this.onValueChanged(data));
  }
  onSubmit() {
    const url = this.buttonOpts.url;
    this.httpService
      .post<IResponse<any>>(url, this.ngbForm.getRawValue())
      .subscribe(response => {
        if (IResponse.statuscode.SUCCESS === response.code) {
          this.activeModal.close(response.message);
        } else {
          this.toastr.error(response.message);
        }
      });
  }

  get retrieveflag() {
    return (
      this.buttonOpts.curd &&
      GoldbalConstant.CRUD.retrieve === this.buttonOpts.curd
    );
  }

  get editflag() {
    return (
      this.buttonOpts.curd &&
      (GoldbalConstant.CRUD.create === this.buttonOpts.curd ||
        GoldbalConstant.CRUD.update === this.buttonOpts.curd)
    );
  }

  buildFormGroup() {
    const displayFieldList = new Array<FieldModel>();
    this.dataViewEditModel.fields.forEach(fieldModel => {
      // fg[element.field] = new FormControl(this.viewModel[element.field], <any>Validators.required),

      // 添加操作
      if (
        this.buttonOpts.curd === GoldbalConstant.CRUD.create &&
        fieldModel.insert
      ) {
        displayFieldList.push(fieldModel);

        // 修改操作
      } else if (
        this.buttonOpts.curd === GoldbalConstant.CRUD.update &&
        fieldModel.updateType !== GoldbalConstant.UPDATE_TYPE.MODIFTY_HIDE
      ) {
        displayFieldList.push(fieldModel);

        // 查看
      } else if (
        this.buttonOpts.curd === GoldbalConstant.CRUD.retrieve &&
        fieldModel.view
      ) {
        displayFieldList.push(fieldModel);
      }
      // 表单验证
      const validators = this.getValidators(fieldModel);
      this.formGroup[fieldModel.field] = new FormControl(
        {
          value: this.record[fieldModel.field],
          disabled:
            fieldModel.updateType ===
              GoldbalConstant.UPDATE_TYPE.MODIFTY_DISABLE &&
            (this.buttonOpts.curd &&
              GoldbalConstant.CRUD.update === this.buttonOpts.curd),
        },
        validators,
      );
    });
    this.displayData = displayFieldList;
  }

  // 添加表单验证器
  getValidators(fieldModel: FieldModel) {
    const validators = [];
    if (fieldModel.maxlength) {
      validators.push(Validators.maxLength(fieldModel.maxlength));
    }
    // 遍历表单验证器
    if (fieldModel.pattern) {
      const rules = JSON.parse(fieldModel.pattern);
      for (const item in rules) {
        if (GoldbalConstant.validators.required === item) {
          validators.push(Validators.required);
        } else if (GoldbalConstant.validators.email === item) {
          validators.push(Validators.email);
        } else {
          validators.push(Validators.pattern(item));
        }
      }
    }
    return validators;
  }

  labelFormat(title) {
    if (title && title.length > 4) {
      return title.substr(0, 3) + '..';
    }
    return title;
  }

  // 动态获取表单验证提示
  formErrorMsg(field) {
    const validator = this.ngbForm.get(field.field).errors;
    const ruleJson = JSON.parse(field.pattern);
    // 判断是否为表达式
    const pattern = validator[GoldbalConstant.validators.pattern];
    if (pattern) {
      // ng包含^ $
      const rule = pattern.requiredPattern.substring(
        1,
        pattern.requiredPattern.length - 1,
      );
      return ruleJson[rule];
    } else if (validator[GoldbalConstant.validators.required]) {
      return ruleJson[GoldbalConstant.validators.required];
    } else if (validator[GoldbalConstant.validators.email]) {
      return ruleJson[GoldbalConstant.validators.email];
    }
  }
}

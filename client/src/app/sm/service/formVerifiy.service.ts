import { Injectable } from '@angular/core';
import {
  FormArray,
  FormGroup,
  FormBuilder,
  Validators,
  FormControl,
} from '@angular/forms';

/**
 * FormVerifiyService
 * @ningzk
 */
@Injectable()
export class FormVerifiyService {
  constructor() {}

  formVerifiy(ngbForm, data?: any) {
    if (!ngbForm) {
      return;
    }

    // 是否清空message ??
    const formErrors = new Array<string>();
    for (const key in ngbForm.controls) {
      const control = ngbForm.controls[key];

      if (control instanceof FormGroup) {
        Array.prototype.push.apply(
          formErrors,
          this.formGroupErrors(control, data),
        );
      }

      if (control instanceof FormArray) {
        Array.prototype.push.apply(
          formErrors,
          this.formArrayErrors(control, data),
        );
      }

      if (control instanceof FormControl) {
        const errors = this.formControlErrors(key, control, data);
        if (errors) {
          formErrors.push(errors);
        }
      }
    }
    return formErrors;
  }

  formArrayErrors(formArray, data?: any) {
    const formErrors = new Array<string>();
    formArray.controls.forEach(element => {
      const errors = this.formGroupErrors(element, data);
      if (errors.length > 0) {
        Array.prototype.push.apply(formErrors, errors);
      }
    });
    return formErrors;
  }

  formGroupErrors(formGrop, data?: any) {
    const formErrors = new Array<string>();
    for (const name in formGrop.controls) {
      const errors = this.formControlErrors(
        name,
        formGrop.controls[name],
        data,
      );
      if (errors) {
        formErrors.push(errors);
      }
    }
    return formErrors;
  }

  // 获取错误信息
  formControlErrors(name, control, data?: any) {
    if (!control.valid) {
      // control.errors required,minlength
      for (const error in control.errors) {
        // 错误信息..
        return `${name}` + error;
      }
    }
  }
}

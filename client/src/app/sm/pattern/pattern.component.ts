import { Component, OnInit, Input } from '@angular/core';
import {
  FormArray,
  FormGroup,
  FormBuilder,
  Validators,
  FormControl,
} from '@angular/forms';
import { FormVerifiyService } from '../service/formVerifiy.service';
import { ActivatedRoute } from '@angular/router';
import { PatternModel } from '../model/pattern.model';
import { NzModalRef } from 'ng-zorro-antd';

@Component({
  selector: 'app-sm-pattern',
  templateUrl: './pattern.component.html',
  // styleUrls: ['../dataview/edit/edit.component.css'],
})
export class PatternComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private modal: NzModalRef,
  ) {}
  ngbForm: FormGroup;
  // 规则
  patterns: Array<PatternModel> = [];
  selectedPatterns: Array<PatternModel> = [];

  // 自定义规则
  definePatterns: Array<PatternModel> = [];
  @Input() formGroup: any;
  ngOnInit() {
    this.ngbForm = this.fb.group({
      defaultPatterns: this.fb.array([]),
      definePatterns: this.fb.array([]),
    });

    // 初始化默认规则
    this.defaultPatterns();
    if (this.formGroup && this.formGroup.controls.pattern.value) {
      // console.info(this.formGroup.controls.pattern.value);
      // const testJson = '{"email" : "Email格式不正确" , "[0-9]+" : "请输入整数" , "[A-Za-z]+" : "请输入英文" , "ABC" : "ABC TIP"}';

      // 遍历json
      const rules = JSON.parse(this.formGroup.controls.pattern.value);
      for (const item in rules) {
        this.checkboxSelected(item, rules[item]);
      }

      // 自定义规则初始化
      this.definePatterns.forEach(pattern => {
        this.definePatternsControls.push(
          this.fb.group({
            tip: [pattern.tip, Validators.required],
            rule: [pattern.rule, Validators.required],
            checked: [pattern.checked],
          }),
        );
      });
    }

    // 添加默认规则并比较是否选中
    this.patterns.forEach(pattern => {
      this.patternsControls.push(
        this.fb.group({
          tip: [pattern.tip],
          rule: [pattern.rule],
          checked: [pattern.checked],
        }),
      );
    });

    // 默认defaultPatterns
    this.patternsControls.valueChanges.subscribe(values => {
      const selects: Array<PatternModel> = [];
      values.forEach((selected: PatternModel, i: number) => {
        if (selected.checked === true) {
          selects.push(this.patterns[i]);
        }
      });
      this.selectedPatterns = selects;
    });
    //   this.ngbForm.valueChanges.subscribe(data => this.onValueChanged(data));
  }

  get patternsControls() {
    return this.ngbForm.controls.defaultPatterns as FormArray;
  }

  get definePatternsControls() {
    return this.ngbForm.controls.definePatterns as FormArray;
  }

  defaultPatterns() {
    this.patterns.push(new PatternModel('必填', 'required', false));
    // this.patterns.push(new PatternModel('超出最大长度', 'maxlength', false));
    this.patterns.push(new PatternModel('请输入整数', '[0-9]+', false));
    this.patterns.push(new PatternModel('请输入英文', '[A-Za-z]+', false));
    this.patterns.push(new PatternModel('Email格式不正确', 'email', false));
  }

  addRule() {
    this.addDefinePatternsControls(new PatternModel(null, null, false));
  }

  addDefinePatternsControls(pattern) {
    this.definePatternsControls.push(
      this.fb.group({
        tip: [pattern.tip, [Validators.required]],
        rule: [pattern.rule, [Validators.required]],
      }),
    );
  }

  removeControls(controls, idx) {
    controls.removeAt(idx);
  }

  checkboxSelected(ruleItem, tipItem) {
    for (const item of this.patterns) {
      if (item.rule === ruleItem) {
        // 默认项标记为选中
        item.checked = true;
        this.selectedPatterns.push(new PatternModel(tipItem, ruleItem, true));
        return;
      }
    }
    // 不匹配归类自定义
    this.definePatterns.push(new PatternModel(tipItem, ruleItem, false));
  }

  onSubmit() {
    const lastPatternJson = {};
    this.selectedPatterns.forEach(item => {
      lastPatternJson[item.rule] = item.tip;
    });
    this.definePatternsControls.controls.forEach((item: FormGroup) => {
      lastPatternJson[item.controls.rule.value] = item.controls.tip.value;
    });
    // console.info(JSON.stringify(lastPatternJson));
    this.modal.close(JSON.stringify(lastPatternJson));
  }

  close() {
    this.modal.destroy();
  }
}

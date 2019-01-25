export class PatternModel {
  constructor(tip, rule, checked) {
    this.tip = tip;
    this.rule = rule;
    this.checked = checked || false;
  }
  tip: string;
  rule: string;
  checked: boolean;
}

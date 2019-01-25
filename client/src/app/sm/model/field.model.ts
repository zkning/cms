import { RuleModel } from './rule.model';

export class FieldModel {
  constructor() {
    this.field = '';
    this.title = '';
    this.updateType = 'hide';
    this.view = true;
    this.insert = true;
    this.visible = true;
    this.dataType = 'verchar';
    this.fieldType = 'text';
    this.maxlength = 0;

    // 索引
    this.sort = 0;

    // 排列
    this.align = 'center';
    this.halign = 'center';

    // 宽度
    // this.width = '';

    // 排序
    this.order = '';

    // 不可重复
    this.duplicated = false;

    // 格式化
    this.dataFormat = '';
    this.radio = false;
    this.checkbox = false;
    this.pattern = '';
  }

  // 字段
  field: string;
  halign: string;
  falign: string;
  valign: string;
  sortable: boolean;
  cardVisible: boolean;
  switchable: boolean;
  clickToSelect: boolean;
  formatter: string;
  sortName: string;
  searchable: boolean;

  // 名称
  title: string;

  // 修改选项（隐藏,可用,不可用）
  updateType: string;

  // 可查看
  view: boolean;

  // 可添加
  insert: boolean;

  // 列表可见
  visible: boolean;
  checkbox: boolean;
  radio: boolean;

  // 数据类型
  dataType: string;

  // 字段类型
  fieldType: string;

  // 最大长度
  maxlength: number;

  // 索引
  sort: number;

  // 排列
  align: string;

  // 宽度
  width: string;

  // 排序
  order: string;

  // 不可重复
  duplicated: boolean;

  // 格式化
  dataFormat: string;
  titleTooltip: string;
  pattern: string;
  rowspan: number;
  colspan: number;
  searchFormatter: boolean;
  escape: boolean;
}

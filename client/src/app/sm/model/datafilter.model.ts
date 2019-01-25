import { FieldModel } from './field.model';

export class DataFilterModel {
  constructor() {
    this.title = '';
    this.field = '';
    this.fieldType = '';
    this.expression = '';
    this.sort = '';
    this.extra = '';
  }
  // 标题
  title: string;

  // 字段名
  field: string;

  // 字段类型
  fieldType: string;

  // 表达式
  expression: string;

  // 索引
  sort: string;

  // 扩展
  extra: string;
}

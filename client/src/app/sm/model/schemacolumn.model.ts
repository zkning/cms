import { FieldModel } from './field.model';

export class SchemaColumnModel {
  constructor() {
    this.columnName = '';
    this.columnKey = '';
    this.columnComment = '';
  }
  columnName: string;
  columnKey: string;
  columnComment: string;
}

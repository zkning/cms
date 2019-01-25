import { FieldModel } from './field.model';
import { SchemaColumnModel } from './schemacolumn.model';

export class SchemaTableModel {
  constructor() {
    this.tableName = '';
    this.tableComment = '';
    this.schemaColumnModelList = new Array<SchemaColumnModel>();
  }
  tableName: string;
  tableComment: string;
  schemaColumnModelList: Array<SchemaColumnModel>;
}

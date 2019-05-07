import { FieldModel } from './field.model';
import { DataFilterModel } from './datafilter.model';
import { ButtonModel } from './button.model';
import { OptionsModel } from './options.model';
import { ZtreeModel } from './ztree.model';

export class DataViewEditModel {
  constructor() {
    this.id = '';
    this.dataViewName = '';
    this.sqlId = '';
    this.remark = '';
    this.options = new OptionsModel();
    this.fields = new Array<FieldModel>();
    this.treeOptions = new ZtreeModel();
    this.dataFilters = new Array<DataFilterModel>();
    this.buttons = new Array<ButtonModel>();
  }
  id: string;
  dataViewName: string;
  sqlId: string;
  sqlType: number;
  remark: string;
  options: OptionsModel;
  fields: Array<FieldModel>;
  treeOptions: ZtreeModel;
  dataFilters: Array<DataFilterModel>;
  buttons: Array<ButtonModel>;
  version: number;
}

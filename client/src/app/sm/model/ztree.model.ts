import { FieldModel } from './field.model';

export class ZtreeModel {
  constructor() {
    this.sqlId = '';
    this.idKey = '';
    this.name = '';
    this.pidKey = '';
    this.scope = '';
    this.enable = false;
    this.visible = false;
    this.width = 12;
    this.foreignKey = '';
    this.nodeValue = '';
  }
  visible: boolean;
  sqlId: string;
  idKey: string;
  name: string;
  pidKey: string;
  scope: string;
  enable: boolean;
  width: number;
  foreignKey: string;
  nodeValue: string;
}

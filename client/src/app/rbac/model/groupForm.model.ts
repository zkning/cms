export class GroupInfo {
  constructor() {
    this.groupName = '';
    this.remark = '';
    this.pid = '';
    this.groupType = 1;
    this.extra = '';
    this.version = 0;
    this.isValid = 1;
    this.id = '';
  }

  id: string;
  groupName: string;
  remark: string;
  pid: string;
  groupType: number;
  extra: string;
  version: number;
  isValid: number;
}

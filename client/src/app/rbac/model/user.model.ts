export class RbacUserInfo {
  constructor() {
    this.userName = '';
    this.name = '';
    this.mobile = '';
    this.email = '';
    this.groupId = '';
    this.version = 0;
    // this.isValid = 1;
    this.id = '';
    this.roles = [];
  }

  id: string;
  userName: string;
  name: string;
  mobile: string;
  email: string;
  groupId: string;
  version: number;
  roles: Array<any>;
  avatar: string;
  groupName: string;
  createTime: string;
  lastUpdateTime: string;
  lastUpdateUser: string;
  createUser: string;
  // isValid: number;
}

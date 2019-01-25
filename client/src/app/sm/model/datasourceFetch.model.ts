export class DataSourceFetchModel {
  constructor() {
    this.id = '';
    this.remark = '';
    this.version = 0;
  }

  driverClassName: string;
  name: string;
  url: string;
  dbUsername: string;
  dbPassword: string;
  id: string;
  remark: string;
  version: number;
}

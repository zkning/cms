export class RbacResInfo {
  constructor() {
    this.text = '';
    this.link = '';
    this.externalLink = '';
    this.icon = '';
    this.pid = '0';
    this.version = 0;
    this.resourceType = 0;
    this.id = '';
    this.code = '';
  }

  id: string;
  code: '';
  version: number;
  text: string;
  link: string;
  externalLink: string;
  icon: string;
  pid: string;
  resourceType: number;
  extra: string;
}

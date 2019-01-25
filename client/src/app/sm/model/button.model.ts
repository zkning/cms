import { FieldModel } from './field.model';

export class ButtonModel {
  constructor() {
    this.id = '';
    this.option = 'modal';
    this.modal = '';
    this.size = 800;
    this.icon = '';
    this.title = '';
    this.url = '';
    this.position = 'NAV';
    this.sort = 1;
    this.color = '';
    this.verify = false;
  }
  // id
  id: string;

  // server,model,window
  option: string;

  // 组件
  modal: string;

  // 窗口大小
  size: number;

  // 图标
  icon: string;

  // 标题
  title: string;

  // 服务端url
  url: string;

  // 导航按钮 row,nav
  position: string;

  // 颜色
  color: string;

  // 索引
  sort: number;
  curd: number;
  verify: boolean;
}

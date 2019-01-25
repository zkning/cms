export class SmServerConstant {
  // 列表查询
  static list = '/sm/dataView/list';
  static fetch = '/sm/dataView/fetch';
  static delete = '/sm/dataView/delete';
  static insert = '/sm/dataView/insert';
  static update = '/sm/dataView/update';
  static tree = '/sm/dataView/tree';
  static toRes = '/sm/dataView/toRes';
  static refreshId = '/sm/dataView/refreshId';

  // 获取数据字典根据上级编码
  static findByPValue = '/dict/findByPValue';

  static showFullColumns = '/sm/sqlId/showFullColumns';
  static sqlId_create = '/sm/sqlId/create/';
  static sqlId_modfity = '/sm/sqlId/modfity/';
  static sqlId_delete = '/sm/sqlId/delete/';
  static sqlId_list = '/sm/sqlId/list/';
  static sqlId_fetch = '/sm/sqlId/fetch/';
  static sqlId_getTree = '/sm/sqlId/getTree';

  static sqldefine_fetch = '/sm/sqldefine/fetch';
  static sqldefine_list = '/sm/sqldefine/list';
  static sqldefine_edit = '/sm/sqldefine/edit';
  static sqldefine_delete = '/sm/sqldefine/delete';

  static sqlId_getSchemaTable = '/sm/sqlId/getSchemaTable';
  static sqlId_preview = '/sm/sqlId/preview';

  // 选择器
  static sm_sqldefine_selector = '';

  // 数据源
  static ds_edit = '/dataSource/edit';
  static ds_delete = '/dataSource/delete';
  static ds_fetch = '/dataSource/fetch';
  static ds_list = '/dataSource/list';
  static ds_findAll = '/dataSource/findAll';
}

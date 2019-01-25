export class RbacApplication {
  // 分组树
  static groupTree = '/group/getGroupTree';

  static saveOrUpdate = '/group/edit';
  static delete = '/group/delete';
  static findGroupById = '/group/fetch';

  // 查询分组下成员
  static findAllUserByGroupId = '/rbacuser/list';
  static findById = '/rbacuser/fetch';
  static userSaveOrUpdate = '/rbacuser/edit';
  static userDelete = '/rbacuser/delete';
  static getAccountInfo = '/rbacuser/getAccountInfo';
  static unlock = '/rbacuser/unlock';

  // 数据字典
  static dict_edit = '/dict/edit';
  static dict_delete = '/dict/delete';
  static dict_fetch = '/dict/fetch';
  static dict_list = '/dict/list';
  static dict_tree = '/dict/tree';
  static dict_findByPValue = '/dict/findByPValue';

  // 角色
  static role_list = '/role/list';
  static role_edit = '/role/edit';
  static role_delete = '/role/delete';
  static role_fetch = '/role/fetch';

  // 资源
  static res_list = '/resource/list';
  static res_edit = '/resource/edit';
  static res_delete = '/resource/delete';
  static res_fetch = '/resource/fetch';
  static res_getRoleResTree = '/resource/getRoleResTree';
  static res_saveRoleRes = '/resource/saveRoleRes';
  static res_saveRoleUser = '/resource/saveRoleUser';
  static res_removeRoleUser = '/resource/removeRoleUser';

  // 授权登陆
  static login = '/login';
  static oauth2Token = '/oauth/token';

  // 获取用户信息
  static user_me = '/rbacuser/getCustomInfo';
  static user_updateInfo = '/rbacuser/updateInfo';

  // 修改密码
  static user_updatePwd = '/rbacuser/updatePwd';
}

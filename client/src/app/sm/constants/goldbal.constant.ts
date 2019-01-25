export class GoldbalConstant {
  static POSTION = { NAV: 'NAV', ROW: 'ROW' };
  static METHOD = { POST: 'POST', GET: 'GET' };
  static CRUD = {
    create: 1,
    update: 2,
    delete: 3,
    retrieve: 4,
  };
  static OPTION = { service: 'SERVER', modal: 'MODAL', window: 'WINDOW' };
  static UPDATE_TYPE = {
    MODIFTY_HIDE: 'hide',
    MODIFTY_ENABLE: 'enable',
    MODIFTY_DISABLE: 'disable',
  };

  static validators = {
    required: 'required',
    email: 'email',
    pattern: 'pattern',
    maxlength: 'maxlength',
  };

  static manipulate = { QUERY: 'QUERY', CRUD: 'CRUD' };
}

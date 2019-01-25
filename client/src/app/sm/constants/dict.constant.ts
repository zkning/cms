import { GoldbalConstant } from './goldbal.constant';

/**
 * 字典
 */
export class DictConstant {
  constructor(parameters) {}

  // 修改方式
  static createUpdateTypes() {
    const updateTypes = new Array<any>();
    updateTypes.push({ value: 'hide', text: '隐藏' });
    updateTypes.push({ value: 'enable', text: '可用' });
    updateTypes.push({ value: 'disable', text: '禁用' });
    return updateTypes;
  }

  static createAligns() {
    const aligns = new Array<any>();
    aligns.push({ value: 'center', text: '居中' });
    aligns.push({ value: 'right', text: '居右' });
    aligns.push({ value: 'left', text: '居左' });
    return aligns;
  }

  static createValigns() {
    const valigns = new Array<any>();
    valigns.push({ value: 'middle', text: '中部' });
    valigns.push({ value: 'top', text: '顶部' });
    valigns.push({ value: 'bottom', text: '底部' });
    return valigns;
  }

  static createfieldTypes() {
    const fieldTypes = new Array<any>();
    fieldTypes.push({ value: 'TEXT', text: '文本框' });
    fieldTypes.push({ value: 'DROPDOWN', text: '下拉框' });
    fieldTypes.push({ value: 'CHECKBOX', text: '选择框' });
    fieldTypes.push({ value: 'TEXTAREA', text: '文本域' });
    return fieldTypes;
  }

  static createButtons() {
    const funcButtons = new Array<any>();
    funcButtons.push({
      value: GoldbalConstant.OPTION.service,
      text: GoldbalConstant.OPTION.service,
    });
    funcButtons.push({
      value: GoldbalConstant.OPTION.modal,
      text: GoldbalConstant.OPTION.modal,
    });
    // funcButtons.push({ value: 'window', text: 'window' });
    return funcButtons;
  }

  static createScopes() {
    const scopes = new Array<any>();
    scopes.push({ value: 'ALL', text: '全部' });
    scopes.push({ value: 'CHILD', text: '子节点' });
    scopes.push({ value: 'SELF', text: '当前节点' });
    return scopes;
  }

  static createExpressions() {
    const expressions = new Array<any>();
    expressions.push({ value: '=', text: '=' });
    expressions.push({ value: '>', text: '>' });
    expressions.push({ value: '>=', text: '>=' });
    expressions.push({ value: '<', text: '<' });
    expressions.push({ value: '<=', text: '<=' });
    expressions.push({ value: 'LIKE', text: 'LIKE' });
    return expressions;
  }

  static createMethods() {
    const methods = new Array<any>();
    methods.push({ value: 'POST', text: 'POST' });
    methods.push({ value: 'GET', text: 'GET' });
    return methods;
  }

  static createLocation() {
    const location = new Array<any>();
    location.push({ value: 'NAV', text: 'NAV' });
    location.push({ value: 'ROW', text: 'ROW' });
    return location;
  }

  static createModalsize() {
    const modalsize = new Array<any>();
    modalsize.push({ value: 'lg', text: 'lg' });
    modalsize.push({ value: 'sm', text: 'sm' });
    return modalsize;
  }

  static createSidePagination() {
    const sidePagination = new Array<any>();
    sidePagination.push({ value: 'client', text: 'client' });
    sidePagination.push({ value: 'server', text: 'server' });
    return sidePagination;
  }

  static createQueryParamsType() {
    const queryParamsType = new Array<any>();
    queryParamsType.push({ value: 'undefined', text: 'undefined' });
    queryParamsType.push({ value: 'limit', text: 'limit' });
    return queryParamsType;
  }

  // 'basic', 'all', 'selected'
  static createExportDataType() {
    const exportDataType = new Array<any>();
    exportDataType.push({ value: 'basic', text: '当前页记录' });
    exportDataType.push({ value: 'all', text: '所有记录' });
    exportDataType.push({ value: 'selected', text: '所选记录' });
    return exportDataType;
  }

  static createOrderBy() {
    const exportDataType = new Array<any>();
    exportDataType.push({ value: 'desc', text: 'DESC' });
    exportDataType.push({ value: 'asc', text: 'ASC' });
    return exportDataType;
  }

  static defineType() {
    const defineType = new Array<any>();
    defineType.push({ value: 'QUERY', text: 'QUERY' });
    defineType.push({ value: 'CRUD', text: 'CRUD' });
    return defineType;
  }
}

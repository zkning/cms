import { FieldModel } from './field.model';
import { GoldbalConstant } from '../constants/goldbal.constant';

export class SqlDefineFetchModel {
  constructor() {
    // ID
    this.id = '';

    // 别名
    this.sqlName = '';

    // 查询SQL
    this.selectSql = '';

    // 扩展SQL
    this.sqlExtra = '';

    // 数据源
    this.datasource = '';
    this.sqlType = GoldbalConstant.SQL_TYPE.QUERY;

    // 是否缓存
    this.isCache = 0;

    // 备注
    this.remark = '';

    // 对象
    this.tableName = '';

    /**
     * 主表对应的ID
     */
    // 对象主键
    this.pri = '';
    this.version = 0;
  }

  // ID
  id: string;

  // 别名
  sqlName: string;

  // 查询SQL
  selectSql: string;

  // 扩展SQL
  sqlExtra: string;

  // 数据源
  datasource: string;
  datasourceText: string;
  sqlType: number;

  // 是否缓存
  isCache: number;

  // 1-编辑,2-发布
  state: number;

  // 备注
  remark: string;

  // 对象
  tableName: string;

  /**
   * 主表对应的ID
   */
  // 对象主键
  pri: string;
  version: number;
}

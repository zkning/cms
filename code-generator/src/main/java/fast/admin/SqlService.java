package fast.admin;

import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlService {
    /**
     * 字段名
     */
    private static final String FIELDNAME = "Field";
    /**
     * 注释
     */
    private static final String COMMENT = "Comment";
    private static final String KEY = "Key";
    private static final String PRI = "PRI";
    /**
     * 驱动名称
     */
    String driver = "com.mysql.jdbc.Driver";

    /**
     * 连接字符串
     */
    String url;

    /**
     * 用户名
     */
    String username;

    /**
     * 密码
     */
    String password;

    public SqlService(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * 获得连接对象
     *
     * @return 连接对象
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConnection() throws ClassNotFoundException,
            SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 关闭三剑客
     *
     * @throws SQLException
     */
    public void close(ResultSet rs, PreparedStatement pstmt, Connection con) {

        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 执行更新
     *
     * @param sql    传入的预设的 sql语句
     * @param params 问号参数列表
     * @return 影响行数
     */
    public int execUpdate(String sql, Object[] params) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 获得连接对象
            connection = this.getConnection();

            // 获得预设语句对象
            pstmt = connection.prepareStatement(sql);

            if (params != null) {
                // 设置参数列表
                for (int i = 0; i < params.length; i++) {
                    // 因为问号参数的索引是从1开始，所以是i+1，将所有值都转为字符串形式，好让setObject成功运行
                    pstmt.setObject(i + 1, params[i] + "");
                }
            }

            // 执行更新，并返回影响行数
            return pstmt.executeUpdate();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            this.close(rs, pstmt, connection);
        }
        return 0;
    }

    /**
     * 执行查询
     *
     * @param sql    传入的预设的 sql语句
     * @param params 问号参数列表
     * @return 查询后的结果
     */
    public List<Map<String, Object>> execQuery(String sql, Object[] params) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 获得连接对象
            connection = this.getConnection();

            // 获得预设语句对象
            pstmt = connection.prepareStatement(sql);

            if (params != null) {
                // 设置参数列表
                for (int i = 0; i < params.length; i++) {
                    // 因为问号参数的索引是从1开始，所以是i+1，将所有值都转为字符串形式，好让setObject成功运行
                    pstmt.setObject(i + 1, params[i] + "");
                }
            }

            // 执行查询
            rs = pstmt.executeQuery();
            List<Map<String, Object>> al = new ArrayList<Map<String, Object>>();

            // 获得结果集元数据（元数据就是描述数据的数据，比如把表的列类型列名等作为数据）
            ResultSetMetaData rsmd = rs.getMetaData();

            // 获得列的总数
            int columnCount = rsmd.getColumnCount();

            // 遍历结果集
            while (rs.next()) {
                Map<String, Object> hm = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    // 根据列索引取得每一列的列名,索引从1开始
                    String columnName = rsmd.getColumnLabel(i);
                    // 根据列名获得列值
                    Object columnValue = rs.getObject(columnName);
                    // 将列名作为key，列值作为值，放入 hm中，每个 hm相当于一条记录
                    hm.put(columnName, columnValue);
                }
                // 将每个 hm添加到al中, al相当于是整个表，每个 hm是里面的一条记录
                al.add(hm);
            }
            return al;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            this.close(rs, pstmt, connection);
        }
        return null;
    }


    public List<CodeTemplateModel.ColModel> execQuery(String tableName) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // 获得连接对象
            connection = this.getConnection();

            // 获得预设语句对象
            pstmt = connection.prepareStatement(" SELECT * FROM " + tableName + " WHERE 1=2 ");

            // 执行查询
            rs = pstmt.executeQuery();
            List<CodeTemplateModel.ColModel> al = new ArrayList<CodeTemplateModel.ColModel>();

            // 获得结果集元数据（元数据就是描述数据的数据，比如把表的列类型列名等作为数据）
            ResultSetMetaData rsmd = rs.getMetaData();

            // 获得列的总数
            int columnCount = rsmd.getColumnCount();
            List<Map<String, Object>> colMapList = showColumns(tableName);
            Map<String, String> commentMap = getCommentMap(colMapList);
            Map<String, String> priMap = getCommentMap(colMapList);
            for (int index = 1; index <= columnCount; index++) {
                CodeTemplateModel.ColModel colModel = new CodeTemplateModel.ColModel();

                String colName = rsmd.getColumnName(index);

                // 驼峰
                colModel.setColName(camelCaseName(colName));

                // 类型
                String className = rsmd.getColumnClassName(index);
                colModel.setColType(getTypeStr(className));

                //注释
                if (commentMap.containsKey(colName.toLowerCase())) {
                    colModel.setComment(commentMap.get(colName.toLowerCase()));
                }

                // 主键
                colModel.setPri(priMap.containsKey(colName.toLowerCase()));
                al.add(colModel);
            }
            return al;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            this.close(rs, pstmt, connection);
        }
        return null;
    }

    private Map<String, String> getCommentMap(List<Map<String, Object>> coluMapList) {
        Map<String, String> commentMap = new HashMap<String, String>();
        for (Map<String, Object> item : coluMapList) {
            commentMap.put(String.valueOf(item.get(FIELDNAME)).toLowerCase(),
                    String.valueOf(item.get(COMMENT)));
        }
        return commentMap;
    }

    private Map<String, String> getPri(List<Map<String, Object>> coluMapList) {
        Map<String, String> priMap = new HashMap<String, String>();
        for (Map<String, Object> item : coluMapList) {
            if (PRI.equalsIgnoreCase(String.valueOf(item.get(KEY)))) {
                priMap.put(String.valueOf(item.get(FIELDNAME)).toLowerCase(),
                        String.valueOf(item.get(COMMENT)));
                break;
            }
        }
        return priMap;
    }

    private String getTypeStr(String className) {
        return className.substring(className.lastIndexOf(".") + 1);
    }

    public List<Map<String, Object>> showColumns(String tableName) {
        String sql = " SHOW FULL COLUMNS FROM " + tableName;
        List<Map<String, Object>> maps = this.execQuery(sql, null);
        return maps;
    }


    /**
     * 驼峰
     *
     * @param underscoreName
     * @return
     */
    public static String camelCaseName(String underscoreName) {
        StringBuilder result = new StringBuilder();
        if (StringUtils.isBlank(underscoreName)) {
            return result.toString();
        }
        boolean flag = false;
        for (int i = 0; i < underscoreName.length(); i++) {
            char ch = underscoreName.charAt(i);
            if ("_".charAt(0) == ch) {
                flag = true;
            } else {
                if (flag) {
                    result.append(Character.toUpperCase(ch));
                    flag = false;
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }
}
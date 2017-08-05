package com.xxx.web.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 数据库操作模版，实现了数据层和业务层的对接和隔离 
 * @author 门士松 20121107
 * @version 1.0
 */
public class JdbcTemplate {
	
    private String name = null;
    /**
     * 根据datasource.xml文件中配置的数据源名称，构造数据操作对象
     * @param id 数据源的name
     */
    public JdbcTemplate(String name){
    	this.name = name;
    }
    /**
     * 根据默认数据源名称，构造数据操作对象
     * @param id 数据源的name
     */
    public JdbcTemplate(){
    }    
    /**
     * 获得一个新的Session
     */
    private Session getSession()
    {
        Session session = null;    
    	Connection conn = null;
    	try {
    		if(name!=null){
        		conn = DBConfigure.getDataSource(name).getConnection();
    		}else{
    			conn = DBConfigure.getDataSource().getConnection();
    		}
    		session = new Session(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return session;
    }
    /**
     * 在相应的表中增加一条记录
     * @throws SQLException 
     */
	public void insert(String tableName, DataRow data) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			session.insert(tableName, data);
		} finally {
				session.close();
		}
	}
    /**
     * 更新表中的一条记录信息
     * @param tableName     需要更新的表名
     * @param data          需要更新的信息
     * @param identify      识别字段的名称
     * @param identifyValue 识别字段的值
     * @throws SQLException 
     */
	public void update(String tableName, DataRow data, String identify,Object identifyValue) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			session.update(tableName, data, identify, identifyValue);
		} finally {
				session.close();
		}
	}
    /**
     * 删除表中的记录
     * @param tableName     需要删除的记录的表名
     * @param identify      识别字段的名称
     * @param identifyValue 识别字段的值
     * @throws SQLException 
     */
	public void delete(String tableName, String identify, Object identifyValue) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			session.delete(tableName, identify, identifyValue);
		} finally {
			session.close();
		}
	}
	/**
	 * 执行指定的sql并返回更新的记录数。
	 * @param sql SQL语句
	 * @return 更新的记录数
	 * @throws SQLException 
	 */
	public int update(String sql) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.update(sql);
		} finally {
			session.close();
		}
	}
    /**
     * 执行指定的sql并返回更新的记录数。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @return 更新的记录数
     * @throws SQLException 
     */
	public int update(String sql, Object[] args) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.update(sql, args);
		} finally {
			session.close();
		}
	}
    /**
     * 查询一个整型结果。
     * @param sql SQL语句
     * @return 查询的第一行的第一个字段的整型值。
     * @throws SQLException 
     */
	public int queryInt(String sql) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.queryInt(sql);
		} finally {
			session.close();
		}
	}
    /**
     * 查询一个整型结果。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @return 查询的第一行的第一个字段的整型值。
     * @throws SQLException 
     */
	public Integer queryInt(String sql, Object[] args) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.queryInt(sql, args);
		} finally {
			session.close();
		}
	}
    /**
     * 查询一个字符串结果。
     * @param sql SQL语句
     * @return 查询的第一行的第一个字段的字符串值。
     * @throws SQLException 
     */
	public String queryString(String sql) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.queryString(sql);
		} finally {
				session.close();
		}
	}
    /**
     * 查询一个字符串结果。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @return 查询的第一行的第一个字段的字符串值。
     * @throws SQLException 
     */
	public String queryString(String sql, Object[] args) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.queryString(sql, args);
		} finally {
				session.close();
		}
	}
    /**
     * 查询一条记录，返回类型为DataRow，
     * @param sql SQL语句
     * @return 查询的第一行的结果,反回结果中的字段名都为小写
     * @throws SQLException 
     */
	public DataRow queryMap(String sql) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.queryMap(sql);
		} finally {
				session.close();
		}
	}
    /**
     * 查询一条记录，返回类型为DataRow。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @return 查询的第一行的结果,反回结果中的字段名都为小写。
     * @throws SQLException 
     */
	public DataRow queryMap(String sql, Object[] args) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.queryMap(sql, args);
		} finally {
				session.close();
		}
	}
    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow。
     * @param sql SQL语句
     * @return 查询所有结果列表。
     * @throws SQLException 
     */
	public List<DataRow> query(String sql) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.query(sql);
		} finally {
			session.close();
		}
	}
    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @return 查询所有结果。
     * @throws SQLException 
     */
	public List<DataRow> query(String sql, Object[] args) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.query(sql, args);
		} finally {
				session.close();
		}
	}
    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow。
     * @param sql  SQL语句
     * @param rows 返回的记录数量
     * @return 查询固定的记录数
     * @throws SQLException 
     */
    public List<DataRow> query(String sql, int rows) throws SQLException
	{
		Session session = null;
		try {
			session = getSession();
			return session.query(sql, rows);
		} finally {
				session.close();
		}
	}
    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @param rows 返回的记录数量*
     * @return 查询固定的记录数
     * @throws SQLException 
     */
	public List<DataRow> query(String sql, Object[] args, int rows) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.query(sql, args, rows);
		} finally {
			session.close();
		}
	}
    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow。
     * @param sql      SQL语句
     * @param startRow 起始的行数
     * @param rows     记录的数量
     * @return 查询所有结果并。
     * @throws SQLException 
     */
	public List<DataRow> query(String sql, int startRow, int rows) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.query(sql, startRow, rows);
		} finally {
				session.close();
		}
	}
    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow。
     * @param sql      SQL语句
     * @param args     参数中的值
     * @param startRow 起始的行数
     * @param rows     记录的数量
     * @return 查询所有结果并。
     * @throws SQLException 
     */
	public List<DataRow> query(String sql, Object[] args, int startRow, int rows) throws SQLException {
		Session session = null;
		try {
			session = getSession();
			return session.query(sql, args, startRow, rows);
		} finally {
				session.close();
		}
	}
}

package com.xxx.web.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC操作模板，实现了各种增删查改，并且支持事务处理
 * 注意：操作完成要关闭conn连接
 * @author 门士松  20121027
 * @version 1.0
 * @since
 */
public class Session
{
    private Logger logger = Logger.getLogger(this.getClass());
    private Connection conn = null;

	public Session(Connection conn){
        this.conn = conn;
    }
    public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}	
    /**
     * 在相应的表中增加一条记录(序列自增长添加)
     * @param tableName 需要添加的表名
     * @param data      需要添加的信息
     * @param sequencesName   主键名   
     * @param primaryKey   序列名  
     * @return
     * @throws SQLException 
     */
    public void insert(String tableName, DataRow data,String primaryKey ,String sequencesName) throws SQLException{
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("insert into " + tableName + "( "+primaryKey+", ");
        String interrogationStr = "";
        int i = 0;
        ArrayList<Object> valueList = new ArrayList<Object>();
        for (String key : data.keySet()){
            valueList.add(data.get(key));
            i++;
            if (i < data.size()){
                sqlBuffer.append(key + ",");
                interrogationStr += " ?,";
            }else{
                sqlBuffer.append(key);
                interrogationStr += "?";
            }
        }
        sqlBuffer.append(") values ("+sequencesName+".nextval, ");
        sqlBuffer.append(interrogationStr);
        sqlBuffer.append(") ");
        logger.debug( sqlBuffer.toString());
        update(sqlBuffer.toString(), valueList.toArray());
    }    
    /**
     * 在相应的表中增加一条记录
     * @param tableName 需要添加的表名
     * @param data      需要添加的信息
     * @return
     * @throws SQLException 
     */
    public void insert(String tableName, DataRow data) throws SQLException{
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("insert into " + tableName + "(");
        String interrogationStr = "";
        int i = 0;
        ArrayList<Object> valueList = new ArrayList<Object>();
        for (String key : data.keySet()){
            valueList.add(data.get(key));
            i++;
            if (i < data.size()){
                sqlBuffer.append(key + ",");
                interrogationStr += " ?,";
            }else{
                sqlBuffer.append(key);
                interrogationStr += "?";
            }
        }
        sqlBuffer.append(") values (");
        sqlBuffer.append(interrogationStr);
        sqlBuffer.append(") ");
        logger.debug( sqlBuffer.toString());
        update(sqlBuffer.toString(), valueList.toArray());
    }

    /**
     * 更新表中的一条记录信息
     *
     * @param tableName     需要更新的表名
     * @param data          需要更新的信息
     * @param identify      识别字段的名称
     * @param identifyValue 识别字段的值
     * @return
     * @throws SQLException 
     */
    public void update(String tableName, DataRow data, String identify, Object identifyValue) throws SQLException{
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("update " + tableName + " set ");
        int i = 0;
        ArrayList<Object> valueList = new ArrayList<Object>();
        data.remove(identify);
        for (String key :data.keySet()){
            valueList.add(data.get(key));
            if (i < data.size()){
                sqlBuffer.append(key + "=?,");
            }else{
                sqlBuffer.append(key + "=?");
            }
        }
        sqlBuffer.append(" where " + identify + "=?");
        valueList.add(identifyValue);
        logger.debug( sqlBuffer.toString());
        update(sqlBuffer.toString(), valueList.toArray());
    }

    /**
     * 删除表中的记录
     * @param tableName     需要删除的记录的表名
     * @param identify      识别字段的名称
     * @param identifyValue 识别字段的值
     * @throws SQLException 
     */
    public void delete(String tableName, String identify, Object identifyValue) throws SQLException{
        String sql = "delete from " + tableName + " where " + identify + "=?";
        logger.debug( sql);
        update(sql, new Object[]{identifyValue});
    }
    /**
     * 执行指定的sql并返回更新的记录数。
     * @param sql SQL语句
     * @return 更新的记录数
     * @throws SQLException 
     */
    public int update(String sql) throws SQLException{
    	logger.debug( sql);
        return update(sql, null);
    }
    /**
     * 执行指定的sql并返回更新的记录数。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @return 更新的记录数
     * @throws SQLException 
     */
    public int update(String sql, Object[] args) throws SQLException{
        PreparedStatement pstmt = null;
        try{
            pstmt = conn.prepareStatement(sql);
            if (args != null){
                for (int i = 1; i <= args.length; i++){
                    pstmt.setObject(i, args[i - 1]);
                }
            }
            logger.debug( sql);
            return pstmt.executeUpdate();
        }catch (SQLException ex){
        	ex.printStackTrace();
            throw ex;
        }finally{
            closeStatement(pstmt);
        }
    }
    /**
     * 查询一个整型结果。
     * @param sql SQL语句
     * @return 查询的第一行的第一个字段的整型值。
     * @throws SQLException 
     */
    public Integer queryInt(String sql) throws SQLException{
    	logger.debug(sql);
        return queryInt(sql, null);
    }
    /**
     * 查询一个整型结果。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @return 查询的第一行的第一个字段的整型值。
     * @throws SQLException 
     */
    public Integer queryInt(String sql, Object[] args) throws SQLException{
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer result = null ;
        try{
            pstmt = conn.prepareStatement(sql);
            if (args != null){
                for (int i = 1; i <= args.length; i++){
                    pstmt.setObject(i, args[i - 1]);
                }
            }
            logger.debug( sql);
            rs = pstmt.executeQuery();
            if (rs.next()){
                result = rs.getInt(1);
            }
        }catch (SQLException ex){
        	ex.printStackTrace();
            throw ex;
        }
        finally{
            closeResultSet(rs);
            closeStatement(pstmt);
        }
        return result;
    }
    /**
     * 查询一个字符串结果。
     * @param sql SQL语句
     * @return 查询的第一行的第一个字段的字符串值。
     * @throws SQLException 
     */
    public String queryString(String sql) throws SQLException{
    	logger.debug( sql);
        return queryString(sql, null);
    }
    /**
     * 查询一个字符串结果。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @return 查询的第一行的第一个字段的字符串值。
     * @throws SQLException 
     */
    public String queryString(String sql, Object[] args) throws SQLException{
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String result = null;
        try{
            pstmt = conn.prepareStatement(sql);
            if (args != null){
                for (int i = 1; i <= args.length; i++){
                    pstmt.setObject(i, args[i - 1]);
                }
            }
            logger.debug(sql);
            rs = pstmt.executeQuery();
            if (rs.next()){
                result = rs.getString(1);
            }
        }catch (SQLException ex){
        	ex.printStackTrace();
            throw ex;
        }finally{
            closeResultSet(rs);
            closeStatement(pstmt);
        }
        return result;
    }
    /**
     * 查询一个DataRow结果，若没有查询到，则返回null。
     * @param sql SQL语句
     * @return 查询的第一行的结果,反回结果中的字段名都为小写
     * @throws SQLException 
     */
    public DataRow queryMap(String sql) throws SQLException{
    	logger.debug(sql);
        return queryMap(sql, null);
    }
    /**
     * 查询一个DataRow结果,若没有查询到，则返回null。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @return 查询的第一行的结果,反回结果中的字段名都为小写。
     * @throws SQLException 
     */
    public DataRow queryMap(String sql, Object[] args) throws SQLException{
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        DataRow result = null;
        try{
            pstmt = conn.prepareStatement(sql);
            if (args != null){
                for (int i = 1; i <= args.length; i++){
                    pstmt.setObject(i, args[i - 1]);
                }
            }
            logger.debug(sql);
            rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            if (rs.next()){
                result = toDataRow(rs, metaData);
            }
        }
        catch (SQLException ex){
        	ex.printStackTrace();
            throw ex;
        }finally{
            closeResultSet(rs);
            closeStatement(pstmt);
        }
        return result;
    }
    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow,若没有查询到，则返回null。
     * @param sql SQL语句
     * @return 查询所有结果并。
     * @throws SQLException 
     */
    public List<DataRow> query(String sql) throws SQLException{
        logger.debug( sql);
        return query(sql, null);
    }
    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @return 查询所有结果。
     * @throws SQLException 
     */
    public List<DataRow> query(String sql, Object[] args) throws SQLException{
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<DataRow>  list  = new ArrayList<DataRow>();
        try{
            pstmt = conn.prepareStatement(sql);
            if (args != null){
                for (int i = 1; i <= args.length; i++){
                    pstmt.setObject(i, args[i - 1]);
                }
            }
            logger.debug(sql);
            rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()){
                list.add(toDataRow(rs, metaData));
            }
        }catch (SQLException ex){
        	ex.printStackTrace();
            throw ex;
        }finally{
            closeResultSet(rs);
            closeStatement(pstmt);
        }
        return list;
    }

    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow,若没有查询到，则返回null。
     *
     * @param sql  SQL语句
     * @param rows 查询结果数量
     * @return 查询所有结果并。
     * @throws SQLException 
     */
    public List<DataRow> query(String sql, int rows) throws SQLException{
    	logger.debug( sql);
        return query(sql, null, rows);
    }
    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow,若没有查询到，则返回null。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @param rows 查询结果数量
     * @return 查询所有结果。
     * @throws SQLException 
     */
    public List<DataRow> query(String sql, Object[] args, int rows) throws SQLException{
    	logger.debug( sql);    	
        return query(sql, args, 0, rows);
    }
    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow。
     * @param sql  SQL语句
     * @param startRow 开始记录数      
     * @param rows 返回的记录数
     * @return 查询所有结果并。
     * @throws SQLException 
     */
    public List<DataRow> query(String sql, int startRow, int rows) throws SQLException{
    	logger.debug(sql);
        return query(sql, null, startRow, rows);
    }

    /**
     * 查询一个对象列表结果,列表中的每一行为一个DataRow。
     * @param sql  SQL语句
     * @param args 参数中的值
     * @param startRow 开始记录数      
     * @param rows 返回的记录数
     * @return 查询所有结果。
     * @throws SQLException 
     */
    public List<DataRow> query(String sql, Object[] args, int startRow, int rows) throws SQLException{
            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("select * from ( select row_.*, rownum rownum_ from ( ");
            sqlBuffer.append(sql);
            sqlBuffer.append(" ) row_ where rownum <= " + (startRow + rows) + ") where rownum_ > " + startRow + "");
        	logger.debug(sql);   
            return queryFromSpecialDB(sqlBuffer.toString(), args);
    }
    private List<DataRow> queryFromSpecialDB(String sql, Object[] args) throws SQLException{
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<DataRow> list = new ArrayList<DataRow>();
        try{
            pstmt = conn.prepareStatement(sql);
            pstmt.setFetchSize(50);
            if (args != null){
                for (int i = 1; i <= args.length; i++){
                    pstmt.setObject(i, args[i - 1]);
                }
            }
        	logger.debug(sql);
            rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()){
                list.add(toDataRow(rs, metaData));
            }
            return list;
        }catch (SQLException ex){
        	ex.printStackTrace();
            throw ex;
        }finally{
            closeResultSet(rs);
            closeStatement(pstmt);
        }
    }
	/**
	 * 关闭ResultSet
	 */
    private void closeResultSet(ResultSet rs){
        try{
            if (rs != null){
                rs.close();
            }
        }catch (Exception ex){
        	ex.printStackTrace();
        }
    }
	/**
	 * 关闭Statement
	 */
    private void closeStatement(Statement stmt){
        try{
            if (stmt != null){
                stmt.close();
            }
        }catch (Exception ex){
        	ex.printStackTrace();
        }
    }
	/**
	 * 结果集合转换为DataRow格式
	 * @param rs
	 * @param metaData
	 * @throws SQLException
	 */
    private DataRow toDataRow(ResultSet rs, ResultSetMetaData metaData) throws SQLException
    {
        DataRow dataRow = new DataRow();
        int count = metaData.getColumnCount();
        for (int i = 0; i < count; i++){
            String fieldName = metaData.getColumnName(i + 1);
            Object value = rs.getObject(fieldName);
            if (value instanceof Clob){
                value = rs.getString(fieldName);
            }else if (value instanceof Blob){
                value = rs.getBytes(fieldName);
            }else if (value instanceof Date){
                value = rs.getTimestamp(fieldName);
            }
            //把字段名转换为小写
            dataRow.set(fieldName.toLowerCase(), value);
        }
        return dataRow;
    }
    /**
     * 开始数据库事务
     * @throws Exception 
     */
    public void begin() throws SQLException{
        try{
        	conn.setAutoCommit(false);
        }catch (SQLException e){
        	e.printStackTrace();
            throw e;
        }
    }
    /**
     * 提交数据库事务
     * @throws SQLException 
     */
    public void commit() throws SQLException{
        try{
        	conn.commit();
        }catch (SQLException e){
        	e.printStackTrace();
            throw e;
        }
    }
    /**
     * 回滚数据库事务
     * @throws SQLException 
     */
    public void rollback() throws SQLException{
        try{
        	conn.rollback();
        }catch (SQLException e){
        	e.printStackTrace();
            throw e;
        }
    }
    /**
     * 关闭数据库连接
     * @throws Exception 
     */
    public void close() throws SQLException{
        try{
        	conn.close();
        }catch (SQLException e){
        	e.printStackTrace();
            throw e;
        }
    }    
}

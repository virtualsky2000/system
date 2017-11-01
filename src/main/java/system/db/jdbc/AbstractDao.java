package system.db.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import system.exception.ApplicationException;
import system.logging.LogManager;
import system.logging.Logger;

public class AbstractDao<T> {

    protected Logger log;

    protected Connection conn;

    private int dbType = 0;

    private List<String> lstFieldName;

    private Map<String, String> mapFieldName;

    private static ThreadLocal<String> curSql = new ThreadLocal<String>() {

        @Override
        protected String initialValue() {
            return new String();
        }

    };

    protected AbstractDao(Connection conn) {
        if (log == null) {
            log = LogManager.getLogger(this.getClass());
        }
        this.conn = conn;
        if (conn.getClass().getName().indexOf(".HiRDB.") > 0) {
            dbType = 1;
        }
    }

    public void rollback() throws SQLException {
        conn.rollback();
    }

    protected void closeOnCompletion(Statement stmt) throws SQLException {
        if (dbType == 0) {
            stmt.closeOnCompletion();
        }
    }

    protected Statement createStatement() throws SQLException {
        Statement stmt = conn.createStatement();
        closeOnCompletion(stmt);
        return stmt;
    }

    protected Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        Statement stmt = conn.createStatement(resultSetType, resultSetConcurrency);
        closeOnCompletion(stmt);
        return stmt;
    }

    protected Statement createStatement(int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        Statement stmt = conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        closeOnCompletion(stmt);
        return stmt;
    }

    protected PreparedStatement createPreparedStatement(String sql) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        curSql.set(sql);
        closeOnCompletion(ps);
        return ps;
    }

    protected PreparedStatement createPreparedStatement(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
        curSql.set(sql);
        closeOnCompletion(ps);
        return ps;
    }

    protected PreparedStatement createPreparedStatement(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        curSql.set(sql);
        closeOnCompletion(ps);
        return ps;
    }

    protected PreparedStatement createPreparedStatement(String sql, Map<String, Object> params) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql);
        setParamters(ps, params);
        return ps;
    }

    protected PreparedStatement createPreparedStatement(String sql, Object... params) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql);
        setParamters(ps, params);
        return ps;
    }

    protected PreparedStatement createCallableStatement(String sql) throws SQLException {
        CallableStatement stmt = conn.prepareCall(sql);
        curSql.set(sql);
        closeOnCompletion(stmt);
        return stmt;
    }

    protected PreparedStatement createCallableStatement(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        CallableStatement stmt = conn.prepareCall(sql, resultSetType, resultSetConcurrency);
        curSql.set(sql);
        closeOnCompletion(stmt);
        return stmt;
    }

    protected PreparedStatement createCallableStatement(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        CallableStatement stmt = conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        curSql.set(sql);
        closeOnCompletion(stmt);
        return stmt;
    }

    protected ResultSet executeQuery(PreparedStatement ps) throws SQLException {
        log.info("SQL:{}", curSql.get());
        return ps.executeQuery();
    }

    protected int executeUpdate(PreparedStatement ps) throws SQLException {
        log.info("SQL:{}", curSql.get());
        return ps.executeUpdate();
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql);
        return executeQuery(ps);
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql, params);
        return executeQuery(ps);
    }

    public int executeUpdate(String sql) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql);
        return executeUpdate(ps);
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        PreparedStatement ps = createPreparedStatement(sql, params);
        return executeUpdate(ps);
    }

    public void setParamters(PreparedStatement ps, Object... params) throws SQLException {
        int index = 0;
        for (Object param : params) {
            if (param instanceof String) {
                ps.setString(index, (String) param);
            } else if (param instanceof Number) {
                if (param instanceof Byte) {
                    ps.setByte(index, (byte) param);
                } else if (param instanceof Short) {
                    ps.setShort(index, (short) param);
                } else if (param instanceof Integer) {
                    ps.setInt(index, (int) param);
                } else if (param instanceof Long) {
                    ps.setLong(index, (long) param);
                } else if (param instanceof Float) {
                    ps.setFloat(index, (float) param);
                } else if (param instanceof Double) {
                    ps.setDouble(index, (double) param);
                } else if (param instanceof BigDecimal) {
                    ps.setBigDecimal(index, (BigDecimal) param);
                }
            } else if (param instanceof Boolean) {
                ps.setBoolean(index, (boolean) param);
            } else if (param instanceof Date) {
                ps.setDate(index, (Date) param);
            } else if (param instanceof Time) {
                ps.setTime(index, (Time) param);
            } else if (param instanceof Timestamp) {
                ps.setTimestamp(index, (Timestamp) param);
            } else if (param instanceof Blob) {
                ps.setBlob(index, (Blob) param);
            } else if (param instanceof Clob) {
                ps.setClob(index, (Clob) param);
            } else {
                ps.setObject(index, param);
            }

            index++;
        }
    }

    public List<T> query(String sql, Class<T> clazz, Object... params) throws SQLException {
        List<T> lstData = new ArrayList<>();
        try (ResultSet rs = executeQuery(sql, params)) {
            while (rs.next()) {
                lstData.add(mapRow(rs, clazz));
            }

            return lstData;
        }
    }

    public int insert(T entity) throws SQLException {

        return 0;
    }

    public int update(T entity) throws SQLException {

        return 0;
    }

    public int delete(T entity) throws SQLException {

        return 0;
    }

    private String getFieldName(String key) {
        if (mapFieldName.containsKey(key)) {
            return mapFieldName.get(key);
        }
        for (String name : lstFieldName) {
            if (name.equalsIgnoreCase(key)) {
                mapFieldName.put(key, name);
                return name;
            }
        }

        mapFieldName.put(key, null);
        return null;
    }

    protected T mapRow(ResultSet rs, Class<T> clazz) throws SQLException {
        try {
            T bean = clazz.newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int count = metaData.getColumnCount();

            if (lstFieldName == null) {
                Field[] fields = clazz.getDeclaredFields();
                lstFieldName = new ArrayList<>(fields.length);
                mapFieldName = new HashMap<>();
                for (Field field : fields) {
                    lstFieldName.add(field.getName());
                }
            }

            for (int i = 1; i <= count; i++) {
                String name = getFieldName(metaData.getColumnName(i));
                if (name != null) {
                    BeanUtils.setProperty(bean, name, rs.getObject(i));
                }
            }

            return bean;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ApplicationException(e);
        }
    }

}

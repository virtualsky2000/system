package system.db.jpa;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;

import system.db.SqlUtils;
import system.logging.LogManager;
import system.logging.Logger;
import system.utils.ClassUtils;

public abstract class AbstractDao {

    private Logger log;

    /** エンティティマナージャー */
    @PersistenceContext
    private EntityManager em;

    protected AbstractDao() {
        if (log == null) {
            log = LogManager.getLogger(this.getClass());
        }
    }

    public void clear() {
        em.clear();
    }

    public void close() {
        em.close();
    }

    public <T> List<T> getResultListBySql(String sql, Map<String, Object> params, Class<T> clazz) {
        Query query = em.createNativeQuery(sql, clazz);
        setParameters(query, params);
        return ClassUtils.cast(query.getResultList());
    }

    public <T> List<T> getResultListBySql(String sql, Object[] params, Class<T> clazz) {
        Query query = em.createNativeQuery(sql, clazz);
        setParameters(query, params);
        return ClassUtils.cast(query.getResultList());
    }

    public <T> List<T> getResultListByJpql(String sql, Map<String, Object> params, Class<T> clazz) {
        TypedQuery<T> query = em.createQuery(sql, clazz);
        setParameters(query, params);
        return query.getResultList();
    }

    public <T> List<T> getResultListByJpql(String sql, Object[] params, Class<T> clazz) {
        TypedQuery<T> query = em.createQuery(sql, clazz);
        setParameters(query, params);
        return query.getResultList();
    }

    public int executeUpdateBySql(String sql, Map<String, Object> params) {
        Query query = em.createNativeQuery(sql);
        setParameters(query, params);
        return query.executeUpdate();
    }

    public int executeUpdateBySql(String sql, Object[] params) {
        Query query = em.createNativeQuery(sql);
        setParameters(query, params);
        return query.executeUpdate();
    }

    public int executeUpdateByJpql(String sql, Map<String, Object> params) {
        Query query = em.createQuery(sql);
        setParameters(query, params);
        outputJpqlLog(sql, params);
        return query.executeUpdate();
    }

    public int executeUpdateByJpql(String sql, Object[] params) {
        Query query = em.createQuery(sql);
        setParameters(query, params);
        outputJpqlLog(sql, params);
        return query.executeUpdate();
    }

    public int executeUpdateByName(String name, Map<String, Object> params) {
        Query query = em.createNamedQuery(name);
        setParameters(query, params);
        return query.executeUpdate();
    }

    public int executeUpdateByName(String name, Object[] params) {
        Query query = em.createNamedQuery(name);
        setParameters(query, params);
        return query.executeUpdate();
    }

    public boolean executeProcedure(String name, Map<String, Object> params) {
        StoredProcedureQuery procedure = em.createNamedStoredProcedureQuery(name);
        setParameters(procedure, params);
        return procedure.execute();
    }

    protected void setParameters(Query query, Map<String, Object> params) {
        for (Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }

    protected void setParameters(Query query, Object[] params) {
        for (int i = 0, len = params.length; i < len; i++) {
            query.setParameter(i, params[i]);
        }
    }

    protected void outputLog(String type, String sql, Map<String, Object> params) {
        if (log.isDebugEnabled()) {
            log.debug("old {}=({})", type, sql);
            params.entrySet().forEach(entry -> log.debug("key=({}), value=({})", entry.getKey(), entry.getValue()));
            sql = SqlUtils.formatSql(sql, params);
            log.debug("format {}=({})", type, sql);
        }
    }

    protected void outputLog(String type, String sql, Object[] params) {
        if (log.isDebugEnabled()) {
            log.debug("old {}=({})", type, sql);
            sql = SqlUtils.formatSql(sql, params);
            log.debug("format {}=({})", type, sql);
        }
    }

    protected void outputSqlLog(String sql, Map<String, Object> params) {
        outputLog("SQL", sql, params);
    }

    protected void outputSqlLog(String sql, Object[] params) {
        outputLog("SQL", sql, params);
    }

    protected void outputJpqlLog(String sql, Map<String, Object> params) {
        outputLog("JPQL", sql, params);
    }

    protected void outputJpqlLog(String sql, Object[] params) {
        outputLog("JPQL", sql, params);
    }

}

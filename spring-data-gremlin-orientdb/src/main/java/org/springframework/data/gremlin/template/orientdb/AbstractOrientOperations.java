package org.springframework.data.gremlin.template.orientdb;

import com.orientechnologies.common.exception.OException;
import com.orientechnologies.orient.core.cache.OLocalRecordCache;
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseListener;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.dictionary.ODictionary;
import com.orientechnologies.orient.core.exception.OTransactionException;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.intent.OIntent;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.query.OQuery;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLQuery;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.storage.ORecordCallback;
import com.orientechnologies.orient.core.storage.ORecordMetadata;
import com.orientechnologies.orient.core.storage.OStorage;
import com.orientechnologies.orient.core.tx.OTransaction;
import com.orientechnologies.orient.core.version.ORecordVersion;
import org.springframework.data.gremlin.annotation.orientdb.DetachMode;
import org.springframework.data.gremlin.tx.orientdb.OrientDBGremlinGraphFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.Callable;

public abstract class AbstractOrientOperations<T> implements OrientOperations<T> {
    //private static final Logger logger = LoggerFactory.getLogger(AbstractOrientOperations.class);

    protected final OrientDBGremlinGraphFactory dbf;

    protected Set<String> defaultClusters;

    protected AbstractOrientOperations(OrientDBGremlinGraphFactory dbf) {
        this.dbf = dbf;
    }

    @Override
    public String getName() {
        return database().getName();
    }

    @Override
    public String getURL() {
        return database().getURL();
    }

    @Override
    public ODatabase<T> database() {
        return (ODatabase<T>) dbf.graph().getRawGraph();
    }

    @Override
    public Object setProperty(String name, Object value) {
        return database().setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) {
        return database().getProperty(name);
    }

    @Override
    public Iterator<Map.Entry<String, Object>> getProperties() {
        return database().getProperties();
    }

    @Override
    public Object get(ODatabase.ATTRIBUTES attribute) {
        return database().get(attribute);
    }

    @Override
    public <DB extends ODatabase<T>> DB set(ODatabase.ATTRIBUTES attribute, Object value) {
        return database().set(attribute, value);
    }

    @Override
    public void registerListener(ODatabaseListener listener) {
        database().registerListener(listener);
    }

    @Override
    public void unregisterListener(ODatabaseListener listener) {
        database().unregisterListener(listener);
    }

    @Override
    public Map<ORecordHook, ORecordHook.HOOK_POSITION> getHooks() {
        return database().getHooks();
    }

    @Override
    public <DB extends ODatabase<T>> DB registerHook(ORecordHook hook) {
        return database().registerHook(hook);
    }

    @Override
    public <DB extends ODatabase<T>> DB registerHook(ORecordHook hook, ORecordHook.HOOK_POSITION position) {
        return database().registerHook(hook, position);
    }

    @Override
    public <DB extends ODatabase<T>> DB unregisterHook(ORecordHook hook) {
        return database().unregisterHook(hook);
    }

    @Override
    public ORecordHook.RESULT callbackHooks(ORecordHook.TYPE type, OIdentifiable id) {
        return database().callbackHooks(type, id);
    }

    @Override
    public void backup(OutputStream out, Map<String, Object> options, Callable<Object> callable, OCommandOutputListener listener, int compressionLevel, int bufferSize) throws IOException {
        database().backup(out, options, callable, listener, compressionLevel, bufferSize);
    }

    @Override
    public void restore(InputStream in, Map<String, Object> options, Callable<Object> callable, OCommandOutputListener listener) throws IOException {
        database().restore(in, options, callable, listener);
    }

    @Override
    public String getType() {
        return database().getType();
    }

    @Override
    public long getSize() {
        return database().getSize();
    }

    @Override
    public void freeze(boolean throwException) {
        database().freeze(throwException);
    }

    @Override
    public void freeze() {
        database().freeze();
    }

    @Override
    public void release() {
        database().release();
    }

    @Override
    public OMetadata getMetadata() {
        return database().getMetadata();
    }

    @Override
    public ORecordMetadata getRecordMetadata(ORID rid) {
        return database().getRecordMetadata(rid);
    }


    @Override
    public ODictionary<T> getDictionary() {
        return database().getDictionary();
    }

    @Override
    public boolean declareIntent(OIntent intent) {
        return database().declareIntent(intent);
    }

    @Override
    public boolean isMVCC() {
        return database().isMVCC();
    }

    @Override
    public <DB extends ODatabase<T>> DB setMVCC(boolean mvcc) {
        return database().setMVCC(mvcc);
    }

    @Override
    public boolean isClosed() {
        return database().isClosed();
    }

    @Override
    public boolean isDefault(String clusterName) {
        loadDefaultClusters();
        return defaultClusters.contains(clusterName);
    }

    private void loadDefaultClusters() {
        if (defaultClusters == null) {
            synchronized (this) {
                if (defaultClusters == null) {
                    defaultClusters = new HashSet<>();
                    for (OClass oClass : database().getMetadata().getSchema().getClasses()) {
                        String defaultCluster = getClusterNameById(oClass.getDefaultClusterId());
                        defaultClusters.add(defaultCluster);
                    }
                }
            }
        }
    }

    @Override
    public void reload() {
        database().reload();
    }

    @Override
    public T reload(T entity, String fetchPlan, boolean ignoreCache) {
        return database().reload(entity, fetchPlan, ignoreCache);
    }

    @Override
    public ODatabase.STATUS getStatus() {
        return database().getStatus();
    }

    @Override
    public <DB extends ODatabase<T>> DB setStatus(ODatabase.STATUS status) {
        return database().setStatus(status);
    }

    @Override
    public OTransaction getTransaction() {
        return database().getTransaction();
    }

    @Override
    public ODatabase<T> begin() {
        return database().begin();
    }

    @Override
    public ODatabase<T> begin(OTransaction.TXTYPE type) {
        return database().begin(type);
    }

    @Override
    public ODatabase<T> begin(OTransaction tx) {
        return database().begin(tx);
    }

    @Override
    public ODatabase<T> commit() {
        return database().commit();
    }

    @Override
    public ODatabase<T> commit(boolean force) throws OTransactionException {
        return database().commit(force);
    }

    @Override
    public ODatabase<T> rollback() {
        return database().rollback();
    }

    @Override
    public ODatabase<T> rollback(boolean force) throws OTransactionException {
        return database().rollback(force);
    }

    @Override
    public OLocalRecordCache getLevel2Cache() {
        return database().getLocalCache();
    }

    @Override
    public T newInstance() {
        return database().newInstance();
    }

    @Override
    public T load(ORID recordId) {
        return database().load(recordId);
    }

    @Override
    public T load(String recordId) {
        return load(new ORecordId(recordId));
    }

    public T load(T entity) {
        return database().load(entity);
    }

    @Override
    public T load(T entity, String fetchPlan) {
        return database().load(entity, fetchPlan);
    }

    @Override
    public T load(T entity, String fetchPlan, boolean ignoreCache) {
        return database().load(entity, fetchPlan, ignoreCache);
    }

    @Override
    public T load(ORID recordId, String fetchPlan) {
        return database().load(recordId, fetchPlan);
    }

    @Override
    public T load(ORID recordId, String fetchPlan, boolean ignoreCache) {
        return database().load(recordId, fetchPlan, ignoreCache);
    }

    @Override
    public T load(T entity, String fetchPlan, boolean ignoreCache, boolean loadTombstone, OStorage.LOCKING_STRATEGY lockingStrategy) {
        return database().load(entity, fetchPlan, ignoreCache, loadTombstone, lockingStrategy);
    }

    @Override
    public T load(ORID recordId, String fetchPlan, boolean ignoreCache, boolean loadTombstone, OStorage.LOCKING_STRATEGY lockingStrategy) {
        return database().load(recordId, fetchPlan, ignoreCache, loadTombstone, lockingStrategy);
    }

    @Override
    public <S extends T> S save(S entity) {
        return database().save(entity);
    }

    @Override
    public <S extends T> S save(S entity, String cluster) {
        return database().save(entity, cluster);
    }

    @Override
    public <S extends T> S save(S entity, ODatabase.OPERATION_MODE mode, boolean forceCreate, ORecordCallback<? extends Number> recordCallback, ORecordCallback<ORecordVersion> recordUpdatedCallback) {
        return database().save(entity, mode, forceCreate, recordCallback, recordUpdatedCallback);
    }

    @Override
    public long countClass(Class<?> clazz) {
        return count(new OSQLSynchQuery<Long>("select count(*) from " + clazz.getSimpleName()));
    }

    @Override
    public long countClass(String className) {
        return count(new OSQLSynchQuery<Long>("select count(*) from " + className));
    }

    @Override
    public long count(OSQLQuery<?> query, Object... args) {
        return ((ODocument) database().query(query, args).get(0)).field("count");
    }

    @Override
    public long countClusterElements(String clusterName) {
        return database().countClusterElements(clusterName);
    }

    @Override
    public long countClusterElements(int clusterId) {
        return database().countClusterElements(clusterId);
    }

    @Override
    public long countClusterElements(int[] clusterIds) {
        return database().countClusterElements(clusterIds);
    }

    @Override
    public long countClusterElements(int iClusterId, boolean countTombstones) {
        return database().countClusterElements(iClusterId, countTombstones);
    }

    @Override
    public long countClusterElements(int[] iClusterIds, boolean countTombstones) {
        return database().countClusterElements(iClusterIds, countTombstones);
    }

    @Override
    public int getClusters() {
        return database().getClusters();
    }

    @Override
    public boolean existsCluster(String clusterName) {
        return database().existsCluster(clusterName);
    }

    @Override
    public Collection<String> getClusterNames() {
        return database().getClusterNames();
    }

    @Override
    public int getClusterIdByName(String clusterName, Class<?> clazz) {
        OClass oClass = database().getMetadata().getSchema().getClass(clazz);
        for (int clusterId : oClass.getClusterIds()) {
            if (getClusterNameById(clusterId).equals(clusterName)) {
                return clusterId;
            }
        }

        throw new OException("Cluster " + clusterName + " not found");
    }

    @Override
    public String getClusterNameByRid(String rid) {
        return getClusterNameById(new ORecordId(rid).getClusterId());
    }

    @Override
    public List<String> getClusterNamesByClass(Class<?> clazz, boolean showDefault) {
        int[] clusterIds = database().getMetadata().getSchema().getClass(clazz).getClusterIds();
        int defaultCluster = getDefaultClusterId(clazz);

        List<String> clusters = new ArrayList<>(clusterIds.length);
        for (int clusterId : clusterIds) {
            if (showDefault || clusterId != defaultCluster) {
                clusters.add(getClusterNameById(clusterId));
            }
        }

        return clusters;
    }

    @Override
    public int getDefaultClusterId(Class<?> domainClass) {
        return database().getMetadata().getSchema().getClass(domainClass).getDefaultClusterId();
    }

    @Override
    public long getClusterRecordSizeById(int clusterId) {
        return database().getClusterRecordSizeById(clusterId);
    }

    @Override
    public long getClusterRecordSizeByName(String clusterName) {
        return database().getClusterRecordSizeByName(clusterName);
    }

    @Override
    public int addCluster(String type, String clusterName, String location, String dataSegmentName, Object... params) {
        return database().addCluster(type, clusterName, location, dataSegmentName, params);
    }

    @Override
    public int addCluster(String type, String clusterName, int requestedId, String location, String dataSegmentName, Object... params) {
        return database().addCluster(type, clusterName, requestedId, location, dataSegmentName, params);
    }

    @Override
    public int addCluster(String clusterName, Object... params) {
        return database().addCluster(clusterName, params);
    }

    @Override
    public int addCluster(String clusterName) {
        return database().addCluster(clusterName);
    }

    @Override
    public void freezeCluster(int iClusterId, boolean throwException) {
        database().freezeCluster(iClusterId, throwException);
    }

    @Override
    public void freezeCluster(int iClusterId) {
        database().freezeCluster(iClusterId);
    }

    @Override
    public void releaseCluster(int iClusterId) {
        database().releaseCluster(iClusterId);
    }

    @Override
    public ODatabase<T> delete(ORID recordId) {
        return database().delete(recordId);
    }

    @Override
    public ODatabase<T> delete(T entity) {
        return database().delete(entity);
    }

    @Override
    public ODatabase<T> delete(ORID rid, ORecordVersion version) {
        return database().delete(rid, version);
    }

    @Override
    public int getDefaultClusterId() {
        return database().getDefaultClusterId();
    }

    @Override
    public String getClusterNameById(int clusterId) {
        return database().getClusterNameById(clusterId);
    }

    @Override
    public int getClusterIdByName(String clusterName) {
        return database().getClusterIdByName(clusterName);
    }

    @Override
    public boolean existsClass(Class<?> clazz) {
        return existsClass(clazz.getSimpleName());
    }

    @Override
    public boolean existsClass(String className) {
        return database().getMetadata().getSchema().existsClass(className);
    }

    @Override
    public OSecurityUser getUser() {
        return database().getUser();
    }

    @Override
    public void setUser(OUser user) {
        database().setUser(user);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detach(RET entities) {
        List<Object> result = new ArrayList<>(entities.size());

        for (Object entity : entities) {
            result.add(detach(entity));
        }

        return (RET) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET extends List<?>> RET detachAll(RET entities) {
        List<Object> result = new ArrayList<>(entities.size());

        for (Object entity : entities) {
            result.add(detachAll(entity));
        }

        return (RET) result;
    }

    @Override
    public <RET extends List<?>> RET query(OQuery<?> query, Object... args) {
        return database().query(query, args);
    }

    @Override
    public <RET extends List<?>> RET query(OQuery<?> query, DetachMode detachMode, Object... args) {
        RET result = query(query, args);

        switch (detachMode) {
        case ENTITY:
            return detach(result);
        case ALL:
            return detachAll(result);
        case NONE:
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET> RET queryForObject(OSQLQuery<?> query, DetachMode detachMode, Object... args) {
        RET result = queryForObject(query, args);

        switch (detachMode) {
        case ENTITY:
            return detach(result);
        case ALL:
            return detachAll(result);
        case NONE:
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RET> RET queryForObject(OSQLQuery<?> query, Object... args) {
        return (RET) query(query, args).get(0);
    }

    @Override
    public <RET extends OCommandRequest> RET command(OCommandRequest command) {
        return database().command(command);
    }

    @Override
    public <RET> RET command(OCommandSQL command, Object... args) {
        return database().command(command).execute(args);
    }

    @Override
    public <RET> RET command(String sql, Object... args) {
        return database().command(new OCommandSQL(sql)).execute(args);
    }

    public boolean equals(Object other) {
        return database().equals(other);
    }

    public String toString() {
        return database().toString();
    }
}

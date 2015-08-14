package org.springframework.data.gremlin.template.orientdb;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public interface OrientObjectOperations extends OrientOperations<Object> {
    OObjectDatabaseTx getObjectDatabase();
}

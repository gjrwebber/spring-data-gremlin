package org.springframework.data.gremlin.template.orientdb;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.ORecord;

public interface OrientDocumentOperations extends OrientOperations<ORecord> {
    ODatabaseDocumentTx getDocumentDatabase();
}

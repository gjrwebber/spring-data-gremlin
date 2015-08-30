package org.springframework.data.gremlin.object.core.repository;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gremlin.object.core.domain.Address;
import org.springframework.data.gremlin.object.core.domain.Located;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("SpringJavaAutowiringInspection")
public abstract class AbstractLocatedRepositoryTest extends BaseRepositoryTest {

    @Autowired
    protected LocatedRepository locatedRepository;

    @Test
    public void should_findAll_Located() throws Exception {
        List<Located> located = new ArrayList<Located>();

        CollectionUtils.addAll(located, locatedRepository.findAll());
        assertNotNull(located);
        assertEquals(5, located.size());

        for (Located locate : located) {
            Assert.assertNotNull(locate.getLocation());
            Assert.assertNotNull(locate.getPerson());
        }
    }

    @Test
    public void should_deleteAll_Located() throws Exception {
        List<Located> located = new ArrayList<Located>();

        CollectionUtils.addAll(located, locatedRepository.findAll());
        assertEquals(5, located.size());

        locatedRepository.deleteAll();

        CollectionUtils.addAll(located, locatedRepository.findAll());
        assertEquals(0, located.size());
    }

}

package org.springframework.data.gremlin.object.core.repository;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gremlin.object.core.domain.Likes;
import org.springframework.data.gremlin.object.core.domain.Located;
import org.springframework.data.gremlin.object.core.domain.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("SpringJavaAutowiringInspection")
public abstract class AbstractEdgeRepositoryTest extends BaseRepositoryTest {

    @Autowired
    protected LikesRepository likesRepository;

    @Autowired
    protected LocatedRepository locatedRepository;

    @Test
    public void should_save_simple_edge() throws Exception {
        Likes likes = new Likes(graham, lara);
        likesRepository.save(likes);

        List<Likes> allLikes = new ArrayList<Likes>();
        CollectionUtils.addAll(allLikes, likesRepository.findAll());
        assertEquals(1, allLikes.size());

    }

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
        located.clear();

        locatedRepository.deleteAll();

        CollectionUtils.addAll(located, locatedRepository.findAll());
        assertEquals(0, located.size());
    }

    @Test
    public void should_save_edge() throws Exception {
        Located located = new Located(new Date(), graham, new Location(35, 165));
        locatedRepository.save(located);

        List<Located> newLocated = new ArrayList<Located>();
        CollectionUtils.addAll(newLocated, locatedRepository.findAll());
        assertEquals(6, newLocated.size());

    }
}

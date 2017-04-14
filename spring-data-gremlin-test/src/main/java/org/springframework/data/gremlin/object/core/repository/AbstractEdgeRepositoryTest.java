package org.springframework.data.gremlin.object.core.repository;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gremlin.object.core.domain.Likes;
import org.springframework.data.gremlin.object.core.domain.Located;
import org.springframework.data.gremlin.object.core.domain.Location;

import java.util.*;

import static org.junit.Assert.*;

@SuppressWarnings("SpringJavaAutowiringInspection")
public abstract class AbstractEdgeRepositoryTest extends BaseRepositoryTest {

    @Autowired
    protected LikesRepository likesRepository;

    @Autowired
    protected LocatedRepository locatedRepository;

    @Test
    public void should_save_simple_edge() throws Exception {

        Likes likes = new Likes(graham, lara);
        graham.getLikes().add(likes);
        lara.getLikes().add(likes);
        likesRepository.save(likes);

        List<Likes> allLikes = new ArrayList<Likes>();
        CollectionUtils.addAll(allLikes, likesRepository.findAll());
        assertEquals(6, allLikes.size());

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

        Location loc = locationRepository.save(new Location(35, 165));
        Located located = new Located(new Date(), graham, loc);
        graham.getLocations().add(located);
        locatedRepository.save(located);
        List<Located> newLocated = new ArrayList<Located>();
        CollectionUtils.addAll(newLocated, locatedRepository.findAll());
        assertEquals(6, newLocated.size());

    }

    @Test
    public void should_find_by_referenced() throws Exception {

        Likes likes = new Likes(graham, lara);
        likesRepository.save(likes);

        Iterable<Likes> all = likesRepository.findAll();
        Iterable<Likes> found = likesRepository.findByPerson1_FirstName("Graham");

        Collection<Likes> disjunction = CollectionUtils.disjunction(all, found);
        assertEquals(3, disjunction.size());
    }

    @Test
    public void should_find_by_query() throws Exception {

        Likes likes = new Likes(lara, graham);
        likesRepository.save(likes);

        Iterator<Likes> query = likesRepository.findByLiking("Lara", "Graham").iterator();
        assertTrue(query.hasNext());
        assertEquals(likes, query.next());
        assertFalse(query.hasNext());
    }
}

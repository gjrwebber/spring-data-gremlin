package org.springframework.data.gremlin.object.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.gremlin.object.domain.Location;
import org.springframework.data.gremlin.query.CompositeResult;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@SuppressWarnings("SpringJavaAutowiringInspection")
public abstract class AbstractLocationRepositoryTest extends BaseRepositoryTest {

    @Autowired
    protected NativeLocationRepository nativeLocationRepository;

    @Test
    public void should_find_locations() throws Exception {
        List<Location> locations = nativeLocationRepository.find(-33.0003, 151.0003, 0.05);
        assertNotNull(locations);
        assertEquals(1, locations.size());
    }

    @Test
    public void should_find_locations_with_params() throws Exception {
        List<Location> locations = nativeLocationRepository.findWithParam(-33.0003, 151.0003, 0.05);
        assertNotNull(locations);
        assertEquals(1, locations.size());
    }

    @Test
    public void should_find_many_locations() throws Exception {
        List<Location> locations = nativeLocationRepository.find(-33.0003, 151.0003, 1000);
        assertNotNull(locations);
        assertEquals(5, locations.size());
    }

    @Test
    public void should_find_locations_pageable() throws Exception {
        Page<Location> locations = nativeLocationRepository.find(-33.0003, 151.0003, 1000, new PageRequest(0, 2));
        assertNotNull(locations);
        assertEquals(5, locations.getTotalElements());
        assertEquals(3, locations.getTotalPages());
        assertEquals(2, locations.getNumberOfElements());

        locations = nativeLocationRepository.find(-33.0003, 151.0003, 1000, new PageRequest(2, 2));
        assertNotNull(locations);
        assertEquals(5, locations.getTotalElements());
        assertEquals(3, locations.getTotalPages());
        assertEquals(1, locations.getNumberOfElements());
    }

    @Test
    public void should_find_locations_with_distance() throws Exception {
        List<Map<String, Object>> locations = nativeLocationRepository.findNear(-33.0003, 151.0003, 0.05);
        assertNotNull(locations);
        assertEquals(1, locations.size());
        Object distance = locations.get(0).get("distance");
        assertNotNull(distance);
        assertTrue(distance instanceof Double);
        assertTrue((Double) distance > 10);

    }

    @Test
    public void should_find_locations_with_distance_pageable() throws Exception {
        Page<Map<String, Object>> locations = nativeLocationRepository.findNear(-33.0003, 151.0003, 0.05, new PageRequest(0, 2));
        assertNotNull(locations);
        assertEquals(1, locations.getNumberOfElements());
        Object distance = locations.iterator().next().get("distance");
        assertNotNull(distance);
        assertTrue(distance instanceof Double);
        assertTrue((Double) distance > 10);

    }

    @Test
    public void should_find_composite() throws Exception {
        List<CompositeResult<Location>> composites = nativeLocationRepository.findComposite(-33.0003, 151.0003, 0.05);
        assertNotNull(composites);
        assertEquals(1, composites.size());
        Location location = composites.get(0).getEntity();
        assertNotNull(location);
        Map<String, Object> properties = composites.get(0).getProperties();
        Object distance = properties.get("distance");
        assertNotNull(distance);
        assertTrue(distance instanceof Double);
        assertTrue((Double) distance > 10);

    }

    @Test
    public void should_find_composite_pageable() throws Exception {
        Page<CompositeResult<Location>> composites = nativeLocationRepository.findComposite(-33.0003, 151.0003, 0.05, new PageRequest(0, 2));
        assertNotNull(composites);
        assertEquals(1, composites.getNumberOfElements());
        CompositeResult<Location> result = composites.iterator().next();
        Location location = result.getEntity();
        assertNotNull(location);
        Map<String, Object> properties = result.getProperties();
        Object distance = properties.get("distance");
        assertNotNull(distance);
        assertTrue(distance instanceof Double);
        assertTrue((Double) distance > 10);

    }

}

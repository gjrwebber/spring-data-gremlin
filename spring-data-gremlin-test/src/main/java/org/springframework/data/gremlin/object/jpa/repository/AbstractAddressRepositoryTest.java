package org.springframework.data.gremlin.object.jpa.repository;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gremlin.object.jpa.domain.Address;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("SpringJavaAutowiringInspection")
public abstract class AbstractAddressRepositoryTest extends BaseRepositoryTest {

    @Autowired
    protected AddressRepository addressRepository;

    @Test
    public void should_find_addresses() throws Exception {
        List<Address> addresses = new ArrayList<Address>();

        CollectionUtils.addAll(addresses, addressRepository.findAll());
        assertNotNull(addresses);
        assertEquals(2, addresses.size());

        for (Address address : addresses) {
            Assert.assertNotNull(address.getPeople());
            Assert.assertTrue(address.getPeople().size() > 0);
        }
    }

    @Test
    public void should_save_and_find_embedded_country() throws Exception {
        List<Address> addresses = new ArrayList<>();

        CollectionUtils.addAll(addresses, addressRepository.findAll());
        assertNotNull(addresses);
        assertEquals(2, addresses.size());
        assertNotNull(addresses.get(0).getCountry());
        assertEquals("Australia", addresses.get(0).getCountry().getName());
        assertNotNull(addresses.get(1).getCountry());
        assertEquals("Australia", addresses.get(1).getCountry().getName());
    }

}

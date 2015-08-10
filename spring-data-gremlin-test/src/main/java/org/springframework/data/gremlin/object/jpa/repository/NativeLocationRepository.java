package org.springframework.data.gremlin.object.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gremlin.annotation.Query;
import org.springframework.data.gremlin.object.jpa.domain.Location;
import org.springframework.data.gremlin.query.CompositeResult;
import org.springframework.data.gremlin.repository.GremlinRepositoryWithNativeSupport;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by gman on 12/06/15.
 */
public interface NativeLocationRepository extends GremlinRepositoryWithNativeSupport<Location> {

    @Query(value = "SELECT * FROM Location WHERE [latitude,longitude,$spatial] NEAR [?,?,{\"maxDistance\":?}]", nativeQuery = true)
    List<Location> find(double latitude, double longitude, double radius);

    @Query(value = "SELECT * FROM Location WHERE [latitude,longitude,$spatial] NEAR [:lat,:lon,{\"maxDistance\"::radius}]", nativeQuery = true)
    List<Location> findWithParam(@Param("lat") double latitude, @Param("lon") double longitude, @Param("radius") double radius);

    @Query(value = "SELECT * FROM Location WHERE [latitude,longitude,$spatial] NEAR [?,?,{\"maxDistance\":?}]", nativeQuery = true)
    Page<Location> find(double latitude, double longitude, double radius, Pageable pageable);

    @Query(value = "SELECT *,eval('1000d * $distance') as distance FROM Location WHERE [latitude,longitude,$spatial] NEAR [?,?,{\"maxDistance\":?}]", nativeQuery = true)
    List<Map<String, Object>> findNear(double latitude, double longitude, double radius);

    @Query(value = "SELECT *,eval('1000d * $distance') as distance FROM Location WHERE [latitude,longitude,$spatial] NEAR [?,?,{\"maxDistance\":?}]", nativeQuery = true)
    Page<Map<String, Object>> findNear(double latitude, double longitude, double radius, Pageable pageable);

    @Query(value = "SELECT *,eval('1000d * $distance') as distance FROM Location WHERE [latitude,longitude,$spatial] NEAR [?,?,{\"maxDistance\":?}]", nativeQuery = true)
    List<CompositeResult<Location>> findComposite(double latitude, double longitude, double radius);

    @Query(value = "SELECT *,eval('1000d * $distance') as distance FROM Location WHERE [latitude,longitude,$spatial] NEAR [?,?,{\"maxDistance\":?}]", nativeQuery = true)
    Page<CompositeResult<Location>> findComposite(double latitude, double longitude, double radius, Pageable pageable);

}

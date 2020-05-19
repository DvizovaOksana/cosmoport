package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;

public interface ShipService {
    Ship getById(Long id);

    Ship create(Ship ship);

    List<Ship> getAll(String name, String planet, ShipType shipType, Long after, Long before,
                      Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                      Integer maxCrewSize, Double minRating, Double maxRating,
                      String order, Integer pageNumber, Integer pageSize);

    Ship update(Ship ship, Long id);

    void delete(Long id);

    int getCount(String name, String planet, ShipType shipType, Long after, Long before,
                 Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                 Integer maxCrewSize, Double minRating, Double maxRating);
}

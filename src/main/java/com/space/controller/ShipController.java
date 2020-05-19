package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {
    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long shipId){
        if (shipId == null || shipId < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Ship ship = this.shipService.getById(shipId);

        if ( ship == null )
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ship>> getShipList(
            @RequestParam(value="name", required=false, defaultValue = "_") String name,
            @RequestParam(value="planet", required=false) String planet,
            @RequestParam(value="shipType", required=false) ShipType shipType,
            @RequestParam(value="after", required=false) Long after,
            @RequestParam(value="before", required=false) Long before,
            @RequestParam(value="isUsed", required=false) Boolean isUsed,
            @RequestParam(value="minSpeed", required=false) Double minSpeed,
            @RequestParam(value="maxSpeed", required=false) Double maxSpeed,
            @RequestParam(value="minCrewSize", required=false) Integer minCrewSize,
            @RequestParam(value="maxCrewSize", required=false) Integer maxCrewSize,
            @RequestParam(value="minRating", required=false) Double minRating,
            @RequestParam(value="maxRating", required=false) Double maxRating,
            @RequestParam(value="order", required=false, defaultValue = "id") String order,
            @RequestParam(value="pageNumber", required=false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value="pageSize", required=false, defaultValue = "3") Integer pageSize
    ){
        List<Ship> ships = shipService.getAll(name, planet, shipType,
                after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize,maxCrewSize,minRating,maxRating,
                order, pageNumber, pageSize);

        if(ships.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(ships, HttpStatus.OK);
    }

    @RequestMapping(value = "count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Integer> getCount(@RequestParam(value = "name", required = false, defaultValue = "_") String name,
                                         @RequestParam(value = "planet", required = false) String planet,
                                         @RequestParam(value = "shipType", required = false) ShipType shipType,
                                         @RequestParam(value = "after", required = false) Long after,
                                         @RequestParam(value = "before", required = false) Long before,
                                         @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                         @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                         @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                         @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                         @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                         @RequestParam(value = "minRating", required = false) Double minRating,
                                         @RequestParam(value = "maxRating", required = false) Double maxRating){
        return new ResponseEntity<>(shipService.getCount(name, planet, shipType,
                after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize,maxCrewSize,minRating,maxRating), HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship){
        Ship newShip = shipService.create(ship);
        if (newShip == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(newShip, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> deleteShip(@PathVariable("id") Long shipId){
        if (shipId == null || shipId < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Ship ship = shipService.getById(shipId);

        if (ship == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        shipService.delete(shipId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> updateShip(@PathVariable("id") Long shipId, @RequestBody Ship ship){
        if (shipId == null || shipId < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (shipService.getById(shipId) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Ship updatedShip = shipService.update(ship, shipId);

        if (updatedShip == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(updatedShip, HttpStatus.OK);
    }
}

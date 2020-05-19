package com.space.service;

import com.space.specification.SearchOperation;
import com.space.specification.ShipSpecification;
import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {
    private final ShipRepository shipRepository;
    private final static Integer PROD_YEAR_MIN = 2800;
    private final static Integer PROD_YEAR_MAX = 3019;

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship getById(Long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override
    public Ship create(Ship ship) {
        if (ship == null || ship.getName() == null || ship.getPlanet() == null
        || ship.getCrewSize() == null || ship.getProdDate() == null || ship.getShipType() == null
        || ship.getSpeed() == null)
            return null;

        if (ship.getName().isEmpty() || ship.getName().length() > 50)
            return null;

        if (ship.getPlanet().isEmpty() || ship.getPlanet().length() > 50)
            return null;

        int shipYear = ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();

        if (shipYear < PROD_YEAR_MIN || shipYear > PROD_YEAR_MAX )
            return null;

        if (ship.getSpeed() < 0.01d || ship.getSpeed() > 0.99d)
            return null;

        if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)
            return null;

        if (ship.getUsed() == null)
            ship.setUsed(false);

        setRating(ship);
        return shipRepository.save(ship);
    }

    @Override
    public List<Ship> getAll(String name, String planet, ShipType shipType, Long after, Long before,
                             Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                             Integer maxCrewSize, Double minRating, Double maxRating,
                             String order, Integer pageNumber, Integer pageSize){
        Specification<Ship> specification = getSpecification(name, planet, shipType, after, before, isUsed,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        try {
            order = ShipOrder.valueOf(order.toUpperCase()).getFieldName();
        } catch (Exception e) {
            order = "id";
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order).ascending());

        return shipRepository.findAll(specification, pageable).getContent();
    }

    @Override
    public Ship update(Ship ship, Long id) {
        Ship shipToUpdate = shipRepository.findById(id).orElse(null);

        if (shipToUpdate == null)
            return null;

        if (ship.getName() != null)
            shipToUpdate.setName(ship.getName());

        if (ship.getPlanet() != null)
            shipToUpdate.setPlanet(ship.getPlanet());

        if (ship.getShipType() != null)
            shipToUpdate.setShipType(ship.getShipType());

        if (ship.getProdDate() != null)
            shipToUpdate.setProdDate(ship.getProdDate());

        if (ship.getUsed() != null)
            shipToUpdate.setUsed(ship.getUsed());

        if (ship.getSpeed() != null)
            shipToUpdate.setSpeed(ship.getSpeed());

        if ( ship.getCrewSize() != null)
            shipToUpdate.setCrewSize(ship.getCrewSize());

        return create(shipToUpdate);
    }

    @Override
    public void delete(Long id) {
        shipRepository.deleteById(id);
    }

    @Override
    public int getCount(String name, String planet, ShipType shipType, Long after, Long before,
                        Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                        Integer maxCrewSize, Double minRating, Double maxRating) {
        Specification<Ship> specification = getSpecification(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating);
        return (int) shipRepository.count(specification);
    }

    private Specification<Ship> getSpecification(String name,String planet,ShipType shipType,Long after,Long before,Boolean isUsed,
                                                 Double minSpeed,Double maxSpeed,Integer minCrewSize,Integer maxCrewSize,
                                                 Double minRating,Double maxRating) {
        Specification<Ship> specification = new ShipSpecification("name", SearchOperation.CONTAINS, name);

        if (planet != null)
            specification = specification.and(new ShipSpecification("planet", SearchOperation.CONTAINS, planet));

        if (shipType != null)
            specification = specification.and(new ShipSpecification("shipType", SearchOperation.EQUAL, shipType));

        if (after != null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(after));
            specification = specification.and(new ShipSpecification("prodDate", SearchOperation.GE_DATE, cal.getTime()));
        }

        if (before != null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(before));
            specification = specification.and(new ShipSpecification("prodDate", SearchOperation.LE_DATE, cal.getTime()));
        }

        if (isUsed != null)
            specification = specification.and(new ShipSpecification("isUsed", SearchOperation.EQUAL, isUsed ? 1 : 0));

        if (minSpeed != null)
            specification = specification.and(new ShipSpecification("speed", SearchOperation.GE, minSpeed));

        if (maxSpeed != null)
            specification = specification.and(new ShipSpecification("speed", SearchOperation.LE, maxSpeed));

        if (minCrewSize != null)
            specification = specification.and(new ShipSpecification("crewSize", SearchOperation.GE, minCrewSize));

        if (maxCrewSize != null)
            specification = specification.and(new ShipSpecification("crewSize", SearchOperation.LE, maxCrewSize));

        if (minRating != null)
            specification = specification.and(new ShipSpecification("rating", SearchOperation.GE, minRating));

        if (maxRating != null)
            specification = specification.and(new ShipSpecification("rating", SearchOperation.LE, maxRating));

        return specification;
    }

    private void setRating(Ship ship){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(ship.getProdDate());
        int year = calendar.get(Calendar.YEAR);
        double rating = ( 80 * ship.getSpeed() * ( ship.getUsed() ? 0.5 : 1.0 ) ) / ( 3019 - year + 1 );
        BigDecimal bd = new BigDecimal(rating).setScale(2, RoundingMode.HALF_UP);
        ship.setRating(bd.doubleValue());
    }
}

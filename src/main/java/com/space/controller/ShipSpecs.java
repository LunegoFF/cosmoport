package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;

public class ShipSpecs {

    private ShipSpecs(){}

    private static String check(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        }
        else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }

    public static Specification<Ship> findByName(String name){
        return (r, cq, cb) -> cb.like(r.get("name"), check(name));
    }

    public static Specification<Ship> findByPlanet(String planet){
        return (r, cq, cb) -> cb.like(r.get("planet"), check(planet));
    }

    public static Specification<Ship> findByShipType(ShipType shipType) {
        if (shipType == null)
            return null;
        else
        switch(shipType){
            case TRANSPORT: return (r, cq, cb) -> cb.equal(r.<ShipType>get("shipType"), ShipType.TRANSPORT);
            case MILITARY: return (r, cq, cb) -> cb.equal(r.<ShipType>get("shipType"), ShipType.MILITARY);
            case MERCHANT: return (r, cq, cb) -> cb.equal(r.<ShipType>get("shipType"), ShipType.MERCHANT);
            default: return null;
        }
    }

    public static Specification<Ship> findByDate(Long after, Long before) {
        return (r, cq, cb) -> cb.between(r.<Date>get("prodDate"), new Date(after), new Date(before));
    }

    public static Specification<Ship> findByUsage(Boolean isUsed) {
        if (isUsed == null)
            return null;
        else
            return (r, cq, cb) -> cb.equal(r.<Boolean>get("isUsed"), isUsed);
    }

    public static Specification<Ship> findBySpeed(Double minSpeed, Double maxSpeed) {
        return (r, cq, cb) -> cb.between(r.<Double>get("speed"), minSpeed, maxSpeed);
    }

    public static Specification<Ship> findByCrewSize(Integer minCrewSize, Integer maxCrewSize) {
        return (r, cq, cb) -> cb.between(r.<Integer>get("crewSize"), minCrewSize, maxCrewSize);
    }

    public static Specification<Ship> findByRating(Double minRating, Double maxRating) {
        return (r, cq, cb) -> cb.between(r.<Double>get("rating"), minRating, maxRating);
    }


}

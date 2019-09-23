package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {

    private final ShipService shipService;

    ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping
    public ResponseEntity<List<Ship>> getShips(@RequestParam(name="name", required = false) String name,
                                               @RequestParam(name="planet", required = false) String planet,
                                               @RequestParam(name="shipType", required = false) ShipType shipType,
                                               @RequestParam(name="after", required = false) Long after,
                                               @RequestParam(name="before", required = false) Long before,
                                               @RequestParam(name="isUsed", required = false) Boolean isUsed,
                                               @RequestParam(name="minSpeed", required = false) Double minSpeed,
                                               @RequestParam(name="maxSpeed", required = false) Double maxSpeed,
                                               @RequestParam(name="minCrewSize", required = false) Integer minCrewSize,
                                               @RequestParam(name="maxCrewSize", required = false) Integer maxCrewSize,
                                               @RequestParam(name="minRating", required = false) Double minRating,
                                               @RequestParam(name="maxRating", required = false) Double maxRating,
                                               @RequestParam(name="order", required = false) ShipOrder order,
                                               @RequestParam(name="pageNumber", required = false) Integer pageNumber,
                                               @RequestParam(name="pageSize", required = false) Integer pageSize) {

        if (pageNumber == null)
            pageNumber = 0;

        if (pageSize == null)
            pageSize = 3;

        if (order == null)
            order = ShipOrder.ID;

        if (after == null)
            after = new Date(900, 0, 1).getTime();

        if (before == null)
            before = new Date(1120, 0, 1).getTime();

        if (minSpeed == null)
            minSpeed = 0.01;

        if (maxSpeed == null)
            maxSpeed = 0.99;

        if (minRating == null)
            minRating = 0.01;

        if (maxRating == null)
            maxRating = 80.0;

        if (minCrewSize == null)
            minCrewSize = 1;

        if (maxCrewSize == null)
            maxCrewSize = 9999;

        Pageable page = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        Specification<Ship> spec = Specification.where(ShipSpecs.findByPlanet(planet))
                .and(ShipSpecs.findByName(name))
                .and(ShipSpecs.findBySpeed(minSpeed, maxSpeed))
                .and(ShipSpecs.findByCrewSize(minCrewSize, maxCrewSize))
                .and(ShipSpecs.findByRating(minRating, maxRating))
                .and(ShipSpecs.findByDate(after, before))
                .and(ShipSpecs.findByUsage(isUsed))
                .and(ShipSpecs.findByShipType(shipType));
        Page<Ship> ships = shipService.findAll(spec, page);
        return ResponseEntity.ok().body(ships.getContent());
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCount(@RequestParam(name="name", required = false) String name,
                                            @RequestParam(name="planet", required = false) String planet,
                                            @RequestParam(name="shipType", required = false) ShipType shipType,
                                            @RequestParam(name="after", required = false) Long after,
                                            @RequestParam(name="before", required = false) Long before,
                                            @RequestParam(name="isUsed", required = false) Boolean isUsed,
                                            @RequestParam(name="minSpeed", required = false) Double minSpeed,
                                            @RequestParam(name="maxSpeed", required = false) Double maxSpeed,
                                            @RequestParam(name="minCrewSize", required = false) Integer minCrewSize,
                                            @RequestParam(name="maxCrewSize", required = false) Integer maxCrewSize,
                                            @RequestParam(name="minRating", required = false) Double minRating,
                                            @RequestParam(name="maxRating", required = false) Double maxRating) {

        if (after == null) after = new Date(900, 0, 1).getTime();
        if (before == null) before = new Date(1120, 0, 1).getTime();
        if (minSpeed == null) minSpeed = 0.01;
        if (maxSpeed == null) maxSpeed = 0.99;
        if (minRating == null) minRating = 0.01;
        if (maxRating == null) maxRating = 80.0;
        if (minCrewSize == null) minCrewSize = 1;
        if (maxCrewSize == null) maxCrewSize = 9999;

        Specification<Ship> spec = Specification.where(ShipSpecs.findByName(name))
                .and(ShipSpecs.findByPlanet(planet))
                .and(ShipSpecs.findByDate(after, before))
                .and(ShipSpecs.findBySpeed(minSpeed, maxSpeed))
                .and(ShipSpecs.findByCrewSize(minCrewSize, maxCrewSize))
                .and(ShipSpecs.findByRating(minRating, maxRating))
                .and(ShipSpecs.findByShipType(shipType))
                .and(ShipSpecs.findByUsage(isUsed));
        return ResponseEntity.ok().body(shipService.findAll(spec).size());
    }

    @GetMapping("{id}")
    public ResponseEntity<Ship> findById(@PathVariable String id){
        if (id.matches("\\d+")){
            if (Long.parseLong(id) <= 0)
                return ResponseEntity.badRequest().build();
            if (shipService.findById(Long.parseLong(id)).isPresent())
                return ResponseEntity.ok().body(shipService.findById(Long.parseLong(id)).get());
            else
                return ResponseEntity.notFound().build();
        }else
            return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public ResponseEntity<Ship> create(@RequestBody Map<String, String> ships){

        if (ships.isEmpty())
            return ResponseEntity.badRequest().build();

        String isUsedStr = ships.get("isUsed");
        boolean isUsed = false;
        double rate = 1.0;
        if (isUsedStr != null)
            switch(isUsedStr) {
                case "true":
                    isUsed = true;
                    rate = 0.5;
                    break;
                case "false": break;
                default: return ResponseEntity.badRequest().build();
            }

        String name = ships.get("name");
        if (name == null) return ResponseEntity.badRequest().build();
        if (name.length() >= 50 || name.equals(""))
            return ResponseEntity.badRequest().build();

        String planet = ships.get("planet");
        if (planet == null) return ResponseEntity.badRequest().build();
        if (planet.length() >= 50 || planet.equals(""))
            return ResponseEntity.badRequest().build();

        String shipTypeStr = ships.get("shipType");
        ShipType shipType;
        switch(shipTypeStr) {
            case "MILITARY": shipType = ShipType.MILITARY; break;
            case "MERCHANT": shipType = ShipType.MERCHANT; break;
            case "TRANSPORT": shipType = ShipType.TRANSPORT; break;
            default:
                return ResponseEntity.badRequest().build();
        }

        String prodDateStr = ships.get("prodDate");
        long prodDate = 0L;
        int year = 0;
        if (prodDateStr != null){
            prodDate = Long.parseLong(prodDateStr);
            if (prodDate <= 0)
                return ResponseEntity.badRequest().build();
            year = new Date(prodDate).getYear() + 1900;
            if (year < 2800 || year > 3019)
                return ResponseEntity.badRequest().build();
        }else
            return ResponseEntity.badRequest().build();

        String speedStr = ships.get("speed");
        double speed = 0.0, spid = 0.0;
        if (speedStr != null) {
            speed = Double.valueOf(speedStr);
            spid = Math.round(speed * 100)/100.0;
            if (spid <= 0 || spid >= 1)
                return ResponseEntity.badRequest().build();
        }
        else
            return ResponseEntity.badRequest().build();

        String crewSizeStr = ships.get("crewSize");
        int crewSize = 0;
        if (crewSizeStr != null){
            crewSize = Integer.valueOf(crewSizeStr);
            if (crewSize < 0 || crewSize > 10000)
                return ResponseEntity.badRequest().build();
        }
        else
            return ResponseEntity.badRequest().build();

        double rating = Math.round(80 * spid * rate / (3019 - year + 1) * 100)/100.0;

        return ResponseEntity.ok().body(shipService.create(new Ship(name, planet, shipType, new Date(prodDate), isUsed, speed, crewSize, rating)));

    }

    @PostMapping("{id}")
    public ResponseEntity<Ship> update(@PathVariable String id,
                                       @RequestBody Map<String, String> ships){


        if (id.matches("\\d+")){
            if (Long.parseLong(id) <= 0)
                return ResponseEntity.badRequest().build();

            Optional<Ship> ship = shipService.findById(Long.parseLong(id));
            String name = ships.get("name");
            String planet = ships.get("planet");
            String shipTypeStr = ships.get("shipType");
            String prodDateStr = ships.get("prodDate");
            String speedStr = ships.get("speed");
            String crewSizeStr = ships.get("crewSize");
            String isUsedStr = ships.get("isUsed");


            double rate = 1.0;
            boolean isSame = true;
            if (ship.isPresent()){
                Ship updateShip = ship.get();

                if (name != null)
                    if (name.length() <= 50 && !name.equals("")) {
                        updateShip.setName(name);
                        isSame = false;
                    }
                    else
                        return ResponseEntity.badRequest().build();

                if (planet != null)
                    if (planet.length() <= 50 && !planet.equals("")){
                        updateShip.setPlanet(planet);
                        isSame = false;
                    }
                    else
                        return ResponseEntity.badRequest().build();

                if (shipTypeStr != null)
                    switch(shipTypeStr){
                        case "MILITARY": updateShip.setShipType(ShipType.MILITARY); isSame = false; break;
                        case "MERCHANT": updateShip.setShipType(ShipType.MERCHANT); isSame = false; break;
                        case "TRANSPORT": updateShip.setShipType(ShipType.TRANSPORT); isSame = false; break;
                        default: return ResponseEntity.badRequest().build();
                    }

                if (isUsedStr != null) {
                    switch(isUsedStr) {
                        case "true":
                            updateShip.setUsed(true);
                            rate = 0.5;
                            isSame = false;
                            break;
                        case "false":
                            updateShip.setUsed(false);
                            isSame = false;
                            break;
                        default: return ResponseEntity.badRequest().build();
                    }

                }

                if (crewSizeStr != null) {
                    int crewSize = Integer.parseInt(crewSizeStr);
                    if (crewSize > 0 && crewSize < 10000){
                        updateShip.setCrewSize(crewSize);
                        isSame = false;
                    }
                    else
                        return ResponseEntity.badRequest().build();
                }

                double spid;
                if (speedStr != null) {
                    double speed = Double.parseDouble(speedStr);
                    spid = Math.round(speed * 100)/100.0;
                    if (spid > 0 && spid < 1){
                        updateShip.setSpeed(speed);
                        isSame = false;
                    }
                    else
                        return ResponseEntity.badRequest().build();
                }else
                    spid = updateShip.getSpeed();

                int year;
                if (prodDateStr != null) {
                    long prodDate = Long.parseLong(prodDateStr);
                    Date date = new Date(prodDate);
                    year = date.getYear() + 1900;
                    if (year >= 2800 && year <= 3019){
                        updateShip.setProdDate(date);
                        isSame = false;
                    }
                    else
                        return ResponseEntity.badRequest().build();
                }else
                    year = updateShip.getProdDate().getYear() + 1900;

                if (!isSame) {
                    double rating = 80 * spid * rate / (3019 - year + 1);
                    updateShip.setRating(Math.round(rating * 100)/100.0);
                }

                return ResponseEntity.ok().body(shipService.update(updateShip));

            }
            else
                return ResponseEntity.notFound().build();
        }else
            return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id){
        if (id.matches("\\d+")){
            if (Long.parseLong(id) <= 0)
                return ResponseEntity.badRequest().build();
            if (shipService.deleteById(Long.parseLong(id)))
                return ResponseEntity.ok().build();
            else
                return ResponseEntity.notFound().build();
        }else
            return ResponseEntity.badRequest().build();
    }
}

package com.space.service;

import com.space.model.Ship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface ShipService {

    Page<Ship> findAll(Specification<Ship> spec, Pageable page);
    List<Ship> findAll(Specification<Ship> spec);
    Ship create(Ship ship);
    Ship update(Ship ship);
    Long getCount();
    Optional<Ship> findById(Long id);
    boolean deleteById(Long id);
}

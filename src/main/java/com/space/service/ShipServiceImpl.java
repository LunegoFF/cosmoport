package com.space.service;

import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShipServiceImpl implements ShipService{

    @Autowired
    private ShipRepository shipRepository;

    @Override
    public Page<Ship> findAll(Specification spec, Pageable page) {
        return shipRepository.findAll(spec, page);
    }

    @Override
    public List<Ship> findAll(Specification<Ship> spec) {
        return shipRepository.findAll(spec);
    }

    @Override
    public Ship create(Ship ship) {
        return shipRepository.saveAndFlush(ship);
    }

    @Override
    public Ship update(Ship ship) {
        return shipRepository.saveAndFlush(ship);
    }

    @Override
    public Long getCount() {
        return shipRepository.count();
    }

    @Override
    public Optional<Ship> findById(Long id){
        if (shipRepository.existsById(id)) {
            return shipRepository.findById(id);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(Long id){
        if (shipRepository.existsById(id)) {
            shipRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

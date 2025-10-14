package io.hellorin.boathub.service;

import io.hellorin.boathub.repository.BoatRepository;
import io.hellorin.boathub.mapper.BoatMapper;
import org.springframework.stereotype.Service;

@Service
public class BoatService {

    private final BoatMapper boatMapper;

    private final BoatRepository boatRepository;

    public BoatService(BoatMapper boatMapper, BoatRepository boatRepository) {
        this.boatMapper = boatMapper;
        this.boatRepository = boatRepository;
    }
}

package io.hellorin.boathub.controller;

import io.hellorin.boathub.service.BoatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/boats")
public class BoatV1Controller {

    private final BoatService boatService;

    public BoatV1Controller(BoatService boatService) {
        this.boatService = boatService;
    }
}

package com.wowtracker.web;

import com.wowtracker.domain.entity.Region;
import com.wowtracker.service.RealmService;
import com.wowtracker.service.dto.RealmSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RealmController {

    private final RealmService realmService;

    public RealmController(RealmService service){
        this.realmService = service;
    }

    @GetMapping("/realms")
    public List<RealmSummary> listRealms(@RequestParam Region region){
        return realmService.listRealms(region);
    }
}

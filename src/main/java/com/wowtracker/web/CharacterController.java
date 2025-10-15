package com.wowtracker.web;

import com.wowtracker.domain.entity.Region;
import com.wowtracker.service.CharacterService;
import com.wowtracker.web.dto.CharacterResponse;
import com.wowtracker.web.dto.CreateCharacterRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@RequestMapping("/characters")
public class CharacterController {

    private static final int MAX_PAGE = 100;
    private final CharacterService service;

    public CharacterController(CharacterService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateCharacterRequest request) {
        boolean created = service.addIfNotExists(request.region(), request.realmSlug(), request.name());
        if (created) {
            String location = String.format("/characters/find?region=%s&realmSlug=%s&name=%s",
                    request.region(), request.realmSlug(), request.name());
            return ResponseEntity.created(URI.create(location)).build();
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Character already exists");
        }
    }

    @GetMapping("/find")
    public ResponseEntity<CharacterResponse> find(
            @RequestParam Region region,
            @RequestParam String realmSlug,
            @RequestParam String name) {
        return service.findByKey(region, realmSlug, name)
                .map(character -> ResponseEntity.ok(CharacterResponse.from(character)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Character not found"));
    }

    @GetMapping
    public Page<CharacterResponse> list(@RequestParam Region region,
                                        @RequestParam String realmSlug,
                                        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC)Pageable pageable){
        if (pageable.getPageSize() > MAX_PAGE){
            pageable = PageRequest.of(pageable.getPageNumber(), MAX_PAGE, pageable.getSort());
        }
        var page = service.list(region, realmSlug, pageable);

        return page.map(CharacterResponse::from);
    }
}

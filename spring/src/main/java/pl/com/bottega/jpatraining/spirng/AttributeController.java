package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
@Log
class AttributeController {

    private final EntityManager entityManager;

    @PostMapping
    public void createAttribute(@RequestBody AttributeDataRequest request) {

    }

    @PutMapping("/{id}")
    public void updateAttribute(@PathVariable UUID id, @RequestBody AttributeDataRequest request) {

    }

    record AttributeDataRequest(String name) {}

}

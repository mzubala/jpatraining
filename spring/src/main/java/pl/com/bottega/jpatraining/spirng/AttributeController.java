package pl.com.bottega.jpatraining.spirng;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
@Log
class AttributeController {

    private final AttributeRepository attributeRepository;

    @PostMapping
    //@Transactional
    public void createAttribute(@RequestBody AttributeDataRequest request) {
        attributeRepository.save(new Attribute(request.name()));
    }

    @PutMapping("/{id}")
    @Transactional
    public void updateAttribute(@PathVariable UUID id, @RequestBody AttributeDataRequest request) {
        var attribute = attributeRepository.findById(id).get();
        attribute.setName(request.name());
    }

    @GetMapping
    //@Transactional(readOnly = true)
    public List<Attribute> getAttributes() {
        return attributeRepository.findAll();
    }

    record AttributeDataRequest(String name) {}

}

package pl.com.bottega.jpatraining.spirng;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface AttributeRepository extends JpaRepository<Attribute, UUID> {

}

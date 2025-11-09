package org.example.edufypodproducerservice.repositories;

import org.example.edufypodproducerservice.entities.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, UUID> {
    Optional<Producer> findByPodcastsContaining(UUID podcast);

}

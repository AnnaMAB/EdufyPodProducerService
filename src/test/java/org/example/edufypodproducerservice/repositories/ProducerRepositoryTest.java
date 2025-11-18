package org.example.edufypodproducerservice.repositories;

import org.example.edufypodproducerservice.entities.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ProducerRepositoryTest {

    @Autowired
    private ProducerRepository producerRepository;

    private Producer producer1;
    private Producer producer2;
    UUID podcastId1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    UUID podcastId2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    UUID podcastId3 = UUID.fromString("00000000-0000-0000-0000-000000000003");

    @BeforeEach
    void setUp() {
        producerRepository.deleteAll();

        producer1 = new Producer();
        producer1.setName("Producer One");
        producer1.getPodcasts().add(podcastId1);
        producer1.getPodcasts().add(podcastId2);

        producer2 = new Producer();
        producer2.setName("Producer Two");
        producer2.getPodcasts().add(podcastId3);

        producerRepository.saveAll(List.of(producer1, producer2));
    }

    @Test
    void testFindByPodcastsContaining() {
        Optional<Producer> pOne = producerRepository.findByPodcastsContaining(podcastId1);

        assertThat(pOne).isPresent();
        assertThat(pOne.get().getName()).isEqualTo("Producer One");
        assertThat(pOne.get().getPodcasts().size()).isEqualTo(2);
        assertThat(pOne.get().getPodcasts().contains(podcastId1));
    }
}
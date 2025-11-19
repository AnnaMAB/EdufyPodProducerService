package org.example.edufypodproducerservice.repositories;

import org.example.edufypodproducerservice.entities.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestPropertySource(properties = {"spring.sql.init.mode=never"})
@DataJpaTest
class ProducerRepositoryTest {

    @Autowired
    private ProducerRepository producerRepository;

    private Producer producer1;
    private Producer producer2;
    UUID podcastId1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    UUID podcastId2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    UUID podcastId3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
    UUID podcastId4 = UUID.fromString("00000000-0000-0000-0000-000000000004");

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

    @Test
    void testFindByPodcastsNotContaining() {
        Optional<Producer> pOne = producerRepository.findByPodcastsContaining(podcastId4);

        assertThat(pOne).isNotPresent();
    }


    @Test
    void testFindByName() {
        Optional<Producer> result = producerRepository.findByName("Producer One");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Producer One");
    }

    @Test
    void testFindByName_NotFound() {
        Optional<Producer> result = producerRepository.findByName("Unknown Producer");

        assertThat(result).isNotPresent();
    }


}
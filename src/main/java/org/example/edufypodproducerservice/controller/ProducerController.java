package org.example.edufypodproducerservice.controller;

import org.example.edufypodproducerservice.dto.ProducerDto;
import org.example.edufypodproducerservice.entities.Producer;
import org.example.edufypodproducerservice.services.ProducerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/podcasts")
public class ProducerController {

    private final ProducerServiceImpl producerService;

    @Autowired
    public ProducerController(ProducerServiceImpl producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/addproducer")
    public ResponseEntity<Producer> addProducer(@RequestBody ProducerDto producerDto) {
        return ResponseEntity.ok(producerService.addProducer(producerDto));
    }

    @PutMapping("/updateproducer")
    public ResponseEntity<Producer> updateProducer(@RequestBody ProducerDto producerDto) {
        return ResponseEntity.ok(producerService.updateProducer(producerDto));
    }

    @DeleteMapping("/deleteproducer/{producerId}")
    public ResponseEntity<String> deleteProducer(@PathVariable UUID producerId) {
        return ResponseEntity.ok(producerService.deleteProducer(producerId));
    }

    @GetMapping("/producerfull/{producerId}")
    public ResponseEntity<ProducerDto> getFullProducer(@PathVariable UUID producerId) {
        return ResponseEntity.ok(producerService.getProducer(producerId, true));
    }

    @GetMapping("/producerlimited/{producerId}")
    public ResponseEntity<ProducerDto> getLimitedProducer(@PathVariable UUID producerId) {
        return ResponseEntity.ok(producerService.getProducer(producerId, false));
    }

    @GetMapping("/allproducersfull")
    public ResponseEntity<List<ProducerDto>> getAllFullProducers(boolean full) {
        return ResponseEntity.ok(producerService.getAllProducers(true));
    }

    @GetMapping("/allproducerslimited")
    public ResponseEntity<List<ProducerDto>> getAllLimitedProducers() {
        return ResponseEntity.ok(producerService.getAllProducers(false));
    }

    @GetMapping("/producerbypodcastfull/{podcastId}")
    public ResponseEntity<ProducerDto> getFullProducerByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(producerService.getProducerByPodcastId(podcastId, true));
    }

    @GetMapping("/producerbypodcastlimited/{podcastId}")
    public ResponseEntity<ProducerDto> getLimitedProducerByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(producerService.getProducerByPodcastId(podcastId, false));
    }

    @PostMapping("/{ProducerId}/addepisodes/{PodcastId}")
    public ResponseEntity<ProducerDto> addOneEpisodeToSeason(@PathVariable UUID ProducerId,@PathVariable UUID PodcastId) {
        return ResponseEntity.ok(producerService.addPodcastToProducer(ProducerId, PodcastId));
    }

    @DeleteMapping("/{ProducerId}/removeepisodes/{podcastId}")
    public ResponseEntity<ProducerDto> removeOneEpisodeFromSeason(@PathVariable UUID ProducerId, @PathVariable UUID PodcastId) {
        return ResponseEntity.ok(producerService.removePodcastFromProducer(ProducerId, PodcastId));
    }

}

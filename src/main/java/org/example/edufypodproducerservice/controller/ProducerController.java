package org.example.edufypodproducerservice.controller;

import org.example.edufypodproducerservice.dto.ProducerDto;
import org.example.edufypodproducerservice.entities.Producer;
import org.example.edufypodproducerservice.services.ProducerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/pods/producers")
public class ProducerController {

    private final ProducerServiceImpl producerService;

    @Autowired
    public ProducerController(ProducerServiceImpl producerService) {
        this.producerService = producerService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addproducer")
    public ResponseEntity<Producer> addProducer(@RequestBody ProducerDto producerDto) {
        return ResponseEntity.ok(producerService.addProducer(producerDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateproducer")
    public ResponseEntity<Producer> updateProducer(@RequestBody ProducerDto producerDto) {
        return ResponseEntity.ok(producerService.updateProducer(producerDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteproducer/{producerId}")
    public ResponseEntity<String> deleteProducer(@PathVariable UUID producerId) {
        return ResponseEntity.ok(producerService.deleteProducer(producerId));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/producerfull/{producerId}")
    public ResponseEntity<ProducerDto> getFullProducer(@PathVariable UUID producerId) {
        return ResponseEntity.ok(producerService.getProducer(producerId, true));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/producerlimited/{producerId}")
    public ResponseEntity<ProducerDto> getLimitedProducer(@PathVariable UUID producerId) {
        return ResponseEntity.ok(producerService.getProducer(producerId, false));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/allproducersfull")
    public ResponseEntity<List<ProducerDto>> getAllFullProducers(boolean full) {
        return ResponseEntity.ok(producerService.getAllProducers(true));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/allproducerslimited")
    public ResponseEntity<List<ProducerDto>> getAllLimitedProducers() {
        return ResponseEntity.ok(producerService.getAllProducers(false));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/producerbypodcastfull/{podcastId}")
    public ResponseEntity<ProducerDto> getFullProducerByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(producerService.getProducerByPodcastId(podcastId, true));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/producerbypodcastlimited/{podcastId}")
    public ResponseEntity<ProducerDto> getLimitedProducerByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(producerService.getProducerByPodcastId(podcastId, false));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{producerId}/addpodcast/{podcastId}")
    public ResponseEntity<ProducerDto> addPodcastToProducer(@PathVariable UUID producerId,@PathVariable UUID podcastId) {
        return ResponseEntity.ok(producerService.addPodcastToProducer(producerId, podcastId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{producerId}/removepodcast/{podcastId}")
    public ResponseEntity<Boolean> removePodcastFromProducer(@PathVariable UUID producerId, @PathVariable UUID podcastId) {
        return ResponseEntity.ok(producerService.removePodcastFromProducer(producerId, podcastId));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> podcastExists(@PathVariable UUID id) {
        return ResponseEntity.ok(producerService.producerExist(id));
    }


}

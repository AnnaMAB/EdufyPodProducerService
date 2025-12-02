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

    @PreAuthorize("hasRole('edufy_Admin')")
    @PostMapping("/addproducer")
    public ResponseEntity<Producer> addProducer(@RequestBody ProducerDto producerDto) {
        return ResponseEntity.ok(producerService.addProducer(producerDto));
    }

    @PreAuthorize("hasRole('edufy_Admin')")
    @PutMapping("/updateproducer")
    public ResponseEntity<Producer> updateProducer(@RequestBody ProducerDto producerDto) {
        return ResponseEntity.ok(producerService.updateProducer(producerDto));
    }

    @PreAuthorize("hasRole('edufy_Admin')")
    @DeleteMapping("/deleteproducer/{producerId}")
    public ResponseEntity<String> deleteProducer(@PathVariable UUID producerId) {
        return ResponseEntity.ok(producerService.deleteProducer(producerId));
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/producerfull/{producerId}")
    public ResponseEntity<ProducerDto> getFullProducer(@PathVariable UUID producerId) {
        return ResponseEntity.ok(producerService.getProducer(producerId, true));
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/producerlimited/{producerId}")
    public ResponseEntity<ProducerDto> getLimitedProducer(@PathVariable UUID producerId) {
        return ResponseEntity.ok(producerService.getProducer(producerId, false));
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/allproducersfull")
    public ResponseEntity<List<ProducerDto>> getAllFullProducers(boolean full) {
        return ResponseEntity.ok(producerService.getAllProducers(true));
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/allproducerslimited")
    public ResponseEntity<List<ProducerDto>> getAllLimitedProducers() {
        return ResponseEntity.ok(producerService.getAllProducers(false));
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/producerbypodcastfull/{podcastId}")
    public ResponseEntity<ProducerDto> getFullProducerByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(producerService.getProducerByPodcastId(podcastId, true));
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/producerbypodcastlimited/{podcastId}")
    public ResponseEntity<ProducerDto> getLimitedProducerByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(producerService.getProducerByPodcastId(podcastId, false));
    }

    @PreAuthorize("hasRole('edufy_Admin')")
    @PutMapping("/{producerId}/addpodcast/{podcastId}")
    public ResponseEntity<ProducerDto> addPodcastToProducer(@PathVariable UUID producerId,@PathVariable UUID podcastId) {
        return ResponseEntity.ok(producerService.addPodcastToProducer(producerId, podcastId));
    }

    @PreAuthorize("hasRole('edufy_Admin')")
    @PutMapping("/{producerId}/removepodcast/{podcastId}")
    public ResponseEntity<Boolean> removePodcastFromProducer(@PathVariable UUID producerId, @PathVariable UUID podcastId) {
        return ResponseEntity.ok(producerService.removePodcastFromProducer(producerId, podcastId));
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> podcastExists(@PathVariable UUID id) {
        return ResponseEntity.ok(producerService.producerExist(id));
    }


}

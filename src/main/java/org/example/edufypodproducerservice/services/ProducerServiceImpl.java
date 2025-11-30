package org.example.edufypodproducerservice.services;


import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.edufypodproducerservice.converters.UserInfo;
import org.example.edufypodproducerservice.dto.ProducerDto;
import org.example.edufypodproducerservice.entities.Producer;
import org.example.edufypodproducerservice.external.PodcastApiClient;
import org.example.edufypodproducerservice.mapper.ProducerDtoConverter;
import org.example.edufypodproducerservice.repositories.ProducerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class ProducerServiceImpl implements ProducerService {

    private final ProducerRepository producerRepository;
    private final ProducerDtoConverter producerDtoConverter;
    private final PodcastApiClient podcastApiClient;
    private final UserInfo userInfo;
    private static final Logger F_LOG = LogManager.getLogger("functionality");


    @Autowired
    public ProducerServiceImpl(ProducerRepository producerRepository, ProducerDtoConverter producerDtoConverter,
                               PodcastApiClient podcastApiClient, UserInfo userInfo) {
        this.producerRepository = producerRepository;
        this.producerDtoConverter = producerDtoConverter;
        this.podcastApiClient = podcastApiClient;
        this.userInfo = userInfo;
    }

    @Transactional
    @Override
    public Producer addProducer(ProducerDto producerDto) {
        String role = userInfo.getRole();
        Producer producer = new Producer();
        if (producerDto.getName() == null || producerDto.getName().isBlank()) {
            F_LOG.warn("{} tried to add a producer without a name.", role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if(producerRepository.findByName(producerDto.getName()).isPresent()){
            F_LOG.warn("{} tried to add a producer with a name that already exists.", role);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A producer with that name already exists");
        }
        if (producerDto.getDescription() == null || producerDto.getDescription().isBlank()) {
            F_LOG.warn("{} tried to add a producer without a description.", role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is required");
        }
        if (producerDto.getImageUrl() != null && !producerDto.getImageUrl().isBlank()) {
            producer.setImageUrl(producerDto.getImageUrl());
        }else {
            producer.setImageUrl("https://default/image.url");
        }
        if (producerDto.getThumbnailUrl() != null && !producerDto.getThumbnailUrl().isBlank()) {
            producer.setThumbnailUrl(producerDto.getThumbnailUrl());
        }else {
            producer.setThumbnailUrl("https://default/thumbnail.url");
        }
        if (producerDto.getPodcasts() != null && !producerDto.getPodcasts().isEmpty()) {
            F_LOG.warn("{} tried to add podcast to a producer from the wrong endpoint.", role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Podcasts can't be added from this endpoint");
        }
        producer.setName(producerDto.getName());
        producer.setDescription(producerDto.getDescription());

        Producer savedProducer = producerRepository.save(producer);
        F_LOG.info("{} added a producer with id {}.", role, savedProducer.getId());
        return savedProducer;
    }

    @Transactional
    @Override
    public Producer updateProducer(ProducerDto producerDto) {
        String role = userInfo.getRole();
        if(producerDto.getId() == null) {
            F_LOG.warn("{} tried to retrieve a producer without providing an id.", role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producer id is required");
        }
        Producer producer = producerRepository.findById(producerDto.getId()).orElseThrow(() -> {
            F_LOG.warn("{} tried to retrieve a producer with id {} that doesn't exist.", role, producerDto.getId());
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No producer exists with id: %s.", producerDto.getId())
            );
        });
        if (producerDto.getName() != null && !producerDto.getName().equals(producer.getName())) {
            if(producerDto.getName().isBlank()) {
                F_LOG.warn("{} tried to update a producer with invalid name.", role);
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Name can not be left blank."
                );
            }
            if(producerRepository.findByName(producerDto.getName()).isPresent()){
                F_LOG.warn("{} tried to change producer name to a name that already exist.", role);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "A producer with that name already exists");
            }
            producer.setName(producerDto.getName());
        }
        if (producerDto.getDescription() != null && !producerDto.getDescription().equals(producer.getDescription())) {
            if(producerDto.getDescription().isBlank()) {
                F_LOG.warn("{} tried to update a producer with invalid description.", role);
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Description can not be left blank."
                );
            }
            producer.setDescription(producerDto.getDescription());
        }
        if (producerDto.getImageUrl() != null && !producerDto.getImageUrl().equals(producer.getImageUrl())) {
            producer.setImageUrl(producerDto.getImageUrl());
        }
        if (producerDto.getThumbnailUrl() != null && !producerDto.getThumbnailUrl().equals(producer.getThumbnailUrl())) {
            producer.setThumbnailUrl(producerDto.getThumbnailUrl());
        }
        if (producerDto.getPodcasts() != null && !producerDto.getPodcasts().isEmpty() && !producerDto.getPodcasts().equals(producer.getPodcasts())) {
            F_LOG.warn("{} tried to add podcast to a producer from the wrong endpoint.", role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Podcasts can't be added from this endpoint");
        }

        F_LOG.info("{} updated producer with id {}.", role, producer.getId());
        return producerRepository.save(producer);
    }

    @Transactional
    @Override
    public String deleteProducer(UUID producerId) {
        String role = userInfo.getRole();
        if (producerId == null) {
            F_LOG.warn("{} tried to delete a producer without providing an id.", role);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Id must be provided"
            );
        }
        Producer producer = producerRepository.findById(producerId).orElseThrow(() -> {
            F_LOG.warn("{} tried to delete a producer with id {} that doesn't exist.", role, producerId);
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No producer exists with id: %s.", producerId)
            );
        });
        List<UUID> podcasts = producer.getPodcasts();
        if (!podcasts.isEmpty()) {
            Boolean exists;
            for (UUID podcastId : podcasts) {
                exists = podcastApiClient.podcastAssociatedWithProducer(podcastId, producerId);
                if (exists) {
                    F_LOG.warn("{} tried to delete a producer with id {} with podcasts still associated.", role, producerId);
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT, String.format("Producer can't be deleted while podcasts are still associated.")
                    );
                }
            }
        }

        producerRepository.deleteById(producerId);
        F_LOG.info("{} deleted producer with id: {}", role, producerId);
        return String.format("Producer with Id: %s have been successfully deleted.", producerId);
    }

    @Override
    public ProducerDto getProducer(UUID producerId, boolean full) {
        String role = userInfo.getRole();
        if (producerId == null) {
            F_LOG.warn("{} tried to retrieve a producer without providing an id.", role);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Id must be provided"
            );
        }
        Producer producer = producerRepository.findById(producerId).orElseThrow(() -> {
            F_LOG.warn("{} tried to retrieve a producer with id {} that doesn't exist.", role, producerId);
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No producer exists with id: %s.", producerId)
            );
        });
        F_LOG.info("{} retrieved producer with id {}.", role, producerId);
        if (full) {
            return producerDtoConverter.producerFullDtoConvert(producer);
        }else {
            return producerDtoConverter.producerLimitedDtoConvert(producer);
        }
    }

    @Override
    public List<ProducerDto> getAllProducers(boolean full) {
        String role = userInfo.getRole();
        List<Producer> producers = producerRepository.findAll();
        List<ProducerDto> producerDtos = new ArrayList<>();
        if (full) {
            for (Producer producer : producers) {
                producerDtos.add(producerDtoConverter.producerFullDtoConvert(producer));
            }
        }else{
            for (Producer producer : producers) {
                producerDtos.add(producerDtoConverter.producerLimitedDtoConvert(producer));
            }
        }
        F_LOG.info("{} retrieved all producers.", role);
        return producerDtos;
    }

    @Override
    public ProducerDto getProducerByPodcastId(UUID podcastId, boolean full) {
        String role = userInfo.getRole();
        if (podcastId == null) {
            F_LOG.warn("{} tried to retrieve a producer without providing an PodcastId.", role);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,"PodcastId must be provided"
            );
        }
        Producer producer = producerRepository.findByPodcastsContaining(podcastId).orElseThrow(() -> {
            F_LOG.warn("{} tried to retrieve a producer containing podcast id {} that doesn't exist.", role, podcastId);
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No producer exists for podcast with id: %s.", podcastId)
            );
        });
        F_LOG.info("{} retrieved producer with id {}.", role, producer.getId());
        if (full) {
            return producerDtoConverter.producerFullDtoConvert(producer);
        }else {
            return producerDtoConverter.producerLimitedDtoConvert(producer);
        }
    }

    @Override
    public ProducerDto addPodcastToProducer(UUID producerId, UUID podcastId) {
        String role = userInfo.getRole();
        if (producerId == null) {
            F_LOG.warn("{} tried to add a podcast without providing producerId.", role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producer ID must be provided");
        }
        if (podcastId == null) {
            F_LOG.warn("{} tried to add a podcast without providing podcastId.", role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Podcast ID must be provided");
        }
        Producer producer = producerRepository.findById(producerId).orElseThrow(() -> {
            F_LOG.warn("{} tried to retrieve a producer with id {} that doesn't exist.", role, producerId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("No producer exists with ID: %s", producerId)
                );
            });
            if(!podcastApiClient.podcastExists(podcastId)) {
            F_LOG.warn("{} tried to add an podcast that does not exist.", role);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Podcast does not exist");
        }
        List<UUID> podcasts = producer.getPodcasts();
        if (podcasts.contains(podcastId)) {
            F_LOG.warn("{} tried to add an podcast that's already has that producer", role);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Podcast %s already exists for producer %s", podcastId, producerId));
        }
        podcasts.add(podcastId);
        producer.setPodcasts(podcasts);
        Producer saved = producerRepository.save(producer);

        F_LOG.info("{} added a podcast to producer with id {}.", role, producerId);
        return producerDtoConverter.producerFullDtoConvert(saved);
    }

    @Transactional
    @Override
    public Boolean removePodcastFromProducer(UUID producerId, UUID podcastId) {
        String role = userInfo.getRole();
        if (producerId == null) {
            F_LOG.warn("{} tried to remove a podcast without providing producerId.", role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producer ID must be provided");
        }
        if (podcastId == null) {
            F_LOG.warn("{} tried to add a podcast without providing podcastId.", role);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Podcast ID must be provided");
        }
        Producer producer = producerRepository.findById(producerId).orElseThrow(() -> {
            F_LOG.warn("{} tried to retrieve a producer with id {} that doesn't exist.", role, producerId);
            return new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("No producer exists with ID: %s", producerId)
            );
        });
        List<UUID> podcasts = new ArrayList<>(producer.getPodcasts());
        podcasts.remove(podcastId);
        producer.setPodcasts(podcasts);
        producerRepository.save(producer);

        F_LOG.info("{} removed podcast from producer with {}.", role, producerId);
        return true;
    }

    @Override
    public Boolean producerExist(UUID producerId) {
        F_LOG.info("{} checked if producer exists", userInfo.getRole());
        return producerRepository.existsById(producerId);
    }

}

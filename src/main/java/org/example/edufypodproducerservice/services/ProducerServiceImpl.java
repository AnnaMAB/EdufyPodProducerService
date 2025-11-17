package org.example.edufypodproducerservice.services;


import jakarta.transaction.Transactional;
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


    @Autowired
    public ProducerServiceImpl(ProducerRepository producerRepository, ProducerDtoConverter producerDtoConverter, PodcastApiClient podcastApiClient) {
        this.producerRepository = producerRepository;
        this.producerDtoConverter = producerDtoConverter;
        this.podcastApiClient = podcastApiClient;
    }

    @Transactional
    @Override
    public Producer addProducer(ProducerDto producerDto) {
        Producer producer = new Producer();
        if (producerDto.getName() == null || producerDto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (producerDto.getDescription() == null || producerDto.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is required");
        }
        if (producerDto.getImageUrl() != null && !producerDto.getImageUrl().isBlank()) {
            producer.setImageUrl(producerDto.getImageUrl());
        }
        if (producerDto.getThumbnailUrl() != null && !producerDto.getThumbnailUrl().isBlank()) {
            producer.setThumbnailUrl(producerDto.getThumbnailUrl());
        }
        if (producerDto.getPodcasts() != null && !producerDto.getPodcasts().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Podcasts can't be added from this endpoint");
        }
        producer.setName(producerDto.getName());
        producer.setDescription(producerDto.getDescription());
        producer.setPodcasts(producerDto.getPodcasts());
        return producerRepository.save(producer);
    }

    @Transactional
    @Override
    public Producer updateProducer(ProducerDto producerDto) {
        if(producerDto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Podcast id is required");
        }
        Producer producer = producerRepository.findById(producerDto.getId()).orElseThrow(() -> {
            //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No producer exists with id: %s.", producerDto.getId())
            );
        });
        if (producerDto.getName() != null && !producerDto.getName().equals(producerDto.getName())) {
            if(producerDto.getName().isBlank()) {
                // F_LOG.warn("{} tried to update a workout with invalid title.", role);
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Name can not be left blank."
                );
            }
            producer.setName(producerDto.getName());
        }
        if (producerDto.getDescription() != null && !producerDto.getDescription().equals(producer.getDescription())) {
            if(producerDto.getDescription().isBlank()) {
                // F_LOG.warn("{} tried to update a workout with invalid title.", role);
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Podcasts can't be added from this endpoint");
        }
        return producerRepository.save(producer);
    }

    @Transactional
    @Override
    public String deleteProducer(UUID producerId) {
        if (producerId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Id must be provided"
            );
        }
        Producer producer = producerRepository.findById(producerId).orElseThrow(() -> {
            //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
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
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            String.format("Producer can't be deleted while podcasts are still associated.")
                    );
                }
            }
        }
        producerRepository.deleteById(producerId);
        return String.format("Producer with Id: %s have been successfully deleted.", producerId);
    }

    @Override
    public ProducerDto getProducer(UUID producerId, boolean full) {
        if (producerId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Id must be provided"
            );
        }
        Producer producer = producerRepository.findById(producerId).orElseThrow(() -> {
            //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No producer exists with id: %s.", producerId)
            );
        });
        if (full) {
            return producerDtoConverter.producerFullDtoConvert(producer);
        }else {
            return producerDtoConverter.producerLimitedDtoConvert(producer);
        }
    }

    @Override
    public List<ProducerDto> getAllProducers(boolean full) {
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
        return producerDtos;
    }

    @Override
    public ProducerDto getProducerByPodcastId(UUID podcastId, boolean full) {
        if (podcastId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "PodcastId must be provided"
            );
        }
        Producer producer = producerRepository.findByPodcastsContaining(podcastId).orElseThrow(() -> {
            //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No producer exists for podcast with id: %s.", podcastId)
            );
        });
        if (full) {
            return producerDtoConverter.producerFullDtoConvert(producer);
        }else {
            return producerDtoConverter.producerLimitedDtoConvert(producer);
        }
    }

    @Override
    public ProducerDto addPodcastToProducer(UUID producerId, UUID podcastId) {
        if (producerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producer ID must be provided");
        }
        if (podcastId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Podcast ID must be provided");
        }
        Producer producer = producerRepository.findById(producerId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("No producer exists with ID: %s", producerId)));
        if(!podcastApiClient.podcastExists(podcastId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Podcast does not exist");
        }
        List<UUID> podcasts = producer.getPodcasts();
        if (podcasts.contains(podcastId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Podcast %s already exists for producer %s", podcastId, producerId));
        }
        podcasts.add(podcastId);
        producer.setPodcasts(podcasts);
        Producer saved = producerRepository.save(producer);

        return producerDtoConverter.producerFullDtoConvert(saved);
    }

    @Override
    public ProducerDto removePodcastFromProducer(UUID producerId, UUID podcastId) {
        if (producerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producer ID must be provided");
        }
        if (podcastId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Podcast ID must be provided");
        }
        if (podcastApiClient.podcastAssociatedWithProducer(podcastId, producerId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Remove producer from podcast first");
        }
        Producer producer = producerRepository.findById(producerId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("No producer exists with ID: %s", producerId)));
        List<UUID> podcasts = producer.getPodcasts();
        if (!podcasts.contains(podcastId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Podcast %s dosen't exists for producer %s", podcastId, producerId));
        }
        podcasts.remove(podcastId);
        producer.setPodcasts(podcasts);
        Producer saved = producerRepository.save(producer);

        return producerDtoConverter.producerFullDtoConvert(saved);
    }

    @Override
    public Boolean producerExist(UUID producerId) {
        return producerRepository.existsById(producerId);
    }

}

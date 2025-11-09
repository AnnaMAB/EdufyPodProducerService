package org.example.edufypodproducerservice.services;

import org.example.edufypodproducerservice.dto.ProducerDto;
import org.example.edufypodproducerservice.entities.Producer;

import java.util.List;
import java.util.UUID;

public interface ProducerService {

   Producer addProducer(ProducerDto producerDto);
   Producer updateProducer(ProducerDto producerDto);
   String deleteProducer(UUID producerId);

   ProducerDto getProducer(UUID producerId, boolean full);
   List<ProducerDto> getAllProducers(boolean full);
   ProducerDto getProducerByPodcastId(UUID podcastId, boolean full);

}

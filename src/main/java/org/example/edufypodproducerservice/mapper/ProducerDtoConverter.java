package org.example.edufypodproducerservice.mapper;


import org.example.edufypodproducerservice.dto.ProducerDto;
import org.example.edufypodproducerservice.entities.Producer;
import org.springframework.stereotype.Component;

@Component
public class ProducerDtoConverter {

    public ProducerDto producerFullDtoConvert(Producer producer) {
        ProducerDto producerDto = new ProducerDto();
        producerDto.setId(producer.getId());
        producerDto.setName(producer.getName());
        producerDto.setDescription(producer.getDescription());
        producerDto.setImageUrl(producer.getImageUrl());
        producerDto.setThumbnailUrl(producer.getThumbnailUrl());
        producerDto.setPodcasts(producer.getPodcasts());

        return producerDto;
    }

    public ProducerDto producerLimitedDtoConvert(Producer producer) {
        ProducerDto producerDto = new ProducerDto();
        producerDto.setId(producer.getId());
        producerDto.setName(producer.getName());
        producerDto.setThumbnailUrl(producer.getThumbnailUrl());

        return producerDto;
    }

}

package org.example.edufypodproducerservice.services;

import org.example.edufypodproducerservice.converters.UserInfo;
import org.example.edufypodproducerservice.dto.ProducerDto;
import org.example.edufypodproducerservice.entities.Producer;
import org.example.edufypodproducerservice.external.PodcastApiClient;
import org.example.edufypodproducerservice.mapper.ProducerDtoConverter;
import org.example.edufypodproducerservice.repositories.ProducerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ProducerServiceImplTest {

    @Mock
    private ProducerRepository producerRepositoryMock;

    @Mock
    private PodcastApiClient podcastApiClientMock;

    @Mock
    private UserInfo userInfoMock;

    private final ProducerDtoConverter dtoConverter = new ProducerDtoConverter();

    @InjectMocks
    private ProducerServiceImpl producerService;

    private Producer producer;
    private ProducerDto dto;

    private final UUID producerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID podcastId = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @BeforeEach
    void setUp() {
        producerService = new ProducerServiceImpl(producerRepositoryMock, dtoConverter, podcastApiClientMock, userInfoMock);

        producer = new Producer();
        producer.setId(producerId);
        producer.setName("Producer One");
        producer.setDescription("Description");
        producer.setImageUrl("image.png");
        producer.setThumbnailUrl("thumb.png");
        producer.setPodcasts(new ArrayList<>());

        dto = new ProducerDto();
        dto.setId(producerId);
        dto.setName("Producer One");
        dto.setDescription("Description");
        dto.setImageUrl("image.png");
        dto.setThumbnailUrl("thumb.png");
        dto.setPodcasts(new ArrayList<>());
    }


    // addProducer
    @Test
    void addProducer_ShouldSaveAndReturnProducer_WhenValidInput() {
        when(producerRepositoryMock.findByName(dto.getName())).thenReturn(Optional.empty());
        when(producerRepositoryMock.save(any())).thenAnswer(inv -> {
            Producer p = inv.getArgument(0);
            p.setId(producerId);
            return p;
        });

        Producer result = producerService.addProducer(dto);

        assertNotNull(result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getImageUrl(), result.getImageUrl());
        assertEquals(dto.getThumbnailUrl(), result.getThumbnailUrl());
        assertEquals(dto.getPodcasts(), result.getPodcasts());

        verify(producerRepositoryMock, times(1)).save(any(Producer.class));
    }

    @Test
    void addProducer_ShouldSaveAndReturnProducerWithDefaultValuesWhenBlank() {
        dto.setThumbnailUrl("");
        dto.setImageUrl("");

        when(producerRepositoryMock.findByName(dto.getName())).thenReturn(Optional.empty());
        when(producerRepositoryMock.save(any())).thenAnswer(inv -> {
            Producer p = inv.getArgument(0);
            p.setId(producerId);
            return p;
        });

        Producer result = producerService.addProducer(dto);

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals("https://default/thumbnail.url", result.getThumbnailUrl());
        assertEquals("https://default/image.url", result.getImageUrl());
        assertEquals(dto.getDescription(), result.getDescription());
        verify(producerRepositoryMock, times(1)).save(any(Producer.class));
    }


    @Test
    void addProducer_ShouldSaveAndReturnProducerWithDefaultValuesWhenNull() {
        dto.setThumbnailUrl(null);
        dto.setImageUrl(null);
        dto.setPodcasts(null);

        when(producerRepositoryMock.findByName(dto.getName())).thenReturn(Optional.empty());
        when(producerRepositoryMock.save(any())).thenAnswer(inv -> {
            Producer p = inv.getArgument(0);
            p.setId(producerId);
            return p;
        });

        Producer result = producerService.addProducer(dto);

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals("https://default/thumbnail.url", result.getThumbnailUrl());
        assertEquals("https://default/image.url", result.getImageUrl());
        assertThat(result.getPodcasts().isEmpty());
        verify(producerRepositoryMock, times(1)).save(any(Producer.class));
    }

    @Test
    void addProducer_ShouldThrow_WhenNameNull() {
        dto.setName(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> producerService.addProducer(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Name is required", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void addProducer_ShouldThrow_WhenNameIsBlank() {
        dto.setName("");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> producerService.addProducer(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Name is required", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void addProducer_ShouldThrow_WhenNameAlreadyExists() {
        when(producerRepositoryMock.findByName(dto.getName())).thenReturn(Optional.of(producer));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> producerService.addProducer(dto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("A producer with that name already exists", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void addProducer_ShouldThrow_WhenDescriptionNull() {
        dto.setDescription(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> producerService.addProducer(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Description is required", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void addProducer_ShouldThrow_WhenDescriptionIsBlank() {
        dto.setDescription("");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> producerService.addProducer(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Description is required", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void addProducer_ShouldThrow_WhenGetPodcastsIsNotEmpty() {
        dto.getPodcasts().add(podcastId);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> producerService.addProducer(dto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Podcasts can't be added from this endpoint", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    // updateProducer
    @Test
    void updateProducer_ShouldUpdateAndReturnProducer_WhenValidInput() {
        dto.setName("newName");
        dto.setImageUrl("newImageUrl");
        dto.setDescription("newDescription");
        dto.setThumbnailUrl("newThumbnailUrl");


        when(producerRepositoryMock.findById(dto.getId())).thenReturn(Optional.of(producer));
        when(producerRepositoryMock.findByName(dto.getName())).thenReturn(Optional.empty());
        when(producerRepositoryMock.save(any())).thenReturn(producer);

        Producer result = producerService.updateProducer(dto);

        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getThumbnailUrl(), result.getThumbnailUrl());
        assertEquals(dto.getImageUrl(), result.getImageUrl());
        verify(producerRepositoryMock, times(1)).save(any());
    }

    @Test
    void updateProducer_ShouldSaveAndReturnProducerWithDefaultValuesWhenNull() {
        dto.setThumbnailUrl(null);
        dto.setImageUrl(null);
        dto.setPodcasts(null);
        dto.setName(null);
        dto.setDescription(null);

        when(producerRepositoryMock.findById(dto.getId())).thenReturn(Optional.of(producer));
        when(producerRepositoryMock.save(any())).thenReturn(producer);

        Producer result = producerService.updateProducer(dto);

        assertNotNull(result);
        assertEquals(producer.getName(), result.getName());
        assertEquals(producer.getThumbnailUrl(), result.getThumbnailUrl());
        assertEquals(producer.getImageUrl(), result.getImageUrl());
        assertEquals(producer.getDescription(), result.getDescription());
        verify(producerRepositoryMock, times(1)).save(any(Producer.class));
    }

    @Test
    void updateProducer_ShouldThrow_WhenIdIsNull() {
        dto.setId(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.updateProducer(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Producer id is required", ex.getReason());
        verify(producerRepositoryMock, never()).findById(any());
    }

    @Test
    void updateProducer_ShouldThrow_WhenProducerNotFound() {
        when(producerRepositoryMock.findById(dto.getId())).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.updateProducer(dto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains(dto.getId().toString()));
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void updateProducer_ShouldThrow_WhenNewNameExists() {
        dto.setName("Existing Name");
        when(producerRepositoryMock.findById(dto.getId())).thenReturn(Optional.of(producer));
        when(producerRepositoryMock.findByName("Existing Name")).thenReturn(Optional.of(new Producer()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.updateProducer(dto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("A producer with that name already exists", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void updateProducer_ShouldThrow_WhenNewNameBlank() {
        dto.setName("");

        when(producerRepositoryMock.findById(dto.getId())).thenReturn(Optional.of(producer));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.updateProducer(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Name can not be left blank.", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void updateProducer_ShouldThrow_WhenNewDescriptionBlank() {
        dto.setDescription("");

        when(producerRepositoryMock.findById(dto.getId())).thenReturn(Optional.of(producer));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.updateProducer(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Description can not be left blank.", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void updateProducer_ShouldThrow_WhenPodcastsProvided() {
        dto.getPodcasts().add(podcastId);

        when(producerRepositoryMock.findById(dto.getId())).thenReturn(Optional.of(producer));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.updateProducer(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Podcasts can't be added from this endpoint", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void updateProducer_ShouldNotThrow_WhenPodcastsProvidedButUnchanged() {
        ArrayList<UUID> existingPodcasts = new ArrayList<>();
        existingPodcasts.add(podcastId);
        producer.setPodcasts(existingPodcasts);

        dto.setPodcasts(new ArrayList<>(existingPodcasts));

        when(producerRepositoryMock.findById(dto.getId())).thenReturn(Optional.of(producer));
        when(producerRepositoryMock.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Producer result = producerService.updateProducer(dto);

        assertNotNull(result);
        assertEquals(existingPodcasts, result.getPodcasts());
        verify(producerRepositoryMock, times(1)).save(any());
    }


    // deleteProducer
    @Test
    void deleteProducer_ShouldDeleteWhenProducerHasNoPodcasts() {
        producer.setPodcasts(new ArrayList<>());

        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.of(producer));

        producerService.deleteProducer(producerId);

        verify(producerRepositoryMock, times(1)).deleteById(producerId);
        verify(podcastApiClientMock, never()).podcastAssociatedWithProducer(any(), any());
    }

    @Test
    void deleteProducer_ShouldDeleteWhenPodcastsExistButNotAssociated() {
        ArrayList<UUID> podcastList = new ArrayList<>();
        podcastList.add(podcastId);
        producer.setPodcasts(podcastList);

        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.of(producer));
        when(podcastApiClientMock.podcastAssociatedWithProducer(podcastId, producerId)).thenReturn(false);

        producerService.deleteProducer(producerId);

        verify(producerRepositoryMock, times(1)).deleteById(producerId);
        verify(podcastApiClientMock, times(1)).podcastAssociatedWithProducer(podcastId, producerId);
    }

    @Test
    void deleteProducer_ShouldThrow_WhenIdIsNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.deleteProducer(null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Id must be provided", ex.getReason());
        verify(producerRepositoryMock, never()).findById(any());
    }

    @Test
    void deleteProducer_ShouldThrow_WhenProducerNotFound() {
        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.deleteProducer(producerId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains(producerId.toString()));
        verify(producerRepositoryMock, never()).delete(any());
    }

    @Test
    void deleteProducer_ShouldThrow_WhenAtLeastOnePodcastStillAssociated() {
        ArrayList<UUID> podcastList = new ArrayList<>();
        podcastList.add(podcastId);
        producer.setPodcasts(podcastList);

        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.of(producer));
        when(podcastApiClientMock.podcastAssociatedWithProducer(podcastId, producerId)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.deleteProducer(producerId));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Producer can't be deleted while podcasts are still associated.", ex.getReason());
        verify(producerRepositoryMock, never()).delete(any());
    }


    // getProducer
    @Test
    void getProducer_ShouldReturnFullDto_WhenFullTrue() {
        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.of(producer));

        ProducerDto fullDto = dtoConverter.producerFullDtoConvert(producer);

        ProducerDto result = producerService.getProducer(producerId, true);

        assertEquals(fullDto.getName(), result.getName());
        assertEquals(fullDto.getPodcasts(), result.getPodcasts());
        assertEquals(fullDto.getImageUrl(), result.getImageUrl());
        assertEquals(fullDto.getThumbnailUrl(), result.getThumbnailUrl());
        verify(producerRepositoryMock, times(1)).findById(producerId);
    }

    @Test
    void getProducer_ShouldReturnLimitedDto_WhenFullFalse() {
        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.of(producer));

        ProducerDto result = producerService.getProducer(producerId, false);

        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getThumbnailUrl(), result.getThumbnailUrl());
        assertNull(result.getPodcasts());
        assertNull(result.getImageUrl());
        verify(producerRepositoryMock, times(1)).findById(producerId);
    }

    @Test
    void getProducer_ShouldThrow_WhenIdIsNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.getProducer(null, false));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Id must be provided", ex.getReason());
        verify(producerRepositoryMock, never()).findById(any());
    }

    @Test
    void getProducer_ShouldThrow_WhenProducerNotFound() {
        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.getProducer(producerId, false));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No producer exists with id: " + producerId + ".", ex.getReason());
        verify(producerRepositoryMock, times(1)).findById(producerId);
    }


    // getAllProducers
    @Test
    void getAllProducers_ShouldReturnLimitedDtos_WhenFullFalse() {
        List<Producer> producers = List.of(producer);
        when(producerRepositoryMock.findAll()).thenReturn(producers);

        List<ProducerDto> result = producerService.getAllProducers(false);

        assertEquals(1, result.size());
        assertEquals(dto.getName(), result.get(0).getName());
        assertEquals(dto.getThumbnailUrl(), result.get(0).getThumbnailUrl());
        assertNull(result.get(0).getPodcasts());
        assertNull(result.get(0).getImageUrl());
        assertNull(result.get(0).getPodcasts());
        verify(producerRepositoryMock, times(1)).findAll();
    }

    @Test
    void getAllProducers_ShouldReturnFullDtos_WhenFullTrue() {
        List<Producer> producers = List.of(producer);
        when(producerRepositoryMock.findAll()).thenReturn(producers);

        List<ProducerDto> result = producerService.getAllProducers(true);

        assertEquals(dto.getName(), result.get(0).getName());
        assertEquals(dto.getPodcasts(), result.get(0).getPodcasts());
        assertEquals(dto.getImageUrl(), result.get(0).getImageUrl());
        assertEquals(dto.getThumbnailUrl(), result.get(0).getThumbnailUrl());
        assertEquals(1, result.size());
        assertEquals(producer.getPodcasts(), result.get(0).getPodcasts());
        verify(producerRepositoryMock, times(1)).findAll();
    }


    // getProducerByPodcastId
    @Test
    void getProducerByPodcastId_ShouldThrow_WhenPodcastIdIsNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.getProducerByPodcastId(null, false));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("PodcastId must be provided", ex.getReason());
        verify(producerRepositoryMock, never()).findByPodcastsContaining(any());
    }

    @Test
    void getProducerByPodcastId_ShouldThrow_WhenNoProducerFound() {
        when(producerRepositoryMock.findByPodcastsContaining(podcastId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.getProducerByPodcastId(podcastId, false));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No producer exists for podcast with id: " + podcastId + ".", ex.getReason());
        verify(producerRepositoryMock, times(1)).findByPodcastsContaining(any());
    }

    @Test
    void getProducerByPodcastId_ShouldReturnLimitedDto_WhenFullFalse() {
        when(producerRepositoryMock.findByPodcastsContaining(podcastId)).thenReturn(Optional.of(producer));

        ProducerDto result = producerService.getProducerByPodcastId(podcastId, false);

        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getThumbnailUrl(), result.getThumbnailUrl());
        assertNull(result.getPodcasts());
        assertNull(result.getImageUrl());
        verify(producerRepositoryMock, times(1)).findByPodcastsContaining(any());
    }

    @Test
    void getProducerByPodcastId_ShouldReturnFullDto_WhenFullTrue() {
        when(producerRepositoryMock.findByPodcastsContaining(podcastId)).thenReturn(Optional.of(producer));

        ProducerDto result = producerService.getProducerByPodcastId(podcastId, true);

        ProducerDto fullDto = dtoConverter.producerFullDtoConvert(producer);

        assertEquals(fullDto.getName(), result.getName());
        assertEquals(fullDto.getPodcasts(), result.getPodcasts());
        assertEquals(fullDto.getImageUrl(), result.getImageUrl());
        assertEquals(fullDto.getThumbnailUrl(), result.getThumbnailUrl());
        verify(producerRepositoryMock, times(1)).findByPodcastsContaining(any());
    }


    // addPodcastToProducer
    @Test
    void addPodcastToProducer_ShouldAddPodcastAndReturnFullDto_WhenValid() {
        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.of(producer));
        when(podcastApiClientMock.podcastExists(podcastId)).thenReturn(true);
        when(producerRepositoryMock.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProducerDto result = producerService.addPodcastToProducer(producerId, podcastId);

        assertNotNull(result);
        assertTrue(result.getPodcasts().contains(podcastId));
        verify(producerRepositoryMock, times(1)).save(any());
    }

    @Test
    void addPodcastToProducer_ShouldThrow_WhenProducerIdIsNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.addPodcastToProducer(null, podcastId));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Producer ID must be provided", ex.getReason());
        verify(producerRepositoryMock, never()).findById(any());
    }

    @Test
    void addPodcastToProducer_ShouldThrow_WhenPodcastIdIsNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.addPodcastToProducer(producerId, null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Podcast ID must be provided", ex.getReason());
        verify(producerRepositoryMock, never()).findById(any());
    }

    @Test
    void addPodcastToProducer_ShouldThrow_WhenProducerNotFound() {
        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.addPodcastToProducer(producerId, podcastId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No producer exists with ID: " + producerId, ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void addPodcastToProducer_ShouldThrow_WhenPodcastDoesNotExist() {
        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.of(producer));
        when(podcastApiClientMock.podcastExists(podcastId)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.addPodcastToProducer(producerId, podcastId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Podcast does not exist", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void addPodcastToProducer_ShouldThrow_WhenPodcastAlreadyAdded() {
        producer.getPodcasts().add(podcastId);

        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.of(producer));
        when(podcastApiClientMock.podcastExists(podcastId)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.addPodcastToProducer(producerId, podcastId));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Podcast " + podcastId + " already exists for producer " + producerId, ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    // removePodcastFromProducer
    @Test
    void removePodcastFromProducer_ShouldRemovePodcastAndReturnTrue_WhenValid() {
        producer.getPodcasts().add(podcastId);

        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.of(producer));
        when(producerRepositoryMock.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Boolean result = producerService.removePodcastFromProducer(producerId, podcastId);

        assertTrue(result);
        assertFalse(producer.getPodcasts().contains(podcastId));
        verify(producerRepositoryMock, times(1)).save(producer);
    }

    @Test
    void removePodcastFromProducer_ShouldThrow_WhenProducerIdIsNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.removePodcastFromProducer(null, podcastId));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Producer ID must be provided", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void removePodcastFromProducer_ShouldThrow_WhenPodcastIdIsNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.removePodcastFromProducer(producerId, null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Podcast ID must be provided", ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void removePodcastFromProducer_ShouldThrow_WhenProducerNotFound() {
        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> producerService.removePodcastFromProducer(producerId, podcastId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No producer exists with ID: " + producerId, ex.getReason());
        verify(producerRepositoryMock, never()).save(any());
    }

    @Test
    void removePodcastFromProducer_ShouldDoNothingIfPodcastNotInList_ButStillSaveProducer() {
        producer.getPodcasts().clear();

        when(producerRepositoryMock.findById(producerId)).thenReturn(Optional.of(producer));
        when(producerRepositoryMock.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Boolean result = producerService.removePodcastFromProducer(producerId, podcastId);

        assertTrue(result);
        verify(producerRepositoryMock).save(producer);
        assertTrue(producer.getPodcasts().isEmpty());
        verify(producerRepositoryMock, times(1)).save(any());
    }

    // producerExist
    @Test
    void producerExist_ShouldReturnTrue_WhenProducerExists() {
        when(producerRepositoryMock.existsById(producerId)).thenReturn(true);

        Boolean result = producerService.producerExist(producerId);

        assertTrue(result);
        verify(producerRepositoryMock).existsById(producerId);
    }

    @Test
    void producerExist_ShouldReturnFalse_WhenProducerDoesNotExist() {
        when(producerRepositoryMock.existsById(producerId)).thenReturn(false);

        Boolean result = producerService.producerExist(producerId);

        assertFalse(result);
        verify(producerRepositoryMock).existsById(producerId);
    }



}
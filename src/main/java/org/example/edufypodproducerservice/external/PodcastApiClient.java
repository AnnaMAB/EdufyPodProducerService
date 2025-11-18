package org.example.edufypodproducerservice.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.UUID;

@Service
public class PodcastApiClient {


    private final RestClient restClient;
    @Value("${podcastAssociated.api.url}")
    private String podcastAssociatedApiUrl;
    @Value("${podcastExists.api.url}")
    private String podcastExistsApiUrl;

    @Autowired
    public PodcastApiClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public boolean podcastExists(UUID podcastId) {
        try {
            ResponseEntity<Boolean> podcastExistsResponse = restClient.get()
                    .uri(podcastExistsApiUrl, podcastId)
                    .retrieve()
                    .toEntity(Boolean.class);
            if (podcastExistsResponse.getStatusCode().is2xxSuccessful()) {
                return podcastExistsResponse.getBody();
            }
            return false;
        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to check podcast " + podcastId, e);
        }
    }

    public boolean podcastAssociatedWithProducer(UUID podcastId, UUID producerId) {
        try {
            ResponseEntity<Boolean> podAssociatedResponse = restClient.get()
                    .uri(podcastAssociatedApiUrl, podcastId, producerId)
                    .retrieve()
                    .toEntity(Boolean.class);
            if (podAssociatedResponse.getStatusCode().is2xxSuccessful()) {
                return podAssociatedResponse.getBody();
            }
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String body = e.getResponseBodyAsString();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(body);

                String message = json.path("message").asText();
                String path = json.path("path").asText();

                throw new IllegalStateException(
                        String.format("Failed to check podcast. Status %s, %s, Path:%s",
                                status, message, path), e);
            } catch (IOException parseEx) {
                throw new IllegalStateException("Failed to check podcast. Status=" + status + " body=" + body, e);
            }
        } catch (ResourceAccessException ex) {
            throw new IllegalStateException("Could not connect to podcast service: " + ex.getMessage(), ex);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unexpected error calling podcast service", ex);
        }
        return false;
    }

}

package org.example.edufypodproducerservice.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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
            Boolean podcastExistsResponse = restClient.get()
                    .uri(podcastExistsApiUrl, podcastId)
                    .retrieve()
                    .body(Boolean.class);
            return podcastExistsResponse;
        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to check podcast " + podcastId, e);
        }
    }

    public boolean podcastAssociatedWithProducer(UUID podcastId, UUID producerId) {
        try {
            Boolean podAssociatedResponse = restClient.get()
                    .uri(podcastAssociatedApiUrl, podcastId, producerId)
                    .retrieve()
                    .body(Boolean.class);
            return podAssociatedResponse;
        }catch (RestClientException e) {
            throw new IllegalStateException("Failed to check podcastassociation " + podcastId, e);
        }
    }

}

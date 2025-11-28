package org.example.edufypodproducerservice.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.edufypodproducerservice.converters.UserInfo;
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
    private final UserInfo userInfo;
    private static final Logger F_LOG = LogManager.getLogger("functionality");

    @Autowired
    public PodcastApiClient(RestClient.Builder restClientBuilder, UserInfo userInfo) {
        this.restClient = restClientBuilder.build();
        this.userInfo = userInfo;
    }

    public boolean podcastExists(UUID podcastId) {
        String role = userInfo.getRole();
        try {
            ResponseEntity<Boolean> podcastExistsResponse = restClient.get()
                    .uri(podcastExistsApiUrl, podcastId)
                    .retrieve()
                    .toEntity(Boolean.class);
            if (podcastExistsResponse.getStatusCode().is2xxSuccessful() && podcastExistsResponse.getBody() != null) {
                F_LOG.info("{} successfully checked if podcast exists.", role);
                return podcastExistsResponse.getBody();
            } else {
                F_LOG.warn("{}: Podcast exist check failed: {}", role, podcastExistsResponse.getStatusCode());
                throw new IllegalStateException(
                        podcastExistsResponse.getStatusCode().toString());
            }
        } catch (RestClientException e) {
            F_LOG.warn("{}: Podcast exist check failed: {}", role, e.getMessage());
            throw new IllegalStateException("Failed to check podcast " + podcastId, e);
        }
    }

    public boolean podcastAssociatedWithProducer(UUID podcastId, UUID producerId) {
        String role = userInfo.getRole();
        try {
            ResponseEntity<Boolean> podAssociatedResponse = restClient.get()
                    .uri(podcastAssociatedApiUrl, podcastId, producerId)
                    .retrieve()
                    .toEntity(Boolean.class);
            if (podAssociatedResponse.getStatusCode().is2xxSuccessful() && podAssociatedResponse.getBody() != null) {
                F_LOG.info("{} successfully checked if podcast is associated with producer.", role);
                return podAssociatedResponse.getBody();
            } else {
                F_LOG.warn("{}: Producer association check failed: {}", role, podAssociatedResponse.getStatusCode());
                throw new IllegalStateException(
                        podAssociatedResponse.getStatusCode().toString());
            }
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String body = e.getResponseBodyAsString();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(body);

                String message = json.path("message").asText();
                String path = json.path("path").asText();

                F_LOG.warn("{}: Failed to check producer association. error: {}", role, message);
                throw new IllegalStateException(
                        String.format("Failed to check podcast. Status %s, %s, Path:%s",
                                status, message, path), e);
            } catch (IOException parseEx) {
                F_LOG.warn("{}: Failed to check producer association. error: {}", role, parseEx.getMessage());
                throw new IllegalStateException("Failed to check podcast. Status=" + status + " body=" + body, e);
            }
        } catch (ResourceAccessException ex) {
            F_LOG.warn("{}: Failed to check producer association. error: {}", role, ex.getMessage());
            throw new IllegalStateException("Could not connect to podcast service: " + ex.getMessage(), ex);
        } catch (RestClientException ex) {
            F_LOG.warn("{}: Failed to check producer association. error: {}", role, ex.getMessage());
            throw new IllegalStateException("Unexpected error calling podcast service", ex);
        }
    }

}

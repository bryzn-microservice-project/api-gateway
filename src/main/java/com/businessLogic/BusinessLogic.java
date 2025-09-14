package com.businessLogic;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.kafka.KafkaService;

/*
 * Handles the business logic for processing various topics and utilizes 
 * REST clients to communicate with other microservices.
 */
@Service
public class BusinessLogic {
    private final KafkaService kafkaService;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogic.class);

    // REST Clients to communicate with other microservices
    private RestClient serviceOrchestratorClient = RestClient.create();
    private RestClient userMangementClient = RestClient.create();
    private RestClient movieServiceClient = RestClient.create();

    private HashMap<String, RestClient> restRouter = new HashMap<>();
    private HashMap<RestClient, String> restEndpoints = new HashMap<>();

    public BusinessLogic(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
        mapTopicsToClient();
    }

    /* Method to map topics to their respective microservices and endpoints
     * # api-gateway:8081
     * # movie-service:8082
     * # notification-service:8083
     * # payment-service:8084
     * # seating-service:8085
     * # user-management-service:8086
     * # gui-service:8087
     * # ticketing-manager:8088
     * # service-orchestrator:8089
     * # session-manager:8090
    */
    public void mapTopicsToClient() {
        restRouter.put("LoginRequest", userMangementClient);
        restRouter.put("NewAccountRequest", userMangementClient);
        restEndpoints.put(userMangementClient, "http://user-management-service:8086/api/v1/processTopic");

        restRouter.put("MovieTicketRequest", serviceOrchestratorClient);
        restEndpoints.put(serviceOrchestratorClient, "http://service-orchestrator:8089/api/v1/processTopic");

        restRouter.put("MovieListRequest", movieServiceClient);
        restEndpoints.put(movieServiceClient, "http://movie-service:8082/api/v1/processTopic");
        
        LOG.info("Sucessfully mapped the topics to their respective microservices...");
    }

    /*
     * Request handlers for the various topics, which communicate through REST clients
     */
    public void processRequest(String json, String topicName) {
        String microservice = "";
        switch(topicName) {
            case "LoginRequest":
            case "NewAccountRequest":
                microservice = "USER-MANAGEMENT-SERVICE";
                break;
            case "MovieListRequest":
                microservice = "MOVIE-SERVICE";
                break;
            case "MovieTicketRequest":
                microservice = "SERVICE-ORCHESTRATOR";
                break;
            default:
                LOG.error("Unknown topic: " + topicName);
                return;
        }

        if(!microservice.isEmpty()) {
            LOG.info("Received a " + topicName + ". Sending the topic to the [" + microservice + "]");
            ResponseEntity<Void> response = restRouter.get(topicName)
                .post()
                .uri(restEndpoints.get(restRouter.get(topicName)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .retrieve()
                .toBodilessEntity();
            LOG.info("LoginRequest processed with status: " + response.getStatusCode());
        }
    }

    /*
     * Response handlers for the various topics, which require kafka to communicate with the GUI
     */

    public void processResponse(String response, String topicName)
    {
        LOG.info("Received a " + topicName + ". Sending the topic to the [GUI Service]");
        kafkaService.publishTopic(topicName, response);
        LOG.info(topicName + " processed and sent to GUI.");
    }
}

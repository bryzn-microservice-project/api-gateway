package com.businessLogic;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.kafka.KafkaService;
import com.topics.*;

/*
 * Handles the business logic for processing various topics and utilizes 
 * REST clients to communicate with other microservices.
 */
@Service
public class BusinessLogic {
    private final KafkaService kafkaService;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogic.class);

    // REST Clients to communicate with other microservices
    private RestClient paymentServiceClient = RestClient.create();
    private RestClient userMangementClient = RestClient.create();
    private RestClient seatingServiceClient = RestClient.create();
    private RestClient movieServiceClient = RestClient.create();
    private RestClient notificationServiceClient = RestClient.create();

    private HashMap<String, RestClient> restRouter = new HashMap<>();
    private HashMap<RestClient, String> restEndpoints = new HashMap<>();

    public BusinessLogic(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
        mapTopicsToClient();
    }

    /* Method to map topics to their respective microservices and endpoints
        # api-gateway:8081
        # movie-service:8082
        # notification-service:8083
        # payment-service:8084
        # seating-service:8085
        # user-management-service:8086
        # gui-service:8087 
    */
    public void mapTopicsToClient() {
        restRouter.put("PaymentRequest", paymentServiceClient);
        restEndpoints.put(paymentServiceClient, "http://payment-service:8084/api/v1/processTopic"); 

        restRouter.put("LoginRequest", userMangementClient);
        restEndpoints.put(userMangementClient, "http://user-management-service:8086/api/v1/processTopic");

        restRouter.put("NewAccountRequest", userMangementClient);
        restEndpoints.put(userMangementClient, "http://user-management-service:8086/api/v1/processTopic");

        restRouter.put("SeatRequest", seatingServiceClient);
        restEndpoints.put(seatingServiceClient, "http://seating-service:8085/api/v1/processTopic");

        restRouter.put("MovieTicketRequest", movieServiceClient);
        restEndpoints.put(movieServiceClient, "http://movie-service:8082/api/v1/processTopic");

        restRouter.put("MovieTicketResponse", notificationServiceClient);
        restEndpoints.put(notificationServiceClient, "http://notification-service:8083/api/v1/processTopic");
        
        LOG.info("Sucessfully mapped the topics to their respective microservices...");
    }

    /*
     * Request handlers for the various topics, which communicate through REST clients
     */
    public void processPaymentRequest(PaymentRequest paymentRequest) {
        LOG.info("Received a PaymentRequest. Sending the topic to the [Payment Service]");
        ResponseEntity<Void> response = restRouter.get("PaymentRequest")
            .post()
            .uri(restEndpoints.get(restRouter.get("PaymentRequest")))
            .contentType(MediaType.APPLICATION_JSON)
            .body(paymentRequest)
            .retrieve()
            .toBodilessEntity();
        LOG.info("PaymentRequest processed with status: " + response.getStatusCode());
    }

    public void processLoginRequest(LoginRequest loginRequest) {
        LOG.info("Received a LoginRequest. Sending the topic to the [User Management]");
        ResponseEntity<Void> response = restRouter.get("LoginRequest")
            .post()
            .uri(restEndpoints.get(restRouter.get("LoginRequest")))
            .contentType(MediaType.APPLICATION_JSON)
            .body(loginRequest)
            .retrieve()
            .toBodilessEntity();
        LOG.info("LoginRequest processed with status: " + response.getStatusCode());
    }

    public void processNewAccountRequest(NewAccountRequest newAccountRequest) {
        LOG.info("Received a NewAccountRequest. Sending the topic to the [User Management]");
        ResponseEntity<Void> response = restRouter.get("NewAccountRequest")
            .post()
            .uri(restEndpoints.get(restRouter.get("NewAccountRequest")))
            .contentType(MediaType.APPLICATION_JSON)
            .body(newAccountRequest)
            .retrieve()
            .toBodilessEntity();
        LOG.info("NewAccountRequest processed with status: " + response.getStatusCode());
    }

    public void processSeatRequest(SeatRequest seatRequest) {
        LOG.info("Received a SeatRequest. Sending the topic to the [Seating Service]");
        ResponseEntity<Void> response = restRouter.get("SeatRequest")
            .post()
            .uri(restEndpoints.get(restRouter.get("SeatRequest")))
            .contentType(MediaType.APPLICATION_JSON)
            .body(seatRequest)
            .retrieve()
            .toBodilessEntity();
        LOG.info("SeatRequest processed with status: " + response.getStatusCode());
    }

    public void processMovieTicketRequest(MovieTicketRequest movieTicketRequest) {
        LOG.info("Received a MovieTicket. Sending the topic to the [Movie Service]");
        ResponseEntity<Void> response = restRouter.get("MovieTicketRequest")
            .post()
            .uri(restEndpoints.get(restRouter.get("MovieTicketRequest")))
            .contentType(MediaType.APPLICATION_JSON)
            .body(movieTicketRequest)
            .retrieve()
            .toBodilessEntity();
        LOG.info("MovieTicketRequest processed with status: " + response.getStatusCode());
    }

    /*
     * Response handlers for the various topics, which require kafka to communicate with the GUI
     */
    public void processPaymentResponse(PaymentResponse paymentResponse) {
        LOG.info("Received a PaymentResponse. Sending the topic to the [GUI]");
        kafkaService.publishTopic("PaymentResponse", paymentResponse.toString());
    }

    public void processLoginResponse(LoginResponse loginResponse) {
        LOG.info("Received a LoginResponse. Sending the topic to the [GUI]");
        kafkaService.publishTopic("LoginResponse", loginResponse.toString());
    }

    public void processNewAccountResponse(NewAccountResponse newAccountResponse) {
        LOG.info("Received a NewAccountResponse. Sending the topic to the [GUI]");
        kafkaService.publishTopic("NewAccountResponse", newAccountResponse.toString());
    }

    public void processSeatResponse(SeatResponse seatResponse) {
        LOG.info("Received a SeatResponse. Sending the topic to the [GUI]");
        kafkaService.publishTopic("SeatResponse", seatResponse.toString()); 
    }

    public void processMovieTicketResponse(MovieTicketResponse movieTicketResponse) {
        LOG.info("Received a MovieTicket. Sending the topic to the [GUI]");
        kafkaService.publishTopic("MovieTicketResponse", movieTicketResponse.toString());   
    }
}

package com.businessLogic;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.topics.*;

/*
 * Handles the business logic for processing various topics and utilizes 
 * REST clients to communicate with other microservices.
 */
@Service
public class BusinessLogic {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogic.class);

    // REST Clients to communicate with other microservices
    private RestClient paymentServiceClient = RestClient.create();
    private RestClient userMangementClient = RestClient.create();
    private RestClient seatingServiceClient = RestClient.create();
    private RestClient movieServiceClient = RestClient.create();
    private RestClient notificationServiceClient = RestClient.create();

    private HashMap<String, RestClient> restRouter = new HashMap<>();

    // Method to map topics to their respective microservices
    public void mapTopics() {
        restRouter.put("PaymentRequest", paymentServiceClient);
        restRouter.put("LoginRequest", userMangementClient);
        restRouter.put("NewAccountRequest", userMangementClient);
        restRouter.put("SeatRequest", seatingServiceClient);
        restRouter.put("MovieTicketRequest", movieServiceClient);
        restRouter.put("MovieTicketResponse", notificationServiceClient);
        LOG.info("Sucessfully mapped the topics to their respective microservices...");
    }

    public void processPaymentRequest(PaymentRequest paymentRequest) {
        LOG.info("Received a PaymentRequest. Sending the topic to the [Payment Service]");
    }

    public void processPaymentResponse(PaymentResponse paymentResponse) {
        LOG.info("Received a PaymentResponse. Sending the topic to the [GUI]");
    }

    public void processLoginRequest(LoginRequest loginRequest) {
        LOG.info("Received a LoginRequest. Sending the topic to the [User Management]");
    }

    public void processLoginResponse(LoginResponse loginResponse) {
        LOG.info("Received a LoginResponse. Sending the topic to the [GUI]");
    }

    public void processNewAccountRequest(NewAccountRequest newAccountRequest) {
        LOG.info("Received a NewAccountRequest. Sending the topic to the [User Management]");
    }

    public void processNewAccountResponse(NewAccountResponse newAccountResponse) {
        LOG.info("Received a NewAccountResponse. Sending the topic to the [GUI]");
    }

    public void processSeatRequest(SeatRequest seatRequest) {
        LOG.info("Received a SeatRequest. Sending the topic to the [Seating Service]");
    }

    public void processSeatResponse(SeatResponse seatResponse) {
        LOG.info("Received a SeatResponse. Sending the topic to the [GUI]");
    }

    public void processMovieTicketRequest(MovieTicketRequest movieTicketRequest) {
        LOG.info("Received a MovieTicket. Sending the topic to the [Movie Service]");
    }
    
    public void processMovieTicketResponse(MovieTicketResponse movieTicketResponse) {
        LOG.info("Received a MovieTicket. Sending the topic to the [GUI]");
    }
}

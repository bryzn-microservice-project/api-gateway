package com.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.SchemaService;
import com.businessLogic.BusinessLogic;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schema.SchemaValidator;

// topic list
import com.topics.LoginRequest;
import com.topics.LoginResponse;
import com.topics.MovieTicketRequest;
import com.topics.MovieTicketResponse;
import com.topics.NewAccountRequest;
import com.topics.NewAccountResponse;
import com.topics.PaymentRequest;
import com.topics.PaymentResponse;
import com.topics.SeatRequest;
import com.topics.SeatResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.net.URL;

/*
 * MainController.java reponsible for handling incoming requests and delegating other classes to
 * handle the topics
 */
@RestController
public class MainController {
    private SchemaValidator schemaValidator;
    private BusinessLogic businessLogic;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogic.class);

    public MainController(SchemaValidator schemaValidator, BusinessLogic businessLogic) {
        this.schemaValidator = schemaValidator;
        this.businessLogic = businessLogic;
    }

    @GetMapping("/api/v1/name")
    public String microserviceName() {
        return "This microservice is the [API-GATEWAY]!";
    }

    /*
     * Main entry point for processing incoming topics other microservices will use this enpoint
     */
    @PostMapping("/api/v1/processTopic")
    public void processRestTopics(@RequestBody String jsonString) {
        LOG.info("Received an incoming topic... Processing now!");
        System.out.println("\n\nJSON: " + jsonString + "\n\n");
        JSONObject jsonNode = new JSONObject(jsonString);
        String topicName = jsonNode.getString("topicName");
        URL schemaUrl =
                getClass().getClassLoader().getResource(SchemaService.getPathFor(topicName));
        LOG.info("Schema URL: " + schemaUrl);
        InputStream schemaStream = null;
        try {
            schemaStream = schemaValidator.getSchemaStream(SchemaService.getPathFor(topicName));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        if (schemaStream == null) {
            LOG.error("No schema found for topic: " + topicName);
        }

        if (schemaValidator.validateJson(schemaStream, jsonNode)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                switch (jsonNode.getString("topicName")) {
                    case "PaymentResponse": {
                        PaymentResponse paymentResponse =
                                mapper.readValue(jsonNode.toString(), PaymentResponse.class);
                        businessLogic.processPaymentResponse(paymentResponse);
                    }
                        break;
                    case "LoginResponse": {
                        LoginResponse loginResponse =
                                mapper.readValue(jsonNode.toString(), LoginResponse.class);
                        businessLogic.processLoginResponse(loginResponse);
                    }
                        break;
                    case "NewAccountResponse": {
                        NewAccountResponse newAccountResponse =
                                mapper.readValue(jsonNode.toString(), NewAccountResponse.class);
                        businessLogic.processNewAccountResponse(newAccountResponse);
                    }
                        break;
                    case "SeatResponse": {
                        SeatResponse seatResponse =
                                mapper.readValue(jsonNode.toString(), SeatResponse.class);
                        businessLogic.processSeatResponse(seatResponse);
                    }
                        break;
                    case "MovieTicketResponse": {
                        MovieTicketResponse movieTicketResponse =
                                mapper.readValue(jsonNode.toString(), MovieTicketResponse.class);
                        businessLogic.processMovieTicketResponse(movieTicketResponse);
                    }
                        break;
                    default: {
                        LOG.warn("Non-supported Topic: " + topicName);
                    }
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        } else {
            LOG.error("Failed schema validation...");
        }
    }

    @KafkaListener(topics = {"PaymentRequest", "LoginRequest", "NewAccountRequest", "SeatRequest",
            "MovieTicketRequest"}, groupId = "api-gateway-group")
    public void processKafka(String jsonString) {
        LOG.info("Received an incoming topic... Processing now!");
        System.out.println("\n\nJSON: " + jsonString + "\n\n");
        JSONObject jsonNode = new JSONObject(jsonString);
        String topicName = jsonNode.getString("topicName");
        URL schemaUrl =
                getClass().getClassLoader().getResource(SchemaService.getPathFor(topicName));
        LOG.info("Schema URL: " + schemaUrl);
        InputStream schemaStream = null;
        try {
            schemaStream = schemaValidator.getSchemaStream(SchemaService.getPathFor(topicName));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        if (schemaStream == null) {
            LOG.error("No schema found for topic: " + topicName);
        }

        if (schemaValidator.validateJson(schemaStream, jsonNode)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                switch (jsonNode.getString("topicName")) {
                    case "PaymentRequest": {
                        PaymentRequest paymentRequest =
                                mapper.readValue(jsonNode.toString(), PaymentRequest.class);
                        businessLogic.processPaymentRequest(paymentRequest);
                    }
                        break;
                    case "LoginRequest": {
                        LoginRequest loginRequest =
                                mapper.readValue(jsonNode.toString(), LoginRequest.class);
                        businessLogic.processLoginRequest(loginRequest);
                    }
                        break;
                    case "NewAccountRequest": {
                        NewAccountRequest newAccountRequest =
                                mapper.readValue(jsonNode.toString(), NewAccountRequest.class);
                        businessLogic.processNewAccountRequest(newAccountRequest);
                    }
                        break;
                    case "SeatRequest": {
                        SeatRequest seatRequest =
                                mapper.readValue(jsonNode.toString(), SeatRequest.class);
                        businessLogic.processSeatRequest(seatRequest);
                    }
                        break;
                    case "MovieTicketRequest": {
                        MovieTicketRequest movieTicketRequest =
                                mapper.readValue(jsonNode.toString(), MovieTicketRequest.class);
                        businessLogic.processMovieTicketRequest(movieTicketRequest);
                    }
                        break;
                    default: {
                        LOG.warn("Non-supported Topic: " + topicName);
                    }
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        } else {
            LOG.error("Failed schema validation...");
        }
    }
}

package com.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.SchemaService;
import com.businessLogic.BusinessLogic;
import com.schema.SchemaValidator;

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
            try {
                switch (jsonNode.getString("topicName")) {
                    case "LoginResponse":
                    case "NewAccountResponse":
                    case "MovieTicketResponse":
                    case "MovieListResponse": {
                        businessLogic.processResponse(jsonString, topicName);
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

    @KafkaListener(topics = {"LoginRequest", "NewAccountRequest", "MovieTicketRequest",
            "MovieListRequest"}, groupId = "api-gateway-group")
    public void processKafka(String jsonString) {
        LOG.info("Received an incoming topic from the bus... Processing now!");
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
            try {
                switch (jsonNode.getString("topicName")) {
                    case "LoginRequest": 
                    case "NewAccountRequest":
                    case "MovieTicketRequest":
                    case "MovieListRequest": {
                        businessLogic.processRequest(jsonString, topicName);
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

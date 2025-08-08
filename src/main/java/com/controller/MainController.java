package com.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.businessLogic.BusinessLogic;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schema.SchemaValidator;
import com.topics.LoginRequest;
import com.topics.LoginResponse;
import com.topics.MovieTicket;
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

@RestController
public class MainController {
    private SchemaValidator schemaValidator;
    private BusinessLogic businessLogic;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogic.class);

    public MainController(SchemaValidator schemaValidator, BusinessLogic businessLogic)
    {
        this.schemaValidator = schemaValidator;
        this.businessLogic = businessLogic;
    }

    @GetMapping("/api/v1/name")
    public String microserviceName() {
        return "This microservice is the [API-GATEWAY]!";
    }

    @PostMapping("/api/v1/processTopic")
    public void processTopic(@RequestBody String jsonString) {
        LOG.info("Received an incoming topic... Processing now!");
        System.out.println("\n\nJSON: " + jsonString + "\n\n");
        JSONObject jsonNode = new JSONObject(jsonString);
        String topicName = jsonNode.getString("topicName");

        URL schemaUrl = getClass().getClassLoader().getResource("json-schema/payment-request.json");
        LOG.info("Schema URL: " + schemaUrl);
        InputStream schemaStream = null;
        try
        {
            schemaStream = schemaValidator.getSchemaStream(topicName);
        }
        catch(Exception e)
        {
            LOG.error(e.getMessage());
        }

        if (schemaStream == null) {
            LOG.error("No schema found for topic: " + topicName);
        }

        if (schemaValidator.validateJson(schemaStream, jsonNode)) {
            ObjectMapper mapper = new ObjectMapper();
            try{
                switch(jsonNode.getString("topicName"))
                {
                    case "PaymentRequest": 
                    {
                        PaymentRequest paymentRequest = mapper.readValue(jsonNode.toString(), PaymentRequest.class);
                        businessLogic.processPaymentRequest(paymentRequest);
                    }
                    case "PaymentResponse":
                    {
                        PaymentResponse paymentResponse = mapper.readValue(jsonNode.toString(), PaymentResponse.class);
                        businessLogic.processPaymentResponse(paymentResponse);
                    }
                    case "LoginRequest":
                    {
                        LoginRequest loginRequest = mapper.readValue(jsonNode.toString(), LoginRequest.class);
                        businessLogic.processLoginRequest(loginRequest);
                    }
                    case "LoginResponse":
                    {
                        LoginResponse loginResponse = mapper.readValue(jsonNode.toString(), LoginResponse.class);
                        businessLogic.processLoginResponse(loginResponse);
                    }
                    case "NewAccountRequest":
                    {
                        NewAccountRequest newAccountRequest = mapper.readValue(jsonNode.toString(), NewAccountRequest.class);
                        businessLogic.processNewAccountRequest(newAccountRequest);
                    }
                    case "NewAccountResponse":
                    {
                        NewAccountResponse newAccountResponse = mapper.readValue(jsonNode.toString(), NewAccountResponse.class);
                        businessLogic.processNewAccountResponse(newAccountResponse);
                    }
                    case "SeatRequest":
                    {
                        SeatRequest seatRequest = mapper.readValue(jsonNode.toString(), SeatRequest.class);
                        businessLogic.processSeatRequest(seatRequest);
                    }
                    case "SeatResponse":
                    {
                        SeatResponse seatResponse = mapper.readValue(jsonNode.toString(), SeatResponse.class);
                        businessLogic.processSeatResponse(seatResponse);
                    }
                    case "MovieTicket":
                    {
                        MovieTicket movieTicket = mapper.readValue(jsonNode.toString(), MovieTicket.class);
                        businessLogic.processMovieTicket(movieTicket);
                    }
                    default:
                    {
                        LOG.warn("Non-supported Topic: " + topicName);
                    }
                }
            }
            catch(Exception e)
            {
                LOG.error(e.getMessage());
            }
        } else {
            LOG.error("Failed schema validation...");
        }
    }
}

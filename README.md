################################################################
#                                                              #
#                       API-GATEWAY                            #
#                                                              #
################################################################


#########################################
   GUI TO API-GW (REST TO MICROSERVICE)
#########################################

NewAccountRequest
{
  "topicName": "NewAccountRequest",
  "correlatorId": 55555,
  "name": "Bryan Nguyen",
  "email": "bryan.nguyen@example.com",
  "username": "bryannguyen",
  "password": "SecurePass123",
  "rewardPoints": 100,
  "creditCard": "4111111111111111",
  "cvc": "123"
}

LoginRequest
{
  "topicName": "LoginRequest",
  "correlatorId": 10001,
  "email": "user@example.com",
  "password": "MySecretPass"
}

MovieTicketRequest
{
  "topicName": "MovieTicketRequest",
  "correlatorId": 10101,
  "ticketId": 5001,
  "movie": {
    "movieName": "Inception",
    "showtime": "2025-09-14T18:00:00Z",
    "genre": "SCIFI"
  },
  "seatNumber": "A12",
  "price": 12.50,
  "payment": {
    "topicName": "PaymentRequest",
    "correlatorId": 10101,
    "paymentAmount": 12.50,
    "email": "user@example.com",
    "creditCard": "4111111111111111",
    "cvc": "123"
  }
}

MovieListRequest
{
  "topicName": "MovieListRequest",
  "correlatorId": 20201,
  "genre": "SCIFI"
}

{
  "topicName": "MovieListRequest",
  "correlatorId": 20202,
  "startingShowtime": "2025-09-14T00:00:00Z",
  "endingShowtime": "2025-09-14T23:59:59Z"
}

{
  "topicName": "MovieListRequest",
  "correlatorId": 20203,
  "movieName": "Inception"
}



#########################################
    API-GW TO GUI (PUBLISH TO KAFKA)
#########################################

NewAccountResponse
{
  "topicName": "NewAccountResponse",
  "correlatorId": 67890,
  "username": "bryan.nguyen",
  "status": "SUCCESSFUL",
  "statusMessage": "Account created successfully."
}

Login Response
{
  "topicName": "LoginResponse",
  "correlatorId": 12345,
  "status": "SUCCESSFUL",
  "username": "bryan.nguyen",
  "timestamp": "2025-09-13T20:15:30Z"
}

MovieTicketResponse
{
  "topicName": "MovieTicketResponse",
  "correlatorId": 54321,
  "ticketId": 9876,
  "movie": {
    "movieName": "Interstellar",
    "showtime": "2025-09-14T19:30:00Z",
    "genre": "SCIFI"
  },
  "seatNumber": "B12"
}

MovieListResponse
{
  "topicName": "MovieListResponse",
  "correlatorId": 11223,
  "movies": [
    {
      "movieName": "Inception",
      "showtime": "2025-09-14T18:00:00Z",
      "genre": "SCIFI"
    },
    {
      "movieName": "The Godfather",
      "showtime": "2025-09-14T20:30:00Z",
      "genre": "DRAMA"
    }
  ],
  "timestamp": "2025-09-13T21:00:00Z"
}
package org.itmo.testing.lab2.integration;

import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.itmo.testing.lab2.controller.UserAnalyticsController;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserAnalyticsIntegrationTest {

    private Javalin app;
    private int port = 7000;

    @BeforeAll
    void setUp() {
        app = UserAnalyticsController.createApp();
        app.start(port);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @AfterAll
    void tearDown() {
        app.stop();
    }

    @Test
    @Order(1)
    @DisplayName("Тест регистрации пользователя")
    void testUserRegistration() {
        given()
                .queryParam("userId", "user1")
                .queryParam("userName", "Alice")
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body(equalTo("User registered: true"));
    }

//    @Test
//    @Order(2)
//    @DisplayName("Тест повторной регистрации пользователя")
//    void testRepeatedUserRegistration() {
//        given()
//                .queryParam("userId", "user1")
//                .queryParam("userName", "Alice")
//                .when()
//                .post("/register")
//                .then()
//                .statusCode(400)
//                .body(equalTo("User registered: false"));
//    }

    @Test
    void testRegisterMissingUserId() {
        given()
            .queryParam("userName", "Alice")
            .when()
            .post("/register")
            .then()
            .statusCode(400)
            .body(equalTo("Missing parameters"));
    }

    @Test
    void testRegisterMissingUserName() {
        given()
            .queryParam("userId", "user1")
            .when()
            .post("/register")
            .then()
            .statusCode(400)
            .body(equalTo("Missing parameters"));
    }


    @Test
    @Order(3)
    @DisplayName("Тест записи сессии")
    void testRecordSession() {
        LocalDateTime now = LocalDateTime.now();
        given()
                .queryParam("userId", "user1")
                .queryParam("loginTime", now.minusHours(1).toString())
                .queryParam("logoutTime", now.toString())
                .when()
                .post("/recordSession")
                .then()
                .statusCode(200)
                .body(equalTo("Session recorded"));
    }

    @Test
    void testRecordSessionMissingParameters() {
        LocalDateTime now = LocalDateTime.now();
        given()
            .queryParam("loginTime", now.minusHours(1).toString())
            .queryParam("logoutTime", now.toString())
            .when()
            .post("/recordSession")
            .then()
            .statusCode(400)
            .body(equalTo("Missing parameters"));
        given()
            .queryParam("userId", "user1")
            .queryParam("logoutTime", now.toString())
            .when()
            .post("/recordSession")
            .then()
            .statusCode(400)
            .body(equalTo("Missing parameters"));
        given()
            .queryParam("userId", "user1")
            .queryParam("loginTime", now.minusHours(1).toString())
            .when()
            .post("/recordSession")
            .then()
            .statusCode(400)
            .body(equalTo("Missing parameters"));
    }

    @Test
    void testRecordSessionInvalidUserId() {
        LocalDateTime now = LocalDateTime.now();
        given()
            .queryParam("userId", "user2")
            .queryParam("loginTime", now.minusHours(1).toString())
            .queryParam("logoutTime", now.toString())
            .when()
            .post("/recordSession")
            .then()
            .statusCode(400)
            .body(equalTo("Invalid data: User not found"));
    }


    @Test
    @Order(4)
    @DisplayName("Тест получения общего времени активности")
    void testGetTotalActivity() {
        given()
                .queryParam("userId", "user1")
                .when()
                .get("/totalActivity")
                .then()
                .statusCode(200)
                .body(containsString("Total activity:"))
                .body(containsString("minutes"));
    }

    @Test
    void testGetTotalActivityMissingUserId() {
        given()
                .when()
                .get("/totalActivity")
                .then()
                .statusCode(400)
                .body(equalTo("Missing userId"));
    }

//    @Test
//    void testGetTotalActivityInvalidUserId() {
//        given()
//                .queryParam("userId", "user2")
//                .when()
//                .get("/totalActivity")
//                .then()
//                .statusCode(400)
//                .body(equalTo("Invalid data: User not found"));
//    }


//    @Test
//    @Order(5)
//    void testInactiveUsers() {
//        given()
//            .queryParam("days", 1)
//            .when()
//            .get("/inactiveUsers")
//            .then()
//            .statusCode(200);
//    }

    @Test
    void testInactiveUsersMissingDays() {
        given()
            .when()
            .get("/inactiveUsers")
            .then()
            .statusCode(400)
            .body(equalTo("Missing days parameter"));
    }

    @Test
    void testInactiveUsersWrongDaysFormat() {
        given()
            .queryParam("days", "hello")
            .when()
            .get("/inactiveUsers")
            .then()
            .statusCode(400)
            .body(equalTo("Invalid number format for days"));
    }


//    @Test
//    @Order(6)
//    void testMonthlyActivity() {
//        given()
//            .queryParam("userId", "user1")
//            .queryParam("month", "2020-12")
//            .when()
//            .get("/monthlyActivity")
//            .then()
//            .statusCode(200);
//    }

    @Test
    void testMonthlyActivityMissingParameters() {
        given()
            .queryParam("month", "2020-12")
            .when()
            .get("/monthlyActivity")
            .then()
            .statusCode(400)
            .body(equalTo("Missing parameters"));
        given()
            .queryParam("userId", "user2")
            .when()
            .get("/monthlyActivity")
            .then()
            .statusCode(400)
            .body(equalTo("Missing parameters"));
    }

    @Test
    void testMonthlyActivityInvalidUserId() {
        given()
            .queryParam("userId", "user2")
            .queryParam("month", "2020-12")
            .when()
            .get("/monthlyActivity")
            .then()
            .statusCode(400)
            .body(equalTo("Invalid data: No sessions found for user"));
    }
}

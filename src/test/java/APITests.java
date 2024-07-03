import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class APITests {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    public void testGetUsers() {
        Response response = given()
                .when()
                .get("/api/users?page=2")
                .then()
                .statusCode(200)
                .body("page", equalTo(2))
                .body("data", not(empty()))
                .body("data.id", everyItem(notNullValue()))
                .body("data.email", everyItem(containsString("@")))
                .extract()
                .response();

        System.out.println("Response: " + response.asString());
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Body: " + response.getBody().asString());
        System.out.println("Headers: " + response.headers());
        System.out.println("Cookies: " + response.cookies());
        System.out.println("Time Taken: " + response.getTime());

        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertTrue(response.getBody().asString().contains("page"));
        Assert.assertTrue(response.getBody().asString().contains("data"));
    }

    @Test
    public void testGetSingleUser() {
        given()
                .baseUri("https://reqres.in")
                .when()
                .get("/api/users/2")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(2))
                .body("data.email", containsString("@"))
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue());
    }
    @Test
    public void testGetSingleUserNotFound() {
        given()
                .baseUri("https://reqres.in")
                .when()
                .get("/api/users/23")
                .then()
                .statusCode(404)
                .body(equalTo("{}"));
    }
    @Test
    public void testListResources() {
        given()
                .baseUri("https://reqres.in")
                .when()
                .get("/api/unknown")
                .then()
                .statusCode(200)
                .body("data", not(empty()));
    }

    @Test
    public void testSingleResource() {
        given()
                .baseUri("https://reqres.in")
                .when()
                .get("/api/unknown/2")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(2))
                .body("data.name", notNullValue())
                .body("data.year", notNullValue())
                .body("data.color", notNullValue())
                .body("data.pantone_value", notNullValue());
    }

    @Test
    public void testSingleResourceNotFound() {
        given()
                .baseUri("https://reqres.in")
                .when()
                .get("/api/unknown/23")
                .then()
                .statusCode(404)
                .body(equalTo("{}"));  // Check if the response body is an empty JSON object
    }


    @Test
    public void testCreateUser() {
        String requestBody = "{\"name\": \"John Doe\", \"job\": \"QA Engineer\"}";

        given()
                .baseUri("https://reqres.in")
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("John Doe"))
                .body("job", equalTo("QA Engineer"))
                .body("id", notNullValue());
    }
    @Test
    public void testPutUser() {
        String requestBody = "{\"name\": \"morpheus\", \"job\": \"zion resident\"}";

        given()
                .baseUri("https://reqres.in")
                .contentType("application/json")
                .body(requestBody)
                .when()
                .put("/api/users/2")
                .then()
                .statusCode(200)
                .body("name", equalTo("morpheus"))
                .body("job", equalTo("zion resident"))
                .body("updatedAt", notNullValue());
    }
    @Test
    public void testPatchUser() {
        String requestBody = "{\"name\": \"morpheus\", \"job\": \"zion resident\"}";

        given()
                .baseUri("https://reqres.in")
                .contentType("application/json")
                .body(requestBody)
                .when()
                .patch("/api/users/2")
                .then()
                .statusCode(200)
                .body("name", equalTo("morpheus"))
                .body("job", equalTo("zion resident"))
                .body("updatedAt", notNullValue());
    }
    @Test
    public void testDeleteUser() {
        given()
                .baseUri("https://reqres.in")
                .when()
                .delete("/api/users/2")
                .then()
                .statusCode(204); // Assuming status code 204 for successful deletion
    }


    @Test
    public void testRegisterUser() {
        // Request body JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", "eve.holt@reqres.in");
        requestBody.put("password", "pistol");

        // Perform POST request
        given()
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    public void testRegisterUnsuccessfulUser() {
        // Request body JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", "sydney@fife");

        // Perform POST request
        given()
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .statusCode(400);
    }

    @Test
    public void testLogin() {
        // Request body JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", "eve.holt@reqres.in");
        requestBody.put("password", "cityslicka");

        // Perform POST request
        given()
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    public void testUnsuccessfulLogin() {
        // Request body JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("email", "sydney@fife");
        // Perform POST request
        given()
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }






        @Test
        public void testDelayedResponse() {
            // Perform GET request with delay parameter
            given()
                    .queryParam("delay", 1)
                    .when()
                    .get("https://reqres.in/api/users")
                    .then()
                    .statusCode(200)
                    .time(lessThan(4000L));
        }
    }


package GoRest;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {

    int userID;
    User newUser;

    @BeforeClass
    void Setup() {
        baseURI = "https://gorest.co.in/public/v2/users"; // PROD
        // baseURI = "https://test.gorest.co.in/public/v2/users";  // TEST
    }

    public String getRandomName() {  return RandomStringUtils.randomAlphabetic(8); }

    public String getRandomEmail() { return RandomStringUtils.randomAlphabetic(8).toLowerCase()+"@gmail.com"; }

    @Test(enabled = false) // It is stated that the method is temporarily disabled
    public void createUserObject()
    {
        // start operations
        // got tokens
        // users I prepared the JSON.
        int userID=
                given()
                        .header("Authorization", "Bearer dbe788af1336349e6a1032d893488639cdaa8eaceb524c8b9f4b716e2364f5dd") // It uses a token-based authentication mechanism to authenticate the user.
                        .contentType(ContentType.JSON)
                        .body("{\"name\":\""+getRandomName()+"\", \"gender\":\"male\", \"email\":\""+getRandomEmail()+"\", \"status\":\"active\"}")
                        // The upper part is the request properties: preparation operations Authorization and request BODY part in POSTMAN
                        .log().uri()
                        .log().body()
                        .when() // The point where request is SEND button in POSTMAN
                        .post("") // base URL+parentheses (if there is no http) the point where the response occurs
                        // We call the CREATE operation with the POST method, as in POSTMAN

                        // Test window in POSTMAN after bottom side response
                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
                ;

        System.out.println("userID = " + userID);
    }

    @Test(enabled = false)
    public void createUserObject2WithMap()
    {
        Map<String,String> newUser=new HashMap<>();
        newUser.put("name",getRandomName());
        newUser.put("gender","male");
        newUser.put("email",getRandomEmail());
        newUser.put("status","active");

        int userID=
                given()
                        .header("Authorization", "Bearer dbe788af1336349e6a1032d893488639cdaa8eaceb524c8b9f4b716e2364f5dd")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id")
                ;

        System.out.println("userID = " + userID);
    }

    @Test
    public void createUserObject3WithObject()
    {
        newUser=new User();
        newUser.setName(getRandomName());
        newUser.setGender("male");
        newUser.setEmail(getRandomEmail());
        newUser.setStatus("active");

        userID=
                given()
                        .header("Authorization", "Bearer dbe788af1336349e6a1032d893488639cdaa8eaceb524c8b9f4b716e2364f5dd")
                        .contentType(ContentType.JSON)
                        .body(newUser)
                        .log().body()

                        .when()
                        .post("")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        //.extract().path("id")
                        .extract().jsonPath().getInt("id");
        ;
        System.out.println("userID = " + userID);

        // path : Returns direct data that does not allow class or type conversion. like List<String>
        // jsonPath : Allows class conversion and type conversion, giving the data in the format we want.
    }

    // dependsOnMethod is a feature used in the TestNG testing framework.
    // This feature ensures that one test runs before another test runs.
    // Thus, if a test fails, dependent tests are automatically skipped.
    // priority is a property used in TestNG tests to set the running priority of tests.
    @Test(dependsOnMethods = "createUserObject3WithObject", priority = 1)
    public void getUserByID()
    {
        given()
                .header("Authorization", "Bearer dbe788af1336349e6a1032d893488639cdaa8eaceb524c8b9f4b716e2364f5dd")
                .pathParam("userId",userID)
                .log().uri()

                .when()
                .get("/{userId}")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(userID))
        ;
    }

    @Test(dependsOnMethods = "createUserObject3WithObject", priority = 2)
    public void updateUserObject()
    {
        // newUser.setName("ismet temur");

        Map<String,String> updateUser=new HashMap<>();
        updateUser.put("name", "Omer Boncuk");

        given()
                .header("Authorization", "Bearer dbe788af1336349e6a1032d893488639cdaa8eaceb524c8b9f4b716e2364f5dd")
                .pathParam("userId",userID)
                .contentType(ContentType.JSON)
                .body(updateUser)
                .log().body()
                .log().uri()

                .when()
                .put("/{userId}")

                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id",equalTo(userID))
                .body("name",equalTo("Omer Boncuk"))
        ;
    }

    @Test(dependsOnMethods = "updateUserObject", priority = 3)
    public void deleteUserById()
    {

        given()
                .header("Authorization", "Bearer dbe788af1336349e6a1032d893488639cdaa8eaceb524c8b9f4b716e2364f5dd")
                .pathParam("userId",userID)
                .log().uri()

                .when()
                .delete("/{userId}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deleteUserById")
    public void deleteUserByIdNegative()
    {

        given()
                .header("Authorization", "Bearer dbe788af1336349e6a1032d893488639cdaa8eaceb524c8b9f4b716e2364f5dd")
                .pathParam("userId",userID)
                .log().uri()

                .when()
                .delete("/{userId}")

                .then()
                .log().body()
                .statusCode(404)
        ;
    }

    @Test
    public void getUsers()
    {
        Response body=
        given()
                .header("Authorization","dbe788af1336349e6a1032d893488639cdaa8eaceb524c8b9f4b716e2364f5dd")

                .when()
                .get()

                .then()
                .log().body()
                .statusCode(200)
                .extract().response()
                ;
        int idUser3=body.path("[2].id");
        int idUser3JsonPath=body.jsonPath().getInt("[2].id");
        System.out.println("idUser3 = " + idUser3);
        System.out.println("idUser3JsonPath = " + idUser3JsonPath);

        User[] users=body.as(User[].class); // extract.as
        System.out.println("Arrays.toString(users) = " + Arrays.toString(users));

        List<User>listUser= body.jsonPath().getList("", User.class);
        System.out.println("listUser = " + listUser);
    }

    @Test
    public void getUserv1()
    {
        Response body=
                given()
                        .header("Authorization","dbe788af1336349e6a1032d893488639cdaa8eaceb524c8b9f4b716e2364f5dd")

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response();
                // body.as(), extract.as // All classes need to be made for all incoming response appropriate objects

                List<User>dataUser=body.jsonPath().getList("data", User.class);
                // JSONPATH We can convert a fragment in a response to an object
                System.out.println("dataUser = " + dataUser);

        // Corresponding to the whole structure for (as) Class transformations in the previous examples
        // We were transforming by writing all the necessary classes and reaching the elements we wanted.
        // Here (JsonPath) allows us to convert an intermediate data into a clas and take it as a list.
        // We used the JSONPATH that allows. Thus, if it is a single class, the data is taken.
        // without the need for other classes

        // path : Returns direct data that does not allow class or type conversion. like List<String>
        // jsonPath : Allows class conversion and type conversion, giving the data in the format we want.
    }
}

class User{
    private int id;
    private String name;
    private String gender;
    private String email;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

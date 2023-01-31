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

    @Test(enabled = false)
    public void createUserObject()
    {
        // başlangıç işlemleri
        // token aldım
        // users JSON ı hazırladım.
        int userID=
                given()
                        .header("Authorization", "Bearer dbe788af1336349e6a1032d893488639cdaa8eaceb524c8b9f4b716e2364f5dd")
                        .contentType(ContentType.JSON)
                        .body("{\"name\":\""+getRandomName()+"\", \"gender\":\"male\", \"email\":\""+getRandomEmail()+"\", \"status\":\"active\"}")
                        // üst taraf request özellikleridir : hazırlık işlemleri POSTMAN deki Authorization ve request BODY kısmı
                        .log().uri()
                        .log().body()
                        .when() // request in olduğu nokta    POSTMAN deki SEND butonu
                        .post("") //baseURI+paratez içi(http yoksa) respons un oluştuğu nokta
                        // CREATE işlemi POST metodu ile çağırıyoruz POSTMAN deki gibi

                        // alt taraf response sonrası  POSTMAN deki test penceresi
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

        // path : class veya tip dönüşümüne imkan veremeyen direk veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.
    }

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
        updateUser.put("name", "ismet temur");

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
                .body("name",equalTo("ismet temur"))
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
                // body.as(), extract.as // tum gelen response uygun nesneler icin tum classlarin yapilmasi gerekiyor

                List<User>dataUser=body.jsonPath().getList("data", User.class);
                // JSONPATH bir response icindeki bir parcayi nesneye donusturebiliriz
                System.out.println("dataUser = " + dataUser);

        // Daha önceki örneklerde (as) Clas dönüşümleri için tüm yapıya karşılık gelen
        // gereken tüm classları yazarak dönüştürüp istediğimiz elemanlara ulaşıyorduk.
        // Burada ise(JsonPath) aradaki bir veriyi clasa dönüştürerek bir list olarak almamıza
        // imkan veren JSONPATH i kullandık.Böylece tek class ise veri alınmış oldu
        // diğer class lara gerek kalmadan

        // path : class veya tip dönüşümüne imkan veremeyen direk veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.
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

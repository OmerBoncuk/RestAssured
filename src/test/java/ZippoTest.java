import POJO.Locations;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class ZippoTest {
    //pm                              RestAssured
    //body.country                    body("country",
    //body.'post code'                body("post code",
    //body.places[0].'place name'     body("places[0].'place name'")
    //body.places.'place name'        body("places.'place name'")   -> bütün place name leri verir
    //                                bir index verilmezse dizinin bütün elemanlarında arar
    @Test
    public void test (){
                given()
                // hazırlık işlemlerini yapacağız (token,send body, parametreler)
                .when()
                // link i ve metodu veriyoruz
                .then()
                //  assertion ve verileri ele alma extract
                ;
    }
    @Test
    public void statusCodeTest(){
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body() // log.all butun response yi gosterir
                .statusCode(200) // status kontrolu
                .contentType(ContentType.JSON) // donen sonuc JSON tipinde mi
                .body("country",equalTo("United States")) // body.country == United States

        ;
    }
    @Test
    public void checkstateInResponseBody(){
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .log().body() // log.all butun response yi gosterir
                .statusCode(200) // status kontrolu
                .body("places[0].state",equalTo("California"))

        ;
    }
    @Test
    public void bodyJsonPathTest3(){
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")

                .then()
                .log().body() // log.all butun response yi gosterir
                .statusCode(200) // status kontrolu
                .body("places.'place name'",hasItem("Dörtağaç Köyü")) // places in icinde Dörtağaç Köyü varmi?

        ;
    }
    @Test
    public void bodyJsonPathTest4(){
        given()
                .when()
                .get("http://api.zippopotam.us/US/90210")

                .then()
                .log().body() // log.all butun response yi gosterir
                .statusCode(200) // status kontrolu
                .body("places",hasSize(1)) // PLACES IN SIZE 1 E ESITMI

        ;
    }
    @Test
    public void combiningTest(){
        given()
                .when()
                .get("http://api.zippopotam.us/US/90210")

                .then()
                .log().body() // log.all butun response yi gosterir
                .statusCode(200) // status kontrolu
                .body("places.state",hasItem("California")) // PLACES IN SIZE 1 E ESITMI
                .body("places[0].'place name'",equalTo("Beverly Hills")) // verilen path deki deger buna esitmi ?

        ;
    }
    @Test
    public void pathParamTest(){
        given()
                .pathParams("Country","us")
                .pathParams("ZipKod",90210)
                .log().uri() // request link Request URL: http://api.zippopotam.us/us/90210

                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipKod}")

                .then()
                .log().body()
                .statusCode(200)

        ;
    }
    @Test
    public void pathParamTest2  (){

        for (int i = 90210; i <= 90213; i++) {


            given()
                    .pathParams("Country", "us")
                    .pathParams("ZipKod", i)
                    .log().uri() // request link Request URL: http://api.zippopotam.us/us/90210

                    .when()
                    .get("http://api.zippopotam.us/{Country}/{ZipKod}")

                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("places",hasSize(1))
            ;
        }
    }
    @Test
    public void queryParamTest(){
        given()
                .param("page",1)
                .log().uri()

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page",equalTo(1))

        ;
    }
    @Test
    public void queryParamTest2(){
        // bu linkteki 1 den 10 kadar sayfaları çağırdığınızda response daki donen page degerlerinin
        // çağrılan page nosu ile aynı olup olmadığını kontrol ediniz.
        for (int i = 1; i <= 10; i++) {

            given()
                    .param("page", i)
                    .log().uri()

                    .when()
                    .get("https://gorest.co.in/public/v1/users")

                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("meta.pagination.page", equalTo(i))
            ;
        }
    }
    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    @BeforeClass
    void Setup(){
        baseURI="https://gorest.co.in/public/v1";
        requestSpec=new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setContentType(ContentType.JSON)
                .build();

        responseSpec=new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();
    }
    @Test
    public void requestResponseSpecification(){
        given()
                .param("page",1)
                .spec(requestSpec)

                .when()
                .get("/users")

                .then()
                .body("meta.pagination.page",equalTo(1))
                .spec(responseSpec)
        ;
    }
    @Test
    public void extractionJsonPath(){
        String placeName=
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .statusCode(200)
                .extract().path("places[0].'place name'")
                // extract metodu ile given ile baslayan satir
                // bir deger dondurur hale . en sonda extract olmali
                ;
        System.out.println("placeName = " + placeName);
    }

    @Test
    public void extractionJsonPathInt() {
        // alinacak tipin degerine en uygun olan karsiliktaki tip yazilir
        int limit=
        given()
                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .statusCode(200)
                .extract().path("meta.pagination.limit")
                ;
        System.out.println("limit = " + limit);
        Assert.assertEquals(limit,10,"test sonucu");
    }

    @Test
    public void extractionJsonPathList() {
        List<Integer>idler=
        // alinacak tipin degerine en uygun olan karsiliktaki tip yazilir
        given()
                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .statusCode(200)
                .extract().path("data.id")
                ;
        System.out.println("idler = " + idler);
        Assert.assertTrue(idler.contains(4180));
    }

    @Test
    public void extractionJsonPathStringList() {
        List<String>names=
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .statusCode(200)
                        .extract().path("data.name")
                ;
        System.out.println("names = " + names);
        Assert.assertTrue(names.contains("Tanya Prajapat"));
    }

    @Test
    public void extractionJsonPathResponseAll() {
        Response response=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .statusCode(200)
                        .extract().response();

        List <Integer> ids = response.path("data.id");
        List <String> names = response.path("data.name");
        int limit = response.path("meta.pagination.limit");

        System.out.println("response = " + response.prettyPrint());
        System.out.println("ids = " + ids);
        System.out.println("names = " + names);
        System.out.println("limit = " + limit);

        Assert.assertTrue(names.contains("Rev. Diptendu Desai"));
        Assert.assertTrue(ids.contains(4175));
        Assert.assertEquals(limit,10,"Test sonucu");
    }

    @Test
    public void extractingJsonPOJO()
    {
        Locations yer=
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                .extract().as(Locations.class) // locations sablonuna
                ;
        System.out.println("yer = " + yer.getPostCode());
        System.out.println("yer.getPlaces().get(0).getPlaceName() = " + yer.getPlaces().get(0).getPlaceName());
    }

}

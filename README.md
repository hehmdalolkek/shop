# Shop
REST API. A simple Spring Boot project. Used PostgreSQL, LiquiBase, JdbcTemplate, MapStruct, Spring Security - basic auth, Testcontainers. Unit/integration tests.

## Technologies
* Java 17
* Maven
* Spring Boot 3
* Spring Web
* Spring Security
* Lombok
* MapStruct
* JUnit
* Mockito
* PostgreSQL
* LiquiBase
* Testcontainers

## Run locally
1. First, you need to start the PostgreSQL database.
2. Next, you need to create and fill out your .env file in the project root according to the example.
```
# database
HOST=localhost
POSTGRES_DB=shop
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=root

# security
SECURITY_USERNAME=username
SECURITY_PASSWORD=password
SECURITY_ROLES=ADMIN

# security in tests
SECURITY_USERNAME_TEST=test
SECURITY_PASSWORD_TEST=test
SECURITY_ROLES_TEST=ADMIN
```
3. Finally, you can build a jar file and run it from the command line:
```
git clone https://github.com/hehmdalolkek/shop.git
cd shop
./mvnw package
java -jar target/shop-0.0.1-SNAPSHOT.jar
```
4. You can then access the project at http://localhost:8080/.

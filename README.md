# Modern Spring Boot Secure API
This is a basic user management template based on Java 17 + Spring Boot 3.4 + Spring Security 6.4. You should start new projects with this.

## Features

### User management
- Fine-grained permissions vs ~~traditional RBAC~~.
- Signup, Login and Token Refresh.

### Security
- Mint JWTs using rsa 2048 private key vs ~~traditional string secret key~~.
- Bearer Token support using in-built SpringBoot OAuth2 resource server.
- ~~Setup hacked Authentication Filters or tokens caching~~.


### Data storage
- MySQL + JPA

### Documentation
- Swagger + OpenAPI 3

### Lombok
- Absolutely no lombok. thank you.

### Not yet implemented
- Integration tests.
- Detailed swagger docs.
- Cache manager to allow refresh token revocations.

## Usage
### setup
- Start your local mysql server or use the provided `docker-compose.yml` to start one.
- Change the datasource `port`, `username` and `password` to match your mysql instance.
```yaml
  datasource:
    url: jdbc:mysql://localhost:32000/secureapi
    username: springboot
    password: springboot
    driverClassName: com.mysql.cj.jdbc.Driver
```
- Run `commands.sh` in the `src/main/resources` to generate RSA 2048 keys
```bash
/bin/bash commands.sh
```
### run
```bash
./mvnw package
java -jar target/secureapi-0.0.1-SNAPSHOT.jar
```

## Reference
[Spring Security](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html) <br>
[GeeksForGeeks Article](https://www.geeksforgeeks.org/spring-boot-oauth2-with-jwt/)

_No rights reserved_
FROM maven:3.8.5-openjdk-18

ENV SPRING_PROFILES_ACTIVE docker
ENV SPRING_SERVER_PORT=8002
ENV SPRING_DATASOURCE_URL=jdbc:mysql://api2-mysql:8080/PostSM?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root

ENV USER_MS_API_URL=registry.kthcloud.com/data-ht2022/data1-api1/api/v1

WORKDIR /app

COPY pom.xml ./

COPY src ./src

CMD mvn spring-boot:run
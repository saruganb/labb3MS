FROM maven:3.8.5-openjdk-18

ENV SPRING_PROFILES_ACTIVE docker
ENV SPRING_SERVER_PORT=8003
ENV SPRING_DATASOURCE_URL=jdbc:mysql://api1-mysql:8080/UserSM?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root

WORKDIR /app

COPY pom.xml ./

COPY src UserMS/src

CMD mvn spring-boot:run

FROM maven:3.8.5-openjdk-18

ENV SPRING_PROFILES_ACTIVE docker
ENV SPRING_SERVER_PORT=8001
ENV SPRING_DATASOURCE_URL=jdbc:mysql://api3-mysql:8080/FriendSM?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root

WORKDIR /app

COPY FriendMS/pom.xml .

COPY FriendMS/src .

CMD mvn spring-boot:run
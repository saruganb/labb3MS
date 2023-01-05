package com.example.starter;

import com.mongodb.reactivestreams.client.MongoDatabase;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import org.flywaydb.core.Flyway;

import java.util.List;

public class PersistenceVerticle extends AbstractVerticle {

  private MongoClient mongoClient;
  private MongoDatabase database;

  @Override
  public void start(Promise<Void> start){
    /*accessDatabase()
      .compose(this:: configureMongoClient)
      .setHandler(start:: handle);*/
    //mongoClient = MongoClient.createShared(vertx, new JsonObject().put("GraphDb", config().getString("GraphDb", "admin")));
  }
  Future<Void> configureMongoClient(Void unused){
    JsonObject mongo = new JsonObject()
      .put("GraphDb", "GraphDb")
      .put("url", "jdbc:mongodb:localhost:27017/GraphDb")
      .put("username", "user")
      .put("password", "pass");
    mongoClient = MongoClient.createShared(vertx, mongo);
    return Future.<Void>succeededFuture();
  }

  public void addDoc(List<Integer> listWithData, Long userId){
    JsonObject document = new JsonObject()
      .put("userId",userId);
    for(int i = 0; i < listWithData.size(); i++){
        document
        .put("data", "" + listWithData.get(i));
    }
    mongoClient.insert("graph", document, res -> {
      if (res.succeeded()) {
        String id = res.result();
        System.out.println("Inserted graph with id " + id);
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void updateDoc(){
    JsonObject query = new JsonObject()
      .put("title", "The Hobbit");
      // Set the author field
    JsonObject update = new JsonObject().put("$set", new JsonObject()
      .put("author", "J. R. R. Tolkien"));
    mongoClient.updateCollection("books", query, update, res -> {
      if (res.succeeded()) {
        System.out.println("graph updated !");
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void deleteDoc(Long userId, String graphName){
    JsonObject query = new JsonObject()
      .put("userId", userId)
      .put("graphName", graphName);
    mongoClient.removeDocuments("graph", query, res -> {
      if (res.succeeded()) {
        System.out.println("graph is deleted");
      } else {
        res.cause().printStackTrace();
      }
    });
  }

  public void getData(Long userId, String graphName){
    JsonObject query = new JsonObject()
      .put("userId", userId)
      .put("graphName", graphName);
    mongoClient.find("graph", query, res -> {
      if (res.succeeded()) {
        for (JsonObject json : res.result()) {
          System.out.println(json.encodePrettily());
        }
      } else {
        res.cause().printStackTrace();
      }
    });
  }



  /*Future<Void> accessDatabase() {
    JsonObject dbConfig = config().getJsonObject("db", new JsonObject());
    String url = dbConfig.getString("url", "jdbc:mongodb:localhost:27017/GraphDb");
    String adminUser = dbConfig.getString("admin_user", "admin_user");
    String adminPass = dbConfig.getString("admin_pass", "admin_pass");
    Flyway flyway = Flyway.configure().dataSource(url, adminUser, adminPass).load();
  }
  MongoClient mongoClient = MongoClient.create(vertx, new JsonObject()
      .put("url", "http://localhost:27017/GraphDb")
    .put("db_name", "GraphDb"));*/
}

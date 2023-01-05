package com.example.starter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.JsonParser;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainVerticle extends AbstractVerticle {
  private MongoClient mongoClient;
  @Override
  public void start() throws Exception {
    // Create a Router
    Router router = Router.router(vertx);
    router.route().handler(CorsHandler.create("http://localhost:3000"));
    router
      .route("/")
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        response
          .putHeader("content-type", "text/html")
          .end("<h1>Hello from Vert.x 3 application</h1>");
      });
    router
      .route(HttpMethod.POST, "/addDoc/:id/:graphName")
      .handler(routingContext -> {
        List<Object> obj = new ArrayList<>();
        Long userId = Long.parseLong(routingContext.pathParam("id"));
        String graphName = routingContext.pathParam("graphName");
        JsonObject query1 = new JsonObject()
          .put("userId", userId)
          .put("graphName", graphName);
        routingContext.request().bodyHandler(bodyHandler -> {
          obj.add(bodyHandler.toJson());
        });
        mongoClient.find("userGraph", query1, res -> {
          System.out.println(res + ", " + res.result());
          if (res.result().isEmpty()) {
              System.out.println(obj);
              JsonObject document = new JsonObject()
                .put("userId", userId)
                .put("graphName", graphName)
                .put("values", obj.get(0));
              mongoClient.insert("userGraph", document, res2 -> {
                if (res2.succeeded()) {
                  String id = res2.result();
                  System.out.println("Inserted graph with id " + id);
                } else {
                  res2.cause().printStackTrace();
                }
              });
              routingContext.response().end();
          } else {
            routingContext.response().end();
            System.out.println("GraphName already used");
            res.cause().printStackTrace();
          }
        });
      });
      /*.handler(routingContext -> {
        Long userId = Long.parseLong(routingContext.pathParam("id"));
        String graphName = routingContext.pathParam("graphName");
          routingContext.request().bodyHandler(bodyHandler -> {
            final Object body = bodyHandler.toJson();
            JsonObject document = new JsonObject()
              .put("userId", userId)
              .put("graphName", graphName)
              .put("values", body);
            mongoClient.insert("userGraph", document, res -> {
              if (res.succeeded()) {
                String id = res.result();
                System.out.println("Inserted graph with id " + id);
              } else {
                res.cause().printStackTrace();
              }
            });
          });
        }
      );*/
    router
      .route(HttpMethod.GET, "/getDoc/:id/:graphName")
      .handler(routingContext -> {
          Long userId = Long.parseLong(routingContext.pathParam("id"));
          String graphName = routingContext.pathParam("graphName");
          JsonObject query = new JsonObject()
            .put("userId", userId)
            .put("graphName", graphName);
          mongoClient.find("userGraph", query, res -> {
            if (res.succeeded()) {
              for (JsonObject json : res.result()) {
                routingContext.response()
                  .putHeader("content-type", "application/json; charset=utf-8")
                  .end(Json.encodePrettily(json));
                //System.out.println(json.encodePrettily());
              }
            } else {
              res.cause().printStackTrace();
              routingContext.response().end();
            }
          });
        }
      );
    router
      .route(HttpMethod.DELETE, "/deleteDoc/:id/:graphName")
      .handler(routingContext -> {
        Long userId = Long.parseLong(routingContext.pathParam("id"));
        String graphName = routingContext.pathParam("graphName");
        JsonObject query = new JsonObject()
          .put("userId", userId)
          .put("graphName", graphName);
        mongoClient.removeDocument("userGraph", query, res -> {
          if (res.succeeded()) {
            System.out.println("Document removed");
            routingContext.response().end();
          } else {
            res.cause().printStackTrace();
          }
        });
      });

    router
      .route(HttpMethod.GET, "/getAllMyDocs/:id")
      .handler(routingContext -> {
        Long userId = Long.parseLong(routingContext.pathParam("id"));
          JsonObject query = new JsonObject()
            .put("userId", userId);
          mongoClient.find("userGraph", query, res -> {
            if (res.succeeded()) {
              List<JsonObject> list = new ArrayList<>();
              for (JsonObject json : res.result()) {
                //routingContext.response()
                  //.putHeader("content-type", "application/json; charset=utf-8");
                list.add(json);
                //System.out.println(json.encodePrettily());
              }
              routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .putHeader("Access-Control-Allow-Origin", "http://localhost:3000")
                .putHeader("Access-Control-Allow-Credentials", "true")
                .putHeader("Access-Control-Allow-Methods", "GET")
                //.putHeader("Access-Control-Allow-Headers", "Content-Type")
                .end(Json.encodePrettily(list));
            } else {
              res.cause().printStackTrace();
            }
          });
      });
    router
      .route(HttpMethod.PUT, "/insertDataIntoGraph/:id/:graphName")
        .handler(routingContext -> {
          System.out.println("n");
          Long userId = Long.parseLong(routingContext.pathParam("id"));
          String graphName = routingContext.pathParam("graphName");
          //StringBuilder sb = new StringBuilder();
          List<Object> obj = new ArrayList<>();
          JsonObject query1 = new JsonObject()
            .put("userId", userId)
            .put("graphName", graphName);
          routingContext.request().bodyHandler(bodyHandler -> {
            //sb.append(bodyHandler.toJson());
            //sb.deleteCharAt(0);
            StringBuilder str = new StringBuilder();
            str.append(bodyHandler.toString());
            str.deleteCharAt(0);
            str.deleteCharAt(str.length()-1);
            System.out.println(str);
            JsonObject jsonObject = new JsonObject(str.toString());
            obj.add(jsonObject);
            System.out.println(obj.get(0));
          });
          mongoClient.find("userGraph", query1, res -> {
            if (res.succeeded()) {
              System.out.println(obj.get(0));
              JsonObject query2 = new JsonObject()
                .put("userId", userId)
                .put("graphName", graphName);
              JsonObject update = new JsonObject().put("$push", new JsonObject()
                .put("values", obj.get(0)));
              mongoClient.updateCollection("userGraph", query2, update, res2 -> {
                if (res2.succeeded()) {
                  System.out.println("Updated!");
                  routingContext.response().end();
                } else {
                  res2.cause().printStackTrace();
                }
              /*System.out.println("s");
              StringBuilder str = new StringBuilder();
              str.append(res.result().get(0).getJsonArray("values").encode());
              str.deleteCharAt(str.indexOf("]"));
              str.append(",").append(sb);
              System.out.println(str);
              JsonObject obj = new JsonObject(str.toString());
              System.out.println(obj);
              JsonObject query2 = new JsonObject()
                .put("userId", userId)
                .put("graphName", graphName);
              JsonObject update = new JsonObject().put("$set", new JsonObject()
                .put("values", obj));
              mongoClient.updateCollection("userGraph", query2, update, res2 -> {
                if (res2.succeeded()) {
                  System.out.println("Updated!");
                } else {
                  res2.cause().printStackTrace();
                }*/
                //System.out.println(sb);
              });
            } else {
              res.cause().printStackTrace();
            }
          });
        });
      /*.route(HttpMethod.PUT, "/insertDataIntoGraph/:id/:graphName")
        .handler(routingContext -> {
          Long userId = Long.parseLong(routingContext.pathParam("id"));
          String graphName = routingContext.pathParam("graphName");
          JsonObject query1 = new JsonObject()
            .put("userId", userId)
            .put("graphName", graphName);
          mongoClient.find("userGraph", query1, res -> {
            if (res.succeeded()) {
              //JsonObject obj = new JsonObject(s.toString());
              routingContext.request().bodyHandler(bodyHandler -> {
                StringBuilder str = new StringBuilder();
                str.append(res.result().get(0).getJsonArray("values").encode());
                str.deleteCharAt(str.indexOf("]"));
                str.append(", ").append(bodyHandler.toJson().toString()).append("]");
                System.out.println(str);
                JsonObject obj = new JsonObject(str.toString());
                JsonObject query2 = new JsonObject()
                  .put("userId", userId)
                  .put("graphName", graphName);
                JsonObject update = new JsonObject().put("$set", new JsonObject()
                  .put("values", obj));
                mongoClient.updateCollection("userGraph", query2, update, res2 -> {
                  if (res2.succeeded()) {
                    System.out.println("Updated!");
                  } else {
                    res2.cause().printStackTrace();
                  }
                });
              });
            } else {
              res.cause().printStackTrace();
            }
          });
        });*/

    /*ConfigStoreOptions defaultConfig = new ConfigStoreOptions()
      .setFormat("json")
      .setConfig(new JsonObject().put("path", "http://localhost:27017/GraphDb"));*/
    // Mount the handler for all incoming requests at every path and HTTP method
    router.route().handler(context -> {
      // Get the address of the request
      String address = context.request().connection().remoteAddress().toString();
      // Get the query parameter "name"
      MultiMap queryParams = context.queryParams();
      String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";
      // Write a json response
      context.json(
        new JsonObject()
          .put("name", name)
          .put("address", address)
          .put("message", "Hello " + name + " connected from " + address)
      );
    });

    // Create the HTTP server
    vertx.createHttpServer()
    // Handle every request using the router
    .requestHandler(router)
    // Start listening
    .listen(8888)
    // Print the port
    .onSuccess(server ->
      System.out.println(
        "HTTP server started on port " + server.actualPort()
      )
    );
    configureMongoClient();
  }

  Future<Void> configureMongoClient(){
    JsonObject mongo = new JsonObject()
      //jdbc:mongodb:localhost:27017/GraphDb
      .put("url", "jdbc:mongodb:localhost:27017/GraphDb")
      .put("db_name", "GraphDb")
      .put("username", "theUser")
      .put("password", "pass");

    mongoClient = MongoClient.createShared(vertx, mongo);
    //mongoClient.createCollection("userGraph");
    return Future.<Void>succeededFuture();
  }
}

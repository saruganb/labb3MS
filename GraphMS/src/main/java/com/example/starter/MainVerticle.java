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
    router.route().handler(CorsHandler.create("http://localhost:3000")
      .allowedMethod(io.vertx.core.http.HttpMethod.GET)
      .allowedMethod(io.vertx.core.http.HttpMethod.POST)
      .allowedMethod(io.vertx.core.http.HttpMethod.PUT)
      .allowedMethod(io.vertx.core.http.HttpMethod.DELETE)
      .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
      .allowedHeader("Access-Control-Request-Method")
      .allowedHeader("Access-Control-Allow-Credentials")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Headers")
      .allowedHeader("Content-Type"));
    router
      .route("/")
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        response
          .putHeader("content-type", "text/html")
          .end("<h1>Hello from Vert.x 3 application</h1>");
      });
    router
      .route(HttpMethod.POST, "/addDoc/:id/:graphName/:graphType")
      .handler(routingContext -> {
        List<Object> obj = new ArrayList<>();
        String userId = routingContext.pathParam("id");
        String graphName = routingContext.pathParam("graphName");
        String graphType = routingContext.pathParam("graphType");
        JsonObject query1 = new JsonObject()
          .put("userId", userId)
          .put("graphName", graphName)
          .put("graphType", graphType);
        routingContext.request().bodyHandler(bodyHandler -> {
          System.out.println(bodyHandler.toJson());
          obj.add(bodyHandler.toJson());
        });
        mongoClient.find("userGraph", query1, res -> {
          System.out.println(res + ", " + res.result());
          if (res.result().isEmpty()) {
              System.out.println(obj);
              JsonObject document = new JsonObject()
                .put("userId", userId)
                .put("graphName", graphName)
                .put("graphType", graphType)
                .put("values", obj.get(0));
              mongoClient.insert("userGraph", document, res2 -> {
                if (res2.succeeded()) {
                  String id = res2.result();
                  System.out.println("Inserted graph with id " + id);
                } else {
                  res2.cause().printStackTrace();
                }
              });
              routingContext.response().ended();
          } else {
            routingContext.response().ended();
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
          String userId = routingContext.pathParam("id");
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
        String userId = routingContext.pathParam("id");
        String graphName = routingContext.pathParam("graphName");
        JsonObject query = new JsonObject()
          .put("userId", userId)
          .put("graphName", graphName);
        mongoClient.removeDocument("userGraph", query, res -> {
          if (res.succeeded()) {
            System.out.println("Document removed");
          } else {
            res.cause().printStackTrace();
          }
          routingContext.response().ended();
        });
      });

    router
      .route(HttpMethod.GET, "/getAllMyDocs/:id")
      .handler(routingContext -> {
        String userId = routingContext.pathParam("id");
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
                .end(Json.encodePrettily(list));
            } else {
              res.cause().printStackTrace();
            }
          });
      });
    router
      .route(HttpMethod.PUT, "/insertDataIntoGraph/:id/:graphName")
        .handler(routingContext -> {
          String userId = routingContext.pathParam("id");
          String graphName = routingContext.pathParam("graphName");
          List<JsonArray> obj = new ArrayList<>();
          JsonObject query1 = new JsonObject()
            .put("userId", userId)
            .put("graphName", graphName);
          routingContext.request().bodyHandler(bodyHandler -> {
            obj.add(bodyHandler.toJsonArray());
          });
          mongoClient.find("userGraph", query1, res -> {
            if (res.succeeded()) {
              JsonObject query2 = new JsonObject()
                .put("userId", userId)
                .put("graphName", graphName);
              JsonObject update = new JsonObject().put("$push", new JsonObject()
                .put("values", obj.get(0).getValue(0)));
              mongoClient.updateCollection("userGraph", query2, update, res2 -> {
                if (res2.succeeded()) {
                  System.out.println("Updated!");

                  //routingContext.response()
                  //.putHeader("content-type", "application/json; charset=utf-8")
                  //.putHeader("Access-Control-Allow-Origin", "http://localhost:3000")
                  //.putHeader("Access-Control-Allow-Credentials", "true")
                  //.putHeader("Access-Control-Allow-Methods", "PUT")
                  //.end();
                } else {
                  res2.cause().printStackTrace();
                }
              });
              if(obj.get(0).size() > 1) {
                for (int i = 1; i < obj.get(0).size(); i++) {
                  JsonObject updates = new JsonObject().put("$push", new JsonObject()
                    .put("values", obj.get(0).getValue(i)));
                  mongoClient.updateCollection("userGraph", query2, updates, res3 -> {
                    if (res3.succeeded()) {
                      System.out.println("Updated!");
                      //routingContext.response()
                        //.end();
                    } else {
                      res3.cause().printStackTrace();
                    }
                });
              }
              }
              System.out.println(routingContext.response().end(Json.encodePrettily(res.result())));
              routingContext.response().ended();
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

    configureMongoClient();
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
  }

  Future<Void> configureMongoClient(){
    JsonObject mongo = new JsonObject()
      .put("db_name", "GraphDb")
      .put("url", "ac-jfjtte3-shard-00-01.4qlreuh.mongodb.net:27017")
      .put("username", "user")
      .put("password", "pass");
    mongoClient = MongoClient.createShared(vertx, mongo);
    return Future.<Void>succeededFuture();
  }
}

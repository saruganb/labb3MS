package com.example.starter;

import io.vertx.core.Vertx;
public class Starter {

  public static void main(String[] args){
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle(), res ->{
      if(res.succeeded()){
        //log.info("Deployment id is: " + res.result());
      }else{
        //log.info("Deployment failed");
      }
    });
  }
}

package com.example.starter;

import java.util.ArrayList;

public class Graph {
  private Long id;
  private Long userId;
  private String graphName;
  private ArrayList<Integer> list = new ArrayList<>();

  public Graph(Long id, Long userId,String graphName){
    this.id = id;
    this.userId = userId;
    this.graphName = graphName;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getGraphName() {
    return graphName;
  }

  public void setGraphName(String graphName) {
    this.graphName = graphName;
  }

  public ArrayList<Integer> getList() {
    return list;
  }

  public void setList(ArrayList<Integer> list) {
    this.list = list;
  }
}

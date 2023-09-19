package com.soulradio.Classes.SpotifyUser;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
@Component
public class Users {
  Integer userCount = 0;
  public ArrayList<JSONObject> users = new ArrayList<JSONObject>();

  public void addUser() {
    userCount++;
  }

  public void removeUser() {
    userCount--;
  }

  public Integer getUserCount() {
    return userCount;
  }

  public void addNewUser(JSONObject user) {
    users.add(user);
  }

  public void removeUser(JSONObject user) {
    users.remove(user);
  }

  public ArrayList<JSONObject> getUsers() {
    return users;
  }
}

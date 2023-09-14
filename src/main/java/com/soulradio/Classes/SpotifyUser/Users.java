package com.soulradio.Classes.SpotifyUser;

import java.util.ArrayList;

import org.json.simple.JSONObject;

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
}

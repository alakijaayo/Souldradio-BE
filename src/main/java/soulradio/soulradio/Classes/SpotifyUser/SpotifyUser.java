package soulradio.soulradio.Classes.SpotifyUser;

import se.michaelthelin.spotify.model_objects.specification.User;

public class SpotifyUser {
  
  private String accessToken;
  private String refreshToken;
  private String deviceID;
  private User newUser;

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public void setDeviceID(String deviceID) {
    this.deviceID = deviceID;
  }

  public String getDeviceId() {
    return deviceID;
  }

  public void setUser(User user) {
    this.newUser = user;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public User getUser() {
    return newUser;
  }
}

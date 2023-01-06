package soulradio.soulradio.Classes.SpotifyUser;

import se.michaelthelin.spotify.model_objects.specification.User;

public class SpotifyUser {
  
  private String accessToken;
  private String refreshToken;
  private User newUser;
  private String device;

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public void setUser(User user) {
    this.newUser = user;
  }

  public void setDevice(String device) {
    this.device = device;
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

  public String getDevice() {
    return device;
  }
}

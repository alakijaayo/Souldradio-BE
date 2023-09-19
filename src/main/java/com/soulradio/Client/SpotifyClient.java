package com.soulradio.Client;

import java.io.IOException;
import java.net.URI;

import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;
import com.soulradio.Classes.SpotifyUser.SpotifyUser;

// import io.github.cdimascio.dotenv.Dotenv;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

@Component
public class SpotifyClient {
  String accessToken;
  // Dotenv dotenv = Dotenv.configure().load();
  // String clientID = dotenv.get("CLIENT_ID");
  // String secretID = dotenv.get("SECRET_ID");
  String clientID = System.getenv("CLIENT_ID");
  String secretID = System.getenv("SECRET_ID");
  Paging<Track> trackPaging;

  private URI redirectUri = SpotifyHttpManager.makeUri("https://api.soulradiovibe.com/callback");
  
  private SpotifyApi spotifyAPI = new SpotifyApi.Builder()
    .setClientId(clientID)
    .setClientSecret(secretID)
    .setRedirectUri(redirectUri)
    .build();
  
  private AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyAPI.authorizationCodeUri()
    .scope("user-read-private user-read-email user-read-playback-state user-modify-playback-state streaming")
    .show_dialog(true)
    .build();

  public String userLogin() {
    return authorizationCodeUriRequest.execute().toString();
  }

  public void setAuthorizationCode (String code, SpotifyUser user) {
    try {
      AuthorizationCodeCredentials credentials = spotifyAPI.authorizationCode(code).build().execute();
      accessToken = credentials.getAccessToken();
      spotifyAPI.setAccessToken(accessToken);
      spotifyAPI.setRefreshToken(credentials.getRefreshToken());
      user.setAccessToken(accessToken);
      user.setRefreshToken(credentials.getRefreshToken());
      user.setUser(spotifyAPI.getCurrentUsersProfile().build().execute());
      System.out.println("AccessToken received: " + accessToken);
      System.out.println("Expires in: " + credentials.getExpiresIn());
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }
}

package soulradio.soulradio.Client;

import java.io.IOException;
import java.net.URI;
import org.apache.hc.core5.http.ParseException;
// import java.util.Base64;

import org.springframework.stereotype.Component;

import io.github.cdimascio.dotenv.Dotenv;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

@Component
public class SpotifyClient {
  User user;
  String accessToken;
  Dotenv dotenv = Dotenv.configure().load();
  String clientID = dotenv.get("CLIENT_ID");
  String secretID = dotenv.get("SECRET_ID");

  private URI redirectUri = SpotifyHttpManager.makeUri("http://localhost:8080/callback");
  
  private SpotifyApi spotifyAPI = new SpotifyApi.Builder()
    .setClientId(clientID)
    .setClientSecret(secretID)
    .setRedirectUri(redirectUri)
    .build();
  
  private AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyAPI.authorizationCodeUri()
    .scope("user-read-private user-read-email user-read-playback-state streaming")
    .show_dialog(true)
    .build();

  public String userLogin() {
    return authorizationCodeUriRequest.execute().toString();
  }

  public String setAuthorizationCode (String code) {
    try {
      AuthorizationCodeCredentials credentials = spotifyAPI.authorizationCode(code).build().execute();
      accessToken = credentials.getAccessToken();
      spotifyAPI.setAccessToken(accessToken);
      spotifyAPI.setRefreshToken(credentials.getRefreshToken());
      user = spotifyAPI.getCurrentUsersProfile().build().execute();
      System.out.println("AccessToken received: " + accessToken);
      System.out.println("Expires in: " + credentials.getExpiresIn());
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
    
    String encode = "userLoggedIn=true";
    // + user.getDisplayName() + "+" + user.getImages()[0].getUrl() + "+" + accessToken;
    // String encodedString = Base64.getEncoder().encodeToString(encode.getBytes()); 
    String host = "http://localhost:3000/?" + encode;
    return host;
  }
}

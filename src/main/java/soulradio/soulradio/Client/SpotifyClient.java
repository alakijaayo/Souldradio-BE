package soulradio.soulradio.Client;

import java.io.IOException;
import java.net.URI;
import org.apache.hc.core5.http.ParseException;
// import java.util.Base64;

import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;

import io.github.cdimascio.dotenv.Dotenv;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

@Component
public class SpotifyClient {
  User user;
  String accessToken;
  Dotenv dotenv = Dotenv.configure().load();
  String clientID = dotenv.get("CLIENT_ID");
  String secretID = dotenv.get("SECRET_ID");
  Paging<Track> trackPaging;

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

  public void setAuthorizationCode (String code) {
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
  }

  public User getUser() {
    return user;
  }

  public Paging<Track> searchTrack(String track) {
    
    final SearchTracksRequest searchTracksRequest = spotifyAPI.searchTracks(track)
    .market(CountryCode.GB)
    .limit(10)
    .includeExternal("audio")
    .build();

    try {
      trackPaging = searchTracksRequest.execute();

      System.out.println(trackPaging.getItems()[0].getArtists()[0].getName());
      
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }

    return trackPaging;
  }
}

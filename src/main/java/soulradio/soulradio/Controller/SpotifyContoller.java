package soulradio.soulradio.Controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import soulradio.soulradio.Classes.SpotifyUser.SpotifyUser;
import soulradio.soulradio.Client.PlayTrackClient;
import soulradio.soulradio.Client.SpotifyClient;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class SpotifyContoller {
  SpotifyUser spotifyUser = new SpotifyUser();
  
  @Autowired
  SpotifyClient spotifyClient;
  
  @Autowired
  PlayTrackClient playTrackClient;

  
  @GetMapping("/login")
  public RedirectView login() {
    return new RedirectView(spotifyClient.userLogin());
  }

  @GetMapping("/callback")
  public RedirectView getCode(@RequestParam String code) {
    spotifyClient.setAuthorizationCode(code, spotifyUser);
    return new RedirectView( "http://localhost:3000/home");
  }

  @GetMapping("/username")
  public User getUser() {
    playTrackClient.getUserDevice(spotifyUser);
    return spotifyUser.getUser();
  }

  @GetMapping("/token")
  public HashMap<String, String> getAccessToken() {
    HashMap<String, String> token = new HashMap<String, String>();
    token.put("token", spotifyUser.getAccessToken()); 
    return token;
  }

  @GetMapping("/searchtrack")
  public Paging<Track> getTracks(@RequestParam String track) {
    return playTrackClient.searchTrack(spotifyUser.getAccessToken(), track);
  }

  @GetMapping("/play")
  public void playTrack(@RequestParam String trackid) {
    playTrackClient.play(spotifyUser.getAccessToken(), trackid, spotifyUser.getDevice());
  }
}

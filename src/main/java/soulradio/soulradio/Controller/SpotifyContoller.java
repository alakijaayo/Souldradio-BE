package soulradio.soulradio.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import soulradio.soulradio.Client.SpotifyClient;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class SpotifyContoller {

  @Autowired
  SpotifyClient spotifyClient;
  
  @GetMapping("/login")
  public RedirectView login() {
    return new RedirectView(spotifyClient.userLogin());
  }

  @GetMapping("/callback")
  public RedirectView getCode(@RequestParam String code) {
    spotifyClient.setAuthorizationCode(code);
    return new RedirectView( "http://localhost:3000/?userLoggedIn=true");
  }

  @GetMapping("/username")
  public User getUser() {
    return spotifyClient.getUser();
  }

  @GetMapping("/searchtrack")
  public Paging<Track> getTracks(@RequestParam String track) {
    return spotifyClient.searchTrack(track);
  }
}

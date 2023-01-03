package soulradio.soulradio.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

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
  @ResponseBody
  public RedirectView getCode(@RequestParam String code) {
    return new RedirectView(spotifyClient.setAuthorizationCode(code));
  }
}

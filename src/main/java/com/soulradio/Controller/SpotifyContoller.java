package com.soulradio.Controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.soulradio.Classes.MessageBean;
import com.soulradio.Classes.SpotifyUser.Queue;
import com.soulradio.Classes.SpotifyUser.SpotifyUser;
import com.soulradio.Client.PlayTrackClient;
import com.soulradio.Client.SpotifyClient;

import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class SpotifyContoller {
  SpotifyUser spotifyUser = new SpotifyUser();
  
  @Autowired
  SpotifyClient spotifyClient;
  
  @Autowired
  PlayTrackClient playTrackClient;

  @Autowired
  SimpMessagingTemplate template;

  @Autowired
  Queue queue;
  
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

  @PutMapping("/queuetrack")
  public ArrayList<JSONObject> queueTrack(@RequestParam String device_id, @RequestBody String Track){
    spotifyUser.setDeviceID(device_id);
    return playTrackClient.queueTrack(spotifyUser.getAccessToken(), Track, device_id, queue);
  }

  @PutMapping("/play")
  public ArrayList<JSONObject> playNextTrack() {
    String trackString = queue.getNextTrack();
    return playTrackClient.play(spotifyUser.getAccessToken(), trackString, spotifyUser.getDeviceId(), queue);
  }

  @PutMapping("/votes")
  public ArrayList<JSONObject> updateQueue(@RequestBody String Track) {
    int number = Integer.parseInt(queue.getStringValue(Track, "trackNumber"));
    String vote = queue.getStringValue(Track, "vote");
    String key = vote.equals("up") ? "votesUp" : "votesDown";

    return queue.updateQueuedTracks(number, vote, key);
  }

  @MessageMapping("/topic-message")
    @SendTo("/topic/user")
    public MessageBean send(@Payload MessageBean message) {
      return message;
    }
}

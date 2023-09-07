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

  @PutMapping("/play")
  public ArrayList<JSONObject> playNextTrack() {
    String trackString = queue.getNextTrack();
    return playTrackClient.play(spotifyUser.getAccessToken(), trackString, spotifyUser.getDeviceId(), queue);
  }

  @MessageMapping("/sendmessage")
    @SendTo("/topic/messages")
    public MessageBean send(@Payload MessageBean message) {
      return message;
    }
  
  @MessageMapping("/queuetrack")
    @SendTo("/topic/queue")
    public ArrayList<JSONObject> queueTracks(@Payload String Track){
      String device = queue.getStringValue(Track, "device");
      spotifyUser.setDeviceID(device);
      String track = queue.getStringValue(Track, "track");
      return playTrackClient.queueTrack(spotifyUser.getAccessToken(), track, device, queue);
    }
  
  @MessageMapping("/votes")
    @SendTo("/topic/votes")
    public ArrayList<JSONObject> updateQueue(@Payload String Track) {
      int number = Integer.parseInt(queue.getStringValue(Track, "trackNumber"));
      String vote = queue.getStringValue(Track, "vote");
      String key = vote.equals("up") ? "votesUp" : "votesDown";

      return queue.updateQueuedTracks(number, vote, key);
    }
  
  @MessageMapping("/getnexttrack")
    @SendTo("/topic/nexttrack")
    public JSONObject getNextTrack() {
      return queue.getTrack();
    }
}

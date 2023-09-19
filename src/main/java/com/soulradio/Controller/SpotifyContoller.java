package com.soulradio.Controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.soulradio.Classes.MessageBean;
import com.soulradio.Classes.SpotifyUser.Queue;
import com.soulradio.Classes.SpotifyUser.SpotifyUser;
import com.soulradio.Classes.SpotifyUser.Users;
import com.soulradio.Client.PlayTrackClient;
import com.soulradio.Client.SpotifyClient;

import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;

@CrossOrigin(origins = "https://api.soulradiovibe.com")
@RestController
public class SpotifyContoller {
  SpotifyUser spotifyUser = new SpotifyUser();
  
  @Autowired
  SpotifyClient spotifyClient;
  
  @Autowired
  PlayTrackClient playTrackClient;

  @Autowired
  Queue queue;

  @Autowired
  Users users;
  
  @GetMapping("/login")
  public RedirectView login() {
    return new RedirectView(spotifyClient.userLogin());
  }

  @GetMapping("/callback")
  public RedirectView getCode(@RequestParam String code) {
    spotifyClient.setAuthorizationCode(code, spotifyUser);
    return new RedirectView( "https://api.soulradiovibe.com/home");
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
      JSONObject noTrack = new JSONObject();
      noTrack.put("message", "No tracks in queue");

      return queue.getSize() == 0 ? noTrack : queue.getTrack();
    }

  @MessageMapping("/play")
    @SendTo("/topic/play")
    public ArrayList<JSONObject> playNextTrack() {
      String trackString = new String();

      if(queue.getSize() != 0) {
        trackString = queue.getNextTrack();
      }

      return queue.getSize() == 0 ? queue.getQueuedTracks() : playTrackClient.play(spotifyUser.getAccessToken(), trackString, spotifyUser.getDeviceId(), queue);
    }
  
  @MessageMapping("/loggedin")
    @SendTo("/topic/loggedin")
    public JSONObject loggedIn(@Payload String newUser) {
      String username = queue.getStringValue(newUser, "name");
      Boolean isEqual = username.equals(spotifyUser.getUser().getDisplayName());

      if(users.getUserCount() == 0 || !isEqual) {
        users.addUser();
        JSONObject newUsername =  new JSONObject();
        newUsername.put("name", username);
        users.addNewUser(newUsername);
      };

      JSONObject newLogin = new JSONObject();
      newLogin.put("number", users.getUserCount());
      newLogin.put("users", users.getUsers());
      
      return newLogin;
    }

  @MessageMapping("/loggedout")
    @SendTo("/topic/loggedout")
    public JSONObject loggedOut() {
      users.removeUser();
      JSONObject onLogout = new JSONObject();
      onLogout.put("message", spotifyUser.getUser().getDisplayName() + " has logged out!");

      return onLogout;
    }
}

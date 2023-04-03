package soulradio.soulradio.Classes.SpotifyUser;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hc.core5.http.ParseException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import com.google.gson.JsonParser;

public class Queue {
  
  private String accessToken;
  public ArrayList<JSONObject> queuedTracks = new ArrayList<JSONObject>();
  JSONParser getTrack = new JSONParser();
  JSONObject trackDetails;

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public Integer getSize() {
    return queuedTracks.size(); 
  }

  public void clearQueue() {
    queuedTracks.clear();
  }

  public void addToQueue(JSONObject trackInfo) {
    this.queuedTracks.add(trackInfo);
  }

  public String getNextTrack() {
    JSONObject nextTrack = queuedTracks.get(0);
    Object track = nextTrack.get("uri");
    removeTrack();
    return track.toString();
  }

  public void removeTrack() {
    queuedTracks.remove(0);
  }

  public ArrayList<JSONObject> getQueuedTracks() {
    return queuedTracks;
  }

  public JSONObject getTrack(String track) {
    try {
      trackDetails = (JSONObject) getTrack.parse(track);
    } catch (org.json.simple.parser.ParseException e) {
      e.printStackTrace();
    }

    return trackDetails;
  } 

  public void playTrack(String deviceID, String track) {
    final SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setAccessToken(accessToken)
      .build();
      
    final StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
    .startResumeUsersPlayback()
    .device_id(deviceID)
    .uris(JsonParser.parseString(track).getAsJsonArray())
    .build();

    try {
      startResumeUsersPlaybackRequest.execute();
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
  } 
}

package soulradio.soulradio.Classes.SpotifyUser;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

@Component
public class Queue {
  
  public ArrayList<JSONObject> queuedTracks = new ArrayList<JSONObject>();
  JSONParser getTrack = new JSONParser();
  JSONObject trackDetails;


  public Integer getSize() {
    return queuedTracks.size(); 
  }

  public void clearQueue() {
    queuedTracks.clear();
  }

  public void addToQueue(JSONObject trackInfo) {
    this.queuedTracks.add(trackInfo);
  }

  public void removeTrack() {
    queuedTracks.remove(0);
  }

  public ArrayList<JSONObject> getQueuedTracks() {
    return queuedTracks;
  }

  public JSONObject getTrack() {
    return queuedTracks.get(0);  
  }

  public String getNextTrack() {
    JSONObject trackDetails = getTrack();
    Object Trackid = trackDetails.get("uri");
    String trackString = String.valueOf(Trackid);

    return trackString;
  }

  public JSONObject getTrackDetails(String track) {
    try {
      trackDetails = (JSONObject) getTrack.parse(track);
    } catch (org.json.simple.parser.ParseException e) {
      e.printStackTrace();
    }

    return trackDetails;
  } 
}

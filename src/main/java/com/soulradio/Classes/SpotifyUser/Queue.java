package com.soulradio.Classes.SpotifyUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

  public ArrayList<JSONObject> sortQueuedTracks(ArrayList<JSONObject> jsonArray, String key, boolean ascending) {
    Collections.sort(jsonArray, new Comparator<JSONObject>() {
      @Override
      public int compare(JSONObject jsonObject1, JSONObject jsonObject2) {
        Object value1 = jsonObject1.get(key);
        Object value2 = jsonObject2.get(key);
          
        if (value1 instanceof Integer && value2 instanceof Integer) {
          return ascending ? ((Integer) value2).compareTo((Integer) value1) : ((Integer) value1).compareTo((Integer) value2);
      } else if (value1 instanceof Long && value2 instanceof Long) {
          return ascending ? ((Long) value2).compareTo((Long) value1) : ((Long) value1).compareTo((Long) value2);
      } else {
          int compareResult = Long.compare(((Number) value1).longValue(), ((Number) value2).longValue());
          return ascending ? -compareResult : compareResult;
      }
      }
  });

  return jsonArray;
  }

  public String getStringValue(String Track, String key) {
    JSONObject track =  getTrackDetails(Track);
    Object trackDetails = track.get(key);

    return String.valueOf(trackDetails);
  }

  public ArrayList<JSONObject> updateQueuedTracks(int number, String vote, String key) {
    int count = Integer.parseInt(queuedTracks.get(number).get(key).toString());
    Integer updatedCount = count + 1;

    if (vote.equals("up")) {
      queuedTracks.get(number).replace(key, updatedCount);
      sortQueuedTracks(queuedTracks, "votesUp", true);
    } else {
      queuedTracks.get(number).replace(key, updatedCount);
      sortQueuedTracks(queuedTracks, "votesDown", false);
    }
    
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

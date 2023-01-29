package soulradio.soulradio.Client;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hc.core5.http.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import soulradio.soulradio.Classes.SpotifyUser.Queue;

@Component
public class PlayTrackClient {
  Paging<Track> trackList;
  Queue queuedTracks = new Queue();
  ArrayList<JSONObject> testArrayList = new ArrayList<>();
  JSONParser getTrack = new JSONParser();

  
  public Paging<Track> searchTrack(String accessToken, String track) {
    queuedTracks.setAccessToken(accessToken);
    final SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setAccessToken(accessToken)
      .build();
    
      final SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(track)
      .market(CountryCode.GB)
      .limit(10)
      .includeExternal("audio")
      .build();

      try {
        trackList = searchTracksRequest.execute();
  
        System.out.println(trackList.getItems()[0].getArtists()[0].getName());
        
      } catch (IOException | SpotifyWebApiException | ParseException e) {
        System.out.println("Error: " + e.getMessage());
      }

      return trackList;
  }

  public ArrayList<JSONObject> play(String accessToken, String Track, String deviceID) throws org.json.simple.parser.ParseException   {
    final SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setAccessToken(accessToken)
      .build();
    
    JSONObject jsonObject = (JSONObject) getTrack.parse(Track);
    System.out.println(jsonObject);
    Object Trackid = jsonObject.get("uri");
    String trackString = String.valueOf(Trackid);
    
    final GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = spotifyApi
    .getUsersCurrentlyPlayingTrack()
    .market(CountryCode.GB)
    .additionalTypes("track")
    .build();


    final StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
    .startResumeUsersPlayback()
    .device_id(deviceID)
    .uris(JsonParser.parseString(trackString).getAsJsonArray())
    .build();

    try {
      CurrentlyPlaying test = getUsersCurrentlyPlayingTrackRequest.execute();
      if (test == null || (test.getIs_playing() == false && queuedTracks.getSize() == 0)) {
        queuedTracks.clearQueue();
        queuedTracks.playTrack(deviceID, trackString);
        startResumeUsersPlaybackRequest.execute();
      } else if (test.getIs_playing() == true) {
        queuedTracks.addToQueue(jsonObject);
        System.out.println(queuedTracks.getSize());
      } else if (test.getIs_playing() == false) {
        queuedTracks.playTrack(deviceID, queuedTracks.getNextTrack());
        queuedTracks.addToQueue(jsonObject);
      }
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }

    return queuedTracks.getQueuedTracks();
  } 
}

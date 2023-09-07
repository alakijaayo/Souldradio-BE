package com.soulradio.Client;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hc.core5.http.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;
import com.soulradio.Classes.SpotifyUser.Queue;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.GetUsersCurrentlyPlayingTrackRequest;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

@Component
public class PlayTrackClient {
  Paging<Track> trackList;
  JSONParser getTrack = new JSONParser();

  
  public Paging<Track> searchTrack(String accessToken, String track) {
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
        
      } catch (IOException | SpotifyWebApiException | ParseException e) {
        System.out.println("Error: " + e.getMessage());
      }

      return trackList;
  }

  public ArrayList<JSONObject> queueTrack(String accessToken, String Track, String deviceID, Queue queue) {
    final SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setAccessToken(accessToken)
      .build();

      System.out.println(Track);

      JSONObject trackDetails =  queue.getTrackDetails(Track);
      String trackString = queue.getStringValue(Track, "uri");

      final GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = spotifyApi
      .getUsersCurrentlyPlayingTrack()
      .market(CountryCode.GB)
      .additionalTypes("track")
      .build();
    
      try {
        CurrentlyPlaying track = getUsersCurrentlyPlayingTrackRequest.execute();
        if (track == null || (track.getIs_playing() == false && queue.getSize() == 0)) {
          queue.clearQueue();
          play(accessToken, trackString, deviceID, queue);
        } else {
          queue.addToQueue(trackDetails);
        }
      } catch (IOException | SpotifyWebApiException | ParseException e) {
        System.out.println("Error: " + e.getMessage());
      }

      return queue.getQueuedTracks();
  };

    public ArrayList<JSONObject> play(String accessToken, String track, String deviceID, Queue queue) {
      final SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setAccessToken(accessToken)
      .build();

      final GetUsersCurrentlyPlayingTrackRequest getUsersCurrentlyPlayingTrackRequest = spotifyApi
      .getUsersCurrentlyPlayingTrack()
      .market(CountryCode.GB)
      .additionalTypes("track")
      .build();

      final StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
      .startResumeUsersPlayback()
      .device_id(deviceID)
      .uris(JsonParser.parseString(track).getAsJsonArray())
      .build();

      if (queue.getSize() != 0) {
        queue.removeTrack();
      }

      try {  
        getUsersCurrentlyPlayingTrackRequest.execute();
        startResumeUsersPlaybackRequest.execute();
      } catch (IOException | SpotifyWebApiException | ParseException e) {
        System.out.println("Error: " + e.getMessage());
      }

      return  queue.getQueuedTracks();
    }
}

package soulradio.soulradio.Client;

import java.io.IOException;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;

import com.google.gson.JsonParser;
import com.neovisionaries.i18n.CountryCode;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;

@Component
public class PlayTrackClient {
  Paging<Track> trackList;

  
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
  
        System.out.println(trackList.getItems()[0].getArtists()[0].getName());
        
      } catch (IOException | SpotifyWebApiException | ParseException e) {
        System.out.println("Error: " + e.getMessage());
      }
  
      return trackList;
  }

  public void play(String accessToken, String id, String deviceID)   {
    final SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setAccessToken(accessToken)
      .build();


    final StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
    .startResumeUsersPlayback()
    .device_id(deviceID)
    .uris(JsonParser.parseString(id).getAsJsonArray())
    .build();

    try {
      startResumeUsersPlaybackRequest.execute();
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
  } 
}

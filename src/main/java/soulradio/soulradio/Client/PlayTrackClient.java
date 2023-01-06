package soulradio.soulradio.Client;

import java.io.IOException;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;

import com.neovisionaries.i18n.CountryCode;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.Device;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.player.AddItemToUsersPlaybackQueueRequest;
import se.michaelthelin.spotify.requests.data.player.GetUsersAvailableDevicesRequest;
import se.michaelthelin.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import soulradio.soulradio.Classes.SpotifyUser.SpotifyUser;

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

  public void getUserDevice(SpotifyUser user) {

    final SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setAccessToken(user.getAccessToken())
      .build();

    final GetUsersAvailableDevicesRequest getUsersAvailableDevicesRequest = spotifyApi
    .getUsersAvailableDevices()
    .build();

    try {
      final Device[] devices = getUsersAvailableDevicesRequest.execute();
      user.setDevice(devices[0].getId());
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public void play(String accessToken, String id, String deviceID) {
    final SpotifyApi spotifyApi = new SpotifyApi.Builder()
      .setAccessToken(accessToken)
      .build();

    final AddItemToUsersPlaybackQueueRequest addItemToUsersPlaybackQueueRequest = spotifyApi
    .addItemToUsersPlaybackQueue(id)
    .device_id(deviceID)
    .build();

    final StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = spotifyApi
    .startResumeUsersPlayback()
    .device_id(deviceID)
    .build();

    try {
      addItemToUsersPlaybackQueueRequest.execute();
      startResumeUsersPlaybackRequest.execute();
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
  } 
}

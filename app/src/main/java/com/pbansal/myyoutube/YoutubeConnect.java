package com.pbansal.myyoutube;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Joiner;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Surabhi on 07/10/2015.
 */
public class YoutubeConnect {

    private static final String TAG = "YoutubeConnect";
    private YouTube youtube;
    private YouTube.Search.List searchQuery;
    private YouTube.Playlists.List playlistQuery;
    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
    DateFormat df = new SimpleDateFormat("yyyyMMdd");
    GoogleCredential credential = new GoogleCredential();

    //     Your developer key goes here
    public static final String KEY
            = "YOUR DEVELOPER KEY HERE";

    public YoutubeConnect() {
        youtube = YouTubeAPI.getYoutube();
    }

    public List<VideoInformationModel> search(String keywords) {
        try {
            searchQuery = youtube.search().list("id,snippet");
            searchQuery.setType("video");
            searchQuery.setFields("items(id/videoId)");
            searchQuery.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            searchQuery.setQ(keywords);
            SearchListResponse response = searchQuery.execute();
            List<SearchResult> results = response.getItems();
            List<String> videoIds = new ArrayList<String>();

            List<VideoInformationModel> items = new ArrayList<VideoInformationModel>();
            for (SearchResult result : results) {
                videoIds.add(result.getId().getVideoId());
            }
            return getVideoInformation(videoIds);
        } catch (IOException e) {
            Log.e(TAG, "Could not search videos: " + e);
            return null;
        }
    }

    public List<VideoInformationModel> searchPlaylist(String playlistName) {
        try {
            String playlistId = "";
            Playlist playlist = null;

            //To Get List of Playlist of Authorized Users
            List<Playlist> results = getAllPlaylists();
            for (Playlist result : results) {
                if (result.getSnippet().getTitle().equals(playlistName)) {
                    playlistId = result.getId();
                    playlist = result;
                    break;
                }
            }
            if (playlist != null) {
                //To get List of Videos of a Particular Playlist
                PlaylistItemListResponse playlistItemListResponse = youtube.playlistItems()
                        .list("id, snippet, contentDetails")
                        .setPlaylistId(playlistId)
                        .setFields("items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo")
                        .setMaxResults((long) 10)
                        .execute();
                List<PlaylistItem> playlistItemListResponseItems = playlistItemListResponse.getItems();
                List<String> videoIds = new ArrayList<String>();
                for (PlaylistItem result : playlistItemListResponseItems) {
                    videoIds.add(result.getContentDetails().getVideoId());
                }
                return getVideoInformation(videoIds);
            }
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Could not search videos: " + e);
            return null;
        }
    }

    private List<VideoInformationModel> getVideoInformation(List<String> videoIds) {
        List<VideoInformationModel> items = new ArrayList<VideoInformationModel>();
        Joiner stringJoiner = Joiner.on(',');
        String videoId = stringJoiner.join(videoIds);
        YouTube.Videos.List listVideoRequest;
        try {
            listVideoRequest = youtube.videos()
                    .list("id, snippet, statistics")
                    .setFields("items(id,snippet,statistics)")
                    .setId(videoId);

            VideoListResponse listResponse = listVideoRequest.execute();
            List<Video> videos = listResponse.getItems();
            for (Video result : videos) {
                VideoInformationModel item = new VideoInformationModel();
                item.setTitle(result.getSnippet().getTitle());
                item.setDescription(result.getSnippet().getDescription());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setNumberOfViews(result.getStatistics().getViewCount().toString());
                item.setPublishedDate(result.getSnippet().getPublishedAt().toString());
                item.setId(result.getId());
                items.add(item);
            }
            return items;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Playlist> getAllPlaylists() {
        try {
            playlistQuery = youtube.playlists()
                    .list("id, snippet")
                    .setFields("items(id, snippet)")
                    .setMine(true);

            PlaylistListResponse response = playlistQuery.execute();
            List<Playlist> results = response.getItems();
            return results;
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
    }

    public String insertPlaylistItem(String playlistId, String videoId) {

        if(!playlistId.isEmpty()) {
            // Define a resourceId that identifies the video being added to the
            // playlist.
            ResourceId resourceId = new ResourceId();
            resourceId.setKind("youtube#video");
            resourceId.setVideoId(videoId);

            // Set fields included in the playlistItem resource's "snippet" part.
            PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
            playlistItemSnippet.setTitle("First video in the test playlist");
            playlistItemSnippet.setPlaylistId(playlistId);
            playlistItemSnippet.setResourceId(resourceId);

            // Create the playlistItem resource and set its snippet to the
            // object created above.
            PlaylistItem playlistItem = new PlaylistItem();
            playlistItem.setSnippet(playlistItemSnippet);

            try {
                // Call the API to add the playlist item to the specified playlist.
                // In the API call, the first argument identifies the resource parts
                // that the API response should contain, and the second argument is
                // the playlistx item being inserted.
                YouTube.PlaylistItems.Insert playlistItemsInsertCommand =
                        youtube.playlistItems().insert("snippet,contentDetails", playlistItem);
                PlaylistItem returnedPlaylistItem = playlistItemsInsertCommand.execute();

                // Print data from the API response and return the new playlist
                // item's unique playlistItem ID.

                System.out.println("New PlaylistItem name: " + returnedPlaylistItem.getSnippet().getTitle());
                System.out.println(" - Video id: " + returnedPlaylistItem.getSnippet().getResourceId().getVideoId());
                System.out.println(" - Posted: " + returnedPlaylistItem.getSnippet().getPublishedAt());
                System.out.println(" - Channel: " + returnedPlaylistItem.getSnippet().getChannelId());
                return returnedPlaylistItem.getId();
            }
            catch (IOException e){
                Log.e(TAG, e.getMessage());
                return null;
            }
        }else{
            return null;
        }

    }


}
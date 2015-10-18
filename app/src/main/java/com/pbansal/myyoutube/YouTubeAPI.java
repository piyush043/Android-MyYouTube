package com.pbansal.myyoutube;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

/**
 * Created by pbansal on 10/16/15.
 */
public class YouTubeAPI {
    private static YouTube youtube = null;

    public YouTubeAPI(GoogleCredential credential){
        if(this.youtube==null){
            this.youtube = new YouTube.Builder(
                    new NetHttpTransport(),
                    new JacksonFactory(),
                    credential
            ).setApplicationName("My YouTube").build();
        }
    }
    public static YouTube getYoutube(){
        return  youtube;
    }
}

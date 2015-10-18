package com.pbansal.myyoutube;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.services.youtube.model.Playlist;

import java.util.List;

/**
 * Created by pbansal on 10/16/15.
 */
public class PlaylistFragment extends Fragment{
    private RecyclerView recyclerView;
    private PlaylistAdapter playlistAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_playlist, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.playlist_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        getPlaylists();
        return layout;
    }

    public void getPlaylists() {
        new RetrievePlaylists().execute();
    }

    class RetrievePlaylists extends AsyncTask<String, Void, List<Playlist>> {

        @Override
        protected List<Playlist> doInBackground(String... params) {
            YoutubeConnect yc = new YoutubeConnect();
            return yc.getAllPlaylists();
        }

        @Override
        protected void onPostExecute(List<Playlist> playlists) {
            super.onPostExecute(playlists);
            playlistAdapter = new PlaylistAdapter(getActivity(), playlists);
            recyclerView.setAdapter(playlistAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

}

package com.pbansal.myyoutube;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pbansal on 10/7/15.
 */
public class ListFragment extends Fragment {
    private RecyclerView recyclerView;
    private InformationAdapter informationAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.video_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        getPlaylistVideos("SJSU-CMPE-277");
        return layout;
    }

    public void getPlaylistVideos(String playlistName) {
        new RetrievePlaylistItems().execute(playlistName);
    }

    class RetrievePlaylistItems extends AsyncTask<String, Void, List<VideoInformationModel>> {

        @Override
        protected List<VideoInformationModel> doInBackground(String... params) {
            YoutubeConnect yc = new YoutubeConnect();
            return yc.searchPlaylist(params[0]);
        }

        @Override
        protected void onPostExecute(List<VideoInformationModel> videoInformationModels) {
            super.onPostExecute(videoInformationModels);
            informationAdapter = new InformationAdapter(getActivity(), videoInformationModels);
            recyclerView.setAdapter(informationAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        }
    }
}

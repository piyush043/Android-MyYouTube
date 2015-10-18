package com.pbansal.myyoutube;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pbansal on 10/7/15.
 */

public class SearchFragment extends Fragment {

    private EditText searchInput;
    private RecyclerView recyclerView;
    private InformationAdapter informationAdapter;
    private Handler handler;
    private List<VideoInformationModel> searchResults;

    public static SearchFragment getInstance(int position) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_search, container, false);
        searchInput = (EditText) layout.findViewById(R.id.search_input);
        recyclerView = (RecyclerView) layout.findViewById(R.id.search_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        handler = new Handler();

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchOnYoutube(v.getText().toString());
                    return false;
                }
                return true;
            }
        });

        informationAdapter = new InformationAdapter(getActivity(), searchResults);
        recyclerView.setAdapter(informationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("search_input", searchInput.getText());
    }


    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){
                YoutubeConnect yc = new YoutubeConnect();
                searchResults = yc.search(keywords);
                handler.post(new Runnable(){
                    public void run(){
                        updateSearchResults();
//                        updateVideosFound();
                    }
                });
            }
        }.start();
    }

    private void updateSearchResults(){
        informationAdapter = new InformationAdapter(getActivity(), searchResults);
        recyclerView.setAdapter(informationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}
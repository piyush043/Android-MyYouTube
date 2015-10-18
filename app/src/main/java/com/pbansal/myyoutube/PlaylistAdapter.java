package com.pbansal.myyoutube;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.Playlist;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

/**
 * Created by pbansal on 10/6/15.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    List<Playlist> playlists = Collections.emptyList();
    List<?> data = Collections.emptyList();
    private Context context;

    public PlaylistAdapter(Context context, List<?> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = inflater.inflate(R.layout.playlist_item, parent, false);
        Log.d("------Piyush", "onCreateViewHolder Called from Playlist Adapter");
        MyViewHolder viewHolder = new MyViewHolder(row);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Playlist current = (Playlist) data.get(position);
        Log.d("------Piyush", "onBindViewHolder Called " + position);
        holder.title.setText(current.getSnippet().getTitle());
        holder.description.setText(current.getSnippet().getDescription());
        holder.publishedDate.setText(current.getSnippet().getPublishedAt().toString());
//        holder.numberOfViews.setText(current.getNumberOfViews());
        Picasso.with(context).load(current.getSnippet().getThumbnails().getDefault().getUrl()).into(holder.icon);
//        holder.icon.setImageResource();
    }

    @Override
    public int getItemCount() {
        if(data!=null)
            return data.size();
        else
            return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title, description, publishedDate, numberOfVideos;

        public MyViewHolder(View itemView) {
            super(itemView);
//            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.playlistTitle);
            description = (TextView) itemView.findViewById(R.id.playlistDescription);
            icon = (ImageView) itemView.findViewById(R.id.playlistIcon);
            publishedDate = (TextView) itemView.findViewById(R.id.playlistPublishedDate);
            numberOfVideos = (TextView) itemView.findViewById(R.id.playlistNumberOfVideos);
        }
/*
        @Override
        public void onClick(View v) {
            VideoInformationModel current = (VideoInformationModel) data.get(getAdapterPosition());

            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("PLAYLIST_ID", current.getId());
            intent.putExtra("PLAYLIST_TITLE", current.getTitle());
            intent.putExtra("PLAYLIST_DESCRIPTION", current.getDescription());
            intent.putExtra("PLAYLIST_PUB_DATE", current.getPublishedDate());
//            intent.putExtra("VIDEO_VIEWS",current.getNumberOfViews());
            context.startActivity(intent);
//            context.startActivity(new Intent(context, LoginActivity.class));
        }
    */
    }
}

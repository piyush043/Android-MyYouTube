package com.pbansal.myyoutube;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by pbansal on 10/6/15.
 */
public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    List<VideoInformationModel> videos = Collections.emptyList();
    List<?> data = Collections.emptyList();
    private Context context;

    public InformationAdapter(Context context, List<?> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = inflater.inflate(R.layout.list_item, parent, false);
        Log.d("------Piyush", "onCreateViewHolder Called  from Information Adapter");
        MyViewHolder viewHolder = new MyViewHolder(row);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        VideoInformationModel current = (VideoInformationModel) data.get(position);
        Log.d("------Piyush", "onBindViewHolder Called " + position);
        holder.title.setText(current.getTitle());
        holder.description.setText(current.getDescription());
        holder.publishedDate.setText(current.getPublishedDate());
        holder.numberOfViews.setText(current.getNumberOfViews());
        Picasso.with(context).load(current.getThumbnailURL()).into(holder.icon);
//        holder.icon.setImageResource(current.getThumbnailURL());
    }

    @Override
    public int getItemCount() {
        if(data!=null)
            return data.size();
        else
            return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView icon;
        TextView title, description, publishedDate, numberOfViews;
        ToggleButton toggleFavorite;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.listTitle);
            description = (TextView) itemView.findViewById(R.id.listDescription);
            icon = (ImageView) itemView.findViewById(R.id.listIcon);
            publishedDate = (TextView) itemView.findViewById(R.id.listPublishedDate);
            numberOfViews = (TextView) itemView.findViewById(R.id.listNumberOfViews);
            toggleFavorite = (ToggleButton) itemView.findViewById(R.id.listToggleFavoriteBtn);
            toggleFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        YoutubeConnect yc = new YoutubeConnect();
                        VideoInformationModel video = (VideoInformationModel)data.get(getAdapterPosition());
                        String videoId = video.getId();
                        String playlistId = "PLp5jGxpQLK1VNmnUBUHJ4SemmfuzYnHgq";
                        try {
                            new InsertVideo().execute(playlistId,videoId);
                        } catch (Exception e) {
                            Log.e("Error---" ,e.getMessage());
                        }

                    }else {

                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            VideoInformationModel current = (VideoInformationModel) data.get(getAdapterPosition());

            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("VIDEO_ID", current.getId());
            intent.putExtra("VIDEO_TITLE", current.getTitle());
            intent.putExtra("VIDEO_DESCRIPTION", current.getDescription());
            intent.putExtra("VIDEO_PUB_DATE", current.getPublishedDate());
            intent.putExtra("VIDEO_VIEWS",current.getNumberOfViews());
            context.startActivity(intent);
        }
    }

    class InsertVideo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            YoutubeConnect yc = new YoutubeConnect();
            return yc.insertPlaylistItem(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String playlistId) {
            super.onPostExecute(playlistId);

        }
    }
}

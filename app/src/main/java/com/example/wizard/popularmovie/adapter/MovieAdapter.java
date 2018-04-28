package com.example.wizard.popularmovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wizard.popularmovie.DetailActivity;
import com.example.wizard.popularmovie.Models.Movies;
import com.example.wizard.popularmovie.R;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {
    private Context mContext;
    private List<Movies> moviesList;
    public MovieAdapter(Context mContext,List<Movies> moviesList){
        this.mContext=mContext;
        this.moviesList=moviesList;
    }


    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(moviesList.get(position).getTitle());
        String vote=Double.toString(moviesList.get(position).getVoteAverage());
        holder.userRating.setText(vote);
        Glide.with(mContext).load("http://image.tmdb.org/t/p/w185/"+moviesList.get(position).getPosterPath()).placeholder(R.drawable.load).into(holder.thumbnail);


    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title,userRating;
        public ImageView thumbnail;
        public  MyViewHolder(View view){
            super(view);
            title=view.findViewById(R.id.title);
            userRating=view.findViewById(R.id.user_rating);
            thumbnail=view.findViewById(R.id.thumbnail);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        Movies clickedItem=moviesList.get(pos);
                        Intent intent=new Intent(mContext, DetailActivity.class);
                        intent.putExtra("original_title",moviesList.get(pos).getTitle());
                        intent.putExtra("id",moviesList.get(pos).getId());
                        intent.putExtra("poster_path",moviesList.get(pos).getPosterPath());
                        intent.putExtra("overview",moviesList.get(pos).getOverview());
                        intent.putExtra("user_rating",Double.toString(moviesList.get(pos).getVoteAverage()));
                        intent.putExtra("release_date",moviesList.get(pos).getReleaseDate());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }

                }
            });

        }
    }
}

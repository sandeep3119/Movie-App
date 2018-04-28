package com.example.wizard.popularmovie.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wizard.popularmovie.Models.Trailer;
import com.example.wizard.popularmovie.R;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {
    private Context context;
    private List<Trailer> trailerList;
    public TrailerAdapter(Context context, List<Trailer> trailerList){
        this.context=context;
        this.trailerList=trailerList;
    }
    @Override
    public TrailerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.MyViewHolder holder, int position) {
    holder.title.setText(trailerList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.trailer_name);
            thumbnail=itemView.findViewById(R.id.thumbnail);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION){
                        Trailer clickedItem=trailerList.get(pos);
                        String videoId=trailerList.get(pos).getKey();
                        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+videoId));
                        intent.putExtra("VIDEO_ID",videoId);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}

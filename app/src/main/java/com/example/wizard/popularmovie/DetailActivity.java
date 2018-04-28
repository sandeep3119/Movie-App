package com.example.wizard.popularmovie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.wizard.popularmovie.Models.Movies;
import com.example.wizard.popularmovie.Models.Trailer;
import com.example.wizard.popularmovie.Models.TrailerResponse;
import com.example.wizard.popularmovie.adapter.TrailerAdapter;
import com.example.wizard.popularmovie.api.Client;
import com.example.wizard.popularmovie.api.Service;
import com.example.wizard.popularmovie.data.FavoriteDbHelper;
import com.example.wizard.popularmovie.data.FavoriteMovies;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    TextView movieName,plotSynopsis,userRating,releaseDate;
    ImageView poster;
    MaterialFavoriteButton favoriteButton;
    private RecyclerView recyclerView;
    private TrailerAdapter adapter;
    private List<Trailer> trailerList;
    private FavoriteDbHelper favoriteDbHelper;
    private Movies favoriteMovies;
    private  final AppCompatActivity activity=DetailActivity.this;
    private SQLiteDatabase mDb;
    int movie_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        android.support.v7.widget.Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FavoriteDbHelper dbHelper = new FavoriteDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        poster=findViewById(R.id.thumbnail_header);
        movieName=findViewById(R.id.title);
        plotSynopsis=findViewById(R.id.plot_synopsis);
        releaseDate=findViewById(R.id.release_date);
        userRating=findViewById(R.id.user_rating);
        Intent previousActivity=getIntent();
        if(previousActivity.hasExtra("original_title")){
            String thumbnail=getIntent().getExtras().getString("poster_path");
            String movieTitle=getIntent().getExtras().getString("original_title");
            String synopsis=getIntent().getExtras().getString("overview");
            String release=getIntent().getExtras().getString("release_date");
            movie_id=getIntent().getExtras().getInt("id");
            String rating=getIntent().getExtras().getString("user_rating");
            Glide.with(this).load("http://image.tmdb.org/t/p/w500/"+thumbnail).placeholder(R.drawable.load).into(poster);
            movieName.setText(movieTitle);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(release);
            ((CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar)).setTitle(movieTitle);

        }else{
            Toast.makeText(this,"No api data..",Toast.LENGTH_SHORT).show();

        }
        favoriteButton=findViewById(R.id.favorite);
        String movieTitle=getIntent().getExtras().getString("original_title");
        if (Exists(movieTitle )){
            favoriteButton.setFavorite(true);
            favoriteButton.setOnFavoriteChangeListener(
                    new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                            if (favorite == true) {
                                saveFavorite();
                                Snackbar.make(buttonView, "Added to Favorite",
                                        Snackbar.LENGTH_SHORT).show();
                            } else {
                                favoriteDbHelper = new FavoriteDbHelper(DetailActivity.this);
                                favoriteDbHelper.deleteFavorite(movie_id);
                                Snackbar.make(buttonView, "Removed from Favorite",
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });


        }else {
            favoriteButton.setOnFavoriteChangeListener(
                    new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                            if (favorite == true) {
                                saveFavorite();
                                Snackbar.make(buttonView, "Added to Favorite",
                                        Snackbar.LENGTH_SHORT).show();
                            } else {
                                int movie_id = getIntent().getExtras().getInt("id");
                                favoriteDbHelper = new FavoriteDbHelper(DetailActivity.this);
                                favoriteDbHelper.deleteFavorite(movie_id);
                                Snackbar.make(buttonView, "Removed from Favorite",
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });


        }
        initView();

    }
    public boolean Exists(String searchItem) {

        String[] projection = {
                FavoriteMovies.FavoriteEntry._ID,
                FavoriteMovies.FavoriteEntry.COLUMN_MOVIEID,
                FavoriteMovies.FavoriteEntry.COLUMN_TITLE,
                FavoriteMovies.FavoriteEntry.COLUMN_USERRATING,
                FavoriteMovies.FavoriteEntry.COLUMN_POSTER_PATH,
                FavoriteMovies.FavoriteEntry.COLUMN_PLOT_SYNOPSIS

        };
        String selection = FavoriteMovies.FavoriteEntry.COLUMN_TITLE + " =?";
        String[] selectionArgs = { searchItem };
        String limit = "1";

        Cursor cursor = mDb.query(FavoriteMovies.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public void saveFavorite(){
        favoriteDbHelper=new FavoriteDbHelper(activity);
        favoriteMovies=new Movies();
        int movieId=getIntent().getExtras().getInt("id");
        Double rate= Double.valueOf(getIntent().getExtras().getString("user_rating"));
        Log.d("Rating",String.valueOf(rate));
        String poster=getIntent().getExtras().getString("poster_path");
        String title=getIntent().getExtras().getString("original_title");
        Log.d("MOVIEEEEEEEE", title);
        favoriteMovies.setId(movieId);
        favoriteMovies.setTitle(title);
        favoriteMovies.setPosterPath(poster);
        favoriteMovies.setOverview(plotSynopsis.getText().toString());
        favoriteMovies.setVoteAverage(rate);
        favoriteDbHelper.addFavorite(favoriteMovies);

    }

    private void initView(){
        trailerList=new ArrayList<>();
        adapter=new TrailerAdapter(this,trailerList);
        recyclerView=findViewById(R.id.recycle_view_trailer);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        loadTrailer();


    }
    private void loadTrailer(){
        int movieId=getIntent().getExtras().getInt("id");
        Log.d("TAG", "loadTrailer: "+movieId);
        try{
            if(BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()){
                Toast.makeText(this,"Please obtain api key",Toast.LENGTH_SHORT).show();

            }
            Client client=new Client();
            Service apiService=Client.getClient().create(Service.class);
            retrofit2.Call<TrailerResponse> call=apiService.getMovieTrailer(movieId,BuildConfig.THE_MOVIE_DB_API_KEY);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(retrofit2.Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailers=response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(),trailers));
                    recyclerView.smoothScrollToPosition(0);
                    Log.e("POSERR",trailers.toString());
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                Toast.makeText(DetailActivity.this,"Error Loading Trailer",Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            Toast.makeText(DetailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}

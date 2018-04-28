package com.example.wizard.popularmovie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.wizard.popularmovie.Models.MovieResponse;
import com.example.wizard.popularmovie.Models.Movies;
import com.example.wizard.popularmovie.adapter.MovieAdapter;
import com.example.wizard.popularmovie.api.Client;
import com.example.wizard.popularmovie.api.Service;
import com.example.wizard.popularmovie.data.FavoriteDbHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movies> moviesList;
    private AppCompatActivity activity=MainActivity.this;
    ProgressDialog progressDialog;
    private FavoriteDbHelper favoriteDbHelper;
    private SwipeRefreshLayout swipeContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Fetching Movies"); // Setting Message
        progressDialog.setMessage("Loading ....."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);

        initViews();

    }

    public void initViews(){
        progressDialog.show();
        recyclerView=findViewById(R.id.recycle_view);
        moviesList=new ArrayList<>();
        adapter=new MovieAdapter(this,moviesList);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        favoriteDbHelper=new FavoriteDbHelper(activity);
        swipeContainer=findViewById(R.id.main_content);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
                Toast.makeText(MainActivity.this,"Movies Refreshed",Toast.LENGTH_SHORT).show();
            }
        });
        checkSortOrder();

    }
    private void initViews2(){

        progressDialog.show(); // Display Progress Dialog
        recyclerView=findViewById(R.id.recycle_view);
        moviesList=new ArrayList<>();
        adapter=new MovieAdapter(this,moviesList);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        favoriteDbHelper=new FavoriteDbHelper(activity);
        getAllFavorite();



    }

    @SuppressLint("StaticFieldLeak")
    private void getAllFavorite(){
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                moviesList.clear();
                moviesList.addAll(favoriteDbHelper.getAllFavorite());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

        }.execute();
    }
    private void loadMovies(){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()){
                Toast.makeText(this,"Get an api key First",Toast.LENGTH_LONG).show();
                return;
            }
            Client client=new Client();
            Service apiService=Client.getClient().create(Service.class);
            retrofit2.Call<MovieResponse> call=apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_KEY);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(retrofit2.Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movies> movies=response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(getApplicationContext(),movies));
                    recyclerView.smoothScrollToPosition(0);
                    if(swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }progressDialog.dismiss();

                }

                @Override
                public void onFailure(retrofit2.Call<MovieResponse> call, Throwable t) {
                    Log.d("Error","Error fetching");
                    Toast.makeText(MainActivity.this,"Error Fetching data",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();


                }
            });



        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }

    }
    private void loadMovies1(){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()){
                Toast.makeText(this,"Get an api key First",Toast.LENGTH_LONG).show();
                return;
            }
            Client client=new Client();
            Service apiService=Client.getClient().create(Service.class);
            retrofit2.Call<MovieResponse> call=apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_KEY);
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(retrofit2.Call<MovieResponse> call, Response<MovieResponse> response) {
                    List<Movies> movies=response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(getApplicationContext(),movies));
                    recyclerView.smoothScrollToPosition(0);
                    if(swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                    progressDialog.dismiss();

                }

                @Override
                public void onFailure(retrofit2.Call<MovieResponse> call, Throwable t) {
                    Log.d("Error","Error fetching");
                    Toast.makeText(MainActivity.this,"Error Fetching data",Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();


                }
            });



        }catch (Exception e){
            Log.d("Error",e.getMessage());
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                Intent intent=new Intent(this,SettingActivity.class);
                startActivity(intent);
                return  true;
                default:
                    return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        checkSortOrder();

    }
    private void checkSortOrder(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder=preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_sort_popular)
        );
        if(sortOrder.equals(this.getString(R.string.pref_sort_popular))){
            loadMovies();
        }
        else if(sortOrder.equals(this.getString(R.string.favorite))){
            initViews2();
        }
        else {
            loadMovies1();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(moviesList.isEmpty()){
            checkSortOrder();
        }else{
            checkSortOrder();


        }
    }
}

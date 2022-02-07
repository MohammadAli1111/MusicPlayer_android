package com.mohammad.musicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Controller.MusicAdapter;
import Model.Music;

public class MainActivity extends AppCompatActivity {

    boolean nextloopState=false,nextRanDOM=false;
    private MediaPlayer mediaPlayer=new MediaPlayer();
    private MusicAdapter musicAdapter;
    private ImageButton playpause,loop,RanDOM;
    private TextView music_name,finalTimeText,startTimeText;
    private SearchView searchView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        isStoragePermissionGranted();
        RecyclerView recyclerView = findViewById(R.id.RecyclerViewId);
        playpause=findViewById(R.id.playButton);
        loop=findViewById(R.id.loop);
        RanDOM=findViewById(R.id.idRanDOM);
        SeekBar seekBar = findViewById(R.id.seekBar);
        music_name=findViewById(R.id.music_name);
        finalTimeText=findViewById(R.id.finalTimeText);
        startTimeText=findViewById(R.id.startTimeText);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mediaPlayer=new MediaPlayer();
        musicAdapter = new MusicAdapter(this,getAllMusic(),mediaPlayer, seekBar,playpause,music_name,
                startTimeText,finalTimeText);
        recyclerView.setAdapter(musicAdapter);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }
        });



        searchView=findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                musicAdapter.Search(query);
                recyclerView.setAdapter(musicAdapter);
                musicAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                musicAdapter.Search(query);
                recyclerView.setAdapter(musicAdapter);
                musicAdapter.notifyDataSetChanged();
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                musicAdapter = new MusicAdapter(getApplicationContext(),getAllMusic(),mediaPlayer, seekBar,playpause,
                        music_name,
                        startTimeText,finalTimeText);
                recyclerView.setAdapter(musicAdapter);
                musicAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    public void play_pause(View view){
        musicAdapter.play_pause();
    }

    public void next(View view){
        musicAdapter.next();
    }

    public void previous(View view){
        musicAdapter.previous();
    }

    public void Loop(View view) {
        if(!nextloopState){
            RanDOM.setImageResource(R.drawable.shuffle);
            nextloopState=true;
            loop.setImageResource(R.drawable.infinity_action);
            musicAdapter.LoopMediaPlayer();
        }else {
            nextloopState=false;
            loop.setImageResource(R.drawable.infinity);
            musicAdapter.nextAutoMediaPlayer();
        }
    }
    public void nextRanDOM(View view) {
        if(!nextRanDOM){
            loop.setImageResource(R.drawable.infinity);
            nextRanDOM=true;
            RanDOM.setImageResource(R.drawable.shuffle_action);
            musicAdapter.nextRanDom();

        }else{
            nextRanDOM=false;
            musicAdapter.nextAutoMediaPlayer();
            RanDOM.setImageResource(R.drawable.shuffle);

        }
    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<Music> getAllMusic(){
        List<Music>musicList=new ArrayList<>();
        //your database elect statement
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        //your projection statement
        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        //query
        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);


        while(cursor.moveToNext()){

            String name=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            String path=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String duration=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            musicList.add(new Music(name,path,duration));

        }

        return musicList;
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }



}
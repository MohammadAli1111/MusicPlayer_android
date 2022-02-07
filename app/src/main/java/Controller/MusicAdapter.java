package Controller;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammad.musicplayer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Model.Music;
import Utils.Utils;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyRecyclerView> {
   private List<Music> music=new ArrayList<>();
   private Context context;
   private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private int id=0;
    private Handler myHandler = new Handler();
    private SeekBar seekBarTime;
    Runnable UpdateSongTime = new Runnable(){

        @Override
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            seekBarTime.setProgress((int)startTime);
            startTimeText.setText(Utils.milliSecondsToTimer((long) startTime));
            myHandler.postDelayed(this, 100);
        }
    };
    private TextView Title,startTimeText,finalTimeText;
    private ImageButton playpause;

    public MusicAdapter(Context context,List<Music> music,MediaPlayer mediaPlayer,SeekBar seekBarTime
    ,ImageButton playpause,TextView title,TextView startTimeText,TextView finalTimeText) {
        this.context = context;
        this.music = music;
        this.mediaPlayer=mediaPlayer;
        this.seekBarTime=seekBarTime;
        this.Title=title;
        this.startTimeText=startTimeText;
        this.finalTimeText=finalTimeText;
        this.playpause=playpause;
    }

    @NonNull
    @Override
    public MyRecyclerView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item,parent,false);
        return new MyRecyclerView(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerView holder, int position) {
        holder.name.setText(music.get(position).getName());
        holder.duration.setText(Utils.milliSecondsToTimer(Long.parseLong(music.get(position).getDuration())));
    }

    @Override
    public int getItemCount() {
        return music.size();
    }

public void play_pause(){

    if(mediaPlayer.isPlaying()){
        playpause.setImageResource(R.drawable.play);
        mediaPlayer.pause();

    }else {
        playpause.setImageResource(R.drawable.pause);
        mediaPlayer.start();

    }
}
public void next(){
    if(id==music.size()-1){
        id=0;
       Player(id);

    }else {
        id++;
        Player(id);

    }
}
public void previous(){
    if(id==0){
        id=music.size()-1;
        Player(id);

    }else {
        id--;
        Player(id);

    }
}

private void Player(int id){

    try { mediaPlayer.reset();
        this.id=id;
        mediaPlayer.setDataSource(music.get(id).getPath());
        mediaPlayer.prepare();
        play_pause();
        mediaPlayer.start();
        finalTime = mediaPlayer.getDuration();
        finalTimeText.setText(Utils.milliSecondsToTimer((long) finalTime));
        startTime = mediaPlayer.getCurrentPosition();
        seekBarTime.setProgress((int)startTime);
        seekBarTime.setMax((int) finalTime);
        Title.setText(music.get(id).getName());
        myHandler.postDelayed(UpdateSongTime,100);

    } catch (IOException e) {
        e.printStackTrace();
    }

}

   public void nextAutoMediaPlayer() {

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {

                  next();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public  void  LoopMediaPlayer(){
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {

                    Player(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void LoopMediaPlayerIsFalse() {
        mediaPlayer.setOnCompletionListener(null);
    }
    public void nextRanDom(){


            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    try {

                        Random r = new Random();
                        int low = id;
                        int high = music.size()-1;
                        int result = r.nextInt(high-low) + low;
                        Player(result);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


    }



    public void Search(String query){
        List<Music> list = new ArrayList<>();

                for (int i = 0; i < music.size(); i++) {
                    if (music.get(i).getName().contains(query)) {
                        list.add(music.get(i));
                    }
                }


        music.clear();
        music=list;
    }


    class MyRecyclerView extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name,duration;
        CardView cardView;
        public MyRecyclerView(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name=itemView.findViewById(R.id.music_name);
            duration=itemView.findViewById(R.id.duration);
            cardView=itemView.findViewById(R.id.cardItem);
            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.cardItem){
                id=getPosition();
                Player(id);
                Title.setText(music.get(id).getName().toString());
                nextAutoMediaPlayer();
            }

        }
    }


    }

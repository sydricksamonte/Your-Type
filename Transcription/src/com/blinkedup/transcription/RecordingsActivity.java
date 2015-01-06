package com.blinkedup.transcription;


import java.util.ArrayList;
import java.util.List;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class RecordingsActivity extends Activity {
	
	SeekBar seekBar;
	Button btnVolUp, btnVolDown;
	ToggleButton tglPlayPause;
	
	MediaPlayer mediaPlayer;
	ArrayAdapter<String> adapter;
	ListView lv;

	List<Music> musicInfoList;
	Cursor cursor;
	AudioManager audioManager;
	Music musicSelected;
	Handler seekHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recordings);
		
		lv = (ListView) findViewById(R.id.listView1);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		musicInfoList = new ArrayList<Music>();
		btnVolUp = (Button) findViewById(R.id.btnVolUp);
		btnVolDown = (Button) findViewById(R.id.btnVolDown);
		adapter = new ArrayAdapter<String>(this, R.layout.dropdown_item);
		lv.setAdapter(adapter);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		LoadMusic();
		
		lv.setOnItemClickListener (new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				if (mediaPlayer != null && mediaPlayer.isPlaying())
				mediaPlayer.stop();
				
				mediaPlayer = new MediaPlayer();
				
				try{
					
					musicSelected = musicInfoList.get(position);
					mediaPlayer.setDataSource(musicInfoList.get(position).getFullPath());
					mediaPlayer.prepare();
					seekBar.setMax(mediaPlayer.getDuration());
					mediaPlayer.start();
					seekUpdate();
					tglPlayPause.setChecked(true);
				}catch(Exception e) {
					Toast.makeText(getApplicationContext(), "You selected" +
									parent.getAdapter().getItemId(position), Toast.LENGTH_LONG);
				}
	
		}
		});
		
		if (mediaPlayer !=null) seekUpdate();
		
		seekBar.setOnTouchListener(new OnTouchListener(){
			public boolean onTouch(View v, MotionEvent event) {
				seekChange(v);
				return false;
			}
		});
		
		initPlayPause();
		initUp();
		initDown();
	}
		
		private void seekChange(View v){
			if (mediaPlayer !=null && mediaPlayer.isPlaying()){
				SeekBar sb= (SeekBar) v;
				mediaPlayer.seekTo(sb.getProgress());
			}
		}
		
		private void initPlayPause(){
			tglPlayPause = (ToggleButton) findViewById(R.id.tglPlayPause);
			tglPlayPause.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (mediaPlayer !=null){
						if (isChecked)
							mediaPlayer.start();
						else
							mediaPlayer.pause();
					}
				}
				
			});
		}
		
		private void initUp(){
			btnVolUp = (Button) findViewById(R.id.btnVolUp);
			btnVolUp.setOnClickListener (new OnClickListener(){
				public void onClick(View v){
					audioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC,
													AudioManager.ADJUST_RAISE,
													AudioManager.FLAG_SHOW_UI);
				}
			});
				
			}
		
		private void initDown(){
			btnVolDown = (Button) findViewById(R.id.btnVolDown);
			btnVolDown.setOnClickListener (new OnClickListener(){
				public void onClick(View v){
					audioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC,
													AudioManager.ADJUST_LOWER,
													AudioManager.FLAG_SHOW_UI);
				}
			});
				
			}
	private void LoadMusic(){
		Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		String listOfSongs = MediaStore.Audio.Media.IS_MUSIC + " !=0 ";
		String fullPath =  "";
		
		cursor = managedQuery(songUri, new String[]{"*"}, listOfSongs, null, null);
		Music music = null;
		if (cursor !=null){
			while(cursor.moveToNext()){
				music = new Music();
				String songName = cursor.getString(cursor.getColumnIndex(
													MediaStore.Audio.Media.DISPLAY_NAME));
				int songID = cursor.getInt(cursor.getColumnIndex(
													MediaStore.Audio.Media._ID));
				fullPath = cursor.getString(cursor.getColumnIndex(
													MediaStore.Audio.Media.DATA));
				String albumName = cursor.getString(cursor.getColumnIndex(
													MediaStore.Audio.Media.ALBUM));
				
				String artistName = cursor.getString(cursor.getColumnIndex(
													MediaStore.Audio.Media.ARTIST));
				
				int duration = cursor.getInt(cursor.getColumnIndex(
													MediaStore.Audio.Media.DURATION));
				int fileSize = cursor.getInt(cursor.getColumnIndex(
													MediaStore.Audio.Media.SIZE));
				
				adapter.add(songName + " by :" + artistName);
				
				music.setMusicID(songID);
				music.setmusicName(songName);
				music.setArtistName(artistName);
				music.setAlbum(albumName);
				music.setDuration(duration);
				music.setFileSize(fileSize);
				music.setFullPath(fullPath);
				musicInfoList.add(music);
				
			}
		}
		cursor.close();
	}
	
	Runnable run = new Runnable(){

		@Override
		public void run() {

			seekUpdate();
			
			
		}
		
	};
	
	private void seekUpdate(){
		seekBar.setProgress(mediaPlayer.getCurrentPosition());
		seekHandler.postDelayed(run, 1000);
	}

	
}

package uk.co.flumeland.reversi;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener; // for button click
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class HomeScreen extends Activity {
	
	private static final int FIRST_OR_SECOND=1;
	private Button startGameButton;
	private Button settingsButton;
	private Button highScoresButton;
	private Button howToButton;
	private Button quitButton;
	private RadioGroup timingRG;
	private RadioGroup playingRG;
	Intent startGameIntent;
	SharedPreferences settings;
	//private static final int CONTACT_PICKER_RESULT = 1001;  
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		startGameButton = (Button) findViewById(R.id.start_button);
		settingsButton = (Button) findViewById(R.id.setting_button);
		highScoresButton = (Button) findViewById(R.id.high_scores_button);
		howToButton = (Button) findViewById(R.id.how_to_play_button);
		quitButton = (Button) findViewById(R.id.quit_button);
		TextView title = (TextView) findViewById(R.id.title_textview);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/tattoowoo_naughty-nights/Naughty Nights.ttf");
		title.setTypeface(tf);
		settings = PreferenceManager
			    .getDefaultSharedPreferences(HomeScreen.this);
		final SharedPreferences.Editor editor = settings.edit();
		timingRG = (RadioGroup) findViewById(R.id.timer_radio_group);
		playingRG = (RadioGroup) findViewById(R.id.players_radio_group);
		setPlayRadio();
		setTimerRadio();
		
		
	Board.timed = settings.getBoolean("pref_timed", false);
		if (Board.timed == true) {
			timingRG.check(R.id.timed_radio);
		} else {
			timingRG.check(R.id.not_timed_radio);
		}
		
	
		
		
		startGameButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				startGameIntent = new Intent(getApplicationContext(), Board.class);
				showDialog(FIRST_OR_SECOND);
				
			}
		});
		
		
		timingRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup timingRG, int checkedId) {
				//Toast timeTst = Toast.makeText(getApplicationContext(), "Timed game selected: " + Board.timed, Toast.LENGTH_SHORT);
				switch (timingRG.getCheckedRadioButtonId()) {
				case R.id.timed_radio:
					//Board.timed = true;
					editor.putBoolean("pref_timed", true);
					editor.commit();
					//timeTst.show();
					break;

				case R.id.not_timed_radio:
					//Board.timed = false;
					editor.putBoolean("pref_timed", false);
					editor.commit();
					//timeTst.show();
					break;
				}
			}
		});
		
		playingRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup playingRG, int checkedId) {
				switch (playingRG.getCheckedRadioButtonId()) {
				case R.id.one_player_radio:
					Board.numPlayers = 1;
					editor.putString("pref_player", "1");
					editor.commit();
					break;

				case R.id.two_player_radio:
					Board.numPlayers = 2;
					editor.putString("pref_player", "2");
					editor.commit();
					break;
				}
			}
		});
		
		settingsButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent settingsIntent = new Intent(getApplicationContext(), Settings.class);
				startActivity(settingsIntent);
				finish();
			}
		});
		
		highScoresButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent highScoresIntent = new Intent(getApplicationContext(), HighScores.class);
				startActivity(highScoresIntent);
				//finish();
				}
		});
		
		howToButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent howToIntent = new Intent(getApplicationContext(), HowTo.class);
				startActivity(howToIntent);
				//finish();
			}
		});
		
		quitButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	public void setPlayRadio() {
		Board.numPlayers = Byte.parseByte(settings.getString("pref_player", "1"));
		if (Board.numPlayers == 1) {
			playingRG.check(R.id.one_player_radio);
		} else {
			playingRG.check(R.id.two_player_radio);
		}
	}
	
	public void setTimerRadio() {
		Board.timed = settings.getBoolean("pref_timed", false);
		if (Board.timed == true) {
			timingRG.check(R.id.timed_radio);
		} else {
			timingRG.check(R.id.not_timed_radio);
		}
	}

	@Override
	public Dialog onCreateDialog(int id) {
		switch(id) {
		case FIRST_OR_SECOND:
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Player One: Do you want to be Black or White?");
			builder.setCancelable(true);
			builder.setPositiveButton("WHITE", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Board.playAs = 2;
					dialog.cancel();
					startActivity(startGameIntent);
					finish();
					}
			});
			
			builder.setNegativeButton("BLACK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Board.playAs = 1;
					dialog.cancel();
					startActivity(startGameIntent);
					finish();
				}
			});
			
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		return super.onCreateDialog(id);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.action_how_to:
			startActivity(new Intent(this, HowTo.class));
			return true;
		case R.id.action_high_scores:
			startActivity(new Intent(this, HighScores.class));
			return true;
		}
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setPlayRadio();
		setTimerRadio();
	   }


}

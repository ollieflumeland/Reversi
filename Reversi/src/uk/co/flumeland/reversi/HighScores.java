package uk.co.flumeland.reversi;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class HighScores extends Activity {

	private Score[] score = new Score[10];
	private ListView scoreList;
	private ContentResolver cr;
	private RevHighScoreAdapter rHSad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_scores_screen);
		TextView title = (TextView) findViewById(R.id.title_textview);
		scoreList = (ListView) findViewById(R.id.high_score_list);
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/tattoowoo_naughty-nights/Naughty Nights.ttf");
		title.setTypeface(tf);
		getScores();
		loadScores();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	/**
	 * Loads scores into the score ArrayList
	 */
	public void getScores() {
		Log.i("HighScores", "getScores() called");
		cr = getContentResolver();
		Cursor c = cr.query(HighScoresData.CONTENT_URI, null, null, null,
				HighScoresData.KEY_SCORE + " DESC");
		// looping through all rows and adding to list
		int numScores = c.getCount();
		int i = 0;
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			do {
				Log.i("HighScores", "getScores() = "
								+ c.getString(HighScoresData.NAME_COLUMN) + " " 
								+ c.getString(HighScoresData.PHOTO_COLUMN) + " "
								+ c.getInt(HighScoresData.SCORE_COLUMN) + " " + i);
				score[i] = new Score(c.getString(HighScoresData.NAME_COLUMN),
						c.getString(HighScoresData.PHOTO_COLUMN),
						c.getInt(HighScoresData.SCORE_COLUMN));
				//score[i] = sc;
				i++;
			} while (c.moveToNext() && i < 10);
		} else {
			Log.i("HighScores", "getScores() no scores found");
		}
	}
	
	private void loadScores() {
	rHSad = new RevHighScoreAdapter(this, score);
	// Set the Adapter to GridView
	scoreList.setAdapter(rHSad);
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
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}
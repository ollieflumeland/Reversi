package uk.co.flumeland.reversi;

import uk.co.flumeland.reversi.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class HowTo extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rules_screen);
		TextView title = (TextView) findViewById(R.id.title_textview);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/tattoowoo_naughty-nights/Naughty Nights.ttf");
		title.setTypeface(tf);
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
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}  
}
package uk.co.flumeland.reversi;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import android.database.Cursor;
import android.net.Uri;



public class Settings extends PreferenceActivity {

	private static final int PLAYER_ONE_CHOICE = 1;
	private static final int PLAYER_TWO_CHOICE = 2;
	public int playerOneNum;
	public int playerTwoNum;
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		addActionBar(); 

		// Set Player One Pref
		Preference chosenContactOne = (Preference)findPreference("contactChoice1");
		chosenContactOne.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent pick1 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(pick1, PLAYER_ONE_CHOICE);
				return true;
			}
		}); 

		// Set Player Two Pref  
		Preference chosenContactTwo = (Preference)findPreference("contactChoice2");
		chosenContactTwo.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent pick2 = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(pick2, PLAYER_TWO_CHOICE);
				return true;
			}

		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK) {
			if(requestCode == PLAYER_ONE_CHOICE) {
				Uri contactUri = data.getData();
				Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);

				if(cursor.moveToFirst()){
					int idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
					String id = cursor.getString(idx);
					SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("contactChoice1", id);
					editor.commit();
					int playerOneNum = Integer.parseInt(id);
					Toast play1id = Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG);
					play1id.show();
				}
			}
			if (requestCode == PLAYER_TWO_CHOICE) {
				Uri contactUri = data.getData();
				Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);

				if(cursor.moveToFirst()){
					int idx = cursor.getColumnIndex(ContactsContract.Contacts._ID);
					String id = cursor.getString(idx);
					Log.i("Setting","onActivityResulat() called " + id  + " " + idx);
					SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("contactChoice2", id);
					editor.commit();
					Toast play2id = Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG);
					play2id.show();
				}
			}
		}
	}
	
	@Override
	public void onBackPressed(){
		Intent homeIntent = new Intent(getApplicationContext(), HomeScreen.class);
		startActivity(homeIntent);
		finish();
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void addActionBar() {
		//Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
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
}

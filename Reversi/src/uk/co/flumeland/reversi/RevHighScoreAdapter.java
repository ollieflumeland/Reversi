package uk.co.flumeland.reversi;

import java.util.ArrayList;
import java.lang.String;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RevHighScoreAdapter extends ArrayAdapter {
	
    private Score[] scores;
    private Context context;
  
// Constructor
public RevHighScoreAdapter(Context context, Score[] score){
		super(context, 0);
	    scores = score;
	    this.context = context;
	   	}


	@Override
	public int getCount() {
		// length of list
		return scores.length;
	}

	@Override
	public Object getItem(int position) {
		// length of list
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		Log.i("Reversi, RevHSCustAd getView called", "position = " + position );
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(R.layout.high_scores_listview_row, parent, false);
		}
		TextView nameTV = (TextView) row.findViewById(R.id.hs_player_name_lv);
		ImageView photoIV = (ImageView) row.findViewById(R.id.hs_player_image_lv);
		TextView scoreTV = (TextView) row.findViewById(R.id.hs_score_lv);
		Log.i("Reversi, RevHSCustAd getView called", "position = " + position );
		
		//if (scores[position].getPhotolink() != null) {
			//photoIV.setImageURI(Uri.parse(scores[position].getPhotolink()));
		//} else {
			//photoIV.setImageResource(R.drawable.social_add_person);
		//}
			nameTV.setText(scores[position].getName());
			Log.i("Reversi, RevHSCustAd getView called", "position = " + position );
			scoreTV.setText(String.valueOf(scores[position].getScore()));

		return row;
	}
}

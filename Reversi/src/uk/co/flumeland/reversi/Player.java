package uk.co.flumeland.reversi;

import java.util.ArrayList;
import java.util.Arrays;
import java.lang.String;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.net.Uri;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.database.Cursor;
import android.widget.AdapterView.OnItemClickListener;

public class Player {
	public int playerNum;
	public int playingAs;
	public int score;
	public boolean idset;
	public boolean winner;
	public boolean willset;
	public int[] playableSquares;
	public String playerName;
	public String photoId;
	

	public final Cursor managedQuery (Uri uri, String[] projection, String selection,        
	                                                            String[] selectionArgs, String sortOrder){
	return null;
	}

	
	
	public Player(int player, byte playAs){
		playerNum = player;
		playerName = "Unknown " + playAs;
		photoId = "generic";
		playingAs = playAs;
		willset = false;
		winner = false;
		idset = false;
		score = 2;
		playableSquares = new int[64];	
	}
	
	public Player(byte playAs) {
		playerNum = 000;
		playerName = "Computer";
		photoId = "computer";
		playingAs = playAs;
		winner = false;
		idset = true;
		willset = false;
		score = 2;
		playableSquares = new int[64];
	}

	public String scoreToString(){
		return String.format("%02d", score);
	}
	
	public void clearArray() {
		Arrays.fill(playableSquares, 0);
	}

	public int chooseMove() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String playableToString() {
		String play = Arrays.toString(playableSquares);
		System.out.println(play);

		return play;
	}
	
	/*public void retPlayDetails(Player player) {
		Cursor cursor = managedQuery(ContactsContract.Contacts.CONTENT_URI,  null,null,null,null);
		int idIdx = cursor.getColumnIndexOrThrow(
                ContactsContract.Contacts._ID);
		int nameIdx= cursor.getColumnIndexOrThrow (  
                 ContactsContract.Contacts.DISPLAY_NAME);
		int photoIdx = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)
		
       contactList += id + ". " + name + "\”;	
   } while (cursor.moveToNext());
}
contactText.setText(contactList);
		null);
		int nameIdx= nameCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);

		if (nameCursor.moveToFirst()){
		playerName = nameCursor.getString(nameIdx);
		}

		if (player == 1){
		playerOneUriString = pic.toString();
		playerOneName = playerName;
		}
		else {
		playerTwoUriString = pic.toString();
		playerTwoName = playerName;
		}
		}
		}


	}
	
*/
	
}
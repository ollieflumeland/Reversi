package uk.co.flumeland.reversi;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.ContentUris;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class HighScoresData extends ContentProvider {
	
	public static final String AUTHORITY = "uk.co.flumeland.reversi.scores";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/scores");
    
    private SQLiteDatabase highScoresDB;
    private static final String DATABASE_NAME = "highscores.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String SCORES_TABLE = "scores";
	public static final String KEY_ROWID = "row_id";
	public static final String KEY_PLAYID = "player_id";
	public static final String KEY_NAME = "player_name";
	public static final String KEY_PHOTO = "player_photo";
	public static final String KEY_SCORE = "player_score";
	public static final int ID_COLUMN = 0;
	public static final int PLAYERID_COLUMN = 1;
    public static final int NAME_COLUMN = 2;
    public static final int PHOTO_COLUMN = 3;
	public static final int SCORE_COLUMN = 4;
	
	private static final int MULTI = 1;
	private static final int SINGLE = 2;
	private static final UriMatcher uriMatcher;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "scores", MULTI);
		uriMatcher.addURI(AUTHORITY, "scores/#", SINGLE);
	}
		
		@Override
		public int delete(Uri uri, String selection, String[] selectionArgs) {
			// TODO Auto-generated method stub
			highScoresDB.delete(SCORES_TABLE, selection, selectionArgs);
			return 0;
		}

		@Override
		public String getType(Uri uri) {
			switch (uriMatcher.match(uri)) {
			case MULTI:
				return "vnd.android.cursor.item/vnd.flumeland.scores";			
			case SINGLE:
				return "vnd.android.cursor.dir/vnd.flumeland.scores";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
			}
		}

		@Override
		public Uri insert(Uri uri, ContentValues values) {
			long rowID = highScoresDB.insert(SCORES_TABLE, null, values);
			if (rowID > 0) {
				Uri newuri = ContentUris.withAppendedId(CONTENT_URI, rowID);
				getContext().getContentResolver().notifyChange(newuri, null);
				return uri;
			} else {
				throw new SQLException("Failed to insert row into " + uri);
			}
		}

		@Override
		public boolean onCreate() {
			DBHelper helper = new DBHelper(
					this.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
			this.highScoresDB = helper.getWritableDatabase();
			return (highScoresDB != null);
		}

		@Override
		public Cursor query(Uri uri, String[] projection, String selection,
				String[] selectionArgs, String sortOrder) {
			
			SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
			qb.setTables(SCORES_TABLE);
			
			if (uriMatcher.match(uri) == SINGLE) {
				qb.appendWhere(KEY_ROWID + "=" + uri.getPathSegments().get(1)); 
			}
			
			Cursor c = qb.query(highScoresDB, projection, selection, selectionArgs, null, null, sortOrder);
			// register to watch for changes which are
			// signalled by notifyChange elsewhere
			c.setNotificationUri(getContext().getContentResolver(), uri);
			return c;
		}

		@Override
		public int update(Uri uri, ContentValues values, String selection,
				String[] selectionArgs) {
			// TODO Auto-generated method stub
			return 0;
		}
	
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + SCORES_TABLE + " (" + 
					KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
					KEY_PLAYID + " INTEGER NOT NULL, "  +
					KEY_NAME + " TEXT NOT NULL, " +
					KEY_PHOTO + " TEXT NOT NULL, " +
					KEY_SCORE + " INTEGER NOT NULL);"
			);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
			onCreate(db);
		}
	}
		
	/*public HighScoresData(Context c) {
		ourContext = c;
	}
	
	public HighScoresData open() throws SQLException {
		ourHelper = new DBHelper (ourContext);
		highScoreDB = ourHelper.getWritableDatabase();
		return this;	
	}
	
	public void close() {
		ourHelper.close();
	}

	public long createEntry(int playerNum, String playerName, String photo,
			int score) {
		ContentValues ConVals = new ContentValues();
		ConVals.put(KEY_PLAYID, playerNum);
		ConVals.put(KEY_NAME, playerName);
		ConVals.put(KEY_PHOTO, photo);
		ConVals.put(KEY_SCORE, score);
		return highScoreDB.insert(SCORES_TABLE, null, ConVals);
	}

	public String getData() {
		String[] columns = new String[] {KEY_ROWID, KEY_PLAYID, KEY_NAME, KEY_PHOTO, KEY_SCORE};
		Cursor c = highScoreDB.query(SCORES_TABLE, columns, null, null, null, null, null);
		String result = "";
		int iRowHS = c.getColumnIndex(KEY_ROWID);
		int iIDHS = c.getColumnIndex(KEY_PLAYID);
		int iNameHS = c.getColumnIndex(KEY_NAME);
		int iPhotoHS = c.getColumnIndex(KEY_PHOTO);
		int iScoreHS = c.getColumnIndex(KEY_SCORE);
		
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result = result + c.getString(iRowHS) + " " + c.getString(iIDHS) + " " 
					+ c.getString(iNameHS) + " " + c.getString(iPhotoHS) + " " + c.getString(iScoreHS) + "\n";
		}
	
		return result;
	}
	
	public String getHighScores() {
		String[] highScores = new String[] {KEY_NAME, KEY_SCORE};
		Cursor c = highScoreDB.query(SCORES_TABLE, highScores, null, null, null, null, KEY_SCORE);
		String result = "";
		int iNameHS = c.getColumnIndex(KEY_NAME);
		int iScoreHS = c.getColumnIndex(KEY_SCORE);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result = result + c.getString(iNameHS) + " " + c.getString(iScoreHS) + "\n";
		}
		return result;
	}
	
	public String getPhotosId() {
		String[] highScores = new String[] {KEY_PHOTO};
		Cursor c = highScoreDB.query(SCORES_TABLE, highScores, null, null, null, null, KEY_SCORE);
		String result = "";
		int iNameHS = c.getColumnIndex(KEY_NAME);
		int iScoreHS = c.getColumnIndex(KEY_PHOTO);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result = result + c.getString(iNameHS) + " " + c.getString(iScoreHS) + "\n";
		}
		return result;
	}
	*/
}

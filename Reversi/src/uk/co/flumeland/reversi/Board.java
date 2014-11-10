package uk.co.flumeland.reversi;

import java.util.Arrays;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContentValues;
import android.database.Cursor;

public class Board extends Activity {

	// Counter/Player/Game Constants
	// navigation
	private static final int PERSONAL_PLAYER_ONE = 1;
	private static final int GO_TO_SETTINGS = 2;
	private static final int GO_TO_HIGH = 3;
	private static final int GO_TO_RULES = 4;
	private static final int GO_BACK = 5;
	private static final int G0_TO_HOME = 6;
	// Amount to delay computer move delay
	private static final int COMP_DELAY = 2000;

	public static final byte BLANK = 0;
	public static final byte BLACK = 1;
	public static final byte WHITE = 2;
	public static final byte BLNK_PLAY = 3;
	public static final byte BLNK_PLAY2 = 4;
	private static final int[] dirRow = new int[] { -1, -1, -1, 0, 0, 1, 1, 1 };
	private static final int[] dirCol = new int[] { -1, 0, 1, -1, 1, -1, 0, 1 };

	public static boolean showPoss; // should potential moves be displayed?
	private static boolean movePoss = true; // is a move possible?
	private static boolean gameOver = false; // has game ended?
	private boolean locked = false; // is computer taking its turn?
	private static byte cantGo = 0; // Counts move not possible
	public static byte[] board = new byte[64]; // game board 1D
	public static byte playAs = BLACK; // is player one playing as black or
										// white?
	public static byte numPlayers;
	public static boolean timed;
	public static int tiles;
	public static boolean sounds = true;
	public static byte winner;
	public ContentResolver cr;

	private int row, column;
	private int testPos;
	private byte playerNo = BLACK;
	private byte nonPlayer = WHITE;
	private static final int dirLen = dirRow.length; // length of vector arrays
	public final int brdLen = board.length; // length of 1D board array
	private RevBrdImageAdapter revBrdAdapter;
	private GridView revBrdGrid; // Gridview to hold the board
	TextView playerBlkTxt;
	TextView playerWhtTxt;
	TextView title;
	ImageView blackIV;
	ImageView whiteIV;
	ImageView player1IV;
	ImageView player2IV;
	Button resetBut;
	int playerOneId = 1;
	int playerTwoId = 0;
	int move;
	public Player playerOne = new Player(playerOneId, playAs); // Player object
	public Player playerTwo;
	Player playerBlack;
	Player playerWhite;
	// Timer Variables
	private CountDownTimer countDownTimer;
	private CountDownTimer countDownTimer2;
	public TextView timerTView;
	private int timeDur = 2; // Length of timer
	private final long int1s = 1 * 1000; // timer interval 1sec
	private final long int30s = 30 * 1000; // timer interval 30secs
	android.media.ToneGenerator beep = new ToneGenerator(
			AudioManager.STREAM_ALARM, 20);
	SharedPreferences settings;
	Intent settingsIntent;

	// private static final String PREF_GAME_SAVE = "saveGame";
	// protected static final int CONTINUE = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reversi_board);
		addActionBar();
		revBrdGrid = (GridView) findViewById(R.id.board_gridview);
		revBrdAdapter = new RevBrdImageAdapter(this, board);
		revBrdGrid.setAdapter(revBrdAdapter);
		playerBlkTxt = (TextView) findViewById(R.id.black_score);
		playerWhtTxt = (TextView) findViewById(R.id.white_score);
		player1IV = (ImageView) findViewById(R.id.player1_imageview);
		player2IV = (ImageView) findViewById(R.id.player2_imageview);
		timerTView = (TextView) this.findViewById(R.id.time_textview);
		title = (TextView) findViewById(R.id.title_textview);
		blackIV = (ImageView) findViewById(R.id.blk_score_icon_imageview);
		whiteIV = (ImageView) findViewById(R.id.wht_score_icon_imageview);
		resetBut = (Button) findViewById(R.id.reset_button);
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/tattoowoo_naughty-nights/Naughty Nights.ttf");
		title.setTypeface(tf);
		settings = PreferenceManager.getDefaultSharedPreferences(Board.this);
		setPlayerTwo();
		isSetPersPlayerOne();
		setPersPlayerOne();
		useTimer();
		chooseTiles();
		gameSetup();

		resetBut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelTimers();
				showDialog(G0_TO_HOME);
				// settingsIntent = new Intent(getApplicationContext(),
				// HomeScreen.class);
				// startActivity(settingsIntent);
				// finish();
			}
		});

		// on move detected
		OnItemClickListener revBrdListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (movePoss == true && gameOver == false && locked == false) {
					// if able to go and game in play and computer not playing
					if (validMove(position)) {
						takeMove(position);
						if (movePoss != true && gameOver == false) { // if
																		// unable
																		// to go
							cantGo++;
							while (movePoss != true && cantGo < 2) {
								Toast chgPlayer = Toast.makeText(
										getApplicationContext(),
										"No move available for player "
												+ playerNo, Toast.LENGTH_SHORT);
								chgPlayer.show(); // show toast
								changePlayer(); // revert to opponent
								showPossibleMoves();
								findPossible();
								ifGameOver();
								revBrdAdapter.notifyDataSetChanged();
								if (movePoss != true && gameOver == false) { // if
																				// unable
																				// to
																				// go
									cantGo++;
								}
							}
							if (cantGo == 2) { // if neither player can play
								gameOver = true;
								Toast noMoves = Toast
										.makeText(
												getApplicationContext(),
												"No move available for either player - GAME OVER",
												Toast.LENGTH_SHORT);
								noMoves.show();
								ifGameOver();
							}
						}
					}
					// Computer Moves:
					if (numPlayers == 1 && playAs != playerNo) {
						// if single player game and computers turn
						locked = true;
						final Handler mHandler = new Handler();
						mHandler.postDelayed(rMakeCompMove, COMP_DELAY);
						/*
						 * revBrdAdapter.notifyDataSetChanged(); try {
						 * Thread.sleep(6000); } catch(InterruptedException ex)
						 * {}
						 */
						// Find Position to Play
						String playPrnt = playerTwo.playableToString();
						// Toast playa = Toast.makeText(getApplicationContext(),
						// playPrnt, Toast.LENGTH_SHORT);
						// playa.show();

					}
				}
			}
		};

		revBrdGrid.setOnItemClickListener(revBrdListener);
	}

	/**
	 * Used to delay the computer moves without delaying the main UI thread
	 */
	private final Runnable rMakeCompMove = new Runnable() {

		@Override
		public void run() {
			move = playerTwo.chooseMove();
			Toast moveCh = Toast.makeText(getApplicationContext(),
					"Computer Chooses: Row " + ((move / 8) + 1) + ", Column "
							+ ((move % 8) + 1), Toast.LENGTH_SHORT);
			moveCh.show();
			// make the move:
			if (validMove(move)) {
				takeMove(move);
			}
			if (movePoss != true && gameOver == false) { // if unable to go
				cantGo++;
				while (movePoss != true && cantGo < 2) {
					Toast chgPlayer = Toast.makeText(getApplicationContext(),
							"No move available for player " + playerNo,
							Toast.LENGTH_SHORT);
					chgPlayer.show(); // show toast
					changePlayer(); // revert to opponent
					showPossibleMoves();
					findPossible();
					ifGameOver();
					revBrdAdapter.notifyDataSetChanged();
					if (movePoss != true && gameOver == false) { // if unable to
																	// go
						cantGo++;
					}
				}
				if (cantGo == 2) { // if neither player can play
					gameOver = true;
					Toast noMoves = Toast.makeText(getApplicationContext(),
							"No move available for either player - GAME OVER",
							Toast.LENGTH_SHORT);
					noMoves.show();
					ifGameOver();
				}
			}
		locked = false;
		}
	};

	/**
	 * 
	 */
	public void takeMove(int position) {
		board[position] = playerNo;
		cantGo = 0;
		changePlayer(); // swap player to go
		setTiles();
		calcShowScores(); // recalc scores
		showPossibleMoves(); // check if possible moves should be displayed
		findPossible(); // find legal moves
		ifGameOver();
		revBrdAdapter.notifyDataSetChanged(); // notify changes
	}

	/**
	 * Method that adds the action bar if the sdk is high enough
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void addActionBar() {
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	/**
	 * Method to prepare the game board with the opening counters and possible
	 * moves
	 */
	private void gameSetup() {
		// if (CONTINUE == 1) {

		// }
		gameOver = false;
		Arrays.fill(board, (byte) 0);
		setLinkPlayer();
		board[27] = WHITE;
		board[36] = WHITE;
		board[28] = BLACK;
		board[35] = BLACK;
		setTiles();
		ifSetTimer();
		if (playAs == 2 && numPlayers == 1) {
			board[19] = BLACK;
			board[27] = BLACK;
			changePlayer();
			// setTiles();
			calcShowScores();
		}
		playerBlkTxt.setText(playerBlack.scoreToString());
		playerWhtTxt.setText(playerWhite.scoreToString());
		showPossibleMoves();
		findPossible();
	}

	/**
	 * Method that swaps the current player and non-player. if the game is timed
	 * the timer is also restarted
	 */
	private void changePlayer() {
		if (playerNo == BLACK) {
			playerNo = WHITE;
			nonPlayer = BLACK;
		} else {
			playerNo = BLACK;
			nonPlayer = WHITE;
		}
		if (timed == true) {
			resetTimer();
		}
	}

	private void setTiles() {
		if (tiles == 0 && playerNo == BLACK) {
			blackIV.setImageResource(R.drawable.blk_rnd_cnt_bk_glw);
			whiteIV.setImageResource(R.drawable.wht_rnd_cnt_bk);
		} else if (tiles == 1 && playerNo == BLACK) {
			blackIV.setImageResource(R.drawable.prp_oct_cnt_bk_glw);
			whiteIV.setImageResource(R.drawable.bnz_oct_cnt_bk);
		} else if (tiles == 0 && playerNo == WHITE) {
			blackIV.setImageResource(R.drawable.blk_rnd_cnt_bk);
			whiteIV.setImageResource(R.drawable.wht_rnd_cnt_bk_glw);
		} else {
			blackIV.setImageResource(R.drawable.prp_oct_cnt_bk);
			whiteIV.setImageResource(R.drawable.bnz_oct_cnt_bk_glw);
		}
	}

	/**
	 * A method to set the second player as computer or person
	 */
	private void setPlayerTwo() {
		byte playAs2;
		if (playAs == BLACK) {
			playAs2 = WHITE;
		} else {
			playAs2 = BLACK;
		}
		if (numPlayers == 1) {
			playerTwo = new Computer(playAs2);
			// Toast computer = Toast.makeText(getApplicationContext(),
			// "Creating computer", Toast.LENGTH_SHORT);
			// computer.show();
		} else {
			playerTwo = new Player(playerTwoId, playAs2);
		}
	}

	/**
	 * A method that sets up the timer
	 */
	private void ifSetTimer() {
		View show = findViewById(R.id.time_textview);
		if (timed == true) {
			show.setVisibility(View.VISIBLE);
			timerTView.setText(timerTView.getText()
					+ String.valueOf(timeDur * 60));
			countDownTimer = new MoveTimer(timeDur * 60 * 1000 + 100, int1s);
			countDownTimer.start();
			if (sounds == true) {
				countDownTimer2 = new MoveTimer2(timeDur * 60 * 1000 + 100,
						int30s);
				countDownTimer2.start();
			}
		} else {
			show.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * A method to cancel the timer
	 */
	public void cancelTimers() {
		if (timed == true) {
			View show = findViewById(R.id.time_textview);
			show.setVisibility(View.INVISIBLE);
			countDownTimer.cancel();
			if (sounds == true) {
				countDownTimer2.cancel();
			}
		}
	}

	/**
	 * A Method that resets the timer
	 */
	private void resetTimer() {
		countDownTimer.cancel();
		countDownTimer.start();
		if (sounds == true) {
			countDownTimer2.cancel();
			countDownTimer2.start();
		}
	}

	/**
	 * A method to set playerBLACK/playerWHITE
	 */
	private void setLinkPlayer() {
		if (playerOne.playingAs == BLACK) {
			playerBlack = playerOne;
			playerWhite = playerTwo;
		} else {
			playerBlack = playerTwo;
			playerWhite = playerOne;
		}
	}

	/**
	 * A Method to recalculate the scores after a turn
	 */
	private void calcShowScores() {
		byte blkScore = 0;
		byte whtScore = 0;
		for (int i = 0; i < brdLen; i++) {
			if (board[i] == BLACK) {
				blkScore++;
			} else if (board[i] == WHITE) {
				whtScore++;
			}
		}
		playerBlack.score = blkScore;
		playerWhite.score = whtScore;
		playerBlkTxt.setText("Score:  " + playerBlack.scoreToString());
		playerWhtTxt.setText("Score:  " + playerWhite.scoreToString());
	}

	// public void setPlayers() {
	// if
	// }

	/**
	 * A Method that checks if all positions have been played
	 * 
	 * @return
	 */
	private boolean allCellsTaken() {
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 0 || board[i] == 3 || board[i] == 4) {
				return false;
			}
		}
		return true;
	}

	/**
	 * A Method to test if possible moves should be displayed
	 */
	public void showPossibleMoves() {
		showPoss = settings.getBoolean("showPossi", false);
	}

	/**
	 * A Method to test if the timer should be on
	 */
	public void useTimer() {
		sounds = settings.getBoolean("sound_timed", true);
		timed = settings.getBoolean("pref_timed", false);
		timeDur = Integer
				.parseInt(settings.getString("pref_time_allowed", "2"));
	}

	public void chooseTiles() {
		tiles = Integer.parseInt(settings.getString("pref_tile", "0"));
	}

	public void isSetPersPlayerOne() {
		if (playerOne.idset == false) {
			// showDialog(PERSONAL_PLAYER_ONE);
		}
	}

	public void setPersPlayerOne() {
		int temp = Integer.parseInt(settings.getString("contactChoice1", "1"));
		if (temp != 1) {
			playerOne.playerNum = temp;
			// playerOne.playerName =
		}
	}

	/**
	 * Method to find the possible moves available
	 */
	private void findPossible() {
		movePoss = false;
		playerOne.clearArray();
		playerTwo.clearArray();
		for (int i = 0; i < brdLen; i++) {
			if (isEmptySquare(board[i])) {
				board[i] = BLANK;
				if (isPlayableAdjEnc(i)) {
					if (showPoss == true) {
						board[i] = BLNK_PLAY;
					} else {
						board[i] = BLNK_PLAY2;
					}
					movePoss = true;
					// Toast toast2 = Toast.makeText(getApplicationContext(),
					// "board [" + i + "]" + board[i], Toast.LENGTH_SHORT);
					// toast2.show();
				}
			}
		}

	}

	/**
	 * Method to convert board gridview position into row/column values
	 * 
	 * @param position
	 *            Holds the grid number selected by the player
	 */
	private void convertPosition(int position) {
		row = position / 8;
		column = position % 8;
	}

	/**
	 * Method that converts row + column coords into single coord
	 * 
	 * @param row
	 *            row of the chosen square
	 * @param column
	 *            column of the chosen square
	 * @return int value of grid square
	 */
	private int convertRowCol(int row, int column) {
		return row * 8 + column;
	}

	/**
	 * Method to check if a valid move has been selected. Calls the
	 * isEmptySquare method then calls the isPlayable method
	 * 
	 * @param position
	 *            Holds the grid number selected by the player
	 * @return boolean valid true if legal move, false if illegal
	 */
	private boolean validMove(int position) {
		boolean valid = false;
		if (isEmptySquare(board[position])) {
			if (ifPlayableThenFlip(position)) { // if playable take turn
				valid = true;
			}
		}
		return valid;
	}

	/**
	 * Method to check if an empty square has been selected.
	 * 
	 * @param position
	 *            Holds the grid number selected by the player
	 * @return boolean valid true if legal move, false if illegal
	 */
	private boolean isEmptySquare(int value) {
		boolean empty = false;
		if (value == BLANK || value == BLNK_PLAY || value == BLNK_PLAY2) { // is
																			// it
																			// free?
			empty = true;
		}
		return empty;
	}

	/**
	 * Method to test if square is adjacent to an opponents counter
	 * 
	 * @return
	 */
	private boolean isPlayableAdjEnc(int position) {
		testPos = position;
		boolean adjEnc = false;
		for (int i = 0; i < dirLen; i++) {
			convertPosition(testPos);
			row += dirRow[i];
			column += dirCol[i];
			// Toast toastRC = Toast.makeText(getApplicationContext(), "Row: " +
			// row + ",  Column: " + column + ",  i: " + i, Toast.LENGTH_SHORT);
			// toastRC.show();
			if (row >= 0 && column >= 0 && row < 8 && column < 8) { // if still
																	// on board
				if (board[convertRowCol(row, column)] == nonPlayer) {
					if (encapsNoFlip(i)) {
						adjEnc = true;
					}
				}
			}
		}
		return adjEnc;
	}

	/**
	 * Method that tests for encapsulation of opponents counters and performs
	 * the flip of the pieces
	 * 
	 * @param i
	 *            Index of vector direction of testing
	 * @return returns encaps true if encaps & flip takes places else false
	 */
	private boolean encapsNoFlip(int direction) {
		boolean encap = false;
		int possFlipsCounter = 0;
		// Toast toast3 = Toast.makeText(getApplicationContext(),
		// "T3: 1st test pos: " + testPos + ",  Value: " + board[testPos] +
		// ",  i: " + i, Toast.LENGTH_SHORT);
		// toast3.show();
		row += dirRow[direction]; // next square in direction
		column += dirCol[direction];
		// Toast toast4 = Toast.makeText(getApplicationContext(),
		// "T4: 2nd test row: " + encRow + ",  2nd test col: " + encCol +
		// ",  i: " + i, Toast.LENGTH_SHORT);
		// toast4.show();
		while (row >= 0 && column >= 0 && row < 8 && column < 8) { // if still
																	// on board
			if (board[convertRowCol(row, column)] == BLANK
					|| board[convertRowCol(row, column)] == BLNK_PLAY
					|| board[convertRowCol(row, column)] == BLNK_PLAY2) {
				break;
			} else if (board[convertRowCol(row, column)] == nonPlayer) {
				row += dirRow[direction]; // next square in direction
				column += dirCol[direction];
				if (row < 0 || column < 0 || row >= 8 || column >= 8) { // if
																		// off
																		// board
					break;
				}
				// Toast toast5 = Toast.makeText(getApplicationContext(),
				// "T5: nxt test row: " + encRow + ",  nxt test col: " + encCol
				// + ",  i: " + i, Toast.LENGTH_SHORT);
				// toast5.show();
			} else if (board[convertRowCol(row, column)] == playerNo) {
				encap = true;
				// Toast toast6 = Toast.makeText(getApplicationContext(),
				// "T6: Encap found: Enc " + encRow + " = " + testRow + "? ",
				// Toast.LENGTH_SHORT);
				// toast6.show();
				while (convertRowCol(row, column) != testPos) {
					row -= dirRow[direction]; // move back one square
					column -= dirCol[direction];
					// Toast toast7 = Toast.makeText(getApplicationContext(),
					// "T7: flip row: " + encRow + ",  flip col: " + encCol +
					// ",  flip board: " + (encRow*8 + (encCol)),
					// Toast.LENGTH_SHORT);
					// toast7.show();
					possFlipsCounter++;
				}
				if (playerNo == BLACK) {
					playerBlack.playableSquares[testPos] = possFlipsCounter;
				} else {
					playerWhite.playableSquares[testPos] = possFlipsCounter;
				}

				break;
			}
		}
		return encap;
	}

	/**
	 * Method tests if the square chosen is adjacent to an opponents counter
	 * 
	 * @param row
	 *            the row of the chosen square
	 * @param column
	 *            the column of the chosen square
	 * @return returns true if adjacent to, false if not
	 */
	private boolean ifPlayableThenFlip(int position) {
		testPos = position;
		boolean adjEnc = false;
		for (int i = 0; i < dirLen; i++) {
			convertPosition(testPos);
			row += dirRow[i];
			column += dirCol[i];
			if (row >= 0 && column >= 0 && row < 8 && column < 8) { // if still
																	// on board
				// if an oppenent's counter is adjacent
				if (board[convertRowCol(row, column)] == nonPlayer) {
					if (encapsWithFlip(i)) {
						adjEnc = true;
					}
				}
			}

		}
		return adjEnc;
	}

	/**
	 * Method that test for encapsulation of opponents counters and performs the
	 * flip of the pieces
	 * 
	 * @param i
	 *            Index of direction of testing
	 * @return returns encaps true if encaps & flip takes places else false
	 */
	private boolean encapsWithFlip(int i) {
		boolean encap = false;
		// Toast toast3 = Toast.makeText(getApplicationContext(),
		// "T3: 1st test row: " + testRow + ",  1st test col: " + testCol +
		// ",  i: " + i, Toast.LENGTH_SHORT);
		// toast3.show();
		row += dirRow[i]; // next square in direction
		column += dirCol[i];
		// Toast toast4 = Toast.makeText(getApplicationContext(),
		// "T4: 2nd test row: " + encRow + ",  2nd test col: " + encCol +
		// ",  i: " + i, Toast.LENGTH_SHORT);
		// toast4.show();
		while ((row >= 0 && row < 8) && (column >= 0 && column < 8)) { // if on
																		// board
			if (board[convertRowCol(row, column)] == BLANK
					|| board[convertRowCol(row, column)] == BLNK_PLAY
					|| board[convertRowCol(row, column)] == BLNK_PLAY2) {
				break;
			} else if (board[convertRowCol(row, column)] == nonPlayer) {
				row += dirRow[i]; // next square in direction
				column += dirCol[i];
				if (row < 0 || column < 0 || row >= 8 || column >= 8) { // if
																		// off
																		// board
					break;
				}
				// Toast toast5 = Toast.makeText(getApplicationContext(),
				// "T5: nxt test row: " + encRow + ",  nxt test col: " + encCol
				// + ",  i: " + i, Toast.LENGTH_SHORT);
				// toast5.show();
			} else if (board[convertRowCol(row, column)] == playerNo) {
				encap = true;
				// Toast toast6 = Toast.makeText(getApplicationContext(),
				// "T6: Encap found: Enc " + encRow + " = " + testRow + "? ",
				// Toast.LENGTH_SHORT);
				// toast6.show();
				while (convertRowCol(row, column) != testPos) {
					row -= dirRow[i]; // move back one square
					column -= dirCol[i];
					// Toast toast7 = Toast.makeText(getApplicationContext(),
					// "T7: flip row: " + encRow + ",  flip col: " + encCol +
					// ",  flip board: " + (encRow*8 + (encCol)),
					// Toast.LENGTH_SHORT);
					// toast7.show();
					board[convertRowCol(row, column)] = playerNo;
				}
				break;
			}
		}
		return encap;
	}

	public boolean addToDB(Player player) {
		boolean success = true;
		try {
			ContentValues newRow = new ContentValues();
			cr = getContentResolver();
			Log.i("Board", "addToDB() called " + player.playerNum + " "
					+ player.playerName + " " + player.photoId + " "
					+ player.score);
			newRow.put(HighScoresData.KEY_PLAYID, player.playerNum);
			newRow.put(HighScoresData.KEY_NAME, player.playerName);
			newRow.put(HighScoresData.KEY_PHOTO, player.photoId);
			newRow.put(HighScoresData.KEY_SCORE, player.score);
			cr.insert(HighScoresData.CONTENT_URI, newRow);
		} catch (Exception e) {
			success = false;
		}

		Cursor c = cr.query(HighScoresData.CONTENT_URI, null, null, null, null);
		// looping through all rows and adding to list
		int numScores = c.getCount();
		Log.i("Board", "addToDB() called " + numScores);
		return success;

	}

	public void ifGameOver() {
		Log.i("Board", "ifGameOver() called");
		if (gameOver == true) {
			if (playerBlack.score > playerWhite.score) {
				playerBlack.winner = true;
				winner = BLACK;
				Log.i("Board", "addToDB() called " + playerBlack.playerNum
						+ " " + playerBlack.playerName + " "
						+ playerBlack.photoId + " " + playerBlack.score);
				Toast winnerBlk = Toast.makeText(getApplicationContext(),
						"Congratulations Black, " + playerBlack.playerNum
								+ "  wins ", Toast.LENGTH_SHORT);
				winnerBlk.show();
				timerTView.setText("BLACK WINS " + playerBlack.playerNum);
				if (addToDB(playerBlack)) {
					Toast success = Toast.makeText(getApplicationContext(),
							"Added to db", Toast.LENGTH_SHORT);
					success.show();
				} else {
					Toast nosuccess = Toast.makeText(getApplicationContext(),
							"Didn't Work", Toast.LENGTH_SHORT);
					nosuccess.show();
				}

			} else {
				playerWhite.winner = true;
				winner = WHITE;
				Log.i("Board", "addToDB() called " + playerWhite.playerNum
						+ " " + playerWhite.playerName + " "
						+ playerWhite.photoId + " " + playerWhite.score);
				Toast winnerWht = Toast.makeText(getApplicationContext(),
						"Congratulations White, " + playerWhite.playerNum
								+ "  wins ", Toast.LENGTH_SHORT);
				winnerWht.show();
				timerTView.setText("WHITE WINS " + playerWhite.playerNum);
				if (addToDB(playerWhite)) {
					;
					Toast success = Toast.makeText(getApplicationContext(),
							"Added to db", Toast.LENGTH_SHORT);
					success.show();
				} else {
					Toast nosuccess = Toast.makeText(getApplicationContext(),
							"Didn't Work", Toast.LENGTH_SHORT);
					nosuccess.show();
				}
			}
			// new game
			resetBut.setText("Home");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	public class MoveTimer extends CountDownTimer {
		public MoveTimer(long timeDur, long int1s) {
			super(timeDur, int1s);
		}

		@Override
		public void onFinish() {
			timerTView.setText("OUT OF TIME! GAME OVER");
			gameOver = true;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			timerTView.setText("Time: " + millisUntilFinished / 1000);
			// if (sounds == true) {
			// if (millisUntilFinished %30 == 0) {
			// beep.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);
			// }
			// }
		}
	}

	public class MoveTimer2 extends CountDownTimer {
		public MoveTimer2(long timeDur, long int30s) {
			super(timeDur, int30s);
		}

		@Override
		public void onFinish() {
			gameOver = true;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// if (sounds == true) {
			beep.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);
			// }
		}
	}

	@Override
	public Dialog onCreateDialog(int id) {
		switch (id) {
		case PERSONAL_PLAYER_ONE:
			Builder persBuilder = new AlertDialog.Builder(this);
			persBuilder
					.setMessage("Do you want to set personal player profile using your contacts? \n ('YES' takes you to settings)");
			persBuilder.setCancelable(true);
			persBuilder.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							playerOne.idset = true;
							playerTwo.idset = true;
							dialog.cancel();
							Intent settingsIntent = new Intent(
									getApplicationContext(), Settings.class);
							startActivity(settingsIntent);
							finish();

						}
					});

			persBuilder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Board.playAs = 1;
							dialog.cancel();
						}
					});
			AlertDialog dialog = persBuilder.create();
			dialog.show();
			return super.onCreateDialog(id);

		case GO_TO_SETTINGS:
			Builder setBuilder = new AlertDialog.Builder(this);
			setBuilder
					.setMessage("WARNING: Game will be abandoned. Yes / No ?");
			setBuilder.setCancelable(true);
			setBuilder.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent settingsIntent = new Intent(
									getApplicationContext(), Settings.class);
							startActivity(settingsIntent);
							cancelTimers();
							finish();
						}
					});

			setBuilder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Board.playAs = 1;
							dialog.cancel();
						}
					});

			AlertDialog setDialog = setBuilder.create();
			setDialog.show();
			return super.onCreateDialog(id);

		case GO_TO_HIGH:
			Builder highBuilder = new AlertDialog.Builder(this);
			highBuilder
					.setMessage("WARNING: Game will be abandoned. Yes / No ?");
			highBuilder.setCancelable(true);
			highBuilder.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent settingsIntent = new Intent(
									getApplicationContext(), HighScores.class);
							startActivity(settingsIntent);
							cancelTimers();
							finish();
						}
					});

			highBuilder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Board.playAs = 1;
							dialog.cancel();
						}
					});

			AlertDialog highDialog = highBuilder.create();
			highDialog.show();
			return super.onCreateDialog(id);

		case GO_TO_RULES:
			Builder ruleBuilder = new AlertDialog.Builder(this);
			ruleBuilder
					.setMessage("WARNING: Game will be abandoned. Yes / No ?");
			ruleBuilder.setCancelable(true);
			ruleBuilder.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent settingsIntent = new Intent(
									getApplicationContext(), HowTo.class);
							startActivity(settingsIntent);
							cancelTimers();
							finish();
						}
					});

			ruleBuilder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Board.playAs = 1;
							dialog.cancel();
						}
					});

			AlertDialog ruleDialog = ruleBuilder.create();
			ruleDialog.show();
			return super.onCreateDialog(id);

		case GO_BACK:
			Builder backBuilder = new AlertDialog.Builder(this);
			if (gameOver == true) {
				backBuilder
						.setMessage("WARNING: Game will be abandoned. Yes / No ?");
			} else {
				backBuilder.setMessage("Go back to home page. Yes / No ?");
			}
			backBuilder.setCancelable(true);
			backBuilder.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent homeIntent = new Intent(
									getApplicationContext(), HomeScreen.class);
							startActivity(homeIntent);
							cancelTimers();
							finish();
						}
					});

			backBuilder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Board.playAs = 1;
							dialog.cancel();
						}
					});

			AlertDialog backDialog = backBuilder.create();
			backDialog.show();
			return super.onCreateDialog(id);

		case G0_TO_HOME:
			Builder homeBuilder = new AlertDialog.Builder(this);
			if (gameOver == true) {
				homeBuilder.setMessage("Go back to home page. Yes / No ?");
			} else {
				homeBuilder
						.setMessage("WARNING: Game will be abandoned. Yes / No ?");
			}
			homeBuilder.setCancelable(true);
			homeBuilder.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							Intent settingsIntent = new Intent(
									getApplicationContext(), HomeScreen.class);
							startActivity(settingsIntent);
							cancelTimers();
							finish();
						}
					});

			homeBuilder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Board.playAs = 1;
							dialog.cancel();
						}
					});

			AlertDialog homeDialog = homeBuilder.create();
			homeDialog.show();
			return super.onCreateDialog(id);
		}
		return null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			showDialog(GO_TO_SETTINGS);
			return true;
		case R.id.action_how_to:
			showDialog(GO_TO_RULES);
			return true;
		case R.id.action_high_scores:
			showDialog(GO_TO_HIGH);
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		showDialog(G0_TO_HOME);
		// Intent homeIntent = new Intent(getApplicationContext(),
		// HomeScreen.class);
		// startActivity(homeIntent);
		// finish();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		cancelTimers();

	}
}

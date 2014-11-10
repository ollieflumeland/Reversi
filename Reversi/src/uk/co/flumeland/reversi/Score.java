package uk.co.flumeland.reversi;

public class Score {

	private String name;
	private String photolink;
	private int score;
	//private int rank;

	// Constructor
	public Score(String name, String photolink, int score) {
		super();
		this.name = name;
		this.photolink = photolink;
		this.score = score;
		//this.rank = rank;
	}

	public String getName() {
		return name;
	}

	public String getPhotolink() {
		return photolink;
	}

	public int getScore() {
		return score;
	}
	
	

}

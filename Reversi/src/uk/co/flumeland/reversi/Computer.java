package uk.co.flumeland.reversi;

import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import android.content.Context;
import android.widget.Toast;
import android.content.Intent;

public class Computer extends Player {
	
	private ArrayList<Integer> maxFlips = new ArrayList<Integer>();
	int max;
	private final int arrayLen = playableSquares.length; // length of vector arrays
	
	private static final byte[] bonus = new byte[] {4, 2, 2, 2, 2, 2, 2, 4,
													2, 0, 1, 1, 1, 1, 0, 2,
													2, 1, 0, 0, 0, 0, 1, 2,
													2, 1, 0, 0, 0, 0, 1, 2,
													2, 1, 0, 0, 0, 0, 1, 2,
													2, 1, 0, 0, 0, 0, 1, 2,
													2, 0, 1, 1, 1, 1, 0, 2,
													4, 2, 2, 2, 2, 2, 2, 4};
	
	public Computer(byte playAs) {
		super(playAs);
	}
	
	public void addBonus() {
		for (int i=0; i<arrayLen; i++) {
			if (playableSquares[i] > 0) { //i.e. if valid move
				playableSquares[i] += bonus[i];
			}
		}			
	}
	
	@Override
	public int chooseMove() {
		addBonus();
		max=0;
		maxFlips.clear();
		for (int i=0; i<arrayLen; i++) {
			if (playableSquares[i] > max) {
				max = playableSquares[i];
				maxFlips.clear(); // reset list of best moves
				maxFlips.add(i);
			}
			if (playableSquares[i] == max) {
				maxFlips.add(i);
			}
		}
		Random rand = new Random();
		int size = maxFlips.size();
		int choose = rand.nextInt(size);
		clearArray();
		return maxFlips.get(choose);
	}

	
}

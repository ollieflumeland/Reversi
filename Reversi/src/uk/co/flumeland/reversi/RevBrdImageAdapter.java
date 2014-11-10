package uk.co.flumeland.reversi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class RevBrdImageAdapter extends BaseAdapter {
	private Context context;
	private byte[] board;

	
	public RevBrdImageAdapter(Context c, byte[] board) {
		context = c;
		this.board = board;
	}

	@Override
	public int getCount() {
		return board.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(parent.getWidth()/8,parent.getWidth()/8));
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setPadding(3, 3, 3, 3);
		} else {
			imageView = (ImageView) convertView;
		}

		switch (board[position]) {
		case 1:
			if (Board.tiles==0) {
				imageView.setImageResource(R.drawable.blk_rnd_counter);
			} else {
				imageView.setImageResource(R.drawable.purp_oct_counter);
			}
			break;

		case 2:
			if (Board.tiles==0) {
				imageView.setImageResource(R.drawable.wht_rnd_counter);
			} else {
				imageView.setImageResource(R.drawable.brnz_oct_counter);
			}
			break;
		
		case 3:
			if (Board.tiles==0) {
				imageView.setImageResource(R.drawable.blank_poss_counter);
			} else {
				imageView.setImageResource(R.drawable.blank_poss_cnt_mod);
			}
			break;
		
		case 4:
			if (Board.tiles==0) {
				imageView.setImageResource(R.drawable.blank_counter);
			} else {
				imageView.setImageResource(R.drawable.blank_counter_mod);
			}
			break;

		default:
			if (Board.tiles==0) {
				imageView.setImageResource(R.drawable.blank_counter);
			} else {
				imageView.setImageResource(R.drawable.blank_counter_mod);
			}
			break;

		}
		
		return imageView;
	}
	
}


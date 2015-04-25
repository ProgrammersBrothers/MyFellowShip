package com.mygame.myfellowship.adapter;


import java.util.List;

import com.mygame.myfellowship.R;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 查找中的更多的界面中右边listview的适配器
 * @author 苦涩
 *
 */

public class FriendListViewAdapter extends BaseAdapter {
	private Context ctx;
	private List<String> listItems;
	private int layout = R.layout.item_list_friend;
	private OnWareItemClickClass onItemClickClass;
	public FriendListViewAdapter(Context ctx) {
		this.ctx = ctx;
	}

	public FriendListViewAdapter(Context ctx, int layout) {
		this.ctx = ctx;
		this.layout = layout;
	}

	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}
	public void SetOnWareItemClickClassListener(OnWareItemClickClass Listener){
		this.onItemClickClass = Listener;
	}
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Holder hold;
		if (arg1 == null) {
			hold = new Holder();
			arg1 = View.inflate(ctx, layout, null);
			hold.TextViewFriendName = (TextView) arg1
					.findViewById(R.id.TextViewFriendName);
			hold.TextViewAge = (TextView) arg1
					.findViewById(R.id.TextViewAge);
			hold.TextViewDistance = (TextView) arg1
					.findViewById(R.id.TextViewDistance);
			hold.TextViewActivityAddress = (TextView) arg1
					.findViewById(R.id.TextViewActivityAddress);
			arg1.setTag(hold);
		} else {
			hold = (Holder) arg1.getTag();
		}
		return arg1;
	}


	private static class Holder {
		TextView TextViewFriendName;
		TextView TextViewAge;
		TextView TextViewDistance;
		TextView TextViewActivityAddress;
	}
	public interface OnWareItemClickClass{
		public void OnItemClick(View v,int Position);
	}
	class OnOptionsClick implements OnClickListener{
		int position;
		
		public OnOptionsClick(int position) {
			this.position=position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (onItemClickClass!=null ) {
				onItemClickClass.OnItemClick(v, position);
			}
		}
	
	}
}
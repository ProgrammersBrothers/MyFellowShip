package com.mygame.myfellowship.adapter;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.mygame.myfellowship.R;
import com.mygame.myfellowship.struct.StructFriendListShowContent;
import com.mygame.myfellowship.utils.CharacterParse;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 查找中的更多的界面中右边listview的适配器
 * @author 苦涩
 *
 */

public class FriendListViewAdapter extends BaseAdapter {
	private Context ctx;
	private List<StructFriendListShowContent> listItems;
	private int layout = R.layout.item_list_friend;
	private OnWareItemClickClass onItemClickClass;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	public FriendListViewAdapter(Context ctx) {
		this.ctx = ctx;
	}

	public FriendListViewAdapter(Context ctx, int layout,List<StructFriendListShowContent> data,DisplayImageOptions options,ImageLoader mImageLoader) {
		this.ctx = ctx;
		this.layout = layout;
		this.listItems = data;
		this.options = options;
		this.mImageLoader = mImageLoader;
	}
	public void setListItems(List<StructFriendListShowContent> data){
		this.listItems = data;
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
			hold.TextViewNaturn = (TextView) arg1
					.findViewById(R.id.TextViewNaturn);
			hold.ImageViewUserImage = (ImageView) arg1
					.findViewById(R.id.ImageViewUserImage);
			arg1.setTag(hold);
		} else {
			hold = (Holder) arg1.getTag();
		}
		hold.TextViewFriendName.setText(listItems.get(arg0).getNickname());
		hold.TextViewAge.setText(listItems.get(arg0).getAge()+"岁");
		hold.TextViewDistance.setText(listItems.get(arg0).getDistance()+"km");
		hold.TextViewActivityAddress.setText(listItems.get(arg0).getAddress());
		hold.TextViewNaturn.setText(CharacterParse.getNature(listItems.get(arg0).getNaturn()));
		mImageLoader.displayImage(listItems.get(arg0).getUserimage(), hold.ImageViewUserImage, options, animateFirstListener);
		return arg1;
	}

	private static class Holder {
		ImageView ImageViewUserImage;
		TextView TextViewFriendName;
		TextView TextViewAge;
		TextView TextViewDistance;
		TextView TextViewActivityAddress;
		TextView TextViewNaturn;
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
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
//					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}

package com.mygame.myfellowship;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mygame.myfellowship.adapter.FriendListViewAdapter;
import com.mygame.myfellowship.bean.Response;
import com.mygame.myfellowship.struct.StructFriendListShowContent;
import com.mygame.myfellowship.utils.AssetUtils;
import com.mygame.myfellowship.view.XListView;
import com.mygame.myfellowship.view.XListView.IXListViewListener;

import android.content.Context;
import android.os.Bundle;

public class FriendListActivity extends BaseActivity implements IXListViewListener{
	
	XListView mListViewFriendList;
	FriendListViewAdapter mFriendListViewAdapter;
	List<StructFriendListShowContent> mStructFriendListShowContent;
	private onRefreshClass mOnRefreshClass;
	private final static int XLIST_REFRESH = 1; //下拉刷新
	private final static int XLIST_LOAD_MORE = 2; //加载更多
	@Override
	protected void onCreate(Bundle arg0) { 
		super.onCreate(arg0);
		setContentView(R.layout.activity_friend_list);
		initXListView(getApplicationContext());
		paserFriendList();
		
	}
	void paserFriendList(){
		
		String t = AssetUtils.getDataFromAssets(this, "friend_list.txt");	
		Response<List<StructFriendListShowContent>> response = new Gson().fromJson(t, 
				
				new TypeToken<Response<List<StructFriendListShowContent>>>(){}.getType());
		if(response.getResult(this)){
			mStructFriendListShowContent = response.getResponse();
			mFriendListViewAdapter = new FriendListViewAdapter(this, mStructFriendListShowContent);
			mListViewFriendList.setAdapter(mFriendListViewAdapter);
		}
		
	}
	private void initXListView(Context context) {
		mListViewFriendList = (XListView) findViewById(R.id.ListViewFriendList);
		// 首先不允许加载更多
		mListViewFriendList.setPullLoadEnable(false);
		// 允许下拉
		mListViewFriendList.setPullRefreshEnable(true);
		// 设置监听器
		mListViewFriendList.setXListViewListener(this);
		//
		mListViewFriendList.toRefreshing();
	}
	
	
	public interface onRefreshClass{
		public void onSuccess(Object list);
		public void onError(int arg0, String arg1);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		sendData(XLIST_REFRESH,new onRefreshClass() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(Object list) {
				// TODO Auto-generated method stub
				mStructFriendListShowContent = (List<StructFriendListShowContent>) list;
				mFriendListViewAdapter = new FriendListViewAdapter(getApplicationContext(), mStructFriendListShowContent);
				mListViewFriendList.setAdapter(mFriendListViewAdapter);
				mListViewFriendList.stopLoadMore();
				mListViewFriendList.stopRefresh();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	@Override
	public void onLoadMore() {
	}
	public void sendData(int sendMode,onRefreshClass OnRefreshClass){
		mOnRefreshClass = OnRefreshClass;
		//向服务器请求数据
		String t = AssetUtils.getDataFromAssets(this, "friend_list.txt");	
		Response<List<StructFriendListShowContent>> response = new Gson().fromJson(t, 
				
				new TypeToken<Response<List<StructFriendListShowContent>>>(){}.getType());
		if(response.getResult(this)){
			mOnRefreshClass.onSuccess(response.getResponse());

		}
	}
}
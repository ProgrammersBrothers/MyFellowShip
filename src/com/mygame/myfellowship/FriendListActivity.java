package com.mygame.myfellowship;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mygame.myfellowship.adapter.FriendListViewAdapter;
import com.mygame.myfellowship.bean.Constant;
import com.mygame.myfellowship.bean.Response;
import com.mygame.myfellowship.bean.Urls;
import com.mygame.myfellowship.custom.SlidingMenu;
import com.mygame.myfellowship.gps.MyLocation;
import com.mygame.myfellowship.http.AjaxCallBack;
import com.mygame.myfellowship.http.AjaxParams;
import com.mygame.myfellowship.struct.StructBaseUserInfo;
import com.mygame.myfellowship.struct.StructFriendListShowContent;
import com.mygame.myfellowship.utils.AssetUtils;
import com.mygame.myfellowship.utils.ToastHelper;
import com.mygame.myfellowship.view.XListView;
import com.mygame.myfellowship.view.XListView.IXListViewListener;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.pgyersdk.update.PgyUpdateManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
public class FriendListActivity extends BaseActivity implements IXListViewListener{
	
	XListView mListViewFriendList;
	FriendListViewAdapter mFriendListViewAdapter;
	List<StructFriendListShowContent> mStructFriendListShowContent;
	private onRefreshClass mOnRefreshClass;
	private final static int XLIST_REFRESH = 1; //下拉刷新
	private final static int XLIST_LOAD_MORE = 2; //加载更多
	private StructBaseUserInfo mStructBaseUserInfo = new StructBaseUserInfo();
	private TextView mTextViewUserName;
	//定位参数
	private MyLocation myLocation = new MyLocation();
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	
	private SlidingMenu mMenu;
	
	public Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mTextViewUserName.setText(mStructBaseUserInfo.getNickname());
				break;
				default:break;
			}
		}
	};
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location != null){
				myLocation.setLatitude(location.getLatitude());
				myLocation.setLongitude(location.getLongitude());
				if(myLocation.getLatitude() == 0.0 && myLocation.getLongitude() == 0.0){

				}else{
					//得到经纬度
					getUserBaseInfo(mStructBaseUserInfo);
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
					SubmitAllUserInfo(mStructBaseUserInfo);
					if(mLocClient.isStarted()){
						mLocClient.stop();
					}
				}
				Log.d("huwei", "地理位置更新，纬度 = " + location.getLatitude()+"，经度 = "+location.getLongitude());
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}
	private void locationInit() {
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		if(!mLocClient.isStarted()){
			mLocClient.start();
		}
	}
	@Override
	protected void onCreate(Bundle arg0) { 
		super.onCreate(arg0);
		setContentView(R.layout.activity_friend_list);
        mMenu = (SlidingMenu) findViewById(R.id.id_menu);
        mTextViewUserName = (TextView) findViewById(R.id.TextViewUserName);
		mMenu.setSlideEnable(true);
		setTitle("朋友列表");
		addBackImage(R.drawable.ic_slid, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mMenu != null)
					mMenu.toggle();
			}
		});
		initXListView(getApplicationContext());
	}
	
	public void OnclickButtonQuitLogin(View v){
		AlertDialog.Builder builder;
		if(Build.VERSION.SDK_INT < 11){
			builder = new Builder(this);
		}else{
			builder = new Builder(this,R.style.dialog);
		}
		builder.setTitle(R.string.title_quitlogin);
		
		builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}
	//解析朋友列表，并显示
	void paserFriendList(String t){
		
//		String t = AssetUtils.getDataFromAssets(this, "friend_list.txt");	
		Response<List<StructFriendListShowContent>> response = new Gson().fromJson(t, 
				
				new TypeToken<Response<List<StructFriendListShowContent>>>(){}.getType());
		if(response.getResult()){
			mStructFriendListShowContent = response.getResponse();
			if(mFriendListViewAdapter == null){
				mFriendListViewAdapter = new FriendListViewAdapter(this, mStructFriendListShowContent);
				mListViewFriendList.setAdapter(mFriendListViewAdapter);
			}else{
				mFriendListViewAdapter.setListItems(mStructFriendListShowContent);
				mFriendListViewAdapter.notifyDataSetChanged();
			}
			
			mListViewFriendList.stopLoadMore();
			mListViewFriendList.stopRefresh();
		}else{
			ToastHelper.ToastLg(response.getMessage(), getActivity());
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
		locationInit();
		
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
		if(response.getResult()){
			mOnRefreshClass.onSuccess(response.getResponse());

		}
	}
	//获取用户信息
	void SubmitAllUserInfo(StructBaseUserInfo x_StructBaseUserInfo){
		String getjson = new Gson().toJson(x_StructBaseUserInfo);
		Log.i("huwei", getjson);
		showReqeustDialog(R.string.matching_for_you);
		AjaxParams params = new AjaxParams();
		params.put("userMsg", getjson);

		
		getFinalHttp().post(Urls.getuser, params, new AjaxCallBack<String>(){

			@Override
			public void onSuccess(String t) {
				super.onSuccess(t);
				paserFriendList(t);
				cancelRequestDialog();
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				ToastHelper.ToastLg(strMsg, getActivity());
				cancelRequestDialog();
			}
		});
		
	}
	//基本用户信息解析
	void getUserBaseInfo(StructBaseUserInfo x_StructBaseUserInfo){
		x_StructBaseUserInfo.setUserid(preferences.getString(Constant.USER_ID, ""));
		x_StructBaseUserInfo.setNickname(preferences.getString(Constant.NICK_NAME, ""));
		x_StructBaseUserInfo.setSex(preferences.getString(Constant.Sex, ""));

		x_StructBaseUserInfo.setBirthday(preferences.getString(Constant.Age, ""));

		x_StructBaseUserInfo.setStature(preferences.getString(Constant.Height,  ""));

		x_StructBaseUserInfo.setIfHaveChildren(preferences.getString(Constant.IfChild, ""));
	
		x_StructBaseUserInfo.setIfMindHaveChildren(preferences.getString(Constant.IfMind,  ""));

		x_StructBaseUserInfo.setSubstanceNeeds(preferences.getString(Constant.ThingAsk, ""));

		x_StructBaseUserInfo.setInLovePeriod(preferences.getString(Constant.MarryNum, ""));

		x_StructBaseUserInfo.setFaith(preferences.getString(Constant.Faith, ""));
		
		List<String> coordinates = new ArrayList<String>();
		coordinates.add(Double.toString(myLocation.getLongitude()));
		coordinates.add(Double.toString(myLocation.getLatitude()));
		x_StructBaseUserInfo.setCoordinates(coordinates);
		
		x_StructBaseUserInfo.setSpareTime(preferences.getString(Constant.Freetime,""));
		
		x_StructBaseUserInfo.setMBTI(preferences.getString(Constant.Nature,""));
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		PgyFeedbackShakeManager.register(this, Constant.PgyerAPPID);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		PgyFeedbackShakeManager.unregister();
	}
}
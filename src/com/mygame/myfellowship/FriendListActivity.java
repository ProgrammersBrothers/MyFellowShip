package com.mygame.myfellowship;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.mygame.myfellowship.utils.FormatTools;
import com.mygame.myfellowship.utils.HttpUploadedFile;
import com.mygame.myfellowship.utils.PathUtils;
import com.mygame.myfellowship.utils.PhotoUtils;
import com.mygame.myfellowship.utils.SimpleNetTask;
import com.mygame.myfellowship.utils.ToastHelper;
import com.mygame.myfellowship.utils._HttpUploadedFile;
import com.mygame.myfellowship.view.XListView;
import com.mygame.myfellowship.view.XListView.IXListViewListener; 
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableLayout.LayoutParams;
public class FriendListActivity extends BaseActivity implements IXListViewListener{
	
	XListView mListViewFriendList;
	FriendListViewAdapter mFriendListViewAdapter;
	List<StructFriendListShowContent> mStructFriendListShowContent;
	private onRefreshClass mOnRefreshClass;
	private final static int XLIST_REFRESH = 1; //下拉刷新
	private final static int XLIST_LOAD_MORE = 2; //加载更多
	private StructBaseUserInfo mStructBaseUserInfo = new StructBaseUserInfo();
	private TextView mTextViewUserName;
	private TextView mTextViewVersion;
	
	public static final int IMAGE_PICK_REQUEST = 10001;
	public static final int CROP_REQUEST = 10002;
	public static final int HANDLE_SET_USER_IMAGE = 10002;
	
	
	//定位参数
	private MyLocation myLocation = new MyLocation();
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	
	private SlidingMenu mMenu;
	private ImageView mImageViewUserPicture;
	private String mUploadFilePathName = "";
	
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	
	private final String TAG = "FriendListActivity";
	public Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mTextViewUserName.setText(mStructBaseUserInfo.getNickname());
				Bitmap userimage = getHttpBitmap(mStructBaseUserInfo.getUserimage());
				if(userimage != null){
					mImageViewUserPicture.setImageBitmap(userimage);
				}
				break;
				default:break;
			}
		}
	};
	
	void ImageViewUserPicture(View v){
		
	}
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
	/*
	 * 获取应用版本号
	 * return 返回版本号
	 * */
	public String getVersionName()
	{
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packInfo.versionName;
	}
	@Override
	protected void onCreate(Bundle arg0) { 
		super.onCreate(arg0);
		setContentView(R.layout.activity_friend_list);
        mMenu = (SlidingMenu) findViewById(R.id.id_menu);
        mTextViewUserName = (TextView) findViewById(R.id.TextViewUserName);
        mTextViewVersion = (TextView) findViewById(R.id.TextViewVersion);
        mImageViewUserPicture = (ImageView) findViewById(R.id.ImageViewUserPicture);
        mTextViewVersion.setText("V"+getVersionName());
        mImageViewUserPicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		/*	      Intent intent = new Intent(Intent.ACTION_PICK, null);
			      Intent intent = new Intent(Intent.ACTION_PICK, null);
			      intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			      startActivityForResult(intent, IMAGE_PICK_REQUEST);
			      //******************************************
		    	/*	AjaxParams params = new AjaxParams();
		    		params.put(Constant.USER_ID,mStructBaseUserInfo.getUserid());
		    		
		    		InputStream imageinput =  FormatTools.getInstance().Drawable2InputStream(getResources().getDrawable(R.drawable.xingfukuaiche));
					params.put("imageurl",imageinput);
		    		showReqeustDialog(R.string.sending_data);
		    		getFinalHttp().post(Urls.ImageUrl, params, new AjaxCallBack<String>(){

		    			@Override
		    			public void onSuccess(String t) {
		    				super.onSuccess(t);
		    				cancelRequestDialog();
		    			}

		    			@Override
		    			public void onFailure(Throwable t, int errorNo, String strMsg) {
		    				super.onFailure(t, errorNo, strMsg);
		    				ToastHelper.ToastLg(strMsg, getActivity());
		    				cancelRequestDialog();
		    			}
		    		});
		    	  	Log.i("huwei","上传文件："+ mUploadFilePathName+"到服务器");*/
		    	  	//***************************************************************
//		    	  	new UploadPhotoTask().execute();
			}
		});
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
		initDisplayOptions();
	}
	private void initDisplayOptions() {
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_stub)	//设置正在加载图片
//		.showImageOnLoading(R.drawable.ic_stub) //1.8.7新增
		.showImageForEmptyUri(R.drawable.ic_empty)	
		.showImageOnFail(R.drawable.ic_error)	//设置加载失败图片
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(20))	//设置图片角度,0为方形，360为圆角
		.build();
		
		mImageLoader = ImageLoader.getInstance();
		
		//缓存目录
		//有SD卡 path=/sdcard/Android/data/com.example.universalimageloadertest/cache
		//无SD卡 path=/data/data/com.example.universalimageloadertest/cache
		File cacheDir = StorageUtils.getCacheDirectory(this);
		Log.e("huwei", "cacheDir path="+cacheDir.getAbsolutePath());
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    Log.d("","on Activity result " + requestCode + " " + resultCode);
	    super.onActivityResult(requestCode, resultCode, data);
	    if (resultCode == Activity.RESULT_OK) {
	    	if (requestCode == IMAGE_PICK_REQUEST) {
	    		Uri uri = data.getData();
	    		startImageCrop(uri, 300, 300, CROP_REQUEST);
	    	} else if (requestCode == CROP_REQUEST) {
	    	  
	    	  	mUploadFilePathName = saveCropAvatar(data);
	    	  	Message msg = new Message();
	    	  	msg.what = HANDLE_SET_USER_IMAGE;
	    	  	mHandler.sendMessage(msg);
	    		AjaxParams params = new AjaxParams();
	    		params.put(Constant.USER_ID,mStructBaseUserInfo.getUserid());
	    		
				try {
					params.put("imageurl",new File(mUploadFilePathName));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		showReqeustDialog(R.string.sending_data);
	    		getFinalHttp().post(Urls.ImageUrl, params, new AjaxCallBack<String>(){

	    			@Override
	    			public void onSuccess(String t) {
	    				super.onSuccess(t);
	    				cancelRequestDialog();
	    			}

	    			@Override
	    			public void onFailure(Throwable t, int errorNo, String strMsg) {
	    				super.onFailure(t, errorNo, strMsg);
	    				ToastHelper.ToastLg(strMsg, getActivity());
	    				cancelRequestDialog();
	    			}
	    		});
	    	  	Log.i("huwei","上传文件："+ mUploadFilePathName+"到服务器");
//	    	  	new UploadPhotoTask().execute();
	      }
	    }
	}
	public Uri startImageCrop(Uri uri, int outputX, int outputY,
          int requestCode) {
		Intent intent = null;
		intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		String outputPath = PathUtils.getAvatarTmpPath();
		Uri outputUri = Uri.fromFile(new File(outputPath));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", false); // face detection
		startActivityForResult(intent, requestCode);
		return outputUri;
	}
	public static String getSDcardDir() {
	    return Environment.getExternalStorageDirectory().getPath() + "/";
	}
	
	public static String checkAndMkdirs(String dir) {
	    File file = new File(dir);
	    if (file.exists() == false) {
	    	file.mkdirs();
	    }
	    return dir;
	}
	private String saveCropAvatar(Intent data) {
	    Bundle extras = data.getExtras();
	    String path = null;
	    if (extras != null) {
	    	Bitmap bitmap = extras.getParcelable("data");
	    	if (bitmap != null) {
	    		bitmap = PhotoUtils.toRoundCorner(bitmap, 10);
	    		String filename = new SimpleDateFormat("yyMMddHHmmss")
	            	.format(new Date())+".jpg";
	    		path = PathUtils.getAvatarDir() + filename;
	    		Log.d("huwei","save bitmap to " + path);
	    		PhotoUtils.saveBitmap(PathUtils.getAvatarDir(), filename,
	    				bitmap, true);
	    		if (bitmap != null && bitmap.isRecycled() == false) {
	          //bitmap.recycle();
	    		}
	    	}
	    }
	    return path;
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
				mFriendListViewAdapter = new FriendListViewAdapter(this, R.layout.item_list_friend,mStructFriendListShowContent,options,mImageLoader);
				//设置scroll时停止加载图片
				mListViewFriendList.setOnScrollListener(new PauseOnScrollListener(mImageLoader, true, false));
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
		
//		x_StructBaseUserInfo.setIfHaveChildren(preferences.getString(Constant.IfChild, ""));
//	
//		x_StructBaseUserInfo.setIfMindHaveChildren(preferences.getString(Constant.IfMind,  ""));
//
//		x_StructBaseUserInfo.setSubstanceNeeds(preferences.getString(Constant.ThingAsk, ""));
//
//		x_StructBaseUserInfo.setInLovePeriod(preferences.getString(Constant.MarryNum, ""));
//
//		x_StructBaseUserInfo.setFaith(preferences.getString(Constant.Faith, ""));
		
		List<String> coordinates = new ArrayList<String>();
		coordinates.add(Double.toString(myLocation.getLongitude()));
		coordinates.add(Double.toString(myLocation.getLatitude()));
		x_StructBaseUserInfo.setCoordinates(coordinates);
		
//		x_StructBaseUserInfo.setSpareTime(preferences.getString(Constant.Freetime,""));
		
		x_StructBaseUserInfo.setMBTI(preferences.getString(Constant.Nature,""));
	}
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HttpUploadedFile.POST_PROGRESS_NOTIFY:
				int completePercent = msg.arg1;
				if(completePercent == 100)
				Toast.makeText(FriendListActivity.this, R.string.upload_photo_success, Toast.LENGTH_SHORT).show();
				break;
			case HANDLE_SET_USER_IMAGE:
				mImageViewUserPicture.setImageBitmap(getLoacalBitmap(mUploadFilePathName));
				break;
			default:
				break;
			}
		}
	};
	/**

	* 加载本地图片

	* http://bbs.3gstdy.com

	* @param url

	* @return

	*/

	public static Bitmap getLoacalBitmap(String url) {

	     try {

	          FileInputStream fis = new FileInputStream(url);

	          return BitmapFactory.decodeStream(fis);

	     } catch (FileNotFoundException e) {

	          e.printStackTrace();

	          return null;

	     }

	}
	/**

	* 从服务器取图片

	*http://bbs.3gstdy.com

	* @param url

	* @return

	*/

	public Bitmap getHttpBitmap(String url) {

	     URL myFileUrl = null;

	     Bitmap bitmap = null;
	     if(url == null){
	    	 Log.e(TAG, "url is null");
	     }
	     try {

	          Log.d(TAG, url);

	          myFileUrl = new URL(url);
	          
	          HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();

	          conn.setConnectTimeout(0);

	          conn.setDoInput(true);

	          conn.connect();

	          InputStream is = conn.getInputStream();

	          bitmap = BitmapFactory.decodeStream(is);

	          is.close();

	     } catch (MalformedURLException e) {

	          e.printStackTrace();

	     }
	     catch (IOException e) {

	          e.printStackTrace();

	     }

	     return bitmap;

	}
	private class UploadPhotoTask extends AsyncTask<String, Void, Boolean>{
		
    	@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Boolean doInBackground(String... params) {
			Map<String, String> textMap = new HashMap<String, String>();
			textMap.put(Constant.USER_ID,mStructBaseUserInfo.getUserid());
			Map<String, String> fileMap = new HashMap<String, String>();
			fileMap.put("imageurl", mUploadFilePathName);
			String ret = _HttpUploadedFile.formUpload(Urls.ImageUrl, textMap, fileMap);
			System.out.println(ret);
    		return true;
    	}  
    	
    	protected void onPostExecute(Boolean result){
			if(result){
				Toast.makeText(FriendListActivity.this, R.string.upload_photo_success, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(FriendListActivity.this, R.string.upload_photo_fail, Toast.LENGTH_SHORT).show();

			}
    	}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		PgyFeedbackShakeManager.register(this, Constant.PgyerAPPID);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		PgyFeedbackShakeManager.unregister();
	}
}

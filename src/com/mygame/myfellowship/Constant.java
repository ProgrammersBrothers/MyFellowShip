package com.mygame.myfellowship;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;

public class Constant {

	public static final long MIN_LOGINTIME = 0;
	public static class Preference {

		public static SharedPreferences getSharedPreferences(Context context) {
			return context.getSharedPreferences("MyFellowShipSharePref", Context.MODE_PRIVATE);
		}
	}

}

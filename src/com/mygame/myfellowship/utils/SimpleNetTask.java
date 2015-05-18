package com.mygame.myfellowship.utils;

import com.mygame.myfellowship.SelfDefineApplication;

import android.content.Context;

/**
 * Created by lzw on 14-9-27.
 */
public abstract class SimpleNetTask extends NetAsyncTask {
  protected SimpleNetTask(Context cxt) {
    super(cxt);
  }

  protected SimpleNetTask(Context cxt, boolean openDialog) {
    super(cxt, openDialog);
  }


  @Override
  protected void onPost(Exception e) {
    if (e != null) {
      e.printStackTrace();
      ToastHelper.ToastLg(e.getMessage(),SelfDefineApplication.getInstance());
      //Utils.toast(ctx, R.string.pleaseCheckNetwork);
    } else {
      onSucceed();
    }
  }

  protected abstract void doInBack() throws Exception;

  protected abstract void onSucceed();
}

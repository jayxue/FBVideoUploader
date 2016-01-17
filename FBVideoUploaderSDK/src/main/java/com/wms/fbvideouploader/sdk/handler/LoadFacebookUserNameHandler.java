package com.wms.fbvideouploader.sdk.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.wms.fbvideouploader.sdk.R;

public class LoadFacebookUserNameHandler extends Handler {

	private Context context;

	public LoadFacebookUserNameHandler(Context context) {
		this.context = context;
	}

	public void handleMessage(Message msg) {
		if (msg.what == HandlerMessage.FACEBOOK_USER_INFO_LOADED) {
			Toast.makeText(context, context.getString(R.string.loginSuccessful), Toast.LENGTH_LONG).show();
		}
	}
}

package com.wms.fbvideouploader.sdk.task;

import com.restfb.exception.FacebookNetworkException;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.User;
import com.wms.fbvideouploader.sdk.R;
import com.wms.fbvideouploader.sdk.facebook.FacebookUtil;
import com.wms.fbvideouploader.sdk.handler.HandlerMessage;
import com.wms.fbvideouploader.sdk.util.DialogUtil;
import com.wms.fbvideouploader.sdk.util.MessageUtil;
import com.wms.fbvideouploader.sdk.util.SharedPreferenceUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class LoadFacebookUserInfoTask extends AsyncTask<String, Void, Void> {

	String errorString = null;
	
	private Context context = null;
	private Handler handler = null;

	private ProgressDialog progressDialog = null;
	
	public LoadFacebookUserInfoTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}
	
	protected void onPreExecute() {
		progressDialog = DialogUtil.showWaitingProgressDialog(context, ProgressDialog.STYLE_SPINNER, "Loading information from Facebook...", false);
    }
	
	@Override
	protected Void doInBackground(String... params) {
		// params[0] is the Facebook access token, params[1] is the user ID. When invoking getUser(), user ID parameter is set as "me".
		// The real user ID will be loaded after the method is successfully executed
		try {
			User user = FacebookUtil.getUser(params[0], params[1]);
			if(user != null) {
				// Facebook user ID is needed in publishing items. Facebook user name can be saved for further use such as displaying on screen
				SharedPreferenceUtil.savePreferenceItemByName(context, SharedPreferenceUtil.FacebookUserID, user.getId());
				SharedPreferenceUtil.savePreferenceItemByName(context, SharedPreferenceUtil.FacebookUserName, user.getName());
			}
		}
		catch(FacebookOAuthException e) {
			errorString = "Facebook authentication failed";
		}
		catch(FacebookNetworkException e) {
			errorString = "Facebook network exception occurred";
		}
		return null;
	}
	
	protected void onPostExecute(Void result) {
		progressDialog.dismiss();

		if(errorString != null) {
			DialogUtil.showExceptionAlertDialog(context, context.getString(R.string.cannotLoadFacebookUserInfo), errorString);
		}
		else {
			MessageUtil.sendHandlerMessage(handler, HandlerMessage.FACEBOOK_USER_INFO_LOADED);
		}
    }

}

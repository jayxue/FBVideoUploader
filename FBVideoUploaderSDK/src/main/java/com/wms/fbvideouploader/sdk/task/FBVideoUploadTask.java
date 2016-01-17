package com.wms.fbvideouploader.sdk.task;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.wms.fbvideouploader.sdk.R;
import com.wms.fbvideouploader.sdk.facebook.VideoUploader;
import com.wms.fbvideouploader.sdk.handler.HandlerMessage;
import com.wms.fbvideouploader.sdk.handler.UploadProgressHandler;
import com.wms.fbvideouploader.sdk.type.HttpClientWorkingStatus;
import com.wms.fbvideouploader.sdk.util.DialogUtil;
import com.wms.fbvideouploader.sdk.util.MessageUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ProgressBar;

public class FBVideoUploadTask extends AsyncTask<String, Integer, String> {

	private Context context = null;
	private ProgressBar progressBar = null;
	private UploadProgressHandler uploadProgressHandler = null;

	private ProgressDialog progressDialog = null;

	private String videoID = null;
	
	public FBVideoUploadTask(Context context, ProgressBar progressBarUpload, UploadProgressHandler uploadProgressHandler) {
		this.context = context;
		this.progressBar = progressBarUpload;
		this.uploadProgressHandler = uploadProgressHandler;
		this.uploadProgressHandler.setTask(this);
	}
	
	protected void onPreExecute() {
		progressBar.setProgress(0);
		progressDialog = DialogUtil.showWaitingProgressDialog(context, ProgressDialog.STYLE_SPINNER, context.getString(R.string.uploadingVideo), false);
	}
	
	@Override
	protected String doInBackground(String... params) {
		// params[0] is Facebook access token, params[1] is the user ID (personal user ID or public page's ID), params[2] is the file path, params[3] is title, params[4] is description
		try {
			String jsonString = VideoUploader.postVideo(params[0], params[1], params[2], params[3], params[4], progressBar, uploadProgressHandler);
			JSONObject videoIDObject = new JSONObject(jsonString);
			videoID = videoIDObject.getString("id");
			return videoID;
		}
		catch (IOException | JSONException e) {
			return null;
		}
	}
    
    protected void onPostExecute(String result) {
		progressDialog.dismiss();

		if(VideoUploader.status.equals(HttpClientWorkingStatus.UPLOAD_COMPLETED)) {
    	    VideoUploader.status = HttpClientWorkingStatus.SLEEPING;
			MessageUtil.sendHandlerMessage(uploadProgressHandler, HandlerMessage.FACEBOOK_VIDEO_UPLOAD_COMPLETED);
    	}
    }
    
    public String getVideoID() {
    	return videoID;
    }
}

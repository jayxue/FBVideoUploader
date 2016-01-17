package com.wms.fbvideouploader.sdk.handler;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.wms.fbvideouploader.sdk.R;
import com.wms.fbvideouploader.sdk.activity.UploadVideoActivity;
import com.wms.fbvideouploader.sdk.task.FBVideoUploadTask;
import com.wms.fbvideouploader.sdk.util.DialogUtil;

public class UploadProgressHandler extends Handler {

	private UploadVideoActivity activity = null;

	private FBVideoUploadTask fbVideoUploadTask = null;

	public UploadProgressHandler(UploadVideoActivity activity) {
		this.activity = activity;
	}

	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		if(msg.what == HandlerMessage.FACEBOOK_VIDEO_UPLOAD_PROGRESS_UPDATE) {
			int progress = activity.getProgressBarUploadVideo().getProgress();
			if(progress < 10) {
				activity.getTextViewProgress().setText(" 0" + progress + "%");
			}
			else {
				activity.getTextViewProgress().setText(" " + progress + "%");
			}
		}
		else if(msg.what == HandlerMessage.FACEBOOK_VIDEO_UPLOAD_COMPLETED) {
			if(!fbVideoUploadTask.getVideoID().isEmpty()) {
				activity.getTextViewVideoUrl().setText("https://www.facebook.com/video.php?v=" + fbVideoUploadTask.getVideoID());
				activity.clearVideoFileName();
				Toast.makeText(activity, R.string.videoUploadCompleted, Toast.LENGTH_LONG).show();
			}
			else {
				DialogUtil.showExceptionAlertDialog(activity, activity.getString(R.string.videoUploadFailedTitle), activity.getString(R.string.videoUploadFailed));
			}
		}
	}

	public void setTask(FBVideoUploadTask task) {
		fbVideoUploadTask = task;
	}

}

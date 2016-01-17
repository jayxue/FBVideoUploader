package com.wms.fbvideouploader.sdk.dialog;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import com.wms.fbvideouploader.sdk.R;
import com.wms.fbvideouploader.sdk.activity.UploadVideoActivity;
import com.wms.fbvideouploader.sdk.handler.UploadProgressHandler;
import com.wms.fbvideouploader.sdk.task.FBVideoUploadTask;
import com.wms.fbvideouploader.sdk.util.SharedPreferenceUtil;

public class ConfirmUploadVideoDialogBuilder extends Builder {

	private UploadVideoActivity activity = null;

	public ConfirmUploadVideoDialogBuilder(UploadVideoActivity activity) {
		super(activity);
		this.activity = activity;
		this.setCancelable(false);
		this.setMessage(getContext().getString(R.string.confirmUploadVideo));
		this.setNegativeButton(getContext().getString(R.string.cancel), null);
		this.setPositiveButton(getContext().getString(R.string.yes), new ConfirmUploadVideoDialogOnClickListener());
	}

	private class ConfirmUploadVideoDialogOnClickListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			FBVideoUploadTask facebookVideoUploadTask = new FBVideoUploadTask(activity, activity.getProgressBarUploadVideo(), new UploadProgressHandler(activity));
			// Upload video to personal wall on Facebook. The parameters are:
			// String facebookAccessToken, String facebookUserID, String filePath, String title, String description
			facebookVideoUploadTask.execute(SharedPreferenceUtil.getPreferenceItemByName(getContext(), SharedPreferenceUtil.FacebookUserAccessToken),
					SharedPreferenceUtil.getPreferenceItemByName(getContext(), SharedPreferenceUtil.FacebookUserID), activity.getVideoFileName(),
					activity.getVideoTitle(), activity.getVideoDescription());

		}

	}
}

package com.wms.fbvideouploader.sdk.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.easy.facebook.android.facebook.FBLoginManager;
import com.easy.facebook.android.facebook.Facebook;
import com.easy.facebook.android.facebook.LoginListener;
import com.wms.fbvideouploader.sdk.dialog.ConfirmUploadVideoDialogBuilder;
import com.wms.fbvideouploader.sdk.handler.LoadFacebookUserNameHandler;
import com.wms.fbvideouploader.sdk.listener.ImageButtonBackgroundSelector;
import com.wms.fbvideouploader.sdk.task.LoadFacebookUserInfoTask;
import com.wms.fbvideouploader.sdk.util.DialogUtil;
import com.wms.fbvideouploader.sdk.util.FileUtil;
import com.wms.fbvideouploader.sdk.util.SharedPreferenceUtil;
import com.wms.fbvideouploader.sdk.R;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class UploadVideoActivity extends Activity implements LoginListener {
	private static final String LOG_TAG = "UploadVideoActivity";

	private ProgressBar progressBarUploadVideo = null;
	private EditText editTextVideoTitle = null;
	private EditText editTextVideoDescription = null;
	private TextView textViewFilePath = null;
	private TextView textViewVideoUrl = null;
	private TextView textViewProgress = null;
	private VideoView videoViewPreview = null;

	private String videoFileName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.upload_video);

		editTextVideoTitle = (EditText) findViewById(R.id.editTextTitle);
		editTextVideoDescription = (EditText) findViewById(R.id.editTextDescription);

		textViewFilePath = (TextView) findViewById(R.id.textViewFilePath);
		textViewVideoUrl = (TextView) findViewById(R.id.textViewVideoUrl);
		textViewProgress = (TextView) findViewById(R.id.textViewProgress);

		progressBarUploadVideo = (ProgressBar) findViewById(R.id.progressBarUploadVideo);

		videoViewPreview = (VideoView) findViewById(R.id.videoViewPreview);

		ImageButton imageButtonUploadVideo = (ImageButton) findViewById(R.id.imageButtonUploadVideo);
		imageButtonUploadVideo.setOnClickListener(new ImageButtonUploadVideoOnClickListener());
		imageButtonUploadVideo.setOnTouchListener(new ImageButtonBackgroundSelector());

		ImageButton imageButtonTakeVideo = (ImageButton) findViewById(R.id.imageButtonTakeVideo);
		imageButtonTakeVideo.setOnClickListener(new ImageButtonTakeVideoOnClickListener());
		imageButtonTakeVideo.setOnTouchListener(new ImageButtonBackgroundSelector());

		ImageButton imageButtonGallery = (ImageButton) findViewById(R.id.imageButtonGallery);
		imageButtonGallery.setOnClickListener(new ImageButtonGalleryOnClickListener());
		imageButtonGallery.setOnTouchListener(new ImageButtonBackgroundSelector());

		// Do not show the soft keyboard
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Dev purpose only. When releasing an app, you'll want to remove this and use another way to create key hash for the release version.
		printDevelopmentKeyHash();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IntentRequestCode.TAKE_VIDEO && resultCode == RESULT_OK) {
			// videoFileName has been prepared for taking video
			File file = new File(videoFileName);
			// On Android 2.2, the file may not be created, therefore we need to check the returned URI.
			if (!file.exists()) {
				if (data.getData() != null) {
					videoFileName = getRealPathFromURI(data.getData());
					if(videoFileName != null) {
						onVideoReady();
					}
				}
				else {
					videoFileName = null;
					Toast.makeText(this, getString(R.string.videoNotAvailable), Toast.LENGTH_LONG).show();
				}
			}
			else {
				onVideoReady();
			}
		}
		else if (requestCode == IntentRequestCode.PICK_UP_VIDEO && resultCode == RESULT_OK) {
			Uri selectedVideo = data.getData();
			videoFileName = getRealPathFromURI(selectedVideo);
			if(videoFileName != null) {
				onVideoReady();
			}
			else {
				Toast.makeText(this, getString(R.string.videoNotAvailable), Toast.LENGTH_LONG).show();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private String getRealPathFromURI(Uri contentUri) {
		String filePath = null;
		String[] projection = { MediaStore.Video.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
		if(cursor.moveToFirst()) {
			int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
			filePath = cursor.getString(columnIndex);
		}
		cursor.close();
		return filePath;
	}

	private class ImageButtonUploadVideoOnClickListener implements ImageButton.OnClickListener {

		@Override
		public void onClick(View v) {
			// A video should be available before uploading
			if(videoFileName == null) {
				Toast.makeText(UploadVideoActivity.this, R.string.takeOrSelectVideo, Toast.LENGTH_LONG).show();
				return;
			}

			// Title must be provided for a Facebook video
			if(editTextVideoTitle.getText().toString().trim().isEmpty()) {
				DialogUtil.showDialog(UploadVideoActivity.this, getString(R.string.enterVideoTitle));
				return;
			}

			// A Facebook app should be pre-configured
			if(getString(R.string.FacebookAppID).isEmpty()) {
				DialogUtil.showDialog(UploadVideoActivity.this, getString(R.string.configureFacebookApp));
				return;
			}

			// Check if Facebook login is needed
			String permissions[] = { "publish_actions" };
			FBLoginManager fbLoginManager = new FBLoginManager(UploadVideoActivity.this, R.layout.upload_video, getString(R.string.FacebookAppID), permissions);
			if (fbLoginManager.existsSavedFacebook()) {
				fbLoginManager.loadFacebook();
				// User has successfully logged in, so confirm with user if uploading is expected
				new ConfirmUploadVideoDialogBuilder(UploadVideoActivity.this).create().show();
			}
			else {
				fbLoginManager.login();
			}
		}

	}

	private File getTempVideoFile() {
		// The method below will return a file path like: /mnt/sdcard/com.company.app
		videoFileName = FileUtil.getAppExternalStoragePath(this);
		File file = new File(videoFileName);
		if (!file.exists()) {
			// Create the folder if it does not exist
			file.mkdir();
		}

		// Generate a UUID as file name and attach to path
		videoFileName += "/" + UUID.randomUUID().toString() + ".3gp";

		file = new File(videoFileName);
		return file;
	}

	private class ImageButtonTakeVideoOnClickListener implements ImageButton.OnClickListener {

		@Override
		public void onClick(View v) {
			startTakeVideo();
		}

	}

	private class ImageButtonGalleryOnClickListener implements ImageButton.OnClickListener {

		@Override
		public void onClick(View v) {
			startPickVideo();
		}

	}

	private void startTakeVideo() {
		resetProgress();
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempVideoFile()));
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 300);
		startActivityForResult(intent, IntentRequestCode.TAKE_VIDEO);
	}

	private void startPickVideo() {
		resetProgress();
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
		try {
			startActivityForResult(intent, IntentRequestCode.PICK_UP_VIDEO);
		}
		catch (ActivityNotFoundException e) {
			 // On Andriod 2.2, the above method may cause exception due to not finding an activity to handle the intent. Use the method below instead.
			Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
			mediaChooser.setType("video/*");
			startActivityForResult(mediaChooser, IntentRequestCode.PICK_UP_VIDEO);
		}
		catch (SecurityException e) {
			// When picking up videos, there may be an exception like:
			//  java.lang.SecurityException:
			//      Permission Denial:
			//      starting Intent { act=android.intent.action.PICK
			//      dat=content://media/external/video/media
			//      cmp=com.android.music/.VideoBrowserActivity } from ProcessRecord
			// Try another way to start the intent
			intent = new Intent(Intent.ACTION_PICK, null);
			intent.setType("video/*");
			try {
				startActivityForResult(intent, IntentRequestCode.PICK_UP_VIDEO);
			} catch (Exception ex) {
				DialogUtil.showExceptionAlertDialog(UploadVideoActivity.this, getString(R.string.cannotPickUpVideo), getString(R.string.notSupportedOnDevice));
			}
		}
	}

	private void onVideoReady() {
		MediaController mediaController = new MediaController(this);
		videoViewPreview.setVisibility(View.VISIBLE);
		videoViewPreview.setVideoPath(videoFileName);
		videoViewPreview.setMediaController(mediaController);
		videoViewPreview.requestFocus();
		videoViewPreview.start();
		videoViewPreview.pause();

		textViewFilePath.setText(videoFileName);

		editTextVideoTitle.setText("");
		editTextVideoDescription.setText("");
		textViewVideoUrl.setText(getString(R.string.noUrlYet));

		Toast.makeText(this, R.string.pressVideoToPreview, Toast.LENGTH_LONG).show();
	}

	private void resetProgress() {
		progressBarUploadVideo.setProgress(0);
		textViewProgress.setText(" 00%");
	}

	public TextView getTextViewProgress() {
		return textViewProgress;
	}

	public ProgressBar getProgressBarUploadVideo() {
		return progressBarUploadVideo;
	}

	public TextView getTextViewVideoUrl() {
		return textViewVideoUrl;
	}

	public String getVideoTitle() {
		return editTextVideoTitle.getText().toString();
	}

	public String getVideoDescription() {
		return editTextVideoDescription.getText().toString();
	}

	public String getVideoFileName() {
		return videoFileName;
	}

	public void clearVideoFileName() {
		videoFileName = null;
	}

	/**
	 * Facebook app requires a key hash of the Android application. This method prints development key hash.
	 * To get release key hash, refer to http://stackoverflow.com/questions/5306009/facebook-android-generate-key-hash.
	 */
	private void printDevelopmentKeyHash() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash: ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		}
		catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
	}

	@Override
	public void loginSuccess(Facebook facebook) {
		String facebookUserAccessToken = facebook.getAccessToken();
		SharedPreferenceUtil.savePreferenceItemByName(this, SharedPreferenceUtil.FacebookUserAccessToken, facebookUserAccessToken);

		String facebookUserID = SharedPreferenceUtil.getPreferenceItemByName(this, SharedPreferenceUtil.FacebookUserID);
		// Only load Facebook user info if it has not been loaded yet
		if(facebookUserID.isEmpty()) {
			LoadFacebookUserInfoTask task = new LoadFacebookUserInfoTask(this, new LoadFacebookUserNameHandler(this));
			task.execute(facebookUserAccessToken, "me");
		}
	}

	@Override
	public void logoutSuccess() {
		Toast.makeText(this, "Successfully logged out from Facebook", Toast.LENGTH_LONG).show();
	}

	@Override
	public void loginFail() {
		Toast.makeText(this, "Facebook login failed!", Toast.LENGTH_LONG).show();
	}
}

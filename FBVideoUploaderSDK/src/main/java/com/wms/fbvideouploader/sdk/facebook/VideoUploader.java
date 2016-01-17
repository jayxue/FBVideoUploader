/**
 * This class uses HttpClient library to upload videos to Facebook. It uses httpmime-4.2.3.jar.
 */
package com.wms.fbvideouploader.sdk.facebook;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.wms.fbvideouploader.sdk.entity.CustomMultiPartEntity;
import com.wms.fbvideouploader.sdk.handler.HandlerMessage;
import com.wms.fbvideouploader.sdk.type.HttpClientWorkingStatus;
import com.wms.fbvideouploader.sdk.util.MessageUtil;

import android.os.Handler;
import android.widget.ProgressBar;

@SuppressWarnings("deprecation")
public class VideoUploader {

	private static long totalSize = 0;
	private static HttpPost httppost = null;
	public static HttpClient httpclient = null;
	public static HttpClientWorkingStatus status = HttpClientWorkingStatus.SLEEPING;

	public static String postVideo(String facebookAccessToken, String facebookUserID, String filePath, String title, String description,
	                               final ProgressBar progressBar, final Handler handler) throws IOException {
	    String result = "";
	    status = HttpClientWorkingStatus.UPLOADING;

	    httpclient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
	    httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
	    httppost = new HttpPost("https://graph-video.facebook.com/" + facebookUserID + "/videos");
	    
	    File file = new File(filePath);

	    CustomMultiPartEntity mpEntity = new CustomMultiPartEntity(new CustomMultiPartEntity.ProgressListener() {
            @Override  
            public void transferred(long num)  
            {  
                if(totalSize > 0) {
                	progressBar.setProgress((int) ((num / (float) totalSize) * 100));
	                MessageUtil.sendHandlerMessage(handler, HandlerMessage.FACEBOOK_VIDEO_UPLOAD_PROGRESS_UPDATE);
                }
                
            }  
	    });
	    mpEntity.addPart("access_token", new StringBody(facebookAccessToken));
	    mpEntity.addPart("title", new StringBody(title));
	    mpEntity.addPart("description", new StringBody(description));
	    mpEntity.addPart("privacy", new StringBody("{'value':'EVERYONE'}"));
	    ContentBody cbFile = new FileBody(file, "video/mp4");
	    mpEntity.addPart("video", cbFile);
	    totalSize = mpEntity.getContentLength();
	    httppost.setEntity(mpEntity);
	    HttpResponse response = httpclient.execute(httppost, httpContext);
	    HttpEntity resEntity = response.getEntity();
	    if (resEntity != null) {
	    	result = EntityUtils.toString(resEntity);
	    }
	    if (resEntity != null) {
	    	resEntity.consumeContent();
	    }
	    
	    httpclient.getConnectionManager().shutdown();
	    httppost = null;
	    httpclient = null;
	    status = HttpClientWorkingStatus.UPLOAD_COMPLETED;

	    return result;
	}

}

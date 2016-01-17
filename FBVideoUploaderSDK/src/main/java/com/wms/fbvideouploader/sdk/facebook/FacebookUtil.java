
package com.wms.fbvideouploader.sdk.facebook;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;

public class FacebookUtil {

	/**
	 * Uses RestFB (restfb-1.6.9.jar) to access Facebook user. See http://restfb.com/
	 */
	public static User getUser(String facebookAccessToken, String userID) {
		FacebookClient facebookClient = new DefaultFacebookClient(facebookAccessToken);		
		User user = facebookClient.fetchObject(userID, User.class);
		return user;
	}

}

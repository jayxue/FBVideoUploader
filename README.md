# FBVideoUploader - Android library for eazily uploading videos to Facebook

An Android library that helps deverlopers easily create apps with the functionality of uploading videos to Facebook.

![Demo Screenshot 1](https://github.com/jayxue/FBVideoUploader/blob/master/FBVideoUploaderSDK/src/main/res/raw/screenshot_1.png)
![Demo Screenshot 2](https://github.com/jayxue/FBVideoUploader/blob/master/FBVideoUploaderSDK/src/main/res/raw/screenshot_2.png)
![Demo Screenshot 3](https://github.com/jayxue/FBVideoUploader/blob/master/FBVideoUploaderSDK/src/main/res/raw/screenshot_3.png)
![Demo Screenshot 4](https://github.com/jayxue/FBVideoUploader/blob/master/FBVideoUploaderSDK/src/main/res/raw/screenshot_4.png)

Details
-------
This Android library facilitates developers to create Android applications with the functionality of uploading videos to Facebook.

The major features include:
* Shoot new videos or pick videos from gallery for uploading.
* Enter title and description for a video to upload.
* Ask user to confirm before starting uploading.
* Ask user to login to Facebook.
* Show progress of uploading.
* Display video URL after uploading is successfully completed.

Usage
-----

In order to utilize this library, you just need to do some configurations without writing any code.
* Import the FBVideoUploaderSDK module into your Android Studio project. Add dependency to the module to your app project.
* In your app's ```AndroidManifest.xml```, make sure that you have the following permissions:
  * ```android.permission.INTERNET```
  * ```android.permission.WRITE_EXTERNAL_STORAGE```
  * ```android.permission.CAMERA```
* In your app's ```AndroidManifest.xml```, include the activity:
  * ```com.wms.fbvideouploader.sdk.activity.UploadVideoActivity```
* In your app's ```res/values/strings.xml```,
  * Set ```app_name``` (name of your application).
* Replace your app's ic_launcher icons.
* In order to upload videos to Facebook, you need to register as a Facebook developer and create an app on Facebook Apps Console (https://developers.facebook.com/apps/).
  Follow Facebook's instructions to configure the app. Note that you need to provide your Android app's package name, class name and key hashes. You'll need to ask for the "publish_actions" permission (of course you need to ask Facebook to review and approve your app).
  To get key hash of your Android app, refer to https://developers.facebook.com/docs/android/getting-started#create_hash.
  Finally, add the Facebook app ID to your app's ```values/strings.xml```.
 
Of course you can modify any components of the library or add new components to customize your Android app's functionality.

Acknowledgement
---------------

This library utilizes the following libraries:
* Easy Facebook Android SDK: http://www.easyfacebookandroidsdk.com/
* RestFB by Mark Allen: http://restfb.com/
* Apache HttpClient Mime: https://hc.apache.org/httpcomponents-client-ga/httpmime/project-summary.html

Developer
---------
* Jay Xue <yxue24@gmail.com>, Waterloo Mobile Studio

License
-------

    Copyright 2015 Waterloo Mobile Studio

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
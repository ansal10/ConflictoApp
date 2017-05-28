package in.co.conflicto.conflictoapp.utilities;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import in.co.conflicto.conflictoapp.models.User;

/**
 * Created by shubhamagrawal on 04/04/17.
 */

public class MyApplication extends Application {

    private static MyApplication instance;
    private static final String LOG_TAG = MyApplication.class.getSimpleName();
    private static DiskCaching diskCachingInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        diskCachingInstance = DiskCaching.getInstance();

    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    public void saveCurrentUser(User user){
        SharedPreferences sharedpreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.UUID_KEY, user.uuid);
        editor.putString("dplink", user.dpLink);
        editor.putString("fcm_token", user.fcmToken);
        editor.putString("firebase_id", user.firebaseId);
        editor.putString(Constants.NAME_KEY, user.name);
    }

    public User retrieveCurrentUser(){
        SharedPreferences sharedpreferences = getSharedPreferences(Constants.SHARED_PREFERENCE_CURRENT_USER, Context.MODE_PRIVATE);
        String uuid = sharedpreferences.getString(Constants.UUID_KEY, null);
        String dplink = sharedpreferences.getString("dplink", null);
        String fcmToken = sharedpreferences.getString("fcm_token", null);
        String firebaseId = sharedpreferences.getString("firebase_id", null);
        String name = sharedpreferences.getString(Constants.NAME_KEY, null);
        return new User(name, dplink, uuid, fcmToken, firebaseId);

    }

    public static DiskCaching getDiskCachingInstance(){
        return diskCachingInstance;
    }
}

package com.proxima.Wubble;

import android.app.Application;
import android.content.Context;

/**
 * Created by Epokhe on 22.02.2015.
 */
public class AppGlobal extends Application {

    private static AppGlobal sAppInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        sAppInstance = this;
    }

    public static AppGlobal getInstance() {
        return sAppInstance;
    }

    public static Context getAppContext() {
        return sAppInstance.getApplicationContext();
    }


}

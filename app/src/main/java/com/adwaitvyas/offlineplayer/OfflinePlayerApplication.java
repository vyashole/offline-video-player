package com.adwaitvyas.offlineplayer;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by vyashole on 24/01/17
 * OfflinePlayer
 */

public class OfflinePlayerApplication extends Application {
    private HttpProxyCacheServer cacheServer;
    static OfflinePlayerApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static HttpProxyCacheServer getCacheServer(Context context) {
        if(application.cacheServer == null) application.cacheServer = application.buildHttpCacheServer();
        return application.cacheServer;
    }

    private HttpProxyCacheServer buildHttpCacheServer() {
        return new HttpProxyCacheServer.Builder(this)
                    .cacheDirectory(getCacheDir())
                    .build();
    }
}

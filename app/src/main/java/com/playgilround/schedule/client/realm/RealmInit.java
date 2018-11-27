package com.playgilround.schedule.client.realm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.kakao.auth.KakaoSDK;
import com.playgilround.schedule.client.kakao.KakaoSDKAdapter;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.io.FileNotFoundException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * 18-08-02
 * Realm 최초 설정 extends Application
 * https://github.com/uPhyca/stetho-realm/issues/62
 */
public class RealmInit extends Application {

    static final String TAG = RealmInit.class.getSimpleName();

    private static volatile RealmInit obj = null;
    private static volatile Activity currentActivity = null;
    @Override
    public void onCreate() {
        super.onCreate();

        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());
        Realm.init(this);
//        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
//
//        Realm.setDefaultConfiguration(config);

        RealmInspectorModulesProvider provider = RealmInspectorModulesProvider.builder(this)
                .build();

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
//                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .enableWebKitInspector(provider)
                        .build());
     /*   Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());*/
   /*     Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build()).build());*/
    }

    public static RealmInit getGlobalApplicationContext() {
        return obj;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    //Activity가 올라 올때마다 Activity에 onCreate에서 호출
    public static void setCurrentActivity(Activity currentActivity) {
        RealmInit.currentActivity = currentActivity;
    }
}




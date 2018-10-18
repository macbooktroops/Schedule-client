package com.playgilround.common.realm;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;


import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * 18-08-02
 * Realm 최초 설정 extends Application
 */
public class RealmInit extends Application {

    static final String TAG = RealmInit.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build()).build());


    }
}

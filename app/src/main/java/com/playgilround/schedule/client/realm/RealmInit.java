package com.playgilround.schedule.client.realm;

import android.app.Application;

import com.facebook.stetho.Stetho;
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

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        Realm.setDefaultConfiguration(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
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
}

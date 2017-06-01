package be.flashapps.beeroclock;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.AppCompatDelegate;

import com.orhanobut.logger.Logger;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.otto.Bus;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by dietervaesen on 9/11/16.
 */

public class App extends Application {

    private static App instance;
    private static Context mContext;
    private static Bus mBus;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    /*private static final String TWITTER_KEY = "zz8pOkrwAppQmAPhu720XJRhF";
    private static final String TWITTER_SECRET = "Bf3AvptHL30JWENqSljtLZgWfIkhk02jYgPG0GrMRw3Ex6nmfY";*/

    @Override
    public void onCreate() {
        super.onCreate();
        //use vectors as drawable everywhere
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Logger.init("App");// default 0
        //initialize here otherwise it doesn't know  all android time zones
        JodaTimeAndroid.init(this);


       /* LeakCanary.install(this);*/
        /*if (!BuildConfig.DEBUG) {
        CrashlyticsCore core = new CrashlyticsCore.Builder().build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build(), new Crashlytics());
        }*/
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);



    }


    public static Bus getBus(){
        if (mBus == null){
            mBus = new Bus();
        }
        return mBus;
    }


    @Override
    protected void attachBaseContext(Context base) {
        Locale defaultLocale = Locale.getDefault();
        super.attachBaseContext(base);
        mContext = this;
    }


    public static Context getContext() {
        return mContext;
    }

    public static App getInstance() {
        return instance;
    }
}

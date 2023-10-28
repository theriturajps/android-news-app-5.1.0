package com.app.androidnewsapp.activity;

import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroid.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;
import static com.solodroid.ads.sdk.util.Constant.WORTISE;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

import com.app.androidnewsapp.R;
import com.app.androidnewsapp.config.AppConfig;
import com.app.androidnewsapp.database.prefs.AdsPref;
import com.app.androidnewsapp.util.Constant;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.solodroid.ads.sdk.format.AppOpenAdAppLovin;
import com.solodroid.ads.sdk.format.AppOpenAdManager;
import com.solodroid.ads.sdk.format.AppOpenAdMob;
import com.solodroid.ads.sdk.format.AppOpenAdWortise;
import com.solodroid.ads.sdk.util.OnShowAdCompleteListener;
import com.solodroid.push.sdk.provider.OneSignalPush;

public class MyApplication extends Application {

    public static final String TAG = "MyApplication";
    public String prefName = "news_pref";
    public static MyApplication mInstance;
    FirebaseAnalytics mFirebaseAnalytics;
    private AppOpenAdMob appOpenAdMob;
    private AppOpenAdManager appOpenAdManager;
    private AppOpenAdAppLovin appOpenAdAppLovin;
    private AppOpenAdWortise appOpenAdWortise;
    Activity currentActivity;
    AdsPref adsPref;

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        adsPref = new AdsPref(this);
        if (!AppConfig.FORCE_TO_SHOW_APP_OPEN_AD_ON_START) {
            registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
            ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleObserver);
            appOpenAdMob = new AppOpenAdMob();
            appOpenAdManager = new AppOpenAdManager();
            appOpenAdAppLovin = new AppOpenAdAppLovin();
            appOpenAdWortise = new AppOpenAdWortise();
        }

        initNotification();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void initNotification() {
        new OneSignalPush.Builder(this)
                .setOneSignalAppId(getResources().getString(R.string.onesignal_app_id))
                .build(() -> {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(OneSignalPush.EXTRA_ID, OneSignalPush.Data.id);
                    intent.putExtra(OneSignalPush.EXTRA_TITLE, OneSignalPush.Data.title);
                    intent.putExtra(OneSignalPush.EXTRA_MESSAGE, OneSignalPush.Data.message);
                    intent.putExtra(OneSignalPush.EXTRA_IMAGE, OneSignalPush.Data.bigImage);
                    intent.putExtra(OneSignalPush.EXTRA_LAUNCH_URL, OneSignalPush.Data.launchUrl);
                    intent.putExtra(OneSignalPush.EXTRA_UNIQUE_ID, OneSignalPush.AdditionalData.uniqueId);
                    intent.putExtra(OneSignalPush.EXTRA_POST_ID, OneSignalPush.AdditionalData.postId);
                    intent.putExtra(OneSignalPush.EXTRA_LINK, OneSignalPush.AdditionalData.link);
                    startActivity(intent);
                });
        FirebaseMessaging.getInstance().subscribeToTopic(getResources().getString(R.string.fcm_notification_topic));
    }

    LifecycleObserver lifecycleObserver = new DefaultLifecycleObserver() {
        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            DefaultLifecycleObserver.super.onStart(owner);
            if (Constant.isAppOpen) {
                if (adsPref.getIsAppOpenAdOnResume()) {
                    if (adsPref.getAdStatus().equals(AD_STATUS_ON)) {
                        switch (adsPref.getMainAds()) {
                            case ADMOB:
                                if (!adsPref.getAdMobAppOpenAdId().equals("0")) {
                                    if (!currentActivity.getIntent().hasExtra("unique_id")) {
                                        appOpenAdMob.showAdIfAvailable(currentActivity, adsPref.getAdMobAppOpenAdId());
                                    }
                                }
                                break;
                            case GOOGLE_AD_MANAGER:
                                if (!adsPref.getAdManagerAppOpenAdId().equals("0")) {
                                    if (!currentActivity.getIntent().hasExtra("unique_id")) {
                                        appOpenAdManager.showAdIfAvailable(currentActivity, adsPref.getAdManagerAppOpenAdId());
                                    }
                                }
                                break;
                            case APPLOVIN:
                            case APPLOVIN_MAX:
                                if (!adsPref.getAppLovinAppOpenAdUnitId().equals("0")) {
                                    if (!currentActivity.getIntent().hasExtra("unique_id")) {
                                        appOpenAdAppLovin.showAdIfAvailable(currentActivity, adsPref.getAppLovinAppOpenAdUnitId());
                                    }
                                }
                                break;

                            case WORTISE:
                                if (!adsPref.getWortiseAppOpenId().equals("0")) {
                                    if (!currentActivity.getIntent().hasExtra("unique_id")) {
                                        appOpenAdWortise.showAdIfAvailable(currentActivity, adsPref.getWortiseAppOpenId());
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        }
    };

    ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            if (adsPref.getIsAppOpenAdOnStart()) {
                if (adsPref.getAdStatus().equals(AD_STATUS_ON)) {
                    switch (adsPref.getMainAds()) {
                        case ADMOB:
                            if (!adsPref.getAdMobAppOpenAdId().equals("0")) {
                                if (!appOpenAdMob.isShowingAd) {
                                    currentActivity = activity;
                                }
                            }
                            break;
                        case GOOGLE_AD_MANAGER:
                            if (!adsPref.getAdManagerAppOpenAdId().equals("0")) {
                                if (!appOpenAdManager.isShowingAd) {
                                    currentActivity = activity;
                                }
                            }
                            break;
                        case APPLOVIN:
                        case APPLOVIN_MAX:
                            if (!adsPref.getAppLovinAppOpenAdUnitId().equals("0")) {
                                if (!appOpenAdAppLovin.isShowingAd) {
                                    currentActivity = activity;
                                }
                            }
                            break;
                        case WORTISE:
                            if (!adsPref.getWortiseAppOpenId().equals("0")) {
                                if (!appOpenAdWortise.isShowingAd) {
                                    currentActivity = activity;
                                }
                            }
                            break;
                    }
                }
            }
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }
    };

    public void showAdIfAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        if (adsPref.getIsAppOpenAdOnStart()) {
            if (adsPref.getAdStatus().equals(AD_STATUS_ON)) {
                switch (adsPref.getMainAds()) {
                    case ADMOB:
                        if (!adsPref.getAdMobAppOpenAdId().equals("0")) {
                            appOpenAdMob.showAdIfAvailable(activity, adsPref.getAdMobAppOpenAdId(), onShowAdCompleteListener);
                            Constant.isAppOpen = true;
                        }
                        break;
                    case GOOGLE_AD_MANAGER:
                        if (!adsPref.getAdManagerAppOpenAdId().equals("0")) {
                            appOpenAdManager.showAdIfAvailable(activity, adsPref.getAdManagerAppOpenAdId(), onShowAdCompleteListener);
                            Constant.isAppOpen = true;
                        }
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                        if (!adsPref.getAppLovinAppOpenAdUnitId().equals("0")) {
                            appOpenAdAppLovin.showAdIfAvailable(activity, adsPref.getAppLovinAppOpenAdUnitId(), onShowAdCompleteListener);
                            Constant.isAppOpen = true;
                        }
                        break;
                    case WORTISE:
                        if (!adsPref.getWortiseAppOpenId().equals("0")) {
                            appOpenAdWortise.showAdIfAvailable(activity, adsPref.getWortiseAppOpenId(), onShowAdCompleteListener);
                            Constant.isAppOpen = true;
                        }
                        break;
                }
            }
        }
    }

}

package io.github.gjyaiya.realmbrowser;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.RealmObject;

public final class RealmBrowser {

    public static final int NOTIFICATION_ID = 1000;

    private static final RealmBrowser sInstance = new RealmBrowser();
    private List<Class<? extends RealmObject>> mRealmModelList;
    private Object mModules;
    private int mVersion;

    private RealmBrowser() {
        mRealmModelList = new ArrayList<>();
    }

    public List<Class<? extends RealmObject>> getRealmModelList() {
        return mRealmModelList;
    }

    @SafeVarargs
    public final RealmBrowser addRealmModel(Class<? extends RealmObject>... arr) {
        mRealmModelList.addAll(Arrays.asList(arr));
        return this;
    }

    @SafeVarargs
    public final RealmBrowser setRealModel(Class<? extends RealmObject>... arr){
        mRealmModelList.clear();
        return addRealmModel(arr);
    }

    public Object getModules() {
        return mModules;
    }

    public void setModules(Object mModules) {
        this.mModules = mModules;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int mVersion) {
        this.mVersion = mVersion;
    }

    public static RealmBrowser getInstance() {
        return sInstance;
    }

    public static void startRealmFilesActivity(@NonNull Activity activity) {
        RealmFilesActivity.start(activity);
    }

    public static void startRealmModelsActivity(@NonNull Activity activity, @NonNull String realmFileName) {
        RealmModelsActivity.start(activity, realmFileName);
    }

}

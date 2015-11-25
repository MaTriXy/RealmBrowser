package io.github.gjyaiya.realmbrowser;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.RealmObject;

public final class RealmBrowser {

    private static RealmBrowser sInstance;

    public static RealmBrowser getInstance() {
        if(sInstance == null) sInstance = new RealmBrowser();
        return sInstance;
    }

    private List<Class<? extends RealmObject>> mRealmModelList;
    private Object mSchema;
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

    public Object getSchema() {
        return mSchema;
    }

    public void setSchema(Object mSchema) {
        this.mSchema = mSchema;
    }

    public int getVersion() {
        return mVersion;
    }

    public void setVersion(int mVersion) {
        this.mVersion = mVersion;
    }

    public static void startRealmFilesActivity(Activity activity) {
        //RealmFilesActivity.start(activity);
        RealmActivity.start(activity);
    }

    public static void startRealmModelsActivity(Activity activity,String realmFileName) {
        RealmModelsActivity.start(activity, realmFileName);
    }
}

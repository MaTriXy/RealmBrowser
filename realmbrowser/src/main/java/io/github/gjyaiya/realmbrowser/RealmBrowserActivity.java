package io.github.gjyaiya.realmbrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.gjyaiya.realmbrowser.utils.MagicUtils;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;


public class RealmBrowserActivity extends Activity implements RealmAdapter.Listener {

    private static final String EXTRAS_REALM_FILE_NAME = "EXTRAS_REALM_FILE_NAME";
    private static final String EXTRAS_REALM_MODEL_INDEX = "REALM_MODEL_INDEX";

    private Realm mRealm;
    private Class<? extends RealmObject> mRealmObjectClass;
    private RealmAdapter mAdapter;
    private TextView mTxtIndex;
    private TextView mTxtColumn1;
    private TextView mTxtColumn2;
    private TextView mTxtColumn3;
    private List<Field> mTmpSelectedFieldList;
    private List<Field> mSelectedFieldList;
    private List<Field> mFieldsList;

    public static void start(Activity activity, int realmModelIndex, String realmFileName) {
        Intent intent = new Intent(activity, RealmBrowserActivity.class);
        intent.putExtra(EXTRAS_REALM_MODEL_INDEX, realmModelIndex);
        intent.putExtra(EXTRAS_REALM_FILE_NAME, realmFileName);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, String realmFileName) {
        Intent intent = new Intent(activity, RealmBrowserActivity.class);
        intent.putExtra(EXTRAS_REALM_FILE_NAME, realmFileName);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_realm_browser);

        findViewById(R.id.index).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });

        String realmFileName = getIntent().getStringExtra(EXTRAS_REALM_FILE_NAME);
        int mVersion = RealmBrowser.getInstance().getVersion();

        RealmConfiguration.Builder mBuilder = new RealmConfiguration.Builder(this).name(realmFileName);
        if(RealmBrowser.getInstance().getSchema() != null)
            mBuilder.setModules(RealmBrowser.getInstance().getSchema());
        if(mVersion != 0) mBuilder.schemaVersion(mVersion);

        RealmConfiguration config = mBuilder.build();

        mRealm = Realm.getInstance(config);

        AbstractList<? extends RealmObject> realmObjects;

        if(getIntent().getExtras().containsKey(EXTRAS_REALM_MODEL_INDEX)) {
            int index = getIntent().getIntExtra(EXTRAS_REALM_MODEL_INDEX, 0);
            mRealmObjectClass = RealmBrowser.getInstance().getRealmModelList().get(index);
            realmObjects = mRealm.allObjects(mRealmObjectClass);
        } else {
            RealmObject object = RealmHolder.getInstance().getObject();
            Field field = RealmHolder.getInstance().getField();
            String methodName = MagicUtils.createMethodName(field);
            realmObjects = invokeMethod(object, methodName);
            if(MagicUtils.isParameterizedField(field)) {
                ParameterizedType pType = (ParameterizedType) field.getGenericType();
                Class<?> pTypeClass = (Class<?>) pType.getActualTypeArguments()[0];
                mRealmObjectClass = (Class<? extends RealmObject>) pTypeClass;
            }
        }

        mSelectedFieldList = new ArrayList<>();
        mTmpSelectedFieldList = new ArrayList<>();
        mFieldsList = new ArrayList<>();
        mFieldsList.addAll(Arrays.asList(mRealmObjectClass.getDeclaredFields()));

        mAdapter = new RealmAdapter(this, realmObjects, mSelectedFieldList, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        mTxtIndex = (TextView) findViewById(R.id.txtIndex);
        mTxtColumn1 = (TextView) findViewById(R.id.txtColumn1);
        mTxtColumn2 = (TextView) findViewById(R.id.txtColumn2);
        mTxtColumn3 = (TextView) findViewById(R.id.txtColumn3);

        selectDefaultFields();
        updateColumnTitle(mSelectedFieldList);
    }

    @Override
    protected void onResume() {
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mRealm != null) {
            mRealm.close();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_columns) {
            showColumnsDialog();
        } if (id == R.id.action_settings) {
            SettingsActivity.start(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRowItemClicked(RealmObject realmObject, Field field) {
        RealmHolder.getInstance().setObject(realmObject);
        RealmHolder.getInstance().setField(field);
        String realmFileName = getIntent().getStringExtra(EXTRAS_REALM_FILE_NAME);
        RealmBrowserActivity.start(this, realmFileName);
    }

    public static RealmList<? extends RealmObject> invokeMethod(Object realmObject, String methodName) {
        RealmList<? extends RealmObject> result = null;
        try {
            Method method = realmObject.getClass().getMethod(methodName);
            result = (RealmList<? extends RealmObject>) method.invoke(realmObject);
        } catch (Exception e) {
            //L.e(e.toString());
        }
        return result;

    }

    private void selectDefaultFields() {
        mSelectedFieldList.clear();
        for (Field field : mFieldsList) {
            if (mSelectedFieldList.size() < 3) {
                mSelectedFieldList.add(field);
            }
        }
    }

    private void updateColumnTitle(List<Field> columnsList) {
        mTxtIndex.setText("#");

        LinearLayout.LayoutParams layoutParams2 = createLayoutParams();
        LinearLayout.LayoutParams layoutParams3 = createLayoutParams();

        if (columnsList.size() > 0) {
            mTxtColumn1.setText(columnsList.get(0).getName());

            if (columnsList.size() > 1) {
                mTxtColumn2.setText(columnsList.get(1).getName());
                layoutParams2.weight = 1;

                if (columnsList.size() > 2) {
                    mTxtColumn3.setText(columnsList.get(2).getName());
                    layoutParams3.weight = 1;
                } else {
                    layoutParams3.weight = 0;
                }
            } else {
                layoutParams2.weight = 0;
            }
        }

        mTxtColumn2.setLayoutParams(layoutParams2);
        mTxtColumn3.setLayoutParams(layoutParams3);
    }

    private LinearLayout.LayoutParams createLayoutParams() {
        return new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void showColumnsDialog() {
        final String[] items = new String[mFieldsList.size()];
        for (int i = 0; i < items.length; i++) {
            Field field = mFieldsList.get(i);
            items[i] = field.getName();
        }

        boolean[] checkedItems = new boolean[mFieldsList.size()];
        for (int i = 0; i < checkedItems.length; i++) {
            checkedItems[i] = mSelectedFieldList.contains(mFieldsList.get(i));
        }

        mTmpSelectedFieldList.clear();
        mTmpSelectedFieldList.addAll(mSelectedFieldList);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Columns to display");
        builder.setMultiChoiceItems(items, checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        Field field = mFieldsList.get(indexSelected);
                        if (isChecked) {
                            mTmpSelectedFieldList.add(field);
                        } else if (mTmpSelectedFieldList.contains(field)) {
                            mTmpSelectedFieldList.remove(field);
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mTmpSelectedFieldList.isEmpty()) {
                            selectDefaultFields();
                        } else {
                            mSelectedFieldList.clear();
                            mSelectedFieldList.addAll(mTmpSelectedFieldList);
                        }
                        updateColumnTitle(mSelectedFieldList);
                        mAdapter.setFieldList(mSelectedFieldList);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

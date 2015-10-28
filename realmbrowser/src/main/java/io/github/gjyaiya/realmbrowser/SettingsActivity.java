package io.github.gjyaiya.realmbrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import io.github.gjyaiya.realmbrowser.model.RealmPreferences;

public class SettingsActivity extends Activity {

    private RealmPreferences mRealmPreferences;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_realm_settings);

        mRealmPreferences = new RealmPreferences(getApplicationContext());

        initView();
    }

    private void initView() {
        CheckBox cbWrapText = (CheckBox) findViewById(R.id.cbWrapText);
        cbWrapText.setChecked(mRealmPreferences.shouldWrapText());
        cbWrapText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRealmPreferences.setShouldWrapText(isChecked);
            }
        });
    }
}

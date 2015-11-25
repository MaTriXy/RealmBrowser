package io.github.gjyaiya.realmbrowser;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class RealmActivity extends Activity{

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, RealmActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm_browser);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        setFragmentAnim(ft);
        ft.replace(R.id.main_container, new RealmFileFragment(), null);
        ft.commit();
    }

    public static void gotoFragment(Activity activity,Fragment fragment){
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        setFragmentAnim(ft);
        ft.add(R.id.main_container,fragment,null);
        ft.addToBackStack(null);
        ft.commit();
    }

    public static void setFragmentAnim(FragmentTransaction ft){
        ft.setCustomAnimations(
                R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_right_enter,
                R.anim.fragment_slide_right_exit);
    }
}

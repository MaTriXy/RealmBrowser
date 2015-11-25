package io.github.gjyaiya.realmbrowser;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

public class RealmModelFragment extends Fragment{
    private List<String> mIgnoreExtensionList;
    private ArrayAdapter<String> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView mListView = new ListView(getActivity());
        mListView.setClickable(true);
        mListView.setBackgroundColor(getActivity().getResources().getColor(R.color.rb_white));
        mListView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return mListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        List<String> modelList = new ArrayList<>();
        for (Class<? extends RealmObject> file : RealmBrowser.getInstance().getRealmModelList()) {
            modelList.add(file.getSimpleName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                        android.R.layout.simple_list_item_1, modelList);
        ListView listView = (ListView)view;
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(position);
            }
        });
    }

    private void onItemClicked(int position) {
        //String realmFileName = getIntent().getStringExtra(EXTRAS_REALM_FILE_NAME);
        //RealmBrowserActivity.start(this, position, realmFileName);
    }
}

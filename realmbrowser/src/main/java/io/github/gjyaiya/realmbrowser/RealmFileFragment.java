package io.github.gjyaiya.realmbrowser;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class RealmFileFragment extends Fragment{
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
        mIgnoreExtensionList = new ArrayList<>();
        mIgnoreExtensionList.add(".log");
        mIgnoreExtensionList.add(".lock");

        File dataDir = new File(getActivity().getApplicationInfo().dataDir, "files");
        File[] files = dataDir.listFiles();
        List<String> fileList = new ArrayList<>();
        for (File file : files) {
            String fileName = file.getName();
            if (isValid(fileName)) {
                fileList.add(fileName);
            }
        }

        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, fileList);
        ListView listView = (ListView)view;
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(position);
            }
        });
    }

    private boolean isValid(String fileName) {
        boolean isValid = true;
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            String extension = fileName.substring(index);
            isValid = !mIgnoreExtensionList.contains(extension);
        }
        return isValid;
    }

    private void onItemClicked(int position) {
        try {
            String realmFileName = mAdapter.getItem(position);
            RealmConfiguration config = new RealmConfiguration.Builder(getActivity())
                    .name(realmFileName)
                    .build();
            Realm realm = Realm.getInstance(config);
            realm.close();
            //RealmModelsActivity.start(getActivity(), realmFileName);
            RealmActivity.gotoFragment(getActivity(),new RealmModelFragment());
        } catch (RealmMigrationNeededException e) {
            Toast.makeText(getActivity(), "RealmMigrationNeededException", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Can't open realm instance", Toast.LENGTH_SHORT).show();
        }
    }
}

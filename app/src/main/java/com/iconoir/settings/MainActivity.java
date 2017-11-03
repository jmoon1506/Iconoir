package com.iconoir.settings;

import android.content.Intent;
import android.database.DataSetObserver;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.actionBarTitle);
        loadLauncherList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.advanced:
                startActivity(new Intent(this, AdvancedActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadLauncherList() {
        listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                getResources().getStringArray(R.array.iconoirPackages));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String  itemValue = (String) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Position :"+position+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();
            }
        });

//        List<LauncherInfo> launcherInfoList = new ArrayList<LauncherInfo>();
//        launcherListView = (ListView) findViewById(R.id.launcherList);
//        launcherListView.setAdapter(new LauncherListAdapter(this, launcherInfoList));
    }
}

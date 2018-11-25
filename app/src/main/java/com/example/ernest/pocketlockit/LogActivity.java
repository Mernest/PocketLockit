package com.example.ernest.pocketlockit;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class LogActivity extends AppCompatActivity {

    protected ListView logListView;

    List<LogItem> logList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        this.setTitle("Activity History");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        logListView = findViewById(R.id.logListView);
        loadListView();
    }

    @Override
    protected void onStart(){
        super.onStart();
        loadListView();
    }

    protected void loadListView() {
        final DatabaseHelper dbhelper = new DatabaseHelper(this);
        logList = dbhelper.getAllLogItems();
        final ArrayList<String> listLogItems = new ArrayList<>();

        for(int i=0; i< logList.size(); i++){ // Adds course to listView along with their average
            listLogItems.add(logList.get(i).getTime() + "\n"+ logList.get(i).getTag());
        }

        final ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listLogItems);
        logListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();

        switch(id){
            case R.id.DeleteItemsMenu:
                DatabaseHelper dbhelper = new DatabaseHelper(LogActivity.this);
                dbhelper.deleteLog();
                goToLockUnlockActivity();
                return true;
            case android.R.id.home:
                goToLockUnlockActivity();
                return true;

        }
        return true;
    }

    void goToLockUnlockActivity(){
        Intent intent = new Intent(LogActivity.this, LockUnlockActivity.class);
        startActivity(intent);
    }

}


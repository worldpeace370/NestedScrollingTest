package com.wxk.nestedscrollingtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private ListView mListView;
  private List<String> mStringList = new ArrayList<>();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initView();
    initData();
    ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mStringList);
    mListView.setAdapter(adapter);
    AppLog.i("ListView", "mListView.getParent() = " + mListView.getParent()); // CoordinatorLayout
  }

  private void initView() {
    mListView = (ListView) findViewById(R.id.listView);
  }

  private void initData() {
    for (int i = 0; i < 20; i++) {
      mStringList.add("这是第 : " + i + " 条数据!");
    }
  }
}

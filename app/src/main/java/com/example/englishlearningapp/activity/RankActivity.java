package com.example.englishlearningapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.adapters.GameHistoryAdapter;
import com.example.englishlearningapp.adapters.RankAdapter;
import com.example.englishlearningapp.models.Game;
import com.example.englishlearningapp.models.Player;
import com.example.englishlearningapp.models.Rank;
import com.example.englishlearningapp.utils.GlobalVariable;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RankActivity extends AppCompatActivity {
    private static final String TAG = "RankActivity";
    Toolbar rankToolbar;
    ListView rankListView;
    GlobalVariable globalVariable;
    ArrayList<Rank> rankList = new ArrayList<>();
    RankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        globalVariable = GlobalVariable.getInstance(this);
        MappingView();
        SetupToolbar();
        SetupListView();
        globalVariable.mSocket.on("pointList", onGetRank);
        globalVariable.mSocket.emit("getPointList");

    }

    private Emitter.Listener onGetRank = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "on get rank: " + args[0]);
                    JSONArray rankArr = (JSONArray) args[0];
                    int len = rankArr.length();
                    if(len > 0){
                        ArrayList<Rank> temp = new ArrayList<>();
                        for (int i = 0; i < len; i++) {
                            try {
                                JSONObject obj = rankArr.getJSONObject(i);
                                Rank rank = new Rank(obj.getString("name"), obj.getInt("point"));
                                temp.add(rank);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        rankList.clear();
                        rankList.addAll(temp);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };

    private void SetupListView() {
        adapter = new RankAdapter(this, rankList);
        rankListView.setAdapter(adapter);
    }

    private void SetupToolbar() {
        rankToolbar.setTitle("");
        setSupportActionBar(rankToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rankToolbar.getNavigationIcon().setColorFilter(getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        }
        rankToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void MappingView() {
        rankToolbar = findViewById(R.id.rank_toolbar);
        rankListView = findViewById(R.id.rank_lv);
    }

}
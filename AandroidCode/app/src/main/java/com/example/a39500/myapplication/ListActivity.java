package com.example.a39500.myapplication;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.a39500.myapplication.adapter.IndexAdapter;
import com.example.a39500.myapplication.entity.Idea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListActivity extends AppCompatActivity {
    int uid;
    String usm;
    ListView elv;
    ProgressDialog progressDialog;
    IndexAdapter indexAdapter;
    FloatingActionButton add_button;
    Button manageSwitch;
    Button allDele;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent lastIntent = getIntent();
        uid = lastIntent.getIntExtra("uid", 0);
        usm = lastIntent.getStringExtra("usm");
        elv = findViewById(R.id.elv);
        manageSwitch = findViewById(R.id.manage);
        allDele= findViewById(R.id.all_dele);
        add_button = findViewById(R.id.add);
        logout = findViewById(R.id.logout);
        generateList();
        manageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!indexAdapter.isManaging() && !indexAdapter.isLinking()) {
                    indexAdapter.setManaging();
                    manageSwitch.setText(R.string.cancel_hint);
                    allDele.setVisibility(View.VISIBLE);
                    allDele.setText( R.string.delete_hint );
                } else {
                    cancelClick();
                }
            }
        });
        allDele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indexAdapter.isManaging()) {
                    indexAdapter.deleteAll();
                } else if(indexAdapter.isLinking()){
                    indexAdapter.updateLink();
                }
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, IdeaActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                finish();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                intent.putExtra("usm", usm);
                startActivity(intent);
                finish();
                SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("name", null);
                editor.putInt("uid", 0);
                editor.commit();
            }
        });
    }

    private void generateList() {
        progressDialog = new ProgressDialog(ListActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = this.getString(R.string.user_api) + uid + "/ideas/";
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray indexIdea) {
                        List<Idea> ideaList = new ArrayList<>();
                        try {
                            for (int j = 0; j < indexIdea.length(); j++) {
                                JSONObject anIdea = indexIdea.getJSONObject(j);
                                int id = anIdea.getInt("id");
                                String index = anIdea.getString("ideaIndex");
                                String topic = anIdea.getString("topic");
                                String content = anIdea.getString("content");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000+0000", Locale.UK);
                                Date modDate = sdf.parse(anIdea.getString("modDate"));
                                int userId = anIdea.getInt("userId");
                                Idea myIdea = new Idea(id, index, topic, content, modDate, userId);
                                ideaList.add(myIdea);
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        indexAdapter = new IndexAdapter(ListActivity.this, R.layout.idea_item, ideaList);
                        elv.setAdapter(indexAdapter);
                        if (progressDialog.isShowing() && progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                Log.e("error", arg0.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void cancelClick(){
        indexAdapter.desetManaging();
        indexAdapter.desetlinking();
        indexAdapter.releaseAll();
        manageSwitch.setText(R.string.manage_hint);
        allDele.setVisibility(View.GONE);
        manageSwitch.setVisibility( View.VISIBLE );
    }

    public void turnCancel(){
        manageSwitch.setText(R.string.cancel_hint);
        allDele.setText( R.string.change_hint );
        allDele.setVisibility( View.VISIBLE );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        generateList();
    }
}

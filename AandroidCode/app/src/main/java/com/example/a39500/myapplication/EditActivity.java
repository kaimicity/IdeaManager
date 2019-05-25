package com.example.a39500.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.a39500.myapplication.adapter.EditAdapter;
import com.example.a39500.myapplication.adapter.IndexAdapter;
import com.example.a39500.myapplication.entity.Idea;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.invoke.MethodType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {
    int iid;
    int uid;
    String getTopic;
    String getContent;
    TextInputEditText topic;
    EditText content;
    Button back;
    Button confirm;
    ImageButton ibMail;
    ImageButton ibMoment;
    ListView lv;
    ProgressDialog progressDialog;
    IWXAPI wxApi;
    EditAdapter ea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        wxApi = WXAPIFactory.createWXAPI(this,getString(R.string.wx_appid));
        wxApi.registerApp(getString(R.string.wx_appid));
        Intent lastIntent = getIntent();
        getTopic = lastIntent.getStringExtra("topic");
        getContent = lastIntent.getStringExtra("content");
        uid = lastIntent.getIntExtra("uid",0);
        iid = lastIntent.getIntExtra("iid",0);
        topic = findViewById(R.id.topic_c);
        content = findViewById(R.id.content);
        back = findViewById(R.id.back);
        confirm = findViewById(R.id.confirm_button);
        ibMail = findViewById(R.id.mail_button);
        ibMoment = findViewById(R.id.moment_button);
        lv = findViewById(R.id.linked_ideas);
        progressDialog = new ProgressDialog(EditActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        generateList();
        topic.setText(getTopic);
        content.setText(getContent);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this, ListActivity.class);
                intent.putExtra("uid", uid);
//                startActivity(intent);
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tpc = topic.getText().toString();
                String ctt = content.getText().toString();
                AlertDialog.Builder builder  = new AlertDialog.Builder(EditActivity.this);
                builder.setTitle("Whoops!");
                if(tpc.length() == 0) {
                    builder.setMessage("Please input a topic!");
                    builder.show();
                } else if(ctt.length() == 0){
                    builder.setMessage("Please input your content!");
                    builder.show();
                } else {
                    progressDialog.show();
                    modifyIdea();
                }
            }
        });
        ibMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMail(topic.getText().toString(),content.getText().toString());
            }
        });

        ibMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMoment(topic.getText().toString(),content.getText().toString());
            }
        });
    }
    void modifyIdea(){
        String tpc = topic.getText().toString();
        String ctt = content.getText().toString();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject job = new JSONObject();
        try {
            job.put("topic", tpc);
            job.put("content", ctt);
            job.put("ideaIndex","ii");
            job.put("userId",uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = this.getString(R.string.idea_api) + "/modify/" + iid ;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, job,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        intent.putExtra("uid", uid);
                        if (progressDialog.isShowing() && progressDialog != null) {
                            progressDialog.dismiss();
                        }
//                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                Log.e("error", arg0.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void openMail(String t, String c){
        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"An idea about " + t);
        intent.putExtra(Intent.EXTRA_TEXT,"I have an idea: " + c);
        startActivity(Intent.createChooser(intent,"Send mail"));
    }

    public void shareMoment(String t, String c){
        WXTextObject textObj = new WXTextObject();
        textObj.text = "I have an idea about " + t + ": " + c;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = "I have an idea about " + t + ": " + c;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "idea";
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    public void generateList(){
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = this.getString(R.string.idea_api) + iid + "/link";
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
                        ea = new EditAdapter(EditActivity.this, R.layout.idea_item, ideaList);
                        lv.setAdapter(ea);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        generateList();
    }
}

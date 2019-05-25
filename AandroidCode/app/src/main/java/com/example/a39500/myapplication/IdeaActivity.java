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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

public class IdeaActivity extends AppCompatActivity {
    TextInputEditText topic;
    EditText content;
    Button back;
    Button confirm;
    ImageButton ibMail;
    ImageButton ibMoment;
    public int uid;
    ProgressDialog progressDialog;
    IWXAPI wxApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        wxApi = WXAPIFactory.createWXAPI(this,getString(R.string.wx_appid));
        wxApi.registerApp(getString(R.string.wx_appid));
        uid = getIntent().getIntExtra("uid",0);
        topic = findViewById(R.id.topic_c);
        content = findViewById(R.id.content);
        back = findViewById(R.id.back);
        confirm = findViewById(R.id.confirm_button);
        ibMail = findViewById((R.id.mail_button));
        ibMoment = findViewById(R.id.moment_button);
        progressDialog = new ProgressDialog(IdeaActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IdeaActivity.this, ListActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tpc = topic.getText().toString();
                String ctt = content.getText().toString();
                AlertDialog.Builder builder  = new AlertDialog.Builder(IdeaActivity.this);
                builder.setTitle("Whoops!");
                if(tpc.length() == 0) {
                    builder.setMessage("Please input a topic!");
                    builder.show();
                } else if(ctt.length() == 0){
                    builder.setMessage("Please input your content!");
                    builder.show();
                } else {
                    progressDialog.show();
                    addIdea();
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

    void addIdea(){
        String tpc = topic.getText().toString();
        String ctt = content.getText().toString();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject job = new JSONObject();
        try {
            job.put("topic", tpc);
            job.put("content", ctt);
            job.put("ideaIndex","ii");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = this.getString(R.string.idea_api) + "/add/" + uid ;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, job,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Intent intent = new Intent(IdeaActivity.this, MainActivity.class);
                        intent.putExtra("uid", uid);
                        if (progressDialog.isShowing() && progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        startActivity(intent);
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

}

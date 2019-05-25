package com.example.a39500.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a39500.myapplication.entity.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class SignupActivity extends AppCompatActivity {
    TextInputEditText username;
    TextInputEditText password;
    TextInputEditText conPwd;
    TextView usernameInfo;
    TextView passwordInfo;
    TextView conPwdInfo;
    Button signup;
    Button back;
    boolean allPass ;
    String futureRes ;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        conPwd = findViewById(R.id.con_password);
        usernameInfo = findViewById(R.id.username_info);
        passwordInfo = findViewById(R.id.password_info);
        conPwdInfo = findViewById(R.id.con_pwd_info);
        signup = findViewById(R.id.signup_con);
        back = findViewById(R.id.back);
        final Handler showInfoHandler = new Handler();
        final String userApi = this.getString(R.string.user_api) ;
        allPass = true ;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
        final RequestQueue requestQueueMain = Volley.newRequestQueue(this);

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(SignupActivity.this);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Loading");
                progressDialog.show();
                allPass = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RequestFuture future = RequestFuture.newFuture();
                        String usm = username.getText().toString();
                        String pwd = password.getText().toString();
                        String pwd2 = conPwd.getText().toString();
                        if(usm.length() == 0){
                            allPass = false;
                            showInfoHandler.post(showInfo(0,"Please input your username!"));
                        } else if(usm.length() > 15 || usm.length() < 4){
                            allPass = false;
                            showInfoHandler.post(showInfo(0,"Username should include 4 ~ 15 characters!"));
                        } else{
                            String url = userApi + "/check/" + usm ;
                            StringRequest request = new StringRequest(url, future, future);
                            requestQueueMain.add(request);
                            try {
                                futureRes = future.get().toString();

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            if(futureRes.equals("true")){
                                allPass = false;
                                showInfoHandler.post(showInfo(0,"This name has been used"));
                            }
                        }
                        if(pwd.length() == 0){
                            allPass = false;
                            showInfoHandler.post(showInfo(1, "Please input your password!"));
                        } else if(pwd.length() > 15 || pwd.length() < 8){
                            allPass = false;
                            showInfoHandler.post(showInfo(1, "Password should include 8 ~ 15 characters!"));
                        }

                        if(!pwd2.equals(pwd)){
                            allPass = false;
                            showInfoHandler.post(showInfo(2,"Please input the same password as above!"));
                        }
                        if(progressDialog.isShowing() && progressDialog!=null){
                            progressDialog.dismiss();
                        }
                        if(allPass){
                            User user = new User(usm,pwd);
                            signUp(user) ;
                        }
                    }
                }).start();
            }
        });

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String usm = username.getText().toString();
                    if(usm.length() == 0){
                        usernameInfo.setText("Please input your username!");
                        usernameInfo.setVisibility(View.VISIBLE);
                    } else if(usm.length() > 15 || usm.length() < 4){
                        usernameInfo.setText("Username should include 4 ~ 15 characters!");
                        usernameInfo.setVisibility(View.VISIBLE);
                    } else{
                        checkUsername(usm);
                    }
                } else{
                    usernameInfo.setVisibility(View.GONE);
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String pwd = password.getText().toString();
                    if(pwd.length() == 0){
                        passwordInfo.setText("Please input your password!");
                        passwordInfo.setVisibility(View.VISIBLE);
                    } else if(pwd.length() > 15 || pwd.length() < 8){
                        passwordInfo.setText("Password should include 8 ~ 15 characters!");
                        passwordInfo.setVisibility(View.VISIBLE);
                    }
                } else{
                    passwordInfo.setVisibility(View.GONE);
                }
            }
        });
        conPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String pwd = password.getText().toString();
                    String pwd2 = conPwd.getText().toString();
                    if(!pwd2.equals(pwd)){
                        conPwdInfo.setText("Please input the same password as above!");
                        conPwdInfo.setVisibility(View.VISIBLE);
                    }
                } else{
                    conPwdInfo.setVisibility(View.GONE);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
                SignupActivity.this.finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.signup) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    void checkUsername(String usm){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = this.getString(R.string.user_api) + "/check/" + usm ;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true")){
                            usernameInfo.setText("This name has been used!");
                            usernameInfo.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                Log.e("error", arg0.toString());
            }
        });
        requestQueue.add(stringRequest);
    }


    void signUp(User user){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject job = new JSONObject();
        try {
            job.put("username", user.getUsername());
            job.put("password", user.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = this.getString(R.string.user_api) + "/signup" ;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, job,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        try {
                            intent.putExtra("usm", (String)jsonObject.get("username"));
                            intent.putExtra("pwd", (String)jsonObject.get("password"));
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    Runnable showInfo(int i, String info){
        Runnable r = null;
        final String ifo = info ;
        switch (i){
            case 0:
                r=new Runnable() {
                    @Override
                    public void run() {
                        usernameInfo.setText(ifo);
                        usernameInfo.setVisibility(View.VISIBLE);
                    }
                };
                break;
            case 1:
                r=new Runnable() {
                    @Override
                    public void run() {
                        passwordInfo.setText(ifo);
                        passwordInfo.setVisibility(View.VISIBLE);
                    }
                };
                break;
            case 2:
                r=new Runnable() {
                    @Override
                    public void run() {
                        conPwdInfo.setText(ifo);
                        conPwdInfo.setVisibility(View.VISIBLE);
                    }
                };
                break;
        }
        return r ;
    }
//    Runnable loading=new Runnable() {
//        @Override
//        public void run() {
//            progressDialog= ProgressDialog.show(this,
//                    "Loading", "Loading");
//        }
//    };

}

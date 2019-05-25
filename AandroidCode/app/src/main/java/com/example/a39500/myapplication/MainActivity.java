package com.example.a39500.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;



public class MainActivity extends AppCompatActivity {

    TextInputEditText username;
    TextInputEditText password;
    Button loginBtn;
    ImageView title;
    Button signup;
    boolean manage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
        String name = sp.getString("name",null);
        int uid = sp.getInt("uid",0);
        if(name!=null && uid != 0){
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            intent.putExtra("usm", name);
            intent.putExtra("uid", uid);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signup = findViewById(R.id.signup);
        title = findViewById(R.id.imageView);
        Intent lastIntent = getIntent();
        username.setText(lastIntent.getStringExtra("usm"));
        password.setText(lastIntent.getStringExtra("pwd"));
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(username.getWindowToken(), 0);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usm = username.getText().toString();
                String pwd = password.getText().toString();
                AlertDialog.Builder builder  = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Whoops!");
                if(usm.length() == 0){
                    builder.setMessage("Please input your username!");
                    builder.show();
                } else if(pwd.length() == 0){
                    builder.setMessage("Please input your password!");
                    builder.show();
                } else
                    login(usm, pwd);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

    }

    private void login(String usm, String pwd) {
        final String usmf = usm ;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = this.getString(R.string.user_api) + "/login/" + usm + "?password=" + pwd;
        final ProgressDialog progressDialog = ProgressDialog.show(this,
                "Loading", "Loading");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(progressDialog.isShowing() && progressDialog!=null){
                            progressDialog.dismiss();
                        }
                        AlertDialog.Builder builder  = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Whoops!");
                        switch (response){
                            case "NO_USER":
                                builder.setMessage("No user found!");
                                builder.show();
                                break;
                            case "DENIED":
                                builder.setMessage("Incorrect password!");
                                builder.show();
                                break;
                            default:
                                SharedPreferences sp = getSharedPreferences("User", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("name", usmf);
                                editor.putInt("uid", Integer.parseInt(response));
                                editor.commit();
                                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                                intent.putExtra("usm", usmf);
                                intent.putExtra("uid", Integer.parseInt(response));
                                startActivity(intent);
                                finish();
                                break;
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


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("--Main--", "onConfigurationChanged");
    }
}

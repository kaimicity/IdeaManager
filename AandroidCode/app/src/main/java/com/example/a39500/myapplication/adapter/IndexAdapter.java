package com.example.a39500.myapplication.adapter;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.a39500.myapplication.EditActivity;
import com.example.a39500.myapplication.ListActivity;
import com.example.a39500.myapplication.MainActivity;
import com.example.a39500.myapplication.R;
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

public class IndexAdapter extends ArrayAdapter {
    final private int resourceId;
    private ProgressDialog progressDialog;
    private ListActivity context;
    private final List<Idea> ideas;
    private boolean managing;
    private boolean linking;
    private int linker;
    int connectPosition;
    int linkedPosition;
    int touchedIdea;
    boolean mt;

    public IndexAdapter(Context context, int textViewResourceId, List<Idea> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.context = (ListActivity) context;
        this.ideas = objects;
        this.managing = false;
        this.linking = false;
        this.linker = 0;
        connectPosition = -1;
        touchedIdea = 0;
        linkedPosition = -1;
        mt = false;
    }

    @Override
    public int getCount() {
        return ideas.size();
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Idea idea = (Idea) getItem(position);
        idea.setPosition(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView idea_id = view.findViewById(R.id.idea_id);
        TextView idea_topic = view.findViewById(R.id.idea_topic);
        TextView idea_content = view.findViewById(R.id.idea_content);
        TextView idea_moddate = view.findViewById(R.id.idea_moddate);
        ConstraintLayout cl = view.findViewById(R.id.constraintLayout);
        cl.setBackgroundColor(context.getColor(idea.getBackColor()));
        idea_moddate.setTextColor(Color.argb(127, 0, 0, 0));
        idea_id.setText("" + idea.getIid());
        idea_topic.setText(idea.getTopic());
        if (idea.getContent().length() < 20) {
            int ent = idea.getContent().indexOf("\n");
            if (ent == -1)
                ent = idea.getContent().length();
            idea_content.setText(idea.getContent().substring(0, ent));
        } else
            idea_content.setText(idea.getContent().substring(0, 18) + "...");
        Date d = idea.getModDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.UK);
        Date n = null;
        try {
            n = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d.getTime() > n.getTime()) {
            idea_moddate.setText(sdf2.format(d));
        } else if (d.getTime() < n.getTime() && d.getTime() >= n.getTime() - 24 * 60 * 60 * 1000) {
            idea_moddate.setText("Yesterday");
        } else {
            idea_moddate.setText(sdf.format(d));
        }
        final LinearLayout ll = view.findViewById(R.id.btns);
        final Button edit = view.findViewById(R.id.edit_button);
        final Button delete = view.findViewById(R.id.delete_button);
        if (idea.isBtns()) {
            ll.setVisibility(View.VISIBLE);
        } else {
            ll.setVisibility(View.GONE);
        }
        cl.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (ll.getVisibility() == View.GONE) {
                    closeAllBtns();
                    if (!managing && !linking) {
                        ((Idea) getItem(position)).setBtns(true);
                    } else {
                        if (idea.getBackColor() == R.color.signUp) {
                            if (managing)
                                idea.waitKill();
                            if (linking)
                                idea.linked();
                        } else
                            idea.release();
                    }

                } else if (ll.getVisibility() == View.VISIBLE) {
                    closeAllBtns();
                }
                notifyDataSetChanged();
            }
        });
        cl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (connectPosition == -1) {
                        touchedIdea = 0;
                        touchedIdea++;
                        connectPosition = position;
                    } else if (position != connectPosition) {
                        touchedIdea++;
                        if (touchedIdea == 2) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                            builder.setTitle("Whoops!");
//                            builder.show();
                            linkedPosition = position;
                            closeAllBtns();
                            notifyDataSetChanged();
                            link();
                            return true;
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    connectPosition = -1;
                    connectPosition = -1;
                    linkedPosition = -1;
                    touchedIdea--;
                }
                return false;
            }
        });
        cl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setLinking();
                context.turnCancel();
                idea.checkLink();
                linker = idea.getIid();
                linkColor();
                notifyDataSetChanged();
                touchedIdea = 0;
                return true;
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = Integer.parseInt(idea.getIid() + "");
                deleteIdea(id, position);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("uid", idea.getUserId());
                intent.putExtra("topic", idea.getTopic());
                intent.putExtra("iid", idea.getIid());
                intent.putExtra("content", idea.getContent());
//                context.startActivity( intent );
                context.startActivityForResult(intent, 1);
//                context.finish();
            }
        });
        return view;
    }

    private void deleteIdea(int id, final int gp) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.idea_api) + "/delete/" + id;
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {
                        ideas.remove(gp);
                        notifyDataSetChanged();
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

    void closeAllBtns() {
        for (int i = 0; i < this.getCount(); i++) {
            ((Idea) this.getItem(i)).setBtns(false);
        }
    }

    public void releaseAll() {
        for (int i = 0; i < this.getCount(); i++) {
            ((Idea) this.getItem(i)).release();
        }
        notifyDataSetChanged();
    }


    public void setManaging() {
        closeAllBtns();
        managing = true;
        notifyDataSetChanged();
    }

    public void desetManaging() {
        managing = false;
    }

    public boolean isManaging() {
        return managing;
    }

    public void setLinking() {
        closeAllBtns();
        linking = true;
        notifyDataSetChanged();
    }

    public void desetlinking() {
        linking = false;
    }

    public boolean isLinking() {
        return linking;
    }

    private List<Integer> generateKillList() {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < this.getCount(); i++) {
            if (((Idea) this.getItem(i)).getBackColor() == R.color.wait_kill)
                res.add(((Idea) this.getItem(i)).getIid());
        }
        return res;
    }

    private List<Integer> generateLinkList() {
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < this.getCount(); i++) {
            if (((Idea) this.getItem(i)).getBackColor() == R.color.linked)
                res.add(((Idea) this.getItem(i)).getIid());
        }
        return res;
    }

    public void deleteAll() {
        if (generateKillList().size() == 0) {
            context.cancelClick();
            return;
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.idea_api) + "deleteList";
        JSONArray para = new JSONArray();
        final List<Integer> gk = generateKillList();
        JSONObject p = new JSONObject();
        try {
            for (Integer i : gk) {
                para.put(i);
            }
            p.put("list", para);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, p,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            List<Idea> survivors = new ArrayList<>();
                            for (int i = 0; i < ideas.size(); i++) {
                                Idea id = ideas.get(i);
                                if (generateKillList().contains(id.getIid())) {
                                    survivors.add(id);
                                }
                            }
                            for (int i = 0; i < survivors.size(); i++) {
                                ideas.remove(survivors.get(i).getPosition() - i);
                            }
                            context.cancelClick();
                            notifyDataSetChanged();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void linkColor() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.idea_api) + linker + "/link";
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray indexIdea) {
                        try {
                            List<Integer> links = new ArrayList<>();
                            for (int i = 0; i < indexIdea.length(); i++) {
                                links.add(indexIdea.getJSONObject(i).getInt("id"));
                            }
                            for (Idea i : ideas) {
                                if (links.contains(i.getIid())) {
                                    i.linked();
                                }
                            }
                            notifyDataSetChanged();
                            if (progressDialog.isShowing() && progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    public void updateLink() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.idea_api) + linker + "/link";
        JSONArray para = new JSONArray();
        final List<Integer> gk = generateLinkList();
        JSONObject p = new JSONObject();
        try {
            for (Integer i : gk) {
                para.put(i);
            }
            p.put("list", para);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, p,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            context.cancelClick();
                            notifyDataSetChanged();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void link(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = context.getString(R.string.idea_api) + ((Idea)getItem(connectPosition)).getIid() + "/link/" + ((Idea)getItem(linkedPosition)).getIid();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {
                        if (progressDialog.isShowing() && progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        touchedIdea--;
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(jsonObject);
                        builder.show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                Log.e("error", arg0.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

}

package com.example.a39500.myapplication.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.a39500.myapplication.EditActivity;
import com.example.a39500.myapplication.ListActivity;
import com.example.a39500.myapplication.R;
import com.example.a39500.myapplication.entity.Idea;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditAdapter extends ArrayAdapter {
    final private int resourceId;
    private ProgressDialog progressDialog;
    private EditActivity context;
    private final List<Idea> ideas;

    public EditAdapter(Context context, int textViewResourceId, List<Idea> objects) {
        super( context, textViewResourceId, objects );
        resourceId = textViewResourceId;
        this.context = (EditActivity) context;
        this.ideas = objects;
    }

    @Override
    public int getCount() {
        return ideas.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Idea idea = (Idea) getItem( position );
        idea.setPosition( position );
        View view = LayoutInflater.from( getContext() ).inflate( resourceId, null );
        TextView idea_id = view.findViewById( R.id.idea_id );
        TextView idea_topic = view.findViewById( R.id.idea_topic );
        TextView idea_content = view.findViewById( R.id.idea_content );
        TextView idea_moddate = view.findViewById( R.id.idea_moddate );
        ConstraintLayout cl = view.findViewById( R.id.constraintLayout );
        cl.setBackgroundColor( context.getColor( idea.getBackColor() ) );
        idea_moddate.setTextColor( Color.argb( 127, 0, 0, 0 ) );
        idea_id.setText( "" + idea.getIid() );
        idea_topic.setText( idea.getTopic() );
        if (idea.getContent().length() < 20) {
            int ent = idea.getContent().indexOf( "\n" );
            if (ent == -1)
                ent = idea.getContent().length();
            idea_content.setText( idea.getContent().substring( 0, ent ) );
        } else
            idea_content.setText( idea.getContent().substring( 0, 18 ) + "..." );
        Date d = idea.getModDate();
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd",Locale.UK );
        SimpleDateFormat sdf2 = new SimpleDateFormat( "HH:mm",Locale.UK );
        Date n = null;
        try {
            n = sdf.parse( sdf.format( new Date() ) );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d.getTime() > n.getTime()) {
            idea_moddate.setText( sdf2.format( d ) );
        } else if (d.getTime() < n.getTime() && d.getTime() >= n.getTime() - 24 * 60 * 60 * 1000) {
            idea_moddate.setText( "Yesterday" );
        } else {
            idea_moddate.setText( sdf.format( d ) );
        }
        final LinearLayout ll = view.findViewById( R.id.btns );
        final Button edit = view.findViewById( R.id.edit_button );
        final Button delete = view.findViewById( R.id.delete_button );
        if (idea.isBtns()) {
            ll.setVisibility( View.VISIBLE );
        } else {
            ll.setVisibility( View.GONE );
        }
        cl.setOnClickListener( new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( context, EditActivity.class );
                intent.putExtra( "uid", idea.getUserId() );
                intent.putExtra( "topic", idea.getTopic() );
                intent.putExtra( "iid", idea.getIid() );
                intent.putExtra( "content", idea.getContent() );
//                context.startActivity( intent );
                context.startActivityForResult(intent, 1);
//                context.finish();
            }
        } );

        return view;
    }

}

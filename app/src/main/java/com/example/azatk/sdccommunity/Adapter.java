package com.example.azatk.sdccommunity;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {

    List<Constructor> list;
    Context context;
    Constructor con;

    public Adapter(FragmentActivity mainActivity, List<Constructor> constructors) {
        this.list = constructors;
        this.context = mainActivity;
    }


    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new AdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final Adapter.AdapterViewHolder holder, final int position) {

        con = list.get(position);

        Picasso.with(context).load(con.getThumb_src()).into(holder.imageView);

        holder.mText.setText(con.getText());

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AdapterViewHolder extends ViewHolder {
        public TextView mText;
        public TextView mText2;
        public ImageView imageView;

        public AdapterViewHolder(View v) {
            super(v);
            mText = (TextView) itemView.findViewById(R.id.text);
            mText2 = (TextView) itemView.findViewById(R.id.text2);
            imageView = (ImageView) itemView.findViewById(R.id.image);

        }
    }

    public void updateList(List<Constructor> constructors) {
        list = constructors;
    }
}

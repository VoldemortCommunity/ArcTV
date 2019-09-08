package com.oxoo.spagreen.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oxoo.spagreen.R;
import com.oxoo.spagreen.models.CommonModels;
import com.oxoo.spagreen.models.GenreModel;

import java.util.ArrayList;
import java.util.List;

public class GenreHomeAdapter extends RecyclerView.Adapter<GenreHomeAdapter.OriginalViewHolder> {

    private List<GenreModel> items = new ArrayList<>();
    private List<CommonModels> listData = new ArrayList<>();
    private Context ctx;


    public GenreHomeAdapter(Context context, List<GenreModel> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public GenreHomeAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        GenreHomeAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_genre_home, parent, false);
        vh = new GenreHomeAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(GenreHomeAdapter.OriginalViewHolder holder, final int position) {

        final GenreModel obj = items.get(position);
        holder.name.setText(obj.getName());

        HomePageAdapter adapter=new HomePageAdapter(ctx,obj.getList());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false));
        holder.recyclerView.setAdapter(adapter);



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        RecyclerView recyclerView;


        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tv_name);
            recyclerView=v.findViewById(R.id.recyclerView);
        }
    }




}

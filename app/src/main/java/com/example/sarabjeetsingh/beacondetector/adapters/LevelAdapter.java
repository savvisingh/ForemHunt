package com.example.sarabjeetsingh.beacondetector.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.sarabjeetsingh.beacondetector.R;

/**
 * Created by Sarabjeet Singh on 3/16/2016.
 */
public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.MyViewHolder> {
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_level_item, parent, false);

        // create ViewHolder

        MyViewHolder viewHolder = new MyViewHolder(itemLayoutView);
        return viewHolder;
    }

    private Context context;
    private OnItemClickListener mItemClickListener;
    private int currentLevel;

    public LevelAdapter( Context context,int currentLevel){

        this.currentLevel = currentLevel;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.levelNumber.setText("LEVEL " + (position+1));
        if(position <= currentLevel){
            holder.levelStatus.setImageResource(R.drawable.ic_action_unlock);
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }


    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView levelNumber;
        private ImageView levelStatus;
        public MyViewHolder(View itemView) {
            super(itemView);
            levelNumber = (TextView) itemView.findViewById(R.id.level_number);
            levelStatus = (ImageView) itemView.findViewById(R.id.level_status);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getAdapterPosition()); //OnItemClickListener mItemClickListener;
        }
    }
}


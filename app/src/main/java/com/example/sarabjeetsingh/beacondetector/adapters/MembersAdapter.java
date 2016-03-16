package com.example.sarabjeetsingh.beacondetector.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sarabjeetsingh.beacondetector.BuildConfig;
import com.example.sarabjeetsingh.beacondetector.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Sarabjeet Singh on 3/13/2016.
 */
public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder> {
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_members, parent, false);

        // create ViewHolder

        MyViewHolder viewHolder = new MyViewHolder(itemLayoutView);
        return viewHolder;
    }

    private List<ParseUser> userList;
    private Context context;
    private final int AVAILABLE=1, MEMBER=2;
    private int userType;
    private OnItemClickListener mItemClickListener;

    public MembersAdapter(List<ParseUser> userList, Context context, int userType){
        this.userList = userList;
        this.context = context;
        this.userType = userType;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if(userType == MEMBER){
            holder.actionButton.setVisibility(View.GONE);
        }
        ParseUser user = userList.get(position);
        Glide.with(context).load(user.get("profilePic")).into(holder.userImage);
        if(user.get("lastName") == null){
            holder.userName.setText(user.get("firstName") + " ");
        }else holder.userName.setText(user.get("firstName") + " " + user.get("lastName"));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CircularImageView userImage;
        private TextView userName;
        private Button actionButton;
        public MyViewHolder(View itemView) {
            super(itemView);
            userImage = (CircularImageView) itemView.findViewById(R.id.user_imageview);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            actionButton = (Button) itemView.findViewById(R.id.action_button);
            actionButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getAdapterPosition()); //OnItemClickListener mItemClickListener;
        }
    }
}

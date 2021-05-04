package com.example.r_edu_kt.User.CourseLayout.Fragments;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.r_edu_kt.R;

import java.util.List;

public class CourseRecyclerViewAdapter extends RecyclerView.Adapter<CourseRecyclerViewAdapter.MyViewHolder> {

    Context mContext;
    List<CourseModel> mData;

    public CourseRecyclerViewAdapter(Context mContext, List<CourseModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        MyViewHolder vHolder = new MyViewHolder(v);
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_mainTitle.setText(mData.get(position).getMainTitle());
        holder.tv_subTitle.setText(mData.get(position).getSubTitle());
        holder.videoIcon.setImageResource(mData.get(position).getVideoIcon());
        holder.completedIcon.setImageResource(mData.get(position).getCompletedIcon());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_mainTitle, tv_subTitle;
        private ImageView videoIcon,completedIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_mainTitle = (TextView) itemView.findViewById(R.id.main_title);
            tv_subTitle = (TextView) itemView.findViewById(R.id.sub_title);
            videoIcon = (ImageView) itemView.findViewById(R.id.video_icon);
            completedIcon = (ImageView) itemView.findViewById(R.id.completed_icon);

        }
    }
}

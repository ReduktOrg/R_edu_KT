package com.example.r_edu_kt.HelperClasses.HomeAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.CourseLayout.CourseOverview;

import java.util.ArrayList;

public class MostViewedAdapter extends RecyclerView.Adapter<MostViewedAdapter.MostViewedViewHolder>{
    ArrayList<MostViewedHelperClass> mostViewedCourses;
    Context context;
    public MostViewedAdapter(ArrayList<MostViewedHelperClass> mostViewedCourses, Context applicationContext) {
        this.mostViewedCourses = mostViewedCourses;
        this.context=applicationContext;
    }


    @NonNull
    @Override
    public MostViewedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.most_viewed_card_design, parent, false);
        MostViewedViewHolder mostViewedViewHolder = new MostViewedViewHolder(view);
        return mostViewedViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MostViewedViewHolder holder, int position) {
        final MostViewedHelperClass helperClass = mostViewedCourses.get(position);

        holder.imageView.setImageResource(helperClass.getImage());
        holder.textView.setText(helperClass.getTitle());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, CourseOverview.class);
                intent.putExtra("courseimage",helperClass.getImage());
                intent.putExtra("title",helperClass.getTitle());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mostViewedCourses.size();
    }

    public static class MostViewedViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public MostViewedViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.mv_image);
            textView = itemView.findViewById(R.id.mv_title);
        }
    }
}

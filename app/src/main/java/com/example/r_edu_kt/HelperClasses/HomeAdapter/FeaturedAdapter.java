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

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder> {

    ArrayList<FeaturedHelperClass> featuredCourses;
    Context context;
    public FeaturedAdapter(ArrayList<FeaturedHelperClass> featuredCourses, Context applicationContext) {
        this.featuredCourses = featuredCourses;
        this.context=applicationContext;
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_card_design,parent,false);
        FeaturedViewHolder featuredViewHolder=new FeaturedViewHolder(view);
        return featuredViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {

        final FeaturedHelperClass featuredHelperClass =featuredCourses.get(position);

        holder.image.setImageResource(featuredHelperClass.getImage());
        holder.title.setText(featuredHelperClass.getTitle());
        holder.desc.setText(featuredHelperClass.getDescription());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, CourseOverview.class);
                intent.putExtra("courseimage",featuredHelperClass.getImage());
                intent.putExtra("title",featuredHelperClass.getTitle());
                intent.putExtra("description",featuredHelperClass.getDescription());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return featuredCourses.size();
    }

    public static class FeaturedViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, desc;

        public FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);

            //Hooks
            image = itemView.findViewById(R.id.featured_image);
            title = itemView.findViewById(R.id.featured_title);
            desc = itemView.findViewById(R.id.featured_desc);

        }
    }
}

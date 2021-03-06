package com.example.r_edu_kt.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.r_edu_kt.CommentsActivity;
import com.example.r_edu_kt.Model.Post;
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.image;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {
    public Context mContext;
    public List<Post> mPostList;
    private FirebaseUser firebaseUser;
    String name="";
    RadioButton radioButton;
    RadioGroup radioGroup;


    public PostAdapter(Context mContext, List<Post> mPostList) {
        this.mContext = mContext;
        this.mPostList = mPostList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.questions_retrieved_layout,parent,false);
        return new PostAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPostList.get(position);

        final String postid = post.getPostid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("questions_report").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count= snapshot.getChildrenCount();
                if(count==25){
                    FirebaseDatabase.getInstance().getReference("questions posts").child(postid).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(post.getQuestionimage() == null){
            holder.questionImage.setVisibility(View.GONE);
        }
        else{
            holder.questionImage.setVisibility(View.VISIBLE);
        }
        Glide.with(mContext).load(post.getQuestionimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(holder.questionImage);
        holder.questionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(holder.questionImage.getContext()).inflate(R.layout.imagedialog,null);

                final PhotoView img=view.findViewById(R.id.img);
                final ImageButton close =view.findViewById(R.id.close);
                final ImageButton rotate = view.findViewById(R.id.rotate);
                Glide.with(img.getContext()).load(post.getQuestionimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(img);

                final AlertDialog dialog = new AlertDialog.Builder(holder.questionImage.getContext())
                        .setView(view).setCancelable(false).create();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                rotate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img.setRotationBy(90);
                    }
                });
            }
        });
        holder.expandable_text.setText(post.getQuestion());
        holder.topicTextview.setText(post.getTopic());
        holder.askedOnTextview.setText(post.getDate());


        publisherInformation(holder.asked_by_Textview,post.getPublisher(),holder.profile_image);
        isLiked(post.getPostid(),holder.like);
        isDisLiked(post.getPostid(),holder.dislike);
        getLikes(holder.likes,post.getPostid());
        getDisLikes(holder.dislikes,post.getPostid());
        getComments(holder.comments,post.getPostid());

        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent img_intent = new Intent(mContext, image.class);
                img_intent.putExtra("Uid",post.getPublisher());
                mContext.startActivity(img_intent);
            }
        });

        holder.like_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like") && holder.dislike.getTag().equals("dislike")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                }
                else if(holder.like.getTag().equals("like") && holder.dislike.getTag().equals("disliked")){
                    FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.dislike_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.dislike.getTag().equals("dislike") && holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                }else if (holder.dislike.getTag().equals("dislike") && holder.like.getTag().equals("liked")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("dislikes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisher",post.getPublisher());
                mContext.startActivity(intent);
            }
        });


        holder.more.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext,v);
                popupMenu.inflate(R.menu.post_menu);
                popupMenu.setGravity(Gravity.END);

                if(!post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }else if(post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                }
                popupMenu.show();
                popupMenu.getMenu().findItem(R.id.edit).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        View view = LayoutInflater.from(holder.more.getContext()).inflate(R.layout.editpost,null);

                        final String postid=post.getPostid();

                        final ImageView qimage=view.findViewById(R.id.que_img);
                        final Spinner sp=view.findViewById(R.id.spin);
                        final EditText question=view.findViewById(R.id.questtext);
                        Button submit=view.findViewById(R.id.postQ);
                        Button canc=view.findViewById(R.id.canc);

                        final AlertDialog dialog = new AlertDialog.Builder(holder.more.getContext())
                                .setView(view).setCancelable(false).create();

                        if(post.getQuestionimage()!=null){
                            qimage.setVisibility(View.VISIBLE);
                        }
                        Glide.with(mContext).load(post.getQuestionimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(qimage);
                        question.setText(post.getQuestion());

                        switch(post.getTopic()) {
                            case "Cse":
                                sp.setSelection(1);
                                break;
                            case "Ece":
                                sp.setSelection(2);
                                break;
                            case "Mech":
                                sp.setSelection(3);
                                break;
                            case "Civil":
                                sp.setSelection(4);
                                break;
                            case "Chemical":
                                sp.setSelection(5);
                                break;
                        }

                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                        dialog.show();


                        canc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        FirebaseDatabase database= FirebaseDatabase.getInstance();
                        final DatabaseReference reference= database.getReference("questions posts");

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                name = question.getText().toString();
                                if (TextUtils.isEmpty(name)) {
                                    question.setError("Question Required");
                                    question.requestFocus();
                                }
                                else if(sp.getSelectedItem().equals("select branch")){
                                    Toast.makeText(mContext,"select a valid branch",Toast.LENGTH_SHORT).show();
                                }else if(!TextUtils.isEmpty(name) && !sp.getSelectedItem().equals("select branch")){
                                    final ProgressDialog pd = new ProgressDialog(mContext);
                                    pd.setMessage("Updating your question");
                                    pd.show();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("question",question.getText().toString());
                                    hashMap.put("topic",sp.getSelectedItem().toString());

                                    reference.child(postid).updateChildren(hashMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(mContext,"question updated successfully",Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        pd.dismiss();
                                                    }
                                                    else {
                                                        Toast.makeText(mContext,"could not update question",Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        pd.dismiss();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        return true;
                    }
                });


                popupMenu.getMenu().findItem(R.id.delete).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String postid=post.getPostid();

                        View view = LayoutInflater.from(holder.more.getContext()).inflate(R.layout.deletedialog,null);

                        Button submit=view.findViewById(R.id.postd);
                        Button canc=view.findViewById(R.id.canceld);

                        final AlertDialog dialog = new AlertDialog.Builder(holder.more.getContext())
                                .setView(view).setCancelable(false).create();

                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();

                        canc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FirebaseDatabase.getInstance().getReference("questions posts").child(postid).removeValue();
                                dialog.dismiss();
                            }
                        });
                        return true;
                    }
                });

                popupMenu.getMenu().findItem(R.id.report).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String postid=post.getPostid();

                        final View view = LayoutInflater.from(holder.more.getContext()).inflate(R.layout.reportdialog,null);
                        Button submit=view.findViewById(R.id.postr);
                        Button canc=view.findViewById(R.id.cancelr);
                        radioGroup = view.findViewById(R.id.radio);

                        final AlertDialog dialog = new AlertDialog.Builder(holder.more.getContext())
                                .setView(view).setCancelable(false).create();

                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int selected = radioGroup.getCheckedRadioButtonId();
                                radioButton=view.findViewById(selected);

                                if(selected == -1){
                                    Toast.makeText(mContext,"please select any one of the option to report",Toast.LENGTH_SHORT).show();
                                }else {
                                    String mDate= DateFormat.getDateInstance().format(new Date());
                                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("questions_report");
                                    String questions_reportid= ref.push().getKey();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("questions_reportid",questions_reportid);
                                    hashMap.put("report",radioButton.getText());
                                    hashMap.put("date",mDate);
                                    ref.child(postid).child(questions_reportid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(mContext,"Reported Successfully", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(mContext,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            }
                        });

                        canc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    @Override
    public long getItemId(int position) {
        return mPostList.get(position).hashCode();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        public CircleImageView profile_image;
        public TextView asked_by_Textview,likes,dislikes,comments;
        public ImageView more,like,dislike;
        public ImageButton questionImage;
        public TextView topicTextview,askedOnTextview;
        public ExpandableTextView expandable_text;
        public CardView mCardView;
        public LinearLayout linear,like_layout,dislike_layout,comment;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image=itemView.findViewById(R.id.publisher_profile_image);
            likes=itemView.findViewById(R.id.likes);
            dislikes=itemView.findViewById(R.id.dislikes);
            comments=itemView.findViewById(R.id.comments);
            more=itemView.findViewById(R.id.more);
            questionImage=itemView.findViewById(R.id.questionImage);
            like=itemView.findViewById(R.id.like);
            dislike=itemView.findViewById(R.id.dislike);
            comment=itemView.findViewById(R.id.comment_layout);
            topicTextview=itemView.findViewById(R.id.topicTextview);
            askedOnTextview=itemView.findViewById(R.id.askedOnTextview);
            asked_by_Textview=itemView.findViewById(R.id.asked_by_Textview);
            expandable_text=itemView.findViewById(R.id.expand_text_view);
            mCardView = itemView.findViewById(R.id.cardView);
            linear=itemView.findViewById(R.id.linear);
            like_layout = itemView.findViewById(R.id.like_layout);
            dislike_layout = itemView.findViewById(R.id.dislike_layout);
        }

    }


    private void publisherInformation(final TextView askedBy, String userName, final CircleImageView profile_image){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userName);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getProfileimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(profile_image);
                askedBy.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void isLiked(String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firebaseUser != null) {
                    if(snapshot.child(firebaseUser.getUid()).exists()){
                        imageView.setImageResource(R.drawable.ic_outline_thumb_up_blue);
                        imageView.setTag("liked");
                    }else {
                        imageView.setImageResource(R.drawable.ic_like_outline);
                        imageView.setTag("like");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isDisLiked(String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("dislikes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firebaseUser != null) {
                    if(snapshot.child(firebaseUser.getUid()).exists()){
                        imageView.setImageResource(R.drawable.ic_outline_thumb_down_blue);
                        imageView.setTag("disliked");
                    }else {
                        imageView.setImageResource(R.drawable.ic_dislike_outline);
                        imageView.setTag("dislike");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikes(final TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long numberOfLikes = snapshot.getChildrenCount();
                int NOL = (int) numberOfLikes;
                if(NOL >1){
                    likes.setText(snapshot.getChildrenCount()+"");
                } else if(NOL == 0){
                    likes.setText("0");
                }else{
                    likes.setText(snapshot.getChildrenCount()+"");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getDisLikes(final TextView dislikes, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("dislikes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long numberOfDisLikes = snapshot.getChildrenCount();
                int NOD = (int) numberOfDisLikes;
                if (NOD > 1) {
                    dislikes.setText(snapshot.getChildrenCount() + " Dislikes");
                } else if (NOD == 0) {
                    dislikes.setText("");
                } else {
                    dislikes.setText(snapshot.getChildrenCount() + " Dislike");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getComments(final TextView comments, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long numberOfComments = snapshot.getChildrenCount();
                int NOC = (int) numberOfComments;
                if (NOC > 1) {
                    comments.setText(snapshot.getChildrenCount() + " Comments");
                } else if (NOC == 0) {
                    comments.setText("");
                } else {
                    comments.setText(snapshot.getChildrenCount() + " Comment");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
package com.example.r_edu_kt.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.r_edu_kt.AskQuestionActivity;
import com.example.r_edu_kt.CommentsActivity;
import com.example.r_edu_kt.Model.Post;
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.Register.VerifyOTP;
import com.example.r_edu_kt.User.UserDashboard;
import com.example.r_edu_kt.discussion_home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {
    public Context mContext;
    public List<Post> mPostList;
    private FirebaseUser firebaseUser;
    String selected="",name="";
    private int reportCount=0;

    public PostAdapter(Context mContext, List<Post> mPostList) {
        this.mContext = mContext;
        this.mPostList = mPostList;
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
        if(post.getQuestionimage() == null){
            holder.questionImage.setVisibility(View.GONE);
        }
        else{
            holder.questionImage.setVisibility(View.VISIBLE);
        }
        Glide.with(mContext).load(post.getQuestionimage()).into(holder.questionImage);
        holder.expandable_text.setText(post.getQuestion());
        holder.topicTextview.setText(post.getTopic());
        holder.askedOnTextview.setText(post.getDate());


        publisherInformation(holder.asked_by_Textview,post.getPublisher());
        isLiked(post.getPostid(),holder.like);
        isDisLiked(post.getPostid(),holder.dislike);
        getLikes(holder.likes,post.getPostid());
        getDisLikes(holder.dislikes,post.getPostid());

        holder.like.setOnClickListener(new View.OnClickListener() {
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

        holder.dislike.setOnClickListener(new View.OnClickListener() {
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

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,CommentsActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisher",post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext,v);
                popupMenu.inflate(R.menu.post_menu);

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
                        final DialogPlus dialogPlus=DialogPlus.newDialog(holder.more.getContext())
                                .setContentHolder(new ViewHolder(R.layout.editpost))
                                .setExpanded(true,1300)
                                .create();

                        final String postid=post.getPostid();

                        View myview=dialogPlus.getHolderView();
                        final ImageView qimage=myview.findViewById(R.id.que_img);
                        final Spinner sp=myview.findViewById(R.id.spin);
                        final EditText question=myview.findViewById(R.id.questtext);
                        final  TextView tv=myview.findViewById(R.id.tv);
                        Button submit=myview.findViewById(R.id.postQ);
                        Button canc=myview.findViewById(R.id.canc);

                        // Intent intent = new Intent(mContext, editpost.class);
                        //((Activity)mContext).startActivityForResult(intent,requestCode);

                        Glide.with(mContext).load(post.getQuestionimage()).into(qimage);
                        if(post.getQuestionimage()==null){
                            qimage.setImageResource(R.drawable.ic_image);
                        }
                        question.setText(post.getQuestion());
                        tv.setText(post.getTopic());


                        dialogPlus.show();


                        canc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogPlus.dismiss();
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
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mContext,"question updated successfully",Toast.LENGTH_SHORT).show();
                                                    dialogPlus.dismiss();
                                                    pd.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext,"could not update question",Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                            pd.dismiss();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.more.getContext());
                        builder.setTitle("Delete Panel");
                        builder.setMessage("Delete...?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference("questions posts").child(postid).removeValue();

                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.show();
                        return true;
                    }
                });

                popupMenu.getMenu().findItem(R.id.report).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String postid=post.getPostid();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(holder.more.getContext());
                        final String[] list=holder.more.getContext().getResources().getStringArray(R.array.choice);
                        builder.setTitle("Report Panel");
                        builder.setSingleChoiceItems(list, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selected=list[which];
                            }
                        });
                        builder.setPositiveButton("report", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String mDate= DateFormat.getDateInstance().format(new Date());
                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("questions_report");
                                String questions_reportid= ref.push().getKey();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("questions_reportid",questions_reportid);
                                hashMap.put("report",selected);
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
                        });
                        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
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

    public class viewHolder extends RecyclerView.ViewHolder{
        public CircleImageView publisher_profile_image;
        public TextView asked_by_Textview,likes,dislikes,comments;
        public ImageView more,like,dislike,comment;
        public ImageView questionImage;
        public TextView topicTextview,askedOnTextview;
        public ExpandableTextView expandable_text;
        public CardView mCardView;
        public LinearLayout linear;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            publisher_profile_image=itemView.findViewById(R.id.publisher_profile_image);
            asked_by_Textview = itemView.findViewById(R.id.asked_by_Textview);
            likes=itemView.findViewById(R.id.likes);
            dislikes=itemView.findViewById(R.id.dislikes);
            comments=itemView.findViewById(R.id.comments);
            more=itemView.findViewById(R.id.more);
            questionImage=itemView.findViewById(R.id.questionImage);
            like=itemView.findViewById(R.id.like);
            dislike=itemView.findViewById(R.id.dislike);
            comment=itemView.findViewById(R.id.comment);
            topicTextview=itemView.findViewById(R.id.topicTextview);
            askedOnTextview=itemView.findViewById(R.id.askedOnTextview);
            asked_by_Textview=itemView.findViewById(R.id.asked_by_Textview);
            expandable_text=itemView.findViewById(R.id.expand_text_view);
            mCardView = itemView.findViewById(R.id.cardView);
            linear=itemView.findViewById(R.id.linear);
        }

    }


    private void publisherInformation(final TextView askedBy, String userName){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userName);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                //Glide.with(mContext).load(user.getProfileimage()).into(publisherImage);
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
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                }else {
                    imageView.setImageResource(R.drawable.ic_thumb_up);
                    imageView.setTag("like");
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
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_disliked);
                    imageView.setTag("disliked");
                }else {
                    imageView.setImageResource(R.drawable.ic_dislike);
                    imageView.setTag("dislike");
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
                    likes.setText(snapshot.getChildrenCount()+"likes");
                } else if(NOL == 0){
                    likes.setText("0 likes");
                }else{
                    likes.setText(snapshot.getChildrenCount()+"like");
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
                    dislikes.setText(snapshot.getChildrenCount() + "dislikes");
                } else if (NOD == 0) {
                    dislikes.setText("0 dislikes");
                } else {
                    dislikes.setText(snapshot.getChildrenCount() + "dislike");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
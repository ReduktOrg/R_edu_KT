package com.example.r_edu_kt.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.r_edu_kt.CommentsActivity;
import com.example.r_edu_kt.Model.Post;
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {
    public Context mContext;
    public List<Post> mPostList;
    private FirebaseUser firebaseUser;
    String selected="",name="";
    RadioButton radioButton;
    RadioGroup radioGroup;

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


        publisherInformation(holder.asked_by_Textview,post.getPublisher(),holder.profile_image);
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

                        View view = LayoutInflater.from(holder.more.getContext()).inflate(R.layout.editpost,null);

                        final String postid=post.getPostid();

                        final ImageView qimage=view.findViewById(R.id.que_img);
                        final Spinner sp=view.findViewById(R.id.spin);
                        final EditText question=view.findViewById(R.id.questtext);
                        final  TextView tv=view.findViewById(R.id.tv);
                        Button submit=view.findViewById(R.id.postQ);
                        Button canc=view.findViewById(R.id.canc);

                        final AlertDialog dialog = new AlertDialog.Builder(holder.more.getContext())
                                .setView(view).setCancelable(false).create();

                        // Intent intent = new Intent(mContext, editpost.class);
                        //((Activity)mContext).startActivityForResult(intent,requestCode);

                        if(post.getQuestionimage()!=null){
                            qimage.setVisibility(View.VISIBLE);
                        }
                        Glide.with(mContext).load(post.getQuestionimage()).into(qimage);
                        question.setText(post.getQuestion());
                        tv.setText(post.getTopic());

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
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mContext,"question updated successfully",Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    pd.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext,"could not update question",Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
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

    public class viewHolder extends RecyclerView.ViewHolder{
        public CircleImageView profile_image;
        public TextView asked_by_Textview,likes,dislikes,comments;
        public ImageView more,like,dislike,comment;
        public ImageView questionImage;
        public TextView topicTextview,askedOnTextview;
        public ExpandableTextView expandable_text;
        public CardView mCardView;
        public LinearLayout linear;

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
            comment=itemView.findViewById(R.id.comment);
            topicTextview=itemView.findViewById(R.id.topicTextview);
            askedOnTextview=itemView.findViewById(R.id.askedOnTextview);
            asked_by_Textview=itemView.findViewById(R.id.asked_by_Textview);
            expandable_text=itemView.findViewById(R.id.expand_text_view);
            mCardView = itemView.findViewById(R.id.cardView);
            linear=itemView.findViewById(R.id.linear);
        }

    }


    private void publisherInformation(final TextView askedBy, String userName, final CircleImageView profile_image){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userName);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getProfileimage()).into(profile_image);
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
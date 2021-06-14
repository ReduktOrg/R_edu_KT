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
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.r_edu_kt.CommentReply;
import com.example.r_edu_kt.Model.Comment;
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.image;
import com.github.chrisbanes.photoview.PhotoView;
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

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mCommentList;
    private String postid,name="";
    private FirebaseUser firebaseUser;
    RadioGroup radioGroup;
    RadioButton radioButton;

    public CommentAdapter(Context mContext, List<Comment> mCommentList, String postid) {
        this.mContext = mContext;
        this.mCommentList = mCommentList;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comments_layout, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Comment comment = mCommentList.get(position);

        final String commentpostid=comment.getPostid();
        final String commentid=comment.getCommentid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments_report").child(commentpostid).child(commentid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count= snapshot.getChildrenCount();
                if(count==25){
                    FirebaseDatabase.getInstance().getReference("comments").child(commentpostid).child(commentid).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(comment.getCommentimage() == null){
            holder.commentimage.setVisibility(View.GONE);
        }
        else{
            holder.commentimage.setVisibility(View.VISIBLE);
        }
        Glide.with(mContext).load(comment.getCommentimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(holder.commentimage);

        isLiked(comment.getCommentid(),comment.getPostid(),holder.like);
        isDisLiked(comment.getCommentid(),comment.getPostid(),holder.dislike);
        getLikes(holder.likes,comment.getCommentid(),comment.getPostid());
        getDisLikes(holder.dislikes,comment.getCommentid(),comment.getPostid());
        getComments(holder.replies,comment.getPostid(),comment.getCommentid());

        holder.commentimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(holder.commentimage.getContext()).inflate(R.layout.imagedialog,null);

                final PhotoView img=view.findViewById(R.id.img);
                final ImageButton close =view.findViewById(R.id.close);
                final ImageButton rotate = view.findViewById(R.id.rotate);
                Glide.with(img.getContext()).load(comment.getCommentimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(img);

                final AlertDialog dialog = new AlertDialog.Builder(holder.commentimage.getContext())
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

        holder.commentor_comment.setText(comment.getComment());
        holder.commentDate.setText(comment.getDate());
        getUserInformation(holder.commentorUserName, comment.getPublisher(),holder.commentor_profile_image);

        holder.commentor_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent img_intent = new Intent(mContext, image.class);
                img_intent.putExtra("Uid",comment.getPublisher());
                mContext.startActivity(img_intent);
            }
        });

        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(comment.getPublisher());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            String commentor_username = user.getUserName();
                            Intent intent=new Intent(mContext, CommentReply.class);
                            intent.putExtra("postid",comment.getPostid());
                            intent.putExtra("commentid",comment.getCommentid());
                            intent.putExtra("comment_publisher_username",commentor_username);
                            mContext.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.replies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(comment.getPublisher());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            String commentor_username = user.getUserName();
                            Intent intent=new Intent(mContext, CommentReply.class);
                            intent.putExtra("postid",comment.getPostid());
                            intent.putExtra("commentid",comment.getCommentid());
                            intent.putExtra("comment_publisher_username",commentor_username);
                            mContext.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like") && holder.dislike.getTag().equals("dislike")){
                    FirebaseDatabase.getInstance().getReference().child("comment likes").child(comment.getPostid()).child(comment.getCommentid()).child(firebaseUser.getUid()).setValue(true);
                }
                else if(holder.like.getTag().equals("like") && holder.dislike.getTag().equals("disliked")){
                    FirebaseDatabase.getInstance().getReference().child("comment dislikes").child(comment.getPostid()).child(comment.getCommentid()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("comment likes").child(comment.getPostid()).child(comment.getCommentid()).child(firebaseUser.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("comment likes").child(comment.getPostid()).child(comment.getCommentid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.dislike.getTag().equals("dislike") && holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("comment dislikes").child(comment.getPostid()).child(comment.getCommentid()).child(firebaseUser.getUid()).setValue(true);
                }else if (holder.dislike.getTag().equals("dislike") && holder.like.getTag().equals("liked")){
                    FirebaseDatabase.getInstance().getReference().child("comment likes").child(comment.getPostid()).child(comment.getCommentid()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("comment dislikes").child(comment.getPostid()).child(comment.getCommentid()).child(firebaseUser.getUid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("comment dislikes").child(comment.getPostid()).child(comment.getCommentid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });


        holder.more.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext,v);
                popupMenu.inflate(R.menu.comment_menu);
                popupMenu.setGravity(Gravity.END);

                if(!comment.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }else if(comment.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                }
                popupMenu.show();

                popupMenu.getMenu().findItem(R.id.edit).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        View view = LayoutInflater.from(holder.more.getContext()).inflate(R.layout.editcomment,null);
                        final String commentid=comment.getCommentid();
                        final String postid=comment.getPostid();


                        final ImageView cimage=view.findViewById(R.id.coment_img);
                        final EditText comment1=view.findViewById(R.id.comenttext);
                        Button submit=view.findViewById(R.id.postC);
                        Button canc=view.findViewById(R.id.cance);

                        final AlertDialog dialog = new AlertDialog.Builder(holder.more.getContext())
                                .setView(view).setCancelable(false).create();

                        // Intent intent = new Intent(mContext, editpost.class);
                        //((Activity)mContext).startActivityForResult(intent,requestCode);

                        if(comment.getCommentimage()!=null){
                            cimage.setVisibility(View.VISIBLE);
                        }
                        Glide.with(mContext).load(comment.getCommentimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(cimage);
                        comment1.setText(comment.getComment());



                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                        dialog.show();

                        canc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference reference = database.getReference("comments");

                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                name = comment1.getText().toString();
                                if (TextUtils.isEmpty(name)) {
                                    comment1.setError("Comment Required");
                                    comment1.requestFocus();
                                }else if(!TextUtils.isEmpty(name)){
                                    final ProgressDialog pd = new ProgressDialog(mContext);
                                    pd.setMessage("Updating your Comment");
                                    pd.show();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("comment",comment1.getText().toString());

                                    reference.child(postid).child(commentid).updateChildren(hashMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(mContext,"comment updated successfully",Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        pd.dismiss();
                                                    }
                                                    else {
                                                        Toast.makeText(mContext,"could not update comment",Toast.LENGTH_SHORT).show();
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
                        final String commentid=comment.getCommentid();
                        final String postid = comment.getPostid();

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
                                FirebaseDatabase.getInstance().getReference("comments").child(postid).child(commentid).removeValue();
                                dialog.dismiss();
                            }
                        });
                        return true;
                    }
                });

                popupMenu.getMenu().findItem(R.id.report).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String commentid=comment.getCommentid();
                        final String postid=comment.getPostid();

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
                                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("comments_report");
                                    String comments_reportid= ref.push().getKey();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("comments_reportid",comments_reportid);
                                    hashMap.put("report",radioButton.getText());
                                    hashMap.put("date",mDate);
                                    ref.child(postid).child(commentid).child(comments_reportid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        return mCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView commentor_profile_image;
        public TextView commentorUserName, commentor_comment, commentDate,likes,dislikes,replies;
        public ImageButton commentimage;
        public ImageView more,like,dislike,reply;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentor_profile_image = itemView.findViewById(R.id.commentor_profile_image);
            commentorUserName = itemView.findViewById(R.id.commentorUserName);
            commentor_comment = itemView.findViewById(R.id.commentor_comment);
            commentDate = itemView.findViewById(R.id.commentDate);
            commentimage = itemView.findViewById(R.id.retrieved_commentimage);
            more = itemView.findViewById(R.id.more);
            likes = itemView.findViewById(R.id.likes);
            dislikes = itemView.findViewById(R.id.dislikes);
            replies = itemView.findViewById(R.id.replies);
            like = itemView.findViewById(R.id.like);
            dislike = itemView.findViewById(R.id.dislike);
            reply = itemView.findViewById(R.id.reply);
        }
    }

    private  void getUserInformation(final TextView username, String publisherid, final CircleImageView circleImageView){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getProfileimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(circleImageView);
                username.setText(user.getUserName());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void isLiked(String commentid,String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("comment likes").child(postid).child(commentid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_outline_thumb_up_blue);
                    imageView.setTag("liked");
                }else {
                    imageView.setImageResource(R.drawable.ic_like_outline);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isDisLiked(String commentid, String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("comment dislikes").child(postid).child(commentid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_outline_thumb_down_blue);
                    imageView.setTag("disliked");
                }else {
                    imageView.setImageResource(R.drawable.ic_dislike_outline);
                    imageView.setTag("dislike");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLikes(final TextView likes,String commentid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comment likes").child(postid).child(commentid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    long numberOfLikes = snapshot.getChildrenCount();
                    int NOL = (int) numberOfLikes;
                    if (NOL > 1) {
                        likes.setText(snapshot.getChildrenCount() + "");
                    } else {
                        likes.setText("1");
                    }
                }else{
                    likes.setText("");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getDisLikes(final TextView dislikes, String commentid, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comment dislikes").child(postid).child(commentid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    long numberOfDisLikes = snapshot.getChildrenCount();
                    int NOD = (int) numberOfDisLikes;
                    if (NOD > 1) {
                        dislikes.setText(snapshot.getChildrenCount() + "");
                    } else {
                        dislikes.setText("1");
                    }
                }else {
                    dislikes.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getComments(final TextView replies, String postid, String commentid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments reply").child(postid).child(commentid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long numberOfComments = snapshot.getChildrenCount();
                int NOC = (int) numberOfComments;
                if (NOC > 1) {
                    replies.setText(snapshot.getChildrenCount() + "");
                } else if (NOC == 0) {
                    replies.setText("");
                } else {
                    replies.setText(snapshot.getChildrenCount() + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

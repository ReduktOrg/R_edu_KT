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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.r_edu_kt.Model.ModelCommentReply;
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.image;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class CommentReplyAdapter extends RecyclerView.Adapter<CommentReplyAdapter.ViewHolder> {
    private Context mContext;
    private List<ModelCommentReply> mcommentReplyList;
    private String postid, name="",previous_commentid,pre_comment;
    private FirebaseUser firebaseUser;
    RadioGroup radioGroup;
    RadioButton radioButton;
    BottomSheetDialog bottomSheetDialog;
    EditText adding_comment;
    private ProgressDialog loader;

    public CommentReplyAdapter(Context mContext, List<ModelCommentReply> mcommentReplyList, String postid, String previous_commentid) {
        this.mContext = mContext;
        this.mcommentReplyList = mcommentReplyList;
        this.postid = postid;
        this.previous_commentid = previous_commentid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comments_comment_layout,parent,false);
        return new CommentReplyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final ModelCommentReply modelCommentReply = mcommentReplyList.get(position);

        holder.commentor_comment.setText(modelCommentReply.getComment());
        holder.commentDate.setText(modelCommentReply.getDate());
        getUserInfo(holder.commentor_profile_image,holder.commentorUserName,modelCommentReply.getPublisher());

        holder.commentor_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent img_intent = new Intent(mContext, image.class);
                img_intent.putExtra("Uid",modelCommentReply.getPublisher());
                mContext.startActivity(img_intent);
            }
        });

        String Uname= modelCommentReply.getPrevious_username();
        String previous_com = modelCommentReply.getPrevious_comment();
        if(Uname != null && previous_com !=null){
            holder.line.setVisibility(View.VISIBLE);
            holder.pre_com.setText(previous_com);
            holder.pre_comment_usr.setText("@"+Uname);
        }else if(Uname == null && previous_com ==null){
            holder.line.setVisibility(View.GONE);
            holder.pre_com.setText("");
            holder.pre_comment_usr.setText("");
        }

        isLiked(holder.like,modelCommentReply.getCommentid(),modelCommentReply.getPrevious_commentid(),modelCommentReply.getPostid());
        isDisLiked(holder.dislike,modelCommentReply.getCommentid(),modelCommentReply.getPrevious_commentid(),modelCommentReply.getPostid());
        getLikes(holder.likes,modelCommentReply.getCommentid(),modelCommentReply.getPrevious_commentid(),modelCommentReply.getPostid());
        getDisLikes(holder.dislikes,modelCommentReply.getCommentid(),modelCommentReply.getPrevious_commentid(),modelCommentReply.getPostid());


         holder.like_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like") && holder.dislike.getTag().equals("dislike")){
                    FirebaseDatabase.getInstance().getReference().child("comments_comment likes").child(modelCommentReply.getPostid()).child(modelCommentReply.getPrevious_commentid()).child(modelCommentReply.getCommentid()).child(firebaseUser.getUid()).setValue(true);
                }
                else if(holder.like.getTag().equals("like") && holder.dislike.getTag().equals("disliked")){
                    FirebaseDatabase.getInstance().getReference().child("comments_comment dislikes").child(modelCommentReply.getPostid()).child(modelCommentReply.getPrevious_commentid()).child(modelCommentReply.getCommentid()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("comments_comment likes").child(modelCommentReply.getPostid()).child(modelCommentReply.getPrevious_commentid()).child(modelCommentReply.getCommentid()).child(firebaseUser.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("comments_comment likes").child(modelCommentReply.getPostid()).child(modelCommentReply.getPrevious_commentid()).child(modelCommentReply.getCommentid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.dislike_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.dislike.getTag().equals("dislike") && holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("comments_comment dislikes").child(modelCommentReply.getPostid()).child(modelCommentReply.getPrevious_commentid()).child(modelCommentReply.getCommentid()).child(firebaseUser.getUid()).setValue(true);
                }else if (holder.dislike.getTag().equals("dislike") && holder.like.getTag().equals("liked")){
                    FirebaseDatabase.getInstance().getReference().child("comments_comment likes").child(modelCommentReply.getPostid()).child(modelCommentReply.getPrevious_commentid()).child(modelCommentReply.getCommentid()).child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("comments_comment dislikes").child(modelCommentReply.getPostid()).child(modelCommentReply.getPrevious_commentid()).child(modelCommentReply.getCommentid()).child(firebaseUser.getUid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("comments_comment dislikes").child(modelCommentReply.getPostid()).child(modelCommentReply.getPrevious_commentid()).child(modelCommentReply.getCommentid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext,v);
                popupMenu.inflate(R.menu.commentreply_menu);
                popupMenu.setGravity(Gravity.END);

                if(!modelCommentReply.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }else if(modelCommentReply.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.reply).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.report).setVisible(false);
                }
                popupMenu.show();

                popupMenu.getMenu().findItem(R.id.edit).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        View view = LayoutInflater.from(holder.more.getContext()).inflate(R.layout.editcomment,null);
                        final String commentid=modelCommentReply.getCommentid();
                        final String previous_commentid = modelCommentReply.getPrevious_commentid();
                        final String postid=modelCommentReply.getPostid();

                        final EditText comment1=view.findViewById(R.id.comenttext);
                        Button submit=view.findViewById(R.id.postC);
                        Button canc=view.findViewById(R.id.cance);

                        final AlertDialog dialog = new AlertDialog.Builder(holder.more.getContext())
                                .setView(view).setCancelable(false).create();

                        comment1.setText(modelCommentReply.getComment());

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
                        final DatabaseReference reference = database.getReference("comments reply");

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

                                    reference.child(postid).child(previous_commentid).child(commentid).updateChildren(hashMap)
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
                        final String commentid=modelCommentReply.getCommentid();
                        final String previous_commentid = modelCommentReply.getPrevious_commentid();
                        final String postid = modelCommentReply.getPostid();

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
                                FirebaseDatabase.getInstance().getReference("comments reply").child(postid).child(previous_commentid).child(commentid).removeValue();
                                dialog.dismiss();
                            }
                        });
                        return true;
                    }
                });

                popupMenu.getMenu().findItem(R.id.reply).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        bottomSheetDialog = new BottomSheetDialog(mContext, R.style.BottomSheetTheme);
                        View sheetView = LayoutInflater.from(mContext).inflate(R.layout.bottomsheet, (ViewGroup) bottomSheetDialog.findViewById(R.id.BottomSheet));
                        adding_comment = sheetView.findViewById(R.id.adding_comment);
                        adding_comment.requestFocus();
                        final ImageView img = sheetView.findViewById(R.id.commenting_post_textview);
                        final LinearLayout linearLayout = sheetView.findViewById(R.id.reply_username);
                        final TextView usernamevisible = sheetView.findViewById(R.id.usernamevisible);
                        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("comments reply").child(postid).child(previous_commentid).child(modelCommentReply.getCommentid());
                        reference.addValueEventListener(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    ModelCommentReply modelCommentReply = snapshot.getValue(ModelCommentReply.class);
                                String publisher = modelCommentReply.getPublisher();
                                pre_comment = modelCommentReply.getComment();

                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(publisher);
                                reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user = snapshot.getValue(User.class);
                                        final String username = user.getUserName();
                                        linearLayout.setVisibility(View.VISIBLE);
                                        usernamevisible.setText("Replying to " + username + "...");
                                        img.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                performvalidations(adding_comment, username, pre_comment);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        CircleImageView comment_profile_image = sheetView.findViewById(R.id.comment_profile_image);
                        getprofileimg(comment_profile_image);
                        bottomSheetDialog.setContentView(sheetView);
                        bottomSheetDialog.show();

                        return false;
                    }
                });

                popupMenu.getMenu().findItem(R.id.report).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String commentid=modelCommentReply.getCommentid();
                        final String previous_commentid = modelCommentReply.getPrevious_commentid();
                        final String postid=modelCommentReply.getPostid();;

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
                                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("comments_reply_report");
                                    String comments_reportid= ref.push().getKey();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("comments_reportid",comments_reportid);
                                    hashMap.put("report",radioButton.getText());
                                    hashMap.put("date",mDate);
                                    ref.child(postid).child(previous_commentid).child(commentid).child(comments_reportid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        return mcommentReplyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView commentor_profile_image;
        public TextView commentorUserName, commentDate, pre_comment_usr, commentor_comment,pre_com,likes,dislikes;
        ImageView more,like,dislike;
        LinearLayout line, like_layout, dislike_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentor_profile_image = itemView.findViewById(R.id.commentor_profile_image);
            commentorUserName = itemView.findViewById(R.id.commentorUserName);
            commentDate = itemView.findViewById(R.id.commentDate);
            pre_comment_usr = itemView.findViewById(R.id.pre_comment_usr);
            commentor_comment = itemView.findViewById(R.id.commentor_comment);
            more = itemView.findViewById(R.id.more);
            line = itemView.findViewById(R.id.line);
            pre_com = itemView.findViewById(R.id.pre_com);
            like = itemView.findViewById(R.id.like);
            likes = itemView.findViewById(R.id.likes);
            dislike = itemView.findViewById(R.id.dislike);
            dislikes = itemView.findViewById(R.id.dislikes);
            like_layout = itemView.findViewById(R.id.like_layout);
            dislike_layout = itemView.findViewById(R.id.dislike_layout);
        }
    }

    private void getUserInfo(final CircleImageView circleImageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getProfileimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(circleImageView);
                username.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDisLikes(final TextView dislikes, String commentid, String previous_commentid, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments_comment dislikes").child(postid).child(previous_commentid).child(commentid);
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

    private void getLikes(final TextView likes, String commentid, String previous_commentid, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments_comment likes").child(postid).child(previous_commentid).child(commentid);
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

    private void isDisLiked(final ImageView dislike, String commentid, String previous_commentid, String postid) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("comments_comment dislikes").child(postid).child(previous_commentid).child(commentid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    dislike.setImageResource(R.drawable.ic_outline_thumb_down_blue);
                    dislike.setTag("disliked");
                }else {
                    dislike.setImageResource(R.drawable.ic_dislike_outline);
                    dislike.setTag("dislike");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isLiked(final ImageView like, String commentid, String previous_commentid, String postid) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("comments_comment likes").child(postid).child(previous_commentid).child(commentid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    like.setImageResource(R.drawable.ic_outline_thumb_up_blue);
                    like.setTag("liked");
                }else {
                    like.setImageResource(R.drawable.ic_like_outline);
                    like.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void performvalidations(EditText editText,String username, String pre_comment) {
        String commentText = editText.getText().toString();
        if (TextUtils.isEmpty(commentText)) {
            editText.setError("Please type something");
        }
        else {
            uploadComment(commentText,editText,username,pre_comment);
        }
    }

    private void uploadComment(final String comment, final EditText editText, final String username, final String pre_comment) {
        loader = new ProgressDialog(mContext);
        loader.setMessage("Uploading the Comment");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("comments reply").child(postid).child(previous_commentid);
        final String comment2id = ref.push().getKey();
        String date = DateFormat.getDateInstance().format(new Date());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment",comment);
        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("commentid",comment2id);
        hashMap.put("previous_commentid",previous_commentid);
        hashMap.put("postid",postid);
        hashMap.put("date",date);
        hashMap.put("previous_username",username);
        hashMap.put("previous_comment",pre_comment);

        ref.child(comment2id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(mContext,"Comment Posted Successfully", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    loader.dismiss();
                    editText.setText("");
                }else {
                    Toast.makeText(mContext,"error posting comment"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    loader.dismiss();
                    editText.setText("");
                }
            }
        });
    }

    private void getprofileimg(final CircleImageView comment_profile_image) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getProfileimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(comment_profile_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
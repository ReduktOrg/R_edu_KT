package com.example.r_edu_kt.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.r_edu_kt.Model.Comment;
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
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
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mCommentList;
    private String postid,selected="",name="";
    private int reportCount=0;
    private FirebaseUser firebaseUser;

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
        if(comment.getCommentimage() == null){
            holder.commentimage.setVisibility(View.GONE);
        }
        else{
            holder.commentimage.setVisibility(View.VISIBLE);
        }
        Glide.with(mContext).load(comment.getCommentimage()).into(holder.commentimage);
        holder.commentor_comment.setText(comment.getComment());
        holder.commentDate.setText(comment.getDate());
        getUserInformation(holder.commentorUserName, comment.getPublisher());

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext,v);
                popupMenu.inflate(R.menu.comment_menu);

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
                        final DialogPlus dialogPlus=DialogPlus.newDialog(holder.more.getContext())
                                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.editcomment))
                                .setExpanded(true,1100)
                                .create();

                        View myview=dialogPlus.getHolderView();
                        final ImageView cimage=myview.findViewById(R.id.coment_img);
                        final EditText comment1=myview.findViewById(R.id.comenttext);
                        Button submit=myview.findViewById(R.id.postC);
                        Button canc=myview.findViewById(R.id.cance);

                        // Intent intent = new Intent(mContext, editpost.class);
                        //((Activity)mContext).startActivityForResult(intent,requestCode);

                        Glide.with(mContext).load(comment.getCommentimage()).into(cimage);
                        if(comment.getCommentimage()==null){
                            cimage.setImageResource(R.drawable.ic_image);
                        }
                        comment1.setText(comment.getComment());

                        final String commentid=comment.getCommentid();
                        final String postid=comment.getPostid();

                        dialogPlus.show();

                        canc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogPlus.dismiss();
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
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(mContext,"comment updated successfully",Toast.LENGTH_SHORT).show();
                                                    dialogPlus.dismiss();
                                                    pd.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext,"could not update comment",Toast.LENGTH_SHORT).show();
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
                        final String commentid=comment.getCommentid();
                        final String postid = comment.getPostid();
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.more.getContext());
                        builder.setTitle("Delete Panel");
                        builder.setMessage("Delete...?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference("comments").child(postid).child(commentid).removeValue();
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
                        final String commentid=comment.getCommentid();
                        final String postid=comment.getPostid();;
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
                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("comments_report");
                                String comments_reportid= ref.push().getKey();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("comments_reportid",comments_reportid);
                                hashMap.put("report",selected);
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
        return mCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView commentor_profile_image;
        public TextView commentorUserName, commentor_comment, commentDate;
        public PhotoView commentimage;
        public ImageView more;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentor_profile_image = itemView.findViewById(R.id.commentor_profile_image);
            commentorUserName = itemView.findViewById(R.id.commentorUserName);
            commentor_comment = itemView.findViewById(R.id.commentor_comment);
            commentDate = itemView.findViewById(R.id.commentDate);
            commentimage = itemView.findViewById(R.id.retrieved_commentimage);
            more = itemView.findViewById(R.id.more);
        }
    }

    private  void getUserInformation(final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                // Glide.with(mContext).load(user.getProfileimage()).into(circleImageView);
                username.setText(user.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }
}

package com.example.r_edu_kt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.r_edu_kt.Adapters.CommentAdapter;
import com.example.r_edu_kt.Adapters.CommentReplyAdapter;
import com.example.r_edu_kt.Model.Comment;
import com.example.r_edu_kt.Model.ModelCommentReply;
import com.example.r_edu_kt.Model.Post;
import com.example.r_edu_kt.Model.User;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentReply extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView commentview, maintext;
    EditText adding_comment;
    private BottomSheetDialog bottomSheetDialog;
    CircleImageView comment_profile_image,comment_profile_img;
    LinearLayout linearLayout;
    ImageView image;

    private ProgressDialog loader;

    String postid, commentid, comment_publisher_username;

    private CommentReplyAdapter commentReplyAdapter;
    private List<ModelCommentReply> modelCommentReplyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_reply);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        commentid = intent.getStringExtra("commentid");
        comment_publisher_username = intent.getStringExtra("comment_publisher_username");
        comment_profile_img = findViewById(R.id.comment_profile_img);

        //requestQueue = Volley.newRequestQueue(this);

        commentview = findViewById(R.id.text1);
        maintext = findViewById(R.id.maintext);
        linearLayout = findViewById(R.id.linearimage);
        image = findViewById(R.id.image);
        loader = new ProgressDialog(this);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments").child(postid).child(commentid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    String com = comment.getComment();
                    commentview.setText(com);
                    if(comment.getCommentimage()!=null){
                        linearLayout.setVisibility(View.VISIBLE);
                        Glide.with(CommentReply.this).load(comment.getCommentimage()).into(image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        maintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(CommentReply.this, R.style.BottomSheetTheme);

                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet, (ViewGroup) findViewById(R.id.BottomSheet));
                adding_comment = sheetView.findViewById(R.id.adding_comment);
                adding_comment.requestFocus();
                ImageView img = sheetView.findViewById(R.id.commenting_post_textview);
                comment_profile_image = sheetView.findViewById(R.id.comment_profile_image);
                getprofileimg(comment_profile_image);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performvalidations(adding_comment);
                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });


        bottomSheetDialog = new BottomSheetDialog(CommentReply.this, R.style.BottomSheetTheme);

        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet, (ViewGroup) findViewById(R.id.BottomSheet));
        adding_comment = sheetView.findViewById(R.id.adding_comment);
        adding_comment.requestFocus();
        ImageView img = sheetView.findViewById(R.id.commenting_post_textview);
        comment_profile_image = sheetView.findViewById(R.id.comment_profile_image);
        getprofileimg(comment_profile_image);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performvalidations(adding_comment);
            }
        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();


        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        modelCommentReplyList = new ArrayList<>();
        commentReplyAdapter = new CommentReplyAdapter(CommentReply.this,modelCommentReplyList,postid,commentid);
        recyclerView.setAdapter(commentReplyAdapter);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments").child(postid).child(commentid);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       Comment comment = snapshot.getValue(Comment.class);
                        View view = LayoutInflater.from(CommentReply.this).inflate(R.layout.imagedialog,null);

                        final PhotoView img=view.findViewById(R.id.img);
                        final ImageButton close =view.findViewById(R.id.close);
                        Glide.with(img.getContext()).load(comment.getCommentimage()).fitCenter().diskCacheStrategy(DiskCacheStrategy.ALL).into(img);

                        final AlertDialog dialog = new AlertDialog.Builder(CommentReply.this)
                                .setView(view).setCancelable(false).create();
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.show();

                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CommentReply.this,error.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        ReadComments();
    }

    private void ReadComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("comments reply").child(postid).child(commentid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelCommentReplyList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelCommentReply modelCommentReply = dataSnapshot.getValue(ModelCommentReply.class);
                    modelCommentReplyList.add(modelCommentReply);
                }
                commentReplyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                 Toast.makeText(CommentReply.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getprofileimg(final CircleImageView comment_profile_image) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(CommentReply.this).load(user.getProfileimage()).into(comment_profile_image);
                Glide.with(CommentReply.this).load(user.getProfileimage()).into(comment_profile_img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void performvalidations(EditText editText) {
        String commentText = editText.getText().toString();
        if (TextUtils.isEmpty(commentText)) {
            editText.setError("please type something");
        }
        else {
            uploadComment(commentText,editText);
        }
    }

    private void uploadComment(final String comment, final EditText editText) {
        loader.setMessage("Uploading the Comment");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("comments reply").child(postid).child(commentid);
        final String comment2id = ref.push().getKey();
        String date = DateFormat.getDateInstance().format(new Date());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment",comment);
        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("commentid",comment2id);
        hashMap.put("previous_commentid",commentid);
        hashMap.put("postid",postid);
        hashMap.put("date",date);

        ref.child(comment2id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CommentReply.this,"Comment Posted Successfully", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    loader.dismiss();
                    editText.setText("");
                }else {
                    Toast.makeText(CommentReply.this,"error posting comment"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    loader.dismiss();
                    editText.setText("");
                }
            }
        });
    }
}

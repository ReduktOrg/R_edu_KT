package com.example.r_edu_kt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.r_edu_kt.Adapters.CommentAdapter;
import com.example.r_edu_kt.Model.Comment;
import com.example.r_edu_kt.Model.Post;
import com.example.r_edu_kt.Model.User;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CircleImageView circleImageView;
    private EditText editText;
    PhotoView photoView;
    ImageView commentimage,imageView;
    TextView tv;
    private String myurl = "";
    private Uri resulturi;
    StorageTask uploadTask;
    StorageReference storageReference;
    LinearLayout linearimage;

    private ProgressDialog loader;

    String postid;

    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        Intent intent=getIntent();
        postid = intent.getStringExtra("postid");


        circleImageView = findViewById(R.id.comment_profile_image);
        photoView = findViewById(R.id.image);
        commentimage=findViewById(R.id.commentimage);
        tv=findViewById(R.id.text1);
        imageView = findViewById(R.id.commenting_post_textview);
        editText = findViewById(R.id.adding_comment);
        linearimage = findViewById(R.id.linearimage);
        loader=new ProgressDialog(this);

        recyclerView = findViewById(R.id.reccycler_view);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        storageReference= FirebaseStorage.getInstance().getReference("comments");

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(CommentsActivity.this,commentList,postid);
        recyclerView.setAdapter(commentAdapter);

        commentimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performvalidations();
            }
        });
        getImage();
        getquestionImage();
        readComments();
    }

    private void performvalidations() {
        String commentText = editText.getText().toString();
        if(TextUtils.isEmpty(commentText)){
            editText.setError("please type something");
        }else if (!TextUtils.isEmpty(commentText) && resulturi == null){
            uploadCommentwithNoImage();
        } else if (!TextUtils.isEmpty(commentText) && resulturi != null){
            uploadCommentwithImage();
        }

    }

    private void startloader(){
        loader.setMessage("posting your comment");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }



    private void uploadCommentwithNoImage(){
        startloader();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("comments").child(postid);
        String commentid = ref.push().getKey();
        String date = DateFormat.getDateInstance().format(new Date());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment",editText.getText().toString());
        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("commentid",commentid);
        hashMap.put("postid",postid);
        hashMap.put("date",date);

        ref.child(commentid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CommentsActivity.this,"Comment Posted Successfully", Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                }else {
                    Toast.makeText(CommentsActivity.this,"error posting comment"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                }
                editText.setText("");
            }
        });
    }

    private void uploadCommentwithImage(){
        startloader();
        final StorageReference fileReference;
        fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(resulturi));
        uploadTask = fileReference.putFile(resulturi);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if(!task.isComplete()){
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("comments").child(postid);
                    Uri downloadUri = (Uri) task.getResult();
                    myurl=downloadUri.toString();

                    String commentid = ref.push().getKey();
                    String date = DateFormat.getDateInstance().format(new Date());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("comment",editText.getText().toString());
                    hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("commentid",commentid);
                    hashMap.put("postid",postid);
                    hashMap.put("date",date);
                    hashMap.put("commentimage",myurl);

                    ref.child(commentid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CommentsActivity.this,"Comment Posted Successfully", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }else {
                                Toast.makeText(CommentsActivity.this,"could not upload image"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                            editText.setText("");
                            commentimage.setImageResource(R.drawable.ic_image);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommentsActivity.this,"Error posting the Comment",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void  getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                //Glide.with(CommentsActivity.this).load(user.getProfileimage()).into(circleImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommentsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void  getquestionImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("questions posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post= snapshot.getValue(Post.class);
                tv.setText(post.getQuestion());
                Glide.with(CommentsActivity.this).load(post.getQuestionimage()).into(photoView);
                if(post.getQuestionimage()==null){
                    linearimage.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommentsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void readComments(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Comment comment=dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommentsActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            resulturi = data.getData();
            commentimage.setImageURI(resulturi);
        }
    }
}
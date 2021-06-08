package com.example.r_edu_kt.User.MyAccount;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.r_edu_kt.Model.User;
import com.example.r_edu_kt.R;
import com.example.r_edu_kt.User.CourseLayout.CourseOverview;
import com.example.r_edu_kt.User.Login.LoginActivity;
import com.example.r_edu_kt.User.Register.SignUp3rdClass;
import com.example.r_edu_kt.User.UserDashboard;
import com.example.r_edu_kt.discussion_home;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAccount extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;


    Button nameButton,birthdayButton,genderButton,passwordButton,emailButton,phoneButton;
    private Uri resultUri;
    private ProgressDialog loader;
    private DatabaseReference reference;
    String onlineuserid="";
    BottomSheetDialog bottomSheetDialog;

    DrawerLayout drawerLayout;
    LinearLayout contentView;
    NavigationView navigationView;
    ImageView menuIcon;
    CircleImageView pro_img,promy_img,proimg;
    TextView userFullName,user_name,mail_id,user_email_header,user_full_name_text,user_birthday,user_gender,user_password,user_email,user_phone;
    static final float END_SCALE = 0.7f;

    String userName,fullName,password,email,phoneNumber,gender,date;
    Button update_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        //Hooks for navigation
        menuIcon = findViewById(R.id.my_account_menu_icon);
        contentView = findViewById(R.id.my_account_content);
        drawerLayout = findViewById(R.id.my_account_drawer_layout);
        navigationView = findViewById(R.id.my_account_navigation_view);
        naviagationDrawer();

        //data from userdashboard
        final View header = navigationView.getHeaderView(0);
        user_name = header.findViewById(R.id.app_name);
        mail_id = header.findViewById(R.id.mail_id);
        promy_img=findViewById(R.id.promy_img);
        proimg=findViewById(R.id.proimg);
        pro_img=header.findViewById(R.id.pro_img);
        update_photo = findViewById(R.id.update_photo);

        loader = new ProgressDialog(this);

        onlineuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(onlineuserid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                Glide.with(header.getContext()).load(user.getProfileimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(pro_img);
                Glide.with(MyAccount.this).load(user.getProfileimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(promy_img);
                Glide.with(MyAccount.this).load(user.getProfileimage()).apply(new RequestOptions().override(Target.SIZE_ORIGINAL).format(DecodeFormat.PREFER_ARGB_8888)).into(proimg);
                fullName = user.getFullName();
                email=user.getEmail();
                userName = user.getUserName();
                password = user.getPassword();
                phoneNumber = user.getPhoneNo();
                gender = user.getGender();
                date = user.getDate();
                user_name.setText("Hi !\n"+fullName);
                mail_id.setText(email);
                userFullName=findViewById(R.id.user_full_name);
                userFullName.setText(fullName.toUpperCase());
                user_email_header=findViewById(R.id.user_email);
                user_email_header.setText(email);
                user_full_name_text=findViewById(R.id.name_text);
                user_full_name_text.setText(fullName);
                user_birthday=findViewById(R.id.birthday_text);
                user_birthday.setText(date);
                user_gender=findViewById(R.id.gender_text);
                user_gender.setText(gender);
                user_password=findViewById(R.id.password_text);
                user_password.setText(password);
                user_email=findViewById(R.id.email_text);
                user_email.setText(email);
                user_phone=findViewById(R.id.phone_text);
                user_phone.setText(phoneNumber);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyAccount.this,error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        //account details


        //buttons
        nameButton=findViewById(R.id.name_button);
        birthdayButton=findViewById(R.id.birthday_button);
        genderButton=findViewById(R.id.gender_button);
        passwordButton=findViewById(R.id.password_button);
        emailButton=findViewById(R.id.email_button);
        phoneButton=findViewById(R.id.phone_button);

        //button onclicks
       promy_img.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              bottomSheetDialog = new BottomSheetDialog(MyAccount.this,R.style.BottomSheetTheme);
                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.imgbottom, (ViewGroup) findViewById(R.id.BottomSheet));

                ImageView camera = sheetView.findViewById(R.id.camera);
                ImageView gallery = sheetView.findViewById(R.id.gallery);

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                                String[] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                requestPermissions(permission,PERMISSION_CODE);
                            }
                            else {
                                openCamer();
                            }
                        }
                        else {
                            openCamer();
                        }
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        Intent intent=new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1);
                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.setCanceledOnTouchOutside(false);
                bottomSheetDialog.show();
           }
       });


        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nameIntent=new Intent(getApplicationContext(),MyAccountName.class);
                startActivity(nameIntent);
                in_animation();
            }
        });

        birthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dateIntent=new Intent(getApplicationContext(),MyAccountBirthday.class);
                startActivity(dateIntent);
                in_animation();
            }
        });

        genderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent genderIntent=new Intent(getApplicationContext(),MyAccountGender.class);
                startActivity(genderIntent);
                in_animation();
            }
        });

        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passwordIntent=new Intent(getApplicationContext(),MyAccountPassword.class);
                startActivity(passwordIntent);
                in_animation();
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent=new Intent(getApplicationContext(),MyAccountEmail.class);
                emailIntent.putExtra("email",email);
                startActivity(emailIntent);
                in_animation();
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent=new Intent(getApplicationContext(),MyAccountPhone.class);
                phoneIntent.putExtra("phoneNumber",phoneNumber);
                startActivity(phoneIntent);
                in_animation();
            }
        });

        update_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_image();
                update_photo.setVisibility(View.GONE);
            }
        });


    }

    private void openCamer() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
        resultUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,resultUri);
        startActivityForResult(cameraintent,IMAGE_CAPTURE_CODE);
    }


    private void update_image() {
        loader.setMessage("updating profile image");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile images").child(onlineuserid);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        final Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageuri = uri.toString();
                                Map hashMap = new HashMap();
                                hashMap.put("profileimage", imageuri);
                                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineuserid);
                                reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MyAccount.this, "profile image updated successfully", Toast.LENGTH_SHORT).show();
                                            loader.dismiss();
                                        } else {
                                            Toast.makeText(MyAccount.this, "process failed" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            loader.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
            update_photo.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            resultUri = data.getData();
            promy_img.setImageURI(resultUri);
            update_photo.setVisibility(View.VISIBLE);
        }
        else if(requestCode == 1001 && resultCode == RESULT_OK){
            promy_img.setImageURI(resultUri);
            update_photo.setVisibility(View.VISIBLE);
        }
    }


    private void in_animation() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void naviagationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigateDrawer();
    }

    private void animateNavigateDrawer() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.colorAccent));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_discussion:
                Intent intent = new Intent(MyAccount.this, discussion_home.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.nav_home:
                Intent intent1 = new Intent(MyAccount.this,UserDashboard.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.nav_profile:
                Intent account_intent=new Intent(getApplicationContext(), MyAccount.class);
                startActivity(account_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.nav_contact_us:
                String[] redukt_mail={"teamredukt@gmail.com"};
                String mailSubject="Help needed for "+fullName.toUpperCase();
                String chooseOne="Choose one email application";
                Intent mailIntent=new Intent(Intent.ACTION_SENDTO);
                mailIntent.setData(Uri.parse("mailto:"));
                mailIntent.putExtra(Intent.EXTRA_EMAIL,redukt_mail);
                mailIntent.putExtra(Intent.EXTRA_SUBJECT,mailSubject);
                startActivity(Intent.createChooser(mailIntent,chooseOne));
                break;

            case R.id.nav_logout:
                View view = LayoutInflater.from(this).inflate(R.layout.logoutdialog,null);

                Button submit=view.findViewById(R.id.postl);
                Button canc=view.findViewById(R.id.cancell);

                final AlertDialog dialog = new AlertDialog.Builder(this)
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
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(MyAccount.this, LoginActivity.class);
                        dialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                });
                break;
        }
        return true;
    }

    private void intent_to_dashboard() {
        Intent dashboard_intent = new Intent(getApplicationContext(), UserDashboard.class);
        startActivity(dashboard_intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }
}
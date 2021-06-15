package com.example.r_edu_kt.User.Quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.example.r_edu_kt.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuizIntro extends AppCompatActivity {


    public static ArrayList<ModelClass> list;
    DatabaseReference databaseReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_intro);

        //STATUS BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.quiz_blue));
        }

        //questions
        list = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Quiz_questions");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ModelClass modelClass = dataSnapshot.getValue(ModelClass.class);
                    list.add(modelClass);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent(getApplicationContext(),QuizDashboard.class);
                        startActivity(intent);
                    }
                },3000);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       // list.add(new ModelClass("Approximately what fraction of the world’s population lives in India?", "1/6", "1/10", "1/50", "1/3", "1/6"));
       // list.add(new ModelClass("Which of the following countries does not share a land border with India?", "Myanmar", "Afghanistan", "China", "Bhutan", "Afghanistan"));
      //  list.add(new ModelClass("Approximately what fraction of the world’s population lives in India?", "Peak of heaven", "Abode of snow", "Rocky mountains", "The high one", "Abode of snow"));
        //list.add(new ModelClass("Which Indian city is home to the Taj Mahal?", "Mumbai", "Chennai", "Agra", "Delhi", "Agra"));
      //  list.add(new ModelClass("What is the name of the tallest mountain in India?", "Damāvand", "Kanchenjunga", "Elbrus", "Everest", "Kanchenjunga"));
       // list.add(new ModelClass("Which of the following cities has the largest population?", "Kolkata", "Hyderabad", "Mumbai", "Chennai", "Mumbai"));
       // list.add(new ModelClass("Roughly what percent of Indians work in agriculture?", "33%", "80%", "10%", "50%", "50%"));
     //   list.add(new ModelClass("Which mountains form the eastern and western edges of the Deccan plateau?", "Eastern and Western Ghats", "Hindu Kush", "Urals", "Himalayas", "Eastern and Western Ghats"));
       // list.add(new ModelClass("What is the name of the boundary that separated India from Pakistan, set in 1947?", "Radcliffe Line", "Plimsoll Line", "Durand Line", "Green Line", "Radcliffe Line"));
      //  list.add(new ModelClass("The name of which Indian union territory means a Hundred Thousand Islands in Sanskrit?", "Lakshadweep", "Puducherry", "Delhi", "Chandigarh", "Lakshadweep"));
    }
}
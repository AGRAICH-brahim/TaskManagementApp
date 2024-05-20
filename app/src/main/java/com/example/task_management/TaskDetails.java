package com.example.task_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.task_management.crud.UpdateTask;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaskDetails extends AppCompatActivity {
    ImageView detailImage;
    TextView detailTitle;
    TextView detailDescrip;
    TextView detailDeadline;
    TextView detailTime;
    String imageUrl = "";
    FloatingActionButton deleteButton, editButton, doneButton;
    FirebaseFirestore db;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailDescrip = findViewById(R.id.detailDesc);
        detailDeadline = findViewById(R.id.detailDate);
        detailTime = findViewById(R.id.detailTime);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance(); // Initialiser l'authentification Firebase

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
            detailTitle.setText(bundle.getString("title"));
            detailDescrip.setText(bundle.getString("description"));
            detailDeadline.setText(bundle.getString("deadline"));
            detailTime.setText(bundle.getString("Time"));
            imageUrl = bundle.getString("Image");
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("User").document(auth.getCurrentUser().getEmail()).collection("Tasks").document(detailTitle.getText().toString()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(TaskDetails.this,"task deleted",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(TaskDetails.this,TasksActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TaskDetails.this, "Error deleting contact", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskDetails.this, UpdateTask.class);
                intent.putExtra("ETitle", detailTitle.getText().toString());
                intent.putExtra("EDescription", detailDescrip.getText().toString());
                intent.putExtra("EDate", detailDeadline.getText().toString());
                intent.putExtra("ETime", detailDeadline.getText().toString());
                intent.putExtra("EImage", imageUrl);
                startActivity(intent);
            }
        });


    }

    private void deleteTask(String title) {
        db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("Tasks")
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            db.collection("Tasks").document(documentSnapshot.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(TaskDetails.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                                        finish(); // Close the activity after deletion
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(TaskDetails.this, "Error deleting task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(TaskDetails.this, "Task not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TaskDetails.this, "Error finding task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

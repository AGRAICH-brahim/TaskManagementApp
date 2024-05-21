package com.example.task_management;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.task_management.crud.UpdateTask;
import com.github.clans.fab.FloatingActionButton;
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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
            detailTitle.setText(bundle.getString("title"));
            detailDescrip.setText(bundle.getString("description"));
            detailDeadline.setText(bundle.getString("deadline"));
            detailTime.setText(bundle.getString("Time"));
            imageUrl = bundle.getString("Image");
        }

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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTask(detailTitle.getText().toString());
            }
        });
    }

    private void deleteTask(String title) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        db.collection("User").document(userEmail).collection("Tasks")
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TaskDetails.this);
                        builder.setCancelable(false);
                        builder.setView(R.layout.progress_layout);

                        if (!isFinishing() && !isDestroyed()) {
                            AlertDialog dialog = builder.create();
                            dialog.show();

                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                // Utiliser la bonne référence pour supprimer le document de la collection "Tasks" sous "User"
                                db.collection("User").document(userEmail).collection("Tasks")
                                        .document(documentSnapshot.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(TaskDetails.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss(); // Dismiss the dialog after deletion
                                            finish(); // Close the activity after deletion
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(TaskDetails.this, "Error deleting task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss(); // Dismiss the dialog on failure
                                        });
                            }
                        } else {
                            Toast.makeText(TaskDetails.this, "Cannot show dialog, activity is finishing or destroyed", Toast.LENGTH_SHORT).show();
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

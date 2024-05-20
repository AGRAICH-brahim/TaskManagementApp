package com.example.task_management.crud;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.task_management.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {
    ImageButton uploadButton;
    EditText  uploadTitle, uploadDetails ;

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        uploadTitle = findViewById(R.id.notes_title_text);
        uploadDetails = findViewById(R.id.notes_content_text);
        uploadButton = findViewById(R.id.save_note_btn);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }
    private void saveData() {
        String title = uploadTitle.getText().toString().trim();
        String description = uploadDetails.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();

        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("description", description);

        db.collection("User").document(userEmail).collection("Notes").add(note)
                .addOnSuccessListener(documentReference -> {
                    String documentId = documentReference.getId();
                    Toast.makeText(AddNote.this, "Note added successfully with ID: ", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after successful addition
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddNote.this, "Error adding note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
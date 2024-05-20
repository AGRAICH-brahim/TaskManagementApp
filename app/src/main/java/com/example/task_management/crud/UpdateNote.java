package com.example.task_management.crud;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.task_management.R;
import com.example.task_management.TasksActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateNote extends AppCompatActivity {
    private static final String TAG = "UpdateNote";

    ImageButton uploadButton;
    EditText editTitle, editDetails;
    TextView idNote;
    Button deleteButton;  // Add delete button

    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        editTitle = findViewById(R.id.notes_title_text);
        editDetails = findViewById(R.id.notes_content_text);
        uploadButton = findViewById(R.id.save_note_btn);
        idNote = findViewById(R.id.Note_id);
        deleteButton = findViewById(R.id.delete_note_btn);  // Initialize delete button

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idNote.setText(bundle.getString("ID"));
            editTitle.setText(bundle.getString("title"));
            editDetails.setText(bundle.getString("description"));
        }

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });
    }

    private void updateData() {
        String title = editTitle.getText().toString();
        String description = editDetails.getText().toString();
        String noteId = idNote.getText().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(noteId)) {
            Toast.makeText(this, "Note ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("description", description);

        DocumentReference noteRef = db.collection("User").document(auth.getCurrentUser().getEmail()).collection("Notes").document(noteId);
        noteRef.update(note)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UpdateNote.this, "Updated", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UpdateNote.this, TasksActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateNote.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteNote() {
        String noteId = idNote.getText().toString();

        if (TextUtils.isEmpty(noteId)) {
            Toast.makeText(this, "Note ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference noteRef = db.collection("User").document(auth.getCurrentUser().getEmail()).collection("Notes").document(noteId);
        noteRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateNote.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UpdateNote.this, TasksActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateNote.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

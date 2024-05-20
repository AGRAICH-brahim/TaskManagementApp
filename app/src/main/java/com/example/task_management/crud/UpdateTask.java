package com.example.task_management.crud;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.task_management.R;
import com.example.task_management.TasksActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateTask extends AppCompatActivity {

    ImageView updateImage;
    EditText updateTitle;
    String oldTitle;
    EditText updateDescription;
    EditText updateDate;
    EditText updateTime;
    AppCompatButton updateButton;
    Uri uri;
    String imageUrl;
    String oldImageUrl;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);


        updateImage = findViewById(R.id.updateTaskImage);
        updateTitle = findViewById(R.id.updateTaskTitle);
        updateDescription = findViewById(R.id.updateTaskDescription);
        updateDate = findViewById(R.id.updateTaskDate);
        updateTime = findViewById(R.id.updateTaskTime);
        updateButton = findViewById(R.id.updatedButton);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        }else {
                            Toast.makeText(UpdateTask.this,"No image Selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Glide.with(UpdateTask.this).load(bundle.getString("EImage")).into(updateImage);
            updateTitle.setText(bundle.getString("ETitle"));
            oldTitle = bundle.getString("ETitle");
            updateDescription.setText(bundle.getString("EDescription"));
            updateDate.setText(bundle.getString("EDate"));
            updateTime.setText(bundle.getString("ETime"));
            oldImageUrl = bundle.getString("EImage");
        }

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateData();
            }
        });
    }
    private void updateData() {
        String editTitle = updateTitle.getText().toString();
        String editDescription = updateDescription.getText().toString();
        String editDate = updateDate.getText().toString();
        String editTime = updateTime.getText().toString();

        if(TextUtils.isEmpty(editTitle) || TextUtils.isEmpty(editDescription) || TextUtils.isEmpty(editDate) || TextUtils.isEmpty(editTime)){
            Toast.makeText(this,"please fill all the required fields",Toast.LENGTH_SHORT).show();
            return;
        }
        uploadImage();
    }

    private void uploadImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Task Image").child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateTask.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask =taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri UrlImage = uriTask.getResult();
                imageUrl = UrlImage.toString();
                uploadUpdatedData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(UpdateTask.this, "Failed to add task", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadUpdatedData() {

        String editTitle = updateTitle.getText().toString();
        String editDescription = updateDescription.getText().toString();
        String editDeadline = updateDate.getText().toString();
        String editTime = updateTime.getText().toString();


        db.collection("User").document(auth.getCurrentUser().getEmail()).collection("Tasks").document(oldTitle)
                .update("title",editTitle,
                        "description",editDescription,
                        "deadline",editDeadline,
                        "Time", editTime,
                        "Image",imageUrl)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UpdateTask.this, "Updated", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UpdateTask.this, TasksActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateTask.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
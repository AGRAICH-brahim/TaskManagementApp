package com.example.task_management.crud;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.task_management.AlarmReceiver;
import com.example.task_management.R;
import com.example.task_management.TasksActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.TextUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;


public class AddTask extends AppCompatActivity {

    ImageView uploadImage;
    Button uploadButton;
    EditText uploadDesc, uploadTitle, uploadDate, uploadTime;
    String title, desc, Date;
    String imageURL;
    String key, oldImageURL;
    Uri uri;
    int mYear, mMonth, mDay;

    FirebaseFirestore db;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        uploadButton = findViewById(R.id.saveButton);
        uploadDesc = findViewById(R.id.addTaskDescription);
        uploadImage = findViewById(R.id.uploadImage);
        uploadDate = findViewById(R.id.taskDate);
        uploadTime = findViewById(R.id.taskTime);
        uploadTitle = findViewById(R.id.addTaskTitle);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            if (data != null) {
                                uri = data.getData();
                                uploadImage.setImageURI(uri);
                            } else {
                                Toast.makeText(AddTask.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AddTask.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("AddTask", "Upload image button clicked");
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        uploadDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDatePicker(); // Open date picker dialog
            }
        });
        uploadTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openTimePicker(); // Open date picker dialog
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }
    private void openDatePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                //Showing the picked value in the textView
                uploadDate.setText(String.valueOf(year)+ "-"+String.valueOf(month)+ "-"+String.valueOf(day));

            }
        }, 2024, 05, 20);

        datePickerDialog.show();
    }
    private void openTimePicker(){

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {


                //Showing the picked value in the textView
                uploadTime.setText(String.valueOf(hour)+ ":"+String.valueOf(minute));

            }
        }, 15, 30, false);

        timePickerDialog.show();
    }

    public void saveDataa(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTask.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }
    private void saveData() {
        String title = uploadTitle.getText().toString();
        String desc = uploadDesc.getText().toString();
        String Date = uploadDate.getText().toString();
        String Time = uploadTime.getText().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(Date) || TextUtils.isEmpty(Time) || uri == null) {
            Toast.makeText(AddTask.this, "Please fill all the required fields and select an image", Toast.LENGTH_LONG).show();
            return;
        }

        uploadImage();
    }

    private void uploadImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Task Image").child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTask.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isComplete());
            Uri urlImage = uriTask.getResult();
            imageURL = urlImage.toString();
            uploadData();
            dialog.dismiss();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(AddTask.this, "Failed to add task", Toast.LENGTH_LONG).show();
        });
    }

    private void uploadData() {
        String title = uploadTitle.getText().toString();
        String desc = uploadDesc.getText().toString();
        String Date = uploadDate.getText().toString();
        String Time = uploadTime.getText().toString();

        HashMap<String, Object> user = new HashMap<>();
        user.put("title", title);
        user.put("description", desc);
        user.put("deadline", Date);
        user.put("Time", Time);
        user.put("Image", imageURL);
        user.put("owner",auth.getCurrentUser().getEmail());
        user.put("done",false);

        db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("Tasks").document(title)
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddTask.this, "Task added", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddTask.this, TasksActivity.class);
                        startActivity(intent); // Navigate back to the home page
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddTask.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        // Planification de l'alarme
        scheduleAlarm(Date, Time);
    }

    private void scheduleAlarm(String date, String time) {
        // Convertir la date et l'heure en millisecondes
        // Vous devez implémenter cette logique pour convertir la date et l'heure en millisecondes
        long alarmTime = convertDateTimeToMillis(date, time);

        // Créer une intention pour l'alarme
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("task_title", title); // Ajoutez des données supplémentaires si nécessaire

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Obtenez le gestionnaire d'alarme
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Planifiez l'alarme
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
    }
    private long convertDateTimeToMillis(String date, String time) {
        try {
            // Divisez la date en année, mois et jour
            String[] dateParts = date.split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Le mois commence à partir de 0
            int day = Integer.parseInt(dateParts[2]);

            // Divisez le temps en heure et minute
            String[] timeParts = time.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // Créez une instance de Calendar et définissez la date et l'heure
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute);

            // Retournez le temps en millisecondes
            return calendar.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // En cas d'erreur, retournez -1
        }
    }

}
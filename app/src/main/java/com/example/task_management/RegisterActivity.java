package com.example.task_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, name;
    private Button signupButton;
    private TextView loginRedirectText;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        signupEmail = findViewById(R.id.SignupEmail);
        signupPassword = findViewById(R.id.password);
        name = findViewById(R.id.name);
        signupButton = findViewById(R.id.signupButton);
        loginRedirectText = findViewById(R.id.loginRedirectText);


        if (auth.getCurrentUser() != null){
            startActivity(new Intent(RegisterActivity.this,TasksActivity.class));
            finish();
        }

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = signupEmail.getText().toString().trim();
                String userPassword = signupPassword.getText().toString().trim();
                String userName = name.getText().toString().trim();

                if (userName.isEmpty()){
                    name.setError("Name cannot be empty");
                    return;
                }

                if (userEmail.isEmpty()){
                    signupEmail.setError("Email cannot be empty");
                    return;
                }

                if (userPassword.isEmpty()){
                    signupPassword.setError("Password cannot be empty");
                    return;
                }

                // Création de l'utilisateur avec e-mail et mot de passe
                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Succès de l'enregistrement de l'utilisateur
                                    Toast.makeText(RegisterActivity.this, "Sign up Successful", Toast.LENGTH_SHORT).show();

                                    // Enregistrement des données de l'utilisateur dans Firestore
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("Username", userName);
                                    user.put("Email", userEmail);
                                    user.put("Password", userPassword);

                                    db.collection("User").document(auth.getCurrentUser().getEmail()).set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(RegisterActivity.this, "Data saved successfully", Toast.LENGTH_LONG).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegisterActivity.this, "Failed to save data to Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });

                                    // Redirection vers l'activité d'authentification
                                    startActivity(new Intent(RegisterActivity.this, AuthActivity.class));
                                } else {
                                    // Échec de l'enregistrement de l'utilisateur
                                    Toast.makeText(RegisterActivity.this, "Sign up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Redirection vers l'activité d'authentification lorsque le texte de connexion est cliqué
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, AuthActivity.class));
            }
        });
    }
}

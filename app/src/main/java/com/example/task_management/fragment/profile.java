package com.example.task_management.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.task_management.AuthActivity; // Assurez-vous d'importer la bonne activité
import com.example.task_management.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends Fragment {

    TextView userNameView, emailView;
    ImageView logoutImageView; // Ajouter cette ligne
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        userNameView = rootView.findViewById(R.id.tv_name);
        emailView = rootView.findViewById(R.id.emailUser);
        logoutImageView = rootView.findViewById(R.id.logoutImageView); // Ajouter cette ligne

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String userEmail = auth.getCurrentUser().getEmail();
        if (userEmail != null) {
            DocumentReference docRef = db.collection("User").document(userEmail);
            docRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String userName = document.getString("Username");
                                    String email = document.getString("Email");

                                    userNameView.setText(userName != null ? userName : "");
                                    emailView.setText(email != null ? email : "");
                                } else {
                                    Toast.makeText(getContext(), "User data not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Error fetching user data: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Handle case where userEmail is null
            Toast.makeText(getContext(), "No authenticated user found", Toast.LENGTH_SHORT).show();
        }

        // Set onClickListener for logout
        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getActivity(), AuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return rootView;
    }
}

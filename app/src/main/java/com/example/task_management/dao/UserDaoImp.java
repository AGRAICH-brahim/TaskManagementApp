package com.example.task_management.dao;

import com.example.task_management.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDaoImp implements UserDao {
    private FirebaseFirestore db;

    public UserDaoImp() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void insertUser(User user, InsertUserCallback onCompleteListener) {
        db.collection("users").document(user.getEmail()).set(user)
                .addOnCompleteListener((OnCompleteListener<Void>) onCompleteListener);
    }

    @Override
    public void getUserByEmail(String email, OnSuccessListener<DocumentSnapshot> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("users").document(email).get()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
}

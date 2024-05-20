package com.example.task_management.dao;

import com.example.task_management.entity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
public interface UserDao {
    void insertUser(User user, InsertUserCallback onCompleteListener);
    void getUserByEmail(String email, OnSuccessListener<DocumentSnapshot> onSuccessListener, OnFailureListener onFailureListener);

    public interface InsertUserCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}

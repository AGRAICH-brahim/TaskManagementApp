package com.example.task_management.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.adapter.AdapterNote;
import com.example.task_management.entity.DataNote;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListNote extends Fragment {
    private List<DataNote> listNotes;
    private RecyclerView noteRecyclerView;
    private AdapterNote adapter;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public ListNote() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note, container, false);

        noteRecyclerView = rootView.findViewById(R.id.noteRecyclerView);
        listNotes = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        getNotes();

        return rootView;
    }

    private void getNotes() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            DocumentReference docRef = db.collection("User").document(currentUser.getEmail());

            docRef.collection("Notes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DataNote note = new DataNote(
                                    document.getId(),
                                    document.getString("title"),
                                    document.getString("description")
                            );
                            listNotes.add(note);
                        }
                        setupRecyclerView();
                    } else {
                        Log.d("ListNote", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        noteRecyclerView.setLayoutManager(layoutManager);
        adapter = new AdapterNote(getActivity(), listNotes);
        noteRecyclerView.setAdapter(adapter);
    }
}

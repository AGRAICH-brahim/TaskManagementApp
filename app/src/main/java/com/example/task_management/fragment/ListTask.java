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
import com.example.task_management.adapter.MyAdapter;
import com.example.task_management.entity.DataTask;
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

public class ListTask extends Fragment {
    private List<DataTask> listTasks;
    private List<DataTask> filteredTasks; // Ajouter cette ligne
    private RecyclerView taskRecyclerView;
    private MyAdapter adapter;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public ListTask() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task, container, false);

        taskRecyclerView = rootView.findViewById(R.id.taskRecyclerView);
        listTasks = new ArrayList<>();
        filteredTasks = new ArrayList<>(); // Initialiser la liste filtrée
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        getTasks();

        return rootView;
    }

    private void getTasks() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            DocumentReference docRef = db.collection("User").document(currentUser.getEmail());

            docRef.collection("Tasks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DataTask tsk = new DataTask(
                                    document.getString("title"),
                                    document.getString("description"),
                                    document.getString("deadline"),
                                    document.getString("Time"),
                                    document.getString("Image")
                            );
                            listTasks.add(tsk);
                        }
                        filteredTasks.addAll(listTasks); // Ajouter toutes les tâches à la liste filtrée
                        setupRecyclerView();
                    } else {
                        Log.d("ListTask", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        taskRecyclerView.setLayoutManager(layoutManager);
        adapter = new MyAdapter(getActivity(), filteredTasks); // Utiliser la liste filtrée
        taskRecyclerView.setAdapter(adapter);
    }

    // Méthode pour filtrer les tâches en fonction du texte de recherche
    public void filterTasks(String searchText) {
        filteredTasks.clear();
        if (searchText.isEmpty()) {
            filteredTasks.addAll(listTasks); // Si la recherche est vide, afficher toutes les tâches
        } else {
            // Parcourir toutes les tâches pour trouver celles qui correspondent au texte de recherche
            for (DataTask task : listTasks) {
                if (task.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredTasks.add(task);
                }
            }
        }
        adapter.notifyDataSetChanged(); // Rafraîchir l'affichage de la liste
    }
}

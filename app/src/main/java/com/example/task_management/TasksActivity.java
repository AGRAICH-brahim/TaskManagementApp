package com.example.task_management;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.example.task_management.crud.AddNote;
import com.example.task_management.crud.AddTask;
import com.example.task_management.databinding.ActivityMainBinding;
import com.example.task_management.entity.DataTask;
import com.example.task_management.fragment.ListNote;
import com.example.task_management.fragment.ListTask;
import com.example.task_management.fragment.profile;
import com.example.task_management.fragment.settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TasksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    private int currentItemId = R.id.tasks; // Initialise avec l'ID par défaut

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ListTask listTaskFragment; // Ajouter cette ligne

    private ActivityMainBinding binding;
    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        frameLayout = findViewById(R.id.frame_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            listTaskFragment = new ListTask(); // Ajouter cette ligne
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, listTaskFragment).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                currentItemId = itemId; // Met à jour l'ID courant

                if (itemId == R.id.tasks) {
                    listTaskFragment = new ListTask(); // Ajouter cette ligne
                    replaceFragment(listTaskFragment, false);
                } else if (itemId == R.id.note) {
                    replaceFragment(new ListNote(), false);
                } else if (itemId == R.id.settings) {
                    replaceFragment(new settings(), false);
                } else { // nav profile
                    replaceFragment(new profile(), false);
                }
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;

                if (currentItemId == R.id.tasks) {
                    intent = new Intent(TasksActivity.this, AddTask.class);
                    startActivity(intent);
                }

                if (currentItemId == R.id.note) {
                    intent = new Intent(TasksActivity.this, AddNote.class);
                    startActivity(intent);
                }
            }
        });

        // Initialiser Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Créer le canal de notification
        createNotificationChannel();

        // Charger les tâches et configurer les alarmes
        loadTasksAndSetAlarms();
    }



    private void replaceFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frame_layout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frame_layout, fragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topbar, menu);

        // Recherchez l'élément SearchView dans le menu
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Configurez l'écouteur pour les changements de texte
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (listTaskFragment != null) {
                    listTaskFragment.filterTasks(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (listTaskFragment != null) {
                    listTaskFragment.filterTasks(newText);
                }
                return false;
            }
        });

        return true;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemed = item.getItemId();
        if (itemed == R.id.nav_logout) {
            Toast.makeText(this,"logout",Toast.LENGTH_SHORT).show();
            auth.signOut();
            Intent intent = new Intent(TasksActivity.this, AuthActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.calendar) {
            showCalendarBottomSheet();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCalendarBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(TasksActivity.this);
        View view = LayoutInflater.from(TasksActivity.this).inflate(R.layout.fragment_calendar_view, null);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

        ImageView backBtn = view.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Toast.makeText(TasksActivity.this, "Bottom sheet dismissed", Toast.LENGTH_SHORT).show();
            }
        });

        // Récupérer et afficher les tâches sur le calendrier
        loadTasksIntoCalendar(view);
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "akchannel";
            String desc = "Channel for Alarm Manager";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("androidknowledge", name, imp);
            channel.setDescription(desc);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void loadTasksIntoCalendar(View view) {
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            DocumentReference docRef = db.collection("User").document(currentUser.getEmail());

            docRef.collection("Tasks").get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<EventDay> events = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DataTask tsk = new DataTask(
                                    document.getString("title"),
                                    document.getString("description"),
                                    document.getString("deadline"),
                                    document.getString("Time"),
                                    document.getString("Image")
                            );
                            // Convertir la date de la tâche en Calendar
                            String[] dateParts = tsk.getDeadline().split("-");
                            int year = Integer.parseInt(dateParts[0]);
                            int month = Integer.parseInt(dateParts[1]) - 1; // Les mois sont indexés à partir de 0 dans Calendar
                            int day = Integer.parseInt(dateParts[2]);

                            // Convertir l'heure de la tâche en Calendar
                            String[] timeParts = tsk.getTime().split(":");
                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            Calendar taskDate = Calendar.getInstance();
                            taskDate.set(year, month, day, hour, minute);

                            // Ajouter l'événement au calendrier
                            events.add(new EventDay(taskDate, R.drawable.ic_event));

                            // Configurer l'alarme pour la tâche
                            setTaskAlarm(taskDate, tsk.getTitle(), tsk.getDescription());
                        }

                        // Ajouter les événements au calendrier
                        calendarView.setEvents(events);
                    } else {
                        Log.d("TasksActivity", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private void setTaskAlarm(Calendar taskDate, String taskTitle, String taskDescription) {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(TasksActivity.this, AlarmReceiver.class);
        intent.putExtra("taskTitle", taskTitle);
        intent.putExtra("taskDescription", taskDescription);

        if (ContextCompat.checkSelfPermission(TasksActivity.this, Manifest.permission.RECEIVE_BOOT_COMPLETED) == PackageManager.PERMISSION_GRANTED) {
            // La permission est accordée, vous pouvez créer le PendingIntent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(TasksActivity.this, (int) taskDate.getTimeInMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            // La permission n'est pas accordée, vous devez demander la permission à l'utilisateur
            // ou afficher un message d'erreur ou prendre une autre mesure appropriée
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, taskDate.getTimeInMillis(), pendingIntent);
    }

    private void loadTasksAndSetAlarms() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            DocumentReference docRef = db.collection("User").document(currentUser.getEmail());

            docRef.collection("Tasks").get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DataTask tsk = new DataTask(
                                    document.getString("title"),
                                    document.getString("description"),
                                    document.getString("deadline"),
                                    document.getString("Time"),
                                    document.getString("Image")
                            );

                            // Convertir la date de la tâche en Calendar
                            String[] dateParts = tsk.getDeadline().split("-");
                            int year = Integer.parseInt(dateParts[0]);
                            int month = Integer.parseInt(dateParts[1]) ; // Les mois sont indexés à partir de 0 dans Calendar
                            int day = Integer.parseInt(dateParts[2]);

                            // Convertir l'heure de la tâche en Calendar
                            String[] timeParts = tsk.getTime().split(":");
                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            Calendar taskDate = Calendar.getInstance();
                            taskDate.set(year, month, day, hour, minute);

                            // Configurer l'alarme pour la tâche
                            setTaskAlarm(taskDate, tsk.getTitle(), tsk.getDescription());
                        }
                    } else {
                        Log.d("TasksActivity", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_add_task);

        LinearLayout videoLayout = dialog.findViewById(R.id.mainlayout);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}

package com.example.task_management.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.task_management.R;
import com.example.task_management.TaskDetails;
import com.example.task_management.entity.DataTask;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataTask> listDataTask;

    public MyAdapter(Context context, List<DataTask> listDataTask) {
        this.context = context;
        this.listDataTask = listDataTask;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Glide.with(context).load(listDataTask.get(position).getImg()).into(holder.recImage);
        holder.recTitle.setText(listDataTask.get(position).getTitle());
        holder.recDesc.setText(listDataTask.get(position).getDescription());
        holder.recDeadline.setText(listDataTask.get(position).getDeadline());
        holder.recTime.setText(listDataTask.get(position).getTime());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    DataTask currentTask = listDataTask.get(adapterPosition);
                    Intent intent = new Intent(context, TaskDetails.class);
                    intent.putExtra("Image", currentTask.getImg());
                    intent.putExtra("title", currentTask.getTitle());
                    intent.putExtra("description", currentTask.getDescription());
                    intent.putExtra("deadline", currentTask.getDeadline());
                    intent.putExtra("Time", currentTask.getTime());
                    context.startActivity(intent);
                }
            }
        });

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    showPopupMenu(view, adapterPosition);
                }
            }
        });
    }

    private void showPopupMenu(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int adapterPosition = position; // Re-validate position for safety

                return false;
            }
        });
        popupMenu.show();
    }

    private void editTask(int position) {
        // Implement edit task functionality
        Toast.makeText(context, "Edit Task: " + listDataTask.get(position).getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void deleteTask(int position) {
        // Implement delete task functionality
        Toast.makeText(context, "Delete Task: " + listDataTask.get(position).getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return listDataTask.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage;
    TextView recTitle;
    TextView recDesc;
    TextView recDeadline;
    TextView recTime;
    CardView recCard;
    ImageView options;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recTitle = itemView.findViewById(R.id.recTitle);
        recDesc = itemView.findViewById(R.id.recDesc);
        recDeadline = itemView.findViewById(R.id.recDate);
        recTime = itemView.findViewById(R.id.time);
        recCard = itemView.findViewById(R.id.recCard);
        options = itemView.findViewById(R.id.options);
    }
}

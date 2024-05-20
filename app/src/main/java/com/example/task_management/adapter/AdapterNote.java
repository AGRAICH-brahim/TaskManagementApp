package com.example.task_management.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.crud.UpdateNote;
import com.example.task_management.entity.DataNote;

import java.util.List;

public class AdapterNote extends RecyclerView.Adapter<AdapterNote.ViewHolder> {

    private Context context;
    private List<DataNote> listNotes;

    public AdapterNote(Context context, List<DataNote> listNotes) {
        this.context = context;
        this.listNotes = listNotes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataNote note = listNotes.get(position);
        holder.noteTitleTextView.setText(note.getTitle());
        holder.noteContentTextView.setText(note.getDescription());
        holder.noteTimestampTextView.setText(note.getId()); // Affichez l'ID si nécessaire

        holder.recCardN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                Toast.makeText(context, "Going to detail Activity", Toast.LENGTH_SHORT).show();
                intent = new Intent(context, UpdateNote.class);
                intent.putExtra("ID",listNotes.get(holder.getAdapterPosition()).getId());
                intent.putExtra("title",listNotes.get(holder.getAdapterPosition()).getTitle());
                intent.putExtra("description",listNotes.get(holder.getAdapterPosition()).getDescription());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitleTextView;
        TextView noteContentTextView;
        TextView noteTimestampTextView;
        LinearLayout recCardN;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitleTextView = itemView.findViewById(R.id.note_title_text_view);
            noteContentTextView = itemView.findViewById(R.id.note_content_text_view);
            noteTimestampTextView = itemView.findViewById(R.id.note_timestamp_text_view);
            recCardN = itemView.findViewById(R.id.recCardN);
        }
    }
}

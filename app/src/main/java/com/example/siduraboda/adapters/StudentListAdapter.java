package com.example.siduraboda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siduraboda.R;
import com.example.siduraboda.models.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ViewHolder> {


    public interface OnClickListener {
        void onClick(Student student);
        void onLongClick(Student student);
    }

    private final List<Student> studentList;
    private final OnClickListener onClickListener;
    public StudentListAdapter(@Nullable final OnClickListener onClickListener) {
        studentList = new ArrayList<>();
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public StudentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        if (student == null) return;

        holder.tvName.setText(student.getName());

        holder.itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(student);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onLongClick(student);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void setList(List<Student> users) {
        studentList.clear();
        studentList.addAll(users);
        notifyDataSetChanged();
    }

    public void add(Student student) {
        studentList.add(student);
        notifyItemInserted(studentList.size() - 1);
    }
    public void update(Student student) {
        int index = studentList.indexOf(student);
        if (index == -1) return;
        studentList.set(index, student);
        notifyItemChanged(index);
    }

    public void remove(Student student) {
        int index = studentList.indexOf(student);
        if (index == -1) return;
        studentList.remove(index);
        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_student_name);
        }
    }
}
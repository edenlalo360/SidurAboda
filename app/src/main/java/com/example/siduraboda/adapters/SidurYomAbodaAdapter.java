package com.example.siduraboda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siduraboda.R;
import com.example.siduraboda.models.Lesson;
import com.example.siduraboda.models.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SidurYomAbodaAdapter extends RecyclerView.Adapter<SidurYomAbodaAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClick(Lesson lesson);

        void onLongClick(Lesson lesson);
    }

    private final List<Lesson> SidurYomAboda;
    private final List<Student> studentList;
    private final SidurYomAbodaAdapter.OnClickListener onClickListener;

    public SidurYomAbodaAdapter(@Nullable final SidurYomAbodaAdapter.OnClickListener onClickListener) {
        SidurYomAboda = new ArrayList<>();
        studentList = new ArrayList<>();
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public SidurYomAbodaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new SidurYomAbodaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SidurYomAbodaAdapter.ViewHolder holder, int position) {
        Lesson lesson = SidurYomAboda.get(position);
        if (lesson == null) return;

        Optional<Student> student = studentList
                .stream()
                .filter(s -> s.getId().equals(lesson.getStudentId()))
                .findFirst();

        student.ifPresent(s -> holder.tvStudentName.setText(s.getName()));
        holder.tv_start_time.setText(lesson.getDayAndHours().getStartTime().toString());
        holder.tv_end_time.setText(lesson.getDayAndHours().getEndTime().toString());



        holder.itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(lesson);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onLongClick(lesson);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        if (studentList.isEmpty()) return 0;
        return SidurYomAboda.size();
    }

    public void setList(List<Lesson> lessons) {
        SidurYomAboda.clear();
        SidurYomAboda.addAll(lessons);
        notifyDataSetChanged();
    }

    public void setStudentList(List<Student> students) {
        this.studentList.clear();
        this.studentList.addAll(students);
        notifyDataSetChanged();
    }

    public void add(Lesson lesson) {
        SidurYomAboda.add(lesson);
        notifyItemInserted(SidurYomAboda.size() - 1);
    }

    public void update(Lesson lesson) {
        int index = SidurYomAboda.indexOf(lesson);
        if (index == -1) return;
        SidurYomAboda.set(index, lesson);
        notifyItemChanged(index);
    }

    public void remove(Lesson lesson) {
        int index = SidurYomAboda.indexOf(lesson);
        if (index == -1) return;
        SidurYomAboda.remove(index);
        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        TextView tv_start_time;
        TextView tv_end_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_item_lesson_teacher_id);
            tv_start_time = itemView.findViewById(R.id.tv_start_time);
            tv_end_time = itemView.findViewById(R.id.tv_end_time);
        }
    }
}


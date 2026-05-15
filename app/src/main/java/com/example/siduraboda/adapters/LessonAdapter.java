package com.example.siduraboda.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.siduraboda.R;
import com.example.siduraboda.models.Lesson;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {

    private List<Lesson> lessons;

    public LessonAdapter(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.tvDate.setText(lesson.getDate());

        if (lesson.getDayAndHours() != null) {
            String hebrewDay = getHebrewDay(lesson.getDayAndHours().getDay());
            String timeRange = lesson.getDayAndHours().getEndTime().toString() + " - " +
                    lesson.getDayAndHours().getStartTime().toString();
            holder.tvTime.setText(hebrewDay + " | " + timeRange);
        }

        if (lesson.getTeacherNotes() != null && !lesson.getTeacherNotes().isEmpty()) {
            holder.tvNotes.setVisibility(View.VISIBLE);
            holder.tvNotes.setText("הערת מורה: " + lesson.getTeacherNotes());
        } else {
            holder.tvNotes.setVisibility(View.GONE);
        }
    }

    private String getHebrewDay(com.example.siduraboda.models.Weekday day) {
        if (day == null) return "";
        switch (day) {
            case SUNDAY:
                return "יום ראשון";
            case MONDAY:
                return "יום שני";
            case TUESDAY:
                return "יום שלישי";
            case WEDNESDAY:
                return "יום רביעי";
            case THURSDAY:
                return "יום חמישי";
            case FRIDAY:
                return "יום שישי";
            default:
                return "";
        }
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime, tvNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.item_lesson_date);
            tvTime = itemView.findViewById(R.id.item_lesson_time);
            tvNotes = itemView.findViewById(R.id.item_lesson_notes);
        }
    }
}
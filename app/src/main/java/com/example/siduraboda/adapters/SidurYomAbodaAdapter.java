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

public class SidurYomAbodaAdapter extends RecyclerView.Adapter<SidurYomAbodaAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClick(Lesson lesson);
        void onLongClick(Lesson lesson);
    }

    private final List<Lesson> SidurYomAboda;
    private final SidurYomAbodaAdapter.OnClickListener onClickListener;
    public SidurYomAbodaAdapter(@Nullable final SidurYomAbodaAdapter.OnClickListener onClickListener) {
        SidurYomAboda = new ArrayList<>();
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

        holder.tvUserId.setText(lesson.getUserId());

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
        return SidurYomAboda.size();
    }

    public void setList(List<Lesson> users) {
        SidurYomAboda.clear();
        SidurYomAboda.addAll(users);
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
        TextView tvUserId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tv_item_lesson_userid);
        }
    }
}


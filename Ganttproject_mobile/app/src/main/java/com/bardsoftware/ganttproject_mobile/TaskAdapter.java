package com.bardsoftware.ganttproject_mobile;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View.OnClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static dataformat.Dataformat.Task;
import static dataformat.Dataformat.Tasks;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private Tasks.Builder tasksBuilder;
    private SparseBooleanArray taskStateArray = new SparseBooleanArray();
    private int currentSelectedPosition = RecyclerView.NO_POSITION;

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_row, parent, false);
        return new TaskAdapter.TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, final int position) {
        if (tasksBuilder == null) return;
        String taskName = tasksBuilder.getTask(position).getName();
        boolean itemIsChecked = tasksBuilder.getTask(position).getIsDone();
        taskStateArray.append(position, itemIsChecked);
        holder.taskView.setText(taskName);
        holder.taskCheckbox.setChecked(itemIsChecked);
        if (itemIsChecked) {
            holder.taskView.setPaintFlags(holder.taskView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
            holder.taskView.setPaintFlags(holder.taskView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        if (currentSelectedPosition == position) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tasksBuilder.removeTask(position);
                    taskStateArray.delete(position);
                    currentSelectedPosition = RecyclerView.NO_POSITION;
                    notifyDataSetChanged();

                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (tasksBuilder == null) return 0;
        return tasksBuilder.getTaskCount();
    }

    public void setData(Tasks tasks1) {
        tasksBuilder = tasks1.toBuilder();
        notifyDataSetChanged();
    }

    public Tasks getTasks() {
        Tasks.Builder tasks1 = Tasks.newBuilder();
        for (int i = 0; i < getItemCount(); i++) {
            Task task = Task.newBuilder()
                    .setName(tasksBuilder.getTask(i).getName())
                    .setIsDone(tasksBuilder.getTask(i).getIsDone())
                    .build();
            tasks1.addTask(task);
        }
        return tasks1.build();
    }

    public void addTask(String name) {
        Task task = Task.newBuilder().setName(name).setIsDone(false).build();
        tasksBuilder.addTask(task);
        taskStateArray.append(tasksBuilder.getTaskCount(), false);
        notifyDataSetChanged();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.task_textview)
        TextView taskView;
        @BindView(R.id.task_checkbox)
        CheckBox taskCheckbox;
        @BindView(R.id.delete_task_button)
        ImageButton deleteButton;

        TaskViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            taskView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
            taskCheckbox.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    if (!taskStateArray.get(adapterPosition, false)) {
                        taskCheckbox.setChecked(true);
                        taskView.setPaintFlags(taskView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        taskStateArray.put(adapterPosition, true);
                        Task task1 = Task.newBuilder().setName(tasksBuilder.getTask(adapterPosition).getName()).setIsDone(true).build();
                        tasksBuilder.setTask(adapterPosition, task1);
                    } else {
                        taskCheckbox.setChecked(false);
                        taskStateArray.put(adapterPosition, false);
                        taskView.setPaintFlags(taskView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        Task task1 = Task.newBuilder().setName(tasksBuilder.getTask(adapterPosition).getName()).setIsDone(false).build();
                        tasksBuilder.setTask(adapterPosition, task1);
                    }
                    currentSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }
}

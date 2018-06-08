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
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import static dataformat.Dataformat.Task;
import static dataformat.Dataformat.Tasks;

public class TaskAdapter  extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{
   private Tasks tasks;
    private Context mContext;
    private SparseBooleanArray taskStateArray = new SparseBooleanArray();
    private ArrayList<String> tasksNames=new ArrayList<String>();

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_row, parent, false);;
        return new TaskAdapter.TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        if (tasks == null) return;
        String taskName = tasks.getTask(position).getName();
        boolean itemIsChecked = tasks.getTask(position).getIsDone();
        taskStateArray.append(position, itemIsChecked);
        tasksNames.add(taskName);
        holder.taskView.setText(taskName);
        holder.taskCheckbox.setChecked(itemIsChecked);
        if (itemIsChecked) {
            holder.taskView.setPaintFlags(holder.taskView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.itemView.setTag(taskName);

    }

    @Override
    public int getItemCount() {
        if (tasks == null) return 0;
        return tasks.getTaskCount();
    }

   public void setData(Tasks tasks1, Context context) {
        tasks = tasks1;
        mContext = context;
        notifyDataSetChanged();
    }

    public Tasks getTasks() {
        Tasks.Builder tasks1 = Tasks.newBuilder();
        for (int i=0; i< getItemCount(); i++){
            Task task = Task.newBuilder()
                    .setName(tasksNames.get(i))
                    .setIsDone(taskStateArray.valueAt(i))
                    .build();
            tasks1.addTask(task);
        }
        return tasks1.build();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        @BindView(R.id.task_textview)
        TextView taskView;
        @BindView(R.id.task_checkbox)
        CheckBox taskCheckbox;

        public TaskViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            taskCheckbox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            int adapterPosition = getAdapterPosition();
            if (!taskStateArray.get(adapterPosition, false)) {
                taskCheckbox.setChecked(true);
                taskView.setPaintFlags(taskView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                taskStateArray.put(adapterPosition, true);
            } else {
                taskCheckbox.setChecked(false);
                taskStateArray.put(adapterPosition, false);
            }
        }
    }
}

package com.example.mytodolist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private Context context;
    private ArrayList<Task> taskList;
    private OnTaskEditListener taskEditListener;
    private DatabaseHelper dbHelper;

    public TaskAdapter(Context context, ArrayList<Task> taskList, OnTaskEditListener taskEditListener) {
        this.context = context;
        this.taskList = taskList;
        this.taskEditListener = taskEditListener;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task item = taskList.get(position);
        holder.checkBox.setText(item.getTask());
        holder.textView.setText(item.getTaskDescription());
        holder.checkBox.setChecked(toBoolean(item.getStatus()));
    }
    public Boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // Created interface to get the current position of the task to override the function in MainActivity to perform Edit.
    public interface OnTaskEditListener {
        void onEditTask(int position);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            Task task = taskList.get(position);

            if (direction == ItemTouchHelper.LEFT) {
                dbHelper.deleteTask(task.getId());
                taskList.remove(position);
                notifyItemRemoved(position);

            } else if (direction == ItemTouchHelper.RIGHT) {
                if (taskEditListener != null) {
                    taskEditListener.onEditTask(position);
                    notifyItemChanged(position);
                }
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;
                Paint paint = new Paint();  // for coloring background

                if (dX < 0) {   // swipes left  and draw red rectangle
                    paint.setColor(Color.RED);
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);

                    paint.setColor(Color.WHITE);
                    paint.setTextSize(50);
                    paint.setTextAlign(Paint.Align.CENTER);

                    float textX = itemView.getRight() - 100;
                    float textY = itemView.getTop() + itemView.getHeight() / 2 + 15;

                    c.drawText("Delete", textX, textY, paint);
                }
                else if(dX > 0){
                    paint.setColor(Color.rgb(49, 164, 244));
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), itemView.getLeft() + dX, (float) itemView.getBottom(), paint);

                    paint.setColor(Color.WHITE);
                    paint.setTextSize(50);
                    paint.setTextAlign(Paint.Align.CENTER);
                    float textX = itemView.getLeft() + 100;
                    float textY = itemView.getTop() + itemView.getHeight() / 2 + 15;

                    c.drawText("Edit", textX, textY, paint);
                }
                else {
                    itemView.setTranslationX(0);
                }
            }
        }
    };

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            textView = itemView.findViewById(R.id.taskDescription);
        }
    }
}

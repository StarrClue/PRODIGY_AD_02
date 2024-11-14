package com.example.mytodolist;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskEditListener{

    FloatingActionButton addTask;

    int i = 1;

    private RecyclerView taskView;
    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);


        taskView = findViewById(R.id.taskView);
        taskView.setLayoutManager(new LinearLayoutManager(this));
        taskList = dbHelper.getAllTasks();

        taskAdapter = new TaskAdapter(this, taskList, this);
        taskView.setAdapter(taskAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(taskAdapter.simpleCallback);
        itemTouchHelper.attachToRecyclerView(taskView);


        addTask = findViewById(R.id.task_add);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
    }

    public void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_task_layout);

        EditText newTask = bottomSheetDialog.findViewById(R.id.newTask);
        TextView taskDescrip = bottomSheetDialog.findViewById(R.id.taskDescription);
        Button saveTask = bottomSheetDialog.findViewById(R.id.saveTask);

        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String task = newTask.getText().toString();
                String taskDescription = taskDescrip.getText().toString();

                if (!task.isEmpty()) {
                    long result = dbHelper.addTask(task, taskDescription, 0);
                    if (result != -1) {

                        taskList.clear();
                        taskList.addAll(dbHelper.getAllTasks());
                        taskAdapter.notifyDataSetChanged();
                        bottomSheetDialog.dismiss();
                    }
                }
            }
        });
        bottomSheetDialog.show();
    }

    @Override
    public void onEditTask(int position) {
        showEditDialog(position);
    }

    public void showEditDialog(int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_task_layout);

        EditText editTask = bottomSheetDialog.findViewById(R.id.newTask);
        TextView taskDescrip = bottomSheetDialog.findViewById(R.id.taskDescription);
        Button saveTask = bottomSheetDialog.findViewById(R.id.saveTask);

        Task task = taskList.get(position);
        editTask.setText(task.getTask());
        taskDescrip.setText(task.getTaskDescription());


        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updateTask = editTask.getText().toString();
                String updateDescription = taskDescrip.getText().toString();

                if (!updateTask.isEmpty()) {

                    boolean updated = dbHelper.updateTask(task.getId(), updateTask, updateDescription, task.getStatus());
                    if (updated) {

                        task.setTask(updateTask);
                        task.setTaskDescription(updateDescription);
                        taskAdapter.notifyItemChanged(position);
                        bottomSheetDialog.dismiss();
                    }
                }
            }
        });
        bottomSheetDialog.show();
    }
    public void deleteTask(int position) {
        Task task = taskList.get(position);
        boolean deleted = dbHelper.deleteTask(task.getId());
        if (deleted) {
            taskList.remove(position);
            taskAdapter.notifyItemRemoved(position);
        }
        }
}
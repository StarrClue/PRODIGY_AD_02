package com.example.mytodolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_TASKS + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_STATUS + " INTEGER)";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(sqLiteDatabase);
    }

    public long addTask(String task, String taskDescription, int status) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);
        values.put(COLUMN_DESCRIPTION, taskDescription);
        values.put(COLUMN_STATUS, status);

        long result = sqLiteDatabase.insert(TABLE_TASKS, null, values);
        sqLiteDatabase.close();
        return result;
    }

    public boolean updateTask(int id, String newTask, String newTaskDescription, int newStatus) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, newTask);
        values.put(COLUMN_DESCRIPTION, newTaskDescription);
        values.put(COLUMN_STATUS, newStatus);

        int rowsUpdated = sqLiteDatabase.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
        return rowsUpdated > 0;
    }

    public boolean deleteTask(int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int rowsUpdated = sqLiteDatabase.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
        return rowsUpdated > 0;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        db.beginTransaction();
        try {
            cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Task task = new Task(
                                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK)),
                                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                        );
                        taskList.add(task);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
        return taskList;
    }
}

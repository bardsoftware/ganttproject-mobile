package com.bardsoftware.ganttproject_mobile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import static dataformat.Dataformat.Tasks;

public class TasksActivity extends AppCompatActivity {
    private Uri filePath;
    private Context mContext;
    private TaskAdapter taskAdapter;
    private Tasks tasks;
    //@BindView(R.id.tasks_recyclerview)
    RecyclerView tasksRecyclerView;
    private static final String LAYOUT_POSITION = "position";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        mContext = this;
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            filePath = Uri.parse(Objects.requireNonNull(bundle).getString(Constants.FILE_PATH));
        }
        verifyStoragePermissions(this);

        try {
            InputStream input = mContext.getContentResolver().openInputStream(filePath);
            tasks = Tasks.parseFrom(input);

        } catch (IOException e) {
            e.printStackTrace();
        }

        tasksRecyclerView = findViewById(R.id.tasks_recyclerview);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter();
        tasksRecyclerView.setAdapter(taskAdapter);
        taskAdapter.setData(tasks);

        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("position");
            tasksRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void saveTasksFile(View view) {
        taskAdapter.notifyDataSetChanged();
        tasks = taskAdapter.getTasks();
        mContext.grantUriPermission("com.bardsoftware.ganttproject_mobile", filePath, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try {

            OutputStream output = mContext.getContentResolver().openOutputStream(filePath);
            tasks.writeTo(Objects.requireNonNull(output));
            output.close();
            Toast.makeText(this, "file " + filePath.getPath() + " is saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTaskClicked(View view) {
        taskAdapter.notifyDataSetChanged();

        final EditText taskTitleEditText = new EditText(mContext);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Add New Task");
        builder.setView(taskTitleEditText);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String taskTitle = String.valueOf(taskTitleEditText.getText());
                taskAdapter.addTask(taskTitle);
            }

        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LAYOUT_POSITION, tasksRecyclerView.getLayoutManager().onSaveInstanceState());
    }
}

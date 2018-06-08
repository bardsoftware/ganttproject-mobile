package com.bardsoftware.ganttproject_mobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final int OPEN_FILE_CODE = 100 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openProtoFile(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/octet-stream");
        startActivityForResult(intent, OPEN_FILE_CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_FILE_CODE && resultCode == RESULT_OK && data != null)
        { String filePath = data.getDataString();
            Bundle b = new Bundle();
            b.putString(Constants.FILE_PATH, filePath);
            Class TasksActivity = TasksActivity.class;
            Intent intent = new Intent(this, TasksActivity);
            intent.putExtras(b);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}

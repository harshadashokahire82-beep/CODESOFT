package com.example.todolistapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    EditText editTextTask;
    Button buttonAdd;
    ListView listViewTasks;
    ArrayList<String> taskList;
    ArrayList<Boolean> checkedList;
    SharedPreferences sharedPreferences;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTask = findViewById(R.id.editTextTask);
        buttonAdd = findViewById(R.id.buttonAdd);
        listViewTasks = findViewById(R.id.listViewTasks);

        sharedPreferences = getSharedPreferences("TodoApp", MODE_PRIVATE);

        // Tasks aur checkbox states load karo
        taskList = new ArrayList<>();
        checkedList = new ArrayList<>();

        Set<String> savedTasks = sharedPreferences.getStringSet("tasks", new HashSet<>());
        int count = sharedPreferences.getInt("task_count", 0);

        // Order maintain karne ke liye index se load karo
        for (int i = 0; i < count; i++) {
            String task = sharedPreferences.getString("task_" + i, null);
            if (task != null) {
                taskList.add(task);
                boolean isChecked = sharedPreferences.getBoolean("checked_" + i, false);
                checkedList.add(isChecked);
            }
        }

        adapter = new ArrayAdapter<String>(this,
                R.layout.task_item, R.id.checkBoxTask, taskList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                CheckBox checkBox = view.findViewById(R.id.checkBoxTask);
                Button deleteButton = view.findViewById(R.id.buttonDelete);

                checkBox.setText(taskList.get(position));

                // Pehle listener hata do taaki loop na ho
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(checkedList.get(position));

                // Ab listener lagao
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    checkedList.set(position, isChecked);
                    saveTasks();
                    if (isChecked) {
                        Toast.makeText(MainActivity.this,
                                "Task completed! ✓", Toast.LENGTH_SHORT).show();
                    }
                });

                deleteButton.setOnClickListener(v -> {
                    taskList.remove(position);
                    checkedList.remove(position);
                    saveTasks();
                    notifyDataSetChanged();
                    Toast.makeText(MainActivity.this,
                            "Task deleted!", Toast.LENGTH_SHORT).show();
                });

                return view;
            }
        };

        listViewTasks.setAdapter(adapter);

        buttonAdd.setOnClickListener(v -> {
            String task = editTextTask.getText().toString().trim();
            if (!task.isEmpty()) {
                taskList.add(task);
                checkedList.add(false);
                saveTasks();
                adapter.notifyDataSetChanged();
                editTextTask.setText("");
            } else {
                Toast.makeText(MainActivity.this,
                        "Please enter a task!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Pehle sab clear karo
        editor.clear();

        // Har task aur uska checkbox state alag save karo
        editor.putInt("task_count", taskList.size());
        for (int i = 0; i < taskList.size(); i++) {
            editor.putString("task_" + i, taskList.get(i));
            editor.putBoolean("checked_" + i, checkedList.get(i));
        }

        editor.apply();
    }
}
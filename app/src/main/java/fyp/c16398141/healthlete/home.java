package fyp.c16398141.healthlete;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class home extends AppCompatActivity {

    private EditText title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RelativeLayout layout1 = findViewById(R.id.relative1);
        layout1.setBackgroundColor(Color.RED);
        RelativeLayout layout2 = findViewById(R.id.relative2);
        layout2.setBackgroundColor(Color.CYAN);
        RelativeLayout layout3 = findViewById(R.id.relative3);
        layout3.setBackgroundColor(Color.YELLOW);

        //title = (EditText) findViewById(R.id.editText);
        //title.setText("Google is your friend.", TextView.BufferType.EDITABLE);

        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(home.this, food_log.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(home.this, fitness.class);
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(home.this, achievements.class);
                startActivity(intent);
            }
        });
    }

}

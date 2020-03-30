package fyp.c16398141.healthlete;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static maes.tech.intentanim.CustomIntent.customType;

public class view_workout_areas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workout_areas);
        Button map_button = findViewById(R.id.map);
        Button home_button = findViewById(R.id.home);

        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view_workout_areas.this, workout_area.class);
                startActivity(intent);
                customType(view_workout_areas.this,"up-to-bottom");
            }
        });

        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view_workout_areas.this, home.class);
                startActivity(intent);
                customType(view_workout_areas.this,"right-to-left");
            }
        });
    }
}

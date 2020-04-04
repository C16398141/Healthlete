package fyp.c16398141.healthlete;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import static java.lang.String.valueOf;
import static maes.tech.intentanim.CustomIntent.customType;

public class edit_goals extends AppCompatActivity {

    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goals);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (user_id == null){
            user_id = getIntent().getExtras().getString("userId");
        }

        Button log = findViewById(R.id.log);
        EditText e_cals = findViewById(R.id.edit_calories);
        EditText e_carbs = findViewById(R.id.edit_carbs);
        EditText e_protein = findViewById(R.id.edit_protein);

        LocalDB ldb;
        ldb = new LocalDB(this);
        ldb.open();
        Cursor goals = ldb.getGoals(user_id);
        int num_goals = goals.getCount();
        if (num_goals == 0) {
            updateGoals(2000,400,200);
            goals = ldb.getGoals(user_id);
        }
        while (goals.moveToNext()){
            e_cals.setText(valueOf(goals.getInt(2)));
            e_carbs.setText(valueOf(goals.getInt(3)));
            e_protein.setText(valueOf(goals.getInt(4)));
        }
        ldb.close();

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(e_cals.getText())) {
                    e_cals.setError("This field must be filled in");
                    return;
                }

                if (TextUtils.isEmpty(e_carbs.getText())) {
                    e_carbs.setError("This field must be filled in");
                    return;
                }

                if (TextUtils.isEmpty(e_protein.getText())) {
                    e_protein.setError("This field must be filled in");
                    return;
                }

                if (!TextUtils.isDigitsOnly(e_cals.getText())) {
                    e_cals.setError("This field requires a whole number");
                    return;
                }

                if (!TextUtils.isDigitsOnly(e_carbs.getText())) {
                    e_carbs.setError("This field requires a whole number");
                    return;
                }

                if (!TextUtils.isDigitsOnly(e_protein.getText())) {
                    e_protein.setError("This field requires a whole number");
                    return;
                }

                Integer cals = Integer.parseInt(e_cals.getText().toString());
                Integer carbs = Integer.parseInt(e_carbs.getText().toString());
                Integer protein = Integer.parseInt(e_protein.getText().toString());
                updateGoals(cals, carbs, protein);
                Intent intent = new Intent(edit_goals.this, food_log.class);
                intent.putExtra("userId", user_id);
                startActivity(intent);
                customType(edit_goals.this,"bottom-to-up");
            }
        });

        ImageButton close = findViewById(R.id.cancel);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit_goals.this, home.class);
                intent.putExtra("userId", user_id);
                startActivity(intent);
            }
        });
}

    public void updateGoals(Integer targetCals, Integer targetCarbs, Integer targetProtein)
    {
        LocalDB ldb;
        ldb = new LocalDB(this);
        ldb.open();
        Boolean deleted = ldb.deleteGoals(user_id);
        if (deleted){
            Boolean result = ldb.addGoals("lose weight",targetCals,targetCarbs,targetProtein, user_id);
            ldb.close();
            if (result == true){
                Log.i("TAGS","goals added");
            }else{
                Log.i("TAGS","goals failed");
                return;
            }
        }else {
            Log.i("Goals","Couldn't be deleted");
        }

    }

}

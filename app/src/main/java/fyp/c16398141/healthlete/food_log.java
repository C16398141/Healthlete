package fyp.c16398141.healthlete;

import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;
import android.text.Spannable;
import android.widget.RelativeLayout;

public class food_log extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RelativeLayout layout1 = findViewById(R.id.relative1);
        layout1.setBackgroundColor(Color.CYAN);
        /*Button addButton = findViewById(R.id.addButton);
        Spannable buttonLabel = new SpannableString(" Add New");
        buttonLabel.setSpan(new ImageSpan(getApplicationContext(), R.drawable.plus_circle,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        addButton.setText(buttonLabel);
*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    public void init() {
        TableLayout table = (TableLayout) findViewById(R.id.food_table);
        TableRow heading = new TableRow(this);
        ImageButton add = new ImageButton(this);
        add.setImageResource(R.mipmap.plus_circle);
        heading.addView(add);
        TextView tv1 = new TextView(this);
        tv1.setText(" Name ");
        tv1.setTextColor(Color.BLACK);
        heading.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Calories ");
        tv2.setTextColor(Color.BLACK);
        heading.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Carbs ");
        tv3.setTextColor(Color.BLACK);
        heading.addView(tv3);
        TextView tv4 = new TextView(this);
        tv4.setText(" Protein ");
        tv4.setTextColor(Color.BLACK);
        heading.addView(tv4);
        table.addView(heading);
        for (int i = 0; i < 10; i++) {
            TableRow tbrow = new TableRow(this);
            ImageButton minus = new ImageButton(this);
            minus.setImageResource(R.mipmap.ic_minus);
            tbrow.addView(minus);
            TextView t2v = new TextView(this);
            t2v.setText("Food " + i);
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText("Cals" + i);
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText("Carbs" + i * 15 / 32 * 10);
            t4v.setTextColor(Color.BLACK);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);
            TextView t5v = new TextView(this);
            t5v.setText("Proteins" + i);
            t5v.setTextColor(Color.BLACK);
            t5v.setGravity(Gravity.CENTER);
            tbrow.addView(t5v);
            table.addView(tbrow);
        }
    }
}

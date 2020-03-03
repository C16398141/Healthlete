package fyp.c16398141.healthlete;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.charts.Polar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;
import android.text.Spannable;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import android.widget.TableRow.LayoutParams;
//import android.widget.RelativeLayout.LayoutParams;
//import android.widget.LinearLayout.LayoutParams;

public class food_log extends AppCompatActivity {

    LocalDB ldb;
    //AnyChartView anyChartView;
    List<ImageButton> list = new ArrayList<ImageButton>();

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
        //anyChartView = findViewById(R.id.any_chart_view);
        TableLayout table = (TableLayout) findViewById(R.id.food_table);

        ldb = new LocalDB(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init(table);
        /*setupPolarChart();
        drawArc(oval, 0F, 90F, true, turquoisePaint);
        drawArc(oval, 90F, 90F, true, orangePaint);
        drawArc(oval, 180F, 90F, true, yellowPaint);
        drawArc(oval, 270F, 90F, true, hotPinkPaint);*/

        //addRows()
        //displayRows();

        //ImageButton add = this.findViewById();



    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    public void init(TableLayout table) {
        ldb.open();
        displayRows(table);
        ldb.close();
    }

    public void setupPolarChart(){
        Polar polar = AnyChart.polar();
    }

    public void addRows()
    {
        String foodname = "porridge";
        ldb.addFoodEntry(foodname,10,"16022020",68,20,12);
    }

    public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint){

    }

    public void displayRows(final TableLayout table)
    {
        TableRow heading = new TableRow(this);
        heading.setGravity(Gravity.CENTER);
        int buttonStyle = R.style.Widget_AppCompat_Button_Borderless;
        ImageButton add = new ImageButton(new ContextThemeWrapper(this, buttonStyle));
        add.setId(View.generateViewId());
        add.setImageResource(R.drawable.plus_circle);
        add.setBackgroundResource(R.drawable.backgroundstate);
        heading.addView(add);
        TextView tv1 = new TextView(this);
        tv1.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv1.setText(" Name ");
        tv1.setTypeface(Typeface.DEFAULT_BOLD);
        tv1.setTextColor(Color.BLACK);
        tv1.setBackgroundColor(Color.WHITE);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.setMargins(5,5,0,0);
        //tv1.setLayoutParams(lp);
        heading.addView(tv1,lp);
        TextView tv2 = new TextView(this);
        //RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv2.getLayoutParams();
        //layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        //tv2.setLayoutParams(layoutParams);
        tv2.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv2.setText(" Calories ");
        tv2.setTypeface(Typeface.DEFAULT_BOLD);
        tv2.setTextColor(Color.BLACK);
        tv2.setBackgroundColor(Color.WHITE);
        //tv2.setLayoutParams(lp);
        heading.addView(tv2,lp);
        TextView tv3 = new TextView(this);
        tv3.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv3.setText(" Carbs ");
        tv3.setTypeface(Typeface.DEFAULT_BOLD);
        tv3.setTextColor(Color.BLACK);
        tv3.setBackgroundColor(Color.WHITE);
        heading.addView(tv3,lp);
        TextView tv4 = new TextView(this);
        tv4.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv4.setText(" Protein ");
        tv4.setTypeface(Typeface.DEFAULT_BOLD);
        tv4.setTextColor(Color.BLACK);
        tv4.setBackgroundColor(Color.WHITE);
        heading.addView(tv4,lp);
        table.addView(heading);

        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(food_log.this, food_entry.class);
                startActivity(intent);
                //RETURNING FROM TOP OF SCREEN SHOWS CHANGES WHILE BOTTOM BUTTON RETURN DOESN'T REFRESH PARENT
            }
        });

        Cursor entries = ldb.getAllFoodEntries();
        int rows = entries.getCount();
        if (rows == 0) {
        } else {
            entries.moveToFirst();
            int i = 0;
            do {
                i++;
                TableRow tbrow = new TableRow(this);
                tbrow.setGravity(Gravity.CENTER);
                ImageButton minus = new ImageButton(this);
                minus.setImageResource(R.drawable.minus_circle);
                minus.setBackgroundResource(R.drawable.backgroundstate);
                minus.setId(entries.getInt(0));
                tbrow.addView(minus);
                list.add(minus);
                TextView t2v = new TextView(this);
                t2v.setBackgroundColor(Color.WHITE);
                //t2v.setId(i);
                t2v.setText(entries.getString(1));
                t2v.setTextColor(Color.BLACK);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v,lp);
                TextView t3v = new TextView(this);
                t3v.setText(Integer.toString(entries.getInt(4)));
                t3v.setBackgroundColor(Color.WHITE);
                t3v.setTextColor(Color.BLACK);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v,lp);
                TextView t4v = new TextView(this);
                t4v.setText(Integer.toString(entries.getInt(5)));
                t4v.setBackgroundColor(Color.WHITE);
                t4v.setTextColor(Color.BLACK);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v,lp);
                TextView t5v = new TextView(this);
                t5v.setText(Integer.toString(entries.getInt(6)));
                t5v.setBackgroundColor(Color.WHITE);
                t5v.setTextColor(Color.BLACK);
                t5v.setGravity(Gravity.CENTER);
                tbrow.addView(t5v,lp);
                table.addView(tbrow);
            } while (entries.moveToNext());

            for(final ImageButton minus : list){
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = minus.getId();
                        Log.i("TAG", "The id is" + id);
                        ldb.open();
                        boolean result = ldb.deleteFoodEntry(id);
                        if (result == true) {
                            Toast.makeText(getApplicationContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                            table.removeAllViews();
                            displayRows(table);
                        } else {
                            Toast.makeText(getApplicationContext(), "unsuccessful delete", Toast.LENGTH_SHORT).show();
                        }
                        ldb.close();
                    }
                });
            }
        }
    }
}


package fyp.c16398141.healthlete;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.charts.Polar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;

import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;
import android.text.Spannable;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.widget.TableRow.LayoutParams;

import static java.lang.String.valueOf;
//import android.widget.RelativeLayout.LayoutParams;
//import android.widget.LinearLayout.LayoutParams;

public class food_log extends AppCompatActivity {

    LocalDB ldb;
    //AnyChartView anyChartView;
    List<ImageButton> list = new ArrayList<ImageButton>();
    List<Integer> dimensions = new ArrayList<>();
    List<Integer> totalCalories = new ArrayList<>();
    List<Integer> totalCarbs = new ArrayList<>();
    List<Integer> totalProtein = new ArrayList<>();
    EditText datefield;
    DatePickerDialog datePickerDialog;
    String user_id = "2013chrisclarke@gmail.com";
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //View myView = null;
        //LayoutInflater.from(context).inflate(R.layout.activity_food_log, myView, true);
        setContentView(R.layout.activity_food_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RelativeLayout layout1 = findViewById(R.id.relative1);
        layout1.setBackgroundColor(Color.CYAN);

        datefield = (EditText) findViewById(R.id.datefield);
        /*Button addButton = findViewById(R.id.addButton);
        Spannable buttonLabel = new SpannableString(" Add New");
        buttonLabel.setSpan(new ImageSpan(getApplicationContext(), R.drawable.plus_circle,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        addButton.setText(buttonLabel);

*/
        //anyChartView = findViewById(R.id.any_chart_view);
        TableLayout table = (TableLayout) findViewById(R.id.food_table);

        ldb = new LocalDB(this);
        ldb.open();
        addGoals();
        ldb.close();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final RelativeLayout rl = findViewById(R.id.relative3);
        final ConstraintLayout cl = findViewById(R.id.constraint1);
        ViewTreeObserver vto = rl.getViewTreeObserver();
        ViewTreeObserver vto2 = rl.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int rlwidth  = rl.getMeasuredWidth();
                int rlheight = rl.getMeasuredHeight();
                dimensions.add(rlwidth);
                dimensions.add(rlheight);
                setupPieChart();
            }
        });

        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR); // current year
        final int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        datefield.setText(mDay + "/" + (mMonth+1) + "/" + mYear);
        date = "" + mDay + (mMonth+1) + mYear;

        init(table);

        datefield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // date picker dialog
                datePickerDialog = new DatePickerDialog(food_log.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datefield.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                                date = "" + dayOfMonth + (monthOfYear+1) + year;
                                Log.i("TAG",valueOf(date));
                                table.removeAllViews();
                                displayRows(table);
                                setupPieChart();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        /*vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                cl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int clwidth  = cl.getMeasuredWidth();
                int clheight = cl.getMeasuredHeight();
                dimensions.add(clheight);
                dimensions.add(clwidth);
                setupPieChart();
            }
        });*/

        //int difference = dimensions.get(0) - dimensions.get(0);
        //Log.i("TAG",valueOf(difference));

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
        displayRows(table);
    }

    public void setupPieChart() {

        int imageViewWidth = dimensions.get(0);
        int imageViewHeight = dimensions.get(1);
        Log.i("TAG",valueOf(imageViewWidth));
        Log.i("TAG",valueOf(imageViewHeight));
        ImageView imageView = (ImageView) findViewById(R.id.imageview);
        Bitmap bitmap = Bitmap.createBitmap(imageViewWidth, imageViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(3*imageViewWidth/16, imageViewHeight/2 +40, imageViewWidth/8, paint);
        canvas.drawCircle(8*imageViewWidth/16, imageViewHeight/2 +40, imageViewWidth/8, paint);
        canvas.drawCircle(13*imageViewWidth/16, imageViewHeight/2 +40, imageViewWidth/8, paint);

        ldb.open();
        Integer targetCals = 0;
        Integer targetCarbs = 0;
        Integer targetProtein = 0;
        Cursor entries = ldb.getGoals();
        while (entries.moveToNext()){
            targetCals = entries.getInt(2);
            targetCarbs = entries.getInt(3);
            targetProtein = entries.getInt(4);
        }
        ldb.close();

        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(Color.CYAN);

        Integer totalCals = 0;
        Integer totalCarbos = 0;
        Integer totalProteins = 0;

        for (Integer c : totalCalories)
            totalCals += c;
        Float angle = ((float)totalCals/targetCals*360);

        for (Integer a : totalCarbs)
            totalCarbos += a;
        Float angle2 = ((float)totalCarbos/targetCarbs*360);

        for (Integer p : totalProtein)
            totalProteins += p;
        Float angle3 = ((float)totalProteins/targetProtein*360);

        Log.i("TAG",valueOf(totalCals));
        Log.i("TAG",valueOf(targetCals));
        Log.i("TAG",valueOf(angle));
        RectF arc  = new RectF(imageViewWidth/16,imageViewHeight/2 +40 - imageViewWidth/8,5*imageViewWidth/16,imageViewHeight/2 + imageViewWidth/8 +40);
        canvas.drawArc (arc, -90, angle, true,  paint2);
        RectF arc2 = new RectF(6*imageViewWidth/16,imageViewHeight/2+40 - imageViewWidth/8,10*imageViewWidth/16,imageViewHeight/2 + imageViewWidth/8 +40);
        canvas.drawArc (arc2, -90, angle2, true,  paint2);
        RectF arc3 = new RectF(11*imageViewWidth/16,imageViewHeight/2+40 - imageViewWidth/8,15*imageViewWidth/16,imageViewHeight/2 + imageViewWidth/8 +40);
        canvas.drawArc (arc3, -90, angle3, true,  paint2);

        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        canvas.drawText("Calories", 100, 65, paint);
        canvas.drawText("Carbs", 470, 65, paint);
        canvas.drawText("Protein", 800, 65, paint);

        Float calFigure = (float)totalCals/targetCals*100;
        Integer calInt = Math.round(calFigure);
        String calPercent = calInt + "%";
        canvas.drawText(calPercent, 160, 245, paint);

        Float carbsFigure = (float)totalCarbos/targetCarbs*100;
        Integer carbsInt = Math.round(carbsFigure);
        String carbsPercent = carbsInt + "%";
        canvas.drawText(carbsPercent, 500, 245, paint);

        Float proteinFigure = (float)totalProteins/targetProtein*100;
        Integer proteInt = Math.round(proteinFigure);
        String proteinPercent = proteInt + "%";
        canvas.drawText(proteinPercent, 830, 245, paint);
        imageView.setImageBitmap(bitmap);
    }

    public void addGoals()
    {
        String type = "weightLoss";
        Integer targetCals = 2000;
        Integer targetCarbs = 400;
        Integer targetProtein = 200;
        Boolean result = ldb.addGoals("lose weight",2000,400,200, user_id);
        if (result == true){
            Log.i("TAGS","goals added");
        }else{
            Log.i("TAGS","goals failed");
        }
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
            }
        });

        totalCalories.clear();
        totalCarbs.clear();
        totalProtein.clear();

        ldb.open();
        Cursor entries = ldb.getAllFoodEntries(date);
        int rows = entries.getCount();
        if (rows == 0) {
        } else {
            entries.moveToFirst();
            do {
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
                t2v.setText(entries.getString(1));
                t2v.setTextColor(Color.BLACK);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v,lp);

                Log.i("Date",entries.getString(4));

                TextView t3v = new TextView(this);
                Integer cals = entries.getInt(5);
                totalCalories.add(cals);
                t3v.setText(Integer.toString(cals));
                t3v.setBackgroundColor(Color.WHITE);
                t3v.setTextColor(Color.BLACK);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v,lp);
                TextView t4v = new TextView(this);
                Integer carbs = entries.getInt(6);
                totalCarbs.add(carbs);
                t4v.setText(Integer.toString(carbs));
                t4v.setBackgroundColor(Color.WHITE);
                t4v.setTextColor(Color.BLACK);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v,lp);
                TextView t5v = new TextView(this);
                Integer protein = entries.getInt(7);
                totalProtein.add(protein);
                t5v.setText(Integer.toString(protein));
                t5v.setBackgroundColor(Color.WHITE);
                t5v.setTextColor(Color.BLACK);
                t5v.setGravity(Gravity.CENTER);
                tbrow.addView(t5v,lp);
                table.addView(tbrow);
            } while (entries.moveToNext());

            Integer count = totalCalories.size();
            Integer totalCals = 0;
            Integer totalCarbos = 0;
            Integer totalProteins = 0;

            for (Integer c : totalCalories)
                totalCals += c;

            for (Integer a : totalCarbs)
                totalCarbos += a;

            for (Integer p : totalProtein)
                totalProteins += p;

            TableRow totrow = new TableRow(this);
            TextView total = new TextView(this);
            totrow.setGravity(Gravity.CENTER);
            total.setBackgroundColor(Color.WHITE);
            total.setText("Total:");
            total.setTextColor(Color.BLACK);
            total.setGravity(Gravity.CENTER);
            totrow.addView(total,lp);
            TextView total1 = new TextView(this);
            totrow.setGravity(Gravity.CENTER);
            total1.setBackgroundColor(Color.WHITE);
            total1.setText(valueOf(count));
            total1.setTextColor(Color.BLACK);
            total1.setGravity(Gravity.CENTER);
            totrow.addView(total1,lp);
            TextView total2 = new TextView(this);
            total2.setBackgroundColor(Color.WHITE);
            total2.setText(valueOf(totalCals));
            total2.setTextColor(Color.BLACK);
            total2.setGravity(Gravity.CENTER);
            totrow.addView(total2,lp);
            TextView total3 = new TextView(this);
            total3.setText(valueOf(totalCarbos));
            total3.setBackgroundColor(Color.WHITE);
            total3.setTextColor(Color.BLACK);
            total3.setGravity(Gravity.CENTER);
            totrow.addView(total3,lp);
            TextView total4 = new TextView(this);
            total4.setText(valueOf(totalProteins));
            total4.setBackgroundColor(Color.WHITE);
            total4.setTextColor(Color.BLACK);
            total4.setGravity(Gravity.CENTER);
            totrow.addView(total4,lp);
            table.addView(totrow);

            Integer targetCals = 0;
            Integer targetCarbs = 0;
            Integer targetProtein = 0;
            Cursor goals = ldb.getGoals();
            while (goals.moveToNext()){
                targetCals = goals.getInt(2);
                targetCarbs = goals.getInt(3);
                targetProtein = goals.getInt(4);
            }
            ldb.close();

            TableRow targetrow = new TableRow(this);
            TextView target = new TextView(this);
            totrow.setGravity(Gravity.CENTER);
            target.setBackgroundColor(Color.WHITE);
            target.setText("Target:");
            target.setTextColor(Color.BLACK);
            target.setGravity(Gravity.CENTER);
            targetrow.addView(target,lp);
            TextView target1 = new TextView(this);
            targetrow.setGravity(Gravity.CENTER);
            target1.setBackgroundColor(Color.WHITE);
            target1.setText("-");
            target1.setTextColor(Color.BLACK);
            target1.setGravity(Gravity.CENTER);
            targetrow.addView(target1,lp);
            TextView target2 = new TextView(this);
            target2.setText(valueOf(targetCals));
            target2.setBackgroundColor(Color.WHITE);
            target2.setTextColor(Color.BLACK);
            target2.setGravity(Gravity.CENTER);
            targetrow.addView(target2,lp);
            TextView target3 = new TextView(this);
            target3.setBackgroundColor(Color.WHITE);
            target3.setText(valueOf(targetCarbs));
            target3.setTextColor(Color.BLACK);
            target3.setGravity(Gravity.CENTER);
            targetrow.addView(target3,lp);
            TextView target4 = new TextView(this);
            target4.setText(valueOf(targetProtein));
            target4.setBackgroundColor(Color.WHITE);
            target4.setTextColor(Color.BLACK);
            target4.setGravity(Gravity.CENTER);
            targetrow.addView(target4,lp);
            table.addView(targetrow);

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
                            setupPieChart();
                        } else {
                            Toast.makeText(getApplicationContext(), "unsuccessful delete", Toast.LENGTH_SHORT).show();
                        }
                        ldb.close();
                    }
                });
            }
        }
    }


    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        data.putExtra("userId", "new_id");
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }
}

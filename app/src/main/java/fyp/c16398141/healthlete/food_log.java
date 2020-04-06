package fyp.c16398141.healthlete;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;

import static maes.tech.intentanim.CustomIntent.customType;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.widget.TableRow.LayoutParams;

import static java.lang.String.valueOf;

public class food_log extends AppCompatActivity {

    LocalDB ldb;
    List<ImageButton> list = new ArrayList<ImageButton>();
    List<Integer> dimensions = new ArrayList<>();
    List<Integer> totalCalories = new ArrayList<>();
    List<Integer> totalCarbs = new ArrayList<>();
    List<Integer> totalProtein = new ArrayList<>();
    EditText datefield;
    DatePickerDialog datePickerDialog;
    static String user_id;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_food_log);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RelativeLayout layout1 = findViewById(R.id.relative1);
        layout1.setBackgroundColor(Color.CYAN);

        datefield = (EditText) findViewById(R.id.datefield);
        TableLayout table = (TableLayout) findViewById(R.id.food_table);

        if (user_id == null){
            user_id = getIntent().getExtras().getString("userId");
        }

        ldb = new LocalDB(this);

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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        food_log.this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Integer screen_height = displayMetrics.heightPixels;
        Integer screen_width = displayMetrics.widthPixels;
        Log.i(screen_height.toString(),screen_width.toString());

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
        Cursor entries = ldb.getGoals(user_id);
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
        canvas.drawText("Calories", 100, 85, paint);
        canvas.drawText("Carbs", 470, 85, paint);
        canvas.drawText("Protein", 795, 85, paint);

        Float calFigure = (float)totalCals/targetCals*100;
        Integer calInt = Math.round(calFigure);
        String calPercent = calInt + "%";
        canvas.drawText(calPercent, 160, 265, paint);

        Float carbsFigure = (float)totalCarbos/targetCarbs*100;
        Integer carbsInt = Math.round(carbsFigure);
        String carbsPercent = carbsInt + "%";
        canvas.drawText(carbsPercent, 500, 265, paint);

        Float proteinFigure = (float)totalProteins/targetProtein*100;
        Integer proteInt = Math.round(proteinFigure);
        String proteinPercent = proteInt + "%";
        canvas.drawText(proteinPercent, 830, 265, paint);
        imageView.setImageBitmap(bitmap);
    }

    public void addGoals()
    {
        String type = "weightLoss";
        Integer defaultCals = 2000;
        Integer defaultCarbs = 400;
        Integer defaultProtein = 200;
        Boolean result = ldb.addGoals(type,defaultCals,defaultCarbs,defaultProtein, user_id);
        if (result == true){
            Log.i("TAGS","goals added");
        }else{
            Log.i("TAGS","goals failed");
            return;
        }
    }

    public void displayRows(final TableLayout table)
    {
        TableRow heading = new TableRow(this);
        heading.setGravity(Gravity.CENTER);
        heading.setBackgroundColor(Color.BLUE);

        int buttonStyle = R.style.Widget_AppCompat_Button_Borderless;
        ImageButton add = new ImageButton(new ContextThemeWrapper(this, buttonStyle));
        add.setId(View.generateViewId());
        add.setImageResource(R.drawable.plus_circle);
        add.setBackgroundColor(Color.BLUE);
        heading.addView(add);

        TextView tv1 = new TextView(this);
        tv1.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv1.setText(" Name ");
        tv1.setTypeface(Typeface.DEFAULT_BOLD);
        tv1.setTextColor(Color.WHITE);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.setMargins(0,0,0,0);
        heading.addView(tv1,lp);

        TextView tv2 = new TextView(this);
        tv2.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv2.setText(" Calories ");
        tv2.setTypeface(Typeface.DEFAULT_BOLD);
        tv2.setTextColor(Color.WHITE);
        heading.addView(tv2,lp);

        TextView tv3 = new TextView(this);
        tv3.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv3.setText(" Carbs ");
        tv3.setTypeface(Typeface.DEFAULT_BOLD);
        tv3.setTextColor(Color.WHITE);
        heading.addView(tv3,lp);

        TextView tv4 = new TextView(this);
        tv4.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv4.setText(" Protein ");
        tv4.setTypeface(Typeface.DEFAULT_BOLD);
        tv4.setTextColor(Color.WHITE);
        heading.addView(tv4,lp);
        table.addView(heading);

        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(food_log.this, food_entry.class);
                intent.putExtra("userId", user_id);
                startActivity(intent);
                customType(food_log.this,"left-to-right");
            }
        });

        totalCalories.clear();
        totalCarbs.clear();
        totalProtein.clear();

        ldb.open();
        Cursor entries = ldb.getAllFoodEntries(date, user_id);
        int rows = entries.getCount();
        if (rows == 0) {
        } else {
            TextView first = findViewById(R.id.instructions);
            first.setVisibility(View.GONE);
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
            totrow.setBackgroundColor(Color.LTGRAY);
            TextView total = new TextView(this);
            totrow.setGravity(Gravity.CENTER);
            total.setText("Total:");
            total.setTextColor(Color.BLACK);
            total.setGravity(Gravity.CENTER);
            totrow.addView(total,lp);

            TextView total1 = new TextView(this);
            totrow.setGravity(Gravity.CENTER);
            total1.setText(valueOf(count));
            total1.setTextColor(Color.BLACK);
            total1.setGravity(Gravity.CENTER);
            totrow.addView(total1,lp);

            TextView total2 = new TextView(this);
            total2.setText(valueOf(totalCals));
            total2.setTextColor(Color.BLACK);
            total2.setGravity(Gravity.CENTER);
            totrow.addView(total2,lp);

            TextView total3 = new TextView(this);
            total3.setText(valueOf(totalCarbos));
            total3.setTextColor(Color.BLACK);
            total3.setGravity(Gravity.CENTER);
            totrow.addView(total3,lp);

            TextView total4 = new TextView(this);
            total4.setText(valueOf(totalProteins));
            total4.setTextColor(Color.BLACK);
            total4.setGravity(Gravity.CENTER);
            totrow.addView(total4,lp);
            table.addView(totrow);

            Integer targetCals = 0;
            Integer targetCarbs = 0;
            Integer targetProtein = 0;

            Cursor goals = ldb.getGoals(user_id);
            int num_goals = goals.getCount();
            if (num_goals == 0) {
                addGoals();
                goals = ldb.getGoals(user_id);
            }
            while (goals.moveToNext()){
                targetCals = goals.getInt(2);
                targetCarbs = goals.getInt(3);
                targetProtein = goals.getInt(4);
            }
            ldb.close();

            TableRow targetrow = new TableRow(this);
            targetrow.setBackgroundColor(Color.LTGRAY);

            Button target = new Button(this);
            target.setClickable(true);
            totrow.setGravity(Gravity.CENTER);
            target.setText("Set Target:");
            target.setTextColor(Color.BLACK);
            target.setGravity(Gravity.CENTER);
            targetrow.addView(target,lp);

            TextView target1 = new TextView(this);
            targetrow.setGravity(Gravity.CENTER);
            target1.setText("-");
            target1.setTextColor(Color.BLACK);
            target1.setGravity(Gravity.CENTER);
            targetrow.addView(target1,lp);

            TextView target2 = new TextView(this);
            target2.setText(valueOf(targetCals));
            target2.setTextColor(Color.BLACK);
            target2.setGravity(Gravity.CENTER);
            targetrow.addView(target2,lp);

            TextView target3 = new TextView(this);
            target3.setText(valueOf(targetCarbs));
            target3.setTextColor(Color.BLACK);
            target3.setGravity(Gravity.CENTER);
            targetrow.addView(target3,lp);

            TextView target4 = new TextView(this);
            target4.setText(valueOf(targetProtein));
            target4.setTextColor(Color.BLACK);
            target4.setGravity(Gravity.CENTER);
            targetrow.addView(target4,lp);
            table.addView(targetrow);

            for(final ImageButton minus : list){
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = minus.getId();
                        ldb.open();
                        boolean result = ldb.deleteFoodEntry(id);
                        if (result == true) {
                            Toast.makeText(getApplicationContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                            table.removeAllViews();
                            displayRows(table);
                            setupPieChart();
                        } else {
                            Toast.makeText(getApplicationContext(), "Unsuccessful delete", Toast.LENGTH_SHORT).show();
                        }
                        ldb.close();
                    }
                });
            }

            target.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goals = new Intent(food_log.this, edit_goals.class);
                    goals.putExtra("userId", user_id);
                    startActivity(goals);
                    customType(food_log.this,"up-to-bottom");
                }
            });
        }
    }


    @Override
    public void onBackPressed()
    {
        Intent data = new Intent(food_log.this,home.class);
        data.putExtra("userId", user_id);
        startActivity(data);
        customType(food_log.this,"fadein-to-fadeout");
        super.onBackPressed();
    }
}

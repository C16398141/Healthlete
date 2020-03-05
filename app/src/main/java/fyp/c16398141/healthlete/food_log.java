package fyp.c16398141.healthlete;

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
        /*Button addButton = findViewById(R.id.addButton);
        Spannable buttonLabel = new SpannableString(" Add New");
        buttonLabel.setSpan(new ImageSpan(getApplicationContext(), R.drawable.plus_circle,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        addButton.setText(buttonLabel);

*/
        //anyChartView = findViewById(R.id.any_chart_view);
        TableLayout table = (TableLayout) findViewById(R.id.food_table);

        ldb = new LocalDB(this);
        /*ldb.open();
        addGoals();
        ldb.close();*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init(table);

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

    protected void onDraw(Canvas canvas) {
        /*Float drawUpto = 46f;


        float mouthInset = mRadius / 3f;
        mArcBounds.set(mouthInset, mouthInset, mRadius * 2 - mouthInset, mRadius * 2 - mouthInset);
        canvas.drawArc(mArcBounds, 0f, 360f, false, circleGray);

        canvas.drawArc(mArcBounds, 270f, drawUpto, false, circleYellow);*/


    }
    public void init(TableLayout table) {
        ldb.open();
        displayRows(table);
        ldb.close();
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
        canvas.drawCircle(3*imageViewWidth/16, imageViewHeight/2, imageViewWidth/8, paint);
        canvas.drawCircle(8*imageViewWidth/16, imageViewHeight/2, imageViewWidth/8, paint);
        canvas.drawCircle(13*imageViewWidth/16, imageViewHeight/2, imageViewWidth/8, paint);

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
        RectF arc  = new RectF(imageViewWidth/16,imageViewHeight/2 - imageViewWidth/8,5*imageViewWidth/16,imageViewHeight/2 + imageViewWidth/8);
        canvas.drawArc (arc, -90, angle, true,  paint2);
        RectF arc2 = new RectF(6*imageViewWidth/16,imageViewHeight/2 - imageViewWidth/8,10*imageViewWidth/16,imageViewHeight/2 + imageViewWidth/8);
        canvas.drawArc (arc2, -90, angle2, true,  paint2);
        RectF arc3 = new RectF(11*imageViewWidth/16,imageViewHeight/2 - imageViewWidth/8,15*imageViewWidth/16,imageViewHeight/2 + imageViewWidth/8);
        canvas.drawArc (arc3, -90, angle3, true,  paint2);
        imageView.setImageBitmap(bitmap);
        /*
        circleYellow = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleYellow.setStyle(Paint.Style.FILL);
        circleYellow.setColor(Color.YELLOW);
        circleYellow.setStyle(Paint.Style.STROKE);
        circleYellow.setStrokeWidth(15 * getResources().getDisplayMetrics().density);
        circleYellow.setStrokeCap(Paint.Cap.SQUARE);
        // mEyeAndMouthPaint.setColor(getResources().getColor(R.color.colorAccent));
        circleYellow.setColor(Color.parseColor("#F9A61A"));

        circleGray = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleGray.setStyle(Paint.Style.FILL);
        circleGray.setColor(Color.GRAY);
        circleGray.setStyle(Paint.Style.STROKE);
        circleGray.setStrokeWidth(15 * getResources().getDisplayMetrics().density);
        circleGray.setStrokeCap(Paint.Cap.SQUARE);
        // mEyeAndMouthPaint.setColor(getResources().getColor(R.color.colorAccent));
        circleGray.setColor(Color.parseColor("#76787a"));
         */
    }

    public void addGoals()
    {
        String type = "weightLoss";
        Integer targetCals = 2000;
        Integer targetCarbs = 400;
        Integer targetProtein = 200;
        Boolean result = ldb.addGoals("lose weight",2000,400,200);
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
                //RETURNING FROM TOP OF SCREEN SHOWS CHANGES WHILE BOTTOM BUTTON RETURN DOESN'T REFRESH PARENT
            }
        });

        totalCalories.clear();
        totalCarbs.clear();
        totalProtein.clear();
        Cursor entries = ldb.getAllFoodEntries();
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
                TextView t3v = new TextView(this);
                Integer cals = entries.getInt(4);
                totalCalories.add(cals);
                t3v.setText(Integer.toString(cals));
                t3v.setBackgroundColor(Color.WHITE);
                t3v.setTextColor(Color.BLACK);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v,lp);
                TextView t4v = new TextView(this);
                Integer carbs = entries.getInt(5);
                totalCarbs.add(carbs);
                t4v.setText(Integer.toString(carbs));
                t4v.setBackgroundColor(Color.WHITE);
                t4v.setTextColor(Color.BLACK);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v,lp);
                TextView t5v = new TextView(this);
                Integer protein = entries.getInt(6);
                totalProtein.add(protein);
                t5v.setText(Integer.toString(protein));
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
}


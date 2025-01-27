package fyp.c16398141.healthlete;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static maes.tech.intentanim.CustomIntent.customType;

public class home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private static String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        try{
            user = firebaseUser.getEmail();
        } catch (NullPointerException e){
            Intent home = new Intent(home.this, MainActivity.class);
            startActivity(home);
            customType(home.this,"right-to-left");
        }

        getSupportActionBar().setTitle(user);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(home.this);
        View headerView = navigationView.getHeaderView(0);

        TextView navUsername = (TextView) headerView.findViewById(R.id.username);
        navUsername.setText(user);

        TextView navName = (TextView) headerView.findViewById(R.id.first_name);
        String first_name = firebaseUser.getDisplayName();
        navName.setText(first_name);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        //toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();


        MaterialCardView food_card = findViewById(R.id.food);
        MaterialCardView fitness_card = findViewById(R.id.fitness);
        MaterialCardView achievement_card = findViewById(R.id.achievement);

        food_card.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(home.this, food_log.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userId", user);
                startActivity(intent);
                customType(home.this,"fadein-to-fadeout");
            }
        });

        fitness_card.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(home.this, fitness.class);
                intent.putExtra("userId", user);
                startActivity(intent);
                customType(home.this,"fadein-to-fadeout");
            }
        });

        achievement_card.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(home.this, achievements.class);
                intent.putExtra("userId", user);
                startActivity(intent);
                customType(home.this,"fadein-to-fadeout");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            user = data.getStringExtra("userId");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.addgym:
                Intent intent = new Intent(home.this, workout_area.class);
                intent.putExtra("userId", user);
                startActivity(intent);
                customType(home.this,"up-to-bottom");
                break;

            case R.id.reminder:
                Intent remind = new Intent(home.this, reminders_setup.class);
                remind.putExtra("userId", user);
                startActivity(remind);
                customType(home.this,"up-to-bottom");
                break;

            case R.id.editgoals:
                Intent goals = new Intent(home.this, edit_goals.class);
                goals.putExtra("userId", user);
                startActivity(goals);
                customType(home.this,"up-to-bottom");
                break;

            case R.id.howtoguide:
                String url = "https://youtu.be/Xfz_hpAGukQ";
                Intent guide = new Intent(Intent.ACTION_VIEW);
                guide.setData(Uri.parse(url));
                startActivity(guide);
                break;

            case R.id.sign_out:
                Intent sign_out = new Intent(home.this, MainActivity.class);
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),"Signed out",Toast.LENGTH_SHORT).show();
                startActivity(sign_out);
                customType(home.this,"right-to-left");
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

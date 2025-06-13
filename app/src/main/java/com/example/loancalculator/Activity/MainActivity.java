package com.example.loancalculator.Activity;  // Change this to your actual package name

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.loancalculator.Fragment.addscheme;
import com.example.loancalculator.Fragment.home_fragment;
import com.example.loancalculator.Fragment.user_manual;
import com.example.loancalculator.R;
import com.example.loancalculator.purity.set_purity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar topAppBar;
    private ActionBarDrawerToggle toggle;
    FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // your XML layout file

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.gold_yellow));
        }

        setFrameLayout(new home_fragment());
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        topAppBar = findViewById(R.id.topAppBar);

        // Setup ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                topAppBar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Optional: Handle NavigationView item clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                   setFrameLayout(new home_fragment());
                } else  if (id == R.id.set_purity) {
                   setFrameLayout(new set_purity());
                } else if (id == R.id.set_scheme) {
                    setFrameLayout(new addscheme());
                } else if (id == R.id.nav_manual) {
                    setFrameLayout(new user_manual());
                }

                drawerLayout.closeDrawers(); // close drawer after selection
                return true;
            }
        });
    }
    public void setFrameLayout(Fragment fragment){
        frameLayout = findViewById(R.id.main_container);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();
    }

}

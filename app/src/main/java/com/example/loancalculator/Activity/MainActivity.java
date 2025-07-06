package com.example.loancalculator.Activity;  // Change this to your actual package name

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.loancalculator.Fragment.SchemeView;
import com.example.loancalculator.Fragment.addscheme;
import com.example.loancalculator.Fragment.home_fragment;
import com.example.loancalculator.Fragment.user_manual;
import com.example.loancalculator.R;
import com.example.loancalculator.purity.set_purity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    String ltvpass;
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
        navigationView.setItemTextColor(new ColorStateList(
                new int[][] {
                        new int[] { android.R.attr.state_checked },
                        new int[] {} // default
                },
                new int[] {
                        ContextCompat.getColor(this, R.color.text_red),
                        ContextCompat.getColor(this, R.color.text_grey)
                }
        ));

        navigationView.setItemIconTintList(new ColorStateList(
                new int[][] {
                        new int[] { android.R.attr.state_checked },
                        new int[] {} // default
                },
                new int[] {
                        ContextCompat.getColor(this, R.color.gold_yellow), // selected icon color
                        ContextCompat.getColor(this, R.color.black)        // default icon color
                }
        ));

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
                // Clear previous selection
                for (int i = 0; i < navigationView.getMenu().size(); i++) {
                    MenuItem menuItem = navigationView.getMenu().getItem(i);
                    menuItem.setChecked(false);
                    // Also clear group items (like settings_group)
                    if (menuItem.hasSubMenu()) {
                        for (int j = 0; j < menuItem.getSubMenu().size(); j++) {
                            menuItem.getSubMenu().getItem(j).setChecked(false);
                        }
                    }
                }

                // Set current selection
                item.setChecked(true);

                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    setFrameLayout(new home_fragment());
                } else if (id == R.id.set_purity) {
                    setFrameLayout(new set_purity());
                } else if (id == R.id.nav_scheme) {
                    setFrameLayout(new SchemeView());
                } else if (id == R.id.set_scheme) {
                    setFrameLayout(new addscheme());
                } else if (id == R.id.nav_manual) {
                    setFrameLayout(new user_manual());
                } else if (id == R.id.nav_authenticate) {
                    showAuthDialog();
                    return true;
                }else if (id == R.id.nav_share) {
                    shareAppFromFirebase();  // Show both options
                    return true;
                }


                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }
    public void setFrameLayout(Fragment fragment){
        frameLayout = findViewById(R.id.main_container);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();
    }

    private void showAuthDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Authentication");

        final EditText input = new EditText(this);
        input.setHint("Enter password");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String enteredPassword = input.getText().toString().trim();
            fetchBaseLTV(this, ltvPrice -> {
                // Now you can use ltvPrice (as String)
                Toast.makeText(this, "LTV "+ltvPrice, Toast.LENGTH_SHORT).show();
                ltvpass=ltvPrice;
            });

            if (enteredPassword.equals("1")) {
                // Show the settings group
                NavigationView navView = findViewById(R.id.nav_view);
                Menu menu = navView.getMenu();
                menu.setGroupVisible(R.id.settings_group, true);
                Toast.makeText(this, "Access granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    public static void fetchBaseLTV(Context context, OnLtvFetchedListener listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("BaseLTV");

        ref.get().addOnSuccessListener(snapshot -> {
            Float value = snapshot.getValue(Float.class);
            if (value != null) {
                listener.onFetched(String.valueOf(value));
            } else {
                listener.onFetched("0");
            }
        }).addOnFailureListener(e -> listener.onFetched("0"));
    }

    public interface OnLtvFetchedListener {
        void onFetched(String ltvPrice);
    }


    private void shareAppFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AppDownloadLink");

        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String apkUrl = snapshot.getValue(String.class);
                shareAppLink(apkUrl);
            } else {
                Toast.makeText(this, "Download link not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to fetch link: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private void shareAppLink(String link) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Loan Calculator App");
        intent.putExtra(Intent.EXTRA_TEXT, "Download the Loan Calculator App:\n" + link);
        startActivity(Intent.createChooser(intent, "Share App via"));
    }


}

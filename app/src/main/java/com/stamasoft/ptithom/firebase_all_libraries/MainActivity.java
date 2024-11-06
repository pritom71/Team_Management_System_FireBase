package com.stamasoft.ptithom.firebase_all_libraries;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    // private FirebaseAuth.AuthStateListener authListener; // Commented out for now

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.your_color));
        }

        mAuth = FirebaseAuth.getInstance();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hide the app name (title) from the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(""); // Hide title
        }

        // Setup DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Setup ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setup BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(this);

        // Default selection
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            bottomNav.setSelectedItemId(R.id.nav_home);
        }

        // Add Log statements to debug
        Log.d("MainActivity", "onCreate: Activity created");

        // Temporarily remove AuthStateListener to check behavior after login
        /*
        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // User is not logged in, redirect to LoginActivity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish(); // Close MainActivity
            }
        };
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        // mAuth.addAuthStateListener(authListener); // Commented out for now
        Log.d("MainActivity", "onStart: Activity started");
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
        */
        Log.d("MainActivity", "onStop: Activity stopped");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int id = item.getItemId();

        // Handle Bottom Navigation items
        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_dashboard) {
            selectedFragment = new DashboardFragment();
        } else if (id == R.id.nav_add) {
            selectedFragment = new AddUserFragment();
        } else if (id == R.id.nav_notifications) {
            selectedFragment = new NotificationsFragment();
        }

        // Handle Navigation Drawer items
        if (selectedFragment == null) {
            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            } else if (id == R.id.nav_share) {
                selectedFragment = new ShareFragment();
            } else if (id == R.id.nav_about) {
                selectedFragment = new AboutFragment();
            } else if (id == R.id.nav_logout) {
                mAuth.signOut();
                // Log out and close MainActivity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            }
        }

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .addToBackStack(null)  // Add this line if needed
                    .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

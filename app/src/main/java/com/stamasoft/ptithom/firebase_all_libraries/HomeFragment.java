// HomeFragment.java
package com.stamasoft.ptithom.firebase_all_libraries;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private Handler handler;
    private Runnable runnable;
    private int currentPage = 0;
    private static final int SLIDE_DELAY = 5000; // 5 seconds delay between slides

    private RecyclerView employeeRecyclerView;
    private EmployeeAdapter employeeAdapter;
    private List<Employee> employeeList;
    private DatabaseReference employeeDbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize ViewPager2 for image slider
        viewPager = view.findViewById(R.id.viewPager);
        setupImageSlider();

        // Initialize RecyclerView for employee list
        employeeRecyclerView = view.findViewById(R.id.employeeRecyclerView);
        employeeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        employeeList = new ArrayList<>();
        employeeAdapter = new EmployeeAdapter(getContext(), employeeList);
        employeeRecyclerView.setAdapter(employeeAdapter);

        // Initialize Firebase reference
        employeeDbRef = FirebaseDatabase.getInstance().getReference("Employees");

        // Fetch employee data from Firebase
        fetchEmployeeData();

        return view;
    }

    private void setupImageSlider() {
        // List of images to display
        List<Integer> images = Arrays.asList(
                R.drawable.teamwork,
                R.drawable.teamwork2,
                R.drawable.teamwork3
        );

        // Create and set the adapter for ViewPager2
        ImageSliderAdapter adapter = new ImageSliderAdapter(images);
        viewPager.setAdapter(adapter);

        // Initialize the handler for automatic image sliding
        handler = new Handler(Looper.getMainLooper());
        startAutoSlide();
    }

    private void startAutoSlide() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == viewPager.getAdapter().getItemCount()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, SLIDE_DELAY);
            }
        };
        handler.postDelayed(runnable, SLIDE_DELAY);
    }

    private void fetchEmployeeData() {
        employeeDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                employeeList.clear();

                for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                    Employee employee = employeeSnapshot.getValue(Employee.class);
                    employeeList.add(employee);
                }

                employeeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors here
                // For example, you can show a Toast message
                // Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}

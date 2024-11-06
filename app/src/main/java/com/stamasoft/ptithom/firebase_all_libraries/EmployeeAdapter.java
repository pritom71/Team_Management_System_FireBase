package com.stamasoft.ptithom.firebase_all_libraries;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private Context context;
    private List<Employee> employeeList;

    public EmployeeAdapter(Context context, List<Employee> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_list_item, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);

        holder.tvName.setText(employee.getName());
        holder.tvDesignation.setText(employee.getDesignation());
        holder.tvPhone.setText(employee.getPhone());
        holder.tvEmail.setText(employee.getEmail());

        // Load the employee's image using Glide
        if (employee.getImageUrl() != null && !employee.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(employee.getImageUrl())
                    .placeholder(R.drawable.boy)  // Replace with an appropriate placeholder
                    .error(R.drawable.user)  // Replace with an appropriate error image
                    .into(holder.ivEmployeeImage);
        } else {
            holder.ivEmployeeImage.setImageResource(R.drawable.boy);  // Set default image
        }

        // Optional: Set click listeners
        holder.tvPhone.setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + employee.getPhone()));
            context.startActivity(dialIntent);
        });

        holder.tvEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + employee.getEmail()));
            context.startActivity(Intent.createChooser(emailIntent, "Send email"));
        });

        holder.cardView.setOnClickListener(v -> {
            Toast.makeText(context, "Clicked on: " + employee.getName(), Toast.LENGTH_SHORT).show();
            // Add more actions here if needed
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvDesignation, tvPhone, tvEmail;
        ImageView ivEmployeeImage;  // ImageView for employee image
        CardView cardView;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEmployeeName);
            tvDesignation = itemView.findViewById(R.id.tvEmployeeDesignation);
            tvPhone = itemView.findViewById(R.id.tvEmployeePhone);
            tvEmail = itemView.findViewById(R.id.tvEmployeeEmail);
            ivEmployeeImage = itemView.findViewById(R.id.ivEmployeeImage);  // Initialize ImageView
            cardView = (CardView) itemView;
        }
    }
}

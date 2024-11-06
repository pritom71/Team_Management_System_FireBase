package com.stamasoft.ptithom.firebase_all_libraries;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Employee> {

    private Activity mContext;
    List<Employee> employeeList;

    public ListAdapter(Activity mContext, List<Employee> employeeList) {
        super(mContext, R.layout.custom_layout, employeeList);
        this.mContext = mContext;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom_layout, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvEmail = convertView.findViewById(R.id.tvEmail);
        TextView tvPhone = convertView.findViewById(R.id.tvPhone);
        TextView tvDesignation = convertView.findViewById(R.id.tvDesignation);
        TextView tvGender = convertView.findViewById(R.id.tvGender);
        TextView tvBloodGroup = convertView.findViewById(R.id.tvBloodGroup);
        ImageView ivEmployeeImage = convertView.findViewById(R.id.ivEmployeeImage);

        Employee employee = employeeList.get(position);

        tvName.setText(employee.getName());
        tvEmail.setText(employee.getEmail());
        tvPhone.setText(employee.getPhone());
        tvDesignation.setText(employee.getDesignation());
        tvGender.setText(employee.getGender());
        tvBloodGroup.setText(employee.getBloodGroup());

        // Load the image using Glide
        Glide.with(mContext)
                .load(employee.getImageUrl())  // Load the image URL from the Employee object
                .apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user))  // Optional: add placeholder and error images
                .into(ivEmployeeImage);

        return convertView;
    }
}

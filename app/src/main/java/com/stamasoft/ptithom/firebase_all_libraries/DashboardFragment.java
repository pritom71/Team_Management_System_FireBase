package com.stamasoft.ptithom.firebase_all_libraries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    ListView myListView;
    List<Employee> employeeList;
    DatabaseReference employeeDbRef;
    StorageReference storageRef;
    private Uri imageUri;
    private ImageView ivEmployeeImage;

    // Register the image picker result launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    imageUri = result.getData().getData();
                    if (ivEmployeeImage != null) {
                        ivEmployeeImage.setImageURI(imageUri);
                    }
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        myListView = view.findViewById(R.id.myListView);
        employeeList = new ArrayList<>();

        employeeDbRef = FirebaseDatabase.getInstance().getReference("Employees");
        storageRef = FirebaseStorage.getInstance().getReference("EmployeeImages");

        employeeDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                employeeList.clear();
                for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                    Employee employee = employeeSnapshot.getValue(Employee.class);
                    employeeList.add(employee);
                }
                ListAdapter adapter = new ListAdapter(getActivity(), employeeList);
                myListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors here
            }
        });

        myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Employee employee = employeeList.get(position);
                showUpdateDeleteDialog(employee.getId(), employee.getName(), employee.getEmail(), employee.getPhone(), employee.getDesignation(), employee.getGender(), employee.getBloodGroup(), employee.getImageUrl());
                return true;
            }
        });

        return view;
    }

    private void showUpdateDeleteDialog(final String id, String name, String email, String phone, String designation, String gender, String bloodGroup, final String imageUrl) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update_dialogue, null);

        dialogBuilder.setView(dialogView);

        final EditText etUpdateName = dialogView.findViewById(R.id.et_name);
        final EditText etUpdateEmail = dialogView.findViewById(R.id.et_email);
        final EditText etUpdatePhone = dialogView.findViewById(R.id.et_phone);
        final EditText etUpdateDesignation = dialogView.findViewById(R.id.et_designation);
        final Spinner spinnerUpdateGender = dialogView.findViewById(R.id.spinner_update_gender);
        final Spinner spinnerUpdateBloodGroup = dialogView.findViewById(R.id.spinner_update_blood_group);
        ivEmployeeImage = dialogView.findViewById(R.id.iv_employee_image);

        Button btnUpdate = dialogView.findViewById(R.id.bt_update);
        Button btnDelete = dialogView.findViewById(R.id.bt_delete);

        // Populate the dialog with current values
        etUpdateName.setText(name);
        etUpdateEmail.setText(email);
        etUpdatePhone.setText(phone);
        etUpdateDesignation.setText(designation);

        // Set spinners to current values
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUpdateGender.setAdapter(genderAdapter);
        int genderPosition = genderAdapter.getPosition(gender);
        spinnerUpdateGender.setSelection(genderPosition);

        ArrayAdapter<CharSequence> bloodGroupAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.blood_group_options, android.R.layout.simple_spinner_item);
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUpdateBloodGroup.setAdapter(bloodGroupAdapter);
        int bloodGroupPosition = bloodGroupAdapter.getPosition(bloodGroup);
        spinnerUpdateBloodGroup.setSelection(bloodGroupPosition);

        // Load the current image into ImageView
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(getContext()).load(imageUrl).into(ivEmployeeImage);
        }

        ivEmployeeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog_background);
        alertDialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etUpdateName.getText().toString();
                String newEmail = etUpdateEmail.getText().toString();
                String newPhone = etUpdatePhone.getText().toString();
                String newDesignation = etUpdateDesignation.getText().toString();
                String newGender = spinnerUpdateGender.getSelectedItem().toString();
                String newBloodGroup = spinnerUpdateBloodGroup.getSelectedItem().toString();

                updateEmployee(id, newName, newEmail, newPhone, newDesignation, newGender, newBloodGroup, imageUrl);
                Toast.makeText(getActivity(), "Employee Updated", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEmployee(id);
                Toast.makeText(getActivity(), "Employee Deleted", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }

    private void updateEmployee(String id, String name, String email, String phone, String designation, String gender, String bloodGroup, String imageUrl) {
        DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference("Employees").child(id);
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child(id + ".jpg");
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String newImageUrl = uri.toString();
                            Employee employee = new Employee(id, name, email, phone, designation, gender, bloodGroup, newImageUrl);
                            employeeRef.setValue(employee);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle possible errors here
                            Toast.makeText(getActivity(), "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle possible errors here
                    Toast.makeText(getActivity(), "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Employee employee = new Employee(id, name, email, phone, designation, gender, bloodGroup, imageUrl);
            employeeRef.setValue(employee);
        }
    }

    private void deleteEmployee(String id) {
        DatabaseReference employeeRef = FirebaseDatabase.getInstance().getReference("Employees").child(id);
        employeeRef.removeValue();
    }
}

package com.stamasoft.ptithom.firebase_all_libraries;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddUserFragment extends Fragment {

    private EditText etName, etEmail, etPhone, etDesignation;
    private Spinner spinnerGender, spinnerBloodGroup;
    private Button btnSubmit;
    private ImageView preimg;

    private DatabaseReference employeeDbRef;
    private StorageReference storageReference;
    private Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        // Initialize UI components
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etDesignation = view.findViewById(R.id.etDesignation);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        spinnerBloodGroup = view.findViewById(R.id.spinnerBloodGroup);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        preimg = view.findViewById(R.id.ivPreview);

        // Initialize Firebase Database and Storage references
        employeeDbRef = FirebaseDatabase.getInstance().getReference("Employees");
        storageReference = FirebaseStorage.getInstance().getReference("EmployeeImages");

        // Set onClickListener for image selection
        preimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // Set onClickListener for the submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadImage();
                } else {
                    Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            preimg.setImageURI(imageUri); // Set the selected image to ImageView
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            final String id = employeeDbRef.push().getKey();
            StorageReference fileReference = storageReference.child(id + ".jpg");

            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            insertEmployeeData(imageUrl);
                        }
                    });
                }
            });
        }
    }

    private void insertEmployeeData(String imageUrl) {
        // Get data from input fields
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String designation = etDesignation.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();

        // Validate input fields
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || designation.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique ID for each employee
        String id = employeeDbRef.push().getKey();

        // Create an Employee object
        Employee employee = new Employee(id, name, email, phone, designation, gender, bloodGroup, imageUrl);

        // Insert data into Firebase database
        assert id != null;
        employeeDbRef.child(id).setValue(employee);

        // Show success message
        Toast.makeText(getActivity(), "Employee data inserted successfully!", Toast.LENGTH_SHORT).show();

        // Optionally, clear the input fields after successful insertion
        etName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etDesignation.setText("");
        spinnerGender.setSelection(0);
        spinnerBloodGroup.setSelection(0);
        preimg.setImageResource(R.drawable.team);
        imageUri = null;
    }
}

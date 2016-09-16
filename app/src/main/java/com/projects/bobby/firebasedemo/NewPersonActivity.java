package com.projects.bobby.firebasedemo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.projects.bobby.firebasedemo.models.Person;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class NewPersonActivity extends PersonActivity {

    private static final String TAG = "NewPersonActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    protected void submitPerson() {
        if (!validateForm()) {
            return;
        }

        final String firstName = mFirstNameField.getText().toString();
        final String lastName = mLastNameField.getText().toString();
        final String zip = mZipField.getText().toString();
        final String dob = mDobField.getText().toString();

        // Disabled editing to prevent multi-posting
        setEditingEnabled(false);
        Toast.makeText(this, "Adding person...", Toast.LENGTH_SHORT).show();


        writeNewPerson(firstName, lastName, zip, dob);
    }

    private void writeNewPerson(String firstName, String lastName, String zip, String dob) {
        Log.d(TAG, "writeNewPerson:" + firstName + " " + lastName);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date dateDob;
        try {
            // Parse date of birth string to a date object
            dateDob = sdf.parse(dob);

            Person person = new Person(firstName, lastName, zip, dateDob);
            Map<String, Object> personValues = person.toMap();
            mDatabase.child("people").push().setValue(personValues);

            setEditingEnabled(true);
            finish();
        } catch (Exception e) {
            // Date format is checked prior to parsing. This should never happen.
            e.printStackTrace();
            Toast.makeText(this, "Failed to add person.", Toast.LENGTH_SHORT).show();
            setEditingEnabled(true);
        }
    }
}

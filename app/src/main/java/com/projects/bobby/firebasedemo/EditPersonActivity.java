package com.projects.bobby.firebasedemo;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.bobby.firebasedemo.models.Person;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by bobby on 9/14/16.
 */
public class EditPersonActivity extends PersonActivity {

    private static final String TAG = "EditPersonActivity";

    public static final String EXTRA_PERSON_KEY = "person_key";

    private ValueEventListener mPersonListener;
    private String mPersonKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPersonKey = getIntent().getStringExtra(EXTRA_PERSON_KEY);
        if (mPersonKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_PERSON_KEY");
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("people").child(mPersonKey);
    }

    @Override
    public void onStart() {
        super.onStart();

        ValueEventListener personListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Person person = dataSnapshot.getValue(Person.class);
                mFirstNameField.setText(person.getFirstName());
                mLastNameField.setText(person.getLastName());
                mZipField.setText(person.getZip());
                mDobField.setText(DateFormat.format("MM/dd/yyyy", person.getDob()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Person failed, log a message
                Log.w(TAG, "loadPerson:onCancelled", databaseError.toException());
                Toast.makeText(EditPersonActivity.this, "Failed to load person.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addValueEventListener(personListener);

        mPersonListener = personListener;
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
        Toast.makeText(this, "Updating person...", Toast.LENGTH_SHORT).show();

        updatePerson(firstName, lastName, zip, dob);
    }

    private void updatePerson(String firstName, String lastName, String zip, String dob) {
        Log.d(TAG, "updatePerson:dob:" + dob);
        Log.d(TAG, "updatePerson:" + firstName + " " + lastName);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date dateDob;
        try {
            dateDob = sdf.parse(dob);

            Person person = new Person(firstName, lastName, zip, dateDob);
            Map<String, Object> personValues = person.toMap();
            mDatabase.setValue(personValues);

            setEditingEnabled(true);
            finish();
        } catch (Exception e) {
            // Date format is checked prior to parsing. This should never happen.
            e.printStackTrace();
            Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show();
            setEditingEnabled(true);
        }

    }
}

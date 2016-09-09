package com.projects.bobby.servicefusiondemo;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projects.bobby.servicefusiondemo.fragment.DatePickerFragment;
import com.projects.bobby.servicefusiondemo.models.Person;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewPersonActivity extends AppCompatActivity implements
        View.OnClickListener{

    private static final String TAG = "NewPersonActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;

    private EditText mFirstNameField;
    private EditText mLastNameField;
    private EditText mZipField;
    private EditText mDobField;
    private ImageButton mSelectDateButton;
    private FloatingActionButton mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_person);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mFirstNameField = (EditText) findViewById(R.id.field_first_name);
        mLastNameField = (EditText) findViewById(R.id.field_last_name);
        mZipField = (EditText) findViewById(R.id.field_zip);
        mDobField = (EditText) findViewById(R.id.field_dob);
        mSelectDateButton = (ImageButton) findViewById(R.id.button_select_date);
        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_person);

        mSelectDateButton.setOnClickListener(this);
        mSubmitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.button_select_date:
                showDatePickerDialog();
                break;
            case R.id.fab_submit_person:
                submitPerson();
        }
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void submitPerson() {
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
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date dateDob;
        try {
            // Parse date of birth string to a date object
            dateDob = sdf.parse(dob);

            Person person = new Person(firstName, lastName, zip, dateDob);
            Map<String, Object> personValues = person.toMap();
            mDatabase.child("People").push().setValue(personValues);

            setEditingEnabled(true);
            finish();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String firstName = mFirstNameField.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameField.setError("Required.");
            valid = false;
        } else {
            mFirstNameField.setError(null);
        }

        String lastName = mLastNameField.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            mLastNameField.setError("Required.");
            valid = false;
        } else {
            mLastNameField.setError(null);
        }

        String zip = mZipField.getText().toString();
        if (TextUtils.isEmpty(zip)) {
            mZipField.setError("Required.");
            valid = false;
        } else if (!zip.matches("^\\d{5}(-\\d{4})?$")) {
            mZipField.setError("Zip code formatted incorrectly.");
            valid = false;
        } else {
            mZipField.setError(null);
        }

        String dob = mDobField.getText().toString();
        if (TextUtils.isEmpty(dob)) {
            mDobField.setError("Required.");
            valid = false;
        } else {
            //Pattern p = Pattern.compile("^(0[1-9]|1[012])/(0[1-9]|[12][0-9]|3[01])/((19|20)\\d\\d)$");
            Pattern p = Pattern.compile("^(\\d\\d)/(\\d\\d)/(\\d\\d\\d\\d)$");
            Matcher m = p.matcher(dob);
            if (m.matches()) {
                // Date has been entered, and it is formatted properly, but is it valid?
                int year = Integer.parseInt(m.group(3));
                int month = Integer.parseInt(m.group(1));
                int day = Integer.parseInt(m.group(2));

                if (day > 31 || month > 12) {
                    // Day or month higher values than they could ever be
                    mDobField.setError("Date is invalid");
                    valid = false;
                } else if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
                    // 31st of a month with only 30 days
                    mDobField.setError("Date is invalid.");
                    valid = false;
                } else if (month == 2 && (day == 31 || day == 30)) {
                    // February 30th or 31st
                    mDobField.setError("Date is invalid.");
                    valid = false;
                } else if (month == 2 && day == 29 && !(year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))) {
                    // February 29th outside of a leap year
                    mDobField.setError("Date is invalid.");
                    valid = false;
                } else {
                    mDobField.setError(null);
                }
            } else {
                // Format is incorrect
                mDobField.setError("Date formatted incorrectly.");
                valid = false;
            }
        }
        return valid;
    }

    private void setEditingEnabled(boolean enabled) {
        mFirstNameField.setEnabled(enabled);
        mLastNameField.setEnabled(enabled);
        mZipField.setEnabled(enabled);
        mDobField.setEnabled(enabled);
        mSelectDateButton.setEnabled(enabled);

        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }
}

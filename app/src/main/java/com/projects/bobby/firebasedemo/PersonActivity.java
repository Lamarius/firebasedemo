package com.projects.bobby.firebasedemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.projects.bobby.firebasedemo.fragment.DatePickerFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bobby on 9/14/16.
 */
public abstract class PersonActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "PersonActivity";

    // Error messages
    private static final String REQUIRED = "Required.";
    private static final String ZIP_FORMAT = "Zip code incorrectly formatted.";
    private static final String DATE_FORMAT = "Date incorrectly formatted.";
    private static final String DATE_INVALID = "Date is invalid.";

    protected DatabaseReference mDatabase;

    protected EditText mFirstNameField;
    protected EditText mLastNameField;
    protected EditText mZipField;
    protected EditText mDobField;
    protected ImageButton mSelectDateButton;
    protected FloatingActionButton mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

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
    public void onClick(View v) {
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

    protected abstract void submitPerson();

    protected boolean validateForm() {
        boolean valid = true;

        // Validate first name
        String firstName = mFirstNameField.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameField.setError(REQUIRED);
            valid = false;
        } else {
            mFirstNameField.setError(null);
        }

        // Validate last name
        String lastName = mLastNameField.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            mLastNameField.setError(REQUIRED);
            valid = false;
        } else {
            mLastNameField.setError(null);
        }

        // Validate zip code
        String zip = mZipField.getText().toString();
        if (TextUtils.isEmpty(zip)) {
            mZipField.setError(REQUIRED);
            valid = false;
        } else if (!zip.matches("^\\d{5}(-\\d{4})?$")) {
            mZipField.setError(ZIP_FORMAT);
            valid = false;
        } else {
            mZipField.setError(null);
        }

        // Validate date of birth
        String dob = mDobField.getText().toString();
        if (TextUtils.isEmpty(dob)) {
            mDobField.setError(REQUIRED);
            valid = false;
        } else {
            Pattern p = Pattern.compile("^(\\d{2})/(\\d{2})/(\\d{4})$");
            Matcher m = p.matcher(dob);
            if (m.matches()) {
                // Date has been entered, and it is formatted properly, but is it a valid date?
                int year = Integer.parseInt(m.group(3));
                int month = Integer.parseInt(m.group(1));
                int day = Integer.parseInt(m.group(2));

                if (day > 31 || month > 12) {
                    // Day or month values higher than they could ever be
                    mDobField.setError(DATE_INVALID);
                    valid = false;
                } else if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
                    // 31st of a month with only 30 days
                    mDobField.setError(DATE_INVALID);
                    valid = false;
                } else if (month == 2 && (day == 31 || day == 30)) {
                    // February 30th or 31st
                    mDobField.setError(DATE_INVALID);
                    valid = false;
                } else if (month == 2 && day == 29 && !(year % 4 == 0 &&
                        (year % 100 != 0 || year % 400 == 0))) {
                    // February 29th outside of a leap year
                    mDobField.setError(DATE_INVALID);
                    valid = false;
                } else {
                    // Everything checks out
                    mDobField.setError(null);
                }
            } else {
                // Date is formatted incorrectly
                mDobField.setError(DATE_FORMAT);
                valid = false;
            }
        }
        return valid;
    }

    protected void setEditingEnabled(boolean enabled) {
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

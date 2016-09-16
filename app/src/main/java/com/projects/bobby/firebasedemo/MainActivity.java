package com.projects.bobby.firebasedemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projects.bobby.firebasedemo.models.Person;
import com.projects.bobby.firebasedemo.viewHolder.PersonViewHolder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";
    private String mUsername;
    private SharedPreferences mSharedPreferences;

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<Person, PersonViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;

    // Firebase instance variables
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set default username to anonymous.
        mUsername = ANONYMOUS;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        // Initialize RecyclerView
        mRecycler = (RecyclerView) findViewById(R.id.recyclerView_person);
        mManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAdapter = new FirebaseRecyclerAdapter<Person, PersonViewHolder>(
                Person.class,
                R.layout.item_person,
                PersonViewHolder.class,
                mDatabase.child("people").orderByChild("firstName")) {

            @Override
            protected void populateViewHolder(PersonViewHolder viewHolder, Person model, int position) {
                final DatabaseReference personRef = getRef(position);
                viewHolder.bindToPerson(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View personView) {
                        final DatabaseReference ref = mDatabase.child("people").child(personRef.getKey());
                        switch (personView.getId()) {
                            case R.id.edit:
                                Intent intent = new Intent(personView.getContext(), EditPersonActivity.class);
                                intent.putExtra(EditPersonActivity.EXTRA_PERSON_KEY, personRef.getKey());
                                startActivity(intent);
                                //onEditClicked(ref);
                                break;
                            case R.id.delete:
                                onDeleteClicked(ref);
                                break;
                        }
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    private void onEditClicked(final DatabaseReference personRef) {
        final Intent intent = new Intent(this, EditPersonActivity.class);
        intent.putExtra(EditPersonActivity.EXTRA_PERSON_KEY, personRef.getKey());
        startActivity(intent);
    }

    private void onDeleteClicked(final DatabaseReference personRef) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Delete item.
                        personRef.removeValue();
                        break;
                    default:
                        // Do nothing.
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_entry_menu:
                Log.d(TAG, "onOptionsSelected:new_entry_menu");
                startActivity(new Intent(this, NewPersonActivity.class));
                return true;
            case R.id.sign_out_menu:
                mAuth.signOut();
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            case R.id.settings_menu:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

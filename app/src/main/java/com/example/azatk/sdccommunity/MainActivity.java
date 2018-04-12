package com.example.azatk.sdccommunity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;
    private DatabaseReference mUserRef;

    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SDC Community");

        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        }else{
            mUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
            mUserRef.child("online").setValue(true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){

            mUserRef.child("online")/*.setValue(ServerValue.TIMESTAMP)*/;

        }


    }

    private void sendToStart() {
        Intent intent = new Intent(MainActivity.this, StartActivty.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.main_logout_btn:
                FirebaseAuth.getInstance().signOut();
                sendToStart();
                break;
            case R.id.main_settings_btn:
                Intent sittingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(sittingsIntent);
                break;
            case R.id.main_all_btn:
                Intent usersIntent = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(usersIntent);
                break;
            case R.id.main_create_btn:
                Intent createIntent = new Intent(this,CreateGroupActivity.class);
                startActivity(createIntent);
                break;
            case R.id.main_create_btn2:
                Intent createIntent2 = new Intent(this,CreateGroupActivity2.class);
                startActivity(createIntent2);
                break;
        }

        return true;
    }
}

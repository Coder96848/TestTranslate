package com.example.testtranslate.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;

import com.example.testtranslate.App;
import com.example.testtranslate.interfaces.IFragment;
import com.example.testtranslate.R;
import com.example.testtranslate.ui.fragments.MainFragment;

public class MainActivity extends AppCompatActivity implements IFragment {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment_container);
        if (currentFragment == null) {
            setFragment(new MainFragment(), MainFragment.class.getSimpleName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.closeDb();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            this.finish();
        }
    }

    @Override
    public void setFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.activity_main_fragment_container, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentBackStack() {
        getSupportFragmentManager().popBackStack();
    }

}

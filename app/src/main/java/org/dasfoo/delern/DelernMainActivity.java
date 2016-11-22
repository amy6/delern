package org.dasfoo.delern;

import android.content.Intent;
import android.os.Bundle;

import org.dasfoo.delern.signin.SignInActivity;

public class DelernMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isUserSignedIn()) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        DelernMainActivityFragment listFragment = new DelernMainActivityFragment();
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, listFragment).commit();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.delern_main_activity;
    }

}

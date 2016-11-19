package org.dasfoo.delern;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.dasfoo.delern.card.ShowCardsFragment;
import org.dasfoo.delern.signin.SignInActivity;

public class DelernMainActivity extends BaseActivity
        implements ShowCardsFragment.OnFragmentInteractionListener {

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
        return R.layout.activity_delern_main;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

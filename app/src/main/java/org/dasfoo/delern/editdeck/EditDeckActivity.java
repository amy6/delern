package org.dasfoo.delern.editdeck;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.ParcelableDeck;

public class EditDeckActivity extends AppCompatActivity {

    /**
     * IntentExtra deck for this activity.
     */
    public static final String DECK = "deck";

    public static void startActivity(final Context context, final Deck deck) {
        Intent intent = new Intent(context, EditDeckActivity.class);
        intent.putExtra(EditDeckActivity.DECK, new ParcelableDeck(deck));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_deck_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        Deck deck = ParcelableDeck.get(intent.getParcelableExtra(DECK));
        this.setTitle(deck.getName());
    }
}

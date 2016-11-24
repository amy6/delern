package org.dasfoo.delern.card;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.Card;

public class PreEditCardActivity extends AppCompatActivity {

    public static String LABEL = "label";
    public static String DECK_ID = "deckId";
    public static String CARD = "card";
    private String mDeckId;
    private Card mCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_edit_card_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO(ksheremet): Implement back navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        String mLabel = intent.getStringExtra(LABEL);
        mDeckId = intent.getStringExtra(DECK_ID);
        mCard = intent.getParcelableExtra(CARD);

        this.setTitle(mLabel);

        TextView frontPreview = (TextView) findViewById(R.id.textFrontPreview);
        TextView backPreview = (TextView) findViewById(R.id.textBackPreview);
        frontPreview.setText(mCard.getFront());
        backPreview.setText(mCard.getBack());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_card_menu, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_card_menu:
                deleteCard(mDeckId, mCard);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void deleteCard(final String deckId, final Card card) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // TODO(ksheremet): Fix messaging
        builder.setMessage("Delete card!");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Card.deleteCardFromDeck(deckId, card);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}

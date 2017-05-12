package org.dasfoo.delern.card;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.dasfoo.delern.R;
import org.dasfoo.delern.listeners.AbstractOnFbOperationCompleteListener;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.listener.AbstractUserMessageValueEventListener;
import org.dasfoo.delern.util.LogUtil;

/**
 * Activity that shows the card before it is being edited.
 * TODO(ksheremet): use existing showCardActivity for that?
 */
public class PreEditCardActivity extends AppCompatActivity {

    /**
     * IntentExtra R.string title of the activity.
     */
    public static final String LABEL = "label";

    /**
     * IntentExtra deck ID that this card belongs to.
     */
    public static final String DECK_ID = "deckId";

    /**
     * IntentExtra card ID that is being edited.
     */
    public static final String CARD_ID = "cardId";

    private static final String TAG = LogUtil.tagFor(PreEditCardActivity.class);

    private String mDeckId;
    private Card mCard;
    private ValueEventListener mCardValueEventListener;
    private TextView mFrontPreview;
    private TextView mBackPreview;
    private Query mCardQuery;
    private String mLabel;
    private String mCardId;
    private final Context mContext = this;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_edit_card_activity);

        configureToolbar();
        getInputVariables();
        mCardQuery = Card.getCardById(mDeckId, mCardId);
        this.setTitle(mLabel);
        mFrontPreview = (TextView) findViewById(R.id.textFrontPreview);
        mBackPreview = (TextView) findViewById(R.id.textBackPreview);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                editCardActivityStart();
            }
        });
    }

    private void getInputVariables() {
        Intent intent = getIntent();
        mLabel = intent.getStringExtra(LABEL);
        mDeckId = intent.getStringExtra(DECK_ID);
        mCardId = intent.getStringExtra(CARD_ID);
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCardValueEventListener = new AbstractUserMessageValueEventListener(this) {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Card card;
                for (DataSnapshot cardSnapshot : dataSnapshot.getChildren()) {
                    card = cardSnapshot.getValue(Card.class);
                    card.setcId(cardSnapshot.getKey());
                    mFrontPreview.setText(card.getFront());
                    mBackPreview.setText(card.getBack());
                    mCard = card;
                }
            }
        };
        mCardQuery.addValueEventListener(mCardValueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCardQuery.removeEventListener(mCardValueEventListener);
    }

    private void editCardActivityStart() {
        Intent intentEdit = new Intent(this, AddEditCardActivity.class);
        intentEdit.putExtra(AddEditCardActivity.DECK_ID, mDeckId);
        intentEdit.putExtra(AddEditCardActivity.LABEL, R.string.edit);
        intentEdit.putExtra(AddEditCardActivity.CARD, mCard);
        startActivity(intentEdit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_card_menu, menu);

        // Menu icons from drawable folder isn't tinted on default.
        // http://stackoverflow.com/questions/24301235/tint-menu-icons
        MenuItem deleteMenuItem = menu.findItem(R.id.delete_card_menu);
        Drawable tintedIcon = deleteMenuItem.getIcon();
        // TODO(ksheremet): Check mode http://ssp.impulsetrain.com/porterduff.html
        tintedIcon.mutate().setColorFilter(ContextCompat.getColor(this, R.color.toolbarIconColor),
                PorterDuff.Mode.SRC_IN);
        deleteMenuItem.setIcon(tintedIcon);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
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
        builder.setMessage(R.string.delete_card_warning);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                Card.deleteCardFromDeck(deckId, card,
                        new AbstractOnFbOperationCompleteListener<Void>(TAG, mContext) {
                    @Override
                    public void onOperationSuccess(final Void param) {
                        Toast.makeText(mContext,
                                R.string.card_deleted_successful_user_message,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}

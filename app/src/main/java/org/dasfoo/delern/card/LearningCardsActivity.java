package org.dasfoo.delern.card;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.Query;

import org.dasfoo.delern.R;
import org.dasfoo.delern.controller.CardColor;
import org.dasfoo.delern.controller.GrammaticalGenderSpecifier;
import org.dasfoo.delern.handlers.OnLearningCardAvailable;
import org.dasfoo.delern.listeners.AbstractOnFbOperationCompleteListener;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckType;
import org.dasfoo.delern.models.ScheduledCard;
import org.dasfoo.delern.models.listener.LearningCardListener;
import org.dasfoo.delern.util.Animation;
import org.dasfoo.delern.util.LogUtil;

/**
 * Activity for showing cards to learn.
 */
public class LearningCardsActivity extends AppCompatActivity {

    /**
     * IntentExtra deck for this activity.
     */
    public static final String DECK = "deck";

    /**
     * Information about class for logging.
     */
    private static final String TAG = LogUtil.tagFor(LearningCardsActivity.class);

    /**
     * Key for saving onSaveInstanceState.
     */
    private static final String BACK_IS_SHOWN = "back";
    private CardView mCardView;
    private FloatingActionButton mKnowButton;
    private FloatingActionButton mRepeatButton;
    private ImageView mTurnCardButton;
    private TextView mFrontTextView;
    private TextView mBackTextView;
    private View mDelimiter;
    private boolean mBackIsShown;
    private Deck mDeck;
    private LearningCardListener mLearningCard;
    private final OnLearningCardAvailable mCardAvailable = new OnLearningCardAvailable() {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onNewCard() {
            showFrontSide();
            // if user decided to edit card, a back side can be shown or not.
            // After returning back it must show the same state (the same buttons
            // and text) as before editing
            if (mBackIsShown) {
                showBackSide();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onNoCards() {
            finish();
        }
    };
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.to_know_button:
                    setClickableRepeatKnowButtons(false);
                    mLearningCard.viewedCard(LearningCardListener.KNOW_CARD);
                    mBackIsShown = false;
                    break;
                case R.id.to_repeat_button:
                    setClickableRepeatKnowButtons(false);
                    mRepeatButton.setClickable(false);
                    mLearningCard.viewedCard(LearningCardListener.DO_NOT_KNOW_CARD);
                    mBackIsShown = false;
                    break;
                case R.id.turn_card_button:
                    showBackSide();
                    break;
                default:
                    Log.v(TAG, "Button is not implemented yet.");
                    break;
            }
        }
    };
    private Query mLearningCardQuery;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_cards_activity);
        if (savedInstanceState != null) {
            mBackIsShown = savedInstanceState.getBoolean(BACK_IS_SHOWN);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getParameters();
        initViews();

        mLearningCardQuery = ScheduledCard.fetchCardsToRepeatWithLimit(mDeck.getdId(), 1);
        mLearningCard = new LearningCardListener(this, mDeck.getdId(), mCardAvailable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BACK_IS_SHOWN, mBackIsShown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        mLearningCardQuery.addValueEventListener(mLearningCard);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Check if current card has already listener, remove it.
        if (mLearningCardQuery != null) {
            mLearningCard.clean();
            mLearningCardQuery.removeEventListener(mLearningCard);
        }
    }

    /**
     * Gets parameters sent from previous Activity.
     */
    private void getParameters() {
        Intent intent = getIntent();
        mDeck = intent.getParcelableExtra(DECK);
        this.setTitle(mDeck.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_card_menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_card_show_menu:
                Intent intentEdit = new Intent(this, AddEditCardActivity.class);
                intentEdit.putExtra(AddEditCardActivity.DECK_ID, mDeck.getdId());
                intentEdit.putExtra(AddEditCardActivity.LABEL, R.string.edit);
                intentEdit.putExtra(AddEditCardActivity.CARD, mLearningCard.getCurrentCard());
                startActivity(intentEdit);
                break;
            case R.id.delete_card_show_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.delete_card_warning);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        mBackIsShown = false;
                        // TODO(ksheremet): delete if not owner
                        Card.deleteCardFromDeck(mDeck.getdId(), mLearningCard.getCurrentCard(),
                                new AbstractOnFbOperationCompleteListener(TAG,
                                        LearningCardsActivity.this) {
                                    @Override
                                    public void onOperationSuccess() {
                                        Toast.makeText(LearningCardsActivity.this,
                                                R.string.deleted_card_successful_user_message,
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Initializes buttons and views.
     * Sets click listeners.
     */
    private void initViews() {
        mCardView = (CardView) findViewById(R.id.card_view);

        mKnowButton = (FloatingActionButton) findViewById(R.id.to_know_button);
        mKnowButton.setOnClickListener(mOnClickListener);

        mRepeatButton = (FloatingActionButton) findViewById(R.id.to_repeat_button);
        mRepeatButton.setOnClickListener(mOnClickListener);

        mFrontTextView = (TextView) findViewById(R.id.textFrontCardView);
        mBackTextView = (TextView) findViewById(R.id.textBackCardView);

        mTurnCardButton = (ImageView) findViewById(R.id.turn_card_button);
        mTurnCardButton.setOnClickListener(mOnClickListener);

        mDelimiter = findViewById(R.id.delimeter);
        mDelimiter.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows front side of the current card and appropriate buttons.
     */
    private void showFrontSide() {
        setBackgroundCardColor();
        mFrontTextView.setText(mLearningCard.getCurrentCard().getFront());
        mBackTextView.setText("");
        mRepeatButton.setVisibility(View.INVISIBLE);
        mKnowButton.setVisibility(View.INVISIBLE);
        mTurnCardButton.setVisibility(View.VISIBLE);
        mDelimiter.setVisibility(View.INVISIBLE);
    }

    /**
     * Specifies grammatical gender of content.
     * Sets background color for mCardView regarding gender.
     */
    private void setBackgroundCardColor() {
        GrammaticalGenderSpecifier.Gender gender;
        try {
            gender = GrammaticalGenderSpecifier.specifyGender(
                    DeckType.valueOf(mDeck.getDeckType()),
                    mLearningCard.getCurrentCard().getBack());

        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
            gender = GrammaticalGenderSpecifier.Gender.NO_GENDER;
        }
        mCardView.setCardBackgroundColor(ContextCompat.getColor(this, CardColor.getColor(gender)));
    }

    /**
     * Shows back side of current card and appropriate buttons.
     */
    private void showBackSide() {
        mBackTextView.setText(mLearningCard.getCurrentCard().getBack());
        Animator repeatButtonAnimation = null;
        Animator knowButtonAnimation = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            repeatButtonAnimation = Animation.appearanceAnimation(mRepeatButton);
            knowButtonAnimation = Animation.appearanceAnimation(mKnowButton);
        }
        setClickableRepeatKnowButtons(true);
        mRepeatButton.setVisibility(View.VISIBLE);
        mKnowButton.setVisibility(View.VISIBLE);
        if (repeatButtonAnimation != null) {
            repeatButtonAnimation.start();
            knowButtonAnimation.start();
        }
        mTurnCardButton.setVisibility(View.INVISIBLE);
        mDelimiter.setVisibility(View.VISIBLE);
        mBackIsShown = true;
    }

    /**
     * Do buttons not clickable after first click to prevent double click and missing
     * next card.
     *
     * @param isClickable whether buttons clickable or not
     */
    private void setClickableRepeatKnowButtons(final Boolean isClickable) {
        mKnowButton.setClickable(isClickable);
        mRepeatButton.setClickable(isClickable);
    }
}

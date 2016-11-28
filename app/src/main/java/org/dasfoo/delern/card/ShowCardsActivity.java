package org.dasfoo.delern.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.controller.RepetitionIntervals;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Level;
import org.dasfoo.delern.util.LogUtil;

import java.util.Iterator;
import java.util.List;

public class ShowCardsActivity extends AppCompatActivity {

    public static final String DECK_ID = "mDeckId";
    public static final String CARDS = "cards";
    public static final String LABEL = "label";
    private static final String TAG = LogUtil.tagFor(ShowCardsActivity.class);

    private FloatingActionButton mKnowButton;
    private FloatingActionButton mRepeatButton;
    private ImageView mTurnCardButton;
    private TextView mFrontTextView;
    private TextView mBackTextView;
    private View mDelimeter;

    private Iterator<Card> mCardIterator;
    private Card mCurrentCard;
    private String mDeckId;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.to_know_button:
                    String newCardLevel = setNewLevel(mCurrentCard.getLevel());
                    mCurrentCard.setLevel(newCardLevel);
                    mCurrentCard.setRepeatAt(System.currentTimeMillis() +
                            RepetitionIntervals.getInstance().intervals.get(newCardLevel));
                    updateCardInFirebase();
                    showNextCard();
                    break;
                case R.id.to_repeat_button:
                    mCurrentCard.setLevel(Level.L0.name());
                    mCurrentCard.setRepeatAt(System.currentTimeMillis());
                    updateCardInFirebase();
                    showNextCard();
                    break;
                case R.id.turn_card_button:
                    Log.v(TAG, "Turn");
                    showBackSide();
                    break;
                default:
                    Log.v("ShowCardsActivity", "Button is not implemented yet.");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_cards_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getParameters();
        initViews();
        showFrontSide();
    }

    /**
     * Gets parameters sended from previous Activity.
     */
    private void getParameters() {
        Intent intent = getIntent();
        mDeckId = intent.getStringExtra(DECK_ID);
        List<Card> cards = intent.getParcelableArrayListExtra(CARDS);
        String label = intent.getStringExtra(LABEL);
        this.setTitle(label);
        if (cards != null) {
            mCardIterator = cards.iterator();
            mCurrentCard = mCardIterator.next();
        } else {
            finish();
        }
    }

    /**
     * Initializes buttons and views.
     * Sets click listeners.
     */
    private void initViews() {
        mKnowButton = (FloatingActionButton) findViewById(R.id.to_know_button);
        mKnowButton.setOnClickListener(onClickListener);

        mRepeatButton = (FloatingActionButton) findViewById(R.id.to_repeat_button);
        mRepeatButton.setOnClickListener(onClickListener);

        mFrontTextView = (TextView) findViewById(R.id.textFrontCardView);
        mBackTextView = (TextView) findViewById(R.id.textBackCardView);

        mTurnCardButton = (ImageView) findViewById(R.id.turn_card_button);
        mTurnCardButton.setOnClickListener(onClickListener);

        mDelimeter = findViewById(R.id.delimeter);
        mDelimeter.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows front side of the current card and appropriate buttons
     */
    private void showFrontSide() {
        mFrontTextView.setText(mCurrentCard.getFront());
        mBackTextView.setText("");
        mRepeatButton.setVisibility(View.INVISIBLE);
        mKnowButton.setVisibility(View.INVISIBLE);
        mTurnCardButton.setVisibility(View.VISIBLE);
        mDelimeter.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows back side of current card and appropriate buttons.
     */
    private void showBackSide() {
        mBackTextView.setText(mCurrentCard.getBack());
        mRepeatButton.setVisibility(View.VISIBLE);
        mKnowButton.setVisibility(View.VISIBLE);
        mTurnCardButton.setVisibility(View.INVISIBLE);
        mDelimeter.setVisibility(View.VISIBLE);
    }

    private String setNewLevel(final String currLevel) {
        Level cLevel = Level.valueOf(currLevel);
        if (cLevel == Level.L7) {
            return Level.L7.name();
        }
        return cLevel.next().name();
    }

    private void updateCardInFirebase() {
        Card.updateCard(mCurrentCard, mDeckId);
    }

    private void showNextCard() {
        if (mCardIterator.hasNext()) {
            mCurrentCard = mCardIterator.next();
            showFrontSide();
        } else {
            finish();
        }
    }
}

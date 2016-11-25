package org.dasfoo.delern.card;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.dasfoo.delern.BaseActivity;
import org.dasfoo.delern.R;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Level;

public class AddEditCardActivity extends BaseActivity implements View.OnClickListener {

    public static final String LABEL = "label";
    public static final String DECK_ID = "mDeckId";
    public static final String CARD = "card";

    private String mDeckId;

    private TextInputEditText mFrontSideInputText;
    private TextInputEditText mBackSideInputText;
    private Card mCard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final String label = intent.getStringExtra(LABEL);
        mDeckId = intent.getStringExtra(DECK_ID);
        mCard = intent.getParcelableExtra(CARD);
        this.setTitle(label);

        enableToolbarArrow(true);

        mFrontSideInputText = (TextInputEditText) findViewById(R.id.front_side_text);
        mBackSideInputText = (TextInputEditText) findViewById(R.id.back_side_text);
        Button mAddCardToDbButton = (Button) findViewById(R.id.add_card_to_db);
        if (mCard != null) {
            mAddCardToDbButton.setText(R.string.save_button_string);
            mFrontSideInputText.setText(mCard.getFront());
            mBackSideInputText.setText(mCard.getBack());
        }
        mAddCardToDbButton.setOnClickListener(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.add_edit_card_activity;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_card_to_db) {
            if (mCard == null) {
                Card newCard = new Card();
                newCard.setFront(mFrontSideInputText.getText().toString());
                newCard.setBack(mBackSideInputText.getText().toString());
                newCard.setLevel(Level.L0.name());
                newCard.setRepeatAt(System.currentTimeMillis());
                Card.createNewCard(newCard, mDeckId);
                cleanTextFields();
                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
            } else {
                mCard.setFront(mFrontSideInputText.getText().toString());
                mCard.setBack(mBackSideInputText.getText().toString());
                Card.updateCard(mCard, mDeckId);
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void cleanTextFields() {
        mFrontSideInputText.setText("");
        mBackSideInputText.setText("");
    }
}

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

public class AddCardActivity extends BaseActivity implements View.OnClickListener {

    public static final String LABEL = "label";
    public static final String DECK_ID = "deckId";

    private String deckId;

    private TextInputEditText mFrontSideInputText;
    private TextInputEditText mBackSideInputText;
    private Button mAddCardToDbButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final String label = intent.getStringExtra(LABEL);
        deckId = intent.getStringExtra(DECK_ID);
        this.setTitle(label);

        enableToolbarArrow(true);

        mFrontSideInputText = (TextInputEditText) findViewById(R.id.front_side_text);
        mBackSideInputText = (TextInputEditText) findViewById(R.id.back_side_text);
        mAddCardToDbButton = (Button) findViewById(R.id.add_card_to_db);
        mAddCardToDbButton.setOnClickListener(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_card;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_card_to_db) {
            Card newCard = new Card();
            newCard.setFront(mFrontSideInputText.getText().toString());
            newCard.setBack(mBackSideInputText.getText().toString());
            newCard.setLevel(Level.L0.name());
            newCard.setRepeatAt(System.currentTimeMillis());
            Card.createNewCard(newCard, deckId);
            cleanTextFields();
            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
        }
    }

    private void cleanTextFields() {
        mFrontSideInputText.setText("");
        mBackSideInputText.setText("");
    }
}

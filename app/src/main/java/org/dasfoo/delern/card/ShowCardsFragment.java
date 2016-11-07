package org.dasfoo.delern.card;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.dasfoo.delern.R;
import org.dasfoo.delern.controller.FirebaseController;
import org.dasfoo.delern.controller.RepetitionIntervals;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowCardsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowCardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowCardsFragment extends Fragment {
    private static final String CARDS = "cards";
    private static final String DECK_ID = "deckID";

    private FirebaseController firebaseController = FirebaseController.getInstance();
    private static final String TAG = ShowCardsFragment.class.getSimpleName();

    private Button mKnowButton;
    private Button mMemorizeButton;
    private Button mRepeatButton;
    private Button mNextButton;
    private TextView mTextView;

    private Iterator<Card> mCardIterator;
    private Card mCurrentCard;
    private String deckId;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.to_know_button:
                    showBackSide();
                    mMemorizeButton.setVisibility(View.VISIBLE);
                    String newCardLevel = setNewLevel(mCurrentCard.getLevel());
                    mCurrentCard.setLevel(newCardLevel);
                    mCurrentCard.setRepeatAt(System.currentTimeMillis() + RepetitionIntervals.getInstance().intervals.get(newCardLevel));
                    break;
                case R.id.to_memorize_button:
                    mCurrentCard.setLevel(Level.L0.name());
                    mCurrentCard.setRepeatAt(System.currentTimeMillis());
                    break;
                case R.id.to_repeat_button:
                    showBackSide();
                    mMemorizeButton.setVisibility(View.INVISIBLE);
                    mCurrentCard.setLevel(Level.L0.name());
                    mCurrentCard.setRepeatAt(System.currentTimeMillis());
                    break;
                case R.id.next_button:
                    firebaseController.updateCard(mCurrentCard, deckId);
                    if (mCardIterator.hasNext()) {
                        mCurrentCard = mCardIterator.next();
                        showFrontSide();
                    } else {
                        getFragmentManager().popBackStack();
                    }
                    break;
                default:
                    Log.v("ShowCardsFragment", "Button is not implemented yet.");
                    break;
            }
        }
    };


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cards to show.
     * @return A new instance of fragment ShowCardsFragment.
     */
    public static ShowCardsFragment newInstance(final ArrayList<Card> cards, final String deckId) {
        ShowCardsFragment fragment = new ShowCardsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(CARDS, cards);
        args.putString(DECK_ID, deckId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deckId = getArguments().getString(DECK_ID);
            List<Card> cards = getArguments().getParcelableArrayList(CARDS);
            mCardIterator = cards.iterator();
            mCurrentCard = mCardIterator.next();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        mKnowButton = (Button) view.findViewById(R.id.to_know_button);
        mKnowButton.setOnClickListener(onClickListener);
        mMemorizeButton = (Button) view.findViewById(R.id.to_memorize_button);
        mMemorizeButton.setOnClickListener(onClickListener);
        mRepeatButton = (Button) view.findViewById(R.id.to_repeat_button);
        mRepeatButton.setOnClickListener(onClickListener);
        mNextButton = (Button) view.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(onClickListener);
        mTextView = (TextView) view.findViewById(R.id.textCardView);
        showFrontSide();
        return view;
    }

    @Override
    public final void onAttach(final Context context) {
        super.onAttach(context);
        // TODO(ksheremet): remove unused code
        if (!(context instanceof OnFragmentInteractionListener)) {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public final void onDetach() {
        super.onDetach();
    }

    /**
     * Shows front side of the current card and appropriate buttons
     */
    private void showFrontSide() {
        mTextView.setText(mCurrentCard.getFront());
        mMemorizeButton.setVisibility(View.INVISIBLE);
        mRepeatButton.setVisibility(View.VISIBLE);
        mKnowButton.setVisibility(View.VISIBLE);
        mNextButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows back side of current card and appropriate buttons.
     */
    private void showBackSide() {
        mTextView.setText(mCurrentCard.getBack());
        mNextButton.setVisibility(View.VISIBLE);
        mRepeatButton.setVisibility(View.INVISIBLE);
        mKnowButton.setVisibility(View.INVISIBLE);
    }

    private String setNewLevel(String currLevel) {
        Level cLevel = Level.valueOf(currLevel);
        if (cLevel == Level.L7) {
            return Level.L7.name();
        }
        return cLevel.next().name();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

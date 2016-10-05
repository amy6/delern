package ch.sheremet.dasfoo.delern.card;

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

import java.util.Iterator;

import ch.sheremet.dasfoo.delern.R;
import ch.sheremet.dasfoo.delern.model.Card;
import ch.sheremet.dasfoo.delern.model.DBListTest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id";

    private Button mKnowButton;
    private Button mMemorizeButton;
    private Button mRepeatButton;
    private Button mNextButton;
    private TextView mTextView;
    private Iterator<Card> mCardIterator;
    private Card mCurrentCard;

    // TODO: Rename and change types of parameters
    private int mParam1;

    private OnFragmentInteractionListener mListener;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.to_know_button:
                    showBackSide(mCurrentCard);
                    mMemorizeButton.setVisibility(View.VISIBLE);
                    break;
                case R.id.to_memorize_button:
                    // TODO: Add time parameters
                    break;
                case R.id.to_repeat_button:
                    showBackSide(mCurrentCard);
                    mMemorizeButton.setVisibility(View.INVISIBLE);
                    break;
                case R.id.next_button:
                    if (mCardIterator.hasNext()){
                        mCurrentCard = mCardIterator.next();
                        showFrontSide(mCurrentCard);
                    } else {
                        getFragmentManager().popBackStack();
                    }
                    break;
                default:
                    Log.v("CardFragment", "Button is not implemented yet.");
                    break;
            }
        }
    };

    public CardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment CardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CardFragment newInstance(int param1) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            Log.v("Input parameter", String.valueOf(mParam1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        // TODO: Move this code somewhere
        mCardIterator = DBListTest.newInstance().getCardsById(mParam1).iterator();
        if (mCardIterator.hasNext()){
            mCurrentCard = mCardIterator.next();
           showFrontSide(mCurrentCard);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    private void showFrontSide(Card card) {
        mTextView.setText(mCurrentCard.getFrontSide());
        mMemorizeButton.setVisibility(View.INVISIBLE);
        mRepeatButton.setVisibility(View.VISIBLE);
        mKnowButton.setVisibility(View.VISIBLE);
        mNextButton.setVisibility(View.INVISIBLE);
    }

    private void showBackSide(Card card) {
        mTextView.setText(mCurrentCard.getBackSide());
        mNextButton.setVisibility(View.VISIBLE);
        mRepeatButton.setVisibility(View.INVISIBLE);
        mKnowButton.setVisibility(View.INVISIBLE);
    }
}

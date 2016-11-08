package org.dasfoo.delern.card;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.dasfoo.delern.R;
import org.dasfoo.delern.controller.FirebaseController;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Level;
import org.dasfoo.delern.util.LogUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddNewCardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddNewCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNewCardFragment extends Fragment implements View.OnClickListener {

    private static final String DECK_ID = "deckId";

    /**
     * Class information for logging.
     */
    private final String TAG = LogUtil.tagFor(AddNewCardFragment.class);

    private String deckId;

    private TextInputEditText mFrontSideInputText;
    private TextInputEditText mBackSideInputText;
    private Button mAddCardToDbButton;

    private OnFragmentInteractionListener mListener;
    private FirebaseController firebaseController = FirebaseController.getInstance();

    public AddNewCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param deckId is is of deck where to store card.
     * @return A new instance of fragment AddNewCardFragment.
     */
    public static AddNewCardFragment newInstance(String deckId) {
        AddNewCardFragment fragment = new AddNewCardFragment();
        Bundle args = new Bundle();
        args.putString(DECK_ID, deckId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deckId = getArguments().getString(DECK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_card, container, false);
        mFrontSideInputText = (TextInputEditText) view.findViewById(R.id.front_side_text);
        mBackSideInputText = (TextInputEditText) view.findViewById(R.id.back_side_text);
        mAddCardToDbButton = (Button) view.findViewById(R.id.add_card_to_db);
        mAddCardToDbButton.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_card_to_db) {
            Card newCard = new Card();
            newCard.setFront(mFrontSideInputText.getText().toString());
            newCard.setBack(mBackSideInputText.getText().toString());
            newCard.setLevel(Level.L0.name());
            newCard.setRepeatAt(System.currentTimeMillis());
            firebaseController.createNewCard(newCard, deckId);
            cleanTextFields();
            Toast.makeText(this.getContext(), "Added", Toast.LENGTH_SHORT).show();
        }
    }

    private void cleanTextFields(){
        mFrontSideInputText.setText("");
        mBackSideInputText.setText("");
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

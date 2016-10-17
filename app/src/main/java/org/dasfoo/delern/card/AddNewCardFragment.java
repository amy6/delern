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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.dasfoo.delern.R;
import org.dasfoo.delern.models.Card;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddNewCardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddNewCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNewCardFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FB_PATH = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = this.getClass().getSimpleName();

    // TODO: Rename and change types of parameters
    private String fbPath;

    private TextInputEditText mFrontSideInputText;
    private TextInputEditText mBackSideInputText;
    private Button mAddCardToDbButton;

    private OnFragmentInteractionListener mListener;

    public AddNewCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNewCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNewCardFragment newInstance(String param1, String param2) {
        AddNewCardFragment fragment = new AddNewCardFragment();
        Bundle args = new Bundle();
        args.putString(FB_PATH, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fbPath = getArguments().getString(FB_PATH);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
            newCard.setFrontSide(mFrontSideInputText.getText().toString());
            newCard.setBackSide(mBackSideInputText.getText().toString());
            writeCardToFirebase(newCard, fbPath);
            cleanTextFields();
            Toast.makeText(this.getContext(), "Added", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeCardToFirebase(Card newCard, String mParam1) {
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase
                .getInstance()
                .getReferenceFromUrl(mParam1)
                .child("cards");
        mFirebaseDatabaseReference.push().setValue(newCard);
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

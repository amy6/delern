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

import ch.sheremet.dasfoo.delern.R;

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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button mKnowButton;
    private Button mMemorizeButton;
    private Button mRepeatButton;
    private Button mNextButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.to_know_button:
                    TextView tViev = (TextView) getActivity().findViewById(R.id.textCardView);
                    tViev.setText("Some text 2");
                    mMemorizeButton.setVisibility(View.VISIBLE);
                    mRepeatButton.setVisibility(View.INVISIBLE);
                    mKnowButton.setVisibility(View.INVISIBLE);
                    mNextButton.setVisibility(View.VISIBLE);
                    break;
                case R.id.to_memorize_button:
                    break;
                case R.id.to_repeat_button:
                    mMemorizeButton.setVisibility(View.INVISIBLE);
                    mRepeatButton.setVisibility(View.INVISIBLE);
                    mKnowButton.setVisibility(View.INVISIBLE);
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
     * @param param2 Parameter 2.
     * @return A new instance of fragment CardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CardFragment newInstance(String param1, String param2) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        mMemorizeButton.setVisibility(View.INVISIBLE);
        mNextButton = (Button) view.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(onClickListener);
        mNextButton.setVisibility(View.INVISIBLE);
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
}

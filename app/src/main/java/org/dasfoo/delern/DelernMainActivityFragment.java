package org.dasfoo.delern;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.dasfoo.delern.callbacks.OnDesktopViewHolderClick;
import org.dasfoo.delern.card.AddNewCardFragment;
import org.dasfoo.delern.card.CardFragment;
import org.dasfoo.delern.models.Desktop;
import org.dasfoo.delern.viewholders.DesktopViewHolder;

/**
 * A placeholder fragment containing a simple view.
 */
public class DelernMainActivityFragment extends Fragment implements OnDesktopViewHolderClick{

    private final String TAG = this.getTag();
    private OnDesktopViewHolderClick onDesktopViewHolderClick = this;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // Firebase realtime database instance variables
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Desktop, DesktopViewHolder>
            mFirebaseAdapter;

    public static final String DESKTOP_PATH = "desktops";

    public DelernMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_delern_main, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // TODO(ksheremet) : move logic in separate class
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Topic");

                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Desktop newDesktop = new
                                Desktop(input.getText().toString());
                        // TODO(ksheremet): move referencies to one place
                        mFirebaseDatabaseReference.child("users").child(mFirebaseUser.getUid()).child(DESKTOP_PATH)
                                .push().setValue(newDesktop);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        //TODO: move init to onActivityCreated
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_recycler_view);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .build());

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Desktop, DesktopViewHolder>(
                Desktop.class,
                R.layout.card_text_view,
                DesktopViewHolder.class,
                mFirebaseDatabaseReference.child("users").child(mFirebaseUser.getUid()).child(DESKTOP_PATH)) {

            @Override
            protected void populateViewHolder(DesktopViewHolder viewHolder, Desktop desktop, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.getmDesktopTextView().setText(desktop.getName());
                viewHolder.setOnViewClick(onDesktopViewHolderClick);
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });

        mRecyclerView.setAdapter(mFirebaseAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void doOnAddButtonClick(int position) {
        AddNewCardFragment newCardFragment = AddNewCardFragment.newInstance(mFirebaseAdapter.getRef(position).toString(), "World");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newCardFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }

    @Override
    public void doOnTextViewClick(int position) {
        CardFragment newFragment = CardFragment.newInstance(mFirebaseAdapter.getRef(position).toString());
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }
}

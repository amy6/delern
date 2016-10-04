package ch.sheremet.dasfoo.delern;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import ch.sheremet.dasfoo.delern.adapters.ListAdapter;
import ch.sheremet.dasfoo.delern.card.CardFragment;
import ch.sheremet.dasfoo.delern.listeners.RecyclerItemClickListener;

/**
 * A placeholder fragment containing a simple view.
 */
public class DelernMainActivityFragment extends Fragment
        implements CardFragment.OnFragmentInteractionListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public DelernMainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_delern_main, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //TODO: move init to onActivityCreated
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_recycler_view);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .build());

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                        // Create fragment and give it an argument specifying the article it should show
                        CardFragment newFragment = new CardFragment();
                        //      Bundle args = new Bundle();
                        //     args.putInt(ArticleFragment.ARG_POSITION, position);
                        //     newFragment.setArguments(args);

                        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                .beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.fragment_container, newFragment);
                        transaction.addToBackStack(null);

// Commit the transaction
                        transaction.commit();
                    }
                })
        );

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // TODO: move to db
        String[] list = {"Deutsch", "English", "Algorithms", "My cards"};
        mAdapter = new ListAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

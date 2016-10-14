package org.dasfoo.delern.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import org.dasfoo.delern.R;

/**
  Created by Katarina Sheremet on 9/22/16 1:11 AM.
  Provide a reference to the views for each data item
  Complex data items may need more than one view per item, and
  you provide access to all the views for a data item in a view holder
 */

public class DesktopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // TODO: try privat
    private TextView desktopTextView;

    public DesktopViewHolder(View v) {
        super(v);
        desktopTextView = (TextView) itemView.findViewById(R.id.desktop_text_view);

    }

    public TextView getDesktopTextView() {
        return desktopTextView;
    }

    public void setDesktopTextView(TextView desktopTextView) {
        this.desktopTextView = desktopTextView;
    }

    @Override
    public void onClick(View v) {

    }
}

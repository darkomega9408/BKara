package com.beast.bkara.util;

import android.content.Context;
import android.support.v7.widget.SearchView;

/**
 * Created by Darka on 4/23/2016.
 */
public class SongSearchView extends SearchView {

    OnSearchViewCollapsedEventListener mSearchViewCollapsedEventListener;

    public SongSearchView(Context context) {
        super(context);
    }

    @Override
    public void onActionViewCollapsed() {
        if (mSearchViewCollapsedEventListener != null)
            mSearchViewCollapsedEventListener.onSearchViewCollapsed();
        super.onActionViewCollapsed();
    }

    public interface OnSearchViewCollapsedEventListener{
        void onSearchViewCollapsed();
    }

    public void setOnSearchViewCollapsedEventListener(OnSearchViewCollapsedEventListener eventListener) {
        mSearchViewCollapsedEventListener = eventListener;
    }

}

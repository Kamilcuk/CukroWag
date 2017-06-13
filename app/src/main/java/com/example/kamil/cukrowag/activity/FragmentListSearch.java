package com.example.kamil.cukrowag.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.kamil.cukrowag.R;
import com.example.kamil.cukrowag.food.HasIdName;

import java.util.ArrayList;

/**
 * Created by kamil on 12.06.17.
 */

public abstract class FragmentListSearch<T extends HasIdName> extends Fragment implements SearchView.OnQueryTextListener {
    protected Context mContext;
    protected ListView mList;
    protected Button mButtonAdd;
    protected SearchView mSearchView;
    protected ArrayAdapter<T> mListAdapter;

    protected String mQuerystring;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_search, container, false);

        mContext = view.getContext();
        mButtonAdd = (Button) view.findViewById(R.id.fragment_list_search_button);
        mSearchView = (SearchView) view.findViewById(R.id.fragment_list_search_searchview);
        mList = (ListView) view.findViewById(R.id.fragment_list_search_list);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(this);
            /*mSearchView.setEnabled(true);
            mSearchView.setFocusable(true);
            mSearchView.setIconified(false);
            mSearchView.setIconifiedByDefault(false);*/

        return view;
    }

    public void filterRefreshListAdapter(ArrayList<T> original)
    {
        ArrayList<T> temp;
        if ( mQuerystring != null && mQuerystring.isEmpty() == false ) {
            temp = new ArrayList<T>();
            for (T o : original) {
                if ( o.name.toLowerCase().contains( mQuerystring.toLowerCase() ) ) {
                    temp.add(o);
                }
            }
        } else {
            temp = original;
        }
        mListAdapter.clear();
        mListAdapter.addAll(temp);
        mListAdapter.notifyDataSetChanged();
    }

    public void setListAdapter(ArrayAdapter<T> mListAdapter) {
        this.mListAdapter = mListAdapter;
        this.mList.setAdapter(mListAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onResume();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        this.mQuerystring = newText;
        onResume();
        return false;
    }
}

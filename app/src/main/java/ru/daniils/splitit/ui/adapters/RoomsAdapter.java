package ru.daniils.splitit.ui.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;

import ru.daniils.splitit.data.components.Model;
import ru.daniils.splitit.data.components.Room;

public class RoomsAdapter implements ListAdapter {

    private Context mContext;
    private ArrayList<Model> mItems = new ArrayList<>();
    private int mRowResId;

    public RoomsAdapter(Context context, int rowResId) {
        mContext = context;
        mRowResId = rowResId;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Room getItem(int position) {
        return (Room) mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(ArrayList<Model> items) {
        mItems.clear();
        mItems.addAll(items);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null)
            view = LayoutInflater.from(parent.getContext()).inflate(mRowResId, parent, false);
        Room room = getItem(position);
        room.fillViewForList(view);
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

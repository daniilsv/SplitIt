package ru.daniils.splitit.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.daniils.splitit.R;
import ru.daniils.splitit.data.components.Model;
import ru.daniils.splitit.data.components.Room;
import ru.daniils.splitit.ui.activities.AddRoomActivity;
import ru.daniils.splitit.ui.activities.RoomActivity;
import ru.daniils.splitit.ui.adapters.RoomsAdapter;
import ru.daniils.splitit.utils.Callback;


public class PublicRoomsFragment extends Fragment {
    Context context;
    @BindView(R.id.list_rooms)
    ListView listRoomsView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private RoomsAdapter mAdapter;

    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Room item = mAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), RoomActivity.class);
            intent.putExtra("room_id", item.localId);
            startActivity(intent);
        }
    };

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Callback.ISuccessError callback = new Callback.ISuccessError() {
                @Override
                public void onSuccess() {
                    fillItems(true);
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onError() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            };
            new Room(context).queryItemsFromSite(null, callback);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View rootView = inflater.inflate(R.layout.fragment_rooms, container, false);
        ButterKnife.bind(this, rootView);
        mAdapter = new RoomsAdapter(context, R.layout.row_item);
        listRoomsView.setAdapter(mAdapter);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        listRoomsView.setOnItemClickListener(onClickListener);

        return rootView;
    }

    private void fillItems(final boolean isFromQuery) {
        Model model = new Room(context);
        model.filter("is_public='ON'");
        ArrayList<Model> items = model.getItems();
        if (items.size() != 0) {
            mAdapter.setItems(items);
            listRoomsView.setAdapter(mAdapter);
        }
        if (!isFromQuery && items.size() == 0) onRefreshListener.onRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        fillItems(false);
    }

    @OnClick(R.id.b_add_room)
    void addRoom() {
        startActivity(new Intent(getActivity(), AddRoomActivity.class));
    }
}
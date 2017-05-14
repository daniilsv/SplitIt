package ru.daniils.splitit.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.daniils.splitit.R;
import ru.daniils.splitit.data.Preferences;


public class MyAccountFragment extends Fragment {
    Context context;
    @BindView(R.id.l_card_person)
    RelativeLayout personCardLayout;
    @BindView(R.id.l_card_info)
    RelativeLayout infoCardLayout;
    @BindView(R.id.card_image)
    ImageView ImageCard;
    @BindView(R.id.card_login)
    TextView loginCard;
    @BindView(R.id.card_funded)
    TextView fundedCard;
    @BindView(R.id.card_cash)
    TextView cashCard;
    @BindView(R.id.card_rooms)
    TextView roomsCard;
    @BindView(R.id.b_card_more_info)
    Button moreInfoButton;

    String login;
    String funded;
    String cash;
    String rooms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View rootView = inflater.inflate(R.layout.fragment_my_rooms, container, false);
        ButterKnife.bind(this, rootView);

        login = Preferences.getString("login"); //pull user data to UI
        cash = String.valueOf(Preferences.getInt("money"));
        loginCard.setText(login);
        cashCard.setText(cash);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        login = Preferences.getString("login"); //pull user data to UI
        cash = String.valueOf(Preferences.getInt("money"));
        loginCard.setText(login);
        cashCard.setText(cash);
    }

}
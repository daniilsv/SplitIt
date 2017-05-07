package ru.daniils.splitit.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.daniils.splitit.R;
import ru.daniils.splitit.data.Preferences;
import ru.daniils.splitit.data.components.Room;
import ru.daniils.splitit.data.network.PostQuery;
import ru.daniils.splitit.utils.Callback;

public class AddRoomActivity extends AppCompatActivity {

    @BindView(R.id.title_field)
    TextView title;
    @BindView(R.id.description_field)
    TextView description;
    @BindView(R.id.price_field)
    TextView price;
    @BindView(R.id.term_field)
    DatePicker term;
    @BindView(R.id.is_public_field)
    CheckBox is_public;

    public static String getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        return year + "-" + month + "-" + day;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.b_add)
    void addRoom() {
        PostQuery query = new PostQuery(this, "add", "room") {
            @Override
            public void onSuccess() {
                new Room(getApplicationContext()).queryItemsFromSite(null, new Callback.ISuccessError() {
                    @Override
                    public void onSuccess() {
                        finish();
                    }

                    @Override
                    public void onError() {
                        finish();
                    }
                });
            }
        };
        query.put("token", Preferences.getString("token"));
        query.put("title", title.getText().toString());
        query.put("description", description.getText().toString());
        query.put("price", price.getText().toString());
        query.put("term", getDateFromDatePicker(term));
        query.put("is_public", is_public.isChecked() ? "1" : "0");
        query.execute();
    }
}
package ru.daniils.splitit.ui.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.daniils.splitit.R;
import ru.daniils.splitit.data.DataBase;
import ru.daniils.splitit.data.Preferences;
import ru.daniils.splitit.data.components.Room;
import ru.daniils.splitit.data.network.PostQuery;
import ru.daniils.splitit.ui.views.CircleProgressBar;

public class RoomActivity extends AppCompatActivity {
    int room_id = -1;
    Room room = null;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.progress)
    CircleProgressBar progress;
    @BindView(R.id.progress_text)
    TextView progress_text;
    EditText donateView;

    @BindView(R.id.progress_percent)
    TextView progressPercent;

    @BindView(R.id.people)
    LinearLayout peopleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        room_id = getIntent().getIntExtra("room_id", -1);
        if (room_id == -1)
            finish();

        room = (Room) new Room(this).getItemById(room_id);
        if (room == null)
            finish();

        ButterKnife.bind(this);
        title.setText(room.title);
        description.setText(room.description);

        progress.setProgress((int) (100 * room.deposit / room.price));
        progressPercent.setText((int) (100 * room.deposit / room.price) + "%");
        progress_text.setText((int) room.deposit + "/" + (int) room.price);

        donateView = new EditText(this);
        donateView.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (!room.isPublic.equals("ON"))
            for (int i = 0; i < new Random().nextInt(50) + 7; i++) {
                View view = LayoutInflater.from(this).inflate(R.layout.row_people, null, false);
                ((TextView) view.findViewById(R.id.name)).setText(i + " qq");
                peopleLayout.addView(view);
            }
        else
            peopleLayout.setVisibility(View.GONE);

    }

    @OnClick(R.id.b_donate)
    void onDonate() {
        showDialog(0);
    }

    //При нажатии кнопки назад
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void donateNow() {
        PostQuery query = new PostQuery(this, "deposit", room.keycode) {
            @Override
            public void onSuccess() {
                try {
                    double deposit = mResponse.getDouble("deposit");
                    int money = Preferences.getInt("money");
                    money -= deposit;
                    Preferences.setInt("money", money);
                    progress.setProgress((int) (100 * deposit / room.price));
                    progress_text.setText((int) deposit + "/" + (int) room.price);
                    progressPercent.setText((int) (100 * deposit / room.price) + "%");
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("deposit", deposit);
                    DataBase db = new DataBase(getApplicationContext());
                    db.update("com_splitit_rooms", room.localId, map);
                    db.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        query.put("token", Preferences.getString("token"));
        query.put("deposit", donateView.getText().toString());
        query.execute();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("How much do you want to donate?")
                .setCancelable(false).
                setView(donateView).
                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        donateNow();
                        dialog.dismiss();
                    }
                }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return builder.create();
    }
}
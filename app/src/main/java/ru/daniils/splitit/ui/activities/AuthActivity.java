package ru.daniils.splitit.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.daniils.splitit.R;
import ru.daniils.splitit.data.Preferences;
import ru.daniils.splitit.data.network.PostQuery;
import ru.daniils.splitit.utils.Utils;

public class AuthActivity extends AppCompatActivity {
    @BindView(R.id.email_field)
    EditText email;
    @BindView(R.id.password_field)
    EditText password;
    AuthActivity th;

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.stroke)
    ImageView imageView2;
    @BindView(R.id.secondStroke)
    ImageView secImg;
    @BindView(R.id.secondStrokeSecond)
    ImageView secBlackImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        th = this;
        setDrawable();
    }

    private void setDrawable() {
        imageView.setImageResource(R.drawable.gradient);
        imageView2.setImageResource(R.drawable.stroke);
        secImg.setImageResource(R.drawable.stroke);
        secBlackImg.setImageResource(R.drawable.stroke);
    }

    @OnClick(R.id.b_sign_in)
    void signIn() {
        PostQuery query = new PostQuery(this, "auth") {
            @Override
            public void onSuccess() {
                try {
                    String token = mResponse.getString("token");
                    Preferences.setBool("logged", true);
                    Preferences.setString("token", token);
                    Preferences.setInt("money", 15000);
                    startActivity(new Intent(th, MainActivity.class));
                    finish();
                } catch (JSONException ignored) {
                }
            }
        };
        query.put("email", email.getText().toString());
        query.put("password", Utils.MD5(password.getText().toString()));
        query.execute();
    }

    @OnClick(R.id.b_sign_up)
    void signUp() {

        Toast.makeText(this, "Up!" + email.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    //При нажатии кнопки назад
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
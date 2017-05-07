package ru.daniils.splitit.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.daniils.splitit.R;
import ru.daniils.splitit.data.Preferences;
import ru.daniils.splitit.data.network.PostQuery;
import ru.daniils.splitit.utils.Utils;

public class AuthActivity extends AppCompatActivity {

    @BindView(R.id.auth_layout)
    RelativeLayout authLayout;
    @BindView(R.id.reg_layout)
    RelativeLayout regLayout;

    @BindView(R.id.email_field)
    EditText email;
    @BindView(R.id.password_field)
    EditText password;


    @BindView(R.id.login_reg_field)
    EditText loginReg;
    @BindView(R.id.email_reg_field)
    EditText emailReg;
    @BindView(R.id.password_reg_field)
    EditText passwordReg;
    @BindView(R.id.password_repeat_reg_field)
    EditText passwordRepeatReg;

    AuthActivity th;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        th = this;
    }

    @OnClick(R.id.b_sign_in)
    void signIn() {
        PostQuery query = new PostQuery(this, "auth") {
            @Override
            public void onSuccess() {
                try {
                    loginAndFinish(mResponse.getString("token"));
                } catch (JSONException ignored) {
                }
            }
        };
        query.put("email", email.getText().toString());
        query.put("password", Utils.MD5(password.getText().toString()));
        query.execute();
    }

    @OnClick(R.id.b_sign_up)
    void toSignUp() {
        authLayout.setVisibility(View.GONE);
        regLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.reg_button)
    void signUp() {
        boolean is_ok = true;

        if (loginReg.getText().toString().length() > 32) {
            is_ok = false;
            Utils.toast(this, "Very long login");
        }
        if (loginReg.getText().toString().length() < 5) {
            is_ok = false;
            Utils.toast(this, "Very short login");
        }

        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(emailReg.getText().toString());
        if (!m.matches()) {
            is_ok = false;
            Utils.toast(this, "Wrong email");
        }

        if (!passwordReg.getText().toString().equals(passwordRepeatReg.getText().toString())) {
            is_ok = false;
            Utils.toast(this, "Different password");
        }
        if (is_ok) {
            PostQuery query = new PostQuery(this, "registration") {
                @Override
                public void onSuccess() {
                    try {
                        String login = loginReg.getText().toString();
                        Preferences.setString("login", login);
                        loginAndFinish(mResponse.getString("token"));
                    } catch (JSONException ignored) {
                    }
                }
            };
            query.put("name", loginReg.getText().toString());
            query.put("email", emailReg.getText().toString());
            query.put("password", Utils.MD5(passwordReg.getText().toString()));
            query.execute();
        }

    }

    protected void loginAndFinish(String token) {
        Preferences.setBool("logged", true);
        Preferences.setString("token", token);
        Preferences.setInt("money", 15000);
        startActivity(new Intent(th, MainActivity.class));
        finish();
    }

    //При нажатии кнопки назад
    @Override
    public void onBackPressed() {
        if (regLayout.getVisibility() == View.VISIBLE) {
            regLayout.setVisibility(View.GONE);
            authLayout.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
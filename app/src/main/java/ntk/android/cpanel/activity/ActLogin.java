package ntk.android.cpanel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.cpanel.R;
import ntk.android.cpanel.config.ConfigRestHeader;
import ntk.android.cpanel.config.ConfigStaticValue;
import ntk.android.cpanel.utillity.EasyPreference;
import ntk.base.api.core.interfase.ICoreUser;
import ntk.base.api.core.model.CoreUserResponse;
import ntk.base.api.core.model.CoreUserloginRequest;
import ntk.base.api.utill.RetrofitManager;

public class ActLogin extends AppCompatActivity {

    @BindView(R.id.lblLoginActLogin)
    TextView lblLogin;
    @BindView(R.id.txtUsernameActLogin)
    EditText Username;
    @BindView(R.id.txtPasswordActLogin)
    EditText Password;
    @BindView(R.id.spinnerLagActLogin)
    Spinner Lag;
    @BindView(R.id.checkboxRememberMeActLogin)
    CheckBox rememberMe;
    @BindView(R.id.btnLoginActLogin)
    Button btnLogin;
    private List<String> lagList = new ArrayList<String>();
    private int lagValue = 0;
    private ConfigStaticValue configStaticValue = new ConfigStaticValue(this);
    private ConfigRestHeader configRestHeader = new ConfigRestHeader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        ButterKnife.bind(this);
        initialization();
    }

    private void initialization() {
        rememberMe.setChecked(true);
        if (EasyPreference.with(ActLogin.this).getBoolean("RememberMe", true)) {
            Username.setText(EasyPreference.with(this).getString("Username", ""));
            Password.setText(EasyPreference.with(this).getString("Password", ""));
        }
        lagList.add(0, "فارسی");
        lagList.add(1, "English");
        lagList.add(2, "German");
        lagList.add(3, "عربی");
        Lag.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lagList));
        Lag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lagValue = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.btnLoginActLogin)
    public void onLoginClick() {
        btnLogin.setVisibility(View.INVISIBLE);
        EasyPreference.with(ActLogin.this).addBoolean("RememberMe", rememberMe.isChecked());
        getData();
    }

    private void getData() {
        CoreUserloginRequest request = new CoreUserloginRequest();
        if (!Username.getText().toString().matches("")) {
            request.username = Username.getText().toString();
        }
        if (!Password.getText().toString().matches("")) {
            request.pwd = Password.getText().toString();
        }
        switch (lagValue) {
            case 0:
                request.lang = "fa_IR";
                break;
            case 1:
                request.lang = "en_US";
                break;
            case 2:
                request.lang = "el_GR";
                break;
            case 3:
                request.lang = "ar_AE";
                break;
        }
        RetrofitManager manager = new RetrofitManager(ActLogin.this);
        ICoreUser iCore = manager.getRetrofit(configStaticValue.ApiBaseUrl).create(ICoreUser.class);
        Map<String, String> headers = new HashMap<>();
        headers = configRestHeader.GetHeaders(this);
        Observable<CoreUserResponse> call = iCore.UserLogin(headers, request);
        call.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<CoreUserResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CoreUserResponse response) {
                        if (response.IsSuccess) {
                            EasyPreference.with(ActLogin.this).addString("Username", request.username);
                            EasyPreference.with(ActLogin.this).addString("Password", request.pwd);
                            EasyPreference.with(ActLogin.this).addString("LoginCookie", response.UserTicketToken);
                            startActivity(new Intent(ActLogin.this, ActSelectSite.class));
                            finish();
                        } else {
                            btnLogin.setVisibility(View.VISIBLE);
                            Toast.makeText(ActLogin.this, response.ErrorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        btnLogin.setVisibility(View.VISIBLE);
                        Toast.makeText(ActLogin.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
package ntk.android.cpanel.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.cpanel.R;
import ntk.android.cpanel.adapter.AdSelectSite;
import ntk.android.cpanel.config.ConfigRestHeader;
import ntk.android.cpanel.config.ConfigStaticValue;
import ntk.android.cpanel.utillity.EasyPreference;
import ntk.base.api.core.entity.CoreSite;
import ntk.base.api.core.interfase.ICoreSite;
import ntk.base.api.core.model.CoreSiteGetAllWithAliasRequest;
import ntk.base.api.core.model.CoreSiteResponse;
import ntk.base.api.utill.RetrofitManager;

public class ActSelectSite extends AppCompatActivity {

    @BindView(R.id.recyclerActSelectSite)
    RecyclerView Rv;

    private ConfigStaticValue configStaticValue = new ConfigStaticValue(this);
    private ConfigRestHeader configRestHeader = new ConfigRestHeader();
    private long lastPressedTime;
    private static final int PERIOD = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_site);
        ButterKnife.bind(this);
        getData();
    }

    private void initialization(List<CoreSite> list) {
        Rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        Rv.setAdapter(new AdSelectSite(this, list));
    }

    private void getData() {
        CoreSiteGetAllWithAliasRequest request = new CoreSiteGetAllWithAliasRequest();
        RetrofitManager manager = new RetrofitManager(ActSelectSite.this);
        ICoreSite iCore = manager.getRetrofit(configStaticValue.ApiBaseUrl).create(ICoreSite.class);
        Map<String, String> headers = new HashMap<>();
        headers = configRestHeader.GetHeaders(this);
        headers.put("Authorization", EasyPreference.with(ActSelectSite.this).getString(EasyPreference.LOGIN_COOKE, ""));
        Observable<CoreSiteResponse> call = iCore.GetAllWithAlias(headers, request);
        call.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<CoreSiteResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CoreSiteResponse response) {
                        initialization(response.ListItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ActSelectSite.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getDownTime() - lastPressedTime < PERIOD) {
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "برای خروج مجددا کلید بازگشت را فشار دهید",
                        Toast.LENGTH_SHORT).show();
                lastPressedTime = event.getEventTime();
            }
            return true;
        }
        return false;
    }
}

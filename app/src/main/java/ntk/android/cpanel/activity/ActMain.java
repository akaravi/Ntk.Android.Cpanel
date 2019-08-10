package ntk.android.cpanel.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import ntk.android.cpanel.adapter.AdMain;
import ntk.android.cpanel.adapter.AdSelectSite;
import ntk.android.cpanel.config.ConfigRestHeader;
import ntk.android.cpanel.config.ConfigStaticValue;
import ntk.android.cpanel.utillity.EasyPreference;
import ntk.base.api.core.entity.CoreCpMainMenu;
import ntk.base.api.core.interfase.ICoreCpMainMenu;
import ntk.base.api.core.interfase.ICoreUser;
import ntk.base.api.core.model.CoreCpMainMenuResponse;
import ntk.base.api.core.model.CoreGetAllMenuRequest;
import ntk.base.api.core.model.CoreUserResponse;
import ntk.base.api.core.model.CoreUserSelectCurrentSiteRequest;
import ntk.base.api.utill.RetrofitManager;

public class ActMain extends AppCompatActivity {

    @BindView(R.id.recyclerActMain)
    RecyclerView Rv;

    @BindView(R.id.progressActMain)
    ProgressBar bar;

    private ConfigStaticValue configStaticValue = new ConfigStaticValue(this);
    private ConfigRestHeader configRestHeader = new ConfigRestHeader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.bind(this);
        getData(getIntent().getLongExtra(AdSelectSite.SITE_ID, 0));
    }

    private void initialization(List<CoreCpMainMenu> list) {
        bar.setVisibility(View.GONE);
        Rv.setLayoutManager(new GridLayoutManager(ActMain.this, 4));
        Rv.setAdapter(new AdMain(ActMain.this, list));
    }

    private void getData(Long Id) {
        CoreUserSelectCurrentSiteRequest request = new CoreUserSelectCurrentSiteRequest();
        request.Id = Id;
        RetrofitManager manager = new RetrofitManager(ActMain.this);
        ICoreUser iCore = manager.getRetrofit(configStaticValue.ApiBaseUrl).create(ICoreUser.class);
        Map<String, String> headers = new HashMap<>();
        headers = configRestHeader.GetHeaders(ActMain.this);
        headers.put("Authorization", EasyPreference.with(ActMain.this).getString(EasyPreference.LOGIN_COOKE, ""));
        Observable<CoreUserResponse> call = iCore.SelectCurrentSite(headers, request);
        call.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<CoreUserResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CoreUserResponse response) {
                        EasyPreference.with(ActMain.this).addString(EasyPreference.SITE_COOKE, response.UserTicketToken);
                        getMenu();
                    }

                    @Override
                    public void onError(Throwable e) {
                        bar.setVisibility(View.GONE);
                        Toast.makeText(ActMain.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getMenu() {
        CoreGetAllMenuRequest request = new CoreGetAllMenuRequest();
        RetrofitManager manager = new RetrofitManager(ActMain.this);
        ICoreCpMainMenu iCore = manager.getRetrofit(configStaticValue.ApiBaseUrl).create(ICoreCpMainMenu.class);
        Map<String, String> headers = new HashMap<>();
        headers = configRestHeader.GetHeaders(ActMain.this);
        headers.put("Cookie", EasyPreference.with(ActMain.this).getString(EasyPreference.LOGIN_COOKE, ""));
        headers.put("Authorization", EasyPreference.with(ActMain.this).getString(EasyPreference.SITE_COOKE, ""));
        Observable<CoreCpMainMenuResponse> call = iCore.GetAllMenu(headers, request);
        call.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<CoreCpMainMenuResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CoreCpMainMenuResponse response) {

                        initialization(response.ListItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        bar.setVisibility(View.GONE);
                        Toast.makeText(ActMain.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }
}

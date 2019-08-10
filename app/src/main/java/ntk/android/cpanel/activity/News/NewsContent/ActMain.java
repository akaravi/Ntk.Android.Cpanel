package ntk.android.cpanel.activity.News.NewsContent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.cpanel.R;
import ntk.android.cpanel.adapter.AdNewsContent;
import ntk.android.cpanel.config.ConfigRestHeader;
import ntk.android.cpanel.config.ConfigStaticValue;
import ntk.android.cpanel.utillity.EasyPreference;
import ntk.base.api.news.entity.NewsContent;
import ntk.base.api.news.interfase.INewsContent;
import ntk.base.api.news.model.NewsContentResponse;
import ntk.base.api.news.model.NewsGetAllRequest;
import ntk.base.api.utill.RetrofitManager;

public class ActMain extends AppCompatActivity {

    @BindView(R.id.recyclerActDetail)
    RecyclerView recycler;

    @BindViews({R.id.fab,
            R.id.fabAdd,
            R.id.fabExport})
    List<FloatingActionButton> fab;

    private Boolean isFABOpen = false;
    private ConfigStaticValue configStaticValue = new ConfigStaticValue(this);
    private ConfigRestHeader configRestHeader = new ConfigRestHeader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dital);
        ButterKnife.bind(this);
        getData();
    }

    private void initialization(List<NewsContent> list) {
        fab.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFABOpen) {
                    closeFabMenu();
                } else {
                    showFabMenu();
                }

            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recycler.setAdapter(new AdNewsContent(this, list));
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isFABOpen)
                    closeFabMenu();
                if (dy > 0) {
                    fab.get(0).hide();
                } else if (dy < 0) {
                    fab.get(0).show();
                }
            }
        });
    }

    private void getData() {
        NewsGetAllRequest request = new NewsGetAllRequest();
        RetrofitManager manager = new RetrofitManager(this);
        INewsContent iNewsContent = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(INewsContent.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        headers.put("Authorization", EasyPreference.with(ActMain.this).getString(EasyPreference.SITE_COOKE, ""));
        Observable<NewsContentResponse> observable = iNewsContent.GetAll(headers, request);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<NewsContentResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NewsContentResponse response) {
                        initialization(response.ListItems);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ActMain.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showFabMenu() {
        Animation fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        Animation fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        for (int i = 1; i < fab.size(); i++) {
            fab.get(i).startAnimation(fab_open);
            fab.get(i).setClickable(true);
        }
        fab.get(0).startAnimation(fab_clock);
        isFABOpen = true;
    }

    private void closeFabMenu() {
        Animation fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        Animation fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);
        for (int i = 1; i < fab.size(); i++) {
            fab.get(i).startAnimation(fab_close);
            fab.get(i).setClickable(false);
        }
        fab.get(0).startAnimation(fab_anticlock);
        isFABOpen = false;
    }

    @OnClick(R.id.fabAdd)
    public void onAddClick() {
        if (isFABOpen) closeFabMenu();
        startActivity(new Intent(this, ActAdd.class));
    }

    @OnClick(R.id.fabExport)
    public void onEditClick() {
        if (isFABOpen) closeFabMenu();
    }
}

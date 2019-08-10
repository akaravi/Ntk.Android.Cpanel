package ntk.android.cpanel.activity.News.NewsContent;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import ntk.android.cpanel.config.ConfigRestHeader;
import ntk.android.cpanel.config.ConfigStaticValue;
import ntk.android.cpanel.utillity.EasyPreference;
import ntk.android.cpanel.utillity.FontManager;
import ntk.base.api.news.interfase.INewsContent;
import ntk.base.api.news.model.NewsContentAddRequest;
import ntk.base.api.news.model.NewsContentResponse;
import ntk.base.api.utill.RetrofitManager;

public class ActAdd extends AppCompatActivity {

    @BindViews({R.id.lblInfo,
            R.id.lblStatus,
            R.id.lblTitle,
            R.id.lblDescription,
            R.id.lblCategory,
            R.id.lblStartDate,
            R.id.lblEndDate,
            R.id.lbl,
            R.id.lblScore,
            R.id.lblSticker,
            R.id.lblKey,
            R.id.lblComplete,
            R.id.lblFile,
            R.id.lblMainImage,
            R.id.lblPodcast,
            R.id.lblMovie,
            R.id.lblAttach,
            R.id.lblLocation,
            R.id.txtBtn})
    List<TextView> lbls;

    @BindViews({R.id.txtTitle,
            R.id.txtDescription,
            R.id.txtCategory,
            R.id.txtScore,
            R.id.txtSticker,
            R.id.txtKey,
            R.id.txtComplete})
    List<EditText> txts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add);
        ButterKnife.bind(this);
        initialization();
    }

    private void initialization() {
        for (int i = 0; i < lbls.size(); i++) {
            lbls.get(i).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        }
        for (int i = 0; i < txts.size(); i++) {
            txts.get(i).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        }
    }

    @OnClick(R.id.btn)
    public void onSaveClick() {
        getData();
    }

    private void getData() {
        NewsContentAddRequest request = new NewsContentAddRequest();
        request.RecordStatus = 2;
        if (txts.get(0).getText() == null) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        } else {
            request.Title = txts.get(0).getText().toString();
        }
        request.Description = txts.get(1).getText().toString();
        RetrofitManager manager = new RetrofitManager(this);
        INewsContent iNewsContent = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(INewsContent.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        headers.put("Authorization", EasyPreference.with(this).getString(EasyPreference.SITE_COOKE, ""));
        Observable<NewsContentResponse> observable = iNewsContent.Add(headers, request);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<NewsContentResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NewsContentResponse response) {
                        if (response.IsSuccess) {
                            Toast.makeText(ActAdd.this, "با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ActAdd.this, "مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ActAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}

package ntk.android.cpanel.activity.News.NewsContent;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import ntk.base.api.news.interfase.INewsCategory;
import ntk.base.api.news.interfase.INewsContent;
import ntk.base.api.news.model.NewsCategoryResponse;
import ntk.base.api.news.model.NewsContentEditRequest;
import ntk.base.api.news.model.NewsContentResponse;
import ntk.base.api.news.model.NewsGetAllRequest;
import ntk.base.api.utill.RetrofitManager;

public class ActEdit extends AppCompatActivity {

    @BindViews({R.id.lblInfoActNewsContentEdit,
            R.id.lblStatusActNewsContentEdit,
            R.id.lblTitleActNewsContentEdit,
            R.id.lblDescriptionActNewsContentEdit,
            R.id.lblCategoryActNewsContentEdit,
            R.id.lblStartDateActNewsContentEdit,
            R.id.startDateActNewsContentEdit,
            R.id.lblEndDateActNewsContentEdit,
            R.id.EndDateActNewsContentEdit,
            R.id.lblActNewsContentEdit,
            R.id.lblScoreActNewsContentEdit,
            R.id.lblStickerActNewsContentEdit,
            R.id.lblKeyActNewsContentEdit,
            R.id.lblCompleteActNewsContentEdit,
            R.id.lblFileActNewsContentEdit,
            R.id.lblMainImageActNewsContentEdit,
            R.id.lblPodcastActNewsContentEdit,
            R.id.lblMovieActNewsContentEdit,
            R.id.lblAttachActNewsContentEdit,
            R.id.lblLocationActNewsContentEdit,
            R.id.lblMoreInfoActNewsContentEdit,
            R.id.lblMoreTitleActNewsContentEdit,
            R.id.lblMoreScoreActNewsContentEdit,
            R.id.lblMoreTypeActNewsContentEdit,
            R.id.lblMoreMainInfoActNewsContentEdit,
            R.id.lblSimilarInfoActNewsContentEdit,
            R.id.lblSimilarTitleActNewsContentEdit,
            R.id.lblSimilarScoreActNewsContentEdit,
            R.id.lblConnectionInfoActNewsContentEdit,
            R.id.lblConnectionTitleActNewsContentEdit,
            R.id.lblConnectionModelActNewsContentEdit,
            R.id.lblConnectionRelativeActNewsContentEdit,
            R.id.txtBtnActNewsContentEdit
    })
    List<TextView> lbls;


    @BindViews({R.id.txtTitleActNewsContentEdit,
            R.id.txtDescriptionActNewsContentEdit,
            R.id.txtScoreActNewsContentEdit,
            R.id.txtStickerActNewsContentEdit,
            R.id.txtKeyActNewsContentEdit,
            R.id.txtCompleteActNewsContentEdit,
            R.id.txtMoreTitleActNewsContentEdit,
            R.id.txtMoreScoreActNewsContentEdit,
            R.id.txtMoreTypeActNewsContentEdit,
            R.id.txtMoreMainInfoActNewsContentEdit,
            R.id.txtSimilarTitleActNewsContentEdit,
            R.id.txtSimilarScoreActNewsContentEdit,
            R.id.txtConnectionTitleActNewsContentEdit,
            R.id.txtConnectionModelActNewsContentEdit,
            R.id.txtConnectionRelativeActNewsContentEdit,
    })
    List<EditText> txts;

    @BindViews({R.id.spinnerStatusActNewsContentEdit,
            R.id.spinnerCategoryActNewsContentEdit})
    List<Spinner> spinner;

    private int categoryValue = 0;
    private int statusValue = 0;
    private final Calendar myCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_news_content_edit);
        ButterKnife.bind(this);
        initialization();
    }

    private void initialization() {
        getCategory();
        List<String> statusList = new ArrayList<>();
        statusList.add("فعال");
        statusList.add("حدف شده");
        statusList.add("معلق");
        statusList.add("تایید شده");
        statusList.add("رد شده");
        statusList.add("بایگانی");
        spinner.get(0).setAdapter(new ArrayAdapter<String>(this, R.layout.row_spinner, statusList));
        spinner.get(0).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                statusValue = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for (int i = 0; i < lbls.size(); i++) {
            lbls.get(i).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        }
        for (int i = 0; i < txts.size(); i++) {
            txts.get(i).setTypeface(FontManager.GetTypeface(this, FontManager.IranSans));
        }
    }

    @OnClick(R.id.btnActNewsContentEdit)
    public void onSaveClick() {
        getData();
    }

    private void getData() {
        NewsContentEditRequest request = new NewsContentEditRequest();
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
        Observable<NewsContentResponse> observable = iNewsContent.Edit(headers, request);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<NewsContentResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NewsContentResponse response) {
                        if (response.IsSuccess) {
                            Toast.makeText(ActEdit.this, "با موفقیت ثبت شد", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ActEdit.this, "مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ActEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getCategory() {
        NewsGetAllRequest request = new NewsGetAllRequest();
        RetrofitManager manager = new RetrofitManager(this);
        INewsCategory iNews = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(INewsCategory.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        headers.put("Authorization", EasyPreference.with(this).getString(EasyPreference.SITE_COOKE, ""));
        Observable<NewsCategoryResponse> observable = iNews.GetAll(headers, request);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<NewsCategoryResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NewsCategoryResponse response) {
                        if (response.IsSuccess) {
                            List<String> categoryList = new ArrayList<>();
                            for (int i = 0; i < response.ListItems.size(); i++) {
                                categoryList.add(response.ListItems.get(i).Title);
                            }
                            spinner.get(1).setAdapter(new ArrayAdapter<String>(ActEdit.this, R.layout.row_spinner, categoryList));
                            spinner.get(1).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    categoryValue = position;
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } else {
                            Toast.makeText(ActEdit.this, "مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ActEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("jhydefg55", "onError: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.startDateActNewsContentEdit)
    public void onStartDateClick() {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                lbls.get(6).setText(updateLabel().format(myCalendar.getTime()));
            }
        };
        new DatePickerDialog(ActEdit.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private SimpleDateFormat updateLabel() {
        String myFormat = "yyyy/MM/dd"; //In which you need put here
        return new SimpleDateFormat(myFormat, Locale.US);
    }
}

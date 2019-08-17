package ntk.android.cpanel.activity.News.NewsContent;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import ntk.android.cpanel.adapter.AdKeyWord;
import ntk.android.cpanel.adapter.AdSelectedTag;
import ntk.android.cpanel.adapter.AdTag;
import ntk.android.cpanel.config.ConfigRestHeader;
import ntk.android.cpanel.config.ConfigStaticValue;
import ntk.android.cpanel.utillity.AppUtill;
import ntk.android.cpanel.utillity.EasyPreference;
import ntk.android.cpanel.utillity.FontManager;
import ntk.base.api.file.interfase.IFile;
import ntk.base.api.news.entity.NewsTag;
import ntk.base.api.news.interfase.INewsCategory;
import ntk.base.api.news.interfase.INewsContent;
import ntk.base.api.news.interfase.INewsTag;
import ntk.base.api.news.model.NewsCategoryResponse;
import ntk.base.api.news.model.NewsContentAddRequest;
import ntk.base.api.news.model.NewsContentResponse;
import ntk.base.api.news.model.NewsGetAllRequest;
import ntk.base.api.news.model.NewsTagResponse;
import ntk.base.api.news.model.NewsTagSearchRequest;
import ntk.base.api.utill.RetrofitManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ActAdd extends AppCompatActivity {

    @BindViews({R.id.lblInfoActNewsContentAdd,
            R.id.lblStatusActNewsContentAdd,
            R.id.lblTitleActNewsContentAdd,
            R.id.lblDescriptionActNewsContentAdd,
            R.id.lblCategoryActNewsContentAdd,
            R.id.lblStartDateActNewsContentAdd,
            R.id.lblEndDateActNewsContentAdd,
            R.id.lblActNewsContentAdd,
            R.id.lblScoreActNewsContentAdd,
            R.id.lblStickerActNewsContentAdd,
            R.id.lblKeyActNewsContentAdd,
            R.id.lblCompleteActNewsContentAdd,
            R.id.lblFileActNewsContentAdd,
            R.id.lblMainImageActNewsContentAdd,
            R.id.lblPodcastActNewsContentAdd,
            R.id.lblMovieActNewsContentAdd,
            R.id.lblAttachActNewsContentAdd,
            R.id.lblLocationActNewsContentAdd,
            R.id.txtBtnActNewsContentAdd,
            R.id.startDateActNewsContentAdd,
            R.id.EndDateActNewsContentAdd,})
    List<TextView> lbls;

    @BindViews({R.id.txtTitleActNewsContentAdd,
            R.id.txtDescriptionActNewsContentAdd,
            R.id.txtScoreActNewsContentAdd,
            R.id.txtStickerActNewsContentAdd,
            R.id.txtKeyActNewsContentAdd,
            R.id.txtCompleteActNewsContentAdd})
    List<EditText> txts;

    @BindViews({R.id.spinnerStatusActNewsContentAdd,
            R.id.spinnerCategoryActNewsContentAdd})
    List<Spinner> spinner;

    @BindViews({R.id.recyclerStickerActNewsContent,
            R.id.recyclerKeyActNewsContent,
            R.id.recyclerSelectedStickerActNewsContent})
    List<RecyclerView> Rv;

    private Long categoryValue;
    private int statusValue = 1;
    private static final int READ_REQUEST_CODE = 1520;
    private Calendar myCalendar = Calendar.getInstance();
    private NewsContentAddRequest request = new NewsContentAddRequest();
    private List<NewsTag> tagsList = new ArrayList<>();
    private List<NewsTag> selectedTagsList = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();
    private AdTag adTag;
    private AdKeyWord adKeyWord;
    private AdSelectedTag adSelectedTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_news_content_add);
        ButterKnife.bind(this);
        initialization();
    }

    private void initialization() {
        getCategory();
        adSelectedTag = new AdSelectedTag(ActAdd.this, selectedTagsList);
        Rv.get(2).setLayoutManager(new LinearLayoutManager(ActAdd.this, RecyclerView.HORIZONTAL, false));
        Rv.get(2).setAdapter(adSelectedTag);
        adTag = new AdTag(ActAdd.this, tagsList, selectedTagsList, adSelectedTag);
        Rv.get(0).setLayoutManager(new LinearLayoutManager(ActAdd.this, RecyclerView.VERTICAL, false));
        Rv.get(0).setAdapter(adTag);
        adKeyWord = new AdKeyWord(ActAdd.this, keyList);
        Rv.get(1).setLayoutManager(new LinearLayoutManager(ActAdd.this, RecyclerView.HORIZONTAL, false));
        Rv.get(1).setAdapter(adKeyWord);
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
                statusValue = position + 1;
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
        txts.get(3).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findViewById(R.id.progressStickerActNewsContentAdd).setVisibility(View.VISIBLE);
                if (s.length() > 2)
                    searchTag(s.toString());
                if (s.toString().equals("")) {
                    txts.get(3).setBackground(ActAdd.this.getResources().getDrawable(R.drawable.edit_text_background));
                    findViewById(R.id.progressStickerActNewsContentAdd).setVisibility(View.GONE);
                    tagsList.clear();
                    adTag.notifyDataSetChanged();
                } else
                    txts.get(3).setBackground(ActAdd.this.getResources().getDrawable(R.drawable.false_text_background));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txts.get(4).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    findViewById(R.id.imgKeyAddActNewsContentAdd).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.imgKeyAddActNewsContentAdd).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchTag(String text) {
        NewsTagSearchRequest request = new NewsTagSearchRequest();
        request.text = text;
        RetrofitManager manager = new RetrofitManager(this);
        INewsTag iNews = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(INewsTag.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        headers.put("Authorization", EasyPreference.with(this).getString(EasyPreference.SITE_COOKE, ""));
        Observable<NewsTagResponse> observable = iNews.Searching(headers, request);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<NewsTagResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NewsTagResponse response) {
                        findViewById(R.id.progressStickerActNewsContentAdd).setVisibility(View.GONE);
                        if (response.IsSuccess) {
                            if (response.ListItems.isEmpty()) {
                                txts.get(3).setBackground(ActAdd.this.getResources().getDrawable(R.drawable.false_text_background));
                                Toast.makeText(ActAdd.this, "موردی یافت نشد", Toast.LENGTH_LONG).show();
                                tagsList.clear();
                                adTag.notifyDataSetChanged();
                            } else {
                                txts.get(3).setBackground(ActAdd.this.getResources().getDrawable(R.drawable.edit_text_background));
                                tagsList.clear();
                                tagsList.addAll(response.ListItems);
                                adTag.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(ActAdd.this, "مجددا تلاش کنید", Toast.LENGTH_LONG).show();
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

    @OnClick(R.id.btnActNewsContentAdd)
    public void onSaveClick() {
        getData();
    }

    private void getData() {
        request.RecordStatus = statusValue;
        request.Title = txts.get(0).getText().toString();
        request.Description = txts.get(1).getText().toString();
        request.LinkCategoryId = String.valueOf(categoryValue);
        request.Source = txts.get(2).getText().toString();
        request.Body = txts.get(5).getText().toString();
        request.Keyword = keyList.toString().replace(" ", "").replace("[", "").replace("]", "");
        RetrofitManager manager = new RetrofitManager(this);
        INewsContent iNewsContent = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(INewsContent.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        headers.put("Authorization", EasyPreference.with(this).getString(EasyPreference.SITE_COOKE, ""));
        headers.put("Cookie", EasyPreference.with(this).getString(EasyPreference.LOGIN_COOKE, ""));
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
                            addBatch();
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

    private void addBatch() {
        request.RecordStatus = statusValue;
        request.Title = txts.get(0).getText().toString();
        request.Description = txts.get(1).getText().toString();
        request.LinkCategoryId = String.valueOf(categoryValue);
        request.Source = txts.get(2).getText().toString();
        request.Body = txts.get(5).getText().toString();
        request.Keyword = keyList.toString().replace(" ", "").replace("[", "").replace("]", "");
        RetrofitManager manager = new RetrofitManager(this);
        INewsContent iNewsContent = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(INewsContent.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        headers.put("Authorization", EasyPreference.with(this).getString(EasyPreference.SITE_COOKE, ""));
        headers.put("Cookie", EasyPreference.with(this).getString(EasyPreference.LOGIN_COOKE, ""));
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
                            addBatch();
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
                            categoryValue = response.ListItems.get(0).Id;
                            for (int i = 0; i < response.ListItems.size(); i++) {
                                categoryList.add(response.ListItems.get(i).Title);
                            }
                            spinner.get(1).setAdapter(new ArrayAdapter<String>(ActAdd.this, R.layout.row_spinner, categoryList));
                            spinner.get(1).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    categoryValue = response.ListItems.get(position).Id;
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
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

    @OnClick(R.id.startDateActNewsContentAdd)
    public void onStartDateClick() {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                lbls.get(19).setText(updateLabel().format(myCalendar.getTime()));
                request.FromDate = dataFormat().format(myCalendar.getTime());
            }
        };
        new DatePickerDialog(ActAdd.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @OnClick(R.id.EndDateActNewsContentAdd)
    public void onEndDateClick() {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                lbls.get(20).setText(updateLabel().format(myCalendar.getTime()));
                request.ExpireDate = dataFormat().format(myCalendar.getTime());
            }
        };
        new DatePickerDialog(ActAdd.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private SimpleDateFormat updateLabel() {
        String myFormat = "yyyy/MM/dd";
        return new SimpleDateFormat(myFormat, Locale.US);
    }

    private SimpleDateFormat dataFormat() {
        String myFormat = "yyyy-MM-dd'T'HH:mm:ss";
        return new SimpleDateFormat(myFormat, Locale.US);
    }

    @OnClick(R.id.imgKeyAddActNewsContentAdd)
    public void addKeyWord() {
        String s = txts.get(4).getText().toString();
        if (!s.equals("")) {
            keyList.add(s);
            adKeyWord.notifyDataSetChanged();
            txts.get(4).setText("");
        }
    }

    @OnClick(R.id.imgMainImageAttach)
    public void onMainImageAttach() {
        if (AppUtill.CheckPermission(ActAdd.this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, READ_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActAdd.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 220);
        }
    }

    @OnClick(R.id.imgPadcastAttach)
    public void onPadcastAttach() {
        if (AppUtill.CheckPermission(ActAdd.this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, READ_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActAdd.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 220);
        }
    }

    @OnClick(R.id.imgMovieAttach)
    public void onMovieAttach() {
        if (AppUtill.CheckPermission(ActAdd.this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, READ_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActAdd.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 220);
        }
    }

    @OnClick(R.id.imgExtraFileAttach)
    public void onExtraFileAttach() {
        if (AppUtill.CheckPermission(ActAdd.this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, READ_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActAdd.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 220);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
            if (resultData != null) {
                uri = resultData.getData();
                if (uri != null) {
                    UploadFileToServer(AppUtill.getPath(ActAdd.this, uri));
                }
            }
        }
    }

    private void UploadFileToServer(String url) {
        if (AppUtill.isNetworkAvailable(this)) {
            File file = new File(String.valueOf(Uri.parse(url)));
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RetrofitManager retro = new RetrofitManager(this);
            Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
            IFile iFile = retro.getRetrofitUnCached(new ConfigStaticValue(this).GetApiBaseUrl()).create(IFile.class);
            Observable<String> Call = iFile.uploadFileWithPartMap(headers, new HashMap<>(), MultipartBody.Part.createFormData("File", file.getName(), requestFile));
            Call.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(String model) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {

        }
    }

}

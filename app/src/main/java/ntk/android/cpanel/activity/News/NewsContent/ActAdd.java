package ntk.android.cpanel.activity.News.NewsContent;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import java.util.Objects;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.cpanel.R;
import ntk.android.cpanel.adapter.AdAttach;
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
import ntk.base.api.news.interfase.INewsContentTag;
import ntk.base.api.news.interfase.INewsTag;
import ntk.base.api.news.model.NewsCategoryResponse;
import ntk.base.api.news.model.NewsContentAddAndEditRequest;
import ntk.base.api.news.model.NewsContentResponse;
import ntk.base.api.news.model.NewsContentTagAddBatchRequest;
import ntk.base.api.news.model.NewsContentTagResponse;
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
            R.id.EndDateActNewsContentAdd,
            R.id.txtMainImageActNewsContentAdd,
            R.id.txtPodcastActNewsContentAdd,
            R.id.txtMovieActNewsContentAdd})
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
            R.id.recyclerSelectedStickerActNewsContent,
            R.id.recyclerAttachActNewsContentAdd})
    List<RecyclerView> Rv;

    private Long categoryValue;
    private int statusValue = 1;
    public static final int READ_REQUEST_CODE = 1520;
    public static final int MAIN_IMAGE_REQUEST_CODE = 558;
    public static final int PODCAST_REQUEST_CODE = 874;
    public static final int MOVIE_REQUEST_CODE = 457;
    public static final int EXTRA_FILE_REQUEST_CODE = 336;
    private Calendar myCalendar = Calendar.getInstance();
    private NewsContentAddAndEditRequest mainRequest = new NewsContentAddAndEditRequest();
    private List<NewsTag> tagsList = new ArrayList<>();
    private List<NewsTag> selectedTagsList = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();
    private List<String> attachList = new ArrayList<>();
    private AdTag adTag;
    private AdKeyWord adKeyWord;
    private AdAttach adAttach;
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
        adAttach = new AdAttach(ActAdd.this, attachList);
        Rv.get(3).setLayoutManager(new LinearLayoutManager(ActAdd.this, RecyclerView.HORIZONTAL, false));
        Rv.get(3).setAdapter(adAttach);
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
        mainRequest.RecordStatus = statusValue;
        mainRequest.Title = txts.get(0).getText().toString();
        mainRequest.Description = txts.get(1).getText().toString();
        mainRequest.LinkCategoryId = String.valueOf(categoryValue);
        mainRequest.Source = txts.get(2).getText().toString();
        mainRequest.Body = txts.get(5).getText().toString();
        mainRequest.Keyword = keyList.toString().replace(" ", "").replace("[", "").replace("]", "");
        RetrofitManager manager = new RetrofitManager(this);
        INewsContent iNewsContent = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(INewsContent.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        headers.put("Authorization", EasyPreference.with(this).getString(EasyPreference.SITE_COOKE, ""));
        headers.put("Cookie", EasyPreference.with(this).getString(EasyPreference.LOGIN_COOKE, ""));
        Observable<NewsContentResponse> observable = iNewsContent.Add(headers, mainRequest);
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

    private void addBatch() {
        NewsContentTagAddBatchRequest request=new NewsContentTagAddBatchRequest();
        RetrofitManager manager = new RetrofitManager(this);
        INewsContentTag iNews = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(INewsContentTag.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        headers.put("Authorization", EasyPreference.with(this).getString(EasyPreference.SITE_COOKE, ""));
        headers.put("Cookie", EasyPreference.with(this).getString(EasyPreference.LOGIN_COOKE, ""));
        Observable<NewsContentTagResponse> observable = iNews.AddBatch(headers, request);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<NewsContentTagResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NewsContentTagResponse response) {
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
                mainRequest.FromDate = dataFormat().format(myCalendar.getTime());
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
                mainRequest.ExpireDate = dataFormat().format(myCalendar.getTime());
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
            startActivityForResult(intent, MAIN_IMAGE_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActAdd.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST_CODE);
        }
    }

    @OnClick(R.id.imgPadcastAttach)
    public void onPadcastAttach() {
        if (AppUtill.CheckPermission(ActAdd.this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, PODCAST_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActAdd.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST_CODE);
        }
    }

    @OnClick(R.id.imgMovieAttach)
    public void onMovieAttach() {
        if (AppUtill.CheckPermission(ActAdd.this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, MOVIE_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActAdd.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST_CODE);
        }
    }

    @OnClick(R.id.imgExtraFileAttach)
    public void onExtraFileAttach() {
        if (AppUtill.CheckPermission(ActAdd.this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, EXTRA_FILE_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActAdd.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        Uri uri;
        if (resultData != null) {
            uri = resultData.getData();
            if (uri != null) {
                String name = Objects.requireNonNull(uri.getPath()).split("/")[uri.getPath().split("/").length - 1];
                switch (requestCode) {
                    case MAIN_IMAGE_REQUEST_CODE:
                        if (resultCode == RESULT_OK) {
                            findViewById(R.id.progressMainImageActNewsContentAdd).setVisibility(View.VISIBLE);
                            findViewById(R.id.imgProgressMainImageActNewsContentAdd).setVisibility(View.GONE);
                            findViewById(R.id.lblDeleteMainImageActNewsContentAdd).setVisibility(View.GONE);
                            UploadFileToServer(AppUtill.getPath(ActAdd.this, uri),
                                    findViewById(R.id.progressMainImageActNewsContentAdd),
                                    findViewById(R.id.imgProgressMainImageActNewsContentAdd),
                                    lbls.get(21),
                                    0);
                            lbls.get(21).setText(name);
                        } else {
                            findViewById(R.id.progressMainImageActNewsContentAdd).setVisibility(View.GONE);
                            findViewById(R.id.imgProgressMainImageActNewsContentAdd).setVisibility(View.GONE);
                            findViewById(R.id.lblDeleteMainImageActNewsContentAdd).setVisibility(View.GONE);
                        }
                        break;
                    case PODCAST_REQUEST_CODE:
                        if (resultCode == RESULT_OK) {
                            findViewById(R.id.progressPodcastActNewsContentAdd).setVisibility(View.VISIBLE);
                            findViewById(R.id.imgProgressPodcastActNewsContentAdd).setVisibility(View.GONE);
                            findViewById(R.id.lblDeletePodcastActNewsContentAdd).setVisibility(View.GONE);
                            UploadFileToServer(AppUtill.getPath(ActAdd.this, uri),
                                    findViewById(R.id.progressPodcastActNewsContentAdd),
                                    findViewById(R.id.imgProgressPodcastActNewsContentAdd),
                                    lbls.get(22),
                                    1);
                            lbls.get(22).setText(name);
                        } else {
                            findViewById(R.id.progressPodcastActNewsContentAdd).setVisibility(View.GONE);
                            findViewById(R.id.imgProgressPodcastActNewsContentAdd).setVisibility(View.GONE);
                            findViewById(R.id.lblDeletePodcastActNewsContentAdd).setVisibility(View.GONE);
                        }
                        break;
                    case MOVIE_REQUEST_CODE:
                        if (resultCode == RESULT_OK) {
                            findViewById(R.id.progressMovieActNewsContentAdd).setVisibility(View.VISIBLE);
                            findViewById(R.id.imgProgressMovieActNewsContentAdd).setVisibility(View.GONE);
                            findViewById(R.id.lblDeleteMovieActNewsContentAdd).setVisibility(View.GONE);
                            UploadFileToServer(AppUtill.getPath(ActAdd.this, uri),
                                    findViewById(R.id.progressMovieActNewsContentAdd),
                                    findViewById(R.id.imgProgressMovieActNewsContentAdd),
                                    lbls.get(23),
                                    2);
                            lbls.get(23).setText(name);
                        } else {
                            findViewById(R.id.progressMovieActNewsContentAdd).setVisibility(View.GONE);
                            findViewById(R.id.imgProgressMovieActNewsContentAdd).setVisibility(View.GONE);
                            findViewById(R.id.lblDeleteMovieActNewsContentAdd).setVisibility(View.GONE);
                        }
                        break;
                    case EXTRA_FILE_REQUEST_CODE:
                        attachList.add(name);
                        adAttach.notifyDataSetChanged();
                        findViewById(R.id.progressAttachActNewsContentAdd).setVisibility(View.VISIBLE);
                        findViewById(R.id.imgExtraFileAttach).setVisibility(View.GONE);
                        UploadFileToServer(AppUtill.getPath(ActAdd.this, uri),
                                findViewById(R.id.progressAttachActNewsContentAdd),
                                findViewById(R.id.imgExtraFileAttach),
                                null,
                                3);
                        break;
                }
            }
        }
    }

    private void UploadFileToServer(String url, ProgressBar bar, ImageView img, @Nullable TextView txt, int type) {
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
                        public void onNext(String s) {
                            switch (type) {
                                case 0:
                                    mainRequest.LinkMainImageId = s;
                                    findViewById(R.id.lblDeleteMainImageActNewsContentAdd).setVisibility(View.VISIBLE);
                                    break;
                                case 1:
                                    mainRequest.LinkFilePodcastId = s;
                                    findViewById(R.id.lblDeletePodcastActNewsContentAdd).setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    mainRequest.LinkFileMovieId = s;
                                    findViewById(R.id.lblDeleteMovieActNewsContentAdd).setVisibility(View.VISIBLE);
                                    break;
                                case 3:
                                    if (mainRequest.LinkFileIds == null) mainRequest.LinkFileIds = s;
                                    else mainRequest.LinkFileIds = mainRequest.LinkFileIds + "," + s;
                                    break;
                            }
                            bar.setVisibility(View.GONE);
                            img.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            bar.setVisibility(View.GONE);
                            img.setVisibility(View.GONE);
                            if (txt != null)
                                txt.setText("");
                            Toast.makeText(ActAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    @OnClick(R.id.lblDeleteMainImageActNewsContentAdd)
    public void deleteMainImage() {
        mainRequest.LinkMainImageId = "";
        findViewById(R.id.progressMainImageActNewsContentAdd).setVisibility(View.GONE);
        findViewById(R.id.imgProgressMainImageActNewsContentAdd).setVisibility(View.GONE);
        findViewById(R.id.lblDeleteMainImageActNewsContentAdd).setVisibility(View.GONE);
        lbls.get(21).setText("");
    }

    @OnClick(R.id.lblDeletePodcastActNewsContentAdd)
    public void deletePodcast() {
        mainRequest.LinkFilePodcastId = "";
        findViewById(R.id.progressPodcastActNewsContentAdd).setVisibility(View.GONE);
        findViewById(R.id.imgProgressPodcastActNewsContentAdd).setVisibility(View.GONE);
        findViewById(R.id.lblDeletePodcastActNewsContentAdd).setVisibility(View.GONE);
        lbls.get(22).setText("");
    }

    @OnClick(R.id.lblDeleteMovieActNewsContentAdd)
    public void deleteMovie() {
        mainRequest.LinkFileMovieId = "";
        findViewById(R.id.progressMovieActNewsContentAdd).setVisibility(View.GONE);
        findViewById(R.id.imgProgressMovieActNewsContentAdd).setVisibility(View.GONE);
        findViewById(R.id.lblDeleteMovieActNewsContentAdd).setVisibility(View.GONE);
        lbls.get(23).setText("");
    }
}

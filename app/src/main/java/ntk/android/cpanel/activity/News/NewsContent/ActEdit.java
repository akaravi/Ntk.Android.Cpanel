package ntk.android.cpanel.activity.News.NewsContent;

import android.Manifest;
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

import com.google.gson.Gson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import ntk.android.cpanel.adapter.AdNewsContent;
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
import ntk.base.api.news.model.NewsContentAddAndEditRequest;
import ntk.base.api.news.model.NewsContentResponse;
import ntk.base.api.news.model.NewsGetAllRequest;
import ntk.base.api.news.model.NewsTagResponse;
import ntk.base.api.news.model.NewsTagSearchRequest;
import ntk.base.api.utill.RetrofitManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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

    @BindViews({R.id.recyclerSelectedStickerActNewsContent,
            R.id.recyclerStickerActNewsContent,
            R.id.recyclerKeyActNewsContent,
            R.id.recyclerAttachActNewsContentEdit})
    List<RecyclerView> recycler;

    private int categoryValue = 0;
    private int statusValue = 0;
    private final Calendar myCalendar = Calendar.getInstance();
    private NewsContentAddAndEditRequest newsContent;
    private List<String> attachList = new ArrayList<>();
    private AdAttach adAttach;
    private List<NewsTag> tagsList = new ArrayList<>();
    private AdTag adTag;
    private List<NewsTag> selectedTagsList = new ArrayList<>();
    private List<String> keyList = new ArrayList<>();
    private AdKeyWord adKeyWord;
    private AdSelectedTag adSelectedTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_news_content_edit);
        ButterKnife.bind(this);
        initialization();
    }

    private void initialization() {
        newsContent = new Gson().fromJson(getIntent().getStringExtra(AdNewsContent.REQUEST), NewsContentAddAndEditRequest.class);
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
        txts.get(0).setText(newsContent.Title);
        txts.get(1).setText(newsContent.Description);
        txts.get(2).setText(newsContent.Source);
        txts.get(5).setText(newsContent.Body);
        if (newsContent.FromDate != null)
            lbls.get(6).setText(AppUtill.GregorianToPersian(newsContent.FromDate));
        if (newsContent.ExpireDate != null)
            lbls.get(8).setText(AppUtill.GregorianToPersian(newsContent.ExpireDate));
        spinner.get(0).setSelection(newsContent.RecordStatus - 1);
        if (!newsContent.Keyword.equals("")) {
            List<String> list = new ArrayList<>(Arrays.asList(newsContent.Keyword.split(",")));
            recycler.get(2).setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            recycler.get(2).setAdapter(new AdKeyWord(this, list));
        }
        adSelectedTag = new AdSelectedTag(ActEdit.this, selectedTagsList);
        recycler.get(2).setLayoutManager(new LinearLayoutManager(ActEdit.this, RecyclerView.HORIZONTAL, false));
        recycler.get(2).setAdapter(adSelectedTag);
        adTag = new AdTag(ActEdit.this, tagsList, selectedTagsList, adSelectedTag);
        recycler.get(0).setLayoutManager(new LinearLayoutManager(ActEdit.this, RecyclerView.VERTICAL, false));
        recycler.get(0).setAdapter(adTag);
        adKeyWord = new AdKeyWord(ActEdit.this, keyList);
        recycler.get(1).setLayoutManager(new LinearLayoutManager(ActEdit.this, RecyclerView.HORIZONTAL, false));
        recycler.get(1).setAdapter(adKeyWord);
        adAttach = new AdAttach(ActEdit.this, attachList);
        recycler.get(3).setLayoutManager(new LinearLayoutManager(ActEdit.this, RecyclerView.HORIZONTAL, false));
        recycler.get(3).setAdapter(adAttach);
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
                    txts.get(3).setBackground(ActEdit.this.getResources().getDrawable(R.drawable.edit_text_background));
                    findViewById(R.id.progressStickerActNewsContentAdd).setVisibility(View.GONE);
                    tagsList.clear();
                    adTag.notifyDataSetChanged();
                } else
                    txts.get(3).setBackground(ActEdit.this.getResources().getDrawable(R.drawable.false_text_background));
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
                                txts.get(3).setBackground(ActEdit.this.getResources().getDrawable(R.drawable.false_text_background));
                                Toast.makeText(ActEdit.this, "موردی یافت نشد", Toast.LENGTH_LONG).show();
                                tagsList.clear();
                                adTag.notifyDataSetChanged();
                            } else {
                                txts.get(3).setBackground(ActEdit.this.getResources().getDrawable(R.drawable.edit_text_background));
                                tagsList.clear();
                                tagsList.addAll(response.ListItems);
                                adTag.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(ActEdit.this, "مجددا تلاش کنید", Toast.LENGTH_LONG).show();
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

    @OnClick(R.id.btnActNewsContentEdit)
    public void onSaveClick() {
        getData();
    }

    private void getData() {
        RetrofitManager manager = new RetrofitManager(this);
        INewsContent iNewsContent = manager.getCachedRetrofit(new ConfigStaticValue(this).GetApiBaseUrl()).create(INewsContent.class);
        Map<String, String> headers = new ConfigRestHeader().GetHeaders(this);
        headers.put("Authorization", EasyPreference.with(this).getString(EasyPreference.SITE_COOKE, ""));
        Observable<NewsContentResponse> observable = iNewsContent.Edit(headers, newsContent);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<NewsContentResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NewsContentResponse response) {
                        if (response.IsSuccess) {
                            Toast.makeText(ActEdit.this, "با موفقیت ویرایش شد", Toast.LENGTH_SHORT).show();
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
                            for (int i = 0; i < response.ListItems.size(); i++) {
                                if (newsContent.LinkCategoryId.equals(String.valueOf(response.ListItems.get(i).Id))) {
                                    spinner.get(1).setSelection(i);
                                    return;
                                }
                            }
                        } else {
                            Toast.makeText(ActEdit.this, "مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ActEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("onError", "onError: " + e.getMessage());

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

    @OnClick(R.id.EndDateActNewsContentEdit)
    public void onDateDateClick() {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                lbls.get(8).setText(updateLabel().format(myCalendar.getTime()));
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

    @OnClick(R.id.imgMainImageAttach)
    public void onMainImageAttach() {
        if (AppUtill.CheckPermission(ActEdit.this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, ActAdd.MAIN_IMAGE_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActEdit.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActAdd.READ_REQUEST_CODE);
        }
    }

    @OnClick(R.id.imgPadcastAttach)
    public void onPadcastAttach() {
        if (AppUtill.CheckPermission(ActEdit.this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, ActAdd.PODCAST_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActEdit.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActAdd.READ_REQUEST_CODE);
        }
    }

    @OnClick(R.id.imgMovieAttach)
    public void onMovieAttach() {
        if (AppUtill.CheckPermission(ActEdit.this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, ActAdd.MOVIE_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActEdit.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActAdd.READ_REQUEST_CODE);
        }
    }

    @OnClick(R.id.imgExtraFileAttach)
    public void onExtraFileAttach() {
        if (AppUtill.CheckPermission(ActEdit.this)) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, ActAdd.EXTRA_FILE_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(ActEdit.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ActAdd.READ_REQUEST_CODE);
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
                    case ActAdd.MAIN_IMAGE_REQUEST_CODE:
                        if (resultCode == RESULT_OK) {
                            findViewById(R.id.progressMainImageActNewsContentEdit).setVisibility(View.VISIBLE);
                            findViewById(R.id.imgProgressMainImageActNewsContentEdit).setVisibility(View.GONE);
                            findViewById(R.id.lblDeleteMainImageActNewsContentEdit).setVisibility(View.GONE);
                            UploadFileToServer(AppUtill.getPath(ActEdit.this, uri),
                                    findViewById(R.id.progressMainImageActNewsContentEdit),
                                    findViewById(R.id.imgProgressMainImageActNewsContentEdit),
                                    lbls.get(21),
                                    0);
                            lbls.get(21).setText(name);
                        } else {
                            findViewById(R.id.progressMainImageActNewsContentEdit).setVisibility(View.GONE);
                            findViewById(R.id.imgProgressMainImageActNewsContentEdit).setVisibility(View.GONE);
                            findViewById(R.id.lblDeleteMainImageActNewsContentEdit).setVisibility(View.GONE);
                        }
                        break;
                    case ActAdd.PODCAST_REQUEST_CODE:
                        if (resultCode == RESULT_OK) {
                            findViewById(R.id.progressPodcastActNewsContentEdit).setVisibility(View.VISIBLE);
                            findViewById(R.id.imgProgressPodcastActNewsContentEdit).setVisibility(View.GONE);
                            findViewById(R.id.lblDeletePodcastActNewsContentEdit).setVisibility(View.GONE);
                            UploadFileToServer(AppUtill.getPath(ActEdit.this, uri),
                                    findViewById(R.id.progressPodcastActNewsContentEdit),
                                    findViewById(R.id.imgProgressPodcastActNewsContentEdit),
                                    lbls.get(22),
                                    1);
                            lbls.get(22).setText(name);
                        } else {
                            findViewById(R.id.progressPodcastActNewsContentEdit).setVisibility(View.GONE);
                            findViewById(R.id.imgProgressPodcastActNewsContentEdit).setVisibility(View.GONE);
                            findViewById(R.id.lblDeletePodcastActNewsContentEdit).setVisibility(View.GONE);
                        }
                        break;
                    case ActAdd.MOVIE_REQUEST_CODE:
                        if (resultCode == RESULT_OK) {
                            findViewById(R.id.progressMovieActNewsContentEdit).setVisibility(View.VISIBLE);
                            findViewById(R.id.imgProgressMovieActNewsContentEdit).setVisibility(View.GONE);
                            findViewById(R.id.lblDeleteMovieActNewsContentEdit).setVisibility(View.GONE);
                            UploadFileToServer(AppUtill.getPath(ActEdit.this, uri),
                                    findViewById(R.id.progressMovieActNewsContentEdit),
                                    findViewById(R.id.imgProgressMovieActNewsContentEdit),
                                    lbls.get(23),
                                    2);
                            lbls.get(23).setText(name);
                        } else {
                            findViewById(R.id.progressMovieActNewsContentEdit).setVisibility(View.GONE);
                            findViewById(R.id.imgProgressMovieActNewsContentEdit).setVisibility(View.GONE);
                            findViewById(R.id.lblDeleteMovieActNewsContentEdit).setVisibility(View.GONE);
                        }
                        break;
                    case ActAdd.EXTRA_FILE_REQUEST_CODE:
                        attachList.add(name);
                        adAttach.notifyDataSetChanged();
                        findViewById(R.id.progressAttachActNewsContentEdit).setVisibility(View.VISIBLE);
                        findViewById(R.id.imgExtraFileAttach).setVisibility(View.GONE);
                        UploadFileToServer(AppUtill.getPath(ActEdit.this, uri),
                                findViewById(R.id.progressAttachActNewsContentEdit),
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
                                    newsContent.LinkMainImageId = s;
                                    findViewById(R.id.lblDeleteMainImageActNewsContentEdit).setVisibility(View.VISIBLE);
                                    break;
                                case 1:
                                    newsContent.LinkFilePodcastId = s;
                                    findViewById(R.id.lblDeletePodcastActNewsContentEdit).setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    newsContent.LinkFileMovieId = s;
                                    findViewById(R.id.lblDeleteMovieActNewsContentEdit).setVisibility(View.VISIBLE);
                                    break;
                                case 3:
                                    if (newsContent.LinkFileIds == null)
                                        newsContent.LinkFileIds = s;
                                    else
                                        newsContent.LinkFileIds = newsContent.LinkFileIds + "," + s;
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
                            Toast.makeText(ActEdit.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }
}

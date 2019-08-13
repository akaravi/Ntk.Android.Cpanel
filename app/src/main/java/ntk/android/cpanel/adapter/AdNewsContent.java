package ntk.android.cpanel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ntk.android.cpanel.R;
import ntk.android.cpanel.activity.News.NewsContent.ActEdit;
import ntk.android.cpanel.config.ConfigRestHeader;
import ntk.android.cpanel.config.ConfigStaticValue;
import ntk.android.cpanel.utillity.AppUtill;
import ntk.android.cpanel.utillity.EasyPreference;
import ntk.android.cpanel.utillity.FontManager;
import ntk.base.api.news.entity.NewsContent;
import ntk.base.api.news.interfase.INewsContent;
import ntk.base.api.news.model.NewsContentResponse;
import ntk.base.api.utill.RetrofitManager;

public class AdNewsContent extends RecyclerView.Adapter<AdNewsContent.ViewHolderNewsContent> {

    private Context context;
    private List<NewsContent> list;
    private ConfigStaticValue configStaticValue;
    private ConfigRestHeader configRestHeader;

    public AdNewsContent(Context context, List<NewsContent> list) {
        this.context = context;
        this.list = list;
        configStaticValue = new ConfigStaticValue(context);
        configRestHeader = new ConfigRestHeader();
    }

    @NonNull
    @Override
    public ViewHolderNewsContent onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderNewsContent(LayoutInflater.from(context).inflate(R.layout.row_news_content, viewGroup, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderNewsContent holder, int position) {
        if (list.get(position).MainImageSrc != null) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true).build();
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
            ImageLoader.getInstance().displayImage(list.get(position).MainImageSrc, holder.img, options, null);
        }
        holder.txt.get(0).setText(list.get(position).Title);
        holder.txt.get(1).setText("کد سیستمی : " + list.get(position).Id);
        holder.txt.get(2).setText("کد سیستمی سایت : " + list.get(position).LinkSiteId);
        if (list.get(position).CreatedDate != null)
            holder.txt.get(3).setText("تاریخ ساخت : " + AppUtill.GregorianToPersian(list.get(position).CreatedDate));
        if (list.get(position).UpdatedDate != null)
            holder.txt.get(4).setText("تاریخ ویرایش : " + AppUtill.GregorianToPersian(list.get(position).UpdatedDate));
        if (list.get(position).FromDate != null)
            holder.txt.get(5).setText("تاریخ آغاز : " + AppUtill.GregorianToPersian(list.get(position).FromDate));
        if (list.get(position).ExpireDate != null)
            holder.txt.get(6).setText("تاریخ پایان : " + AppUtill.GregorianToPersian(list.get(position).ExpireDate));
        holder.txt.get(7).setText("تعداد بازدید : " + list.get(position).ViewCount);
        holder.txt.get(8).setText("توضیحات : " + list.get(position).Description);
        if (list.get(position).Body != null)
            holder.txt.get(9).setText("متن : " + Html.fromHtml(list.get(position).Body).toString());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ActEdit.class));
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsDelete(list.get(position), position);
            }
        });
    }

    private void newsDelete(NewsContent model, int position) {
        RetrofitManager manager = new RetrofitManager(context);
        INewsContent iNews = manager.getRetrofit(configStaticValue.ApiBaseUrl).create(INewsContent.class);
        Map<String, String> headers = new HashMap<>();
        headers = configRestHeader.GetHeaders(context);
        headers.put("Authorization", EasyPreference.with(context).getString(EasyPreference.SITE_COOKE, ""));
        headers.put("Cookie", EasyPreference.with(context).getString(EasyPreference.LOGIN_COOKE, ""));
        Observable<NewsContentResponse> call = iNews.Delete(headers, model);
        call.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<NewsContentResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(NewsContentResponse response) {
                        if (response.IsSuccess) {
                            list.remove(list.get(position));
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, list.size());
                            Toast.makeText(context, "با موفقیت حذف شد", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "مجددا تلاش کنید", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderNewsContent extends RecyclerView.ViewHolder {

        @BindView(R.id.imgNewsContentImage)
        ImageView img;

        @BindView(R.id.imgNewsContentEdit)
        ImageView edit;

        @BindView(R.id.imgNewsContentDelete)
        ImageView delete;

        @BindViews({R.id.txtNewsContentTitle,
                R.id.txtNewsContentCode,
                R.id.txtNewsContentSiteCode,
                R.id.txtNewsContentCreateDate,
                R.id.txtNewsContentEditDate,
                R.id.txtNewsContentStartDate,
                R.id.txtNewsContentExpireDate,
                R.id.txtNewsContentSeenNumber,
                R.id.txtNewsContentDescription,
                R.id.txtNewsContentBody})
        List<TextView> txt;

        ViewHolderNewsContent(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            for (int i = 0; i < txt.size(); i++) {
                txt.get(i).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            }
        }
    }
}

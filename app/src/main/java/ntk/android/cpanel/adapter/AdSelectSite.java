package ntk.android.cpanel.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import ntk.android.cpanel.R;
import ntk.android.cpanel.activity.ActMain;
import ntk.android.cpanel.config.ConfigRestHeader;
import ntk.android.cpanel.config.ConfigStaticValue;
import ntk.android.cpanel.utillity.FontManager;
import ntk.base.api.core.entity.CoreSite;

public class AdSelectSite extends RecyclerView.Adapter<AdSelectSite.ViewHolderSelectSite> {

    private Context context;
    private List<CoreSite> list;
    private ConfigStaticValue configStaticValue;
    private ConfigRestHeader configRestHeader;
    public static final String SITE_ID = "Id";

    public AdSelectSite(Context context, List<CoreSite> list) {
        this.context = context;
        this.list = list;
        configStaticValue = new ConfigStaticValue(context);
        configRestHeader = new ConfigRestHeader();
        ;
    }

    @NonNull
    @Override
    public ViewHolderSelectSite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderSelectSite(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recycler_select_site, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSelectSite holder, int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        ImageLoader.getInstance().displayImage(list.get(position).MainImageSrc, holder.img, options, null);
        holder.txt.get(0).setText(list.get(position).Title);
        holder.txt.get(1).setText(holder.txt.get(1).getText() + String.valueOf(list.get(position).Id));
        holder.txt.get(2).setText(holder.txt.get(2).getText() + list.get(position).Domain);
        holder.txt.get(3).setText(holder.txt.get(3).getText() + list.get(position).CreatedDate);
        if (list.get(position).ExpireDate != null)
            holder.txt.get(4).setText(holder.txt.get(4).getText() + list.get(position).ExpireDate);
        else
            holder.txt.get(4).setText(holder.txt.get(4).getText() + "-");
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ActMain.class).putExtra(SITE_ID, list.get(position).Id));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolderSelectSite extends RecyclerView.ViewHolder {

        @BindView(R.id.imgSiteImage)
        ImageView img;

        @BindView(R.id.rootRecyclerSelectSite)
        CardView root;

        @BindViews({R.id.txtSiteName,
                R.id.txtSiteCode,
                R.id.txtSiteDomain,
                R.id.txtSiteCreateDate,
                R.id.txtSiteExpireDate})
        List<TextView> txt;

        ViewHolderSelectSite(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            for (int i = 0; i < txt.size(); i++) {
                txt.get(i).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            }
        }
    }
}

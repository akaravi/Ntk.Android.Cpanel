package ntk.android.cpanel.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.cpanel.R;
import ntk.android.cpanel.utillity.FontManager;
import ntk.base.api.core.entity.CoreCpMainMenu;

public class AdBottomSheet extends RecyclerView.Adapter<AdBottomSheet.ViewHolderBottomSheet> {

    private Context context;
    private List<CoreCpMainMenu> list;

    public AdBottomSheet(Context context, List<CoreCpMainMenu> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderBottomSheet onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderBottomSheet(LayoutInflater.from(context).inflate(R.layout.row_bottoom_sheet, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderBottomSheet holder, int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        ImageLoader.getInstance().displayImage(list.get(position).Icon, holder.img, options, null);
        holder.txt.setText(list.get(position).Title);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderBottomSheet extends RecyclerView.ViewHolder {

        @BindView(R.id.imgRowBottomSheet)
        ImageView img;

        @BindView(R.id.txtRowBottomSheet)
        TextView txt;

        @BindView(R.id.rootRowBottomSheet)
        LinearLayout root;

        ViewHolderBottomSheet(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            txt.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}

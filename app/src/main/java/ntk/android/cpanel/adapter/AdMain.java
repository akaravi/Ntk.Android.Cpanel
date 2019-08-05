package ntk.android.cpanel.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import butterknife.ButterKnife;
import ntk.android.cpanel.R;
import ntk.android.cpanel.fragment.BottomSheetFragment;
import ntk.android.cpanel.utillity.FontManager;
import ntk.base.api.core.entity.CoreCpMainMenu;

public class AdMain extends RecyclerView.Adapter<AdMain.ViewHolderMain> {

    private Context context;
    private List<CoreCpMainMenu> list;

    public AdMain(Context context, List<CoreCpMainMenu> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolderMain onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderMain(LayoutInflater.from(context).inflate(R.layout.row_main, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMain holder, int position) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).build();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        ImageLoader.getInstance().displayImage(list.get(position).Icon, holder.img, options, null);
        holder.txt.setText(list.get(position).Title);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheetFragment(list.get(position).Children).show(((AppCompatActivity) context).getSupportFragmentManager(), new BottomSheetFragment().getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolderMain extends RecyclerView.ViewHolder {

        @BindView(R.id.imgRowMain)
        ImageView img;

        @BindView(R.id.txtRowMain)
        TextView txt;

        @BindView(R.id.rootRowMain)
        CardView root;

        ViewHolderMain(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            txt.setTypeface(FontManager.GetTypeface(context, FontManager.DastNevis));
        }
    }
}

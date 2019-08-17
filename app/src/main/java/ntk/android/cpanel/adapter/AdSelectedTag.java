package ntk.android.cpanel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.cpanel.R;
import ntk.android.cpanel.utillity.FontManager;
import ntk.base.api.news.entity.NewsTag;

public class AdSelectedTag extends RecyclerView.Adapter<AdSelectedTag.SelectedTagViewHolder> {

    private Context context;
    private List<NewsTag> list;

    public AdSelectedTag(Context context, List<NewsTag> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SelectedTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectedTagViewHolder(LayoutInflater.from(context).inflate(R.layout.row_selected_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedTagViewHolder holder, int position) {
        holder.txt.setText(list.get(position).Title);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(list.get(position));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SelectedTagViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtRowSelectedTag)
        TextView txt;

        @BindView(R.id.imgRowSelectedTag)
        ImageView img;

        SelectedTagViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            txt.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}

package ntk.android.cpanel.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.cpanel.R;
import ntk.android.cpanel.utillity.FontManager;
import ntk.base.api.news.entity.NewsTag;

public class AdTag extends RecyclerView.Adapter<AdTag.TagViewHolder> {

    private Context context;
    private List<NewsTag> list;
    private List<NewsTag> selectedList;
    private AdSelectedTag adapter;

    public AdTag(Context context, List<NewsTag> list, List<NewsTag> selectedList, AdSelectedTag adapter) {
        this.context = context;
        this.list = list;
        this.selectedList = selectedList;
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TagViewHolder(LayoutInflater.from(context).inflate(R.layout.row_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.txt.setText(list.get(position).Title);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedList.size() == 0) {
                    selectedList.add(list.get(position));
                    adapter.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < selectedList.size(); i++) {
                        if (list.get(position).Id.equals(selectedList.get(i).Id)) {
                            Toast.makeText(context, "این مورد قبلا اضافه شده", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    selectedList.add(list.get(position));
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtRowTag)
        TextView txt;

        @BindView(R.id.rootRowTag)
        RelativeLayout root;

        TagViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            txt.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}

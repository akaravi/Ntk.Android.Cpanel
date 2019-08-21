package ntk.android.cpanel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import ntk.android.cpanel.R;
import ntk.android.cpanel.utillity.FontManager;
import ntk.base.api.news.entity.NewsComment;

public class AdNewsComment extends RecyclerView.Adapter<AdNewsComment.NewsCommentViewHolder> {

    private Context context;
    private List<NewsComment> list;

    public AdNewsComment(Context context, List<NewsComment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NewsCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsCommentViewHolder(LayoutInflater.from(context).inflate(R.layout.row_news_comment, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NewsCommentViewHolder holder, int position) {
        holder.txt.get(0).setText(String.valueOf(list.get(position).Id));
        holder.txt.get(1).setText(list.get(position).Writer + " :");
        holder.txt.get(2).setText(list.get(position).Comment);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NewsCommentViewHolder extends RecyclerView.ViewHolder {

        @BindViews({R.id.NewsCommentId,
                R.id.newsCommentName,
                R.id.newsComment})
        List<TextView> txt;

        @BindView(R.id.newsCommentLine)
        View Line;

        NewsCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            for (int i = 0; i < txt.size(); i++) {
                txt.get(i).setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
            }
        }
    }
}

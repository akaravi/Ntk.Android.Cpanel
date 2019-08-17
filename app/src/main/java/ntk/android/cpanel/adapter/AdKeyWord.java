package ntk.android.cpanel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.cpanel.R;
import ntk.android.cpanel.utillity.FontManager;

public class AdKeyWord extends RecyclerView.Adapter<AdKeyWord.KeyViewHolder> {

    private Context context;
    private List<String> list;

    public AdKeyWord(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public KeyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new KeyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_selected_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull KeyViewHolder holder, int position) {
        holder.txt.setText(list.get(position));
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

    class KeyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtRowSelectedTag)
        TextView txt;

        @BindView(R.id.imgRowSelectedTag)
        ImageView img;

        KeyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            txt.setTypeface(FontManager.GetTypeface(context, FontManager.IranSans));
        }
    }
}

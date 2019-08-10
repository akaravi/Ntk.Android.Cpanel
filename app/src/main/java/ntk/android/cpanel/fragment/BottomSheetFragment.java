package ntk.android.cpanel.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ntk.android.cpanel.R;
import ntk.android.cpanel.adapter.AdBottomSheet;
import ntk.base.api.core.entity.CoreCpMainMenu;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    @BindView(R.id.recyclerBottomSheet)
    RecyclerView Rv;

    private List<CoreCpMainMenu> list;

    public BottomSheetFragment() {
    }

    @SuppressLint("ValidFragment")
    public BottomSheetFragment(List<CoreCpMainMenu> list) {
        this.list = list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);
        ButterKnife.bind(this, view);
        initialization();
        return view;
    }

    private void initialization() {
        Rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        Rv.setAdapter(new AdBottomSheet(getContext(), list));
    }
}

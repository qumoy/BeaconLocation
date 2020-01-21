package com.beacon.location.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beacon.location.Beans.BeaconInfo;
import com.beacon.location.R;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author Qumoy
 * Create Date 2020/1/18
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class BeaconLocationViewAdapter extends RecyclerView.Adapter<BeaconLocationViewAdapter.BeaconViewHolder> {



    private List<BeaconInfo> mBeaconList;
    public void setBeaconList(List<BeaconInfo> mBeaconList) {
        this.mBeaconList = mBeaconList;
    }
    public BeaconLocationViewAdapter(List<BeaconInfo> list) {
        this.mBeaconList = list;
    }

    @NonNull
    @Override
    public BeaconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beacon_location, null, false);
        return new BeaconViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BeaconViewHolder beaconViewHolder, int i) {
        beaconViewHolder.mTvName.setText(mBeaconList.get(i).getName());
        beaconViewHolder.mTvX.setText(String.valueOf(mBeaconList.get(i).getX()));
        beaconViewHolder.mTvY.setText(String.valueOf(mBeaconList.get(i).getY()));
        beaconViewHolder.mLayout.setOnClickListener(view -> {
            onItemClickListener.onItemClick(i);
        });
    }

    @Override
    public int getItemCount() {
        return mBeaconList.size();
    }

    class BeaconViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_beacon_location)
        LinearLayout mLayout;
        @BindView(R.id.tv_name)
        TextView mTvName;
        @BindView(R.id.tv_x)
        TextView mTvX;
        @BindView(R.id.tv_y)
        TextView mTvY;

        public BeaconViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }
}

package com.beacon.location.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beacon.location.Beans.Beacon;
import com.beacon.location.R;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author XXXXX
 * Create Date 2020/1/18
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class BeaconViewAdapter extends RecyclerView.Adapter<BeaconViewAdapter.BeaconViewHolder> {
    private List<Beacon> mBeaconList;

    public BeaconViewAdapter(List<Beacon> list) {
        this.mBeaconList = list;
    }

    @NonNull
    @Override
    public BeaconViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beacon, null, false);
        return new BeaconViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BeaconViewHolder beaconViewHolder, int i) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        beaconViewHolder.mItemDistance.setText("距离：" + decimalFormat.format(mBeaconList.get(i).get_dist())+"m");
        beaconViewHolder.mItemName.setText("名称：" + mBeaconList.get(i).getName());
        beaconViewHolder.mItemRssi.setText("Rssi：" + mBeaconList.get(i).get_rssi());
    }

    @Override
    public int getItemCount() {
        return mBeaconList.size();
    }

    class BeaconViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_name)
        TextView mItemName;
        @BindView(R.id.item_rssi)
        TextView mItemRssi;
        @BindView(R.id.item_distance)
        TextView mItemDistance;

        public BeaconViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

package com.example.sakhi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.ViewHolder> {

    private Context context;
    private List<HospitalModel> hospitalList;

    public HospitalAdapter(Context context, List<HospitalModel> hospitalList) {
        this.context = context;
        this.hospitalList = hospitalList;
    }

    // ▼▼▼ THIS IS THE MISSING PART CAUSING YOUR ERRORS ▼▼▼
    public void setFilteredList(List<HospitalModel> filteredList) {
        this.hospitalList = filteredList;
        notifyDataSetChanged();
    }
    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hospital_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HospitalModel hospital = hospitalList.get(position);

        holder.txtName.setText(hospital.getName());
        holder.txtDistance.setText(hospital.getDistance());

        if (hospital.isVerified()) {
            holder.imgVerified.setVisibility(View.VISIBLE);
        } else {
            holder.imgVerified.setVisibility(View.GONE);
        }

        // --- NEW CLICK LISTENER ---
        holder.btnView.setOnClickListener(v -> {
            // Create an Intent to open the Details Screen
            android.content.Intent intent = new android.content.Intent(context, HospitalDetailsActivity.class);

            // Pack the data to send
            intent.putExtra("NAME", hospital.getName());
            intent.putExtra("LOCATION", hospital.getDistance());
            intent.putExtra("DESC", hospital.getDescription());
            intent.putExtra("IMAGE", hospital.getImageResId());
            intent.putExtra("VERIFIED", hospital.isVerified());

            // Start the new screen
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtDistance;
        ImageView imgVerified;
        Button btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtHospitalName);
            txtDistance = itemView.findViewById(R.id.txtDistance);
            imgVerified = itemView.findViewById(R.id.icVerified);
            btnView = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}

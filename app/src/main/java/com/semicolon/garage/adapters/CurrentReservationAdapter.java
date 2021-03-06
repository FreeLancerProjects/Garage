package com.semicolon.garage.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.semicolon.garage.R;
import com.semicolon.garage.fragments.Fragment_Current_Reservation;
import com.semicolon.garage.models.RentModel;
import com.semicolon.garage.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CurrentReservationAdapter extends RecyclerView.Adapter<CurrentReservationAdapter.Holder>{
    private Context context;
    private List<RentModel> rentModelList;
    private Fragment fragment;
    public CurrentReservationAdapter(Context context, List<RentModel> rentModelList, Fragment fragment) {
        this.context = context;
        this.rentModelList = rentModelList;
        this.fragment = fragment;
    }

    @Override
    public CurrentReservationAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.current_reservation_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final CurrentReservationAdapter.Holder holder, int position) {
        RentModel rentModel = rentModelList.get(position);
        holder.BindData(rentModel);
        holder.ll_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RentModel rentModel = rentModelList.get(holder.getAdapterPosition());
                Fragment_Current_Reservation fragment_current_reservation = (Fragment_Current_Reservation) fragment;
                fragment_current_reservation.setItemForUpdate(rentModel);
            }
        });

        holder.ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RentModel rentModel = rentModelList.get(holder.getAdapterPosition());
                Fragment_Current_Reservation fragment_current_reservation = (Fragment_Current_Reservation) fragment;
                fragment_current_reservation.setItemForDelete(rentModel,holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return rentModelList.size();
    }

    class Holder extends RecyclerView.ViewHolder  {
        private ImageView image;
        private TextView tv_name,tv_price,tv_duration,tv_sd_ed,tv_vehicle_address,tv_total;
        private LinearLayout ll_delete,ll_update;
        public Holder(View itemView) {
            super(itemView);

            tv_duration = itemView.findViewById(R.id.tv_duration);
            image = itemView.findViewById(R.id.image);
            tv_sd_ed = itemView.findViewById(R.id.tv_sd_ed);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_vehicle_address = itemView.findViewById(R.id.tv_vehicle_address);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_total = itemView.findViewById(R.id.tv_total);
            ll_delete = itemView.findViewById(R.id.ll_delete);
            ll_update = itemView.findViewById(R.id.ll_update);



        }

        public void BindData(RentModel rentModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.IMAGE_URL+ rentModel.getMain_photo())).into(image);
            tv_price.setText(rentModel.getCost());
            tv_duration.setText(rentModel.getReservation_num_days()+" "+context.getString(R.string.day));
            tv_total.setText(rentModel.getReservation_cost()+" "+context.getString(R.string.sar));
            tv_vehicle_address.setText(rentModel.getAddress());
            tv_sd_ed.setText(context.getString(R.string.from)+" "+rentModel.getReservation_start_date()+" "+context.getString(R.string.to)+" "+rentModel.getReservation_end_date());
            if (rentModel.getCategory_id_fk().equals("1"))
            {
                tv_name.setText(rentModel.getTitle()+" "+rentModel.getCar_trademarks()+" "+rentModel.getCar_model());
                
                }else
                {
                    tv_name.setText(rentModel.getTitle()+" "+rentModel.getCar_trademarks()+"\n"+rentModel.getSize()+" "+context.getString(R.string.size));


                }

        }



    }
}

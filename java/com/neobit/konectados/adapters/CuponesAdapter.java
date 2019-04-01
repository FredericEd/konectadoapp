package com.neobit.konectados.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.neobit.konectados.R;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.recyclerview.widget.RecyclerView;

public class CuponesAdapter extends RecyclerView.Adapter<CuponesAdapter.ViewHolder> {

    JSONArray values;
    Context contexto;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textUsuario;
        TextView textFecha;

        public ViewHolder(View v) {
            super(v);
            textName = v.findViewById(R.id.textName);
            textUsuario = v.findViewById(R.id.textUser);
            textFecha = v.findViewById(R.id.textFecha);
        }
    }

    public CuponesAdapter(Context mContext, JSONArray values) {
        this.contexto = mContext;
        this.values = values;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_cupon, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            JSONObject temp = values.getJSONObject(position);
            holder.textName.setText(temp.getJSONObject("coupon").getJSONObject("product").getString("name"));
            holder.textUsuario.setText(temp.getJSONObject("user").getString("username"));
            holder.textFecha.setText(temp.getString("created_at").split(" ")[1]);
            holder.itemView.setTag(temp.toString());
        } catch(Exception e) {
            Log.e(contexto.getResources().getString(R.string.app_name), contexto.getResources().getString(R.string.error_tag), e);
        }
    }

    @Override
    public int getItemCount() {
        return values.length();
    }

    public void updateList (JSONArray items) {
        values = items;
        notifyDataSetChanged();
    }
}
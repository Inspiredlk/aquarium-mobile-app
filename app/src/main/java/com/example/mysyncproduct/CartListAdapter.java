package com.example.mysyncproduct;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Quoc Nguyen on 13-Dec-16.
 */

public class CartListAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Product> foodsList;

    public CartListAdapter(Context context, int layout, ArrayList<Product> foodsList) {
        this.context = context;
        this.layout = layout;
        this.foodsList = foodsList;
    }

    @Override
    public int getCount() {
        return foodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName, txtPrice,txtDes,txtQty,txtId;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtId = (TextView) row.findViewById(R.id.textViewId);
            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.txtDes = (TextView) row.findViewById(R.id.txtDes);
            holder.txtPrice = (TextView) row.findViewById(R.id.txtPrice);
            holder.imageView = (ImageView) row.findViewById(R.id.imgFood);
            holder.txtQty = (TextView) row.findViewById(R.id.txtQty);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        Product product = foodsList.get(position);

        holder.txtId.setText( String.valueOf( product.getId()));
        holder.txtName.setText( product.getTitle());
        holder.txtPrice.setText(String.valueOf( product.getPrice()));
        holder.txtDes.setText( product.getShortdesc());
        holder.txtQty.setText(String.valueOf( product.getQuantity()));

        byte[] foodImage = product.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage, 0, foodImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}

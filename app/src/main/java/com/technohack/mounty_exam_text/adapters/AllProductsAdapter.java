package com.technohack.mounty_exam_text.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.technohack.mounty_exam_text.R;
import com.technohack.mounty_exam_text.models.Products;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AllProductsAdapter extends RecyclerView.Adapter<AllProductsAdapter.MyViewHolder> {


    private Context context;
    private List<Products> productsList;

    public AllProductsAdapter(Context context, List<Products> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.single_product_item,parent,false);
       return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
      String rupee="Rs.";
      holder.productTitle.setText(productsList.get(position).getProductTitle());
      holder.productCostPrice.setText(String.valueOf(productsList.get(position).getCostPrice())+rupee);
      holder.setProductImage(productsList.get(position).getImageUrl());

      holder.cardView.setOnClickListener(v -> {

        Intent navigateIntent=new Intent();
        navigateIntent.putExtra("title",productsList.get(position).getProductTitle());
        navigateIntent.putExtra("desc",productsList.get(position).getProductDesc());
        navigateIntent.putExtra("cost_price",productsList.get(position).getCostPrice());
        navigateIntent.putExtra("selling_price",productsList.get(position).getSellingPrice());

      });

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productCostPrice;
        TextView productTitle;
        ImageView productImage;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productCostPrice=itemView.findViewById(R.id.single_pro_price);
            productTitle=itemView.findViewById(R.id.single_pro_titleId);
            productImage=itemView.findViewById(R.id.single_pro_image);
            cardView=itemView.findViewById(R.id.single_cardViewId);

        }
        //to set the image
        void setProductImage(String imageUrl){

            Glide.with(context).load(imageUrl+".jpg").into(productImage);

        }
    }
}

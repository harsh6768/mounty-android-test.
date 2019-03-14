package com.technohack.mounty_exam_text;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import butterknife.BindView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class ProductDetails extends AppCompatActivity {

    @BindView(R.id.pro_detail_cost_priceId)
    TextView costPrice;
    @BindView(R.id.pro_details_selling_priceId)
    TextView sellingPrice;
    @BindView(R.id.pro_details_titleId)
    TextView title;
    @BindView(R.id.pro_details_descId)
    TextView desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Intent intent=getIntent();
        String pro_title=intent.getExtras().getString("title");
        String pro_desc=intent.getExtras().getString("desc");
        double pro_cost_price=intent.getExtras().getDouble("cost_price");
        double pro_selling_price=intent.getExtras().getDouble("selling_price");

        title.setText(pro_title);
        desc.setText(pro_desc);
        costPrice.setText((int) pro_cost_price);
        sellingPrice.setText((int) pro_selling_price);


    }
}

package com.technohack.mounty_exam_text;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.opencensus.stats.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.technohack.mounty_exam_text.adapters.UploadImageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private static final int UPLOAG_IMAGE_REQUEST_CODE =123 ;

    //These view shouldn't be private or static other wise it will give the compile time error
    @BindView(R.id.main_product_titleId)
    MaterialEditText productTitle;
    @BindView(R.id.main_product_descriptionId)
    MaterialEditText productDescription;
    @BindView(R.id.main_product_cost_priceId)
    MaterialEditText productCostPrice;
    @BindView(R.id.main_product_selling_priceId)
    MaterialEditText productSellingPrice;
    @BindView(R.id.main_recyclerViewId)
    RecyclerView recyclerView;


    private List<String> fileListName;
    private List<String> fileListDone;
    private List<String> imageUrlList;

    UploadImageAdapter uploadImageAdapter;

    //for fireBase Storage
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    //for progressBar
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for getting the full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.main_toolbarId);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Add Products");
        toolbar.setTitleTextColor(getResources().getColor(R.color.whiteColor));

        //initialize butter knife
        ButterKnife.bind(this);

        //fireBase
        FirebaseApp.initializeApp(this);
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //recyclerView is used to upload the images of product in FireBaseStorage
        recyclerView=findViewById(R.id.main_recyclerViewId);

        fileListName=new ArrayList<>();
        fileListDone=new ArrayList<>();
        imageUrlList=new ArrayList<>();


        uploadImageAdapter=new UploadImageAdapter(fileListName,fileListDone,MainActivity.this);

        //this method will avoid the conflict between the NestedScrollView and the recyclerView
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.hasFixedSize();
        recyclerView.setAdapter(uploadImageAdapter);

    }
    @OnClick(R.id.main_select_image_btnId)
    void onSelectImage(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //check if permission granted or not
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Toast.makeText(AddMenu.this, "Permission Denied!!!", Toast.LENGTH_LONG).show();
                //Requesting for permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {
                //if permission granted then we need to set the image
                uploadProductImage();
            }
        } else {

            //android version is less than 6.0
            uploadProductImage();

        }
    }

    //to open the Gallery
    private void uploadProductImage() {

        Intent intent=new Intent();
        intent.setType("image/*");
        //this method will allow to select the multiple item at a time
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,UPLOAG_IMAGE_REQUEST_CODE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //check if request code is correct and result Ok then get The image From the resource
        if(requestCode==UPLOAG_IMAGE_REQUEST_CODE && resultCode==RESULT_OK){

            //when we select the multiple file
            //to avoid the nullPointerException I have used this method
            if(Objects.requireNonNull(data).getClipData()!=null){

                int totalItem= Objects.requireNonNull(data.getClipData()).getItemCount();

                for(int i=0;i<totalItem;i++){

                    Uri uri=data.getClipData().getItemAt(i).getUri();

                    String fileName=getFileName(uri);

                    fileListName.add(fileName);

                    //at the starting no file uploaded
                    fileListDone.add("uploading");

                    uploadImageAdapter.notifyDataSetChanged();

                    final String proTitle= Objects.requireNonNull(productTitle.getText()).toString().trim();

                    //setting the path where we want to upload the images
                    StorageReference fileToUpload=storageReference.child("Images/"+proTitle+"/").child(fileName);

                    final int finalI = i;

                    //to upload the file
                    fileToUpload.putFile(uri).addOnSuccessListener(taskSnapshot -> {


                        fileToUpload.getDownloadUrl().addOnSuccessListener(uri12 -> {

                            String downloadImageUrl= uri12.toString();
                            imageUrlList.add(downloadImageUrl);

                            //after file uploaded successfully we need to remove uploading string and replace with the done string so that
                            // we can change the progress image to checked image to ensure the user that file is successfully uploaded
                            fileListDone.remove(finalI);
                            fileListDone.add(finalI,"done");
                            uploadImageAdapter.notifyDataSetChanged();
                        });

                    });

                }

            }else if(data.getData()!=null){
                //when we select 1 file only
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //get the image Name
    public String getFileName(Uri uri){

        String result=null;

        if(uri.getScheme().equals("content")){

            Cursor cursor=getContentResolver().query(uri,null,null,null,null);

            try{
                if(cursor!=null && cursor.moveToFirst()){
                    result=cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }

        }
        else if(result==null){

            result=uri.getPath();
            int cut=result.lastIndexOf('/');

            if(cut!=1){
                result=result.substring(cut+1);
            }
        }
        return result;

    }

    //to save the data on fireBase fireStore
    @OnClick(R.id.main_save_data_btnId)
    void onSaveButtonClick(){

        final String proTitle= Objects.requireNonNull(productTitle.getText()).toString().trim();
        final String proDesc= Objects.requireNonNull(productDescription.getText()).toString().trim();
        final String proCostPrice=Objects.requireNonNull(productCostPrice.getText()).toString().trim();
        final String proSellingPrice=Objects.requireNonNull(productSellingPrice.getText()).toString().trim();

        if(proTitle.isEmpty() || proTitle.length()>25){
            productTitle.setError("enter title less than 25 character");
        }else if(proDesc.isEmpty() || productDescription.length()>200){
            productDescription.setError("enter description less than 200 character");
        }else if(proCostPrice.isEmpty()){
            productCostPrice.setError("enter valid cost price");
        }else if(proSellingPrice.isEmpty()) {
            productSellingPrice.setError("enter valid selling price");
        }else if(fileListName.size()==0){
            Toast.makeText(this, "Please Select the Image", Toast.LENGTH_SHORT).show();
        }else{
            //displaying progressbar
            progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Saving Data ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            //if All the fields are filled then save the data into the FireStore
           uploadProductData(proTitle,proDesc,proCostPrice,proSellingPrice);

        }

    }

    private void uploadProductData(String proTitle, String proDesc, String proCostPrice, String proSellingPrice) {

        String downloadImageUrl=imageUrlList.get(0);

        HashMap<String,Object> productHashMap=new HashMap<>();
        productHashMap.put("image_url",downloadImageUrl);
        productHashMap.put("pro_title",proTitle);
        productHashMap.put("pro_desc",proDesc);
        productHashMap.put("pro_cost_price",proCostPrice);
        productHashMap.put("pro_selling_price",proSellingPrice);
        productHashMap.put("time_stamp", FieldValue.serverTimestamp());

        //to save the product in FireBaseFireStore

        firebaseFirestore.collection("Products").add(productHashMap).addOnCompleteListener(task -> {

            if(task.isSuccessful()){

                 progressDialog.dismiss();
                 //to clear the arrayList
                 fileListName.clear();
                 Intent navigateIntent=new Intent(MainActivity.this,AllProducts.class);
                 startActivity(navigateIntent);
                 //Toast.makeText(MainActivity.this, "Product Saved Successfully!!!", Toast.LENGTH_SHORT).show();
            }else{
                 progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Failed To Save Product!!!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //for option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.add_product_menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.main_menu_itemId:
                startActivity(new Intent(MainActivity.this,AllProducts.class));
                return true;
            default:
                return true;
        }
    }

}

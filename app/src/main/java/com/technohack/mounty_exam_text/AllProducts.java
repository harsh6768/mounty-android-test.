package com.technohack.mounty_exam_text;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.technohack.mounty_exam_text.adapters.AllProductsAdapter;
import com.technohack.mounty_exam_text.models.Products;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class AllProducts extends AppCompatActivity {

    @BindView(R.id.all_products_recyclerViewId)
    RecyclerView recyclerView;

    AllProductsAdapter allProductsAdapter;
    List<Products>  productsList;

    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    //define the DataSnapshot to retrieve the last post so that we can retrieve the another three post from the firebase to implement the pagination in out blogpost
    private DocumentSnapshot lastVisible;

    //to check where we have reached last product or not
    private Boolean setNewPostAtFirstPosition=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //for getting the full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_all_products);
         //initializing butter knife
        ButterKnife.bind(this);

        FirebaseApp.initializeApp(this);
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();


        productsList=new ArrayList<>();
        allProductsAdapter=new AllProductsAdapter(this,productsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(allProductsAdapter);
        //to notify the adapter if there is any change occur
        allProductsAdapter.notifyDataSetChanged();

        fetchDataFromFireBase();
    }

    private void fetchDataFromFireBase() {

        firebaseFirestore.collection("Products").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){

                           for(QueryDocumentSnapshot doc:task.getResult()){
                              Map<String,Object> productHashMap=doc.getData();

                              String imageUrl= (String) productHashMap.get("image_url");
                              String proTitle=(String) productHashMap.get("pro_title");
                              String proDesc=(String)productHashMap.get("pro_desc");
                              double proCostPrice=Double.parseDouble((String) Objects.requireNonNull(productHashMap.get("pro_cost_price")));
                              double proSellingPrice=Double.parseDouble((String) Objects.requireNonNull(productHashMap.get("pro_selling_price")));

                              Products products=new Products(imageUrl,proTitle,proDesc,proCostPrice,proSellingPrice);
                              productsList.add(products);
                              allProductsAdapter.notifyDataSetChanged();

                           }
                        }
                    }
                });


    }

    /*
    private void fetchDataFromFireBase() {

        //check if RecylerView is reached at bottom or not
        //if reached bottom then we need to fireUp the next 3 Posts
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //checking the reachedLast value
                boolean reachedLast=!recyclerView.canScrollVertically(1);

                //if recyclerView reached last then we need to load the next posts
                if(reachedLast){
                    String title=lastVisible.getString("pro_title");
                    Toast.makeText(AllProducts.this, "ReachedLast:"+title, Toast.LENGTH_SHORT).show();

                    //to load the another post in the recyclerView
                    loadMorePost();

                }
            }
        });

        //for Pagination we need to use the fireBase fireStore query to arrange the post by using the date
        //to load the 3 product at a time we need to invoke the limit method
        Query firstQuery=fireBaseFireStore.collection("Products")
                .orderBy("time_stamp", Query.Direction.DESCENDING)
                .limit(3);

        //we want real time data from the database so we need to invoke the addSnapshotListener
        //getActivity  is used to prevent for crashing
        firstQuery.addSnapshotListener((documentSnapshots, e) -> {


            //if new product posted then lastVisible don't change
            if(setNewPostAtFirstPosition)
            {
                //to get the last product to implement the pagination
                lastVisible= Objects.requireNonNull(documentSnapshots).getDocuments().get(documentSnapshots.size() -1);

            }

            for(DocumentChange doc: Objects.requireNonNull(documentSnapshots).getDocumentChanges()){

                if(doc.getType()==DocumentChange.Type.ADDED){

                    //fetching the products
                    Products products=doc.getDocument().toObject(Products.class);

                    //to set the New Product at the Fist Position so that Pagination wil work in a right way
                    if(setNewPostAtFirstPosition){

                        productsList.add(products);

                    }else{

                        //so if new blog posted via another device then if will set the new Post at the First Position
                        productsList.add(0,products);
                    }

                    allProductsAdapter.notifyDataSetChanged();

                }
            }

            //Important part for the Pagination
            setNewPostAtFirstPosition=false;

        });

    }
    public void loadMorePost(){

        Query nextQuery=fireBaseFireStore.collection("Products")
                .orderBy("time_stamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)       //to start the next post down after last post
                .limit(3);

        //we want real time data from the database so we need to invoke the addSnapshotListener
        nextQuery.addSnapshotListener((documentSnapshots, e) -> {

            //check condition so that when there is no post ,it shouldn't be execute the query for loading the post
            //if will help to don't let our app crash
            if(!Objects.requireNonNull(documentSnapshots).isEmpty()){

                //we need to set the value of the last visible so that we can fireUp rest of the products into the recyclerView
                lastVisible =documentSnapshots.getDocuments().get(documentSnapshots.size() -1);

                for(DocumentChange  doc: documentSnapshots.getDocumentChanges()){

                    if(doc.getType()==DocumentChange.Type.ADDED){

                        Products products=doc.getDocument().toObject(Products.class);

                        productsList.add(products);

                        allProductsAdapter.notifyDataSetChanged();

                    }
                }
            }

        });

    }
    */
}

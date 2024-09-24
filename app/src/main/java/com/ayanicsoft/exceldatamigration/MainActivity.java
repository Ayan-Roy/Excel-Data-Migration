package com.ayanicsoft.exceldatamigration;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ayanicsoft.exceldatamigration.retrofit.LiveDataUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    int i = 1;
    int j = 1;
    private List<ProductBean> beanList = new ArrayList<>();
    private List<String> docIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*        ExcelReader excelReader = new ExcelReader(this);
        excelReader.readExcelFile();*/

    }


    private void getAllFirebaseProducts() {

        database.collection("Products_List")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot snapshot : snapshots) {
                            ProductBean bean = snapshot.toObject(ProductBean.class);
                            String docId = snapshot.getId();
                            String location = bean.getLocation()+"".trim();
                            String productId = bean.getProductId().trim();

                           if (location.matches("Out of Stock")) {
                                Log.e(TAG, i+" >> Stock Out ( " + bean.getIsInStock() + " )::  Product Id-> " + productId + "   Location-> " + location);
                                bean.setLocation(bean.getLocation2());
                                bean.setLocation2(bean.getLocation3());
                                Log.d(TAG, "loc1-> " + bean.getLocation() + "  loc2-> " + bean.getLocation2() + "  loc3-> " + bean.getLocation3());
                                bean.setIsInStock(0);

                                beanList.add(bean);
                                docIdList.add(docId);
                                i++;
                            } else {
                                //bean.setIsInStock(1);
                                //Log.e(TAG, j+" >> In Stock ( " + bean.getIsInStock() + " )::  Product Id-> " + productId + "   Location-> " + location);
                                j++;
                            }

                            //updateProductBean(bean, productId);

                        }



                        System.out.println("Wrong Data Got "+beanList.size());

                        int itr = 0;
                        for(ProductBean bean : beanList){
                            updateProductBean(bean, docIdList.get(itr));
                            itr++;
                        }
                    }
                });

    }


    int l = 1;

    private void updateProductBean(ProductBean bean, String documentId) {

        database.collection("Products_List").document(documentId)
                .set(bean)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "<<<<<<<<<<<  UPDATED  >>>>>>>: " + l);
                        l++;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onFailure: " + e.getMessage());

                    }
                });

    }



    private void deleteProductBean(ProductBean bean, String documentId) {

        database.collection("Products_List").document(documentId)
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e(TAG, "Deleted: "+j +"  Doc Id => " +documentId);
                        j++;
                    }
                });

    }



    private void insertIntoFirebaseFireStore(String productId, String location) {

        //      Set data to a Hashmap
        HashMap<String, Object> product = new HashMap<>();
        product.put("productId", productId);
        product.put("location", location);
        product.put("insertDate", getCurrentDateTime());
        product.put("insertBy", "Brat");

        Log.e(TAG, "insertIntoFirebaseFireStore: ");
        database.collection("Products_List_Test")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e(TAG, "Inserted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Insert Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public String getCurrentDateTime() {

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(currentDate);

        strDate = strDate.replaceAll("-", "");
        strDate = strDate.replaceAll(" ", "");
        strDate = strDate.replaceAll(":", "");
        return strDate;
    }
}
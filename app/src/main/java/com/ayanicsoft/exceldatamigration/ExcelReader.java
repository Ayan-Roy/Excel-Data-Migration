package com.ayanicsoft.exceldatamigration;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ayanicsoft.exceldatamigration.retrofit.LiveDataUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import jxl.*;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {


    private FirebaseFirestore database;
    List<ProductBean> productBeanList = new ArrayList<>();
    List<String> docIdList = new ArrayList<>();
    List<String> excelDataMap = new ArrayList<>();
    Context context;

    public ExcelReader(Context context) {
        this.context = context;
    }

    public void readExcelFile() {

        database = FirebaseFirestore.getInstance();

        String url = "https://github.com/Ayan-Roy/Excel-Data-Migration/raw/master/live_data_final.xls";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Log.e(TAG, "onFailure: ");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {

                try {
                    FileInputStream fis = new FileInputStream(file);
                    Workbook workbook = Workbook.getWorkbook(fis);

                    // Access the first sheet
                    Sheet sheet = workbook.getSheet(0);


                    int i = 1;
                    // Iterate over rows and columns
                    for (int row = 1; row < sheet.getRows(); row++) {
                        Cell cell1 = sheet.getCell(0, row);
                        Cell cell2 = sheet.getCell(1, row);
                        Log.d(TAG, "Excel Read: " + cell1.getContents() + " -- " + cell2.getContents());
                        excelDataMap.add(cell2.getContents());
                    }

                    Log.e(TAG, "Total Products Found ->> " + excelDataMap.size());

                    getAllFirebaseProducts();
                    workbook.close();

                } catch (BiffException | IOException e) {
                    e.printStackTrace();  // Handle exceptions appropriately
                }
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
                        Toast.makeText(context, "Insert Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void getAllFirebaseProducts() {

        database.collection("Products_List")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int i = 1;
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshots) {

                            ProductBean bean = snapshot.toObject(ProductBean.class);
                            if (excelDataMap.contains(bean.getProductId())) {
                                Log.e(TAG, "Match Found " + i + "   ProductId-> " + bean.getProductId());
                                bean.setIsInStock(1);
                                i++;

                                updateProductBean(bean, snapshot.getId());
                            }
                        }
                    }
                });

    }

    int isUpdated = 1;


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

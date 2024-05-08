package com.ayanicsoft.exceldatamigration;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import jxl.*;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ExcelReader {


    private FirebaseFirestore database;
    Context context;

    public ExcelReader(Context context){
        this.context = context;
    }
    public void readExcelFile() {

        database = FirebaseFirestore.getInstance();

        String url = "https://github.com/Ayan-Roy/Excel-Data-Migration/raw/master/products_list_demo.xls";
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
                    for (int row = 0; row < sheet.getRows(); row++) {
                        Cell cell1 = sheet.getCell(0, row);
                        Cell cell2 = sheet.getCell(1, row);
                        Log.e(TAG, "onSuccess: " + cell1.getContents() + " -- " + cell2.getContents());

                        insertIntoFirebaseFireStore(cell2.getContents(), cell1.getContents());
                    }

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
        database.collection("Products_List")
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
                        Toast.makeText(context, "Insert Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

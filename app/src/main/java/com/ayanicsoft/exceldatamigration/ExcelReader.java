package com.ayanicsoft.exceldatamigration;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ayanicsoft.c_tscan.activity.ActivityAddNewProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
import java.util.Date;
import java.util.HashMap;

public class ExcelReader {


    private FirebaseFirestore database;
    public void readExcelFile(Context context) {

        database = FirebaseFirestore.getInstance();

        String url = "https://github.com/Ayan-Roy/KIT_AyanRoy/raw/master/products_list.xls";
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
                        Log.e(TAG, "onSuccess: "+cell1.getContents() +" -- "+cell2.getContents() );

                        if(cell1.getContents().equals("") || cell2.getContents().equals("")){

                        }else{
                            insertIntoFirebaseFireStore(cell2.getContents(), cell1.getContents());
                            Log.e(TAG, "Inserted"+ i++ );
                        }

/*                        for (int col = 0; col < sheet.getColumns(); col++) {
                            Cell cell = sheet.getCell(col, row);
                            String cellContent = cell.getContents();  // Get cell content
                            Log.e(TAG, "readExcelFile: " + cellContent + "\t");
                        }*/
                    }

                    workbook.close();
                } catch (BiffException | IOException e) {
                    e.printStackTrace();  // Handle exceptions appropriately
                }
            }
        });


        //insertIntoFirebaseFireStore("sgfdhs", "fhdjf");
    }


    private void insertIntoFirebaseFireStore(String productId, String location) {

        //      Set data to a Hashmap
        HashMap<String, Object> product = new HashMap<>();
        product.put("productId", productId);
        product.put("location", location);
        product.put("insertDate", getCurrentDateTime());
        product.put("insertBy", "Brat");

        Log.e(TAG, "insertIntoFirebaseFireStore: " );
        database.collection("Products_List")
                .add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.e(TAG, "onSuccess: Inserted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // If error, display a Toast error message
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

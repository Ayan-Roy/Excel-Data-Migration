package com.ayanicsoft.exceldatamigration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExcelReader excelReader = new ExcelReader(this);
        //excelReader.readExcelFile();
    }
}
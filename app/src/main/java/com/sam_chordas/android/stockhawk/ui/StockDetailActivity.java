package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

/**
 * Created by Brandon on 3/24/2016.
 */
public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    String symbolToShowGraphFor;

    private static final int LOADER = 0;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        if(savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

        }

        if(getIntent() != null) {
            symbolToShowGraphFor = getIntent().getStringExtra("stock_item");
        }

        getLoaderManager().initLoader(LOADER, null, this);


    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
    }


    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.CREATED, QuoteColumns.BIDPRICE},
                QuoteColumns.SYMBOL + " = ?",
                new String[]{symbolToShowGraphFor},
                QuoteColumns.SYMBOL + " asc," + QuoteColumns.CREATED + " asc");
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        LineChart chart =(LineChart) findViewById(R.id.linechart);
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        int count = 0;
        while(data.moveToNext()) {
            xVals.add(data.getString(0));
            yVals.add(new Entry(data.getFloat(1), count));
            count++;
        }
/*
        for(int i = 0; i < 100; i++) {
            xVals.add((i) + "");
        }
        for(int i = 0; i < 100; i++) {
            float val =(float)(Math.random() * 1) + 3;
            yVals.add(new Entry(val, i));
        }*/

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        set1.enableDashedLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setDrawCircleHole(true);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);


        LineData dataPrep = new LineData(xVals, set1);
        chart.setData(dataPrep);
        chart.setDescription("My Chart");
        chart.invalidate();

    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {

    }
}
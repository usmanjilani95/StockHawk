package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = "WidgetDataProvider";

    Context mContext = null;
    private Cursor data = null;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;

    }

    @Override
    public void onCreate() {
    }


    public void onDataSetChanged() {

        initData();

    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
        }
    }

    @Override
    public int getCount() {
        if (data==null)
            return 0;
        else
            return data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION ||data == null || !data.moveToPosition(position)) {
            return null;
        }

        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_collection_item);

        view.setTextViewText(R.id.stock_symbol, data.getString(data.getColumnIndex
                ("symbol")));
        String bidPrice = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
        String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
        view.setTextViewText(R.id.bid_price, bidPrice);
        if (Utils.showPercent) {
            view.setTextViewText(R.id.change, data.getString(data.getColumnIndex("percent_change")));
        } else {
            view.setTextViewText(R.id.change, data.getString(data.getColumnIndex("change")));
        }
        if (data.getInt(data.getColumnIndex("is_up")) == 0) {
            view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        } else {
            view.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        }
        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra("symbol", symbol);
        view.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        if (data != null) {
            data.close();
        }

        final long token = Binder.clearCallingIdentity();
        data = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        Binder.restoreCallingIdentity(token);
    }


}

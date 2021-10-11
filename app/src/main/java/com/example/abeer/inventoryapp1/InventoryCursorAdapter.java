package com.example.abeer.inventoryapp1;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.abeer.inventoryapp1.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.productName);
        TextView priceTextView = (TextView) view.findViewById(R.id.productPrice);
        TextView quantityTextView = (TextView) view.findViewById(R.id.productQuantity);
        Button saleButton = (Button) view.findViewById(R.id.saleButton);

        final int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.PRODUCT_NAME_COLUMN);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.PRICE_COLUMN);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.QUANTITY_COLUMN);

        final String productID = cursor.getString(idColumnIndex);
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogActivity Activity = (CatalogActivity) context;
                Activity.decreaseQuantity(Integer.valueOf(productID), Integer.valueOf(productQuantity));
            }
        });

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);
    }
}

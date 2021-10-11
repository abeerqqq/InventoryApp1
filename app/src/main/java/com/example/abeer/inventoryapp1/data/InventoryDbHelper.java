package com.example.abeer.inventoryapp1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.abeer.inventoryapp1.data.InventoryContract.InventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.PRODUCT_NAME_COLUMN + " TEXT NOT NULL, "
                + InventoryEntry.PRICE_COLUMN + " TEXT NOT NULL, "
                + InventoryEntry.QUANTITY_COLUMN + " INTEGER NOT NULL, "
                + InventoryEntry.SUPPLIER_NAME_COLUMN + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.SUPPLIER_PHONE_NUMBER_COLUMN + " TEXT );";
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

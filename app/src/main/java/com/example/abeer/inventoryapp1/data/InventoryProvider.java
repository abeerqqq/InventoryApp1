package com.example.abeer.inventoryapp1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.abeer.inventoryapp1.data.InventoryContract.InventoryEntry;

public class InventoryProvider extends ContentProvider {
    public static final String LOG_TAG = InventoryEntry.class.getSimpleName();
    private InventoryDbHelper mDbHelper;
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper((getContext()));
        return true;
    }

    @Nullable
    @Override

    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion Not supported for" + uri);
        }

    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        if (values.containsKey(InventoryEntry.PRODUCT_NAME_COLUMN)) {
            String name = values.getAsString(InventoryEntry.PRODUCT_NAME_COLUMN);
            if (name == null) {
                throw new IllegalArgumentException("Product name is required");
            }
        }
        if (values.containsKey(InventoryEntry.PRICE_COLUMN)) {
            Integer price = values.getAsInteger(InventoryEntry.PRICE_COLUMN);
            if (price != null && price < 0) {
                throw new
                        IllegalArgumentException("Product price required to be valid");
            }
        }

        if (values.containsKey(InventoryEntry.QUANTITY_COLUMN)) {
            Integer quantity = values.getAsInteger(InventoryEntry.QUANTITY_COLUMN);
            if (quantity != null && quantity < 0) {
                throw new
                        IllegalArgumentException("Product quantity required to be valid");
            }
        }
        if (values.containsKey(InventoryEntry.SUPPLIER_NAME_COLUMN)) {
            Integer supplierName = values.getAsInteger(InventoryEntry.SUPPLIER_NAME_COLUMN);
            if (InventoryEntry.isValidSupplierName(supplierName) == false) {
                throw new IllegalArgumentException("Supplier name required to be valid");
            }
        }

        if (values.containsKey(InventoryEntry.SUPPLIER_PHONE_NUMBER_COLUMN)) {
            Integer supplierNumber = values.getAsInteger(InventoryEntry.SUPPLIER_PHONE_NUMBER_COLUMN);
            if (supplierNumber != null && supplierNumber < 0) {
                throw new
                        IllegalArgumentException("Supplier number required to be valid");
            }
        }

        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InventoryEntry.PRODUCT_NAME_COLUMN)) {
            String name = values.getAsString(InventoryEntry.PRODUCT_NAME_COLUMN);
            if (name == null) {
                throw new IllegalArgumentException("Product name is required");
            }
        }
        if (values.containsKey(InventoryEntry.PRICE_COLUMN)) {
            Integer price = values.getAsInteger(InventoryEntry.PRICE_COLUMN);
            if (price != null && price < 0) {
                throw new
                        IllegalArgumentException("Product price required to be valid");
            }
        }

        if (values.containsKey(InventoryEntry.QUANTITY_COLUMN)) {
            Integer quantity = values.getAsInteger(InventoryEntry.QUANTITY_COLUMN);
            if (quantity != null && quantity < 0) {
                throw new
                        IllegalArgumentException("Product quantity required to be valid");
            }
        }
        if (values.containsKey(InventoryEntry.SUPPLIER_NAME_COLUMN)) {
            Integer supplierName = values.getAsInteger(InventoryEntry.SUPPLIER_NAME_COLUMN);
            if (supplierName == null || !InventoryEntry.isValidSupplierName(supplierName)) {
                throw new IllegalArgumentException("Supplier name required to be valid");
            }
        }

        if (values.containsKey(InventoryEntry.SUPPLIER_PHONE_NUMBER_COLUMN)) {
            Integer supplierNumber = values.getAsInteger(InventoryEntry.SUPPLIER_PHONE_NUMBER_COLUMN);
            if (supplierNumber != null && supplierNumber < 0) {
                throw new
                        IllegalArgumentException("Supplier number required to be valid");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}

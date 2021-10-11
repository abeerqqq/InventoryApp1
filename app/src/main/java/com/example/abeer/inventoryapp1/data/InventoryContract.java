package com.example.abeer.inventoryapp1.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {
    // URI data
    public static final String CONTENT_AUTHORITY = "com.example.abeer.inventoryapp1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    private InventoryContract() {
    }

    public static final class InventoryEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        public final static String TABLE_NAME = "products";
        public final static String _ID = BaseColumns._ID;
        public final static String PRODUCT_NAME_COLUMN = "productName";
        public final static String PRICE_COLUMN = "price";
        public final static String QUANTITY_COLUMN = "quantity";
        public final static String SUPPLIER_NAME_COLUMN = "supplierName";
        public static final String SUPPLIER_PHONE_NUMBER_COLUMN = "supplierNumber";

        //Possible values for the SUPPLIER_NAME.
        public static final int SUPPLIER_UNKNOWN = 0;
        public static final int SUPPLIER_JARIR = 1;
        public static final int SUPPLIER_JAMLOON = 2;
        public static final int SUPPLIER_ELMAAREFA = 3;

        public static boolean isValidSupplierName(int supplierName) {
            if (supplierName == SUPPLIER_UNKNOWN || supplierName == SUPPLIER_JARIR || supplierName == SUPPLIER_JAMLOON || supplierName == SUPPLIER_ELMAAREFA) {
                return true;
            }
            return false;
        }
    }
}
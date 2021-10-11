package com.example.abeer.inventoryapp1;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abeer.inventoryapp1.data.InventoryContract.InventoryEntry;

public class ProductDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;
    private TextView productNameTextView;
    private TextView priceTextView;
    private TextView quantityTextView;
    private TextView supplierNameTextView;
    private TextView supplierNumberTextView;
    private Button deleteButton;
    private Button editeButton;
    private Button increaseButton;
    private Button decreaseButton;
    private Button callSupplierButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        productNameTextView = (TextView) findViewById(R.id.productNameDetail);
        priceTextView = (TextView) findViewById(R.id.productPriceDetail);
        quantityTextView = (TextView) findViewById(R.id.productQuantityDetail);
        supplierNameTextView = (TextView) findViewById(R.id.supplierNameDetail);
        supplierNumberTextView = (TextView) findViewById(R.id.supplierNumberDetail);

        deleteButton = (Button) findViewById(R.id.deleteButtonDeatil);
        editeButton = (Button) findViewById(R.id.editeButtonDeatil);
        increaseButton = (Button) findViewById(R.id.increaseDetailButton);
        decreaseButton = (Button) findViewById(R.id.decreaseDetailButton);
        callSupplierButton = (Button) findViewById(R.id.callSupplierButton);
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.PRODUCT_NAME_COLUMN,
                InventoryEntry.PRICE_COLUMN,
                InventoryEntry.QUANTITY_COLUMN,
                InventoryEntry.SUPPLIER_NAME_COLUMN,
                InventoryEntry.SUPPLIER_PHONE_NUMBER_COLUMN
        };

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {

            final int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.PRODUCT_NAME_COLUMN);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.PRICE_COLUMN);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.QUANTITY_COLUMN);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.SUPPLIER_NAME_COLUMN);
            int supplierNumberColumnIndex = cursor.getColumnIndex(InventoryEntry.SUPPLIER_PHONE_NUMBER_COLUMN);

            final String productID = cursor.getString(idColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            final int quantity = cursor.getInt(quantityColumnIndex);
            final int supplierName = cursor.getInt(supplierNameColumnIndex);
            final String supplierNumber = cursor.getString(supplierNumberColumnIndex);

            productNameTextView.setText(name);
            priceTextView.setText(Integer.toString(price));
            quantityTextView.setText(Integer.toString(quantity));
            supplierNumberTextView.setText(supplierNumber);

            switch (supplierName) {
                case InventoryEntry.SUPPLIER_JARIR:
                    supplierNameTextView.setText(getText(R.string.jarir));
                    break;
                case InventoryEntry.SUPPLIER_ELMAAREFA:
                    supplierNameTextView.setText(getText(R.string.elmarefa));
                    break;
                case InventoryEntry.SUPPLIER_JAMLOON:
                    supplierNameTextView.setText(getText(R.string.jamloon));
                    break;
                default:
                    supplierNameTextView.setText(getText(R.string.unknown));
                    break;
            }
            decreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decreaseQuantity(quantity);
                }
            });
            increaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseQuantity(quantity);
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

            editeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductDetails.this, InsertActivity.class);
                    Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, Long.parseLong(productID));
                    intent.setData(currentProductUri);
                    startActivity(intent);
                }
            });

            callSupplierButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", supplierNumber, null));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void increaseQuantity(int quantity) {
        quantity = quantity + 1;
        if (quantity >= 0) {
            updateProductQuantity(quantity);
        } else {
            return;
        }
    }

    private void updateProductQuantity(int quantity) {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.QUANTITY_COLUMN, quantity);
        if (mCurrentProductUri == null) {
            return;
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Couldn't update the quantity", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "The quantity updated succsefully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void decreaseQuantity(int quantity) {
        quantity = quantity - 1;
        if (quantity >= 0) {
            updateProductQuantity(quantity);
        } else {
            Toast.makeText(this, "You can't decrease the quantity under 0", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}

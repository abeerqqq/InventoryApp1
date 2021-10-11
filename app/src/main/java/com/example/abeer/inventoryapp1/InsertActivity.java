package com.example.abeer.inventoryapp1;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.abeer.inventoryapp1.data.InventoryContract.InventoryEntry;

public class InsertActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;
    private EditText productNameEditText;
    private EditText priceEditText;
    private EditText quantityEditText;
    private Spinner supplierNameSpinner;
    private EditText supplierNumberEditText;


    private int supplierName = InventoryEntry.SUPPLIER_UNKNOWN;
    private boolean productHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        productNameEditText = (EditText) findViewById(R.id.product_name);
        priceEditText = (EditText) findViewById(R.id.price);
        quantityEditText = (EditText) findViewById(R.id.quantity);
        supplierNameSpinner = (Spinner) findViewById(R.id.spinner_supplierName);
        setupSpinner();
        supplierNumberEditText = (EditText) findViewById(R.id.supplier_phone_number);

        productNameEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        quantityEditText.setOnTouchListener(mTouchListener);
        supplierNameSpinner.setOnTouchListener(mTouchListener);
        supplierNumberEditText.setOnTouchListener(mTouchListener);
    }

    private void setupSpinner() {

        ArrayAdapter supplierNameSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_name_options, android.R.layout.simple_spinner_item);

        supplierNameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        supplierNameSpinner.setAdapter(supplierNameSpinnerAdapter);

        supplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.elmarefa))) {
                        supplierName = InventoryEntry.SUPPLIER_ELMAAREFA;
                    } else if (selection.equals(getString(R.string.jarir))) {
                        supplierName = InventoryEntry.SUPPLIER_JARIR;
                    } else if (selection.equals(getString(R.string.jamloon))) {
                        supplierName = InventoryEntry.SUPPLIER_JAMLOON;
                    } else {
                        supplierName = InventoryEntry.SUPPLIER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                supplierName = InventoryEntry.SUPPLIER_UNKNOWN;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;
            case android.R.id.home:
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(InsertActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(InsertActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        String nameString = productNameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierNumberString = supplierNumberEditText.getText().toString().trim();

        if (mCurrentProductUri == null) {
            if (TextUtils.isEmpty(nameString)) {
                Toast.makeText(this, "Product Name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(priceString)) {
                Toast.makeText(this, "Price is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(quantityString)) {
                Toast.makeText(this, "Quantity is required to be valied", Toast.LENGTH_SHORT).show();
                return;
            }
            if (supplierName == InventoryEntry.SUPPLIER_UNKNOWN) {
                Toast.makeText(this, "Supplier name must be valid", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(supplierNumberString)) {
                Toast.makeText(this, "Supplier number is requored", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues values = new ContentValues();

            values.put(InventoryEntry.PRODUCT_NAME_COLUMN, nameString);
            values.put(InventoryEntry.PRICE_COLUMN, priceString);
            values.put(InventoryEntry.QUANTITY_COLUMN, quantityString);
            values.put(InventoryEntry.SUPPLIER_NAME_COLUMN, supplierName);
            values.put(InventoryEntry.SUPPLIER_PHONE_NUMBER_COLUMN, supplierNumberString);

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Couldn't insert the product",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "The Product inserted Successfully",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            if (TextUtils.isEmpty(nameString)) {
                Toast.makeText(this, "Product Name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(priceString)) {
                Toast.makeText(this, "Price is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(quantityString)) {
                Toast.makeText(this, "Quantity is required to be valied", Toast.LENGTH_SHORT).show();
                return;
            }
            if (supplierName == InventoryEntry.SUPPLIER_UNKNOWN) {
                Toast.makeText(this, "Supplier name must be valid", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(supplierNumberString)) {
                Toast.makeText(this, "Supplier number is required", Toast.LENGTH_SHORT).show();
                return;
            }
            ContentValues values = new ContentValues();

            values.put(InventoryEntry.PRODUCT_NAME_COLUMN, nameString);
            values.put(InventoryEntry.PRICE_COLUMN, priceString);
            values.put(InventoryEntry.QUANTITY_COLUMN, quantityString);
            values.put(InventoryEntry.SUPPLIER_NAME_COLUMN, supplierName);
            values.put(InventoryEntry.SUPPLIER_PHONE_NUMBER_COLUMN, supplierNumberString);

            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Couldn't Update the product",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product Updated Succsefully",
                        Toast.LENGTH_SHORT).show();
                finish();
            }

        }
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
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.PRODUCT_NAME_COLUMN);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.PRICE_COLUMN);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.QUANTITY_COLUMN);
            int supplierNameColumnIndex = cursor.getColumnIndex(InventoryEntry.SUPPLIER_NAME_COLUMN);
            int supplierNumberColumnIndex = cursor.getColumnIndex(InventoryEntry.SUPPLIER_PHONE_NUMBER_COLUMN);

            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int supplierName = cursor.getInt(supplierNameColumnIndex);
            String supplierNumber = cursor.getString(supplierNumberColumnIndex);

            productNameEditText.setText(name);
            priceEditText.setText(Integer.toString(price));
            quantityEditText.setText(Integer.toString(quantity));
            supplierNumberEditText.setText(supplierNumber);

            switch (supplierName) {
                case InventoryEntry.SUPPLIER_JARIR:
                    supplierNameSpinner.setSelection(1);
                    break;
                case InventoryEntry.SUPPLIER_JAMLOON:
                    supplierNameSpinner.setSelection(2);
                    break;
                case InventoryEntry.SUPPLIER_ELMAAREFA:
                    supplierNameSpinner.setSelection(3);
                    break;
                default:
                    supplierNameSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productNameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierNameSpinner.setSelection(0);
        supplierNumberEditText.setText("");

    }

    @Override
    public void onBackPressed() {
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}




package com.csf.joselo.searchsqlite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CustomerDBAdapter {
    public static final String KEY_SHOP = "shop";
    public static final String KEY_CODE = "code";
    public static final String KEY_CODE_CUSTOMER = "code_customer";
    public static final String KEY_CONTACT = "contact";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_REFERENCE_ADDRESS = "reference_address";
    public static final String KEY_COLONY = "colony";
    public static final String KEY_CITY = "city";
    public static final String KEY_ZIP = "zip";
    public static final String KEY_SEARCH = "searchData";


    private static final String TAG = "CustomerDBAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "CustomerData";
    private static final String FTS_VIRTUAL_TABLE = "CustomerInfo";
    private static final int DATABASE_VERSION = 2;

    //Create a FTS3 Virtual Table for fast searches
    private static final String DATABASE_CREATE =
            "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE + " USING fts3(" +
                    KEY_SHOP + "," +
                    KEY_CODE + "," +
                    KEY_CODE_CUSTOMER + "," +
                    KEY_CONTACT + "," +
                    KEY_ADDRESS + "," +
                    KEY_REFERENCE_ADDRESS + "," +
                    KEY_COLONY + "," +
                    KEY_CITY + "," +
                    KEY_ZIP + "," +
                    KEY_SEARCH + "," +
                    " UNIQUE (" + KEY_CODE + "));";


    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }
    }

    public CustomerDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public CustomerDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }


    public long createCustomer(String shop, String code, String code_customer, String contact, String address, String reference_address, String colony, String city, String zip) {

        ContentValues initialValues = new ContentValues();
        String searchValue =     shop + " " +
                code + " " +
                code_customer + " " +
                contact + " " +
                address + " " +
                reference_address + " " +
                colony + " " +
                city + " " +
                zip;
        initialValues.put(KEY_SHOP, shop);
        initialValues.put(KEY_CODE, code);
        initialValues.put(KEY_CODE_CUSTOMER, code_customer);
        initialValues.put(KEY_CONTACT, contact);
        initialValues.put(KEY_ADDRESS, address);
        initialValues.put(KEY_REFERENCE_ADDRESS, reference_address);
        initialValues.put(KEY_COLONY, colony);
        initialValues.put(KEY_CITY, city);
        initialValues.put(KEY_ZIP, zip);
        initialValues.put(KEY_SEARCH, searchValue);

        return mDb.insert(FTS_VIRTUAL_TABLE, null, initialValues);
    }


    public Cursor searchCustomer(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        String query = "SELECT code as _id, " +
                KEY_SHOP + "," +
                KEY_CODE_CUSTOMER + "," +
                KEY_CONTACT + "," +
                KEY_ADDRESS + "," +
                KEY_REFERENCE_ADDRESS + "," +
                KEY_COLONY + "," +
                KEY_CITY + "," +
                KEY_ZIP +
                " from " + FTS_VIRTUAL_TABLE +
                " where " +  KEY_SEARCH + " MATCH '" + inputText + "';";
        Log.w(TAG, query);
        Cursor mCursor = mDb.rawQuery(query,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }


    public boolean deleteAllCustomers() {

        int doneDelete = 0;
        doneDelete = mDb.delete(FTS_VIRTUAL_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }
}

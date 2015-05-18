package com.csf.joselo.searchsqlite;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.csf.joselo.searchsqlite.database.CustomerDBAdapter;

public class SearchViewActivity extends Activity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {

    private ListView mListView;
    private SearchView searchView;
    private CustomerDBAdapter mDbHelper;

    private TextView shopText;
    private TextView code_customerText;
    private TextView contactText;
    private TextView addressText;
    private TextView ref_addressText;
    private TextView colonyText;
    private TextView cityText;
    private TextView zipText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = (SearchView) findViewById(R.id.search);
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        mListView = (ListView) findViewById(R.id.list);

        mDbHelper = new CustomerDBAdapter(this);
        mDbHelper.open();

        //Clean all Customers
        mDbHelper.deleteAllCustomers();
        //Add some Customer data as a sample
        mDbHelper.createCustomer("TIENDA FER", "0000", "CLI-0000", "FERNANDO SAAVEDRA", "ONIQUINA 44", "GUACHARO", "SAGITARIO IV","MEXICO","55170");
        mDbHelper.createCustomer("NUEVA TIENDA", "0073", "CLI-0073", "FER", "ONIQUINA 44", "", "TAMBACOUNDA","SENEGAL","99911");
        mDbHelper.createCustomer("ABARROTES KREATIVECO", "0074", "CLI-0074", "FRANCO", "AV CHAPULTEPEC 540 COL ROMA NORTE", "", "JUAREZ","CIUDAD DE MEXICO","55770");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDbHelper  != null) {
            mDbHelper.close();
        }
    }

    public boolean onQueryTextChange(String newText) {
        showResults(newText + "*");
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        showResults(query + "*");
        return false;
    }

    public boolean onClose() {
        showResults("");
        return false;
    }

    private void showResults(String query) {

        Cursor cursor = mDbHelper.searchCustomer((query != null ? query.toString() : "@@@@"));

        if (cursor == null) {
            //
        } else {
            // Specify the columns we want to display in the result
            String[] from = new String[] {
                    CustomerDBAdapter.KEY_SHOP,
                    CustomerDBAdapter.KEY_CODE_CUSTOMER,
                    CustomerDBAdapter.KEY_CONTACT,
                    CustomerDBAdapter.KEY_ADDRESS,
                    CustomerDBAdapter.KEY_REFERENCE_ADDRESS,
                    CustomerDBAdapter.KEY_COLONY,
                    CustomerDBAdapter.KEY_CITY,
                    CustomerDBAdapter.KEY_ZIP};

            // Specify the Corresponding layout elements where we want the columns to go
            int[] to = new int[] {
                    R.id.sshop,
                    R.id.scode_customer,
                    R.id.scontact,
                    R.id.saddress,
                    R.id.sref_address,
                    R.id.scolony,
                    R.id.scity,
                    R.id.szipCode};

            // Create a simple cursor adapter for the definitions and apply them to the ListView
            SimpleCursorAdapter customers = new SimpleCursorAdapter(this,R.layout.customerresult, cursor, from, to);
            mListView.setAdapter(customers);

            // Define the on-click listener for the list items
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the cursor, positioned to the corresponding row in the result set
                    Cursor cursor = (Cursor) mListView.getItemAtPosition(position);

                    // Get the state's capital from this row in the database.
                    String shop = cursor.getString(cursor.getColumnIndexOrThrow("shop"));
                    String code_customer = cursor.getString(cursor.getColumnIndexOrThrow("code_customer"));
                    String contact = cursor.getString(cursor.getColumnIndexOrThrow("contact"));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                    String reference_address = cursor.getString(cursor.getColumnIndexOrThrow("reference_address"));
                    String colony = cursor.getString(cursor.getColumnIndexOrThrow("colony"));
                    String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
                    String zip = cursor.getString(cursor.getColumnIndexOrThrow("zip"));

                    //Check if the Layout already exists
                    LinearLayout customerLayout = (LinearLayout)findViewById(R.id.customerLayout);
                    if(customerLayout == null){
                        //Inflate the Customer Information View
                        LinearLayout leftLayout = (LinearLayout)findViewById(R.id.rightLayout);
                        View customerInfo = getLayoutInflater().inflate(R.layout.customerinfo, leftLayout, false);
                        leftLayout.addView(customerInfo);
                    }


                    //Get References to the TextViews
                    shopText = (TextView) findViewById(R.id.customer);
                    code_customerText = (TextView) findViewById(R.id.name);
                    contactText = (TextView) findViewById(R.id.address);
                    addressText = (TextView) findViewById(R.id.city);
                    ref_addressText = (TextView) findViewById(R.id.comma);
                    cityText = (TextView) findViewById(R.id.state);
                    zipText = (TextView) findViewById(R.id.zipCode);

                    // Update the parent class's TextView
                    shopText.setText(shop);
                    code_customerText.setText(code_customer);
                    contactText.setText(contact);
                    addressText.setText(address);
                    ref_addressText.setText(reference_address);
                    cityText.setText(city);
                    zipText.setText(zip);

                    searchView.setQuery("",true);
                }
            });
        }
    }


}
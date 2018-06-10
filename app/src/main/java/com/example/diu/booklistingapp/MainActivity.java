package com.example.diu.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    public static final String LOG_TAG = MainActivity.class.getName();
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int BOOK_LOADER_ID = 1;
    private ListView bookListView;
    private BookAdapter bookAdapter;
    private ProgressBar progressBar;
    private TextView mEmptyTextView;
    private ArrayList<Book> bookArrayList;
    private static final String BOOK_REQUEST_URL ="https://www.googleapis.com/books/v1/volumes?q=quilting";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        checkNetworkConnectivity();
        setBookInfo();

    }
    public void findView(){
        bookListView = (ListView) findViewById(R.id.list);
        mEmptyTextView = (TextView) findViewById(R.id.empty_view);
        progressBar=(ProgressBar)findViewById(R.id.loading_spinner);
    }

    /**
     * Update the UI with the given book information.
     */
    public void setBookInfo(){
        // Find a reference to the {@link ListView} in the layout
        bookListView.setEmptyView(mEmptyTextView);
        // Create a new adapter that takes an empty list of books as input
        bookAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(bookAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Book currentBook = bookAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri webReaderLink = Uri.parse(currentBook.getmUrl());

                // Create a new intent to view the earthquake URI
                Intent webReaderIntent = new Intent(Intent.ACTION_VIEW, webReaderLink);

                // Send the intent to launch a new activity
                startActivity(webReaderIntent);
            }
        });


    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG,"onCreateLoader");
        return new BookLoader(this,BOOK_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        //set progress bar visibility gone when background loading finish
        progressBar.setVisibility(View.GONE);
        // Set empty state text to display "No earthquakes found."
        mEmptyTextView.setText(R.string.no_earthquakes);
        // Clear the adapter of previous earthquake data
        bookAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            bookAdapter.addAll(books);
            Log.v(LOG_TAG,"onLoadFinished");
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        bookAdapter.clear();
        Log.v(LOG_TAG,"onLoadReset");
    }


    public void checkNetworkConnectivity(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        //boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        if(isConnected ){
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
            Log.v(LOG_TAG,"initLoader");
        }
        else{
            mEmptyTextView.setText(R.string.no_internet);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        MenuItem item=menu.findItem(R.id.search);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bookAdapter.getFilter().filter(newText);
               return true;
            }
        });

        return true;
    }
}

package au.edu.unsw.infs3634_lab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import au.edu.unsw.infs3634_lab.adapters.CryptoAdapter;
import au.edu.unsw.infs3634_lab.adapters.RecyclerViewClickListener;
import au.edu.unsw.infs3634_lab.api.Crypto;
import au.edu.unsw.infs3634_lab.api.CryptoApiService;
import au.edu.unsw.infs3634_lab.api.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {
    public final static String TAG = "Main-Activity";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private CryptoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the handle to the recycler view
        recyclerView = findViewById(R.id.rvList);

        // Instantiate a linear recycler view layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Use Gson library to convert JSON string to Java object
        Gson gson = new Gson();
        Response response = gson.fromJson(Response.jsonResponse, Response.class);
        List<Crypto> cryptocurrencies = response.getData();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("com.squareup.retrofit2:retrofit:2.9.0")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CryptoApiService cryptoApiService = retrofit.create(CryptoApiService.class);

        Call<Response> call = cryptoApiService.getCryptocurrencies();
        call.enqueue(new Callback<Response>() {
                         public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                         }

                         public void onResponse(Call<Response> call, Response response) {
                             if (response.isSuccessful()) {
                                 Response responseBody = response.body();
                                 if (responseBody != null) {
                                     List<Crypto> cryptocurrencies = responseBody.getData();
                                     adapter = new CryptoAdapter(cryptocurrencies, MainActivity.this);
                                     recyclerView.setAdapter(adapter);
                                 }
                             } else {
                                 // Handle unsuccessful API call
                                 Toast.makeText(MainActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                             }
                         }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                // Handle failure
                Toast.makeText(MainActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
/*
        // Create an adapter instance with the list of cryptos
        adapter = new CryptoAdapter(cryptocurrencies, this);
        // Sort the list by name
        adapter.sortList(CryptoAdapter.SORT_BY_NAME);

        // Connect the adapter to the recycler view
        recyclerView.setAdapter(adapter);

 */


    // Called when user taps launch button
    public void launchDetailActivity(String msg) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.INTENT_MESSAGE, msg);
        startActivity(intent);
    }


    @Override
    public void onRowClick(String symbol) {
        launchDetailActivity(symbol);
    }

    // Instantiate the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Find the handle to search menu item
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    // React to user interaction with the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortName:
                adapter.sortList(CryptoAdapter.SORT_BY_NAME);
                return true;
            case R.id.sortValue:
                adapter.sortList(CryptoAdapter.SORT_BY_VALUE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
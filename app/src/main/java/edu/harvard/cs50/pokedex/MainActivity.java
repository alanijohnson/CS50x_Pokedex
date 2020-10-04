package edu.harvard.cs50.pokedex;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerView;
    private PokedexAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // define views and layout variables
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new PokedexAdapter(getApplicationContext());
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String filterText) {
        adapter.getFilter().filter(filterText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String filterText){
        return onQueryTextChange(filterText);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        Log.d("Activity Result", "Starting");
////        Log.d("Result Code",String.valueOf(resultCode));
////        Log.d("Request Code", String.valueOf(requestCode));
////        Log.d("caught", String.valueOf(data.getBooleanExtra("caught",false)));
////        Log.d("id",String.valueOf(data.getIntExtra("id",-1)));
//        if(resultCode == RESULT_OK && data != null){
//            if(requestCode == PokemonActivity.RETURN_TO_MAIN) {
//                try {
//                    adapter.getPokemons().get(data.getIntExtra("id", -1) - 1).setCaught(data.getBooleanExtra("caught",false));
//
//                } catch (Exception e){
//                    Log.e("Pokemon write", "Pokemon Write Error", e);
//                }
//            }
//        }
//
//
//
//    }
}

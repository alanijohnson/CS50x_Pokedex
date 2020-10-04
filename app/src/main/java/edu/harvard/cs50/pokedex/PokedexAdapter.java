package edu.harvard.cs50.pokedex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> implements Filterable {

    // Adapter - ??
    // ViewHolder - hold view and allows manipulation of views on the screen
    public static class PokedexViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout containerView;
        public TextView textView;

        PokedexViewHolder(View view) {
            super(view);
            // access views using IDs
            containerView = view.findViewById(R.id.pokedex_row);
            textView = view.findViewById(R.id.pokedex_row_text_view);

            containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // access pokemon from the tag within the container view
                    Pokemon current = (Pokemon) containerView.getTag();
                    Intent intent = new Intent(v.getContext(), PokemonActivity.class);
                    intent.putExtra("url", current.getUrl());
                    intent.putExtra("caught", current.isCaught());
                    intent.putExtra("p", (Parcelable) current);

                    //v.getContext().startActivity(intent);

                    ((Activity) v.getContext()).startActivityForResult(intent,PokemonActivity.RETURN_TO_MAIN);


                }
            });
        }
    }

    private class PokemonFilter extends Filter {

        @Override
        protected FilterResults performFiltering(final CharSequence constraint){
            // implement search here
            // compare all results as lowercase so case doesn't matter
            // iterate through pokemons adding all pokemons to filter
            final CharSequence CONST = String.valueOf(constraint).toLowerCase();
            FilterResults results = new FilterResults();
            List<Pokemon> filteredPokemon = new ArrayList<>();
            for(Pokemon p : pokemons){
                if(p.getName().toLowerCase().contains(CONST)){

                    filteredPokemon.add(p);
                }
            }
            results.count = filteredPokemon.size();
            results.values = filteredPokemon;
            return results;
        }

        @Override
        protected void  publishResults(CharSequence constraint, FilterResults results) {
            filtered = (List<Pokemon>) results.values;
            notifyDataSetChanged();
        }
    }

    private List<Pokemon> pokemons = new ArrayList<>();
    private List<Pokemon> filtered = new ArrayList<>();
    private RequestQueue requestQueue;

    PokedexAdapter(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        loadPokemon();
    }

    public void loadPokemon() {
        // load pokemon from pokeAPI

        String url = "https://pokeapi.co/api/v2/pokemon?limit=500";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        String name = result.getString("name");
                        pokemons.add(new Pokemon(
                            name.substring(0, 1).toUpperCase() + name.substring(1),
                            result.getString("url")
                        ));
                    }

                    filtered = pokemons;
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e("cs50", "Json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon list error", error);
            }
        });

        requestQueue.add(request);
    }

    @NonNull
    @Override
    public PokedexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // called when a new View Holder is created
        // LayoutInflater.from ->
        // Inflate -> convert xml layout to java view.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pokedex_row, parent, false);

        return new PokedexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) {
        // method used to set the values when the row is scrolled into frame.
        // position - row
        Pokemon current = filtered.get(position);
        holder.textView.setText(current.getName());
        // give view access to current pokemon;
        holder.containerView.setTag(current);
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    @Override
    public Filter getFilter(){
        return new PokemonFilter();
    }

    public List<Pokemon> getPokemons() {
        return pokemons;
    }
}

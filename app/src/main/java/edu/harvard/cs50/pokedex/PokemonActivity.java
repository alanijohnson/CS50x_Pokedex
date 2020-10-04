package edu.harvard.cs50.pokedex;

import androidx.annotation.IntegerRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class PokemonActivity extends AppCompatActivity {
    private class DownloadSpriteTask extends AsyncTask<String,Void, Bitmap> {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // load the bitmap into ImageView
            pokemonImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()*SCALEFACTOR, bitmap.getHeight()*SCALEFACTOR,true));
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            } catch (IOException e) {
                Log.e("cs50","Download sprite error", e);
                return null;
            }

        }
    }

    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private TextView descTextView;
    private ImageView pokemonImageView;
    private Button button;
    private String url;
    private int id;
    private Pokemon pokemon;
    private RequestQueue requestQueue;
    private SharedPreferences sp;
    private String spriteURL;
    private final int SCALEFACTOR = 5;
    private final String LANG = "en";
    public static final int RETURN_TO_MAIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        // pull shared preferences file. Used to save data about status of application.
        sp = getSharedPreferences("Caught", Context.MODE_PRIVATE);

        // create a request queue to load objects from the API.
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // get data from Intent
        url = getIntent().getStringExtra("url");
        pokemon = getIntent().getParcelableExtra("p");
        pokemon.setCaught(sp.getBoolean(pokemon.getName(),false));
        // parse ID from the URL
        id = Integer.parseInt(url.replace("https://pokeapi.co/api/v2/pokemon/","").replace("/",""));

        // access view elements by ID
        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);
        descTextView = findViewById(R.id.pokemon_desc);
        button = findViewById(R.id.pokemon_caught);
        pokemonImageView = findViewById(R.id.pokemon_image);

        load();

    }

    public void load() {
        // initialize text of type1 and type 2 to empty
        type1TextView.setText("");
        type2TextView.setText("");

        // set text for views
        nameTextView.setText(pokemon.getName());
        numberTextView.setText(String.format("#%03d", id));
        button.setText(catchButtonText(pokemon.isCaught()));
        //setResult(RESULT_CANCELED);

        //request for data from pokemon info page. This url is saved with the pokemon
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //nameTextView.setText(response.getString("name"));
                    //id = response.getInt("id");
                    //Log.d("poke desc url ID",String.valueOf(id));
                    //numberTextView.setText(String.format("#%03d", id));

                    JSONArray typeEntries = response.getJSONArray("types");
                    for (int i = 0; i < typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");

                        if (slot == 1) {
                            type1TextView.setText(type);
                        }
                        else if (slot == 2) {
                            type2TextView.setText(type);
                        }
                    }

                    spriteURL = response.getJSONObject("sprites").getString("front_default");
                    new DownloadSpriteTask().execute(spriteURL);

                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon details error", error);
            }
        });

        requestQueue.add(request);

        // request information about sprite from species page
        url = "https://pokeapi.co/api/v2/pokemon-species/";
        url = url + id;
        Log.d("poke desc url", url);
        request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String txt = "";
                    JSONArray descriptions = response.getJSONArray("flavor_text_entries");
                    JSONObject description;
                    //iterate through descriptions to get the first one that matches the language indicated.
                    for(int i=0; i<descriptions.length(); i++){
                        description = descriptions.getJSONObject(i);
                        Log.d("poke desc lang:", description.getJSONObject("language").getString("name"));
                        if(description.getJSONObject("language").getString("name").compareTo(LANG) == 0){

                            txt += description.getString("flavor_text").replace("\n", " ");
                            break;
                        }
                        Log.d("desc text",txt);
                    }

                    descTextView.setText(txt);

                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "Pokemon details error", error);
            }
        });

        requestQueue.add(request);
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    public void onPause() {
        // save shared preferences only when the application is left.
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(pokemon.getName(), pokemon.isCaught());
        editor.commit();
        Log.d("PokemonActivity onPause",String.valueOf(id) + " is caught: " + String.valueOf(pokemon.isCaught()));
        super.onPause();
    }

    public void toggleCatch(View view) {
        // catch pokemon
        // set pokemon to caught or release and update button text
        pokemon.toggleCaught();
        button.setText(catchButtonText(pokemon.isCaught()));
//        Intent intent = new Intent();
//        intent.putExtra("caught",pokemon.isCaught());
//        intent.putExtra("id", id);
//        if(getParent() == null){
//            setResult(RESULT_OK,intent);
//            //finish();
//        } else {
//            getParent().setResult(RESULT_OK,intent);
//        }
        Log.d("Caught Button toggle", (String) button.getText());

    }

    public String catchButtonText(boolean caught){
        if (caught == true){
            return "Release";
        } else {
            return "Catch";
        }
    }
}

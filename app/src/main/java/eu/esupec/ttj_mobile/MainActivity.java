package eu.esupec.ttj_mobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OffreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ttj_accueil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisation RecyclerView
        recyclerView = findViewById(R.id.rv_offres);

        // Initialisation du BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.ttj_navbar);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                chargerOffres();
                return true;
            }
            // ... autres clics
            return true;
        });

        // Appel API initial
        chargerOffres();
    }

    private void chargerOffres() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.16.4.4:5555/api/") // HTTP car IP locale souvent sans SSL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getOffres().enqueue(new Callback<List<Offre>>() {
            @Override
            public void onResponse(@NonNull Call<List<Offre>> call, @NonNull Response<List<Offre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new OffreAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Erreur API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Offre>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Erreur réseau", t);
                Toast.makeText(MainActivity.this, "Impossible de contacter l'API", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
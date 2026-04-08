package eu.esupec.ttj_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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

import eu.esupec.ttj_mobile.adapter.OffreAdapter;
import eu.esupec.ttj_mobile.entity.Offre;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OffreAdapter adapter;
    private View modalOffre;
    private View modalOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ttj_base);

        modalOffre = findViewById(R.id.modal_offre);
        modalOverlay = findViewById(R.id.modal_overlay);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        recyclerView = findViewById(R.id.rv_offres);

        BottomNavigationView bottomNav = findViewById(R.id.ttj_navbar);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                chargerOffres();
                return true;
            }
            return true;
        });

        chargerOffres();
    }

    private void chargerOffres() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getOffres().enqueue(new Callback<List<Offre>>() {
            @Override
            public void onResponse(@NonNull Call<List<Offre>> call, @NonNull Response<List<Offre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new OffreAdapter(response.body(), MainActivity.this);
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

    public void afficherModalOffre(Offre offre) {
        TextView tvTitre = findViewById(R.id.modal_titre);
        TextView tvType = findViewById(R.id.modal_type);
        TextView tvDesc = findViewById(R.id.modal_description);
        TextView tvStatut = findViewById(R.id.modal_statut);
        TextView tvDatePub = findViewById(R.id.modal_date_pub);
        TextView tvDateLimite = findViewById(R.id.modal_date_limite);
        Button btnPostuler = findViewById(R.id.modal_btn_postuler);
        ImageButton btnClose = findViewById(R.id.btn_close_modal);

        tvTitre.setText(offre.getTitre());
        tvType.setText(offre.getTypeContrat());
        tvDesc.setText(offre.getDescription());
        tvStatut.setText("Statut : " + offre.getStatut());
        tvDatePub.setText("Publiée le : " + offre.getDatePublication());
        tvDateLimite.setText("Limite : " + offre.getDateLimite());

        btnClose.setOnClickListener(v -> masquerModal());
        modalOverlay.setOnClickListener(v -> masquerModal());

        btnPostuler.setOnClickListener(v -> {
            Toast.makeText(this, "Candidature envoyée pour " + offre.getTitre(), Toast.LENGTH_SHORT).show();
            masquerModal();
        });

        modalOverlay.setVisibility(View.VISIBLE);
        modalOverlay.setAlpha(0f);
        modalOverlay.animate().alpha(1f).setDuration(300).start();

        modalOffre.setVisibility(View.VISIBLE);
        modalOffre.setTranslationY(2500f);
        modalOffre.animate()
                .translationY(0)
                .setDuration(450)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void masquerModal() {
        modalOverlay.animate().alpha(0f).setDuration(300).withEndAction(() -> modalOverlay.setVisibility(View.GONE)).start();
        modalOffre.animate()
                .translationY(2500f)
                .setDuration(450)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> modalOffre.setVisibility(View.GONE))
                .start();
    }
}
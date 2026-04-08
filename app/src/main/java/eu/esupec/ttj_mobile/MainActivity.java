package eu.esupec.ttj_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import eu.esupec.ttj_mobile.adapter.OffreAdapter;
import eu.esupec.ttj_mobile.api.ApiClient;
import eu.esupec.ttj_mobile.api.ApiService;
import eu.esupec.ttj_mobile.entity.LoginRequest;
import eu.esupec.ttj_mobile.entity.LoginResponse;
import eu.esupec.ttj_mobile.entity.Offre;
import eu.esupec.ttj_mobile.entity.RegisterRequest;
import eu.esupec.ttj_mobile.entity.User;
import eu.esupec.ttj_mobile.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OffreAdapter adapter;
    private View modalOffre;
    private View modalOverlay;
    private View layoutLogin;
    private View layoutProfileDetails;
    private SessionManager sessionManager;
    private TextView tvAppTitle;

    // Éléments Auth
    private TextView tvAuthTitle, tvSwitchAuth;
    private LinearLayout layoutRegisterFields;
    private TextInputEditText etEmail, etPassword, etFirstname, etLastname, etPhone;
    private Button btnAuthAction;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ttj_base);

        sessionManager = new SessionManager(this);
        
        // Initialisation des composants
        modalOffre = findViewById(R.id.modal_offre);
        modalOverlay = findViewById(R.id.modal_overlay);
        layoutLogin = findViewById(R.id.include_login);
        layoutProfileDetails = findViewById(R.id.include_profile_details);
        recyclerView = findViewById(R.id.rv_offres);
        tvAppTitle = findViewById(R.id.tv_app_title);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.ttj_navbar);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                showHome();
                return true;
            } else if (id == R.id.nav_profile) {
                showProfileOrLogin();
                return true;
            }
            return true;
        });

        initAuthUI();
        showHome(); // Affichage initial
    }

    private void initAuthUI() {
        tvAuthTitle = findViewById(R.id.tv_auth_title);
        tvSwitchAuth = findViewById(R.id.tv_switch_auth);
        layoutRegisterFields = findViewById(R.id.layout_register_fields);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etFirstname = findViewById(R.id.et_firstname);
        etLastname = findViewById(R.id.et_lastname);
        etPhone = findViewById(R.id.et_phone);
        btnAuthAction = findViewById(R.id.btn_auth_action);

        tvSwitchAuth.setOnClickListener(v -> toggleAuthMode());

        btnAuthAction.setOnClickListener(v -> {
            if (isLoginMode) performLogin();
            else performRegister();
        });

        findViewById(R.id.btn_logout).setOnClickListener(v -> performLogout());
    }

    private void toggleAuthMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            tvAuthTitle.setText("Connexion");
            layoutRegisterFields.setVisibility(View.GONE);
            btnAuthAction.setText("SE CONNECTER");
            tvSwitchAuth.setText("Pas encore de compte ? S'inscrire");
        } else {
            tvAuthTitle.setText("Inscription");
            layoutRegisterFields.setVisibility(View.VISIBLE);
            btnAuthAction.setText("S'INSCRIRE");
            tvSwitchAuth.setText("Déjà un compte ? Se connecter");
        }
    }

    private void showHome() {
        tvAppTitle.setText("Trouve Ton Job");
        recyclerView.setVisibility(View.VISIBLE);
        layoutLogin.setVisibility(View.GONE);
        layoutProfileDetails.setVisibility(View.GONE);
        chargerOffres();
    }

    private void showProfileOrLogin() {
        recyclerView.setVisibility(View.GONE);
        if (sessionManager.isLoggedIn()) {
            tvAppTitle.setText("Mon Profil");
            layoutLogin.setVisibility(View.GONE);
            layoutProfileDetails.setVisibility(View.VISIBLE);
            fetchProfile();
        } else {
            tvAppTitle.setText("Connexion");
            layoutLogin.setVisibility(View.VISIBLE);
            layoutProfileDetails.setVisibility(View.GONE);
        }
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getService(this).login(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionManager.saveToken(response.body().getToken());
                    Toast.makeText(MainActivity.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                    showProfileOrLogin();
                } else {
                    Toast.makeText(MainActivity.this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performRegister() {
        String fname = etFirstname.getText().toString().trim();
        String lname = etLastname.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getService(this).register(new RegisterRequest(lname, fname, email, phone, pass)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sessionManager.saveToken(response.body().getToken());
                    Toast.makeText(MainActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                    showProfileOrLogin();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProfile() {
        ApiClient.getService(this).getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    ((TextView)findViewById(R.id.tv_profile_name)).setText(user.getPrenom() + " " + user.getNom());
                    ((TextView)findViewById(R.id.tv_profile_email)).setText(user.getEmail());
                    ((TextView)findViewById(R.id.tv_profile_phone)).setText(user.getTelephone());
                } else if (response.code() == 401) {
                    performLogout(); // Token expiré
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("API", "Erreur profil", t);
            }
        });
    }

    private void performLogout() {
        sessionManager.logout();
        Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show();
        showProfileOrLogin();
    }

    private void chargerOffres() {
        ApiClient.getService(this).getOffres().enqueue(new Callback<List<Offre>>() {
            @Override
            public void onResponse(@NonNull Call<List<Offre>> call, @NonNull Response<List<Offre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new OffreAdapter(response.body(), MainActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Offre>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Erreur réseau", t);
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
        modalOffre.setTranslationY(2000f);
        modalOffre.animate()
                .translationY(0)
                .setDuration(450)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void masquerModal() {
        modalOverlay.animate().alpha(0f).setDuration(300).withEndAction(() -> modalOverlay.setVisibility(View.GONE)).start();
        modalOffre.animate()
                .translationY(2000f)
                .setDuration(450)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> modalOffre.setVisibility(View.GONE))
                .start();
    }
}
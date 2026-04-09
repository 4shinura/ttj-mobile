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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.esupec.ttj_mobile.adapter.CandidatureAdapter;
import eu.esupec.ttj_mobile.adapter.OffreAdapter;
import eu.esupec.ttj_mobile.api.ApiClient;
import eu.esupec.ttj_mobile.api.ApiService;
import eu.esupec.ttj_mobile.entity.Candidature;
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

    private RecyclerView recyclerView, rvCandidatures;
    private OffreAdapter adapter;
    private CandidatureAdapter candidatureAdapter;
    private View modalOffre, modalCandidature;
    private View modalOverlay;
    private View layoutLogin;
    private View layoutProfileDetails;
    private SessionManager sessionManager;
    private TextView tvAppTitle;
    private List<Candidature> userCandidatures = new java.util.ArrayList<>();

    // Éléments Auth
    private TextView tvAuthTitle, tvSwitchAuth;
    private LinearLayout layoutRegisterFields;
    private TextInputEditText etEmail, etPassword, etFirstname, etLastname;
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
        modalCandidature = findViewById(R.id.modal_candidature);
        modalOverlay = findViewById(R.id.modal_overlay);
        layoutLogin = findViewById(R.id.include_login);
        layoutProfileDetails = findViewById(R.id.include_profile_details);
        recyclerView = findViewById(R.id.rv_offres);
        rvCandidatures = findViewById(R.id.rv_candidatures);
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
            } else if (id == R.id.nav_candidatures) {
                showCandidatures();
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
        rvCandidatures.setVisibility(View.GONE);
        layoutLogin.setVisibility(View.GONE);
        layoutProfileDetails.setVisibility(View.GONE);
        chargerOffres();
    }

    private void showCandidatures() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Connectez-vous pour voir vos candidatures", Toast.LENGTH_SHORT).show();
            BottomNavigationView bottomNav = findViewById(R.id.ttj_navbar);
            bottomNav.setSelectedItemId(R.id.nav_profile);
            showProfileOrLogin();
            return;
        }
        tvAppTitle.setText("Mes Candidatures");
        recyclerView.setVisibility(View.GONE);
        rvCandidatures.setVisibility(View.VISIBLE);
        layoutLogin.setVisibility(View.GONE);
        layoutProfileDetails.setVisibility(View.GONE);
        chargerCandidatures();
    }

    private void showProfileOrLogin() {
        recyclerView.setVisibility(View.GONE);
        rvCandidatures.setVisibility(View.GONE);
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
        String pass = etPassword.getText().toString().trim();

        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest(lname, fname, email, pass);

        ApiClient.getService(this).registerCandidat(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(MainActivity.this, "Inscription réussie ! En attente de validation.", Toast.LENGTH_LONG).show();
                    isLoginMode = true;
                    toggleAuthMode();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProfile() {
        User sessionUser = sessionManager.getUser();
        if (sessionUser != null) {
            updateProfileUI(sessionUser);
        }

        ApiClient.getService(this).getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    sessionManager.saveUser(user);
                    updateProfileUI(user);
                } else if (response.code() == 401) {
                    performLogout();
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("API", "Erreur profil", t);
            }
        });
    }

    private void updateProfileUI(User user) {
        ((TextView)findViewById(R.id.tv_profile_name)).setText(user.getPrenom() + " " + user.getNom());
        ((TextView)findViewById(R.id.tv_profile_email)).setText(user.getEmail());
    }

    private void performLogout() {
        sessionManager.logout();
        Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show();
        showProfileOrLogin();
    }

    private void chargerCandidatures() {
        ApiClient.getService(this).getMyCandidatures().enqueue(new Callback<List<Candidature>>() {
            @Override
            public void onResponse(@NonNull Call<List<Candidature>> call, @NonNull Response<List<Candidature>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userCandidatures = response.body();
                    if (userCandidatures.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Aucune candidature trouvée", Toast.LENGTH_SHORT).show();
                    }
                    candidatureAdapter = new CandidatureAdapter(userCandidatures, new CandidatureAdapter.OnCandidatureListener() {
                        @Override
                        public void onDeleteClick(Candidature candidature) {
                            supprimerCandidature(candidature);
                        }

                        @Override
                        public void onItemClick(Candidature candidature) {
                            afficherModalCandidature(candidature);
                        }
                    });
                    rvCandidatures.setAdapter(candidatureAdapter);
                } else {
                    String errorMsg = "Erreur serveur : " + response.code();
                    if (response.code() == 404) {
                        errorMsg = "Route API /candidatures non trouvée (404)";
                    }
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Candidature>> call, @NonNull Throwable t) {
                Log.e("API_DEBUG", "Détail de l'erreur: ", t);
                String errorType = t.getClass().getSimpleName();
                Toast.makeText(MainActivity.this, "Erreur (" + errorType + ") : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void afficherModalCandidature(Candidature candidature) {
        TextView tvTitre = findViewById(R.id.modal_cand_titre);
        TextView tvDate = findViewById(R.id.modal_cand_date);
        TextView tvStatut = findViewById(R.id.modal_cand_statut);
        TextView tvDesc = findViewById(R.id.modal_cand_desc);
        Button btnDelete = findViewById(R.id.modal_cand_btn_delete);
        ImageButton btnClose = findViewById(R.id.btn_close_modal_cand);

        if (candidature.getOffre() != null) {
            tvTitre.setText(candidature.getOffre().getTitre());
            tvDesc.setText(candidature.getOffre().getDescription());
        } else {
            tvTitre.setText("Offre inconnue");
            tvDesc.setText("Aucune description disponible.");
        }
        
        tvDate.setText("Postulé le : " + candidature.getDate());
        tvStatut.setText("STATUT : " + candidature.getStatut().toUpperCase());

        btnClose.setOnClickListener(v -> masquerModalCandidature());
        modalOverlay.setOnClickListener(v -> masquerModalCandidature());

        btnDelete.setOnClickListener(v -> {
            masquerModalCandidature();
            supprimerCandidature(candidature);
        });

        modalOverlay.setVisibility(View.VISIBLE);
        modalOverlay.setAlpha(0f);
        modalOverlay.animate().alpha(1f).setDuration(300).start();

        modalCandidature.setVisibility(View.VISIBLE);
        modalCandidature.setTranslationY(2000f);
        modalCandidature.animate()
                .translationY(0)
                .setDuration(450)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void masquerModalCandidature() {
        modalOverlay.animate().alpha(0f).setDuration(300).withEndAction(() -> modalOverlay.setVisibility(View.GONE)).start();
        modalCandidature.animate()
                .translationY(2000f)
                .setDuration(450)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> modalCandidature.setVisibility(View.GONE))
                .start();
    }

    private void supprimerCandidature(Candidature candidature) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmer la suppression")
                .setMessage("Voulez-vous vraiment supprimer votre candidature pour l'offre : " + 
                        (candidature.getOffre() != null ? candidature.getOffre().getTitre() : "cette offre") + " ?")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    ApiClient.getService(this).deleteCandidature(candidature.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Candidature supprimée", Toast.LENGTH_SHORT).show();
                                chargerCandidatures(); // Rafraîchir la liste
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(MainActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Annuler", null)
                .show();
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

        boolean alreadyApplied = false;
        for (Candidature c : userCandidatures) {
            if (c.getOffre() != null && c.getOffre().getId() == offre.getId()) {
                alreadyApplied = true;
                break;
            }
        }

        if (alreadyApplied) {
            btnPostuler.setEnabled(false);
            btnPostuler.setText("DÉJÀ POSTULÉ");
            btnPostuler.setAlpha(0.5f);
        } else {
            btnPostuler.setEnabled(true);
            btnPostuler.setText("POSTULER");
            btnPostuler.setAlpha(1.0f);
        }

        btnClose.setOnClickListener(v -> masquerModal());
        modalOverlay.setOnClickListener(v -> masquerModal());

        btnPostuler.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(this, "Veuillez vous connecter pour postuler", Toast.LENGTH_SHORT).show();
                masquerModal();
                // Rediriger vers l'onglet Profil
                BottomNavigationView bottomNav = findViewById(R.id.ttj_navbar);
                bottomNav.setSelectedItemId(R.id.nav_profile);
                showProfileOrLogin();
                return;
            }

            // Préparation de la requête
            Map<String, Integer> body = new HashMap<>();
            body.put("offre_id", offre.getId());

            ApiClient.getService(this).postuler(body).enqueue(new Callback<Candidature>() {
                @Override
                public void onResponse(@NonNull Call<Candidature> call, @NonNull Response<Candidature> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Candidature envoyée avec succès !", Toast.LENGTH_SHORT).show();
                        masquerModal();
                    } else if (response.code() == 409) {
                        Toast.makeText(MainActivity.this, "Vous avez déjà postulé à cette offre", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Erreur : " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Candidature> call, @NonNull Throwable t) {
                    Toast.makeText(MainActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
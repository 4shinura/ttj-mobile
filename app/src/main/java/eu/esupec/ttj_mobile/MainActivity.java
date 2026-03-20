package eu.esupec.ttj_mobile;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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

        // Initialisation du BottomNavigationView dans onCreate()
        BottomNavigationView bottomNav = findViewById(R.id.ttj_navbar);

        // Configuration du clic sur les items
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(this, "Vous avez cliqué sur l'accueil", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.nav_candidatures) {
                Toast.makeText(this, "Vous avez cliqué sur vos candidatures", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Vous avez cliqué sur votre profil", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.nav_messages) {
                Toast.makeText(this, "Vous avez cliqué sur messages", Toast.LENGTH_LONG).show();
                return true;
            } else if (id == R.id.nav_logout) {
                Toast.makeText(this, "Vous avez cliqué sur déconnexion", Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        });
    }
}
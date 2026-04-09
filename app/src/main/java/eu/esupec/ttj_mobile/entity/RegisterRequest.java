package eu.esupec.ttj_mobile.entity;

public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;

    public RegisterRequest(String nom, String prenom, String email, String motDePasse) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // Getters et Setters si nécessaires pour GSON
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getMotDePasse() { return motDePasse; }
}

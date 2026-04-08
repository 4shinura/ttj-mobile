package eu.esupec.ttj_mobile.entity;

public class User {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String type; // 'candidat' ou 'administrateur'

    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public String getTelephone() { return telephone; }
    public String getType() { return type; }
}
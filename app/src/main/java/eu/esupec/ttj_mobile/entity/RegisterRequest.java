package eu.esupec.ttj_mobile.entity;

public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String password;
    private String type = "candidat";

    public RegisterRequest(String nom, String prenom, String email, String telephone, String password) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
    }
}
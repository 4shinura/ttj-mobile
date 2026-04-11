package eu.esupec.ttj_mobile.entity;

public class Message {
    private int id;
    private String contenu;
    private String dateEnvoi;
    private User emetteur;
    private User destinataire;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(String dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public User getEmetteur() { return emetteur; }
    public void setEmetteur(User emetteur) { this.emetteur = emetteur; }

    public User getDestinataire() { return destinataire; }
    public void setDestinataire(User destinataire) { this.destinataire = destinataire; }
}

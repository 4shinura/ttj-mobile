package eu.esupec.ttj_mobile.entity;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Offre implements Serializable {
    private int id;
    private String titre;
    private String description;
    private String statut;

    @SerializedName("type")
    private String typeContrat;

    @SerializedName("datePublication")
    private String datePublication;

    @SerializedName("dateLimite")
    private String dateLimite;

    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getTypeContrat() { return typeContrat; }
    public String getDatePublication() { return datePublication; }
    public String getDateLimite() { return dateLimite; }
    public String getStatut() { return statut; }
}
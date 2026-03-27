package eu.esupec.ttj_mobile;

import com.google.gson.annotations.SerializedName;

public class Offre {
    private String titre;
    private String description;
    @SerializedName("type_contrat")
    private String typeContrat;
    @SerializedName("date_publication")
    private String datePublication;

    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getTypeContrat() { return typeContrat; }
    public String getDatePublication() { return datePublication; }
}
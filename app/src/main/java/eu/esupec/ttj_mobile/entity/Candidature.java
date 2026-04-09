package eu.esupec.ttj_mobile.entity;

import java.io.Serializable;

public class Candidature implements Serializable {
    private int id;
    private User candidat;
    private Offre offre;
    private String date;
    private String statut;

    public int getId() { return id; }
    public User getCandidat() { return candidat; }
    public Offre getOffre() { return offre; }
    public String getDate() { return date; }
    public String getStatut() { return statut; }
}

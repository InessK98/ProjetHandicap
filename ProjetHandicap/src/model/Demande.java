package model;

public class Demande {

    private int idDemande;
    private String typeDemande;
    private String description;
    private String dateSoumission;
    private Statut statut;
    private String pieceJustificative;

    public Demande() {
    }

    public Demande(int idDemande, String typeDemande, String description,
                   String dateSoumission, Statut statut, String pieceJustificative) {
        this.idDemande = idDemande;
        this.typeDemande = typeDemande;
        this.description = description;
        this.dateSoumission = dateSoumission;
        this.statut = statut;
        this.pieceJustificative = pieceJustificative;
    }

    public void modifier() {
        System.out.println("Demande modifiée.");
    }

    public void supprimer() {
        System.out.println("Demande supprimée.");
    }

    public void mettreAJourStatut(Statut statut) {
        this.statut = statut;
    }

    public int getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(int idDemande) {
        this.idDemande = idDemande;
    }

    public String getTypeDemande() {
        return typeDemande;
    }

    public void setTypeDemande(String typeDemande) {
        this.typeDemande = typeDemande;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateSoumission() {
        return dateSoumission;
    }

    public void setDateSoumission(String dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public String getPieceJustificative() {
        return pieceJustificative;
    }

    public void setPieceJustificative(String pieceJustificative) {
        this.pieceJustificative = pieceJustificative;
    }
}
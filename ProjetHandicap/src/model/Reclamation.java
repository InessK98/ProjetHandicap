package model;

public class Reclamation {

    private int idReclamation;
    private String motif;
    private String description;
    private String dateReclamation;
    private Statut statut;

    public Reclamation() {
    }

    public Reclamation(int idReclamation, String motif, String description,
                       String dateReclamation, Statut statut) {
        this.idReclamation = idReclamation;
        this.motif = motif;
        this.description = description;
        this.dateReclamation = dateReclamation;
        this.statut = statut;
    }

    public void suivreTraitement() {
        System.out.println("Suivi de la réclamation...");
    }

    public int getIdReclamation() {
        return idReclamation;
    }

    public void setIdReclamation(int idReclamation) {
        this.idReclamation = idReclamation;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateReclamation() {
        return dateReclamation;
    }

    public void setDateReclamation(String dateReclamation) {
        this.dateReclamation = dateReclamation;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }
}
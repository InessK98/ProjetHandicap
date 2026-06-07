package model;

public class PersonneHandicap extends Utilisateur {

    private String typeHandicap;

    public PersonneHandicap() {
    }

    public PersonneHandicap(int id, String nom, String prenom, String email, String motDePasse, String typeHandicap) {
        super(id, nom, prenom, email, motDePasse);
        this.typeHandicap = typeHandicap;
    }

    public void soumettreDemande() {
        System.out.println("Demande soumise.");
    }

    public void soumettreReclamation() {
        System.out.println("Réclamation soumise.");
    }

    public void consulterStatut() {
        System.out.println("Consultation du statut.");
    }

    public String getTypeHandicap() {
        return typeHandicap;
    }

    public void setTypeHandicap(String typeHandicap) {
        this.typeHandicap = typeHandicap;
    }
}
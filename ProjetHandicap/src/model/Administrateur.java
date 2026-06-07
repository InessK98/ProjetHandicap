package model;

public class Administrateur extends Utilisateur {

    public Administrateur() {
    }

    public Administrateur(int id, String nom, String prenom, String email, String motDePasse) {
        super(id, nom, prenom, email, motDePasse);
    }

    public void gererComptes() {
        System.out.println("Gestion des comptes...");
    }

    public void validerDemande() {
        System.out.println("Demande validée.");
    }

    public void afficherStats() {
        System.out.println("Affichage des statistiques...");
    }
}
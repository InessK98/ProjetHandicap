package controller;

import dao.UtilisateurDAO;
import model.Utilisateur;

public class UtilisateurController {

    private UtilisateurDAO utilisateurDAO;

    public UtilisateurController() {
        utilisateurDAO = new UtilisateurDAO();
    }

    public void ajouterUtilisateur(Utilisateur utilisateur, String role, String typeHandicap) {

        if (utilisateur == null) {
            System.out.println("Utilisateur invalide.");
            return;
        }

        if (utilisateur.getNom() == null || utilisateur.getNom().isEmpty()
                || utilisateur.getPrenom() == null || utilisateur.getPrenom().isEmpty()
                || utilisateur.getEmail() == null || utilisateur.getEmail().isEmpty()
                || utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().isEmpty()
                || role == null || role.isEmpty()) {

            System.out.println("Champs obligatoires manquants.");
            return;
        }

        boolean ok = utilisateurDAO.ajouterUtilisateur(utilisateur, role, typeHandicap);

        if (ok) {
            System.out.println("Utilisateur ajouté avec succès !");
        } else {
            System.out.println("Erreur lors de l'ajout de l'utilisateur.");
        }
    }

    public void afficherUtilisateurs() {
        utilisateurDAO.afficherUtilisateurs();
    }

    public void modifierUtilisateur(int id, String nom, String prenom, String email) {

        if (nom == null || nom.isEmpty()
                || prenom == null || prenom.isEmpty()
                || email == null || email.isEmpty()) {
            System.out.println("Champs obligatoires manquants.");
            return;
        }

        boolean ok = utilisateurDAO.modifierUtilisateur(id, nom, prenom, email);

        if (ok) {
            System.out.println("Utilisateur modifié avec succès !");
        } else {
            System.out.println("Erreur lors de la modification de l'utilisateur.");
        }
    }

    public void supprimerUtilisateur(int id) {
        boolean ok = utilisateurDAO.supprimerUtilisateur(id);

        if (ok) {
            System.out.println("Utilisateur supprimé avec succès !");
        } else {
            System.out.println("Erreur lors de la suppression de l'utilisateur.");
        }
    }
}

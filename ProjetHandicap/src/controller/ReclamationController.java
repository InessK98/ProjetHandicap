package controller;

import dao.ReclamationDAO;

public class ReclamationController {

    private ReclamationDAO reclamationDAO;

    public ReclamationController() {
        reclamationDAO = new ReclamationDAO();
    }

    public void ajouterReclamation(String motif, String description, String dateReclamation,
                                   String statut, int idUtilisateur) {

        boolean ok = reclamationDAO.ajouterReclamation(
                motif,
                description,
                dateReclamation,
                statut,
                idUtilisateur
        );

        if (ok) {
            System.out.println("Réclamation ajoutée !");
        } else {
            System.out.println("Échec ajout réclamation !");
        }
    }

    public void afficherReclamations() {
        reclamationDAO.afficherReclamations();
    }

    public Object[][] getReclamationsParUtilisateur(int idUtilisateur) {
        return reclamationDAO.getReclamationsParUtilisateur(idUtilisateur);
    }
}
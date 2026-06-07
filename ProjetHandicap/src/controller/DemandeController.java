package controller;

import dao.DemandeDAO;

public class DemandeController {

    private DemandeDAO demandeDAO;

    public DemandeController() {
        demandeDAO = new DemandeDAO();
    }

    public void ajouterDemande(String typeDemande, String description, String dateSoumission,
                               String statut, String pieceJustificative, int idUtilisateur) {

        boolean ok = demandeDAO.ajouterDemande(
                typeDemande,
                description,
                dateSoumission,
                statut,
                pieceJustificative,
                idUtilisateur
        );

        if (ok) {
            System.out.println("Demande ajoutée !");
        } else {
            System.out.println("Échec ajout demande !");
        }
    }

    public void afficherDemandes() {
        demandeDAO.afficherDemandes();
    }

    public Object[][] getDemandesAdmin() {
        return demandeDAO.getDemandesAdmin();
    }

    public Object[][] getDemandesParUtilisateur(int idUtilisateur) {
        return demandeDAO.getDemandesParUtilisateur(idUtilisateur);
    }

    public void modifierStatutDemande(int idDemande, String nouveauStatut) {
        boolean ok = demandeDAO.modifierStatutDemande(idDemande, nouveauStatut);

        if (ok) {
            System.out.println("Statut demande modifié !");
        } else {
            System.out.println("Échec modification statut demande !");
        }
    }

    public void modifierDemande(int idDemande, String typeDemande, String description, String pieceJustificative) {
        boolean ok = demandeDAO.modifierDemande(idDemande, typeDemande, description, pieceJustificative);

        if (ok) {
            System.out.println("Demande modifiée !");
        } else {
            System.out.println("Modification impossible. La demande n'est pas EN_ATTENTE.");
        }
    }

    public void supprimerDemande(int idDemande) {
        boolean ok = demandeDAO.supprimerDemande(idDemande);

        if (ok) {
            System.out.println("Demande supprimée !");
        } else {
            System.out.println("Échec suppression demande !");
        }
    }

    public int compterToutesDemandesUtilisateur(int idUtilisateur) {
        return demandeDAO.compterToutesDemandesUtilisateur(idUtilisateur);
    }

    public int compterDemandesParStatut(int idUtilisateur, String statut) {
        return demandeDAO.compterDemandesParStatut(idUtilisateur, statut);
    }
}
package controller;

import dao.ArchiveDAO;

public class ArchiveController {

    private ArchiveDAO archiveDAO;

    public ArchiveController() {
        archiveDAO = new ArchiveDAO();
    }

    public void ajouterArchive(String typeDocument, String dateArchivage) {

        if (typeDocument == null || typeDocument.isEmpty()
                || dateArchivage == null || dateArchivage.isEmpty()) {
            System.out.println("Champs obligatoires manquants.");
            return;
        }

        boolean ok = archiveDAO.ajouterArchive(typeDocument, dateArchivage);

        if (ok) {
            System.out.println("Archive ajoutée avec succès !");
        } else {
            System.out.println("Erreur lors de l'ajout de l'archive.");
        }
    }

    public void afficherArchives() {
        archiveDAO.afficherArchives();
    }
}
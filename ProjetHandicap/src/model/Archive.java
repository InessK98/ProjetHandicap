package model;

public class Archive {

    private int idArchive;
    private String typeDocument;
    private String dateArchivage;

    public Archive() {
    }

    public Archive(int idArchive, String typeDocument, String dateArchivage) {
        this.idArchive = idArchive;
        this.typeDocument = typeDocument;
        this.dateArchivage = dateArchivage;
    }

    public void rechercherMulticritere() {
        System.out.println("Recherche multicritère...");
    }

    public void afficherHistorique() {
        System.out.println("Affichage historique...");
    }

    public int getIdArchive() {
        return idArchive;
    }

    public void setIdArchive(int idArchive) {
        this.idArchive = idArchive;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    public String getDateArchivage() {
        return dateArchivage;
    }

    public void setDateArchivage(String dateArchivage) {
        this.dateArchivage = dateArchivage;
    }
}
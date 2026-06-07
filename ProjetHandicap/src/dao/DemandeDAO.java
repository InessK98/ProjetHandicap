package dao;

import java.sql.*;
import java.util.ArrayList;

public class DemandeDAO {

    public boolean ajouterDemande(String typeDemande, String description, String dateSoumission,
                                  String statut, String pieceJustificative, int idUtilisateur) {

        String sql = "INSERT INTO demande(typeDemande, description, dateSoumission, statut, pieceJustificative, idUtilisateur) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, typeDemande);
            ps.setString(2, description);
            ps.setString(3, dateSoumission);
            ps.setString(4, statut);
            ps.setString(5, pieceJustificative);
            ps.setInt(6, idUtilisateur);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur ajout demande : " + e.getMessage());
            return false;
        }
    }

    public void afficherDemandes() {
        String sql = "SELECT * FROM demande";

        try (Connection cn = ConnexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("ID : " + rs.getInt("idDemande"));
                System.out.println("Type : " + rs.getString("typeDemande"));
                System.out.println("Description : " + rs.getString("description"));
                System.out.println("Date : " + rs.getString("dateSoumission"));
                System.out.println("Statut : " + rs.getString("statut"));
                System.out.println("Utilisateur ID : " + rs.getInt("idUtilisateur"));
                System.out.println("-------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Erreur affichage demandes : " + e.getMessage());
        }
    }

    public Object[][] getDemandesAdmin() {
        String sql =
                "SELECT d.idDemande, d.typeDemande, d.description, d.dateSoumission, d.statut, " +
                "u.nom, u.prenom " +
                "FROM demande d " +
                "JOIN utilisateur u ON d.idUtilisateur = u.id " +
                "ORDER BY d.idDemande DESC";

        ArrayList<Object[]> lignes = new ArrayList<>();

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String statut = rs.getString("statut");

                String assigneA;
                if (statut.equalsIgnoreCase("EN_ATTENTE")) {
                    assigneA = "Non assigné";
                } else {
                    assigneA = "Administrateur";
                }

                lignes.add(new Object[]{
                        rs.getInt("idDemande"),
                        rs.getString("prenom") + " " + rs.getString("nom"),
                        rs.getString("typeDemande"),
                        rs.getString("dateSoumission"),
                        statut,
                        assigneA,
                        "Voir détail"
                });
            }

        } catch (SQLException e) {
            System.out.println("Erreur chargement demandes admin : " + e.getMessage());
        }

        Object[][] data = new Object[lignes.size()][7];

        for (int i = 0; i < lignes.size(); i++) {
            data[i] = lignes.get(i);
        }

        return data;
    }

    public boolean modifierStatutDemande(int idDemande, String nouveauStatut) {
        String sql = "UPDATE demande SET statut=? WHERE idDemande=?";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nouveauStatut);
            ps.setInt(2, idDemande);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur modification statut demande : " + e.getMessage());
            return false;
        }
    }

    public boolean modifierDemande(int idDemande, String typeDemande, String description, String pieceJustificative) {
        String sql = "UPDATE demande SET typeDemande=?, description=?, pieceJustificative=? WHERE idDemande=? AND statut='EN_ATTENTE'";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, typeDemande);
            ps.setString(2, description);
            ps.setString(3, pieceJustificative);
            ps.setInt(4, idDemande);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur modification demande : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerDemande(int idDemande) {
        String sql = "DELETE FROM demande WHERE idDemande=?";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idDemande);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur suppression demande : " + e.getMessage());
            return false;
        }
    }

    public Object[][] getDemandesParUtilisateur(int idUtilisateur) {
        String sql = "SELECT idDemande, typeDemande, description, dateSoumission, statut FROM demande WHERE idUtilisateur=?";

        ArrayList<Object[]> lignes = new ArrayList<>();

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lignes.add(new Object[]{
                        rs.getInt("idDemande"),
                        rs.getString("typeDemande"),
                        rs.getString("description"),
                        rs.getString("dateSoumission"),
                        rs.getString("statut")
                });
            }

        } catch (SQLException e) {
            System.out.println("Erreur chargement demandes étudiant : " + e.getMessage());
        }

        Object[][] data = new Object[lignes.size()][5];

        for (int i = 0; i < lignes.size(); i++) {
            data[i] = lignes.get(i);
        }

        return data;
    }

    public int compterToutesDemandesUtilisateur(int idUtilisateur) {
        String sql = "SELECT COUNT(*) FROM demande WHERE idUtilisateur=?";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("Erreur count demandes : " + e.getMessage());
        }

        return 0;
    }

    public int compterDemandesParStatut(int idUtilisateur, String statut) {
        String sql = "SELECT COUNT(*) FROM demande WHERE idUtilisateur=? AND statut=?";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUtilisateur);
            ps.setString(2, statut);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("Erreur count statut : " + e.getMessage());
        }

        return 0;
    }
}
package dao;

import java.sql.*;
import java.util.ArrayList;

public class ReclamationDAO {

    public boolean ajouterReclamation(String motif, String description, String dateReclamation,
                                      String statut, int idUtilisateur) {

        String sql = "INSERT INTO reclamation(motif, description, dateReclamation, statut, idUtilisateur) VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, motif);
            ps.setString(2, description);
            ps.setString(3, dateReclamation);
            ps.setString(4, statut);
            ps.setInt(5, idUtilisateur);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur ajout réclamation : " + e.getMessage());
            return false;
        }
    }

    public void afficherReclamations() {
        String sql = "SELECT * FROM reclamation";

        try (Connection cn = ConnexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("ID : " + rs.getInt("idReclamation"));
                System.out.println("Motif : " + rs.getString("motif"));
                System.out.println("Description : " + rs.getString("description"));
                System.out.println("Date : " + rs.getString("dateReclamation"));
                System.out.println("Statut : " + rs.getString("statut"));
                System.out.println("Utilisateur ID : " + rs.getInt("idUtilisateur"));
                System.out.println("-------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Erreur affichage réclamations : " + e.getMessage());
        }
    }

    public Object[][] getReclamationsParUtilisateur(int idUtilisateur) {
        String sql = "SELECT idReclamation, motif, dateReclamation, statut FROM reclamation WHERE idUtilisateur=?";

        ArrayList<Object[]> lignes = new ArrayList<>();

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lignes.add(new Object[]{
                        "REC-" + rs.getInt("idReclamation"),
                        rs.getString("motif"),
                        rs.getString("dateReclamation"),
                        rs.getString("statut")
                });
            }

        } catch (SQLException e) {
            System.out.println("Erreur chargement réclamations étudiant : " + e.getMessage());
        }

        Object[][] data = new Object[lignes.size()][4];

        for (int i = 0; i < lignes.size(); i++) {
            data[i] = lignes.get(i);
        }

        return data;
    }
}
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDAO {

    public int compterToutesDemandes() {
        return compter("SELECT COUNT(*) FROM demande");
    }

    public int compterDemandesParStatut(String statut) {

        String sql = "SELECT COUNT(*) FROM demande WHERE statut=?";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, statut);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Erreur count statut : " + e.getMessage());
        }

        return 0;
    }

    public int[] compterDemandesParMois() {

        int[] mois = new int[12];

        String sql =
                "SELECT MONTH(dateSoumission) AS mois, COUNT(*) AS total " +
                "FROM demande GROUP BY MONTH(dateSoumission)";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int m = rs.getInt("mois");
                int total = rs.getInt("total");

                if (m >= 1 && m <= 12) {
                    mois[m - 1] = total;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur stats par mois : " + e.getMessage());
        }

        return mois;
    }

    public Map<String, Integer> compterDemandesParType() {

        Map<String, Integer> result = new LinkedHashMap<>();

        String sql =
                "SELECT typeDemande, COUNT(*) AS total " +
                "FROM demande GROUP BY typeDemande";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                result.put(
                        rs.getString("typeDemande"),
                        rs.getInt("total")
                );
            }

        } catch (SQLException e) {
            System.out.println("Erreur stats par type : " + e.getMessage());
        }

        return result;
    }

    // ==========================
    // DEMANDES RÉCENTES SQL
    // ==========================

    public Object[][] getDemandesRecentes() {

        String sql =
                "SELECT d.idDemande, d.typeDemande, d.dateSoumission, d.statut, " +
                "u.nom, u.prenom " +
                "FROM demande d " +
                "JOIN utilisateur u ON d.idUtilisateur = u.id " +
                "ORDER BY d.idDemande DESC";

        ArrayList<Object[]> lignes = new ArrayList<>();

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                lignes.add(new Object[]{

                        rs.getString("prenom") + " " + rs.getString("nom"),
                        rs.getString("typeDemande"),
                        rs.getString("dateSoumission"),
                        rs.getString("statut"),
                        "⋮"
                });
            }

        } catch (SQLException e) {
            System.out.println("Erreur demandes récentes : " + e.getMessage());
        }

        Object[][] data = new Object[lignes.size()][5];

        for (int i = 0; i < lignes.size(); i++) {
            data[i] = lignes.get(i);
        }

        return data;
    }

    // ==========================
    // MÉTHODE GÉNÉRALE COUNT
    // ==========================

    private int compter(String sql) {

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Erreur count dashboard : " + e.getMessage());
        }

        return 0;
    }
}
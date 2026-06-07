package dao;

import java.sql.*;

public class ArchiveDAO {

    public boolean ajouterArchive(String typeDocument, String dateArchivage) {

        String sql = "INSERT INTO archive(typeDocument, dateArchivage) VALUES (?, ?)";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, typeDocument);
            ps.setString(2, dateArchivage);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur ajout archive : " + e.getMessage());
            return false;
        }
    }

    public void afficherArchives() {

        String sql = "SELECT * FROM archive";

        try (Connection cn = ConnexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("ID : " + rs.getInt("idArchive"));
                System.out.println("Type document : " + rs.getString("typeDocument"));
                System.out.println("Date archivage : " + rs.getString("dateArchivage"));
                System.out.println("----------------------");
            }

        } catch (SQLException e) {
            System.out.println("Erreur affichage : " + e.getMessage());
        }
    }
    
}
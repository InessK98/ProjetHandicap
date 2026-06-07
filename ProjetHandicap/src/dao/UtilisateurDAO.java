package dao;

import model.Utilisateur;
import java.sql.*;

public class UtilisateurDAO {

    public boolean ajouterUtilisateur(Utilisateur utilisateur, String role, String typeHandicap) {
        String sql = "INSERT INTO utilisateur(nom, prenom, email, motDePasse, role, typeHandicap) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, utilisateur.getNom());
            ps.setString(2, utilisateur.getPrenom());
            ps.setString(3, utilisateur.getEmail());
            ps.setString(4, utilisateur.getMotDePasse());
            ps.setString(5, role);
            ps.setString(6, typeHandicap);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur ajout utilisateur : " + e.getMessage());
            return false;
        }
    }

    public void afficherUtilisateurs() {
        String sql = "SELECT * FROM utilisateur";

        try (Connection cn = ConnexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("ID : " + rs.getInt("id"));
                System.out.println("Nom : " + rs.getString("nom"));
                System.out.println("Prenom : " + rs.getString("prenom"));
                System.out.println("Email : " + rs.getString("email"));
                System.out.println("Role : " + rs.getString("role"));
                System.out.println("Type handicap : " + rs.getString("typeHandicap"));
                System.out.println("-------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Erreur affichage utilisateurs : " + e.getMessage());
        }
    }

    public boolean modifierUtilisateur(int id, String nom, String prenom, String email) {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, email=? WHERE id=?";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nom);
            ps.setString(2, prenom);
            ps.setString(3, email);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur modification utilisateur : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerUtilisateur(int id) {
        String sql = "DELETE FROM utilisateur WHERE id=?";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Erreur suppression utilisateur : " + e.getMessage());
            return false;
        }
    }

    public String connecterUtilisateur(String email, String motDePasse) {
        String sql = "SELECT role FROM utilisateur WHERE email=? AND motDePasse=?";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, motDePasse);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }

        } catch (SQLException e) {
            System.out.println("Erreur connexion utilisateur : " + e.getMessage());
        }

        return null;
    }

    public int getIdParEmail(String email) {
        String sql = "SELECT id FROM utilisateur WHERE email=?";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            System.out.println("Erreur récupération id : " + e.getMessage());
        }

        return -1;
    }

    public String getNomCompletParEmail(String email) {
        String sql = "SELECT nom, prenom FROM utilisateur WHERE email=?";

        try (Connection cn = ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("prenom") + " " + rs.getString("nom");
            }

        } catch (SQLException e) {
            System.out.println("Erreur récupération nom : " + e.getMessage());
        }

        return email;
    }
}
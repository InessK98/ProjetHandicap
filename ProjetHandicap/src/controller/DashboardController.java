package controller;

import dao.DashboardDAO;
import java.util.Map;

public class DashboardController {

    private DashboardDAO dashboardDAO;

    public DashboardController() {
        dashboardDAO = new DashboardDAO();
    }

    public int getTotalDemandes() {
        return dashboardDAO.compterToutesDemandes();
    }

    public int getDemandesEnCours() {
        return dashboardDAO.compterDemandesParStatut("EN_COURS");
    }

    public int getDemandesAcceptees() {
        return dashboardDAO.compterDemandesParStatut("ACCEPTEE");
    }

    public int getDemandesRefusees() {
        return dashboardDAO.compterDemandesParStatut("REFUSEE");
    }

    public int[] getDemandesParMois() {
        return dashboardDAO.compterDemandesParMois();
    }

    public Map<String, Integer> getDemandesParType() {
        return dashboardDAO.compterDemandesParType();
    }

    public Object[][] getDemandesRecentes() {
        return dashboardDAO.getDemandesRecentes();
    }

    public void afficherStatistiques() {
        System.out.println("===== TABLEAU DE BORD =====");
        System.out.println("Total demandes : " + getTotalDemandes());
        System.out.println("En cours : " + getDemandesEnCours());
        System.out.println("Acceptées : " + getDemandesAcceptees());
        System.out.println("Refusées : " + getDemandesRefusees());
    }
}
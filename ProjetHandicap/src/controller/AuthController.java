package controller;

import dao.UtilisateurDAO;

public class AuthController {

    private UtilisateurDAO utilisateurDAO;

    public AuthController() {
        utilisateurDAO = new UtilisateurDAO();
    }

    public String seConnecter(String email, String motDePasse) {

        if(email == null || email.isEmpty()
                || motDePasse == null || motDePasse.isEmpty()){

            return null;
        }

        return utilisateurDAO.connecterUtilisateur(email, motDePasse);
    }
}
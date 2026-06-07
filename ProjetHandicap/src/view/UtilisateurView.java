package view;

import controller.UtilisateurController;
import model.Utilisateur;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UtilisateurView extends JFrame {

    private static final Color C_SURFACE = new Color(0xFCF9F4);
    private static final Color C_SURFACE_LOW = new Color(0xF6F3EE);
    private static final Color C_SURFACE_CONT = new Color(0xF0EDE9);
    private static final Color C_PRIMARY = new Color(0x445343);
    private static final Color C_ERROR = new Color(0xBA1A1A);
    private static final Color C_TEXT = new Color(0x1C1C19);

    private final UtilisateurController ctrl = new UtilisateurController();

    private JTextField tfNom, tfPrenom, tfEmail, tfRole, tfHandicap;
    private JPasswordField tfMdp;
    private JLabel lblStatus;
    private JTable table;

    public UtilisateurView() {
        setTitle("AccèsU — Gestion des utilisateurs");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(960, 660);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_SURFACE);

        JLabel title = new JLabel("Gestion des utilisateurs");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(24, 32, 20, 32));
        title.setForeground(C_TEXT);

        root.add(title, BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(20, 0));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(0, 24, 24, 24));

        String[] cols = {"ID", "Nom", "Prénom", "Email", "Rôle", "Type handicap"};

        Object[][] data = {
                {1, "Sara", "Amine", "sara@gmail.com", "ADMIN", "—"},
                {2, "Ali", "Hassan", "ali@gmail.com", "ETUDIANT", "Visuel"}
        };

        table = new JTable(data, cols);
        table.setRowHeight(34);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                fillFormFromRow(table.getSelectedRow());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(560, 0));

        center.add(scroll, BorderLayout.CENTER);
        center.add(buildFormPanel(), BorderLayout.EAST);

        return center;
    }

    private JPanel buildFormPanel() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setPreferredSize(new Dimension(310, 0));

        JLabel formTitle = new JLabel("Informations utilisateur");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(formTitle);
        card.add(Box.createVerticalStrut(18));

        tfNom = addField(card, "Nom");
        tfPrenom = addField(card, "Prénom");
        tfEmail = addField(card, "Email");
        tfMdp = addPasswordField(card, "Mot de passe");
        tfRole = addField(card, "Rôle : ADMIN ou ETUDIANT");
        tfHandicap = addField(card, "Type de handicap");

        card.add(Box.createVerticalStrut(18));

        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setOpaque(false);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JButton btnAjouter = buildPrimaryButton("Ajouter");
        JButton btnModifier = buildPrimaryButton("Modifier");
        JButton btnSupprimer = buildDangerButton("Supprimer");
        JButton btnReset = buildSecondaryButton("Réinitialiser");

        btnAjouter.addActionListener(e -> handleAjout());
        btnModifier.addActionListener(e -> handleModifier());
        btnSupprimer.addActionListener(e -> handleSupprimer());
        btnReset.addActionListener(e -> resetForm());

        buttons.add(btnAjouter);
        buttons.add(btnModifier);
        buttons.add(btnSupprimer);
        buttons.add(btnReset);

        card.add(buttons);
        card.add(Box.createVerticalStrut(14));

        JButton btnLister = buildSecondaryButton("Actualiser la liste");
        btnLister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnLister.addActionListener(e -> ctrl.afficherUtilisateurs());
        card.add(btnLister);

        card.add(Box.createVerticalStrut(14));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblStatus);

        return card;
    }

    private JTextField addField(JPanel parent, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField tf = new JTextField();
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setBackground(C_SURFACE_LOW);
        tf.setBorder(new EmptyBorder(8, 12, 8, 12));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));
        parent.add(tf);
        parent.add(Box.createVerticalStrut(10));

        return tf;
    }

    private JPasswordField addPasswordField(JPanel parent, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        pf.setBackground(C_SURFACE_LOW);
        pf.setBorder(new EmptyBorder(8, 12, 8, 12));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);

        parent.add(lbl);
        parent.add(Box.createVerticalStrut(4));
        parent.add(pf);
        parent.add(Box.createVerticalStrut(10));

        return pf;
    }

    private void handleAjout() {
        String nom = tfNom.getText().trim();
        String prenom = tfPrenom.getText().trim();
        String email = tfEmail.getText().trim();
        String mdp = new String(tfMdp.getPassword()).trim();
        String role = tfRole.getText().trim();
        String handicap = tfHandicap.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || mdp.isEmpty() || role.isEmpty()) {
            setStatus("Champs obligatoires manquants.", false);
            return;
        }

        Utilisateur u = new Utilisateur(0, nom, prenom, email, mdp) {};
        ctrl.ajouterUtilisateur(u, role, handicap.isEmpty() ? null : handicap);

        setStatus("Utilisateur ajouté.", true);
        resetForm();
    }

    private void handleModifier() {
        int row = table.getSelectedRow();

        if (row < 0) {
            setStatus("Sélectionnez un utilisateur.", false);
            return;
        }

        int id = (int) table.getValueAt(row, 0);

        ctrl.modifierUtilisateur(
                id,
                tfNom.getText().trim(),
                tfPrenom.getText().trim(),
                tfEmail.getText().trim()
        );

        setStatus("Utilisateur modifié.", true);
    }

    private void handleSupprimer() {
        int row = table.getSelectedRow();

        if (row < 0) {
            setStatus("Sélectionnez un utilisateur.", false);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Supprimer cet utilisateur ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) table.getValueAt(row, 0);
            ctrl.supprimerUtilisateur(id);
            setStatus("Utilisateur supprimé.", true);
            resetForm();
        }
    }

    private void fillFormFromRow(int row) {
        tfNom.setText(table.getValueAt(row, 1).toString());
        tfPrenom.setText(table.getValueAt(row, 2).toString());
        tfEmail.setText(table.getValueAt(row, 3).toString());
        tfRole.setText(table.getValueAt(row, 4).toString());

        String handicap = table.getValueAt(row, 5).toString();
        tfHandicap.setText(handicap.equals("—") ? "" : handicap);

        tfMdp.setText("");
    }

    private void resetForm() {
        tfNom.setText("");
        tfPrenom.setText("");
        tfEmail.setText("");
        tfMdp.setText("");
        tfRole.setText("");
        tfHandicap.setText("");
        table.clearSelection();
    }

    private void setStatus(String msg, boolean ok) {
        lblStatus.setText(msg);
        lblStatus.setForeground(ok ? C_PRIMARY : C_ERROR);
    }

    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(C_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buildDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(0xF5E8E8));
        btn.setForeground(C_ERROR);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buildSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(C_SURFACE_CONT);
        btn.setForeground(C_TEXT);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UtilisateurView().setVisible(true));
    }
}
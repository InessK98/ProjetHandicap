package view;

import controller.DemandeController;
import controller.ReclamationController;
import controller.UtilisateurController;
import dao.UtilisateurDAO;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EtudiantView extends JFrame {

    // =========================================================
    // PALETTE — légèrement plus douce que l'admin pour distinction
    // =========================================================
    private static final Color BG          = new Color(0xFCF9F4);
    private static final Color SIDEBAR     = new Color(0x445343);       // un peu plus clair que admin
    private static final Color SIDEBAR_HOV = new Color(0x5C6B5A);
    private static final Color SIDEBAR_TXT = new Color(0xE9E5DC);
    private static final Color CARD        = Color.WHITE;
    private static final Color PRIMARY     = new Color(0x445343);
    private static final Color PRIMARY_LT  = new Color(0x5C6B5A);
    private static final Color TEXT        = new Color(0x1C1C19);
    private static final Color MUTED       = new Color(0x6B6F69);
    private static final Color SOFT        = new Color(0xF3F0EA);
    private static final Color BORDER      = new Color(0xE5E1D8);
    private static final Color GOLD        = new Color(0xB8922A);
    private static final Color RED         = new Color(0xBA1A1A);
    private static final Color GREEN_BADGE = new Color(0xE8F2EC);
    private static final Color YELLOW_BADGE= new Color(0xFBF2E0);
    private static final Color RED_BADGE   = new Color(0xF5E8E8);

    // =========================================================
    // CONTROLLERS
    // =========================================================
    private final DemandeController     demandeCtrl     = new DemandeController();
    private final ReclamationController reclamationCtrl = new ReclamationController();
    private final UtilisateurController utilisateurCtrl = new UtilisateurController();
    private final UtilisateurDAO        utilisateurDAO  = new UtilisateurDAO();

    // =========================================================
    // STATE
    // =========================================================
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     pages      = new JPanel(cardLayout);

    private final ArrayList<JButton> navButtons = new ArrayList<>();
    private JButton currentNav;
    private JPanel pageAccueil;

    private int    idUtilisateur;
    private String nomEtudiant;
    private String emailEtudiant;

    private JTable tableDemandes;
    private JTable tableReclamations;

    // =========================================================
    // CONSTRUCTEUR
    // =========================================================
    public EtudiantView(String email) {
        this.emailEtudiant = email;
        this.idUtilisateur = utilisateurDAO.getIdParEmail(email);
        this.nomEtudiant   = utilisateurDAO.getNomCompletParEmail(email);

        setTitle("AccèsU — Espace Étudiant");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1180, 720));
        setLocationRelativeTo(null);

        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildSidebar(), BorderLayout.WEST);

        pages.setBackground(BG);
        pageAccueil = buildAccueilPage();
        pages.add(pageAccueil,              "accueil");
        pages.add(buildDemandesPage(),      "demandes");
        pages.add(buildReclamationsPage(),  "reclamations");
        pages.add(buildProfilPage(),        "profil");

        root.add(pages, BorderLayout.CENTER);
        setContentPane(root);

        switchTo("accueil", navButtons.get(0));
    }

    // =========================================================
    // SIDEBAR
    // =========================================================
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setBorder(new EmptyBorder(28, 0, 24, 0));

        // HEADER
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(0, 26, 28, 26));

        JLabel logo = new JLabel("AccèsU");
        logo.setFont(new Font("SansSerif", Font.BOLD, 26));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Espace étudiant · UIR");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(0xCFD6CB));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(logo);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        // NAV
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(8, 14, 8, 14));

        JLabel section = new JLabel("  MENU");
        section.setFont(new Font("SansSerif", Font.BOLD, 10));
        section.setForeground(new Color(0xB8C0B5));
        section.setBorder(new EmptyBorder(0, 6, 10, 0));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        nav.add(section);

        nav.add(makeNavButton("Accueil",          "accueil"));
        nav.add(Box.createVerticalStrut(4));
        nav.add(makeNavButton("Mes demandes",     "demandes"));
        nav.add(Box.createVerticalStrut(4));
        nav.add(makeNavButton("Mes réclamations", "reclamations"));
        nav.add(Box.createVerticalStrut(4));
        nav.add(makeNavButton("Mon profil",       "profil"));

        // BOTTOM
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(20, 22, 0, 22));

        JPanel userCard = new JPanel(new BorderLayout());
        userCard.setBackground(SIDEBAR_HOV);
        userCard.setBorder(new EmptyBorder(12, 14, 12, 14));

        String initiale = (nomEtudiant != null && !nomEtudiant.isEmpty())
                ? nomEtudiant.substring(0, 1).toUpperCase()
                : "E";

        JLabel avatar = new JLabel(initiale);
        avatar.setOpaque(true);
        avatar.setBackground(GOLD);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("SansSerif", Font.BOLD, 16));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(36, 36));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(0, 10, 0, 0));

        JLabel n = new JLabel(nomEtudiant != null ? nomEtudiant : "Étudiant");
        n.setForeground(Color.WHITE);
        n.setFont(new Font("SansSerif", Font.BOLD, 13));

        JLabel r = new JLabel("Étudiant");
        r.setForeground(new Color(0xCFD6CB));
        r.setFont(new Font("SansSerif", Font.PLAIN, 11));

        info.add(n);
        info.add(r);

        userCard.add(avatar, BorderLayout.WEST);
        userCard.add(info,   BorderLayout.CENTER);

        JButton logout = new JButton("Se déconnecter");
        logout.setFont(new Font("SansSerif", Font.BOLD, 13));
        logout.setForeground(Color.WHITE);
        logout.setBackground(SIDEBAR);
        logout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x6E7B6C), 1),
                new EmptyBorder(9, 14, 9, 14)
        ));
        logout.setFocusPainted(false);
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Confirmer la déconnexion ?",
                    "Déconnexion", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                dispose();
                new ConnexionView().setVisible(true);
            }
        });

        bottom.add(userCard);
        bottom.add(Box.createVerticalStrut(14));
        bottom.add(logout);

        sidebar.add(header, BorderLayout.NORTH);
        sidebar.add(nav,    BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton makeNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(SIDEBAR_TXT);
        btn.setBackground(SIDEBAR);
        btn.setBorder(new EmptyBorder(11, 16, 11, 16));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != currentNav) btn.setBackground(SIDEBAR_HOV);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != currentNav) btn.setBackground(SIDEBAR);
            }
        });

        btn.addActionListener(e -> switchTo(cardName, btn));
        navButtons.add(btn);
        return btn;
    }

    private void switchTo(String card, JButton clicked) {
        if (currentNav != null) {
            currentNav.setBackground(SIDEBAR);
            currentNav.setForeground(SIDEBAR_TXT);
            currentNav.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }
        currentNav = clicked;
        clicked.setBackground(SIDEBAR_HOV);
        clicked.setForeground(Color.WHITE);
        clicked.setFont(new Font("SansSerif", Font.BOLD, 14));

        cardLayout.show(pages, card);

        switch (card) {
            case "accueil":      rafraichirAccueil(); break;
            case "demandes":     rechargerDemandes(); break;
            case "reclamations": rechargerReclamations(); break;
        }
    }

    // =========================================================
    // PAGE 1 — ACCUEIL
    // =========================================================
    private JPanel buildAccueilPage() {
        JPanel page = new JPanel(new BorderLayout(0, 22));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 36, 28, 36));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel breadcrumb = new JLabel("AccèsU  ›  Espace étudiant  ›  Accueil");
        breadcrumb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        breadcrumb.setForeground(MUTED);

        JLabel title = new JLabel("Bonjour, " + (nomEtudiant != null ? nomEtudiant : "Étudiant") + " 👋");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(TEXT);

        JLabel sub = new JLabel("Voici un aperçu de vos demandes et réclamations");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(MUTED);

        titleBox.add(breadcrumb);
        titleBox.add(Box.createVerticalStrut(8));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton btnRefresh = secondaryButton("Actualiser");
        btnRefresh.addActionListener(e -> rafraichirAccueil());

        JButton btnNouvelle = primaryButton("+ Nouvelle demande");
        btnNouvelle.addActionListener(e -> ouvrirDialogAjoutDemande());

        JLabel date = new JLabel(new SimpleDateFormat("EEEE d MMMM yyyy", java.util.Locale.FRENCH)
                .format(new Date()));
        date.setFont(new Font("SansSerif", Font.PLAIN, 13));
        date.setForeground(MUTED);
        date.setBorder(new EmptyBorder(8, 0, 0, 12));

        right.add(date);
        right.add(btnRefresh);
        right.add(btnNouvelle);

        header.add(titleBox, BorderLayout.WEST);
        header.add(right,    BorderLayout.EAST);

        // Body
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        // KPI Cards
        int total     = demandeCtrl.compterToutesDemandesUtilisateur(idUtilisateur);
        int enAttente = demandeCtrl.compterDemandesParStatut(idUtilisateur, "EN_ATTENTE");
        int acceptees = demandeCtrl.compterDemandesParStatut(idUtilisateur, "ACCEPTEE");
        int refusees  = demandeCtrl.compterDemandesParStatut(idUtilisateur, "REFUSEE");

        JPanel kpis = new JPanel(new GridLayout(1, 4, 18, 0));
        kpis.setOpaque(false);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));
        kpis.add(kpiCard("Mes demandes",  String.valueOf(total),     "Total soumises",    PRIMARY));
        kpis.add(kpiCard("En attente",    String.valueOf(enAttente), "Pas encore traitée", GOLD));
        kpis.add(kpiCard("Acceptées",     String.valueOf(acceptees), "Demandes validées",  new Color(0x2E7D32)));
        kpis.add(kpiCard("Refusées",      String.valueOf(refusees),  "Demandes rejetées",  RED));

        // Dernières demandes
        JPanel recents = panelDernieresDemandes();

        body.add(kpis);
        body.add(Box.createVerticalStrut(22));
        body.add(recents);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        page.add(header, BorderLayout.NORTH);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private JPanel kpiCard(String title, String value, String subtitle, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorderRounded(BORDER, 1, 12),
                new EmptyBorder(20, 22, 20, 22)
        ));

        JPanel left = new JPanel();
        left.setBackground(accent);
        left.setPreferredSize(new Dimension(4, 0));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(0, 14, 0, 0));

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(new Font("SansSerif", Font.BOLD, 11));
        t.setForeground(MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 34));
        v.setForeground(TEXT);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("SansSerif", Font.PLAIN, 12));
        s.setForeground(MUTED);

        content.add(t);
        content.add(Box.createVerticalStrut(8));
        content.add(v);
        content.add(Box.createVerticalStrut(4));
        content.add(s);

        card.add(left,    BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel panelDernieresDemandes() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorderRounded(BORDER, 1, 12),
                new EmptyBorder(22, 26, 22, 26)
        ));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);

        JPanel headLeft = new JPanel();
        headLeft.setOpaque(false);
        headLeft.setLayout(new BoxLayout(headLeft, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Mes demandes récentes");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(TEXT);

        JLabel sub = new JLabel("Suivez l'état de vos dernières soumissions");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MUTED);

        headLeft.add(title);
        headLeft.add(Box.createVerticalStrut(2));
        headLeft.add(sub);

        JButton voirTout = secondaryButton("Voir toutes");
        voirTout.addActionListener(e -> switchTo("demandes", navButtons.get(1)));

        head.add(headLeft, BorderLayout.WEST);
        head.add(voirTout, BorderLayout.EAST);

        String[] cols = {"ID", "Type", "Description", "Date", "Statut"};
        Object[][] data = demandeCtrl.getDemandesParUtilisateur(idUtilisateur);

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(4).setCellRenderer(new StatutRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(0, 280));
        scroll.getViewport().setBackground(CARD);

        card.add(head,   BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void rafraichirAccueil() {
        pages.remove(pageAccueil);
        pageAccueil = buildAccueilPage();
        pages.add(pageAccueil, "accueil");
        cardLayout.show(pages, "accueil");
        pages.revalidate();
        pages.repaint();
    }

    // =========================================================
    // PAGE 2 — MES DEMANDES
    // =========================================================
    private JPanel buildDemandesPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 36, 28, 36));

        JPanel header = pageHeader(
                "Mes demandes",
                "Consultez l'état de vos demandes ou soumettez-en une nouvelle"
        );

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JLabel info = new JLabel("Liste complète de vos demandes");
        info.setFont(new Font("SansSerif", Font.BOLD, 13));
        info.setForeground(PRIMARY);
        info.setBorder(new EmptyBorder(6, 4, 0, 0));

        JButton btnNouvelle = primaryButton("+ Nouvelle demande");
        btnNouvelle.addActionListener(e -> ouvrirDialogAjoutDemande());

        JButton btnRefresh = secondaryButton("Actualiser");
        btnRefresh.addActionListener(e -> rechargerDemandes());

        JPanel actionsTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionsTop.setOpaque(false);
        actionsTop.add(btnRefresh);
        actionsTop.add(btnNouvelle);

        toolbar.add(info,       BorderLayout.WEST);
        toolbar.add(actionsTop, BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "Type", "Description", "Date", "Statut"};
        DefaultTableModel model = new DefaultTableModel(
                demandeCtrl.getDemandesParUtilisateur(idUtilisateur), cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableDemandes = new JTable(model);
        styleTable(tableDemandes);
        tableDemandes.getColumnModel().getColumn(0).setMaxWidth(60);
        tableDemandes.getColumnModel().getColumn(4).setCellRenderer(new StatutRenderer());

        JScrollPane scroll = new JScrollPane(tableDemandes);
        scroll.setBorder(new LineBorderRounded(BORDER, 1, 12));
        scroll.getViewport().setBackground(CARD);

        // Actions bas
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnVoir     = secondaryButton("Voir détail");
        JButton btnModifier = secondaryButton("Modifier");
        JButton btnSuppr    = dangerButton("Supprimer");

        btnVoir.addActionListener(e -> voirDetailDemande());
        btnModifier.addActionListener(e -> modifierDemandeSelectionnee());
        btnSuppr.addActionListener(e -> supprimerDemandeSelectionnee());

        actions.add(btnVoir);
        actions.add(btnModifier);
        actions.add(btnSuppr);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(toolbar, BorderLayout.NORTH);
        center.add(scroll,  BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        page.add(header, BorderLayout.NORTH);
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private void rechargerDemandes() {
        if (tableDemandes == null) return;
        String[] cols = {"ID", "Type", "Description", "Date", "Statut"};
        DefaultTableModel m = new DefaultTableModel(
                demandeCtrl.getDemandesParUtilisateur(idUtilisateur), cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableDemandes.setModel(m);
        tableDemandes.getColumnModel().getColumn(0).setMaxWidth(60);
        tableDemandes.getColumnModel().getColumn(4).setCellRenderer(new StatutRenderer());
    }

    private void ouvrirDialogAjoutDemande() {
        String[] types = {
                "Aménagement examen", "Accessibilité", "Accompagnement",
                "Support adapté", "Logiciel adapté", "Autre"
        };
        JComboBox<String> cType = new JComboBox<>(types);
        JTextArea  tDesc = new JTextArea(5, 26);
        tDesc.setLineWrap(true);
        tDesc.setWrapStyleWord(true);
        JTextField tPiece = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(new JLabel("Type de demande :"));
        form.add(cType);
        form.add(new JLabel("Description :"));
        form.add(new JScrollPane(tDesc));
        form.add(new JLabel("Pièce justificative (nom du fichier) :"));
        form.add(tPiece);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Nouvelle demande", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String description = tDesc.getText().trim();
        if (description.isEmpty()) {
            info("Veuillez saisir une description.");
            return;
        }

        String dateSoumission = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        demandeCtrl.ajouterDemande(
                (String) cType.getSelectedItem(),
                description,
                dateSoumission,
                "EN_ATTENTE",
                tPiece.getText().trim(),
                idUtilisateur
        );

        info("Demande soumise avec succès. Elle est en attente de traitement.");
        rechargerDemandes();
    }

    private void voirDetailDemande() {
        int row = tableDemandes.getSelectedRow();
        if (row < 0) { info("Sélectionnez une demande."); return; }

        int id = Integer.parseInt(tableDemandes.getValueAt(row, 0).toString());

        String description = "";
        String piece = "";

        String sql = "SELECT description, pieceJustificative FROM demande WHERE idDemande = ?";
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    description = rs.getString("description") != null ? rs.getString("description") : "—";
                    piece       = rs.getString("pieceJustificative") != null ? rs.getString("pieceJustificative") : "—";
                }
            }
        } catch (Exception ex) {
            System.out.println("Erreur détail demande : " + ex.getMessage());
        }

        // Séparer description originale et motif refus
        String motifRefus = null;
        String marqueur = "[MOTIF REFUS]";
        if (description.contains(marqueur)) {
            int idx = description.indexOf(marqueur);
            motifRefus = description.substring(idx + marqueur.length()).trim();
            description = description.substring(0, idx).trim();
        }

        String descHtml  = description.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        String pieceHtml = piece.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");

        StringBuilder sb = new StringBuilder("<html><body style='width:400px; font-family:sans-serif;'>");
        sb.append("<h2 style='margin:0;color:#445343;'>Détail de ma demande</h2>");
        sb.append("<hr style='border:0;border-top:1px solid #ddd;margin:10px 0;'>");
        sb.append("<p><b>ID :</b> #").append(id).append("</p>");
        sb.append("<p><b>Type :</b> ").append(tableDemandes.getValueAt(row, 1)).append("</p>");
        sb.append("<p><b>Date de soumission :</b> ").append(tableDemandes.getValueAt(row, 3)).append("</p>");
        sb.append("<p><b>Statut :</b> ").append(tableDemandes.getValueAt(row, 4)).append("</p>");
        sb.append("<hr style='border:0;border-top:1px solid #ddd;margin:10px 0;'>");
        sb.append("<p><b>Description :</b><br><span style='color:#444;'>").append(descHtml).append("</span></p>");
        sb.append("<p><b>Pièce justificative :</b><br><span style='color:#445343;'>").append(pieceHtml).append("</span></p>");

        // Si refusée : afficher le motif dans une zone rouge
        if (motifRefus != null && !motifRefus.isEmpty()) {
            String motifHtml = motifRefus.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
            sb.append("<hr style='border:0;border-top:1px solid #ddd;margin:10px 0;'>");
            sb.append("<div style='background-color:#F5E8E8;padding:10px;border-left:4px solid #BA1A1A;'>");
            sb.append("<b style='color:#BA1A1A;'>⚠ Motif du refus :</b><br>");
            sb.append("<span style='color:#5C1010;'>").append(motifHtml).append("</span>");
            sb.append("</div>");
        }

        sb.append("</body></html>");

        JOptionPane.showMessageDialog(this, sb.toString(),
                "Détail de la demande #" + id, JOptionPane.INFORMATION_MESSAGE);
    }

    private void modifierDemandeSelectionnee() {
        int row = tableDemandes.getSelectedRow();
        if (row < 0) { info("Sélectionnez une demande."); return; }

        int id = Integer.parseInt(tableDemandes.getValueAt(row, 0).toString());
        String typeActuel = tableDemandes.getValueAt(row, 1).toString();
        String descActuelle = tableDemandes.getValueAt(row, 2) != null
                ? tableDemandes.getValueAt(row, 2).toString() : "";
        String statut = tableDemandes.getValueAt(row, 4).toString();

        if (!statut.equalsIgnoreCase("EN_ATTENTE")) {
            info("Vous ne pouvez modifier qu'une demande EN_ATTENTE.\n"
               + "Statut actuel : " + statut);
            return;
        }

        String[] types = {
                "Aménagement examen", "Accessibilité", "Accompagnement",
                "Support adapté", "Logiciel adapté", "Autre"
        };
        JComboBox<String> cType = new JComboBox<>(types);
        cType.setSelectedItem(typeActuel);

        JTextArea tDesc = new JTextArea(descActuelle, 5, 26);
        tDesc.setLineWrap(true);
        tDesc.setWrapStyleWord(true);
        JTextField tPiece = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(new JLabel("Type de demande :"));
        form.add(cType);
        form.add(new JLabel("Description :"));
        form.add(new JScrollPane(tDesc));
        form.add(new JLabel("Pièce justificative :"));
        form.add(tPiece);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Modifier ma demande #" + id,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            demandeCtrl.modifierDemande(id,
                    (String) cType.getSelectedItem(),
                    tDesc.getText().trim(),
                    tPiece.getText().trim());
            rechargerDemandes();
            info("Demande modifiée avec succès.");
        }
    }

    private void supprimerDemandeSelectionnee() {
        int row = tableDemandes.getSelectedRow();
        if (row < 0) { info("Sélectionnez une demande."); return; }

        int id = Integer.parseInt(tableDemandes.getValueAt(row, 0).toString());
        String statut = tableDemandes.getValueAt(row, 4).toString();

        if (!statut.equalsIgnoreCase("EN_ATTENTE")) {
            info("Vous ne pouvez supprimer qu'une demande EN_ATTENTE.\n"
               + "Une fois traitée, contactez l'administration.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Supprimer cette demande ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            demandeCtrl.supprimerDemande(id);
            rechargerDemandes();
        }
    }

    // =========================================================
    // PAGE 3 — MES RÉCLAMATIONS
    // =========================================================
    private JPanel buildReclamationsPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 36, 28, 36));

        JPanel header = pageHeader(
                "Mes réclamations",
                "Suivez l'état de vos réclamations ou soumettez-en une nouvelle"
        );

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JLabel info = new JLabel("Liste de vos réclamations");
        info.setFont(new Font("SansSerif", Font.BOLD, 13));
        info.setForeground(PRIMARY);
        info.setBorder(new EmptyBorder(6, 4, 0, 0));

        JButton btnNouvelle = primaryButton("+ Nouvelle réclamation");
        btnNouvelle.addActionListener(e -> ouvrirDialogAjoutReclamation());

        toolbar.add(info,        BorderLayout.WEST);
        toolbar.add(btnNouvelle, BorderLayout.EAST);

        String[] cols = {"ID", "Motif", "Date", "Statut"};
        DefaultTableModel model = new DefaultTableModel(
                reclamationCtrl.getReclamationsParUtilisateur(idUtilisateur), cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableReclamations = new JTable(model);
        styleTable(tableReclamations);
        tableReclamations.getColumnModel().getColumn(0).setMaxWidth(70);
        tableReclamations.getColumnModel().getColumn(3).setCellRenderer(new StatutRenderer());

        JScrollPane scroll = new JScrollPane(tableReclamations);
        scroll.setBorder(new LineBorderRounded(BORDER, 1, 12));
        scroll.getViewport().setBackground(CARD);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnVoir = secondaryButton("Voir détail");
        btnVoir.addActionListener(e -> voirDetailReclamation());
        actions.add(btnVoir);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(toolbar, BorderLayout.NORTH);
        center.add(scroll,  BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        page.add(header, BorderLayout.NORTH);
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private void rechargerReclamations() {
        if (tableReclamations == null) return;
        String[] cols = {"ID", "Motif", "Date", "Statut"};
        DefaultTableModel m = new DefaultTableModel(
                reclamationCtrl.getReclamationsParUtilisateur(idUtilisateur), cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableReclamations.setModel(m);
        tableReclamations.getColumnModel().getColumn(0).setMaxWidth(70);
        tableReclamations.getColumnModel().getColumn(3).setCellRenderer(new StatutRenderer());
    }

    private void ouvrirDialogAjoutReclamation() {
        String[] motifs = {
                "Retard traitement", "Réponse incomplète", "Désaccord décision",
                "Manque d'information", "Problème accessibilité", "Autre"
        };
        JComboBox<String> cMotif = new JComboBox<>(motifs);
        JTextArea tDesc = new JTextArea(5, 26);
        tDesc.setLineWrap(true);
        tDesc.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(new JLabel("Motif :"));
        form.add(cMotif);
        form.add(new JLabel("Description :"));
        form.add(new JScrollPane(tDesc));

        int res = JOptionPane.showConfirmDialog(this, form,
                "Nouvelle réclamation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String desc = tDesc.getText().trim();
        if (desc.isEmpty()) {
            info("Veuillez saisir une description.");
            return;
        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        reclamationCtrl.ajouterReclamation(
                (String) cMotif.getSelectedItem(),
                desc, date, "EN_ATTENTE",
                idUtilisateur
        );

        info("Réclamation envoyée. Elle est en attente de traitement.");
        rechargerReclamations();
    }

    private void voirDetailReclamation() {
        int row = tableReclamations.getSelectedRow();
        if (row < 0) { info("Sélectionnez une réclamation."); return; }

        String idStr = tableReclamations.getValueAt(row, 0).toString();
        // Le DAO ajoute "REC-" devant l'id, on l'enlève
        String idClean = idStr.replace("REC-", "").trim();
        int id;
        try { id = Integer.parseInt(idClean); } catch (Exception e) { return; }

        String description = "";
        String sql = "SELECT description FROM reclamation WHERE idReclamation = ?";
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    description = rs.getString("description") != null ? rs.getString("description") : "—";
                }
            }
        } catch (Exception ex) {
            System.out.println("Erreur détail réclamation : " + ex.getMessage());
        }

        String descHtml = description.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");

        StringBuilder sb = new StringBuilder("<html><body style='width:380px; font-family:sans-serif;'>");
        sb.append("<h2 style='margin:0;color:#445343;'>Détail de la réclamation</h2>");
        sb.append("<hr style='border:0;border-top:1px solid #ddd;margin:10px 0;'>");
        sb.append("<p><b>ID :</b> ").append(idStr).append("</p>");
        sb.append("<p><b>Motif :</b> ").append(tableReclamations.getValueAt(row, 1)).append("</p>");
        sb.append("<p><b>Date :</b> ").append(tableReclamations.getValueAt(row, 2)).append("</p>");
        sb.append("<p><b>Statut :</b> ").append(tableReclamations.getValueAt(row, 3)).append("</p>");
        sb.append("<hr style='border:0;border-top:1px solid #ddd;margin:10px 0;'>");
        sb.append("<p><b>Description :</b><br><span style='color:#444;'>").append(descHtml).append("</span></p>");
        sb.append("</body></html>");

        JOptionPane.showMessageDialog(this, sb.toString(),
                "Détail de la réclamation", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================
    // PAGE 4 — MON PROFIL
    // =========================================================
    private JPanel buildProfilPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 36, 28, 36));

        JPanel header = pageHeader(
                "Mon profil",
                "Consultez et modifiez vos informations personnelles"
        );

        // Récupérer les infos depuis la BD
        String nom = "", prenom = "", email = emailEtudiant, role = "ETUDIANT", typeHand = "";
        String sql = "SELECT nom, prenom, email, role, typeHandicap FROM utilisateur WHERE id = ?";
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nom      = rs.getString("nom");
                    prenom   = rs.getString("prenom");
                    email    = rs.getString("email");
                    role     = rs.getString("role");
                    typeHand = rs.getString("typeHandicap");
                    if (typeHand == null) typeHand = "—";
                }
            }
        } catch (Exception ex) {
            System.out.println("Erreur profil : " + ex.getMessage());
        }

        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorderRounded(BORDER, 1, 12),
                new EmptyBorder(32, 36, 32, 36)
        ));

        // Avatar + nom complet en haut
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String initiale = (prenom != null && !prenom.isEmpty())
                ? prenom.substring(0, 1).toUpperCase() : "E";
        JLabel avatar = new JLabel(initiale);
        avatar.setOpaque(true);
        avatar.setBackground(GOLD);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("SansSerif", Font.BOLD, 28));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(64, 64));

        JPanel nameBox = new JPanel();
        nameBox.setOpaque(false);
        nameBox.setLayout(new BoxLayout(nameBox, BoxLayout.Y_AXIS));

        JLabel lblNom = new JLabel((prenom != null ? prenom : "") + " " + (nom != null ? nom : ""));
        lblNom.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblNom.setForeground(TEXT);

        JLabel lblRole = new JLabel(role);
        lblRole.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblRole.setForeground(MUTED);

        nameBox.add(lblNom);
        nameBox.add(lblRole);

        topRow.add(avatar);
        topRow.add(nameBox);

        // Infos
        JPanel infos = new JPanel(new GridLayout(0, 1, 0, 14));
        infos.setOpaque(false);
        infos.setAlignmentX(Component.LEFT_ALIGNMENT);
        infos.setBorder(new EmptyBorder(24, 0, 8, 0));

        infos.add(infoRow("Nom",            nom != null ? nom : "—"));
        infos.add(infoRow("Prénom",         prenom != null ? prenom : "—"));
        infos.add(infoRow("Email",          email != null ? email : "—"));
        infos.add(infoRow("Type handicap",  typeHand));

        // Bouton modifier
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setBorder(new EmptyBorder(18, 0, 0, 0));

        JButton btnModifier = primaryButton("Modifier mes informations");
        btnModifier.addActionListener(e -> ouvrirDialogModifierProfil());
        actions.add(btnModifier);

        card.add(topRow);
        card.add(infos);
        card.add(actions);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.NORTH);

        page.add(header, BorderLayout.NORTH);
        page.add(wrap,   BorderLayout.CENTER);
        return page;
    }

    private JPanel infoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel l = new JLabel(label.toUpperCase());
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(MUTED);
        l.setPreferredSize(new Dimension(160, 0));

        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.PLAIN, 14));
        v.setForeground(TEXT);

        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.CENTER);
        return row;
    }

    private void ouvrirDialogModifierProfil() {
        // Récupérer valeurs actuelles
        String nom = "", prenom = "", email = emailEtudiant;
        String sql = "SELECT nom, prenom, email FROM utilisateur WHERE id = ?";
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nom    = rs.getString("nom");
                    prenom = rs.getString("prenom");
                    email  = rs.getString("email");
                }
            }
        } catch (Exception ex) {
            System.out.println("Erreur récup profil : " + ex.getMessage());
        }

        JTextField tNom    = new JTextField(nom);
        JTextField tPrenom = new JTextField(prenom);
        JTextField tEmail  = new JTextField(email);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(new JLabel("Nom :"));    form.add(tNom);
        form.add(new JLabel("Prénom :")); form.add(tPrenom);
        form.add(new JLabel("Email :"));  form.add(tEmail);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Modifier mes informations",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            utilisateurCtrl.modifierUtilisateur(idUtilisateur,
                    tNom.getText().trim(),
                    tPrenom.getText().trim(),
                    tEmail.getText().trim());

            // Mettre à jour les variables locales
            this.emailEtudiant = tEmail.getText().trim();
            this.nomEtudiant   = tPrenom.getText().trim() + " " + tNom.getText().trim();

            info("Profil mis à jour.\nVeuillez vous reconnecter si vous avez changé votre email.");

            // Recharger la page profil
            // (simple: on remplace le contenu de "profil")
            pages.removeAll();
            pageAccueil = buildAccueilPage();
            pages.add(pageAccueil,              "accueil");
            pages.add(buildDemandesPage(),      "demandes");
            pages.add(buildReclamationsPage(),  "reclamations");
            pages.add(buildProfilPage(),        "profil");
            cardLayout.show(pages, "profil");
            pages.revalidate();
            pages.repaint();
        }
    }

    // =========================================================
    // HELPERS UI
    // =========================================================
    private JPanel pageHeader(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 28));
        t.setForeground(TEXT);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("SansSerif", Font.PLAIN, 14));
        s.setForeground(MUTED);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(t);
        p.add(Box.createVerticalStrut(4));
        p.add(s);
        return p;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(PRIMARY_LT); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(PRIMARY); }
        });
        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(CARD);
        b.setForeground(TEXT);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorderRounded(BORDER, 1, 8),
                new EmptyBorder(8, 16, 8, 16)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(SOFT); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(CARD); }
        });
        return b;
    }

    private JButton dangerButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(RED_BADGE);
        b.setForeground(RED);
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(0xEED6D6)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(RED_BADGE); }
        });
        return b;
    }

    private void styleTable(JTable t) {
        t.setRowHeight(42);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.setForeground(TEXT);
        t.setBackground(CARD);
        t.setSelectionBackground(new Color(0xE9E5DC));
        t.setSelectionForeground(TEXT);
        t.setGridColor(new Color(0xF0EDE6));
        t.setShowHorizontalLines(true);

        JTableHeader h = t.getTableHeader();
        h.setFont(new Font("SansSerif", Font.BOLD, 11));
        h.setBackground(SOFT);
        h.setForeground(MUTED);
        h.setPreferredSize(new Dimension(0, 38));
        h.setBorder(BorderFactory.createEmptyBorder());
        h.setReorderingAllowed(false);

        DefaultTableCellRenderer leftPad = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tab, Object value,
                                                           boolean selected, boolean focus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(tab, value, selected, focus, r, c);
                ((JLabel) comp).setBorder(new EmptyBorder(0, 14, 0, 8));
                return comp;
            }
        };
        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(leftPad);
        }
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================
    // RENDERER STATUT (badge couleur)
    // =========================================================
    private class StatutRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                                                       boolean selected, boolean focus, int r, int c) {
            super.getTableCellRendererComponent(t, value, selected, focus, r, c);

            String s = value == null ? "" : value.toString().toUpperCase();
            final Color bg, fg;
            switch (s) {
                case "ACCEPTEE":
                case "ACCEPTÉE":
                    bg = GREEN_BADGE; fg = new Color(0x2E7D32); break;
                case "REFUSEE":
                case "REFUSÉE":
                    bg = RED_BADGE; fg = RED; break;
                case "EN_COURS":
                case "EN COURS":
                    bg = new Color(0xE8EEF4); fg = new Color(0x2A5A8A); break;
                case "ARCHIVEE":
                case "ARCHIVÉE":
                    bg = SOFT; fg = MUTED; break;
                case "EN_ATTENTE":
                case "EN ATTENTE":
                default:
                    bg = YELLOW_BADGE; fg = new Color(0x8B6914); break;
            }

            JPanel pill = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(selected ? new Color(0xE9E5DC) : CARD);
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    int w = 110, h = 24;
                    int x = 14;
                    int y = (getHeight() - h) / 2;
                    g2.setColor(bg);
                    g2.fillRoundRect(x, y, w, h, 14, 14);
                    g2.setColor(fg);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    String txt = s.replace("_", " ");
                    int strW = g2.getFontMetrics().stringWidth(txt);
                    g2.drawString(txt, x + (w - strW) / 2, y + 16);
                    g2.dispose();
                }
            };
            pill.setOpaque(false);
            return pill;
        }
    }

    // =========================================================
    // BORDURE ARRONDIE
    // =========================================================
    private static class LineBorderRounded extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        LineBorderRounded(Color c, int t, int r) {
            this.color = c; this.thickness = t; this.radius = r;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f,
                    w - thickness, h - thickness, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(1, 1, 1, 1); }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(1, 1, 1, 1); return insets;
        }
    }

    
}
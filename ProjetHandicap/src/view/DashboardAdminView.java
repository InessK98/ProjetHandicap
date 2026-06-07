package view;

import controller.ArchiveController;
import controller.DashboardController;
import controller.DemandeController;
import controller.UtilisateurController;
import model.Utilisateur;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class DashboardAdminView extends JFrame {

    // === PALETTE — cohérente avec ConnexionView / EtudiantView ===
    private static final Color BG          = new Color(0xFCF9F4);
    private static final Color SIDEBAR     = new Color(0x2F3D2F);
    private static final Color SIDEBAR_HOV = new Color(0x3D4D3D);
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

    // === CONTROLLERS ===
    private final DashboardController    dashboardCtrl    = new DashboardController();
    private final DemandeController      demandeCtrl      = new DemandeController();
    private final ArchiveController      archiveCtrl      = new ArchiveController();
    private final UtilisateurController  utilisateurCtrl  = new UtilisateurController();

    // === STATE / WIDGETS ===
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     pages      = new JPanel(cardLayout);

    private final ArrayList<JButton> navButtons = new ArrayList<>();
    private JButton currentNav;

    // Stats labels (refresh dynamique)
    private JLabel lblTotal, lblEnCours, lblAcceptees, lblRefusees;

    // Tables (rebuild dynamique)
    private JTable tableDemandes;
    private JTable tableReclamations;
    private JTable tableArchives;
    private JTable tableUtilisateurs;

    // Référence vers la page accueil pour pouvoir la reconstruire
    private JPanel pageAccueil;

    // === CONSTRUCTEUR ===
    public DashboardAdminView() {
        setTitle("AccèsU — Espace Administrateur");
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
        pages.add(buildComptesPage(),       "comptes");
        pages.add(buildArchivagePage(),     "archivage");
        pages.add(buildParametresPage(),    "parametres");

        root.add(pages, BorderLayout.CENTER);
        setContentPane(root);

        // Page par défaut
        switchTo("accueil", navButtons.get(0));
    }

    // === SIDEBAR ===
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setBorder(new EmptyBorder(28, 0, 24, 0));

        // ---- HEADER ----
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(0, 26, 28, 26));

        JLabel logo = new JLabel("AccèsU");
        logo.setFont(new Font("SansSerif", Font.BOLD, 26));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Administration · UIR");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(0xB8C0B5));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(logo);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        // ---- NAV ----
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(new EmptyBorder(8, 14, 8, 14));

        JLabel section = new JLabel("  MENU");
        section.setFont(new Font("SansSerif", Font.BOLD, 10));
        section.setForeground(new Color(0x9DA89A));
        section.setBorder(new EmptyBorder(0, 6, 10, 0));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        nav.add(section);

        nav.add(makeNavButton("Tableau de bord",   "accueil"));
        nav.add(Box.createVerticalStrut(4));
        nav.add(makeNavButton("Demandes",          "demandes"));
        nav.add(Box.createVerticalStrut(4));
        nav.add(makeNavButton("Réclamations",      "reclamations"));
        nav.add(Box.createVerticalStrut(4));
        nav.add(makeNavButton("Comptes",           "comptes"));
        nav.add(Box.createVerticalStrut(4));
        nav.add(makeNavButton("Archivage",         "archivage"));
        nav.add(Box.createVerticalStrut(4));
        nav.add(makeNavButton("Paramètres",        "parametres"));

        // ---- BOTTOM ----
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(20, 22, 0, 22));

        JPanel userCard = new JPanel(new BorderLayout());
        userCard.setBackground(SIDEBAR_HOV);
        userCard.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel avatar = new JLabel("A");
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

        JLabel n = new JLabel("Administrateur");
        n.setForeground(Color.WHITE);
        n.setFont(new Font("SansSerif", Font.BOLD, 13));

        JLabel r = new JLabel("Accès complet");
        r.setForeground(new Color(0xB8C0B5));
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
                BorderFactory.createLineBorder(new Color(0x4F5F4F), 1),
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
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != currentNav) btn.setBackground(SIDEBAR_HOV);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
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

        // IMPORTANT : recharger les données AVANT d'afficher la page
        switch (card) {
            case "accueil":      rafraichirStats(); break;
            case "demandes":     rechargerDemandes(); break;
            case "reclamations": rechargerReclamations(); break;
            case "comptes":      rechargerUtilisateurs(); break;
            case "archivage":    rechargerArchives(); break;
        }

        // Puis afficher la page demandée
        cardLayout.show(pages, card);
    }

    // === PAGE 1 — ACCUEIL / TABLEAU DE BORD ===
    private JPanel buildAccueilPage() {
        JPanel page = new JPanel(new BorderLayout(0, 22));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 36, 28, 36));

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel breadcrumb = new JLabel("AccèsU  ›  Administration  ›  Tableau de bord");
        breadcrumb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        breadcrumb.setForeground(MUTED);

        JLabel title = new JLabel("Tableau de bord");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(TEXT);

        JLabel sub = new JLabel("Vue d'ensemble des demandes et réclamations");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(MUTED);

        titleBox.add(breadcrumb);
        titleBox.add(Box.createVerticalStrut(8));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton btnFiltre  = secondaryButton("Filtrer par date");
        JButton btnExport  = secondaryButton("Exporter rapport");
        JButton btnRefresh = secondaryButton("Actualiser");

        btnFiltre.addActionListener(e -> ouvrirFiltreDate());
        btnExport.addActionListener(e -> exporterRapport());
        btnRefresh.addActionListener(e -> rafraichirEtAfficherAccueil());

        JLabel date = new JLabel(new SimpleDateFormat("EEEE d MMMM yyyy", java.util.Locale.FRENCH)
                .format(new Date()));
        date.setFont(new Font("SansSerif", Font.PLAIN, 13));
        date.setForeground(MUTED);
        date.setBorder(new EmptyBorder(8, 0, 0, 12));

        right.add(date);
        right.add(btnFiltre);
        right.add(btnExport);
        right.add(btnRefresh);

        header.add(titleBox, BorderLayout.WEST);
        header.add(right,    BorderLayout.EAST);

        // --- BODY (scrollable) ---
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        // KPI cards
        int total     = dashboardCtrl.getTotalDemandes();
        int enCours   = dashboardCtrl.getDemandesEnCours();
        int acceptees = dashboardCtrl.getDemandesAcceptees();
        int refusees  = dashboardCtrl.getDemandesRefusees();

        lblTotal     = new JLabel(String.valueOf(total));
        lblEnCours   = new JLabel(String.valueOf(enCours));
        lblAcceptees = new JLabel(String.valueOf(acceptees));
        lblRefusees  = new JLabel(String.valueOf(refusees));

        JPanel kpis = new JPanel(new GridLayout(1, 4, 18, 0));
        kpis.setOpaque(false);
        kpis.setMaximumSize(new Dimension(Integer.MAX_VALUE, 135));
        kpis.add(kpiCard("Total demandes", lblTotal,     "Tous statuts confondus", PRIMARY));
        kpis.add(kpiCard("En cours",       lblEnCours,   "À traiter",              GOLD));
        kpis.add(kpiCard("Acceptées",      lblAcceptees, "Demandes validées",      new Color(0x2E7D32)));
        kpis.add(kpiCard("Refusées",       lblRefusees,  "Demandes rejetées",      RED));

        // Charts row
        JPanel charts = new JPanel(new GridLayout(1, 2, 18, 0));
        charts.setOpaque(false);
        charts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));
        charts.add(chartParMois());
        charts.add(panneauRepartition());

        // Recent table
        JPanel recents = panelDemandesRecentes();

        body.add(kpis);
        body.add(Box.createVerticalStrut(20));
        body.add(charts);
        body.add(Box.createVerticalStrut(20));
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

    private JPanel kpiCard(String title, JLabel valueLabel, String subtitle, Color accent) {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorderRounded(BORDER, 1, 12),
                new EmptyBorder(20, 22, 20, 22)
        ));

        // bande couleur à gauche
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

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 34));
        valueLabel.setForeground(TEXT);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("SansSerif", Font.PLAIN, 12));
        s.setForeground(MUTED);

        content.add(t);
        content.add(Box.createVerticalStrut(8));
        content.add(valueLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(s);

        card.add(left,    BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel chartParMois() {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorderRounded(BORDER, 1, 12),
                new EmptyBorder(22, 26, 22, 26)
        ));

        JLabel title = new JLabel("Demandes par mois");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(TEXT);

        JLabel sub = new JLabel("Évolution sur l'année en cours");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MUTED);

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(title);
        head.add(Box.createVerticalStrut(2));
        head.add(sub);

        int[] mois = dashboardCtrl.getDemandesParMois();

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int padL = 30, padR = 10, padB = 24, padT = 10;

                int chartW = w - padL - padR;
                int chartH = h - padT - padB;

                int max = 1;
                for (int v : mois) if (v > max) max = v;

                // grille horizontale
                g2.setColor(new Color(0xEDE9E0));
                for (int i = 0; i <= 4; i++) {
                    int y = padT + chartH - (chartH * i / 4);
                    g2.drawLine(padL, y, w - padR, y);
                }

                // labels Y
                g2.setColor(MUTED);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                for (int i = 0; i <= 4; i++) {
                    int val = max * i / 4;
                    int y = padT + chartH - (chartH * i / 4);
                    g2.drawString(String.valueOf(val), 4, y + 4);
                }

                // barres
                String[] noms = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin",
                                 "Juil", "Août", "Sep", "Oct", "Nov", "Déc"};
                int slot = chartW / 12;
                int barW = Math.max(10, slot - 10);

                for (int i = 0; i < 12; i++) {
                    int v = mois[i];
                    int barH = (int) ((double) v / max * chartH);
                    int x = padL + i * slot + (slot - barW) / 2;
                    int y = padT + chartH - barH;

                    g2.setColor(PRIMARY);
                    g2.fillRoundRect(x, y, barW, barH, 6, 6);

                    g2.setColor(MUTED);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    int strW = g2.getFontMetrics().stringWidth(noms[i]);
                    g2.drawString(noms[i], x + (barW - strW) / 2, h - 6);
                }
                g2.dispose();
            }
        };
        chart.setOpaque(false);

        card.add(head,  BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);
        return card;
    }

    private JPanel panneauRepartition() {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorderRounded(BORDER, 1, 12),
                new EmptyBorder(22, 26, 22, 26)
        ));

        JLabel title = new JLabel("Répartition par type");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(TEXT);

        JLabel sub = new JLabel("Catégories de demandes traitées");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MUTED);

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(title);
        head.add(Box.createVerticalStrut(2));
        head.add(sub);

        Map<String, Integer> data = dashboardCtrl.getDemandesParType();
        int total = 0;
        for (int v : data.values()) total += v;

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (data.isEmpty()) {
            JLabel empty = new JLabel("Aucune donnée disponible");
            empty.setFont(new Font("SansSerif", Font.PLAIN, 13));
            empty.setForeground(MUTED);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            list.add(empty);
        } else {
            Color[] palette = {
                    PRIMARY, GOLD, new Color(0x2E7D32),
                    new Color(0x7B5E27), new Color(0x506650),
                    new Color(0x8B4040)
            };
            int idx = 0;
            for (Map.Entry<String, Integer> e : data.entrySet()) {
                int v = e.getValue();
                int pct = total == 0 ? 0 : (v * 100 / total);
                Color c = palette[idx % palette.length];

                JPanel row = new JPanel(new BorderLayout(0, 4));
                row.setOpaque(false);
                row.setAlignmentX(Component.LEFT_ALIGNMENT);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                JPanel rowTop = new JPanel(new BorderLayout());
                rowTop.setOpaque(false);

                JLabel left = new JLabel("● " + e.getKey());
                left.setFont(new Font("SansSerif", Font.PLAIN, 13));
                left.setForeground(c);

                JLabel rightLbl = new JLabel(v + " (" + pct + "%)");
                rightLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
                rightLbl.setForeground(TEXT);

                rowTop.add(left,     BorderLayout.WEST);
                rowTop.add(rightLbl, BorderLayout.EAST);

                final int p = pct;
                final Color barColor = c;
                JPanel bar = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(0xEDE9E0));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                        g2.setColor(barColor);
                        g2.fillRoundRect(0, 0, getWidth() * p / 100, getHeight(), 6, 6);
                        g2.dispose();
                    }
                };
                bar.setOpaque(false);
                bar.setPreferredSize(new Dimension(0, 6));
                bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));

                row.add(rowTop, BorderLayout.NORTH);
                row.add(bar,    BorderLayout.SOUTH);

                list.add(row);
                list.add(Box.createVerticalStrut(10));
                idx++;
            }
        }

        card.add(head, BorderLayout.NORTH);
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel panelDemandesRecentes() {
        JPanel card = cardPanel();
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorderRounded(BORDER, 1, 12),
                new EmptyBorder(22, 26, 22, 26)
        ));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);

        JPanel headLeft = new JPanel();
        headLeft.setOpaque(false);
        headLeft.setLayout(new BoxLayout(headLeft, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Demandes récentes");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(TEXT);

        JLabel sub = new JLabel("Dernières soumissions");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MUTED);

        headLeft.add(title);
        headLeft.add(Box.createVerticalStrut(2));
        headLeft.add(sub);

        JButton voirTout = secondaryButton("Voir tout");
        voirTout.addActionListener(e -> switchTo("demandes", navButtons.get(1)));

        head.add(headLeft, BorderLayout.WEST);
        head.add(voirTout, BorderLayout.EAST);

        String[] cols = {"Étudiant", "Type", "Date", "Statut", ""};
        Object[][] data = dashboardCtrl.getDemandesRecentes();

        DefaultTableModel model = readOnlyModel(data, cols);

        JTable table = new JTable(model);
        styleTable(table);
        table.getColumnModel().getColumn(3).setCellRenderer(new StatutRenderer());

        // hauteur max ~ 5 lignes
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(0, 220));
        scroll.getViewport().setBackground(CARD);

        card.add(head,   BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void rafraichirStats() {
        // Reconstruire la page Accueil en arrière-plan SANS forcer l'affichage
        // (pour ne pas interrompre l'utilisateur si il est sur une autre page)
        pages.remove(pageAccueil);
        pageAccueil = buildAccueilPage();
        pages.add(pageAccueil, "accueil");
        pages.revalidate();
        pages.repaint();
    }

    /** Variante qui actualise ET affiche la page accueil (utilisé par le bouton Actualiser). */
    private void rafraichirEtAfficherAccueil() {
        rafraichirStats();
        cardLayout.show(pages, "accueil");
    }

    /** Filtre les statistiques par plage de dates et affiche un rapport. */
    private void ouvrirFiltreDate() {
        JTextField tDebut = new JTextField(new SimpleDateFormat("yyyy-MM-01").format(new Date()));
        JTextField tFin   = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(new JLabel("Date début (format yyyy-MM-dd) :"));
        form.add(tDebut);
        form.add(new JLabel("Date fin (format yyyy-MM-dd) :"));
        form.add(tFin);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Filtrer les demandes par date", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String debut = tDebut.getText().trim();
        String fin   = tFin.getText().trim();

        if (debut.isEmpty() || fin.isEmpty()) {
            info("Veuillez saisir les deux dates.");
            return;
        }

        int total = 0, enAttente = 0, enCours = 0, acceptees = 0, refusees = 0, archivees = 0;

        String sql = "SELECT statut, COUNT(*) FROM demande " +
                     "WHERE dateSoumission BETWEEN ? AND ? GROUP BY statut";
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, debut);
            ps.setString(2, fin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String s = rs.getString(1);
                    int n = rs.getInt(2);
                    total += n;
                    if (s == null) continue;
                    switch (s.toUpperCase()) {
                        case "EN_ATTENTE": enAttente += n; break;
                        case "EN_COURS":   enCours += n; break;
                        case "ACCEPTEE":   acceptees += n; break;
                        case "REFUSEE":    refusees += n; break;
                        case "ARCHIVEE":   archivees += n; break;
                    }
                }
            }
        } catch (Exception ex) {
            info("Erreur filtre : " + ex.getMessage());
            return;
        }

        // Afficher un rapport détaillé dans une popup
        StringBuilder sb = new StringBuilder("<html><body style='width:380px; font-family:sans-serif;'>");
        sb.append("<h2 style='margin:0;color:#445343;'>📊 Statistiques filtrées</h2>");
        sb.append("<hr style='border:0;border-top:1px solid #ddd;margin:10px 0;'>");
        sb.append("<p><b>Période :</b> du ").append(debut).append(" au ").append(fin).append("</p>");
        sb.append("<hr style='border:0;border-top:1px solid #ddd;margin:10px 0;'>");
        sb.append("<table style='width:100%;'>");
        sb.append("<tr><td><b>Total demandes :</b></td><td style='text-align:right;font-size:18px;color:#445343;'><b>").append(total).append("</b></td></tr>");
        sb.append("<tr><td colspan='2'><hr style='border:0;border-top:1px dashed #ccc;'></td></tr>");
        sb.append("<tr><td>🟡 En attente :</td><td style='text-align:right;'>").append(enAttente).append("</td></tr>");
        sb.append("<tr><td>🔵 En cours :</td><td style='text-align:right;'>").append(enCours).append("</td></tr>");
        sb.append("<tr><td>🟢 Acceptées :</td><td style='text-align:right;'>").append(acceptees).append("</td></tr>");
        sb.append("<tr><td>🔴 Refusées :</td><td style='text-align:right;'>").append(refusees).append("</td></tr>");
        sb.append("<tr><td>⚪ Archivées :</td><td style='text-align:right;'>").append(archivees).append("</td></tr>");
        sb.append("</table>");
        sb.append("</body></html>");

        JOptionPane.showMessageDialog(this, sb.toString(),
                "Filtre par date", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Génère un rapport texte des statistiques actuelles dans un fichier .txt. */
    private void exporterRapport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exporter le rapport");
        chooser.setSelectedFile(new java.io.File(
                "rapport_AccesU_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".txt"
        ));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File f = chooser.getSelectedFile();

        StringBuilder sb = new StringBuilder();
        sb.append("===============================================\n");
        sb.append("  RAPPORT — AccèsU / Système de gestion handicap\n");
        sb.append("  Université Internationale de Rabat\n");
        sb.append("  Généré le : ")
          .append(new SimpleDateFormat("dd/MM/yyyy à HH:mm").format(new Date()))
          .append("\n");
        sb.append("===============================================\n\n");

        sb.append("STATISTIQUES GLOBALES\n");
        sb.append("---------------------\n");
        sb.append("Total demandes  : ").append(dashboardCtrl.getTotalDemandes()).append("\n");
        sb.append("En cours        : ").append(dashboardCtrl.getDemandesEnCours()).append("\n");
        sb.append("Acceptées       : ").append(dashboardCtrl.getDemandesAcceptees()).append("\n");
        sb.append("Refusées        : ").append(dashboardCtrl.getDemandesRefusees()).append("\n\n");

        sb.append("RÉPARTITION PAR TYPE\n");
        sb.append("--------------------\n");
        Map<String, Integer> parType = dashboardCtrl.getDemandesParType();
        if (parType.isEmpty()) {
            sb.append("  (aucune donnée)\n");
        } else {
            for (Map.Entry<String, Integer> e : parType.entrySet()) {
                sb.append(String.format("  %-30s : %d%n", e.getKey(), e.getValue()));
            }
        }
        sb.append("\n");

        sb.append("DEMANDES PAR MOIS\n");
        sb.append("-----------------\n");
        String[] mois = {"Janvier","Février","Mars","Avril","Mai","Juin",
                         "Juillet","Août","Septembre","Octobre","Novembre","Décembre"};
        int[] vals = dashboardCtrl.getDemandesParMois();
        for (int i = 0; i < 12; i++) {
            sb.append(String.format("  %-12s : %d%n", mois[i], vals[i]));
        }

        sb.append("\n===============================================\n");
        sb.append("  Fin du rapport\n");
        sb.append("===============================================\n");

        try (FileWriter w = new FileWriter(f)) {
            w.write(sb.toString());
            info("Rapport exporté avec succès :\n" + f.getAbsolutePath());
        } catch (IOException ex) {
            info("Erreur lors de l'export : " + ex.getMessage());
        }
    }

    // === PAGE 2 — DEMANDES ===
    private JPanel buildDemandesPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 36, 28, 36));

        // Header
        JPanel header = pageHeader(
                "Gestion des demandes",
                "Consulter, valider, refuser ou supprimer les demandes des étudiants"
        );

        // Toolbar : juste un compteur et un bouton Actualiser
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JLabel info = new JLabel("Demandes en attente de traitement");
        info.setFont(new Font("SansSerif", Font.BOLD, 13));
        info.setForeground(PRIMARY);
        info.setBorder(new EmptyBorder(6, 4, 0, 0));

        JButton refresh = secondaryButton("Actualiser");
        refresh.addActionListener(e -> rechargerDemandes());

        toolbar.add(info,    BorderLayout.WEST);
        toolbar.add(refresh, BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "Étudiant", "Type", "Date", "Statut", "Assigné à"};

        DefaultTableModel model = readOnlyModel(
                demandeCtrl.getDemandesAdmin(), cols);
        tableDemandes = new JTable(model);
        styleTable(tableDemandes);
        tableDemandes.getColumnModel().getColumn(0).setMaxWidth(60);
        tableDemandes.getColumnModel().getColumn(4).setCellRenderer(new StatutRenderer());

        JScrollPane scroll = new JScrollPane(tableDemandes);
        scroll.setBorder(new LineBorderRounded(BORDER, 1, 12));
        scroll.getViewport().setBackground(CARD);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnVoir     = secondaryButton("Voir détail");
        JButton btnTraiter  = secondaryButton("Traiter");
        JButton btnAccepter = primaryButton("Accepter");
        JButton btnRefuser  = dangerButton("Refuser");
        JButton btnSuppr    = dangerButton("Supprimer");

        btnVoir.addActionListener(e     -> voirDetailDemande());
        btnTraiter.addActionListener(e  -> mettreEnCoursDemande());
        btnAccepter.addActionListener(e -> changerStatutDemande("ACCEPTEE"));
        btnRefuser.addActionListener(e  -> changerStatutDemande("REFUSEE"));
        btnSuppr.addActionListener(e    -> supprimerDemandeSelectionnee());

        actions.add(btnVoir);
        actions.add(btnTraiter);
        actions.add(btnAccepter);
        actions.add(btnRefuser);
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
        String[] cols = {"ID", "Étudiant", "Type", "Date", "Statut", "Assigné à"};

        Object[][] all = demandeCtrl.getDemandesAdmin();

        // Page Demandes = demandes EN_ATTENTE ou EN_COURS (= demandes non finalisées)
        // Les demandes acceptées/refusées sont automatiquement dans Archivage
        ArrayList<Object[]> filtered = new ArrayList<>();
        for (Object[] row : all) {
            String statut = row[4] == null ? "" : row[4].toString();
            if (statut.equalsIgnoreCase("EN_ATTENTE") || statut.equalsIgnoreCase("EN_COURS")) {
                filtered.add(row);
            }
        }
        Object[][] data = new Object[filtered.size()][6];
        for (int i = 0; i < filtered.size(); i++) data[i] = filtered.get(i);

        DefaultTableModel m = readOnlyModel(data, cols);
        tableDemandes.setModel(m);
        tableDemandes.getColumnModel().getColumn(0).setMaxWidth(60);
        tableDemandes.getColumnModel().getColumn(4).setCellRenderer(new StatutRenderer());
    }

    /** Met à jour visuellement le chip actif (vert plein) et désactive les autres. */
    private void setActiveChip(ArrayList<JButton> chips, JButton active) {
        for (JButton b : chips) {
            boolean on = (b == active);
            b.setBackground(on ? PRIMARY : CARD);
            b.setForeground(on ? Color.WHITE : TEXT);
            b.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorderRounded(on ? PRIMARY : BORDER, 1, 18),
                    new EmptyBorder(6, 14, 6, 14)
            ));
        }
    }

    private void voirDetailDemande() {
        int row = tableDemandes.getSelectedRow();
        if (row < 0) { info("Sélectionnez une demande dans la liste."); return; }

        int id = Integer.parseInt(tableDemandes.getValueAt(row, 0).toString());

        // Récupérer tous les détails depuis la BD
        String description = "";
        String pieceJustificative = "";

        String sql = "SELECT description, pieceJustificative FROM demande WHERE idDemande = ?";
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    description       = rs.getString("description") != null ? rs.getString("description") : "—";
                    pieceJustificative = rs.getString("pieceJustificative") != null ? rs.getString("pieceJustificative") : "—";
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
        String pieceHtml = pieceJustificative.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");

        StringBuilder sb = new StringBuilder("<html><body style='width:400px; font-family:sans-serif;'>");
        sb.append("<h2 style='margin:0;color:#445343;'>Détail de la demande</h2>");
        sb.append("<hr style='border:0;border-top:1px solid #ddd;margin:10px 0;'>");

        sb.append("<p><b>ID :</b> #").append(tableDemandes.getValueAt(row, 0)).append("</p>");
        sb.append("<p><b>Étudiant :</b> ").append(tableDemandes.getValueAt(row, 1)).append("</p>");
        sb.append("<p><b>Type de demande :</b> ").append(tableDemandes.getValueAt(row, 2)).append("</p>");
        sb.append("<p><b>Date de soumission :</b> ").append(tableDemandes.getValueAt(row, 3)).append("</p>");
        sb.append("<p><b>Statut :</b> ").append(tableDemandes.getValueAt(row, 4)).append("</p>");
        sb.append("<p><b>Assigné à :</b> ").append(tableDemandes.getValueAt(row, 5)).append("</p>");

        sb.append("<hr style='border:0;border-top:1px solid #ddd;margin:10px 0;'>");
        sb.append("<p><b>Description :</b><br>");
        sb.append("<span style='color:#444;'>").append(descHtml).append("</span></p>");
        sb.append("<p><b>Pièce justificative :</b><br>");
        sb.append("<span style='color:#445343;'>").append(pieceHtml).append("</span></p>");

        // Afficher le motif du refus si présent
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

    /** Met une demande en cours de traitement (sans l'archiver, elle reste visible). */
    private void mettreEnCoursDemande() {
        int row = tableDemandes.getSelectedRow();
        if (row < 0) { info("Sélectionnez une demande."); return; }

        int id = Integer.parseInt(tableDemandes.getValueAt(row, 0).toString());
        String statut = tableDemandes.getValueAt(row, 4).toString();

        if (statut.equalsIgnoreCase("EN_COURS")) {
            info("Cette demande est déjà en cours de traitement.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Marquer cette demande comme EN_COURS de traitement ?\n"
              + "Elle restera visible ici jusqu'à acceptation ou refus.",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {
            demandeCtrl.modifierStatutDemande(id, "EN_COURS");
            // Pas d'archivage : la demande n'est pas encore finalisée
            rechargerDemandes();
            rafraichirStats();
        }
    }

    private void changerStatutDemande(String nouveauStatut) {
        int row = tableDemandes.getSelectedRow();
        if (row < 0) { info("Sélectionnez une demande."); return; }

        int id = Integer.parseInt(tableDemandes.getValueAt(row, 0).toString());
        String etudiant = tableDemandes.getValueAt(row, 1).toString();
        String type     = tableDemandes.getValueAt(row, 2).toString();

        String motifRefus = null;

        // Si REFUSER → demander un motif obligatoire
        if (nouveauStatut.equalsIgnoreCase("REFUSEE")) {
            motifRefus = demanderMotifRefus();
            if (motifRefus == null) {
                // L'admin a annulé ou n'a pas saisi de motif
                return;
            }
        } else {
            // Pour ACCEPTER, simple confirmation
            int ok = JOptionPane.showConfirmDialog(this,
                    "Confirmer le changement de statut vers " + nouveauStatut + " ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
        }

        // 1. Mettre à jour le statut de la demande
        demandeCtrl.modifierStatutDemande(id, nouveauStatut);

        // 2. Si refus : ajouter le motif dans la description
        if (motifRefus != null) {
            ajouterMotifRefusDansDescription(id, motifRefus);
        }

        // 3. Archivage AUTOMATIQUE : tracer dans la table archive
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String labelArchive = "Demande #" + id + " - " + etudiant + " - " + type + " (" + nouveauStatut + ")";
        if (motifRefus != null) {
            labelArchive += " - Motif : " + motifRefus;
        }
        archiveCtrl.ajouterArchive(labelArchive, date);

        rechargerDemandes();
        rechargerArchives();
        rafraichirStats();
    }

    /** Affiche un dialogue pour saisir le motif d'un refus (obligatoire). */
    private String demanderMotifRefus() {
        String[] motifsPredefinis = {
                "(Sélectionner un motif)",
                "Dossier incomplet",
                "Justificatif non valide ou expiré",
                "Demande hors délai",
                "Aménagement déjà accordé",
                "Étudiant non éligible",
                "Demande non conforme au règlement",
                "Autre (préciser ci-dessous)"
        };
        JComboBox<String> cMotif = new JComboBox<>(motifsPredefinis);

        JTextArea tDetail = new JTextArea(4, 28);
        tDetail.setLineWrap(true);
        tDetail.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(new JLabel("Motif du refus :"));
        form.add(cMotif);
        form.add(new JLabel("Précision (obligatoire si 'Autre') :"));
        form.add(new JScrollPane(tDetail));

        int res = JOptionPane.showConfirmDialog(this, form,
                "Motif du refus", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return null;

        String selection = (String) cMotif.getSelectedItem();
        String precision = tDetail.getText().trim();

        // Validation
        if (selection.equals("(Sélectionner un motif)")) {
            info("Veuillez sélectionner un motif de refus.");
            return null;
        }

        if (selection.equals("Autre (préciser ci-dessous)")) {
            if (precision.isEmpty()) {
                info("Veuillez préciser le motif dans la zone de texte.");
                return null;
            }
            return precision;
        }

        // Si motif prédéfini + précision optionnelle
        if (!precision.isEmpty()) {
            return selection + " — " + precision;
        }
        return selection;
    }

    /** Ajoute le motif du refus dans la description de la demande (Option A). */
    private void ajouterMotifRefusDansDescription(int id, String motif) {
        // Récupère la description actuelle
        String descActuelle = "";
        String sqlGet = "SELECT description FROM demande WHERE idDemande = ?";
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sqlGet)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    descActuelle = rs.getString(1) != null ? rs.getString(1) : "";
                }
            }
        } catch (Exception ex) {
            System.out.println("Erreur lecture description : " + ex.getMessage());
        }

        // Ajoute le motif si pas déjà présent
        String marqueur = "[MOTIF REFUS]";
        String nouvelleDesc;
        if (descActuelle.contains(marqueur)) {
            // Remplace l'ancien motif
            nouvelleDesc = descActuelle.replaceAll(
                    "\n\n" + java.util.regex.Pattern.quote(marqueur) + ".*$",
                    "\n\n" + marqueur + " " + motif
            );
        } else {
            nouvelleDesc = descActuelle + "\n\n" + marqueur + " " + motif;
        }

        // UPDATE la description
        String sqlUpd = "UPDATE demande SET description = ? WHERE idDemande = ?";
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sqlUpd)) {
            ps.setString(1, nouvelleDesc);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Erreur ajout motif refus : " + ex.getMessage());
        }
    }

    private void supprimerDemandeSelectionnee() {
        int row = tableDemandes.getSelectedRow();
        if (row < 0) { info("Sélectionnez une demande."); return; }

        int id = Integer.parseInt(tableDemandes.getValueAt(row, 0).toString());
        int ok = JOptionPane.showConfirmDialog(this,
                "Supprimer définitivement cette demande ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            demandeCtrl.supprimerDemande(id);
            rechargerDemandes();
            rafraichirStats();
        }
    }

    // === PAGE 3 — RÉCLAMATIONS ===
    // Filtre courant pour réclamations
    private String filtreReclamationActuel = null; // null = toutes

    private JPanel buildReclamationsPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 36, 28, 36));

        JPanel header = pageHeader(
                "Gestion des réclamations",
                "Traiter et suivre les réclamations des étudiants"
        );

        // Filtres
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);

        JButton btnToutes  = filterChip("Toutes",     true);
        JButton btnAttente = filterChip("En attente", false);
        JButton btnCours   = filterChip("En cours",   false);
        JButton btnClot    = filterChip("Clôturées",  false);

        ArrayList<JButton> reclChips = new ArrayList<>();
        reclChips.add(btnToutes);
        reclChips.add(btnAttente);
        reclChips.add(btnCours);
        reclChips.add(btnClot);

        btnToutes.addActionListener(e  -> { setActiveChip(reclChips, btnToutes);  filtreReclamationActuel = null;         rechargerReclamations(); });
        btnAttente.addActionListener(e -> { setActiveChip(reclChips, btnAttente); filtreReclamationActuel = "EN_ATTENTE"; rechargerReclamations(); });
        btnCours.addActionListener(e   -> { setActiveChip(reclChips, btnCours);   filtreReclamationActuel = "EN_COURS";   rechargerReclamations(); });
        btnClot.addActionListener(e    -> { setActiveChip(reclChips, btnClot);    filtreReclamationActuel = "ACCEPTEE";   rechargerReclamations(); });

        filters.add(btnToutes);
        filters.add(btnAttente);
        filters.add(btnCours);
        filters.add(btnClot);

        toolbar.add(filters, BorderLayout.WEST);

        String[] cols = {"ID", "Motif", "Date", "Statut", "Utilisateur"};
        DefaultTableModel model = readOnlyModel(
                chargerReclamationsAdmin(), cols);
        tableReclamations = new JTable(model);
        styleTable(tableReclamations);
        tableReclamations.getColumnModel().getColumn(0).setMaxWidth(60);
        tableReclamations.getColumnModel().getColumn(3).setCellRenderer(new StatutRenderer());

        JScrollPane scroll = new JScrollPane(tableReclamations);
        scroll.setBorder(new LineBorderRounded(BORDER, 1, 12));
        scroll.getViewport().setBackground(CARD);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnVoirDetail = secondaryButton("Voir détail");
        JButton btnTraiter    = primaryButton("Traiter");
        JButton btnCloturer   = secondaryButton("Clôturer");

        btnVoirDetail.addActionListener(e -> voirDetailReclamation());
        btnTraiter.addActionListener(e    -> traiterReclamation("EN_COURS"));
        btnCloturer.addActionListener(e   -> traiterReclamation("ACCEPTEE"));

        actions.add(btnVoirDetail);
        actions.add(btnTraiter);
        actions.add(btnCloturer);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(toolbar, BorderLayout.NORTH);
        center.add(scroll,  BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        page.add(header, BorderLayout.NORTH);
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private void voirDetailReclamation() {
        int row = tableReclamations.getSelectedRow();
        if (row < 0) { info("Sélectionnez une réclamation."); return; }

        int id = Integer.parseInt(tableReclamations.getValueAt(row, 0).toString());

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
        sb.append("<p><b>ID :</b> #").append(id).append("</p>");
        sb.append("<p><b>Étudiant :</b> ").append(tableReclamations.getValueAt(row, 4)).append("</p>");
        sb.append("<p><b>Motif :</b> ").append(tableReclamations.getValueAt(row, 1)).append("</p>");
        sb.append("<p><b>Date :</b> ").append(tableReclamations.getValueAt(row, 2)).append("</p>");
        sb.append("<p><b>Statut :</b> ").append(tableReclamations.getValueAt(row, 3)).append("</p>");
        sb.append("<hr style='border:0;border-top:1px solid #ddd;margin:10px 0;'>");
        sb.append("<p><b>Description :</b><br><span style='color:#444;'>").append(descHtml).append("</span></p>");
        sb.append("</body></html>");

        JOptionPane.showMessageDialog(this, sb.toString(),
                "Détail de la réclamation #" + id, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Charge les réclamations (admin) avec filtre optionnel par statut. */
    private Object[][] chargerReclamationsAdmin() {
        String sql = "SELECT r.idReclamation, r.motif, r.dateReclamation, r.statut, " +
                     "CONCAT(u.prenom,' ',u.nom) AS user " +
                     "FROM reclamation r JOIN utilisateur u ON r.idUtilisateur = u.id ";

        if (filtreReclamationActuel != null) {
            sql += " WHERE r.statut = ? ";
        }
        sql += " ORDER BY r.idReclamation DESC";

        ArrayList<Object[]> rows = new ArrayList<>();
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (filtreReclamationActuel != null) {
                ps.setString(1, filtreReclamationActuel);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getInt(1), rs.getString(2),
                            rs.getString(3), rs.getString(4),
                            rs.getString(5)
                    });
                }
            }
        } catch (Exception ex) {
            System.out.println("Erreur réclamations admin : " + ex.getMessage());
        }
        Object[][] out = new Object[rows.size()][5];
        for (int i = 0; i < rows.size(); i++) out[i] = rows.get(i);
        return out;
    }

    private void rechargerReclamations() {
        if (tableReclamations == null) return;
        String[] cols = {"ID", "Motif", "Date", "Statut", "Utilisateur"};
        DefaultTableModel m = readOnlyModel(chargerReclamationsAdmin(), cols);
        tableReclamations.setModel(m);
        tableReclamations.getColumnModel().getColumn(0).setMaxWidth(60);
        tableReclamations.getColumnModel().getColumn(3).setCellRenderer(new StatutRenderer());
    }

    private void traiterReclamation(String nouveauStatut) {
        int row = tableReclamations.getSelectedRow();
        if (row < 0) { info("Sélectionnez une réclamation."); return; }
        int id = Integer.parseInt(tableReclamations.getValueAt(row, 0).toString());
        String motif    = tableReclamations.getValueAt(row, 1).toString();
        String etudiant = tableReclamations.getValueAt(row, 4).toString();

        String sql = "UPDATE reclamation SET statut=? WHERE idReclamation=?";
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nouveauStatut);
            ps.setInt(2, id);
            ps.executeUpdate();

            // Archivage AUTOMATIQUE quand la réclamation est clôturée (ACCEPTEE)
            if (nouveauStatut.equalsIgnoreCase("ACCEPTEE")) {
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                archiveCtrl.ajouterArchive(
                        "Réclamation #" + id + " - " + etudiant + " - " + motif + " (Clôturée)",
                        date
                );
                rechargerArchives();
            }

            info("Statut de la réclamation mis à jour.");
            rechargerReclamations();
        } catch (Exception ex) {
            info("Erreur : " + ex.getMessage());
        }
    }

    // === PAGE 4 — COMPTES (gestion utilisateurs) ===
    private JPanel buildComptesPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 36, 28, 36));

        JPanel header = pageHeader(
                "Gestion des comptes",
                "Créer, modifier ou supprimer les comptes utilisateurs"
        );

        String[] cols = {"ID", "Nom", "Prénom", "Email", "Rôle", "Type handicap", "Statut compte"};
        DefaultTableModel model = readOnlyModel(chargerUtilisateurs(), cols);
        tableUtilisateurs = new JTable(model);
        styleTable(tableUtilisateurs);
        tableUtilisateurs.getColumnModel().getColumn(0).setMaxWidth(60);
        tableUtilisateurs.getColumnModel().getColumn(6).setCellRenderer(new ValidationRenderer());

        JScrollPane scroll = new JScrollPane(tableUtilisateurs);
        scroll.setBorder(new LineBorderRounded(BORDER, 1, 12));
        scroll.getViewport().setBackground(CARD);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnAjouter   = primaryButton("Ajouter un compte");
        JButton btnValider   = primaryButton("Valider compte");
        JButton btnModifier  = secondaryButton("Modifier");
        JButton btnSupprimer = dangerButton("Supprimer");
        JButton btnRefresh   = secondaryButton("Actualiser");

        btnAjouter.addActionListener(e   -> ouvrirDialogAjoutCompte());
        btnValider.addActionListener(e   -> validerCompteSelectionne());
        btnModifier.addActionListener(e  -> ouvrirDialogModifCompte());
        btnSupprimer.addActionListener(e -> supprimerCompte());
        btnRefresh.addActionListener(e   -> rechargerUtilisateurs());

        actions.add(btnRefresh);
        actions.add(btnModifier);
        actions.add(btnSupprimer);
        actions.add(btnValider);
        actions.add(btnAjouter);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(scroll,  BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        page.add(header, BorderLayout.NORTH);
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private Object[][] chargerUtilisateurs() {
        // On gère le cas où la colonne `valide` n'existe pas encore
        String sql = "SELECT id, nom, prenom, email, role, typeHandicap, " +
                     "COALESCE(valide, 1) AS valide " +
                     "FROM utilisateur ORDER BY id DESC";
        ArrayList<Object[]> rows = new ArrayList<>();
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String th = rs.getString(6);
                boolean valide = rs.getInt(7) == 1;
                rows.add(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5),
                        (th == null || th.isEmpty()) ? "—" : th,
                        valide ? "Validé" : "En attente"
                });
            }
        } catch (Exception ex) {
            // Fallback : colonne `valide` absente — on fait sans
            System.out.println("Note : colonne 'valide' absente, fallback. (" + ex.getMessage() + ")");
            rows.clear();
            String sqlFallback = "SELECT id, nom, prenom, email, role, typeHandicap FROM utilisateur ORDER BY id DESC";
            try (Connection cn = dao.ConnexionDB.getConnection();
                 PreparedStatement ps = cn.prepareStatement(sqlFallback);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String th = rs.getString(6);
                    rows.add(new Object[]{
                            rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5),
                            (th == null || th.isEmpty()) ? "—" : th,
                            "Validé"
                    });
                }
            } catch (Exception e2) {
                System.out.println("Erreur chargement utilisateurs : " + e2.getMessage());
            }
        }
        Object[][] out = new Object[rows.size()][7];
        for (int i = 0; i < rows.size(); i++) out[i] = rows.get(i);
        return out;
    }

    private void rechargerUtilisateurs() {
        if (tableUtilisateurs == null) return;
        String[] cols = {"ID", "Nom", "Prénom", "Email", "Rôle", "Type handicap", "Statut compte"};
        DefaultTableModel m = readOnlyModel(chargerUtilisateurs(), cols);
        tableUtilisateurs.setModel(m);
        tableUtilisateurs.getColumnModel().getColumn(0).setMaxWidth(60);
        tableUtilisateurs.getColumnModel().getColumn(6).setCellRenderer(new ValidationRenderer());
    }

    private void validerCompteSelectionne() {
        int row = tableUtilisateurs.getSelectedRow();
        if (row < 0) { info("Sélectionnez un compte."); return; }

        int id = (Integer) tableUtilisateurs.getValueAt(row, 0);
        String statut = tableUtilisateurs.getValueAt(row, 6).toString();

        if (statut.equalsIgnoreCase("Validé")) {
            info("Ce compte est déjà validé.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this,
                "Valider ce compte utilisateur ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        // Tente l'UPDATE — si la colonne n'existe pas, on l'ajoute automatiquement
        String sql = "UPDATE utilisateur SET valide = 1 WHERE id = ?";
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            info("Compte validé.");
            rechargerUtilisateurs();
        } catch (Exception ex) {
            // Si erreur (colonne manquante), on propose l'ajout
            int addCol = JOptionPane.showConfirmDialog(this,
                    "La colonne 'valide' n'existe pas dans la table utilisateur.\n"
                    + "Voulez-vous l'ajouter automatiquement ?",
                    "Migration BDD", JOptionPane.YES_NO_OPTION);
            if (addCol == JOptionPane.YES_OPTION) {
                try (Connection cn = dao.ConnexionDB.getConnection();
                     PreparedStatement ps = cn.prepareStatement(
                             "ALTER TABLE utilisateur ADD COLUMN valide TINYINT(1) DEFAULT 0")) {
                    ps.executeUpdate();
                    // Réessayer la validation
                    try (Connection cn2 = dao.ConnexionDB.getConnection();
                         PreparedStatement ps2 = cn2.prepareStatement(sql)) {
                        ps2.setInt(1, id);
                        ps2.executeUpdate();
                        info("Colonne ajoutée et compte validé.");
                        rechargerUtilisateurs();
                    }
                } catch (Exception e2) {
                    info("Erreur migration : " + e2.getMessage());
                }
            }
        }
    }

    private void ouvrirDialogAjoutCompte() {
        JTextField tNom    = new JTextField();
        JTextField tPrenom = new JTextField();
        JTextField tEmail  = new JTextField();
        JPasswordField tMdp= new JPasswordField();
        JComboBox<String> cRole = new JComboBox<>(new String[]{"ETUDIANT", "ADMIN"});
        JTextField tHand   = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(new JLabel("Nom :"));        form.add(tNom);
        form.add(new JLabel("Prénom :"));     form.add(tPrenom);
        form.add(new JLabel("Email :"));      form.add(tEmail);
        form.add(new JLabel("Mot de passe :"));form.add(tMdp);
        form.add(new JLabel("Rôle :"));       form.add(cRole);
        form.add(new JLabel("Type handicap (optionnel) :")); form.add(tHand);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Nouveau compte", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            Utilisateur u = new model.PersonneHandicap();
            u.setNom(tNom.getText().trim());
            u.setPrenom(tPrenom.getText().trim());
            u.setEmail(tEmail.getText().trim());
            u.setMotDePasse(new String(tMdp.getPassword()).trim());

            String hand = tHand.getText().trim();
            utilisateurCtrl.ajouterUtilisateur(u, (String) cRole.getSelectedItem(),
                    hand.isEmpty() ? null : hand);
            rechargerUtilisateurs();
        }
    }

    private void ouvrirDialogModifCompte() {
        int row = tableUtilisateurs.getSelectedRow();
        if (row < 0) { info("Sélectionnez un utilisateur."); return; }

        int id = (Integer) tableUtilisateurs.getValueAt(row, 0);

        JTextField tNom    = new JTextField(tableUtilisateurs.getValueAt(row, 1).toString());
        JTextField tPrenom = new JTextField(tableUtilisateurs.getValueAt(row, 2).toString());
        JTextField tEmail  = new JTextField(tableUtilisateurs.getValueAt(row, 3).toString());

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.add(new JLabel("Nom :"));    form.add(tNom);
        form.add(new JLabel("Prénom :")); form.add(tPrenom);
        form.add(new JLabel("Email :"));  form.add(tEmail);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Modifier le compte #" + id, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            utilisateurCtrl.modifierUtilisateur(id,
                    tNom.getText().trim(),
                    tPrenom.getText().trim(),
                    tEmail.getText().trim());
            rechargerUtilisateurs();
        }
    }

    private void supprimerCompte() {
        int row = tableUtilisateurs.getSelectedRow();
        if (row < 0) { info("Sélectionnez un utilisateur."); return; }

        int id = (Integer) tableUtilisateurs.getValueAt(row, 0);
        int ok = JOptionPane.showConfirmDialog(this,
                "Supprimer ce compte définitivement ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            utilisateurCtrl.supprimerUtilisateur(id);
            rechargerUtilisateurs();
        }
    }

    // === PAGE 5 — ARCHIVAGE ===
    private String filtreArchiveActuel = "TOUS"; // TOUS, ACCEPTEES, REFUSEES, RECLAMATIONS

    private JPanel buildArchivagePage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 36, 28, 36));

        JPanel header = pageHeader(
                "Archivage et historique",
                "Consulter l'historique complet et rechercher par critères"
        );

        // FILTRES (chips)
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 4, 0));

        JButton btnTous       = filterChip("Tout",                  true);
        JButton btnAcceptees  = filterChip("Demandes acceptées",   false);
        JButton btnRefusees   = filterChip("Demandes refusées",    false);
        JButton btnRecl       = filterChip("Réclamations",         false);

        ArrayList<JButton> archChips = new ArrayList<>();
        archChips.add(btnTous);
        archChips.add(btnAcceptees);
        archChips.add(btnRefusees);
        archChips.add(btnRecl);

        btnTous.addActionListener(e       -> { setActiveChip(archChips, btnTous);      filtreArchiveActuel = "TOUS";         rechargerArchives(); });
        btnAcceptees.addActionListener(e  -> { setActiveChip(archChips, btnAcceptees); filtreArchiveActuel = "ACCEPTEES";    rechargerArchives(); });
        btnRefusees.addActionListener(e   -> { setActiveChip(archChips, btnRefusees);  filtreArchiveActuel = "REFUSEES";     rechargerArchives(); });
        btnRecl.addActionListener(e       -> { setActiveChip(archChips, btnRecl);      filtreArchiveActuel = "RECLAMATIONS"; rechargerArchives(); });

        filterRow.add(btnTous);
        filterRow.add(btnAcceptees);
        filterRow.add(btnRefusees);
        filterRow.add(btnRecl);

        // RECHERCHE
        JPanel search = new JPanel(new BorderLayout(10, 0));
        search.setOpaque(false);
        search.setBorder(new EmptyBorder(0, 0, 8, 0));

        JTextField tSearch = new JTextField();
        tSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorderRounded(BORDER, 1, 8),
                new EmptyBorder(8, 12, 8, 12)));
        tSearch.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel hint = new JLabel("Rechercher : ");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 13));
        hint.setForeground(MUTED);

        JButton btnSearch = primaryButton("Rechercher");

        search.add(hint,      BorderLayout.WEST);
        search.add(tSearch,   BorderLayout.CENTER);
        search.add(btnSearch, BorderLayout.EAST);

        // TABLE
        String[] cols = {"ID", "Type document", "Date d'archivage"};
        DefaultTableModel model = readOnlyModel(chargerArchives("", filtreArchiveActuel), cols);
        tableArchives = new JTable(model);
        styleTable(tableArchives);
        tableArchives.getColumnModel().getColumn(0).setMaxWidth(60);

        btnSearch.addActionListener(e -> {
            DefaultTableModel m = readOnlyModel(
                    chargerArchives(tSearch.getText().trim(), filtreArchiveActuel), cols);
            tableArchives.setModel(m);
            tableArchives.getColumnModel().getColumn(0).setMaxWidth(60);
        });

        JScrollPane scroll = new JScrollPane(tableArchives);
        scroll.setBorder(new LineBorderRounded(BORDER, 1, 12));
        scroll.getViewport().setBackground(CARD);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnRefresh = secondaryButton("Actualiser");
        btnRefresh.addActionListener(e -> {
            tSearch.setText("");
            rechargerArchives();
        });

        actions.add(btnRefresh);

        // Empilage : filtres -> recherche -> table -> actions
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        filterRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        search.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(filterRow);
        top.add(Box.createVerticalStrut(10));
        top.add(search);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(top,     BorderLayout.NORTH);
        center.add(scroll,  BorderLayout.CENTER);
        center.add(actions, BorderLayout.SOUTH);

        page.add(header, BorderLayout.NORTH);
        page.add(center, BorderLayout.CENTER);
        return page;
    }

    private Object[][] chargerArchives(String filtre, String typeFiltre) {
        String sql = "SELECT idArchive, typeDocument, dateArchivage FROM archive WHERE 1=1";

        if (typeFiltre.equals("ACCEPTEES")) {
            sql += " AND typeDocument LIKE '%ACCEPTEE%'";
        } else if (typeFiltre.equals("REFUSEES")) {
            sql += " AND typeDocument LIKE '%REFUSEE%'";
        } else if (typeFiltre.equals("RECLAMATIONS")) {
            sql += " AND typeDocument LIKE '%Réclamation%'";
        }

        if (filtre != null && !filtre.isEmpty()) {
            sql += " AND typeDocument LIKE ?";
        }
        sql += " ORDER BY idArchive DESC";

        ArrayList<Object[]> rows = new ArrayList<>();
        try (Connection cn = dao.ConnexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (filtre != null && !filtre.isEmpty()) {
                ps.setString(1, "%" + filtre + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getInt(1), rs.getString(2), rs.getString(3)
                    });
                }
            }
        } catch (Exception ex) {
            System.out.println("Erreur archives : " + ex.getMessage());
        }
        Object[][] out = new Object[rows.size()][3];
        for (int i = 0; i < rows.size(); i++) out[i] = rows.get(i);
        return out;
    }

    private void rechargerArchives() {
        if (tableArchives == null) return;
        String[] cols = {"ID", "Type document", "Date d'archivage"};
        DefaultTableModel m = readOnlyModel(chargerArchives("", filtreArchiveActuel), cols);
        tableArchives.setModel(m);
        tableArchives.getColumnModel().getColumn(0).setMaxWidth(60);
    }

    // === PAGE 6 — PARAMÈTRES ===
    private JPanel buildParametresPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(28, 36, 28, 36));

        JPanel header = pageHeader(
                "Paramètres",
                "Gestion du compte administrateur et préférences"
        );

        JPanel card = cardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorderRounded(BORDER, 1, 12),
                new EmptyBorder(28, 32, 28, 32)
        ));

        JLabel s1 = new JLabel("Compte administrateur");
        s1.setFont(new Font("SansSerif", Font.BOLD, 16));
        s1.setForeground(TEXT);
        s1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel s1d = new JLabel("Gérez vos informations personnelles et de sécurité.");
        s1d.setFont(new Font("SansSerif", Font.PLAIN, 13));
        s1d.setForeground(MUTED);
        s1d.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel s2 = new JLabel("À propos");
        s2.setFont(new Font("SansSerif", Font.BOLD, 16));
        s2.setForeground(TEXT);
        s2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel s2d = new JLabel("<html>AccèsU v1.0 — Système de gestion des demandes et réclamations<br>"
                + "des personnes en situation de handicap.<br>"
                + "Université Internationale de Rabat — Projet Intégré 2025/2026</html>");
        s2d.setFont(new Font("SansSerif", Font.PLAIN, 13));
        s2d.setForeground(MUTED);
        s2d.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(s1);
        card.add(Box.createVerticalStrut(6));
        card.add(s1d);
        card.add(Box.createVerticalStrut(28));
        card.add(s2);
        card.add(Box.createVerticalStrut(6));
        card.add(s2d);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card, BorderLayout.NORTH);

        page.add(header, BorderLayout.NORTH);
        page.add(wrap,   BorderLayout.CENTER);
        return page;
    }

    // === HELPERS UI ===
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

    private JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        return p;
    }

    private JButton primaryButton(String text)   { return styledButton(text, PRIMARY,   Color.WHITE, PRIMARY_LT,           null, false); }
    private JButton secondaryButton(String text) { return styledButton(text, CARD,      TEXT,        SOFT,                 BORDER, true);  }
    private JButton dangerButton(String text)    { return styledButton(text, RED_BADGE, RED,         new Color(0xEED6D6),  null, false); }

    /** Construit un bouton stylé selon les couleurs passées. */
    private JButton styledButton(String text, Color bg, Color fg, Color hover, Color border, boolean rounded) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(bg);
        b.setForeground(fg);
        if (rounded && border != null) {
            b.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorderRounded(border, 1, 8),
                    new EmptyBorder(8, 16, 8, 16)));
        } else {
            b.setBorder(new EmptyBorder(9, 18, 9, 18));
        }
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private JButton filterChip(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBackground(active ? PRIMARY : CARD);
        b.setForeground(active ? Color.WHITE : TEXT);
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorderRounded(active ? PRIMARY : BORDER, 1, 18),
                new EmptyBorder(6, 14, 6, 14)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

    /** Crée un DefaultTableModel non-éditable (factorisation). */
    private DefaultTableModel readOnlyModel(Object[][] data, String[] cols) {
        return new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    // === RENDERER POUR BADGES STATUT ===
    private class StatutRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                                                       boolean selected, boolean focus, int r, int c) {
            super.getTableCellRendererComponent(t, value, selected, focus, r, c);

            String s = value == null ? "" : value.toString().toUpperCase();
            Color bg, fg;
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

            JPanel pill = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
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

    // === RENDERER POUR STATUT DE COMPTE (Validé / En attente) ===
    private class ValidationRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                                                       boolean selected, boolean focus, int r, int c) {
            super.getTableCellRendererComponent(t, value, selected, focus, r, c);

            String s = value == null ? "" : value.toString();
            final Color bg, fg;
            if (s.equalsIgnoreCase("Validé")) {
                bg = GREEN_BADGE; fg = new Color(0x2E7D32);
            } else {
                bg = YELLOW_BADGE; fg = new Color(0x8B6914);
            }

            JPanel pill = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(selected ? new Color(0xE9E5DC) : CARD);
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    int w = 100, h = 24;
                    int x = 14;
                    int y = (getHeight() - h) / 2;
                    g2.setColor(bg);
                    g2.fillRoundRect(x, y, w, h, 14, 14);
                    g2.setColor(fg);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    int strW = g2.getFontMetrics().stringWidth(s);
                    g2.drawString(s, x + (w - strW) / 2, y + 16);
                    g2.dispose();
                }
            };
            pill.setOpaque(false);
            return pill;
        }
    }

    // === BORDURE ARRONDIE ===
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
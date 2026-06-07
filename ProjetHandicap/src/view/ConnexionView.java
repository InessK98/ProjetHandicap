package view;

import controller.AuthController;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ConnexionView extends JFrame {

    private static final Color C_BG            = new Color(0xE4DFD6);
    private static final Color C_CARD          = Color.WHITE;
    private static final Color C_PRIMARY       = new Color(0x445343);
    private static final Color C_PRIMARY_LIGHT = new Color(0x5C6B5A);
    private static final Color C_FIELD         = new Color(0xF6F3EE);
    private static final Color C_TEXT          = new Color(0x1C1C19);
    private static final Color C_MUTED         = new Color(0x444842);
    private static final Color C_ERROR         = new Color(0xBA1A1A);

    private static final int CARD_RADIUS = 16;

    private JTextField tfEmail;
    private JPasswordField tfMotDePasse;
    private JLabel lblMessage;

    private final AuthController authController = new AuthController();

    public ConnexionView() {
        setTitle("AccèsU — Connexion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 680);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
    JPanel root = new JPanel(new GridBagLayout());
    root.setBackground(C_BG);

    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(C_CARD);
    card.setPreferredSize(new Dimension(430, 560));
    card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(CARD_RADIUS, C_CARD),
            new EmptyBorder(45, 45, 35, 45)
    ));

    JPanel topLine = new JPanel();
    topLine.setPreferredSize(new Dimension(430, 5));
    topLine.setBackground(C_PRIMARY);

    JPanel header = new JPanel();
    header.setOpaque(false);
    header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

    JLabel title = new JLabel("AccèsU");
    title.setFont(new Font("SansSerif", Font.BOLD, 34));
    title.setForeground(C_PRIMARY);
    title.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel subtitle = new JLabel("SYSTÈME DE GESTION DU HANDICAP");
    subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
    subtitle.setForeground(C_MUTED);
    subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

    header.add(title);
    header.add(Box.createVerticalStrut(10));
    header.add(subtitle);
    header.add(Box.createVerticalStrut(35));

    // ── FORM ──────────────────────────────────────────────
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1.0;
    c.gridx = 0;

    c.gridy = 0; c.insets = new Insets(0, 0, 6, 0);
    form.add(label("Adresse e-mail"), c);

    c.gridy = 1; c.insets = new Insets(0, 0, 20, 0);
    tfEmail = field();
    form.add(tfEmail, c);

    c.gridy = 2; c.insets = new Insets(0, 0, 6, 0);
    form.add(label("Mot de passe"), c);

    c.gridy = 3; c.insets = new Insets(0, 0, 30, 0);
    tfMotDePasse = passwordField();
    form.add(tfMotDePasse, c);

    c.gridy = 4; c.insets = new Insets(0, 0, 14, 0);
    JButton btnConnexion = button("Se connecter");
    btnConnexion.addActionListener(e -> handleConnexion());
    form.add(btnConnexion, c);

    c.gridy = 5; c.insets = new Insets(0, 0, 0, 0);
    lblMessage = new JLabel(" ");
    lblMessage.setFont(new Font("SansSerif", Font.PLAIN, 12));
    lblMessage.setForeground(C_ERROR);
    lblMessage.setHorizontalAlignment(SwingConstants.CENTER);
    form.add(lblMessage, c);
    // ──────────────────────────────────────────────────────

    JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
    footer.setOpaque(false);
    JLabel secure = new JLabel("🔒 Université Inclusive — Espace sécurisé");
    secure.setFont(new Font("SansSerif", Font.PLAIN, 12));
    secure.setForeground(C_MUTED);
    footer.add(secure);

    JPanel center = new JPanel(new BorderLayout());
    center.setOpaque(false);
    center.add(header, BorderLayout.NORTH);
    center.add(form, BorderLayout.CENTER);

    card.add(topLine, BorderLayout.NORTH);
    card.add(center, BorderLayout.CENTER);
    card.add(footer, BorderLayout.SOUTH);

    root.add(card);
    return root;
}

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(C_TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField field() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tf.setForeground(C_TEXT);
        tf.setBackground(C_FIELD);
        tf.setBorder(new EmptyBorder(12, 14, 12, 14));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tf;
    }

    private JPasswordField passwordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pf.setForeground(C_TEXT);
        pf.setBackground(C_FIELD);
        pf.setBorder(new EmptyBorder(12, 14, 12, 14));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        return pf;
    }

    private JButton button(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(C_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(C_PRIMARY_LIGHT);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(C_PRIMARY);
            }
        });

        return btn;
    }

    private void handleConnexion() {
        String email = tfEmail.getText().trim();
        String motDePasse = new String(tfMotDePasse.getPassword()).trim();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            lblMessage.setText("Veuillez remplir tous les champs.");
            lblMessage.setForeground(C_ERROR);
            return;
        }

        String role = authController.seConnecter(email, motDePasse);

        if (role == null) {
            lblMessage.setText("Email ou mot de passe incorrect.");
            lblMessage.setForeground(C_ERROR);
            return;
        }

        lblMessage.setText("Connexion réussie.");
        lblMessage.setForeground(C_PRIMARY);

        dispose();

        if (role.equalsIgnoreCase("ADMIN")) {
            new DashboardAdminView().setVisible(true);
        } else {
            new EtudiantView(email).setVisible(true);
        }
    }

    // =========================================================
    // BORDURE ARRONDIE
    // =========================================================
    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }
    }

}
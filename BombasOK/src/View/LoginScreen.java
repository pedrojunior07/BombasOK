/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package View;

import Controller.GenericDao;
import Model.Definicoes;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

/**
 *
 * @author Heisenberg
 */

/**
 * Login Screen for Gas Station Management System
 */
public class LoginScreen extends JFrame {
    private JPanel mainPanel;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JCheckBox rememberCheckbox;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JLabel statusLabel;
    private Timer shakeTimer;
    private int shakeCount = 0;
    private Point originalLocation;
    
    // DAO for authentication
    private final GenericDao<Definicoes> definicaoDao;
    
    public LoginScreen() {
        definicaoDao = new GenericDao<>(Definicoes.class);
        
        setTitle("Login - Sistema de Gestão de Bombas de Gasolina");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 900, 600, 15, 15));
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        initializeComponents();
        setupEventHandlers();
        
        add(cardPanel);
    }
    
    private void initializeComponents() {
        // Create the main login panel
        mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background for the left portion
                g2d.setColor(new Color(0, 100, 140));
                g2d.fillRect(0, 0, getWidth() / 2, getHeight());
                
                // Draw decorative elements on the left side
                g2d.setColor(new Color(0, 120, 160, 50));
                g2d.fillOval(-50, -50, 200, 200);
                g2d.fillOval(getWidth()/4, getHeight()/2, 300, 300);
                g2d.fillOval(50, getHeight() - 100, 150, 150);
                
                g2d.dispose();
            }
        };
        
        // Left side - Image and title
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        
//        JLabel iconLabel = new JLabel(createIcon("/resources/gas-station-large.png", 120, 120));
        
        JLabel titleLabel = new JLabel("Sistema de Gestão de Bombas");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel subtitleLabel = new JLabel("Combustíveis • Lavagem • Loja");
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
//        leftPanel.add(iconLabel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        leftPanel.add(titleLabel, gbc);
        
        gbc.gridy = 2;
        leftPanel.add(subtitleLabel, gbc);
        
        // Right side - Login form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        
        JLabel loginLabel = new JLabel("Acesso ao Sistema");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel userLabel = new JLabel("Utilizador");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        userField = new JTextField(20);
        userField.setPreferredSize(new Dimension(300, 40));
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel passLabel = new JLabel("Senha");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        rememberCheckbox = new JCheckBox("Lembrar-me");
        rememberCheckbox.setFont(new Font("Arial", Font.PLAIN, 14));
        
        loginButton = new JButton("Entrar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 120, 170), 
                        getWidth(), getHeight(), new Color(0, 90, 140));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2d);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        loginButton.setPreferredSize(new Dimension(300, 45));
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add close button in the top-right corner
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.setOpaque(false);
        
        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> System.exit(0));
        
        closePanel.add(closeButton);
        
        // Add components to the right panel
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 0;
        gbcRight.gridy = 0;
        gbcRight.anchor = GridBagConstraints.WEST;
        gbcRight.insets = new Insets(0, 0, 30, 0);
        rightPanel.add(loginLabel, gbcRight);
        
        gbcRight.gridy = 1;
        gbcRight.insets = new Insets(0, 0, 5, 0);
        rightPanel.add(userLabel, gbcRight);
        
        gbcRight.gridy = 2;
        gbcRight.insets = new Insets(0, 0, 20, 0);
        rightPanel.add(userField, gbcRight);
        
        gbcRight.gridy = 3;
        gbcRight.insets = new Insets(0, 0, 5, 0);
        rightPanel.add(passLabel, gbcRight);
        
        gbcRight.gridy = 4;
        gbcRight.insets = new Insets(0, 0, 20, 0);
        rightPanel.add(passwordField, gbcRight);
        
        gbcRight.gridy = 5;
        gbcRight.insets = new Insets(0, 0, 30, 0);
        rightPanel.add(rememberCheckbox, gbcRight);
        
        gbcRight.gridy = 6;
        gbcRight.insets = new Insets(0, 0, 10, 0);
        rightPanel.add(loginButton, gbcRight);
        
        gbcRight.gridy = 7;
        rightPanel.add(statusLabel, gbcRight);
        
        // Add panels to main panel
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 0.5;
        gbcMain.weighty = 1.0;
        gbcMain.fill = GridBagConstraints.BOTH;
        mainPanel.add(leftPanel, gbcMain);
        
        gbcMain.gridx = 1;
        mainPanel.add(rightPanel, gbcMain);
        
        // Add the close panel to the top
        JPanel overlayPanel = new JPanel(new BorderLayout());
        overlayPanel.setOpaque(false);
        overlayPanel.add(closePanel, BorderLayout.NORTH);
        overlayPanel.add(mainPanel, BorderLayout.CENTER);
        
        // Add to card panel
        cardPanel.add(overlayPanel, "login");
        
        // Create loading panel (shown during authentication)
        JPanel loadingPanel = createLoadingPanel();
        cardPanel.add(loadingPanel, "loading");
    }
    
    private JPanel createLoadingPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 100, 140), 
                        getWidth(), getHeight(), new Color(0, 60, 100));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        
        // Create a loading animation
        JLabel spinnerLabel = new JLabel(createSpinnerIcon());
        
        JLabel loadingLabel = new JLabel("Autenticando...");
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(spinnerLabel, gbc);
        
        gbc.gridy = 1;
        panel.add(loadingLabel, gbc);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> attemptLogin());
        
        // Allow login with Enter key
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        });
        
        // Make window draggable
        MouseAdapter dragAdapter = new MouseAdapter() {
            private int offsetX, offsetY;
            
            @Override
            public void mousePressed(MouseEvent e) {
                offsetX = e.getX();
                offsetY = e.getY();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - offsetX, e.getYOnScreen() - offsetY);
            }
        };
        
        addMouseListener(dragAdapter);
        addMouseMotionListener(dragAdapter);
    }
    
    private void attemptLogin() {
        String username = userField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Por favor, preencha todos os campos.");
            shakeLoginForm();
            return;
        }
        
        // Show loading screen
        cardLayout.show(cardPanel, "loading");
        
        // Simulate network delay for authentication
        new Timer(1500, e -> {
            ((Timer) e.getSource()).stop();
            
            // Authenticate using DAO
            boolean loginSuccess = authenticateUser(username, password);
            
            if (loginSuccess) {
                // Open dashboard
                dispose();
                openDashboard();
            } else {
                cardLayout.show(cardPanel, "login");
                statusLabel.setText("Credenciais inválidas. Tente novamente.");
                shakeLoginForm();
                passwordField.setText("");
            }
        }).start();
    }
    
    private boolean authenticateUser(String username, String password) {
        ArrayList<Definicoes> defList = definicaoDao.getAll();
        
        // If no settings exist, create default admin
        if (defList.isEmpty()) {
            Definicoes defaultSettings = new Definicoes();
            defaultSettings.setNomeDaEmpresa("Postos de Combustível");
            defaultSettings.setUserName("admin");
            defaultSettings.setSenha("admin");
            defaultSettings.setDarkMode(false);
            defaultSettings.setIva(16.0);
            definicaoDao.insert(defaultSettings);
            
            // Check if default credentials match
            return username.equals("admin") && password.equals("admin");
        } else {
            // Check if credentials match existing settings
            for (Definicoes def : defList) {
                if (def.getUserName().equals(username) && def.getSenha().equals(password)) {
                    // Apply theme if specified in settings
                    applyTheme(def.isDarkMode());
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void applyTheme(boolean darkMode) {
        try {
            if (darkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void openDashboard() {
        SwingUtilities.invokeLater(() -> {
            try {
                new DashboardBombasOK().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                        "Erro ao abrir o Dashboard: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void shakeLoginForm() {
        if (shakeTimer != null && shakeTimer.isRunning()) {
            return;
        }
        
        originalLocation = getLocation();
        shakeCount = 0;
        
        shakeTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int offset = 10;
                if (shakeCount % 2 == 0) {
                    setLocation(originalLocation.x + offset, originalLocation.y);
                } else {
                    setLocation(originalLocation.x - offset, originalLocation.y);
                }
                
                shakeCount++;
                if (shakeCount > 6) {
                    ((Timer) e.getSource()).stop();
                    setLocation(originalLocation);
                }
            }
        });
        
        shakeTimer.start();
    }
    
    private ImageIcon createSpinnerIcon() {
        // Create a simple spinner animation
        int size = 50;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw spinner
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(4));
        
        // Draw a circle with a gap
        g2d.drawArc(5, 5, size - 10, size - 10, 30, 300);
        
        g2d.dispose();
        
        // Create animation
        Timer animTimer = new Timer(50, null);
        JLabel label = new JLabel(new ImageIcon(img));
        
        animTimer.addActionListener(new ActionListener() {
            private int angle = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                angle = (angle + 10) % 360;
                
                BufferedImage rotated = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = rotated.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Rotate around center
                g2d.rotate(Math.toRadians(angle), size / 2, size / 2);
                g2d.drawImage(img, 0, 0, null);
                g2d.dispose();
                
                label.setIcon(new ImageIcon(rotated));
            }
        });
        
        animTimer.start();
        
        return (ImageIcon) label.getIcon();
    }
    
    // Main method to run the application
    public static void main(String[] args) {
        try {
            // Set FlatLaf theme
            UIManager.setLookAndFeel(new FlatLightLaf());
            
            // Add some custom properties to FlatLaf
            UIManager.put("TextField.arc", 10);
            UIManager.put("Button.arc", 10);
            UIManager.put("CheckBox.arc", 5);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.innerFocusWidth", 1);
            UIManager.put("Button.innerFocusWidth", 1);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            // Start with splash screen
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
        });
    }
}
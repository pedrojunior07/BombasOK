package View;

import Controller.GenericDao;
import Model.Definicoes;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.*;
import java.util.Optional;
import javax.swing.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Splash Screen for Gas Station Management System
 */
public class SplashScreen extends JWindow {
    private Timer progressTimer;
    private JProgressBar progressBar;
    private int progress = 0;
    private JLabel logoLabel;
    private JLabel loadingLabel;
    
    public SplashScreen() {
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 100, 140), 
                        getWidth(), getHeight(), new Color(0, 60, 100));
                g2d.setPaint(gradient);
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        
        // Logo
        ImageIcon logo = createIcon("/resources/gas-station.png", 120, 120);
        logoLabel = new JLabel(logo);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // System title
        JLabel titleLabel = new JLabel("Sistema de Gestão de Bombas de Gasolina");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Loading message
        loadingLabel = new JLabel("Carregando recursos...");
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loadingLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(400, 10));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(0, 60, 90));
        progressBar.setForeground(new Color(0, 200, 83));
        
        // Layout components
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 20, 0);
        centerPanel.add(logoLabel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        centerPanel.add(titleLabel, gbc);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new GridBagLayout());
        southPanel.setOpaque(false);
        GridBagConstraints gbcSouth = new GridBagConstraints();
        gbcSouth.gridx = 0;
        gbcSouth.gridy = 0;
        gbcSouth.insets = new Insets(0, 0, 5, 0);
        southPanel.add(loadingLabel, gbcSouth);
        
        gbcSouth.gridy = 1;
        gbcSouth.insets = new Insets(0, 0, 30, 0);
        southPanel.add(progressBar, gbcSouth);
        
        panel.add(southPanel, BorderLayout.SOUTH);
        
        add(panel);
        
        // Add animation effect
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                startProgress();
            }
        });
    }
    
    private void startProgress() {
        String[] loadingMessages = {
            "Carregando recursos...",
            "Configurando sistema...",
            "Verificando atualizações...",
            "Inicializando serviços...",
            "Preparando interface..."
        };
        
        progressTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progress++;
                progressBar.setValue(progress);
                
                // Update loading message at different stages
                if (progress == 20 || progress == 40 || progress == 60 || progress == 80) {
                    int index = progress / 20;
                    loadingLabel.setText(loadingMessages[index]);
                    
                    // Animate the logo by slightly changing its size
                    new Thread(() -> {
                        try {
                            logoLabel.setIcon(createIcon("/resources/gas-station.png", 130, 130));
                            Thread.sleep(100);
                            logoLabel.setIcon(createIcon("/resources/gas-station.png", 120, 120));
                        } catch (Exception ex) {
                            // Ignore animation exceptions
                        }
                    }).start();
                }
                
                if (progress >= 100) {
                    progressTimer.stop();
                    dispose();
                    
                    // Show login screen with a small delay
                    SwingUtilities.invokeLater(() -> {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        new LoginScreen().setVisible(true);
                    });
                }
            }
        });
        progressTimer.start();
    }
    
    // Utility method to create an icon (as a placeholder - replace with actual resources)
    private ImageIcon createIcon(String path, int width, int height) {
        // This is a placeholder - in a real application, you'd load from resources
        // For now, we'll create a simple icon programmatically
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a gas pump icon
        g2d.setColor(Color.WHITE);
        int padding = width / 6;
        
        // Pump base
        g2d.fillRoundRect(padding, height/2, width - padding*2, height/2 - padding, 10, 10);
        
        // Pump top
        g2d.fillRoundRect(padding + width/6, padding, width - padding*2 - width/3, height/2, 10, 10);
        
        // Nozzle
        g2d.fillRoundRect(width - padding*2, height/3, padding, padding, 5, 5);
        
        g2d.dispose();
        return new ImageIcon(img);
    }
}

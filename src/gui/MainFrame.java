package gui;

import service.GestorVestuario;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private GestorVestuario gestor;
    private JTabbedPane tabbedPane;
    
    public MainFrame() {
        this.gestor = new GestorVestuario();
        initializeComponents();
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("GVP - Gestor de Vestuário Pessoal");
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // abas
        tabbedPane.addTab("Itens", new ItemPanel(gestor));
        tabbedPane.addTab("Looks", new LookPanel(gestor));
        tabbedPane.addTab("Empréstimos", new EmprestimoPanel(gestor));
        tabbedPane.addTab("Lavagens", new LavagemPanel(gestor));
        tabbedPane.addTab("Estatísticas", new EstatisticaPanel(gestor));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        
        // Barra de status
        JLabel statusBar = new JLabel("GVP - Sistema de Gestão de Vestuário Pessoal");
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        add(statusBar, BorderLayout.SOUTH);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
            	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //deixa no tema do SO
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame().setVisible(true);
        });
    }
}
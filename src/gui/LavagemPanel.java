package gui;

import interfaces.ILavavel;
import model.*;
import service.GestorVestuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.UUID;

public class LavagemPanel extends JPanel {
    private GestorVestuario gestor;
    private JTable tabelaLavagens;
    private DefaultTableModel modeloTabela;
    private JList<Item> listaItensLavaveis;
    private DefaultListModel<Item> modeloListaItens;
    private JTextField txtObservacoes;
    
    public LavagemPanel(GestorVestuario gestor) {
        this.gestor = gestor;
        initializeComponents();
        setupLayout();
        carregarDados();
    }
    
    private void initializeComponents() {
        // Tabela de lavagens
        String[] colunas = {"Data", "Qtd Itens", "Observações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaLavagens = new JTable(modeloTabela);
        
        // Lista de itens laváveis
        modeloListaItens = new DefaultListModel<>();
        listaItensLavaveis = new JList<>(modeloListaItens);
        listaItensLavaveis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        txtObservacoes = new JTextField(30);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel superior - Nova lavagem
        JPanel painelLavagem = new JPanel(new BorderLayout());
        painelLavagem.setBorder(BorderFactory.createTitledBorder("Registrar Lavagem"));
        
        JPanel painelObservacoes = new JPanel(new FlowLayout());
        painelObservacoes.add(new JLabel("Observações:"));
        painelObservacoes.add(txtObservacoes);
        
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(new JButton("Registrar Lavagem") {{ 
            addActionListener(e -> registrarLavagem()); 
        }});
        painelBotoes.add(new JButton("Atualizar") {{ 
            addActionListener(e -> carregarDados()); 
        }});
        
        painelLavagem.add(painelObservacoes, BorderLayout.NORTH);
        painelLavagem.add(new JScrollPane(listaItensLavaveis), BorderLayout.CENTER);
        painelLavagem.add(painelBotoes, BorderLayout.SOUTH);
        
        // Painel inferior - Histórico de lavagens
        JPanel painelHistorico = new JPanel(new BorderLayout());
        painelHistorico.setBorder(BorderFactory.createTitledBorder("Histórico de Lavagens"));
        painelHistorico.add(new JScrollPane(tabelaLavagens), BorderLayout.CENTER);
        
        // Layout principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, painelLavagem, painelHistorico);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void registrarLavagem() {
        java.util.List<Item> itensSelecionados = listaItensLavaveis.getSelectedValuesList();
        if (itensSelecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um item para lavar!");
            return;
        }
        
        String id = UUID.randomUUID().toString();
        Lavagem lavagem = new Lavagem(id, LocalDate.now());
        lavagem.setObservacoes(txtObservacoes.getText());
        
        for (Item item : itensSelecionados) {
            if (item instanceof ILavavel) {
                lavagem.adicionarItem((ILavavel) item);
            }
        }
        
        gestor.adicionarLavagem(lavagem);
        txtObservacoes.setText("");
        carregarDados();
        JOptionPane.showMessageDialog(this, "Lavagem registrada com sucesso!");
    }
    
    private void carregarDados() {
        carregarItensLavaveis();
        carregarLavagens();
    }
    
    private void carregarItensLavaveis() {
        modeloListaItens.clear();
        for (Item item : gestor.listarItens()) {
            if (item instanceof ILavavel) {
                modeloListaItens.addElement(item);
            }
        }
    }
    
    private void carregarLavagens() {
        modeloTabela.setRowCount(0);
        for (Lavagem lavagem : gestor.listarLavagens()) {
            Object[] row = {
                lavagem.getData(),
                lavagem.getItensLavados().size(),
                lavagem.getObservacoes()
            };
            modeloTabela.addRow(row);
        }
    }
}
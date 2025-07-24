package gui;

import model.*;
import service.GestorVestuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.UUID;

public class ItemPanel extends JPanel {
    private GestorVestuario gestor;
    private JTable tabelaItens;
    private DefaultTableModel modeloTabela;
    private JTextField txtNome, txtCor, txtTamanho, txtLoja;
    private JComboBox<Item.Conservacao> cbConservacao;
    private JComboBox<String> cbTipoItem;
    
    public ItemPanel(GestorVestuario gestor) {
        this.gestor = gestor;
        initializeComponents();
        setupLayout();
        carregarItens();
    }
    
    private void initializeComponents() {
        // Tabela
        String[] colunas = {"ID", "Nome", "Tipo", "Cor", "Tamanho", "Conservação", "Utilizações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaItens = new JTable(modeloTabela);
        tabelaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Campos de entrada
        txtNome = new JTextField(20);
        txtCor = new JTextField(15);
        txtTamanho = new JTextField(10);
        txtLoja = new JTextField(20);
        cbConservacao = new JComboBox<>(Item.Conservacao.values());
        cbTipoItem = new JComboBox<>(new String[]{"Camisa", "Calça", "Relógio", "Roupa Íntima"});
        
        // Botões
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnRemover = new JButton("Remover");
        JButton btnAtualizar = new JButton("Atualizar");
        
        btnAdicionar.addActionListener(e -> adicionarItem());
        btnRemover.addActionListener(e -> removerItem());
        btnAtualizar.addActionListener(e -> carregarItens());
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel superior com formulário
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Adicionar/Editar Item"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        painelFormulario.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        painelFormulario.add(txtNome, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        painelFormulario.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        painelFormulario.add(cbTipoItem, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        painelFormulario.add(new JLabel("Cor:"), gbc);
        gbc.gridx = 1;
        painelFormulario.add(txtCor, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        painelFormulario.add(new JLabel("Tamanho:"), gbc);
        gbc.gridx = 3;
        painelFormulario.add(txtTamanho, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        painelFormulario.add(new JLabel("Loja:"), gbc);
        gbc.gridx = 3;
        painelFormulario.add(txtLoja, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        painelFormulario.add(new JLabel("Conservação:"), gbc);
        gbc.gridx = 3;
        painelFormulario.add(cbConservacao, gbc);
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(new JButton("Adicionar") {{ addActionListener(e -> adicionarItem()); }});
        painelBotoes.add(new JButton("Remover") {{ addActionListener(e -> removerItem()); }});
        painelBotoes.add(new JButton("Atualizar") {{ addActionListener(e -> carregarItens()); }});
        
        add(painelFormulario, BorderLayout.NORTH);
        add(new JScrollPane(tabelaItens), BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }
    
    private void adicionarItem() {
        try {
            String nome = txtNome.getText().trim();
            String cor = txtCor.getText().trim();
            String tamanho = txtTamanho.getText().trim();
            String loja = txtLoja.getText().trim();
            Item.Conservacao conservacao = (Item.Conservacao) cbConservacao.getSelectedItem();
            String tipoItem = (String) cbTipoItem.getSelectedItem();
            
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome é obrigatório!");
                return;
            }
            
            String id = UUID.randomUUID().toString();
            Item item = null;
            
            switch (tipoItem) {
                case "Camisa":
                    item = new Camisa(id, nome, cor, tamanho, loja, conservacao);
                    break;
                case "Calça":
                    item = new Calca(id, nome, cor, tamanho, loja, conservacao);
                    break;
                case "Relógio":
                    item = new Relogio(id, nome, cor, tamanho, loja, conservacao);
                    break;
                case "Roupa Íntima":
                    item = new RoupaIntima(id, nome, cor, tamanho, loja, conservacao);
                    break;
            }
            
            if (item != null) {
                gestor.adicionarItem(item);
                limparCampos();
                carregarItens();
                JOptionPane.showMessageDialog(this, "Item adicionado com sucesso!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar item: " + e.getMessage());
        }
    }
    
    private void removerItem() {
        int selectedRow = tabelaItens.getSelectedRow();
        if (selectedRow >= 0) {
            String id = (String) modeloTabela.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja remover este item?", 
                "Confirmar Remoção", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                gestor.removerItem(id);
                carregarItens();
                JOptionPane.showMessageDialog(this, "Item removido com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover!");
        }
    }
    
    private void carregarItens() {
        modeloTabela.setRowCount(0);
        for (Item item : gestor.listarItens()) {
            Object[] row = {
                item.getId(),
                item.getNome(),
                item.getClass().getSimpleName(),
                item.getCor(),
                item.getTamanho(),
                item.getConservacao(),
                item.getQuantidadeUtilizacoes()
            };
            modeloTabela.addRow(row);
        }
    }
    
    private void limparCampos() {
        txtNome.setText("");
        txtCor.setText("");
        txtTamanho.setText("");
        txtLoja.setText("");
        cbConservacao.setSelectedIndex(0);
        cbTipoItem.setSelectedIndex(0);
    }
}
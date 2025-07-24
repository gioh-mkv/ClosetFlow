package gui;

import model.*;
import service.GestorVestuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.UUID;

public class LookPanel extends JPanel {
    private GestorVestuario gestor;
    private JTable tabelaLooks;
    private DefaultTableModel modeloTabela;
    private JTextField txtNomeLook;
    private JList<Item> listaItensDisponiveis;
    private DefaultListModel<Item> modeloListaItens;
    private Look lookSelecionado;
    
    public LookPanel(GestorVestuario gestor) {
        this.gestor = gestor;
        initializeComponents();
        setupLayout();
        carregarDados();
    }
    
    private void initializeComponents() {
        // Tabela de looks
        String[] colunas = {"ID", "Nome", "Qtd Itens", "Utilizações"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaLooks = new JTable(modeloTabela);
        tabelaLooks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaLooks.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarLook();
            }
        });
        
        // Campo nome do look
        txtNomeLook = new JTextField(20);
        
        // Lista de itens disponíveis
        modeloListaItens = new DefaultListModel<>();
        listaItensDisponiveis = new JList<>(modeloListaItens);
        listaItensDisponiveis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Painel superior - Criação de look
        JPanel painelCriacao = new JPanel(new BorderLayout());
        painelCriacao.setBorder(BorderFactory.createTitledBorder("Criar/Editar Look"));
        
        JPanel painelNome = new JPanel(new FlowLayout());
        painelNome.add(new JLabel("Nome do Look:"));
        painelNome.add(txtNomeLook);
        
        JPanel painelBotoesCriacao = new JPanel(new FlowLayout());
        painelBotoesCriacao.add(new JButton("Criar Look") {{ 
            addActionListener(e -> criarLook()); 
        }});
        painelBotoesCriacao.add(new JButton("Adicionar Itens") {{ 
            addActionListener(e -> adicionarItensAoLook()); 
        }});
        painelBotoesCriacao.add(new JButton("Registrar Uso") {{ 
            addActionListener(e -> registrarUsoLook()); 
        }});
        
        painelCriacao.add(painelNome, BorderLayout.NORTH);
        painelCriacao.add(new JScrollPane(listaItensDisponiveis), BorderLayout.CENTER);
        painelCriacao.add(painelBotoesCriacao, BorderLayout.SOUTH);
        
        // Painel central - Lista de looks
        JPanel painelLooks = new JPanel(new BorderLayout());
        painelLooks.setBorder(BorderFactory.createTitledBorder("Looks Criados"));
        painelLooks.add(new JScrollPane(tabelaLooks), BorderLayout.CENTER);
        
        JPanel painelBotoesLook = new JPanel(new FlowLayout());
        painelBotoesLook.add(new JButton("Remover Look") {{ 
            addActionListener(e -> removerLook()); 
        }});
        painelBotoesLook.add(new JButton("Ver Detalhes") {{ 
            addActionListener(e -> verDetalhesLook()); 
        }});
        painelBotoesLook.add(new JButton("Atualizar") {{ 
            addActionListener(e -> carregarDados()); 
        }});
        
        painelLooks.add(painelBotoesLook, BorderLayout.SOUTH);
        
        // Layout principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelCriacao, painelLooks);
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void criarLook() {
        String nome = txtNomeLook.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome do look é obrigatório!");
            return;
        }
        
        String id = UUID.randomUUID().toString();
        Look novoLook = new Look(id, nome);
        gestor.adicionarLook(novoLook);
        
        txtNomeLook.setText("");
        carregarLooks();
        JOptionPane.showMessageDialog(this, "Look criado com sucesso!");
    }
    
    private void adicionarItensAoLook() {
        if (lookSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um look primeiro!");
            return;
        }
        
        java.util.List<Item> itensSelecionados = listaItensDisponiveis.getSelectedValuesList();
        if (itensSelecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um item!");
            return;
        }
        
        for (Item item : itensSelecionados) {
            lookSelecionado.adicionarItem(item);
        }
        
        gestor.modificarLook(lookSelecionado);
        carregarLooks();
        JOptionPane.showMessageDialog(this, "Itens adicionados ao look!");
    }
    
    private void registrarUsoLook() {
        if (lookSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um look primeiro!");
            return;
        }
        
        JPanel panel = new JPanel(new GridLayout(3, 2));
        JTextField txtData = new JTextField(LocalDate.now().toString());
        JComboBox<String> cbPeriodo = new JComboBox<>(new String[]{"Manhã", "Tarde", "Noite"});
        JTextField txtOcasiao = new JTextField();
        
        panel.add(new JLabel("Data (YYYY-MM-DD):"));
        panel.add(txtData);
        panel.add(new JLabel("Período:"));
        panel.add(cbPeriodo);
        panel.add(new JLabel("Ocasião:"));
        panel.add(txtOcasiao);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Registrar Uso do Look", 
                                                  JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                LocalDate data = LocalDate.parse(txtData.getText());
                String periodo = (String) cbPeriodo.getSelectedItem();
                String ocasiao = txtOcasiao.getText();
                
                lookSelecionado.registrarUtilizacao(data, periodo, ocasiao);
                gestor.modificarLook(lookSelecionado);
                carregarLooks();
                JOptionPane.showMessageDialog(this, "Uso registrado com sucesso!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao registrar uso: " + e.getMessage());
            }
        }
    }
    
    private void selecionarLook() {
        int selectedRow = tabelaLooks.getSelectedRow();
        if (selectedRow >= 0) {
            String id = (String) modeloTabela.getValueAt(selectedRow, 0);
            lookSelecionado = gestor.buscarLook(id);
        }
    }
    
    private void removerLook() {
        int selectedRow = tabelaLooks.getSelectedRow();
        if (selectedRow >= 0) {
            String id = (String) modeloTabela.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja remover este look?", 
                "Confirmar Remoção", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                gestor.removerLook(id);
                carregarLooks();
                lookSelecionado = null;
                JOptionPane.showMessageDialog(this, "Look removido com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um look para remover!");
        }
    }
    
    private void verDetalhesLook() {
        if (lookSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um look primeiro!");
            return;
        }
        
        StringBuilder detalhes = new StringBuilder();
        detalhes.append("Look: ").append(lookSelecionado.getNome()).append("\n\n");
        detalhes.append("Itens:\n");
        
        for (Item item : lookSelecionado.getItens().values()) {
            detalhes.append("- ").append(item.getNome())
                   .append(" (").append(item.getCor()).append(")\n");
        }
        
        detalhes.append("\nUtilizações:\n");
        for (Look.UtilizacaoLook uso : lookSelecionado.getUtilizacoes()) {
            detalhes.append("- ").append(uso.toString()).append("\n");
        }
        
        JTextArea textArea = new JTextArea(detalhes.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Detalhes do Look", 
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void carregarDados() {
        carregarItensDisponiveis();
        carregarLooks();
    }
    
    private void carregarItensDisponiveis() {
        modeloListaItens.clear();
        for (Item item : gestor.listarItens()) {
            modeloListaItens.addElement(item);
        }
    }
    
    private void carregarLooks() {
        modeloTabela.setRowCount(0);
        for (Look look : gestor.listarLooks()) {
            Object[] row = {
                look.getId(),
                look.getNome(),
                look.getItens().size(),
                look.getQuantidadeUtilizacoes()
            };
            modeloTabela.addRow(row);
        }
    }
}
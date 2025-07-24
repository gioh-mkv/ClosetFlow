package gui;

import interfaces.IEmprestavel;
import model.Item;
import service.GestorVestuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class EmprestimoPanel extends JPanel {
    private GestorVestuario gestor;
    private JTable tabelaEmprestimos;
    private DefaultTableModel modeloTabela;
    private JComboBox<Item> cbItensEmprestáveis;
    private JTextField txtNomeEmprestado;
    
    public EmprestimoPanel(GestorVestuario gestor) {
        this.gestor = gestor;
        initializeComponents();
        setupLayout();
        carregarDados();
    }
    private void initializeComponents() {
        // Tabela de empréstimos
        String[] colunas = {"Item", "Emprestado para", "Data Empréstimo", "Dias Emprestado"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaEmprestimos = new JTable(modeloTabela);
        // ComboBox com itens emprestáveis
        cbItensEmprestáveis = new JComboBox<>();
        txtNomeEmprestado = new JTextField(20);
    }
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Painel superior - Novo empréstimo
        JPanel painelEmprestimo = new JPanel(new GridBagLayout());
        painelEmprestimo.setBorder(BorderFactory.createTitledBorder("Registrar Empréstimo"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        painelEmprestimo.add(new JLabel("Item:"), gbc);
        gbc.gridx = 1;
        painelEmprestimo.add(cbItensEmprestáveis, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        painelEmprestimo.add(new JLabel("Emprestado para:"), gbc);
        gbc.gridx = 1;
        painelEmprestimo.add(txtNomeEmprestado, gbc);
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(new JButton("Emprestar") {
        	{ 
            addActionListener(e -> registrarEmprestimo()); 
        	}
        });
        painelBotoes.add(new JButton("Devolver") {
        	{ 
            addActionListener(e -> registrarDevolucao()); 
        	}
        });
        painelBotoes.add(new JButton("Atualizar") {
        	{ 
            addActionListener(e -> carregarDados()); 
        	}
        });
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        painelEmprestimo.add(painelBotoes, gbc);
        
        // Layout principal
        add(painelEmprestimo, BorderLayout.NORTH);
        add(new JScrollPane(tabelaEmprestimos), BorderLayout.CENTER);
    }
    
    private void registrarEmprestimo() {
        Item itemSelecionado = (Item) cbItensEmprestáveis.getSelectedItem();
        String nomeEmprestado = txtNomeEmprestado.getText().trim();
        
        if (itemSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um item!");
            return;
        }
        
        if (nomeEmprestado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o nome da pessoa!");
            return;
        }
        
        if (!(itemSelecionado instanceof IEmprestavel)) {
            JOptionPane.showMessageDialog(this, "Este item não pode ser emprestado!");
            return;
        }
        
        IEmprestavel itemEmprestavel = (IEmprestavel) itemSelecionado;
        if (itemEmprestavel.estaEmprestado()) {
            JOptionPane.showMessageDialog(this, "Este item já está emprestado!");
            return;
        }
        
        gestor.emprestarItem(itemSelecionado.getId(), nomeEmprestado, LocalDate.now());
        txtNomeEmprestado.setText("");
        carregarDados();
        JOptionPane.showMessageDialog(this, "Empréstimo registrado com sucesso!");
    }
    
    private void registrarDevolucao() {
        int selectedRow = tabelaEmprestimos.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um empréstimo!");
            return;
        }
        
        // Encontra o item pela descricao na tabela
        String nomeItem = (String) modeloTabela.getValueAt(selectedRow, 0);
        Item item = gestor.listarItens().stream()
                .filter(i -> i.toString().equals(nomeItem) && i instanceof IEmprestavel)
                .findFirst()
                .orElse(null);
        
        if (item != null) {
            gestor.devolverItem(item.getId());
            carregarDados();
            JOptionPane.showMessageDialog(this, "Devolução registrada com sucesso!");
        }
    }
    
    private void carregarDados() {
        carregarItensEmprestáveis();
        carregarEmprestimos();
    }
    
    private void carregarItensEmprestáveis() {
        cbItensEmprestáveis.removeAllItems();
        for (Item item : gestor.listarItens()) {
            if (item instanceof IEmprestavel && !((IEmprestavel) item).estaEmprestado()) {
                cbItensEmprestáveis.addItem(item);
            }
        }
    }
    
    private void carregarEmprestimos() {
        modeloTabela.setRowCount(0);
        for (Item item : gestor.getItensEmprestados()) {
            if (item instanceof IEmprestavel) {
                IEmprestavel itemEmprestavel = (IEmprestavel) item;
                Object[] row = {
                    item.toString(),
                    itemEmprestavel.getNomeEmprestado(),
                    itemEmprestavel.getDataEmprestimo(),
                    itemEmprestavel.quantidadeDeDiasDesdeOEmprestimo() + " dias"
                };
                modeloTabela.addRow(row);
            }
        }
    }
}
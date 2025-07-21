package gui;

import interfaces.ILavavel;
import model.*;
import service.GestorVestuario;
import javax.swing.*;
import java.awt.*;

public class EstatisticaPanel extends JPanel {
    private GestorVestuario gestor;
    private JTextArea areaEstatisticas;
    
    public EstatisticaPanel(GestorVestuario gestor) {
        this.gestor = gestor;
        initializeComponents();
        setupLayout();
        atualizarEstatisticas();
    }
    
    private void initializeComponents() {
        areaEstatisticas = new JTextArea();
        areaEstatisticas.setEditable(false);
        areaEstatisticas.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.add(new JButton("Atualizar Estatísticas") {{ 
            addActionListener(e -> atualizarEstatisticas()); 
        }});
        
        add(painelBotoes, BorderLayout.NORTH);
        add(new JScrollPane(areaEstatisticas), BorderLayout.CENTER);
    }
    
    private void atualizarEstatisticas() {
        StringBuilder stats = new StringBuilder();
        
        stats.append("=== ESTATÍSTICAS DO VESTUÁRIO ===\n\n");
        
        // Estatísticas gerais
        stats.append("RESUMO GERAL:\n");
        stats.append("Total de itens: ").append(gestor.listarItens().size()).append("\n");
        stats.append("Total de looks: ").append(gestor.listarLooks().size()).append("\n");
        stats.append("Total de lavagens: ").append(gestor.listarLavagens().size()).append("\n");
        stats.append("Itens emprestados: ").append(gestor.getItensEmprestados().size()).append("\n\n");
        
        // Itens mais usados
        stats.append("ITENS MAIS USADOS:\n");
        java.util.List<Item> itensMaisUsados = gestor.getItensMaisUsados(5);
        for (int i = 0; i < itensMaisUsados.size(); i++) {
            Item item = itensMaisUsados.get(i);
            stats.append((i + 1)).append(". ").append(item.getNome())
                 .append(" - ").append(item.getQuantidadeUtilizacoes()).append(" usos\n");
        }
        stats.append("\n");
        
        // Itens menos usados
        stats.append("ITENS MENOS USADOS:\n");
        java.util.List<Item> itensMenosUsados = gestor.getItensMenosUsados(5);
        for (int i = 0; i < itensMenosUsados.size(); i++) {
            Item item = itensMenosUsados.get(i);
            stats.append((i + 1)).append(". ").append(item.getNome())
                 .append(" - ").append(item.getQuantidadeUtilizacoes()).append(" usos\n");
        }
        stats.append("\n");
        
        // Looks mais usados
        stats.append("LOOKS MAIS USADOS:\n");
        java.util.List<Look> looksMaisUsados = gestor.getLooksMaisUsados(5);
        for (int i = 0; i < looksMaisUsados.size(); i++) {
            Look look = looksMaisUsados.get(i);
            stats.append((i + 1)).append(". ").append(look.getNome())
                 .append(" - ").append(look.getQuantidadeUtilizacoes()).append(" usos\n");
        }
        stats.append("\n");
        
        // Itens mais lavados
        stats.append("ITENS MAIS LAVADOS:\n");
        java.util.List<Item> itensMaisLavados = gestor.getItensMaisLavados(5);
        for (int i = 0; i < itensMaisLavados.size(); i++) {
            Item item = itensMaisLavados.get(i);
            if (item instanceof ILavavel) {
                stats.append((i + 1)).append(". ").append(item.getNome())
                     .append(" - ").append(((ILavavel) item).getQuantidadeLavagens()).append(" lavagens\n");
            }
        }
        stats.append("\n");
        
        // Itens emprestados
        stats.append("ITENS EMPRESTADOS:\n");
        java.util.List<Item> itensEmprestados = gestor.getItensEmprestados();
        if (itensEmprestados.isEmpty()) {
            stats.append("Nenhum item emprestado no momento.\n");
        } else {
            for (Item item : itensEmprestados) {
                if (item instanceof interfaces.IEmprestavel) {
                    interfaces.IEmprestavel itemEmp = (interfaces.IEmprestavel) item;
                    stats.append("- ").append(item.getNome())
                         .append(" para ").append(itemEmp.getNomeEmprestado())
                         .append(" há ").append(itemEmp.quantidadeDeDiasDesdeOEmprestimo()).append(" dias\n");
                }
            }
        }
        
        areaEstatisticas.setText(stats.toString());
    }
}
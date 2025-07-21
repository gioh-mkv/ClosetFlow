package service;

import interfaces.IEmprestavel;
import interfaces.ILavavel;
import model.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class GestorVestuario {
    private Map<String, Item> itens;
    private Map<String, Look> looks;
    private Map<String, Lavagem> lavagens;
    private PersistenciaService persistencia;
    
    public GestorVestuario() {
        this.itens = new HashMap<>();
        this.looks = new HashMap<>();
        this.lavagens = new HashMap<>();
        this.persistencia = new PersistenciaService();
        carregarDados();
    }
    
    //Itens
    public void adicionarItem(Item item) {
        itens.put(item.getId(), item);
        salvarDados();
    }
    
    public void removerItem(String id) {
        itens.remove(id);
        salvarDados();
    }
    
    public void modificarItem(Item item) {
        itens.put(item.getId(), item);
        salvarDados();
    }
    
    public Item buscarItem(String id) {
        return itens.get(id);
    }
    
    public List<Item> listarItens() {
        return new ArrayList<>(itens.values());
    }
    
    //Looks
    public void adicionarLook(Look look) {
        looks.put(look.getId(), look);
        salvarDados();
    }
    
    public void removerLook(String id) {
        looks.remove(id);
        salvarDados();
    }
    
    public void modificarLook(Look look) {
        looks.put(look.getId(), look);
        salvarDados();
    }
    
    public Look buscarLook(String id) {
        return looks.get(id);
    }
    
    public List<Look> listarLooks() {
        return new ArrayList<>(looks.values());
    }
    
    // Empréstimos
    public void emprestarItem(String itemId, String nomeEmprestado, LocalDate data) {
        Item item = itens.get(itemId);
        if (item instanceof IEmprestavel) {
            ((IEmprestavel) item).registrarEmprestimo(nomeEmprestado, data);
            salvarDados();
        }
    }
    
    public void devolverItem(String itemId) {
        Item item = itens.get(itemId);
        if (item instanceof IEmprestavel) {
            ((IEmprestavel) item).registrarDevolucao();
            salvarDados();
        }
    }
    
    // Lavagens
    public void adicionarLavagem(Lavagem lavagem) {
        lavagens.put(lavagem.getId(), lavagem);
        salvarDados();
    }
    
    public List<Lavagem> listarLavagens() {
        return new ArrayList<>(lavagens.values());
    }
    
    // Estatísticas
    public List<Item> getItensMaisUsados(int limite) {
        return itens.values().stream()
                .sorted((a, b) -> Integer.compare(b.getQuantidadeUtilizacoes(), a.getQuantidadeUtilizacoes()))
                .limit(limite)
                .collect(Collectors.toList());
    }
    
    public List<Item> getItensMenosUsados(int limite) {
        return itens.values().stream()
                .sorted((a, b) -> Integer.compare(a.getQuantidadeUtilizacoes(), b.getQuantidadeUtilizacoes()))
                .limit(limite)
                .collect(Collectors.toList());
    }
    
    public List<Item> getItensEmprestados() {
        return itens.values().stream()
                .filter(item -> item instanceof IEmprestavel && ((IEmprestavel) item).estaEmprestado())
                .collect(Collectors.toList());
    }
    
    public List<Look> getLooksMaisUsados(int limite) {
        return looks.values().stream()
                .sorted((a, b) -> Integer.compare(b.getQuantidadeUtilizacoes(), a.getQuantidadeUtilizacoes()))
                .limit(limite)
                .collect(Collectors.toList());
    }
    
    public List<Item> getItensMaisLavados(int limite) {
        return itens.values().stream()
                .filter(item -> item instanceof ILavavel)
                .sorted((a, b) -> Integer.compare(((ILavavel) b).getQuantidadeLavagens(), 
                                                ((ILavavel) a).getQuantidadeLavagens()))
                .limit(limite)
                .collect(Collectors.toList());
    }
    
    // Persistência
    private void salvarDados() {
        persistencia.salvarItens(new ArrayList<>(itens.values()));
        persistencia.salvarLooks(new ArrayList<>(looks.values()));
    }
    
    private void carregarDados() {
        List<Item> itensCarregados = persistencia.carregarItens();
        for (Item item : itensCarregados) {
            itens.put(item.getId(), item);
        }
        
        List<Look> looksCarregados = persistencia.carregarLooks();
        for (Look look : looksCarregados) {
            looks.put(look.getId(), look);
        }
    }
}
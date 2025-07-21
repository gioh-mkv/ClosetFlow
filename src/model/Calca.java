package model;


import interfaces.IEmprestavel;
import interfaces.ILavavel;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Calca extends Item implements IEmprestavel, ILavavel {
    private boolean emprestado;
    private String nomeEmprestado;
    private LocalDate dataEmprestimo;
    private List<LocalDate> lavagens;
    
    public Calca(String id, String nome, String cor, String tamanho, 
                 String lojaOrigem, Conservacao conservacao) {
        super(id, nome, cor, tamanho, lojaOrigem, conservacao, TipoItem.PARTE_INFERIOR);
        this.emprestado = false;
        this.lavagens = new ArrayList<>();
    }
    
    @Override
    public void registrarEmprestimo(String nomeEmprestado, LocalDate dataEmprestimo) {
        this.emprestado = true;
        this.nomeEmprestado = nomeEmprestado;
        this.dataEmprestimo = dataEmprestimo;
    }
    
    @Override
    public long quantidadeDeDiasDesdeOEmprestimo() {
        if (!emprestado || dataEmprestimo == null) return 0;
        return ChronoUnit.DAYS.between(dataEmprestimo, LocalDate.now());
    }
    
    @Override
    public void registrarDevolucao() {
        this.emprestado = false;
        this.nomeEmprestado = null;
        this.dataEmprestimo = null;
    }
    
    @Override
    public boolean estaEmprestado() {
        return emprestado;
    }
    
    @Override
    public String getNomeEmprestado() {
        return nomeEmprestado;
    }
    
    @Override
    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }
    
    @Override
    public void registrarLavagem(LocalDate dataLavagem) {
        lavagens.add(dataLavagem);
    }
    
    @Override
    public int getQuantidadeLavagens() {
        return lavagens.size();
    }
    
    @Override
    public LocalDate getUltimaLavagem() {
        return lavagens.isEmpty() ? null : lavagens.get(lavagens.size() - 1);
    }
}
package model;


import interfaces.IEmprestavel;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Relogio extends Item implements IEmprestavel {
    private boolean emprestado;
    private String nomeEmprestado;
    private LocalDate dataEmprestimo;
    
    public Relogio(String id, String nome, String cor, String tamanho, 
                   String lojaOrigem, Conservacao conservacao) {
        super(id, nome, cor, tamanho, lojaOrigem, conservacao, TipoItem.ACESSORIO);
        this.emprestado = false;
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
}
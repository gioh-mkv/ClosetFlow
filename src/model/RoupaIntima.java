package model;


import interfaces.ILavavel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RoupaIntima extends Item implements ILavavel {
    private List<LocalDate> lavagens;
    
    public RoupaIntima(String id, String nome, String cor, String tamanho, 
                       String lojaOrigem, Conservacao conservacao) {
        super(id, nome, cor, tamanho, lojaOrigem, conservacao, TipoItem.ROUPA_INTIMA);
        this.lavagens = new ArrayList<>();
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
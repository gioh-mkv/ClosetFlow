package service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import model.Item;
import model.Look;

public class PersistenciaService {
    private static final String ARQUIVO_ITENS = "itens.json";
    private static final String ARQUIVO_LOOKS = "looks.json";
    private Gson gson;
    
    public PersistenciaService() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
    }
    
    public void salvarItens(List<Item> itens) {
        try (FileWriter writer = new FileWriter(ARQUIVO_ITENS)) {
            gson.toJson(itens, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar itens: " + e.getMessage());
        }
    }
    
    public List<Item> carregarItens() {
        try (FileReader reader = new FileReader(ARQUIVO_ITENS)) {
            Type listType = new TypeToken<List<Item>>(){}.getType();
            List<Item> itens = gson.fromJson(reader, listType);
            return itens != null ? itens : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Erro ao carregar itens: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void salvarLooks(List<Look> looks) {
        try (FileWriter writer = new FileWriter(ARQUIVO_LOOKS)) {
            gson.toJson(looks, writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar looks: " + e.getMessage());
        }
    }
    
    public List<Look> carregarLooks() {
        try (FileReader reader = new FileReader(ARQUIVO_LOOKS)) {
            Type listType = new TypeToken<List<Look>>(){}.getType();
            List<Look> looks = gson.fromJson(reader, listType);
            return looks != null ? looks : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Erro ao carregar looks: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Adapter para LocalDate
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }
        
        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Optional;
import javax.swing.JOptionPane;



/**
 * Classe genérica para operações DAO (Data Access Object)
 * @author Heisenberg (modificado)
 * @param <T> Tipo do objeto a ser manipulado
 */
public class GenericDao<T> {
    
    private final Class<T> entityClass;
    
    /**
     * Construtor que recebe a classe da entidade
     * @param entityClass classe da entidade a ser manipulada
     */
    public GenericDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    /**
     * Insere um novo objeto no arquivo
     * @param object objeto a ser inserido
     */
    public Boolean insert(T object) {
        ArrayList<T> objects = openFile();
        boolean add = objects.add(object);
        closeFile(objects);
        return add;
    }
    
    /**
     * Atualiza um objeto existente no arquivo
     * @param object objeto a ser atualizado
     * @param id identificador usado para encontrar o objeto (normalmente email, id, etc)
     * @param idExtractor função lambda para extrair o identificador do objeto
     */
    public void update(T object, String id, IdExtractor<T> idExtractor) {
        ArrayList<T> objects = openFile();
        for (int i = 0; i < objects.size(); i++) {
            if (idExtractor.extractId(objects.get(i)).equalsIgnoreCase(id)) {
                objects.remove(i);
                objects.add(object);
                break;
            }
        }
        closeFile(objects);
    }
    
    /**
     * Remove um objeto do arquivo
     * @param id identificador do objeto a ser removido
     * @param idExtractor função lambda para extrair o identificador do objeto
     * @return true se o objeto foi removido, false caso contrário
     */
    public boolean delete(String id, IdExtractor<T> idExtractor) {
        ArrayList<T> objects = openFile();
        boolean removed = false;
        
        for (int i = 0; i < objects.size(); i++) {
            if (idExtractor.extractId(objects.get(i)).equalsIgnoreCase(id)) {
                objects.remove(i);
                removed = true;
                break;
            }
        }
        
        if (removed) {
            closeFile(objects);
        }
        
        return removed;
    }
    
    /**
     * Busca um objeto pelo id
     * @param id identificador do objeto
     * @param idExtractor função lambda para extrair o identificador do objeto
     * @return Optional contendo o objeto se encontrado, ou vazio se não
     */
    public Optional<T> getById(String id, IdExtractor<T> idExtractor) {
        ArrayList<T> objects = openFile();
        
        for (T object : objects) {
            if (idExtractor.extractId(object).equalsIgnoreCase(id)) {
                return Optional.of(object);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Obtém todos os objetos do arquivo
     * @return lista com todos os objetos
     */
    public ArrayList<T> getAll() {
        return openFile();
    }
    
    /**
     * Salva a lista de objetos no arquivo
     * @param objects lista de objetos a ser salva
     */
    public void closeFile(ArrayList<T> objects) {
        try {
            // Usa o nome da classe como nome do arquivo
            String fileName = entityClass.getSimpleName();
            
            // Cria o arquivo para armazenar os objetos
            FileOutputStream fileOutput = new FileOutputStream(fileName);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
            
            // Gravando os objetos no arquivo
            for (T object : objects) {
                objectOutput.writeObject(object);
            }
            
            // Fechando os arquivos abertos
            fileOutput.flush();
            fileOutput.close();
            objectOutput.flush();
            objectOutput.close();
            
            JOptionPane.showMessageDialog(null, "Sucesso na operação");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo: " + e.getMessage());
        }
    }
    
    /**
     * Lê todos os objetos do arquivo
     * @return lista com todos os objetos lidos
     */
    public ArrayList<T> openFile() {
        ArrayList<T> objects = new ArrayList<>();
        
        try {
            // Usa o nome da classe como nome do arquivo
            String fileName = entityClass.getSimpleName();
            
            FileInputStream fileInput = new FileInputStream(fileName);
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            
            T object;
            while ((object = (T) objectInput.readObject()) != null) {
                objects.add(object);
            }
            
            fileInput.close();
            objectInput.close();
            
        } catch (Exception e) {
            // Arquivo pode não existir ainda ou chegou ao fim da leitura
            // Não exibe mensagem de erro pois isso é normal na primeira execução
        }
        
        return objects;
    }
    
    /**
     * Interface funcional para extrair o identificador de um objeto
     * @param <T> tipo do objeto
     */
    @FunctionalInterface
    public interface IdExtractor<T> {
        String extractId(T object);
    }
}
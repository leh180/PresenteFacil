package model;

import bib.Arquivo;
import bib.HashExtensivel;
import bib.ArvoreBMais;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A classe CRUDLista estende a classe genérica Arquivo e gere todas as 
 * operações de persistência para a entidade Lista.
 * Ela mantém um índice secundário por código (Hash Extensível) para buscas públicas
 * e um índice de relacionamento (Árvore B+) para ligar utilizadores às suas listas.
 */
public class CRUDLista extends Arquivo<Lista> {

    // ------------------------------------------ Atributos da Classe ------------------------------------------

    private HashExtensivel<ParCodigoId> indiceCodigo;
    private ArvoreBMais<ParUsuarioLista> indiceUsuarioLista;

    // ------------------------------------------ Construtor ------------------------------------------

    public CRUDLista() throws Exception {
        super("listas", Lista.class.getConstructor());
        
        File d = new File("data");
        if (!d.exists()) d.mkdir();

        indiceCodigo = new HashExtensivel<>(
            ParCodigoId.class.getConstructor(),
            4,
            "data/listas_codigo.diretorio.idx",
            "data/listas_codigo.cestos.idx"
        );
        
        indiceUsuarioLista = new ArvoreBMais<>(
            ParUsuarioLista.class.getConstructor(), 
            5,
            "data/listas_usuario.idx"
        );
    }

    // ------------------------------------------ Métodos Privados ------------------------------------------

    /**
     * Gera um código alfanumérico aleatório de 10 caracteres para partilha (simula o NanoID).
     * @return Uma string com o código gerado.
     */
    private String gerarCodigo() {
        String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder codigo = new StringBuilder(10);
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            codigo.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return codigo.toString();
    }

    // ------------------------------------------ Métodos Públicos (CRUD) ------------------------------------------

    /**
     * Cria uma nova lista, guardando-a no ficheiro principal e atualizando os índices secundários.
     * @param lista O objeto Lista a ser criado.
     * @return O ID gerado para a nova lista.
     * @throws Exception se ocorrer um erro durante a escrita nos ficheiros.
     */
    @Override
    public int create(Lista lista) throws Exception {
        lista.setCodigoCompartilhavel(gerarCodigo());
        
        int id = super.create(lista);
        lista.setID(id);

        indiceCodigo.create(new ParCodigoId(lista.getCodigoCompartilhavel(), id));
        indiceUsuarioLista.create(new ParUsuarioLista(lista.getIdUsuario(), id));
        
        return id;
    }

    /**
     * Procura uma lista pelo seu código compartilhável utilizando o índice de hash.
     * @param codigo O código a ser procurado.
     * @return O objeto Lista se encontrado, caso contrário, null.
     * @throws Exception se ocorrer um erro durante a leitura dos ficheiros.
     */
    public Lista readByCodigo(String codigo) throws Exception {
        ParCodigoId par = indiceCodigo.read(codigo.hashCode());
        
        if (par != null) {
            return super.read(par.getId());
        }
        return null;
    }

    /**
     * Encontra todas as listas pertencentes a um utilizador específico.
     * Utiliza a Árvore B+ para encontrar os IDs e depois lê cada lista do ficheiro.
     * @param idUsuario O ID do utilizador cujas listas devem ser encontradas.
     * @return Uma lista de objetos Lista.
     * @throws Exception se ocorrer um erro durante a leitura dos ficheiros.
     */
    public List<Lista> readAllByUser(int idUsuario) throws Exception {
        List<Lista> listasDoUsuario = new ArrayList<>();
        
        ParUsuarioLista busca = new ParUsuarioLista(idUsuario, -1);
        
        ArrayList<ParUsuarioLista> pares = indiceUsuarioLista.read(busca);
        
        for (ParUsuarioLista par : pares) {
           Lista lista = super.read(par.getIdLista());
           if (lista != null) {
               listasDoUsuario.add(lista);
           }
        }
        
        return listasDoUsuario;
    }
    
    /**
     * Apaga uma lista do sistema, incluindo as suas entradas nos índices secundários.
     * @param id O ID da lista a ser apagada.
     * @return true se a operação for bem-sucedida, false caso contrário.
     * @throws Exception se ocorrer um erro durante a escrita nos ficheiros.
     */
    @Override
    public boolean delete(int id) throws Exception {
        Lista lista = super.read(id);
        if (lista == null) {
            return false;
        }

        if (super.delete(id)) {
            indiceCodigo.delete(lista.getCodigoCompartilhavel().hashCode());
            indiceUsuarioLista.delete(new ParUsuarioLista(lista.getIdUsuario(), lista.getID()));
            return true;
        }
        
        return false;
    }
}


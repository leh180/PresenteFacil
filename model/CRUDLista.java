package model;

import bib.Arquivo;
import bib.HashExtensivel;
import bib.ArvoreBMais;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gerencia todas as operações de persistência (CRUD) para a entidade Lista.
 * Estende a classe genérica {@link Arquivo} e mantém índices secundários
 * para buscas otimizadas por código (Hash Extensível) e por ID de usuário
 * (Árvore B+).
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.0
 */
public class CRUDLista extends Arquivo<Lista> {

    // ------------------------------------------ Atributos da Classe ------------------------------------------

    private HashExtensivel<ParCodigoId> indiceCodigo;
    private ArvoreBMais<ParUsuarioLista> indiceUsuarioLista;

    // ------------------------------------------ Construtor ------------------------------------------

    /**
     * Construtor da classe CRUDLista.
     * Inicializa o arquivo principal de dados ("listas") e os arquivos de
     * índices secundários (hash para códigos e Árvore B+ para usuários).
     *
     * @throws Exception se ocorrer um erro na abertura ou criação dos arquivos
     * de dados ou índices.
     */
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
     * Gera um código alfanumérico aleatório de 10 caracteres.
     * Usado para criar códigos compartilháveis únicos para as listas.
     *
     * @return Uma {@code String} aleatória de 10 caracteres.
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
     * Cria um novo registro de lista no arquivo principal e atualiza os índices.
     * Gera automaticamente um código compartilhável antes de salvar.
     *
     * @param lista O objeto {@link Lista} a ser persistido (sem ID e código).
     * @return O ID (inteiro) gerado para a nova lista.
     * @throws Exception se ocorrer um erro durante a escrita no arquivo principal
     * ou nos índices.
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
     * Busca uma lista específica usando seu código compartilhável.
     * A busca é otimizada pelo uso do índice de Hash Extensível.
     *
     * @param codigo O código compartilhável (String) da lista.
     * @return O objeto {@link Lista} correspondente, ou {@code null} se não for
     * encontrado.
     * @throws Exception se ocorrer um erro durante a leitura do índice ou do
     * arquivo principal.
     */
    public Lista readByCodigo(String codigo) throws Exception {
        ParCodigoId par = indiceCodigo.read(codigo.hashCode());
        
        if (par != null) {
            return super.read(par.getId());
        }
        return null;
    }

    /**
     * Retorna todas as listas associadas a um ID de usuário específico.
     * A busca é otimizada pelo uso do índice de Árvore B+.
     *
     * @param idUsuario O ID (inteiro) do usuário.
     * @return Uma {@code List<Lista>} contendo todas as listas encontradas
     * para o usuário (pode estar vazia).
     * @throws Exception se ocorrer um erro durante a leitura do índice ou do
     * arquivo principal.
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
     * Exclui (logicamente) uma lista do arquivo principal e remove suas
     * respectivas entradas dos índices secundários (código e usuário).
     *
     * @param id O ID (inteiro) da lista a ser excluída.
     * @return {@code true} se a exclusão for bem-sucedida em todos os
     * arquivos, {@code false} caso contrário (ex: lista não encontrada).
     * @throws Exception se ocorrer um erro durante a atualização dos arquivos
     * ou índices.
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
package model;

import bib.Arquivo;
import bib.ArvoreBMais;
import java.io.File;
import java.util.ArrayList;

/**
 * Gerencia as operações de persistência (CRUD) para a entidade associativa
 * {@link ListaProduto}. Esta classe implementa o relacionamento N:N entre
 * {@link Lista} e {@link Produto}.
 * Utiliza duas Árvores B+ como índices secundários para permitir buscas
 * eficientes tanto pelo ID da Lista quanto pelo ID do Produto.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class CRUDListaProduto extends Arquivo<ListaProduto> {

    // --- Atributos da Classe ---
    private ArvoreBMais<ParIdListaProduto> indiceIdLista; // Busca por Lista
    private ArvoreBMais<ParIdProdutoListaProduto> indiceIdProduto; // Busca por Produto

    // --- Construtor ---
    
    /**
     * Construtor da classe CRUDListaProduto.
     * Inicializa o arquivo principal de dados ("listaproduto") e os arquivos
     * de índices B+ (um indexado por idLista, outro por idProduto).
     *
     * @throws Exception se ocorrer um erro na abertura ou criação dos arquivos
     * de dados ou índices.
     */
    public CRUDListaProduto() throws Exception {
        super("listaproduto", ListaProduto.class.getConstructor());

        File d = new File("data");
        if (!d.exists())
            d.mkdir();

        indiceIdLista = new ArvoreBMais<>(
                ParIdListaProduto.class.getConstructor(),
                5, 
                "data/listaproduto_idlista.btree.db");

        indiceIdProduto = new ArvoreBMais<>(
                ParIdProdutoListaProduto.class.getConstructor(),
                5, 
                "data/listaproduto_idproduto.btree.db");
    }

    // --- Métodos CRUD ---

    /**
     * Cria um novo registro de associação {@link ListaProduto} no arquivo
     * principal e atualiza ambos os índices B+.
     *
     * @param lp O objeto {@link ListaProduto} a ser persistido (sem ID).
     * @return O ID (inteiro) gerado para a nova associação.
     * @throws Exception se ocorrer um erro durante a escrita no arquivo principal
     * ou nos índices.
     */
    @Override
    public int create(ListaProduto lp) throws Exception {
        int id = super.create(lp);
        lp.setID(id);

        indiceIdLista.create(new ParIdListaProduto(lp.getIdLista(), id));
        indiceIdProduto.create(new ParIdProdutoListaProduto(lp.getIdProduto(), id));

        return id;
    }

    /**
     * Busca todas as associações ({@link ListaProduto}) pertencentes a uma
     * lista específica, utilizando o índice B+ de ID de Lista.
     *
     * @param idLista O ID (inteiro) da lista a ser consultada.
     * @return Um {@code ArrayList<ListaProduto>} contendo todas as associações
     * encontradas para a lista (pode estar vazio).
     * @throws Exception se ocorrer um erro durante a leitura do índice ou do
     * arquivo principal.
     */
    public ArrayList<ListaProduto> readAllByLista(int idLista) throws Exception {
        ArrayList<ListaProduto> resultado = new ArrayList<>();
        ArrayList<ParIdListaProduto> pares = indiceIdLista.read(new ParIdListaProduto(idLista));

        for (ParIdListaProduto par : pares) {
            ListaProduto lp = super.read(par.getIdListaProduto());
            if (lp != null) {
                resultado.add(lp);
            }
        }
        return resultado;
    }

    /**
     * Busca todas as associações ({@link ListaProduto}) relacionadas a um
     * produto específico, utilizando o índice B+ de ID de Produto.
     *
     * @param idProduto O ID (inteiro) do produto a ser consultado.
     * @return Um {@code ArrayList<ListaProduto>} contendo todas as associações
     * encontradas para o produto (pode estar vazio).
     * @throws Exception se ocorrer um erro durante a leitura do índice ou do
     * arquivo principal.
     */
    public ArrayList<ListaProduto> readAllByProduto(int idProduto) throws Exception {
        ArrayList<ListaProduto> resultado = new ArrayList<>();
        ArrayList<ParIdProdutoListaProduto> pares = indiceIdProduto.read(new ParIdProdutoListaProduto(idProduto));

        for (ParIdProdutoListaProduto par : pares) {
            ListaProduto lp = super.read(par.getIdListaProduto());
            if (lp != null) {
                resultado.add(lp);
            }
        }
        return resultado;
    }

    /**
     * Atualiza um registro de associação {@link ListaProduto} no arquivo
     * principal.
     * A classe {@link Arquivo} base já trata a atualização de índices caso as
     * chaves estrangeiras (idLista, idProduto) sejam alteradas.
     *
     * @param lp O objeto {@link ListaProduto} contendo os dados atualizados.
     * @return {@code true} se a atualização for bem-sucedida, {@code false}
     * caso contrário.
     * @throws Exception se ocorrer um erro durante a escrita no arquivo.
     */
    @Override
    public boolean update(ListaProduto lp) throws Exception {
        // NOTA: A lógica de atualização da superclasse Arquivo.java
        // deve lidar com a remoção e recriação nos índices B+
        // se os IDs de chave estrangeira (idLista, idProduto) mudarem.
        return super.update(lp);
    }

    /**
     * Exclui (logicamente) uma associação {@link ListaProduto} do arquivo
     * principal e remove suas entradas de ambos os índices B+.
     *
     * @param id O ID (inteiro) da associação a ser excluída.
     * @return {@code true} se a exclusão for bem-sucedida, {@code false}
     * caso contrário (ex: associação não encontrada).
     * @throws Exception se ocorrer um erro durante a atualização dos arquivos
     * ou índices.
     */
    @Override
    public boolean delete(int id) throws Exception {
        ListaProduto lp = super.read(id);
        if (lp == null) {
            return false;
        }

        indiceIdLista.delete(new ParIdListaProduto(lp.getIdLista(), id));
        indiceIdProduto.delete(new ParIdProdutoListaProduto(lp.getIdProduto(), id));

        return super.delete(id);
    }

    /**
     * Exclui todas as associações {@link ListaProduto} pertencentes a uma
     * lista específica.
     * Este método é usado, por exemplo, ao excluir uma {@link Lista}.
     *
     * @param idLista O ID (inteiro) da lista cujas associações devem ser
     * removidas.
     * @return O número (inteiro) de associações que foram excluídas com sucesso.
     * @throws Exception se ocorrer um erro durante as operações de exclusão.
     */
    public int deleteByLista(int idLista) throws Exception {
        ArrayList<ListaProduto> associacoes = readAllByLista(idLista);
        int count = 0;

        for (ListaProduto lp : associacoes) {
            if (delete(lp.getID())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Verifica se já existe uma associação entre um ID de lista e um ID de
     * produto.
     *
     * @param idLista O ID (inteiro) da lista.
     * @param idProduto O ID (inteiro) do produto.
     * @return O objeto {@link ListaProduto} se a associação for encontrada,
     * {@code null} caso contrário.
     * @throws Exception se ocorrer um erro durante a leitura dos dados.
     */
    public ListaProduto findAssociacao(int idLista, int idProduto) throws Exception {
        ArrayList<ListaProduto> produtos = readAllByLista(idLista);

        for (ListaProduto lp : produtos) {
            if (lp.getIdProduto() == idProduto) {
                return lp;
            }
        }
        return null;
    }

    /**
     * Fecha as conexões com os arquivos de dados gerenciados por este
     * controlador.
     *
     * @throws Exception se ocorrer um erro ao fechar o recurso.
     */
    @Override
    public void close() throws Exception {
        super.close();
    }
}
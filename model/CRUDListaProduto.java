package model;

import bib.Arquivo;
import bib.ArvoreBMais;
import java.io.File;
import java.util.ArrayList;

/**
 * CRUD para a entidade de associação ListaProduto, que gere o relacionamento
 * N:N
 * entre Listas e Produtos.
 * Utiliza duas Árvores B+ como índices secundários para permitir buscas
 * eficientes
 * a partir do ID da Lista ou do ID do Produto.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class CRUDListaProduto extends Arquivo<ListaProduto> {

    // --- Atributos ---
    private ArvoreBMais<ParIdListaProduto> indiceIdLista;
    private ArvoreBMais<ParIdProdutoListaProduto> indiceIdProduto;

    // --- Construtor ---
    /**
     * Construtor da classe. Inicializa o ficheiro de dados principal
     * ("listaproduto.db")
     * e as duas Árvores B+ que servem como índices para o relacionamento N:N.
     * 
     * @throws Exception se ocorrer um erro na criação ou abertura dos ficheiros.
     */
    public CRUDListaProduto() throws Exception {
        super("listaproduto", ListaProduto.class.getConstructor());

        File d = new File("data");
        if (!d.exists()){
            d.mkdir();
        }
        indiceIdLista = new ArvoreBMais<>(
                ParIdListaProduto.class.getConstructor(),
                5,
                "data/listaproduto_idlista.btree.db"
        );

        indiceIdProduto = new ArvoreBMais<>(
                ParIdProdutoListaProduto.class.getConstructor(),
                5,
                "data/listaproduto_idproduto.btree.db"
        );
    }

    // --- Métodos CRUD ---

    /**
     * Cria uma nova associação entre Lista e Produto.
     * Insere o registo no ficheiro principal e atualiza ambas as Árvores B+.
     * 
     * @param lp O objeto ListaProduto a ser criado.
     * @return O ID gerado para a nova associação.
     * @throws Exception se ocorrer um erro durante a criação.
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
     * Procura todas as associações (produtos) para uma determinada lista.
     * Utiliza a Árvore B+ indexada por `idLista`.
     * 
     * @param idLista O ID da lista.
     * @return Um ArrayList com todas as associações {@link ListaProduto}
     *         encontradas.
     * @throws Exception se ocorrer um erro durante a busca.
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
     * Procura todas as associações (listas) para um determinado produto.
     * Utiliza a Árvore B+ indexada por `idProduto`.
     * 
     * @param idProduto O ID do produto.
     * @return Um ArrayList com todas as associações {@link ListaProduto}
     *         encontradas.
     * @throws Exception se ocorrer um erro durante a busca.
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
     * Atualiza uma associação existente.
     * Se os IDs de Lista ou Produto forem alterados (o que não deve acontecer
     * na lógica normal do TP2), reindexa o registo nas Árvores B+.
     * Caso contrário, apenas atualiza o registo no ficheiro principal.
     * 
     * @param lp O objeto {@link ListaProduto} com os dados atualizados.
     * @return {@code true} se a atualização for bem-sucedida, {@code false} caso
     *         contrário.
     * @throws Exception se ocorrer um erro durante a atualização.
     */
    @Override
    public boolean update(ListaProduto lp) throws Exception {
        ListaProduto lpAntigo = super.read(lp.getID());
        if (lpAntigo == null) {
            return false;
        }

        boolean reindexar = lpAntigo.getIdLista() != lp.getIdLista() || lpAntigo.getIdProduto() != lp.getIdProduto();

        if (reindexar) {
            indiceIdLista.delete(new ParIdListaProduto(lpAntigo.getIdLista(), lp.getID()));
            indiceIdProduto.delete(new ParIdProdutoListaProduto(lpAntigo.getIdProduto(), lp.getID()));
        }

        boolean success = super.update(lp);

        if (success && reindexar) {
            indiceIdLista.create(new ParIdListaProduto(lp.getIdLista(), lp.getID()));
            indiceIdProduto.create(new ParIdProdutoListaProduto(lp.getIdProduto(), lp.getID()));
        }

        return success;
    }

    /**
     * Remove uma associação entre Lista e Produto.
     * Remove o registo do ficheiro principal e de ambas as Árvores B+.
     * 
     * @param id O ID da associação a ser removida.
     * @return {@code true} se a remoção for bem-sucedida, {@code false} caso
     *         contrário.
     * @throws Exception se ocorrer um erro durante a remoção.
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
     * Apaga todas as associações (ListaProduto) pertencentes a uma lista
     * específica.
     * Método essencial para a "exclusão em cascata" ao apagar uma Lista.
     * 
     * @param idLista O ID da lista cujas associações devem ser apagadas.
     * @return O número de associações que foram apagadas.
     * @throws Exception se ocorrer um erro durante a exclusão.
     */
    public int deleteByLista(int idLista) throws Exception {
        ArrayList<ListaProduto> associacoesParaApagar = readAllByLista(idLista);
        int count = 0;

        for (ListaProduto lp : associacoesParaApagar) {
            if (delete(lp.getID())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Testa se já existe uma associação entre uma lista e um produto específicos.
     * Útil para evitar adicionar o mesmo produto duas vezes a uma lista.
     * 
     * @param idLista   O ID da lista.
     * @param idProduto O ID do produto.
     * @return O objeto {@link ListaProduto} se a associação existir, caso contrário
     *         {@code null}.
     * @throws Exception se ocorrer um erro durante a busca.
     */
    public ListaProduto findAssociacao(int idLista, int idProduto) throws Exception {
        ArrayList<ListaProduto> produtosDaLista = readAllByLista(idLista);
        for (ListaProduto lp : produtosDaLista) {
            if (lp.getIdProduto() == idProduto) {
                return lp;
            }
        }
        return null;
    }

    /**
     * Fecha a ligação com o ficheiro de dados principal ("listaproduto.db").
     * 
     * @throws Exception se ocorrer um erro ao fechar o ficheiro.
     */
    @Override
    public void close() throws Exception {
        super.close();
    }
}
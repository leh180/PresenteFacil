package model;

import bib.Arquivo;
import bib.ArvoreBMais;
import java.io.File;
import java.util.ArrayList;

// CRUD para a associação ListaProduto (relacionamento N:N)
public class CRUDListaProduto extends Arquivo<ListaProduto> {

    // Atributos da Classe (2 Árvores B+)
    private ArvoreBMais<ParIdListaProduto> indiceIdLista; // Busca por Lista
    private ArvoreBMais<ParIdProdutoListaProduto> indiceIdProduto; // Busca por Produto

    // Construtores (cria arquivo de data com as listas e produtos caso não existam, e
    // cria também um arquivo com as árvores)
    public CRUDListaProduto() throws Exception {
        super("listaproduto", ListaProduto.class.getConstructor());

        File d = new File("data");
        if (!d.exists())
            d.mkdir();

        // Inicializa a arvore B+ indexada por IdLista
        indiceIdLista = new ArvoreBMais<>(
                ParIdListaProduto.class.getConstructor(),
                5, // ordem da árvore 5
                "data/listaproduto_idlista.btree.db");

        // Inicializa a árvore B+ indexada por IdProduto
        indiceIdProduto = new ArvoreBMais<>(
                ParIdProdutoListaProduto.class.getConstructor(),
                5, // ordem da árvore
                "data/listaproduto_idproduto.btree.db");
    }

    // Funções do CRUD

    /**
     * Cria uma nova associação entre Lista e Produto
     * Insere o registro no arquivo e atualiza ambas as árvores B+
     * 
     * @param lp O objeto ListaProduto a ser criado
     * @return O ID gerado para a associação
     * @throws Exception se ocorrer erro durante a criação
     */
    @Override
    public int create(ListaProduto lp) throws Exception {
        // Cria o registro no arquivo
        int id = super.create(lp);
        lp.setID(id);

        // Insere nas duas árvores B+
        indiceIdLista.create(new ParIdListaProduto(lp.getIdLista(), id));
        indiceIdProduto.create(new ParIdProdutoListaProduto(lp.getIdProduto(), id));

        return id;
    }

    /**
     * Leitura da associação pelo id
     * 
     * @param id O ID da associação
     * @return O objeto ListaProduto ou null se não encontrado
     * @throws Exception se ocorrer erro durante a leitura
     */
    @Override
    public ListaProduto read(int id) throws Exception {
        return super.read(id);
    }

    /**
     * Busca todos os produtos da lista pesquisada
     * Utiliza a arvore B+ indexada por idLista para a pesquisa
     * 
     * @param idLista id da lista
     * @return ArrayList com todas as associações da lista
     * @throws Exception se ocorrer erro durante a busca
     */
    public ArrayList<ListaProduto> readByLista(int idLista) throws Exception {
        ArrayList<ListaProduto> resultado = new ArrayList<>();

        // Busca na árvore B+ todos os ods de ListaProduto para essa lista
        ArrayList<ParIdListaProduto> pares = indiceIdLista.read(new ParIdListaProduto(idLista));

        // Para cada par encontrado se lê o registro completo
        for (ParIdListaProduto par : pares) {
            ListaProduto lp = super.read(par.getIdListaProduto());
            if (lp != null) {
                resultado.add(lp);
            }
        }
        return resultado;
    }

    /**
     * Busca todas as listas com o produto pesquisado
     * Utiliza a arvore B+ indexada por idProduto para a pesquisa
     * 
     * @param idProduto id do produto
     * @return ArrayList com todas as associações do produto
     * @throws Exception se ocorrer erro durante a busca
     */
    public ArrayList<ListaProduto> readByProduto(int idProduto) throws Exception {
        ArrayList<ListaProduto> resultado = new ArrayList<>();

        // Busca na árvore todos os ids de ListaProduto para esse produto
        ArrayList<ParIdProdutoListaProduto> pares = indiceIdProduto.read(new ParIdProdutoListaProduto(idProduto));

        // Para cada par encontrado faz uma leitura do registro inteiro
        for (ParIdProdutoListaProduto par : pares) {
            ListaProduto lp = super.read(par.getIdListaProduto());
            if (lp != null) {
                resultado.add(lp);
            }
        }
        return resultado;
    }

    /**
     * Update de uma associação existente
     * Não atualiza os índices já qu, os ids não mudam
     * 
     * @param lp objeto ListaProduto com os dados atualizados
     * @return true se a atualização foi bem-sucedida, false caso contrário
     * @throws Exception se ocorrer erro durante a atualização
     */
    @Override
    public boolean update(ListaProduto lp) throws Exception {
        ListaProduto lpAntigo = super.read(lp.getID());
        if (lpAntigo == null) {
            return false;
        }

        // Se os ids de Lista ou Produto mudaram: reindexa
        if (lpAntigo.getIdLista() != lp.getIdLista() ||
                lpAntigo.getIdProduto() != lp.getIdProduto()) {

            // Remove dos índices antigos
            indiceIdLista.delete(new ParIdListaProduto(lpAntigo.getIdLista(), lp.getID()));
            indiceIdProduto.delete(new ParIdProdutoListaProduto(lpAntigo.getIdProduto(), lp.getID()));

            // Atualiza o arquivo
            boolean success = super.update(lp);

            if (success) {
                // Insere nos novos índices
                indiceIdLista.create(new ParIdListaProduto(lp.getIdLista(), lp.getID()));
                indiceIdProduto.create(new ParIdProdutoListaProduto(lp.getIdProduto(), lp.getID()));
            }

            return success;
        } else {
            // Apenas atualiza quantidade/observações (índices não mudam)
            return super.update(lp);
        }
    }

    /**
     * Remove uma associação entre Lista e Produto
     * Remove o registro do arquivo principal e das árvores
     * 
     * @param id id da associação removida
     * @return true se a remoção foi concluída, false caso de erro
     * @throws Exception se ocorrer erro durante a remoção
     */
    @Override
    public boolean delete(int id) throws Exception {
        ListaProduto lp = super.read(id);
        if (lp == null) {
            return false;
        }

        // Remove das duas árvores B+
        indiceIdLista.delete(new ParIdListaProduto(lp.getIdLista(), id));
        indiceIdProduto.delete(new ParIdProdutoListaProduto(lp.getIdProduto(), id));

        // Remove do arquivo principal
        return super.delete(id);
    }

    /**
     * Remove todas as associações de uma lista
     * 
     * @param idLista id da lista
     * @return Quantidade de associações removidas
     * @throws Exception se ocorrer erro durante a remoção
     */
    public int deletePorLista(int idLista) throws Exception {
        ArrayList<ListaProduto> associacoes = readByLista(idLista);
        int count = 0;

        for (ListaProduto lp : associacoes) {
            if (delete(lp.getID())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Remove todas as associações de um produto
     * 
     * @param idProduto id do produto
     * @return Quantidade de associações removidas
     * @throws Exception se ocorrer erro durante a remoção
     */
    public int deletePorProduto(int idProduto) throws Exception {
        ArrayList<ListaProduto> associacoes = readByProduto(idProduto);
        int count = 0;

        for (ListaProduto lp : associacoes) {
            if (delete(lp.getID())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Testa se existe uma associação entre uma lista e um produto
     * 
     * @param idLista   id da lista
     * @param idProduto id do produto
     * @return objeto ListaProduto se existir, se não retorna NULL
     * @throws Exception se ocorrer erro durante a busca
     */
    public ListaProduto findAssociacao(int idLista, int idProduto) throws Exception {
        ArrayList<ListaProduto> produtos = readByLista(idLista);

        for (ListaProduto lp : produtos) {
            if (lp.getIdProduto() == idProduto) {
                return lp;
            }
        }
        return null;
    }

    /**
     * Fecha os arquivos principal e índices
     * 
     * @throws Exception se ocorrer erro ao fechar
     */
    @Override
    public void close() throws Exception {
        super.close();
    }
}
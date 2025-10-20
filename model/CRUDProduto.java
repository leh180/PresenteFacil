package model;

import bib.Arquivo;
import bib.HashExtensivel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Gerencia todas as operações de persistência (CRUD) para a entidade {@link Produto}.
 * Estende a classe genérica {@link Arquivo} e mantém um índice secundário
 * (Hash Extensível) baseado no GTIN do produto para otimizar as buscas.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class CRUDProduto extends Arquivo<Produto> {

    /**
     * Índice secundário para busca rápida de produtos pelo GTIN.
     */
    private HashExtensivel<ParGtinId> indiceGtin;

    /**
     * Construtor da classe CRUDProduto.
     * Inicializa o arquivo principal de dados ("produtos") e o arquivo de
     * índice de GTIN (Hash Extensível).
     *
     * @throws Exception se ocorrer um erro na abertura ou criação dos arquivos
     * de dados ou índice.
     */
    public CRUDProduto() throws Exception {
        super("produtos", Produto.class.getConstructor());

        File d = new File("data");
        if (!d.exists())
            d.mkdir();

        indiceGtin = new HashExtensivel<>(
                ParGtinId.class.getConstructor(),
                4,
                "data/produtos_gtin.diretorio.idx",
                "data/produtos_gtin.cestos.idx");
    }

    /**
     * Cria um novo registro de produto no arquivo principal e atualiza o índice de GTIN.
     * O produto é sempre criado com o status 'ativo'.
     *
     * @param produto O objeto {@link Produto} a ser persistido (sem ID).
     * @return O ID (inteiro) gerado para o novo produto.
     * @throws Exception se ocorrer um erro durante a escrita no arquivo principal
     * ou no índice.
     */
    @Override
    public int create(Produto produto) throws Exception {
        produto.setAtivo(true);
        int id = super.create(produto);
        produto.setID(id);

        indiceGtin.create(new ParGtinId(produto.getGtin(), id));
        return id;
    }

    /**
     * Busca um produto específico usando seu código GTIN.
     * A busca é otimizada pelo índice de Hash e inclui uma verificação
     * anti-colisão (comparando a String GTIN original).
     *
     * @param gtin O código GTIN (String) a ser procurado.
     * @return O objeto {@link Produto} correspondente, ou {@code null} se não for
     * encontrado ou se houver colisão de hash.
     * @throws Exception se ocorrer um erro during a leitura do índice ou do
     * arquivo principal.
     */
    public Produto readByGtin(String gtin) throws Exception {
        int gtinHash = gtin.hashCode();
        ParGtinId par = indiceGtin.read(gtinHash);

        if (par != null && par.getGtin().equals(gtin)) {
            return super.read(par.getID());
        }
        return null;
    }

    /**
     * Atualiza os dados de um produto no arquivo principal e garante a
     * consistência do índice de GTIN.
     * Se o GTIN for alterado, o índice antigo é removido e o novo é criado.
     * Se o produto for inativado, seu índice é removido.
     *
     * @param novoProduto O objeto {@link Produto} contendo os dados atualizados.
     * @return {@code true} se a atualização for bem-sucedida, {@code false}
     * caso contrário.
     * @throws Exception se ocorrer um erro during a atualização dos arquivos
     * ou índices.
     */
    @Override
    public boolean update(Produto novoProduto) throws Exception {
        Produto produtoAntigo = super.read(novoProduto.getID());
        if (produtoAntigo == null) {
            return false;
        }

        if (super.update(novoProduto)) {
            // Remove o índice do estado antigo
            indiceGtin.delete(produtoAntigo.getGtin().hashCode());

            // Se o novo estado for 'ativo', cria o índice para o novo GTIN
            if (novoProduto.isAtivo()) {
                indiceGtin.create(new ParGtinId(novoProduto.getGtin(), novoProduto.getID()));
            }
            return true;
        }
        return false;
    }

    /**
     * Realiza uma exclusão lógica (soft delete) de um produto.
     * O produto é marcado como 'inativo' e é removido do índice de GTIN
     * (através da chamada a {@code this.update()}), mas permanece no arquivo de dados.
     *
     * @param id O ID (inteiro) do produto a ser inativado.
     * @return {@code true} se a inativação for bem-sucedida, {@code false}
     * caso contrário (ex: produto não encontrado ou já inativo).
     * @throws Exception se ocorrer um erro durante a operação de atualização.
     */
    @Override
    public boolean delete(int id) throws Exception {
        Produto produto = super.read(id);
        if (produto == null || !produto.isAtivo()) {
            return false;
        }

        produto.setAtivo(false);

        // Chama o update deste próprio objeto (CRUDProduto) para garantir
        // que a lógica de remoção do índice seja executada.
        return this.update(produto);
    }

    /**
     * Lê todos os produtos do arquivo de dados, iterando pelos IDs.
     *
     * @param incluirInativos Se {@code true}, a lista de retorno incluirá produtos
     * marcados como inativos. Se {@code false},
     * retornará apenas produtos ativos.
     * @return Uma {@code List<Produto>} contendo os produtos encontrados.
     * @throws Exception se ocorrer um erro durante a leitura do arquivo.
     */
    public List<Produto> readAll(boolean incluirInativos) throws Exception {
        List<Produto> todosProdutos = new ArrayList<>();
        int ultimoID = 0;
        
        this.arquivo.seek(0);
        ultimoID = this.arquivo.readInt();
        
        for (int i = 1; i <= ultimoID; i++) {
            Produto p = super.read(i);
            if (p != null) { // super.read() retorna null para IDs apagados
                if (incluirInativos || p.isAtivo()) {
                    todosProdutos.add(p);
                }
            }
        }
        return todosProdutos;
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
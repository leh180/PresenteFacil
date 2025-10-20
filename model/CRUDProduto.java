package model;

import bib.Arquivo;
import bib.HashExtensivel;
import java.util.ArrayList;
import java.util.List;

/**
 * A classe CRUDProduto estende a classe genérica Arquivo e gere todas as
 * operações de persistência para a entidade Produto.
 * Ela mantém um índice secundário por GTIN (Hash Extensível) para
 * acelerar as buscas. O índice contém TODOS os produtos (ativos e inativos)
 * para garantir a unicidade do GTIN.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class CRUDProduto extends Arquivo<Produto> {

    private HashExtensivel<ParGtinId> indiceGtin;

    public CRUDProduto() throws Exception {
        super("produtos", Produto.class.getConstructor());

        indiceGtin = new HashExtensivel<>(
                ParGtinId.class.getConstructor(),
                4,
                "data/produtos_gtin.diretorio.idx",
                "data/produtos_gtin.cestos.idx");
    }

    /**
     * Cria um novo produto, garantindo a unicidade do GTIN antes da criação.
     * Adiciona o produto ao ficheiro principal e ao índice de GTIN.
     * @param produto O objeto Produto a ser criado.
     * @return O ID gerado para o novo produto.
     * @throws Exception se o GTIN já existir ou ocorrer um erro de ficheiro.
     */
    @Override
    public int create(Produto produto) throws Exception {
        if (readByGtin(produto.getGtin()) != null) {
            throw new Exception("O GTIN informado já está cadastrado (pode estar inativo).");
        }

        produto.setAtivo(true);
        int id = super.create(produto);
        produto.setID(id);

        indiceGtin.create(new ParGtinId(produto.getGtin(), id));
        return id;
    }

    /**
     * Procura um produto pelo seu GTIN, utilizando o índice secundário.
     * Encontra o produto independentemente do seu estado (ativo/inativo).
     * @param gtin O GTIN (String) a ser procurado.
     * @return O objeto Produto se encontrado, caso contrário, null.
     * @throws Exception se ocorrer um erro durante a leitura.
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
     * Atualiza os dados de um produto. Se o GTIN for alterado, o índice é atualizado.
     * O estado 'ativo' é atualizado no ficheiro principal, mas não afeta o índice.
     * @param novoProduto O objeto Produto com os dados atualizados.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     * @throws Exception se ocorrer um erro durante a escrita.
     */
    @Override
    public boolean update(Produto novoProduto) throws Exception {
        Produto produtoAntigo = super.read(novoProduto.getID());
        if (produtoAntigo == null) return false;

        if (super.update(novoProduto)) {
            if (!produtoAntigo.getGtin().equals(novoProduto.getGtin())) {
                indiceGtin.delete(produtoAntigo.getGtin().hashCode());
                indiceGtin.create(new ParGtinId(novoProduto.getGtin(), novoProduto.getID()));
            }
            return true;
        }
        return false;
    }

    /**
     * Inativa um produto (soft delete). Altera o estado 'ativo' para 'false'
     * no ficheiro principal. O produto NÃO é removido do índice de GTIN.
     * @param id O ID do produto a ser inativado.
     * @return true se a operação for bem-sucedida, false caso contrário.
     * @throws Exception se ocorrer um erro de acesso aos ficheiros.
     */
    @Override
    public boolean delete(int id) throws Exception {
        Produto produto = super.read(id);
        if (produto == null || !produto.isAtivo()) {
            return false;
        }

        produto.setAtivo(false);
        
        return this.update(produto);
    }

    /**
     * Lê todos os produtos do ficheiro, com a opção de incluir ou não os inativos.
     * @param incluirInativos Se true, inclui produtos inativos na lista.
     * @return Uma lista de produtos.
     * @throws Exception se ocorrer um erro de leitura.
     */
    public List<Produto> readAllProdutos(boolean incluirInativos) throws Exception {
        List<Produto> produtos = new ArrayList<>();
        int ultimoID = 0;
        
        if (this.arquivo.length() >= 4) {
            this.arquivo.seek(0);
            ultimoID = this.arquivo.readInt();
        } else {
            return produtos;
        }
        
        for (int i = 1; i <= ultimoID; i++) {
            Produto p = super.read(i);
            if (p != null) {
                if (incluirInativos || p.isAtivo()) {
                    produtos.add(p);
                }
            }
        }
        return produtos;
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
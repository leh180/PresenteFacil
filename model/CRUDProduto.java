package model;

import bib.Arquivo;
import bib.HashExtensivel;

import java.beans.PropertyEditorSupport;
import java.io.File;


public class CRUDProduto extends Arquivo<Produto> {
    
    /*
     *Atributos da Classe
    */ 
    private HashExtensivel<ParGtinId> indiceGtin;

    /*
     * Construtores
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

    /* 
    *Métodos Públicos (CRUD)
    */
    @Override
    public int create(Produto produto) throws Exception {
        int id = super.create(produto);
        produto.setID(id);

        indiceGtin.create(new ParGtinId(produto.getGtin(), id));
        return id;
    }

    /**
     * Procura um produto pelo seu gtin, utilizando o índice secundário de
     * hash.
     * Esta operação é otimizada, evitando uma varredura completa do ficheiro
     * principal.
     * 
     * @param gtin O gtin a ser procurado.
     * @return O objeto Usuário se encontrado, caso contrário, null.
     * @throws Exception se ocorrer um erro durante a leitura dos ficheiros.
     */
    public Produto readByGtin(String gtin) throws Exception {
        ParGtinId par = indiceGtin.read(gtin.hashCode());

        if (par != null && par.getGtin().equals(gtin)) {
            return super.read(par.getID());
        }
        return null;
    }

    /**
     * Atualiza os dados de um produto, garantindo a consistência do índice de gtin.
     * Este método também lida com a reativação de um produto inativo.
     * * @param novoProduto O objeto Produto com os dados atualizados.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     * @throws Exception se ocorrer um erro durante a escrita nos ficheiros.
     */
    @Override
    public boolean update(Produto novoProduto) throws Exception {
        Produto produtoAntigo = super.read(novoProduto.getID());
        if (produtoAntigo == null) {
            return false;
        }

        // Realiza a atualização no arquivo principal
        boolean success = super.update(novoProduto);

        if (success) {
            // Lógica para atualizar o índice secundário (GTIN)
            String gtinAntigo = produtoAntigo.getGtin();
            String gtinNovo = novoProduto.getGtin();
            boolean eraAtivo = produtoAntigo.isAtivo();
            boolean ehAtivo = novoProduto.isAtivo();

            // Se o GTIN mudou, remove o antigo índice
            if (!gtinAntigo.equals(gtinNovo)) {
                indiceGtin.delete(gtinAntigo.hashCode());
            }

            // Se o produto se tornou ativo (reativação) ou se o GTIN mudou enquanto ativo
            if ((!eraAtivo && ehAtivo) || (ehAtivo && !gtinAntigo.equals(gtinNovo))) {
                indiceGtin.create(new ParGtinId(gtinNovo, novoProduto.getID()));
            }
            
            // Se o produto foi inativado
            if (eraAtivo && !ehAtivo) {
                 indiceGtin.delete(gtinAntigo.hashCode());
            }
            
            return true;
        }
        return false;
    }

    /**
     * Inativa um produto (soft delete). O produto não é removido fisicamente,
     * mas seu estado 'ativo' é alterado para 'false' e ele é removido do índice de busca.
     * * @param id O ID do produto a ser inativado.
     * @return true se a operação for bem-sucedida, false caso contrário.
     * @throws Exception se ocorrer um erro de acesso aos ficheiros.
     */
    public boolean delete(int id) throws Exception {
        Produto produto = super.read(id);
        if (produto == null || !produto.isAtivo()) {
            // Não pode deletar um produto que não existe ou que já está inativo
            return false;
        }
        
        // Remove do índice secundário antes de inativar
        indiceGtin.delete(produto.getGtin().hashCode());

        // Inativa o produto
        produto.setAtivo(false);
        
        // Atualiza o registro no arquivo principal
        return super.update(produto);
    }

    /**
     * Fecha a ligação com o ficheiro de dados principal (usuarios.db).
     * Este método é essencial para garantir que todas as alterações sejam salvas no
     * disco.
     * 
     * @throws Exception se ocorrer um erro ao fechar o ficheiro.
     */
    @Override
    public void close() throws Exception {
        super.close();
    }
}
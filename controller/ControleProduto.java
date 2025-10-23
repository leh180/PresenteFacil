package controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.CRUDProduto;
import model.Produto;
import view.VisaoProduto;
import view.VisaoUsuario;

/**
 * A classe 'ControleProduto' orquestra todas as ações relacionadas à gestão
 * de produtos, servindo como intermediário entre a visão (interface com o
 * utilizador) e o modelo (acesso aos dados).
 */
public class ControleProduto {

    // ------------------------------------------ Atributos da Classe
    // ------------------------------------------

    private CRUDProduto crudProduto;
    private VisaoProduto visaoProduto;
    private VisaoUsuario visaoUsuario; // Reutilizado para mensagens e pausas

    // ------------------------------------------ Construtor
    // ------------------------------------------

    public ControleProduto(CRUDProduto crudProduto) {
        this.crudProduto = crudProduto;
        this.visaoProduto = new VisaoProduto();
        this.visaoUsuario = new VisaoUsuario();
    }

    // ------------------------------------------ Métodos de Menu
    // ------------------------------------------

    /**
     * Ponto de entrada e loop principal para o menu de gestão de produtos.
     */
    public void menuProdutos() {
        String opcao;
        do {
            opcao = visaoProduto.menuPrincipalProdutos();
            switch (opcao) {
                case "1":
                    buscarProdutoPorGtin();
                    break;
                case "2":
                    listarProdutosPaginado();
                    break;
                case "3":
                    cadastrarNovoProduto();
                    break;
                case "r":
                    break;
                default:
                    visaoUsuario.mostrarMensagem("Opção inválida!");
                    visaoUsuario.pausa();
                    break;
            }
        } while (!opcao.equals("r"));
    }

    /**
     * Gere a lógica de um sub-menu para um produto específico, permitindo
     * alteração, inativação ou reativação.
     *
     * @param produto O produto a ser gerido.
     */
    private void gerenciarProduto(Produto produto) {
        String opcao;
        do {
            opcao = visaoProduto.mostrarDetalhesProduto(produto);
            switch (opcao) {
                case "1":
                    alterarProduto(produto);
                    break;
                case "2":
                    if (produto.isAtivo()) {
                        inativarProduto(produto);
                    } else {
                        reativarProduto(produto);
                    }
                    // Após inativar/reativar, o estado do produto muda, então saímos do menu de
                    // detalhes.
                    return;
                case "r":
                    break;
                default:
                    visaoUsuario.mostrarMensagem("Opção inválida!");
                    visaoUsuario.pausa();
                    break;
            }
        } while (!opcao.equals("r"));
    }

    // ------------------------------------------ Métodos de Ação (Lógica de
    // Negócio) ------------------------------------------

    /**
     * Controla a lógica de paginação para a listagem de produtos.
     * Lê todos os produtos ativos, ordena-os e exibe-os em páginas.
     */
    private void listarProdutosPaginado() {
        try {
            // 1. Lê TODOS os produtos do CRUD
            List<Produto> todosProdutos = crudProduto.readAll();

            // 2. Ordena a lista completa por nome
            Collections.sort(todosProdutos, Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER));

            final int ITENS_POR_PAGINA = 10;
            int totalPaginas = (int) Math.ceil((double) todosProdutos.size() / ITENS_POR_PAGINA);
            if (totalPaginas == 0)
                totalPaginas = 1;
            int paginaAtual = 1;

            String opcao;
            do {
                int inicio = (paginaAtual - 1) * ITENS_POR_PAGINA;
                int fim = Math.min(inicio + ITENS_POR_PAGINA, todosProdutos.size());

                // 3. Envia apenas a "fatia" de 10 para a visão
                List<Produto> produtosPagina = todosProdutos.subList(inicio, fim);

                opcao = visaoProduto.mostrarListagemPaginada(produtosPagina, paginaAtual, totalPaginas);

                switch (opcao) {
                    case "p":
                        if (paginaAtual < totalPaginas)
                            paginaAtual++;
                        break;
                    case "a":
                        if (paginaAtual > 1)
                            paginaAtual--;
                        break;
                    case "r":
                        // Sai do loop
                        break;
                    default:
                        try {
                            int indice = Integer.parseInt(opcao);
                            if (indice == 0)
                                indice = 10; // Converte 0 para 10
                            indice--; // Ajusta para base 0

                            if (indice >= 0 && indice < produtosPagina.size()) {
                                gerenciarProduto(produtosPagina.get(indice));
                                // Ao retornar, recarrega a lista para refletir possíveis alterações
                                listarProdutosPaginado();
                                return;
                            } else {
                                visaoUsuario.mostrarMensagem("ERRO: Opção numérica inválida!");
                            }
                        } catch (NumberFormatException e) {
                            visaoUsuario.mostrarMensagem("ERRO: Opção inválida!");
                        }
                        visaoUsuario.pausa();
                        break;
                }

            } while (!opcao.equals("r"));

        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao listar produtos: " + e.getMessage());
            e.printStackTrace();
            visaoUsuario.pausa();
        }
    }

    /**
     * Gere o fluxo de cadastro de um novo produto, incluindo a verificação de
     * duplicidade de GTIN.
     */
    private void cadastrarNovoProduto() {
        try {
            Produto p = visaoProduto.lerDadosNovoProduto();
            int id = crudProduto.create(p);
            visaoUsuario.mostrarMensagem("Produto \"" + p.getNome() + "\" cadastrado com sucesso!");
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("ERRO ao cadastrar produto: " + e.getMessage());
        }
        visaoUsuario.pausa();
    }

    /**
     * Gere o fluxo de busca de um produto pelo seu GTIN.
     */
    private void buscarProdutoPorGtin() {
        String gtin = visaoProduto.lerGtinBusca();
        try {
            Produto produto = crudProduto.readByGtin(gtin);
            if (produto != null) {
                gerenciarProduto(produto);
            } else {
                visaoUsuario.mostrarMensagem("\nNenhum produto encontrado com o GTIN \"" + gtin + "\" ativo.");
                visaoUsuario.pausa();
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao buscar produto: " + e.getMessage());
            e.printStackTrace();
            visaoUsuario.pausa();
        }
    }

    /**
     * Altera os dados de um produto (nome e descrição).
     *
     * @param produto O produto a ser alterado.
     */
    private void alterarProduto(Produto produto) {
        Produto dadosAlterados = visaoProduto.lerDadosAlteracao(produto);
        try {
            if (crudProduto.update(dadosAlterados)) {
                // Atualiza o objeto local para refletir a mudança na visão
                produto.setNome(dadosAlterados.getNome());
                produto.setDescricao(dadosAlterados.getDescricao());
                visaoUsuario.mostrarMensagem("\nProduto alterado com sucesso!");
            } else {
                visaoUsuario.mostrarMensagem("\nFalha ao alterar o produto.");
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao alterar produto: " + e.getMessage());
            e.printStackTrace();
        }
        visaoUsuario.pausa();
    }

    /**
     * Inativa um produto (soft delete).
     *
     * @param produto O produto a ser inativado.
     */
    private void inativarProduto(Produto produto) {
        if (visaoProduto.confirmarAcao("inativar", produto.getNome())) {
            try {
                // O método delete do CRUDProduto já faz a inativação (soft delete)
                if (crudProduto.delete(produto.getID())) {
                    visaoUsuario.mostrarMensagem("\nProduto inativado com sucesso!");
                } else {
                    visaoUsuario.mostrarMensagem("\nFalha ao inativar o produto.");
                }
            } catch (Exception e) {
                visaoUsuario.mostrarMensagem("\nERRO ao inativar produto: " + e.getMessage());
                e.printStackTrace();
            }
        }
        visaoUsuario.pausa();
    }

    /**
     * Reativa um produto que estava inativo.
     *
     * @param produto O produto a ser reativado.
     */
    private void reativarProduto(Produto produto) {
        if (visaoProduto.confirmarAcao("reativar", produto.getNome())) {
            try {
                produto.setAtivo(true); // Modifica o estado
                if (crudProduto.update(produto)) { // O update lida com a reativação no índice
                    visaoUsuario.mostrarMensagem("\nProduto reativado com sucesso!");
                } else {
                    visaoUsuario.mostrarMensagem("\nFalha ao reativar o produto.");
                }
            } catch (Exception e) {
                visaoUsuario.mostrarMensagem("\nERRO ao reativar produto: " + e.getMessage());
                e.printStackTrace();
            }
        }
        visaoUsuario.pausa();
    }

    /**
     * Fecha as ligações com os ficheiros de dados geridos por este controlador.
     */
    public void close() throws Exception {
        crudProduto.close();
    }
}

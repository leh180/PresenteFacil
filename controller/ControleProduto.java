package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import model.CRUDLista;
import model.CRUDListaProduto;
import model.CRUDProduto;
import model.Lista;
import model.ListaProduto;
import model.Produto;
import model.Usuario;
import view.VisaoProduto;
import view.VisaoUsuario;

/**
 * Orquestra todas as ações relacionadas à gestão de produtos. Esta classe atua
 * como um intermediário entre a interface do usuário (Visão) e o acesso aos
 * dados (Modelo), aplicando as regras de negócio pertinentes.
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class ControleProduto {

    private CRUDProduto crudProduto;
    private CRUDLista crudLista;
    private CRUDListaProduto crudListaProduto;
    private VisaoProduto visaoProduto;
    private VisaoUsuario visaoUsuario;

    /**
     * Construtor que implementa o padrão de Injeção de Dependência. Recebe as
     * instâncias de CRUD necessárias para realizar as operações de dados
     * relacionadas a produtos, listas e suas associações.
     *
     * @param crudP Instância de CRUDProduto para operações com produtos.
     * @param crudL Instância de CRUDLista para operações com listas.
     * @param crudLP Instância de CRUDListaProduto para operações com a tabela
     * associativa.
     */
    public ControleProduto(CRUDProduto crudP, CRUDLista crudL, CRUDListaProduto crudLP) {
        this.crudProduto = crudP;
        this.crudLista = crudL;
        this.crudListaProduto = crudLP;
        this.visaoProduto = new VisaoProduto();
        this.visaoUsuario = new VisaoUsuario();
    }

    /**
     * Ponto de entrada e loop principal para o menu de gestão de produtos.
     * Apresenta ao usuário as opções de buscar, listar ou cadastrar produtos.
     *
     * @param usuarioLogado O usuário atualmente autenticado na sessão.
     */
    public void menuPrincipal(Usuario usuarioLogado) {
        String opcao;
        do {
            opcao = visaoProduto.menuPrincipalProdutos();
            switch (opcao) {
                case "1":
                    buscarProdutoPorGtin(usuarioLogado);
                    break;
                case "2":
                    listarProdutosPaginado(usuarioLogado, false); 
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
     * Gerencia o submenu de um produto específico. Este método realiza uma
     * consulta cruzada para encontrar e exibir em quais listas do usuário o
     * produto está, além de contar em quantas listas de outros usuários ele
     * aparece. Oferece opções para alterar, inativar ou reativar o produto.
     *
     * @param produto O produto a ser gerenciado.
     * @param usuarioLogado O usuário que está visualizando os detalhes do
     * produto.
     */
    private void gerenciarProduto(Produto produto, Usuario usuarioLogado) {
        String opcao;
        do {
            try {
                List<ListaProduto> associacoes = crudListaProduto.readAllByProduto(produto.getID());
                List<Lista> listasDoUsuario = new ArrayList<>();
                int countOutrasListas = 0;

                for (ListaProduto lp : associacoes) {
                    Lista l = crudLista.read(lp.getIdLista());
                    if (l != null) {
                        if (l.getIdUsuario() == usuarioLogado.getID()) {
                            listasDoUsuario.add(l);
                        } else {
                            countOutrasListas++;
                        }
                    }
                }
                Collections.sort(listasDoUsuario, Comparator.comparing(Lista::getNome, String.CASE_INSENSITIVE_ORDER));

                opcao = visaoProduto.mostrarDetalhesProduto(produto, listasDoUsuario, countOutrasListas);

                switch (opcao) {
                    case "1":
                        alterarProduto(produto);
                        break;
                    case "2":
                        if (produto.isAtivo()) {
                            inativarProduto(produto, associacoes.size());
                        } else {
                            reativarProduto(produto);
                        }
                        return; 
                    case "r":
                        break;
                    default:
                        visaoUsuario.mostrarMensagem("Opção inválida!");
                        visaoUsuario.pausa();
                        break;
                }
            } catch(Exception e) {
                visaoUsuario.mostrarMensagem("ERRO ao gerir o produto: " + e.getMessage());
                e.printStackTrace();
                opcao = "r";
            }
        } while (!opcao.equals("r"));
    }
    
    /**
     * Busca todos os produtos e os exibe em uma lista paginada. Permite a
     * navegação entre páginas e a seleção de um produto para gerenciamento.
     *
     * @param usuarioLogado O usuário autenticado, necessário para o submenu de
     * gerenciamento.
     */
    private void listarProdutosPaginado(Usuario usuarioLogado, boolean incluirInativos) {
        try {
            List<Produto> produtosParaListar = crudProduto.readAllProdutos(incluirInativos); 
            Collections.sort(produtosParaListar, Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER));

            int ITENS_POR_PAGINA = 10;
            int totalPaginas = (int) Math.ceil((double) produtosParaListar.size() / ITENS_POR_PAGINA);
            if (totalPaginas == 0) totalPaginas = 1;
            int paginaAtual = 1;
            String opcao;
            do {
                int inicio = (paginaAtual - 1) * ITENS_POR_PAGINA;
                int fim = Math.min(inicio + ITENS_POR_PAGINA, produtosParaListar.size());
                List<Produto> produtosPagina = produtosParaListar.subList(inicio, fim);

                opcao = visaoProduto.mostrarListagemPaginada(produtosPagina, paginaAtual, totalPaginas); 

                switch (opcao) {
                    case "p":
                        if (paginaAtual < totalPaginas) paginaAtual++;
                        break;
                    case "a":
                        if (paginaAtual > 1) paginaAtual--;
                        break;
                    case "r":
                        break;
                    default:
                        try {
                            int indice = Integer.parseInt(opcao) - 1;
                            if (indice >= 0 && indice < produtosPagina.size()) {
                                gerenciarProduto(produtosPagina.get(indice), usuarioLogado);
                                listarProdutosPaginado(usuarioLogado, incluirInativos); 
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
     * Orquestra o processo de cadastro de um novo produto no sistema.
     * Pede os dados ao utilizador, verifica a duplicidade de GTIN e, se for
     * único, cria o produto.
     */
    private void cadastrarNovoProduto() {
        try {
            Produto p = visaoProduto.lerDadosNovoProduto();
            int id = crudProduto.create(p);
            visaoUsuario.mostrarMensagem("Produto \"" + p.getNome() + "\" cadastrado com sucesso! (ID: " + id + ")");
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("ERRO ao cadastrar produto: " + e.getMessage());
        }
        visaoUsuario.pausa();
    }
    
    /**
     * Permite a busca de um produto específico pelo seu código GTIN.
     * Se o produto for encontrado, encaminha para o menu de gestão de detalhes.
     * @param usuarioLogado O usuário que realiza a busca.
     */
    private void buscarProdutoPorGtin(Usuario usuarioLogado) {
        String gtin = visaoProduto.lerGtinBusca();
        try {
            Produto produto = crudProduto.readByGtin(gtin); 
            if (produto != null) {
                gerenciarProduto(produto, usuarioLogado);
            } else {
                visaoUsuario.mostrarMensagem("\nNenhum produto (ativo ou inativo) encontrado com o GTIN \"" + gtin + "\".");
                visaoUsuario.pausa();
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao buscar produto: " + e.getMessage());
            e.printStackTrace();
            visaoUsuario.pausa();
        }
    }
    
    /**
     * Controla a lógica para alterar os dados de um produto existente
     * (nome e descrição).
     * @param produto O produto a ser modificado.
     */
    private void alterarProduto(Produto produto) {
        try {
            Produto dadosAlterados = visaoProduto.lerDadosAlteracao(produto);
            produto.setNome(dadosAlterados.getNome());
            produto.setDescricao(dadosAlterados.getDescricao());
            if (crudProduto.update(produto)) {
                visaoUsuario.mostrarMensagem("\nProduto alterado com sucesso!");
            } else {
                visaoUsuario.mostrarMensagem("\nFalha ao alterar o produto.");
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao alterar produto: " + e.getMessage());
        }
        visaoUsuario.pausa();
    }

    /**
     * Aplica a regra de negócio para inativar um produto. Um produto só pode
     * ser inativado se não estiver associado a nenhuma lista.
     *
     * @param produto O produto a ser inativado.
     * @param totalAssociacoes O número de listas às quais o produto está
     * vinculado.
     * @throws Exception se ocorrer um erro durante a operação de exclusão
     * lógica no CRUD.
     */
    private void inativarProduto(Produto produto, int totalAssociacoes) {
        if (totalAssociacoes > 0) {
            visaoUsuario.mostrarMensagem("\nERRO: Não é possível inativar um produto que está associado a " + totalAssociacoes + " lista(s).");
            visaoUsuario.pausa();
            return;
        }
        if (visaoProduto.confirmarAcao("inativar", produto.getNome())) {
            try {
                if (crudProduto.delete(produto.getID())) {
                    visaoUsuario.mostrarMensagem("\nProduto inativado com sucesso!");
                } else {
                    visaoUsuario.mostrarMensagem("\nFalha ao inativar o produto.");
                }
            } catch (Exception e) {
                visaoUsuario.mostrarMensagem("\nERRO ao inativar produto: " + e.getMessage());
            }
        }
        visaoUsuario.pausa();
    }

    /**
     * Reverte a inativação de um produto, tornando-o ativo novamente no
     * sistema.
     *
     * @param produto O produto a ser reativado.
     * @throws Exception se ocorrer um erro durante a operação de atualização no
     * CRUD.
     */
    private void reativarProduto(Produto produto) {
        if (visaoProduto.confirmarAcao("reativar", produto.getNome())) {
            try {
                produto.setAtivo(true);
                if (crudProduto.update(produto)) {
                    visaoUsuario.mostrarMensagem("\nProduto reativado com sucesso!");
                } else {
                    visaoUsuario.mostrarMensagem("\nFalha ao reativar o produto.");
                }
            } catch (Exception e) {
                visaoUsuario.mostrarMensagem("\nERRO ao reativar produto: " + e.getMessage());
            }
        }
        visaoUsuario.pausa();
    }

    /**
     * Fecha a conexão com o banco de dados através do objeto CRUDProduto.
     *
     * @throws Exception se ocorrer um erro ao fechar o recurso.
     */
    public void close() throws Exception {
        crudProduto.close();
    }
}
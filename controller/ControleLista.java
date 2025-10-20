package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.CRUDLista;
import model.CRUDListaProduto;
import model.CRUDProduto;
import model.CRUDUsuario;
import model.Lista;
import model.ListaProduto;
import model.Produto;
import model.Usuario;
import view.VisaoLista;
import view.VisaoUsuario;
import view.Pair;

/**
 * A classe ControleLista é responsável por gerenciar a lógica de negócio
 * relacionada às listas de compras, interagindo com a camada de modelo (CRUDs)
 * e a camada de visão (VisaoLista, VisaoUsuario).
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.5
 */
public class ControleLista {

    private CRUDLista crudLista;
    private CRUDProduto crudProduto;
    private CRUDListaProduto crudListaProduto;
    private CRUDUsuario crudUsuario;
    private VisaoLista visaoLista;
    private VisaoUsuario visaoUsuario;

    /**
     * Construtor da classe ControleLista.
     * Inicializa os objetos CRUD necessários para a manipulação dos dados
     * e as classes de visão para interação com o usuário.
     *
     * @param crudL Instância de CRUDLista para operações com listas.
     * @param crudP Instância de CRUDProduto para operações com produtos.
     * @param crudLP Instância de CRUDListaProduto para operações com a tabela associativa.
     * @param crudU Instância de CRUDUsuario para operações com usuários.
     */
    public ControleLista(CRUDLista crudL, CRUDProduto crudP, CRUDListaProduto crudLP, CRUDUsuario crudU) {
        this.crudLista = crudL;
        this.crudProduto = crudP;
        this.crudListaProduto = crudLP;
        this.crudUsuario = crudU;
        this.visaoLista = new VisaoLista();
        this.visaoUsuario = new VisaoUsuario();
    }

    
    /**
     * Exibe o menu principal de "Minhas Listas" para um usuário logado.
     * Permite ao usuário visualizar suas listas, criar uma nova lista,
     * selecionar uma lista para ver detalhes ou retornar ao menu anterior.
     * O método trata internamente as exceções de banco de dados e de entrada do usuário.
     *
     * @param usuarioLogado O objeto do usuário que está com a sessão ativa.
     */
    public void menuMinhasListas(Usuario usuarioLogado) {
        String opcao;
        do {
            try {
                List<Lista> minhasListas = crudLista.readAllByUser(usuarioLogado.getID());
                Collections.sort(minhasListas);
                
                opcao = visaoLista.mostrarListas(minhasListas, usuarioLogado.getNome()).toLowerCase().trim();
                
                if (opcao.equals("n")) {
                    criarNovaLista(usuarioLogado);
                } else if (!opcao.equals("r")) {
                    try {
                        int indice = Integer.parseInt(opcao) - 1;
                        if (indice >= 0 && indice < minhasListas.size()) {
                            menuDetalhesLista(minhasListas.get(indice), usuarioLogado);
                        } else {
                            visaoUsuario.mostrarMensagem("\nERRO: Opção numérica inválida!");
                            visaoUsuario.pausa();
                        }
                    } catch (NumberFormatException e) {
                        visaoUsuario.mostrarMensagem("\nERRO: Opção inválida! Tente novamente.");
                        visaoUsuario.pausa();
                    }
                }
            } catch (Exception e) {
                visaoUsuario.mostrarMensagem("\nERRO ao gerir as listas: " + e.getMessage());
                e.printStackTrace();
                opcao = "r";
            }
        } while (!opcao.equals("r"));
    }

    /**
     * Exibe o menu de detalhes de uma lista específica.
     * Oferece opções para gerenciar produtos, alterar dados da lista,
     * excluí-la ou retornar.
     *
     * @param lista A lista cujos detalhes serão exibidos e gerenciados.
     * @param usuarioLogado O usuário logado, para contextualizar a visão.
     */
    private void menuDetalhesLista(Lista lista, Usuario usuarioLogado) {
        String opcao;
        do {
            opcao = visaoLista.mostrarDetalhesLista(lista, usuarioLogado.getNome(), true).trim().toLowerCase();
            
            switch (opcao) {
                case "1":
                    menuGerenciarProdutosDaLista(lista);
                    break;
                case "2":
                    alterarLista(lista);
                    break;
                case "3":
                    if (excluirLista(lista)) {
                        return;
                    }
                    break;
                case "r":
                    break;
                default:
                    visaoUsuario.mostrarMensagem("\nOpção inválida!");
                    visaoUsuario.pausa();
                    break;
            }
        } while (!opcao.equals("r"));
    }

    /**
     * Permite ao usuário procurar por uma lista pública utilizando seu código único.
     * Exibe os detalhes da lista se encontrada, caso contrário, informa o usuário.
     */
    public void menuProcurarLista() {
        String codigo = visaoLista.pedirCodigo();
        try {
            Lista lista = crudLista.readByCodigo(codigo);
            if (lista != null) {
                Usuario proprietario = crudUsuario.read(lista.getIdUsuario());
                String nomeProprietario = (proprietario != null) ? proprietario.getNome() : "Desconhecido";
                
                visaoLista.mostrarDetalhesLista(lista, nomeProprietario, false);
            } else {
                visaoUsuario.mostrarMensagem("\nNenhuma lista encontrada com o código \"" + codigo + "\".");
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao procurar a lista: " + e.getMessage());
        }
        visaoUsuario.pausa();
    }

    /**
     * Controla o menu de gerenciamento de produtos dentro de uma lista específica.
     * Permite visualizar os produtos, adicionar novos ou selecionar um produto
     * para ver mais detalhes.
     *
     * @param lista A lista cujos produtos serão gerenciados.
     */
    private void menuGerenciarProdutosDaLista(Lista lista) {
        String opcao;
        do {
            try {
                List<ListaProduto> associacoes = crudListaProduto.readAllByLista(lista.getID());
                List<Pair<Produto, ListaProduto>> produtosParaExibir = new ArrayList<>();
                for(ListaProduto lp : associacoes) {
                    Produto p = crudProduto.read(lp.getIdProduto());
                    
                    if (p != null && p.isAtivo()) {
                        produtosParaExibir.add(new Pair<>(p, lp));
                    }
                }
                
                opcao = visaoLista.menuGerenciarProdutosDaLista(produtosParaExibir, lista.getNome());

                if (opcao.equals("a")) {
                    menuAcrescentarProduto(lista);
                } else if (!opcao.equals("r")) {
                    try {
                        int indice = Integer.parseInt(opcao) - 1;
                        if(indice >= 0 && indice < produtosParaExibir.size()) {
                            Pair<Produto, ListaProduto> par = produtosParaExibir.get(indice);
                            menuDetalhesProdutoNaLista(par.first, par.second);
                        } else {
                            visaoUsuario.mostrarMensagem("Opção numérica inválida!");
                        }
                    } catch (NumberFormatException e) {
                        visaoUsuario.mostrarMensagem("Opção inválida!");
                    }
                }
            } catch (Exception e) {
                visaoUsuario.mostrarMensagem("ERRO ao gerir produtos da lista: " + e.getMessage());
                opcao = "r";
            }
        } while(!opcao.equals("r"));
    }

    /**
     * Gerencia a lógica para adicionar um novo produto a uma lista.
     * Atualmente, a adição é feita buscando um produto existente pelo seu GTIN.
     *
     * @param lista A lista à qual o produto será adicionado.
     */
    private void menuAcrescentarProduto(Lista lista) {
        String opcao = visaoLista.menuAcrescentarProduto();
        if(opcao.equals("1")) {
            String gtin = visaoLista.pedirGtinParaAdicionar();
            try {
                Produto p = crudProduto.readByGtin(gtin);
                if(p != null) {
                    if (p.isAtivo()) {
                        if (crudListaProduto.findAssociacao(lista.getID(), p.getID()) != null) {
                            visaoUsuario.mostrarMensagem("ERRO: O produto \"" + p.getNome() + "\" já está nesta lista.");
                        } else {
                            int qtd = visaoLista.pedirNovaQuantidade(1);
                            String obs = visaoLista.pedirNovasObservacoes("");
                            ListaProduto lp = new ListaProduto(-1, lista.getID(), p.getID(), qtd, obs);
                            crudListaProduto.create(lp);
                            visaoUsuario.mostrarMensagem("Produto \"" + p.getNome() + "\" adicionado à lista!");
                        }
                    } else {
                        visaoUsuario.mostrarMensagem("ERRO: Este produto está inativo e não pode ser adicionado.");
                    }
                } else {
                    visaoUsuario.mostrarMensagem("Nenhum produto encontrado com o GTIN informado.");
                }
            } catch(Exception e) {
                visaoUsuario.mostrarMensagem("ERRO ao adicionar produto: " + e.getMessage());
            }
        } else if (opcao.equals("2")) {
            visaoUsuario.mostrarMensagem("Para adicionar um produto, utilize a busca por GTIN.\nConsulte o menu 'Produtos' no menu principal para encontrar o GTIN desejado.");
        }
        visaoUsuario.pausa();
    }
    
    /**
     * Exibe o menu de detalhes de um produto específico dentro de uma lista.
     * Permite ao usuário alterar a quantidade, as observações ou remover
     * o produto da lista.
     *
     * @param produto O objeto Produto a ser gerenciado.
     * @param listaProduto O objeto de associação que contém quantidade e observações.
     */
    private void menuDetalhesProdutoNaLista(Produto produto, ListaProduto listaProduto) {
        String opcao;
        do {
            try {
                opcao = visaoLista.mostrarDetalhesProdutoNaLista(produto, listaProduto);
                switch(opcao) {
                    case "1": // Alterar quantidade
                        int novaQtd = visaoLista.pedirNovaQuantidade(listaProduto.getQuantidade());
                        listaProduto.setQuantidade(novaQtd);
                        crudListaProduto.update(listaProduto);
                        visaoUsuario.mostrarMensagem("Quantidade alterada com sucesso!");
                        break;
                    case "2": // Alterar observações
                        String novaObs = visaoLista.pedirNovasObservacoes(listaProduto.getObservacoes());
                        listaProduto.setObservacoes(novaObs);
                        crudListaProduto.update(listaProduto);
                        visaoUsuario.mostrarMensagem("Observações alteradas com sucesso!");
                        break;
                    case "3": // Remover produto da lista
                        if(visaoLista.confirmarRemocaoProduto(produto.getNome())) {
                            crudListaProduto.delete(listaProduto.getID());
                            visaoUsuario.mostrarMensagem("Produto removido da lista!");
                            return;
                        }
                        break;
                    case "r":
                        break;
                    default:
                        visaoUsuario.mostrarMensagem("Opção inválida!");
                        break;
                }
            } catch(Exception e) {
                visaoUsuario.mostrarMensagem("ERRO ao gerir o produto na lista: " + e.getMessage());
                opcao = "r";
            }
        } while(!opcao.equals("r"));
    }

    /**
     * Orquestra a criação de uma nova lista.
     * Pede os dados da nova lista através da visão e a insere no banco de dados.
     *
     * @param usuarioLogado O usuário que será o dono da nova lista.
     */
    private void criarNovaLista(Usuario usuarioLogado) {
        try {
            Lista novaLista = visaoLista.lerDadosNovaLista();
            novaLista.setIdUsuario(usuarioLogado.getID());
            int id = crudLista.create(novaLista);
            visaoUsuario.mostrarMensagem("\nLista \"" + novaLista.getNome() + "\" criada com sucesso! (ID: " + id + ")");
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao criar a lista: " + e.getMessage());
        }
        visaoUsuario.pausa();
    }

    /**
     * Controla a alteração dos dados de uma lista existente.
     *
     * @param lista A lista a ser alterada.
     */
    private void alterarLista(Lista lista) {
        try {
            Lista dadosAlterados = visaoLista.lerDadosAlteracaoLista(lista);
            lista.setNome(dadosAlterados.getNome());
            lista.setDescricao(dadosAlterados.getDescricao());
            lista.setDataLimite(dadosAlterados.getDataLimite());

            if (crudLista.update(lista)) {
                visaoUsuario.mostrarMensagem("\nLista alterada com sucesso!");
            } else {
                visaoUsuario.mostrarMensagem("\nFalha ao alterar a lista.");
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao alterar a lista: " + e.getMessage());
        }
        visaoUsuario.pausa();
    }
    
    /**
     * Gerencia a exclusão de uma lista.
     * Pede confirmação ao usuário e, se confirmado, remove todas as associações
     * de produtos e depois a própria lista.
     *
     * @param lista A lista a ser excluída.
     * @return {@code true} se a lista foi excluída com sucesso, {@code false} caso contrário.
     */
    private boolean excluirLista(Lista lista) {
        if (visaoLista.confirmarExclusao(lista.getNome())) {
            try {
                crudListaProduto.deleteByLista(lista.getID());

                if (crudLista.delete(lista.getID())) {
                    visaoUsuario.mostrarMensagem("\nLista \"" + lista.getNome() + "\" e todas as suas associações foram excluídas com sucesso.");
                    visaoUsuario.pausa();
                    return true;
                } else {
                    visaoUsuario.mostrarMensagem("\nFalha ao excluir a lista.");
                }
            } catch (Exception e) {
                visaoUsuario.mostrarMensagem("\nERRO ao excluir a lista: " + e.getMessage());
            }
        }
        visaoUsuario.pausa();
        return false;
    }

    /**
     * Fecha as conexões com o banco de dados através do objeto CRUDLista.
     * Este método deve ser chamado ao final do ciclo de vida do controller
     * para liberar os recursos do banco.
     *
     * @throws Exception Se ocorrer um erro ao fechar a conexão com o banco de dados.
     */
    public void close() throws Exception {
        crudLista.close();
    }
}
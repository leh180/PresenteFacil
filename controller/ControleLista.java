package controller;

import java.util.*;
import model.*;
import view.*;
import view.Pair;
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
 * A classe 'ControleLista' é responsável por gerir toda a lógica de negócio
 * relacionada às listas, atuando como o intermediário entre as classes de
 * modelo (dados)
 * e as classes de visão (interface com o utilizador).
 */
public class ControleLista {

    // ------------------------------------------ Atributos da Classe
    // ------------------------------------------

    private CRUDLista crudLista;
    private VisaoLista visaoLista;
    private VisaoUsuario visaoUsuario;
    private CRUDUsuario crudUsuario;
    private CRUDProduto crudProduto;
    private CRUDListaProduto crudListaProduto;

    // ------------------------------------------ Construtor
    // ------------------------------------------

    /**
     * Construtor que recebe a instância de CRUDLista (Injeção de Dependência).
     * Garante que toda a aplicação partilhe a mesma ligação com os ficheiros.
     * 
     * @param crudLista A instância de CRUD para as listas.
     */
    public ControleLista(CRUDLista crudL, CRUDProduto crudP, CRUDListaProduto crudLP, CRUDUsuario crudU) {
        this.crudLista = crudL;
        this.crudProduto = crudP;
        this.crudListaProduto = crudLP;
        this.crudUsuario = crudU;
        this.visaoLista = new VisaoLista();
        this.visaoUsuario = new VisaoUsuario();
    }

    // ------------------------------------------ Métodos de Menu
    // ------------------------------------------

    /**
     * Menu principal para um utilizador gerir as suas próprias listas.
     * Este método é o ponto de entrada para a gestão de listas pessoais.
     * Ele lê todas as listas do utilizador, exibe-as de forma ordenada e
     * direciona para as ações de criação ou gestão de uma lista específica.
     * 
     * @param usuarioLogado O utilizador que está com a sessão ativa.
     */
    public void menuMinhasListas(Usuario usuarioLogado) {
        String opcao;
        do {
            try {
                List<Lista> minhasListas = crudLista.readAllByUser(usuarioLogado.getID());
                // Ordena as listas por ordem alfabética do nome, ignorando
                // maiúsculas/minúsculas.
                Collections.sort(minhasListas, Comparator.comparing(Lista::getNome, String.CASE_INSENSITIVE_ORDER));

                // Lê a opção do utilizador e converte-a imediatamente para minúsculas.
                opcao = visaoLista.mostrarListas(minhasListas, usuarioLogado.getNome()).toLowerCase();

                // Agora, todas as comparações são feitas com a versão em minúsculas.
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
                opcao = "r"; // Força a saída em caso de erro grave.
            }
        } while (!opcao.equals("r")); // A comparação é feita com 'r' minúsculo.
    }

    /**
     * Menu para ver os detalhes de uma lista específica e geri-la.
     * 
     * @param lista         A lista selecionada.
     * @param usuarioLogado O utilizador dono da lista.
     */
    private void menuDetalhesLista(Lista lista, Usuario usuarioLogado) {
        String opcao;
        do {
            // A CORREÇÃO ESTÁ AQUI: Adicionamos .trim() para remover espaços
            // e garantimos que a conversão para minúsculas acontece sempre.
            opcao = visaoLista.mostrarDetalhesLista(lista, usuarioLogado.getNome(), true).trim().toLowerCase();

            switch (opcao) {
                case "1":
                    GerenciarProdutoLista(lista, usuarioLogado);
                    break;
                case "2":
                    alterarLista(lista);
                    break;
                case "3":
                    if (excluirLista(lista)) {
                        return; // Sai imediatamente do método se a lista for excluída
                    }
                    break;
                case "r":
                    // Não faz nada, apenas permite que a condição do loop termine a execução
                    break;
                default:
                    visaoUsuario.mostrarMensagem("\nOpção inválida!");
                    visaoUsuario.pausa();
                    break;
            }

        } while (!opcao.equals("r")); // A condição de saída do loop está correta
    }


    /**
     * Fluxo para procurar uma lista pública usando o seu código.
     */
public void menuProcurarLista() {
    String codigo = visaoLista.pedirCodigo();
    try {
        Lista lista = crudLista.readByCodigo(codigo);
        if (lista != null) {
            // Buscar o usuário dono da lista pelo ID
            Usuario dono = crudUsuario.readById(lista.getIdUsuario());

            // Nome do dono (se não existir, mostrar "Desconhecido")
            String nomeDono = (dono != null) ? dono.getNome() : "Desconhecido";

            // Exibir os detalhes da lista com o nome do dono
            visaoLista.mostrarDetalhesLista(lista, nomeDono, false);
        } else {
            visaoUsuario.mostrarMensagem("\nNenhuma lista encontrada com o código \"" + codigo + "\".");
        }
    } catch (Exception e) {
        visaoUsuario.mostrarMensagem("\nERRO ao procurar a lista: " + e.getMessage());
    }
    visaoUsuario.pausa();
}


// Novo método: menu de detalhes do produto dentro da lista
private void menuDetalhesProdutoLista(ListaProduto lp, Lista lista, Usuario usuarioLogado) {
    try {
        CRUDProduto crudProduto = new CRUDProduto();
        CRUDListaProduto crudListaProduto = new CRUDListaProduto();

        Produto produto = crudProduto.read(lp.getIdProduto());
        if (produto == null) {
            System.out.println("\nERRO: Produto não encontrado!");
            visaoUsuario.pausa();
            return;
        }

        String opcao;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("\nPresenteFácil 2.0");
            System.out.println("-----------------");
            System.out.println("> Início > Minhas listas > " + lista.getNome() + " > Produtos > " + produto.getNome());
            System.out.println();
            System.out.println("NOME.......: " + produto.getNome());
            System.out.println("GTIN-13....: " + produto.getGtin());
            System.out.println("DESCRIÇÃO..: " + produto.getDescricao());
            System.out.println("QUANTIDADE.: " + lp.getQuantidade());
            System.out.println("OBSERVAÇÕES: " + (lp.getObservacoes() == null || lp.getObservacoes().isEmpty() ? "(nenhuma)" : lp.getObservacoes()));
            System.out.println();
            System.out.println("(1) Alterar a quantidade");
            System.out.println("(2) Alterar as observações");
            System.out.println("(3) Remover o produto desta lista");
            System.out.println();
            System.out.println("(R) Retornar ao menu anterior");
            System.out.print("\nOpção: ");

            opcao = sc.nextLine().trim().toLowerCase();

            switch (opcao) {

                // =====================================================
                // (1) Alterar a quantidade
                // =====================================================
                case "1":
                    System.out.print("\nNova quantidade: ");
                    int novaQtd = Integer.parseInt(sc.nextLine());

                    if (novaQtd <= 0) {
                        System.out.println("Erro: quantidade deve ser maior que zero!");
                        visaoUsuario.pausa();
                        break;
                    }

                    lp.setQuantidade(novaQtd);
                    crudListaProduto.update(lp);
                    System.out.println("\nQuantidade atualizada com sucesso!");
                    visaoUsuario.pausa();
                    break;

                // =====================================================
                // (2) Alterar observações
                // =====================================================
                case "2":
                    System.out.print("\nNova observação (deixe em branco para remover): ");
                    String novaObs = sc.nextLine().trim();

                    if (novaObs.isEmpty()) {
                        lp.setObservacoes(null);
                    } else {
                        lp.setObservacoes(novaObs);
                    }

                    crudListaProduto.update(lp);
                    System.out.println("\nObservações atualizadas com sucesso!");
                    visaoUsuario.pausa();
                    break;

                // =====================================================
                // (3) Remover produto da lista
                // =====================================================
                case "3":
                    System.out.print("\nTem certeza que deseja remover este produto da lista? (s/n): ");
                    String confirm = sc.nextLine().trim().toLowerCase();

                    if (confirm.equals("s")) {
                        if (crudListaProduto.delete(lp.getID())) {
                            System.out.println("\nProduto removido com sucesso!");
                            visaoUsuario.pausa();
                            return; // Sai do menu após remover
                        } else {
                            System.out.println("\nErro ao remover produto da lista.");
                        }
                    } else {
                        System.out.println("\nOperação cancelada.");
                    }
                    visaoUsuario.pausa();
                    break;

                // =====================================================
                // (R) Retornar
                // =====================================================
                case "r":
                    break;

                default:
                    System.out.println("\nOpção inválida!");
                    visaoUsuario.pausa();
                    break;
            }
        } while (!opcao.equals("r"));

    } catch (Exception e) {
        System.err.println("\nERRO ao exibir detalhes do produto: " + e.getMessage());
        e.printStackTrace();
        visaoUsuario.pausa();
    }
}


    // ------------------------------------------ Métodos de Ação (CRUD)
    // ------------------------------------------

    /**
     * Realiza o processo de criação de uma nova lista.
     * 
     * @param usuarioLogado O utilizador que está a criar a lista.
     */
    private void criarNovaLista(Usuario usuarioLogado) {
        try {
            Lista novaLista = visaoLista.lerDadosNovaLista();
            novaLista.setIdUsuario(usuarioLogado.getID());

            int id = crudLista.create(novaLista);
            visaoUsuario
                    .mostrarMensagem("\nLista \"" + novaLista.getNome() + "\" criada com sucesso! (ID: " + id + ")");
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao criar a lista: " + e.getMessage());
        }
        visaoUsuario.pausa();
    }

    /**
     * Menu para gerir os produtos dentro de uma lista específica.
     * Exibe os produtos cadastrados e permite adicionar novos.
     * 
     * @param lista A lista cujos produtos serão geridos.
     */
private void GerenciarProdutoLista(Lista lista, Usuario usuarioLogado) {
    try {
        String opcao;
        CRUDListaProduto crudListaProduto = new CRUDListaProduto();

        do {
            System.out.println("\nPresenteFácil 2.0");
            System.out.println("-----------------");
            System.out.println("> Início > Minhas listas > " + lista.getNome() + " > Produtos\n");

            // Exibe produtos numerados e guarda em memória
            ArrayList<ListaProduto> produtos = crudListaProduto.listarTodosListaProdutos(lista.getID(), crudProduto);

            System.out.println("\n(A) Acrescentar produto");
            System.out.println("(R) Retornar ao menu anterior");
            System.out.print("\nOpção: ");
            opcao = new Scanner(System.in).nextLine().trim().toLowerCase();

            // Se o usuário digitar um número (selecionar produto)
            if (opcao.matches("\\d+")) {
                int numeroEscolhido = Integer.parseInt(opcao);
                if (numeroEscolhido >= 1 && numeroEscolhido <= produtos.size()) {
                    ListaProduto lpSelecionado = produtos.get(numeroEscolhido - 1);
                    menuDetalhesProdutoLista(lpSelecionado, lista, usuarioLogado);
                } else if (numeroEscolhido == 0) {
                    opcao = "r";
                } else {
                    System.out.println("\nNúmero inválido!");
                    visaoUsuario.pausa();
                }

            } else {
                switch (opcao) {
                    case "a":
                        menuAcrescentarProduto(lista, crudListaProduto);
                        break;

                    case "r":
                        break;

                    default:
                        System.out.println("\nOpção inválida!");
                        visaoUsuario.pausa();
                        break;
                }
            }

        } while (!opcao.equals("r"));

    } catch (Exception e) {
        System.err.println("\nERRO ao gerenciar produtos: " + e.getMessage());
        e.printStackTrace();
        visaoUsuario.pausa();
    }
}
private void menuAcrescentarProduto(Lista lista, CRUDListaProduto crudListaProduto) throws Exception {
    String opcao;
    do {
        System.out.println("\n> Início > Minhas listas > " + lista.getNome() + " > Produtos > Adicionar\n");
        System.out.println("(1) Buscar produto por GTIN");
        System.out.println("(2) Listar todos os produtos para adicionar");
        System.out.println("\n(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
        opcao = new Scanner(System.in).nextLine().trim().toLowerCase();

        switch (opcao) {
            case "1":
                buscarProdutoPorGTIN(lista, crudListaProduto);
                break;

            case "2":
                listarTodosProdutosParaAdicionar(lista, crudListaProduto);
                break;

            case "r":
                break;

            default:
                System.out.println("\nOpção inválida!");
                visaoUsuario.pausa();
                break;
        }

    } while (!opcao.equals("r"));
}
private void buscarProdutoPorGTIN(Lista lista, CRUDListaProduto crudListaProduto) throws Exception {
    System.out.print("\nDigite o GTIN do produto: ");
    String gtin = new Scanner(System.in).nextLine().trim();

    Produto produto = crudProduto.readByGtin(gtin);
    if (produto == null) {
        System.out.println("\nProduto não encontrado!");
        visaoUsuario.pausa();
        return;
    }

    // Verifica se já está na lista
    ListaProduto existente = crudListaProduto.findAssociacao(lista.getID(), produto.getID());
    if (existente != null) {
        System.out.println("\nEsse produto já está na lista!");
        visaoUsuario.pausa();
        return;
    }

    int qtd = lerQuantidadeProduto();
    String obs = lerObservacaoProduto();

    ListaProduto novaAssoc = new ListaProduto(-1, lista.getID(), produto.getID(), qtd, obs);
    crudListaProduto.create(novaAssoc);

    System.out.println("\nProduto adicionado com sucesso!");
    visaoUsuario.pausa();
}

private void listarTodosProdutosParaAdicionar(Lista lista, CRUDListaProduto crudListaProduto) throws Exception {
    // Pega todos os produtos
    ArrayList<Produto> todos = crudProduto.readAll();

    // Cria uma lista apenas com produtos ativos
    ArrayList<Produto> ativos = new ArrayList<>();
    for (Produto p : todos) {
        if (p.isAtivo()) {
            ativos.add(p);
        }
    }

    if (ativos.isEmpty()) {
        System.out.println("\nNenhum produto ativo disponível!");
        visaoUsuario.pausa();
        return;
    }

    // Exibe produtos ativos numerados de 1 até n
    System.out.println("\n===== Lista de produtos disponíveis =====");
    for (int i = 0; i < ativos.size(); i++) {
        System.out.println("(" + (i + 1) + ") " + ativos.get(i).getNome());
    }

    // Lê a opção do usuário
    System.out.print("\nDigite o número do produto que deseja adicionar (ou 0 para voltar): ");
    int opc = Integer.parseInt(new Scanner(System.in).nextLine().trim());

    if (opc == 0) return;

    // Verifica se a opção é válida dentro dos produtos ativos
    if (opc < 1 || opc > ativos.size()) {
        System.out.println("\nNúmero inválido!");
        visaoUsuario.pausa();
        return;
    }

    // Seleciona o produto correto da lista de ativos
    Produto produto = ativos.get(opc - 1);

    // Verifica duplicidade
    ListaProduto existente = crudListaProduto.findAssociacao(lista.getID(), produto.getID());
    if (existente != null) {
        System.out.println("\nEsse produto já está na lista!");
        visaoUsuario.pausa();
        return;
    }

    // Lê quantidade e observação
    int qtd = lerQuantidadeProduto();
    String obs = lerObservacaoProduto();

    // Cria a nova associação e adiciona
    ListaProduto novaAssoc = new ListaProduto(-1, lista.getID(), produto.getID(), qtd, obs);
    crudListaProduto.create(novaAssoc);

    System.out.println("\nProduto adicionado com sucesso!");
    visaoUsuario.pausa();
}


private Produto escolherProduto(CRUDProduto crudProduto) throws Exception {
    ArrayList<Produto> produtos = crudProduto.readAllAtivos();

    if (produtos.isEmpty()) {
        System.out.println("\nNão há produtos cadastrados.");
        return null;
    }

    System.out.println("\nProdutos disponíveis:\n");
    for (int i = 0; i < produtos.size(); i++) {
        Produto p = produtos.get(i);
        System.out.printf("(%d) %s - GTIN: %s%n", i + 1, p.getNome(), p.getGtin());
    }

    System.out.print("\n(R) Retornar ao menu anterior\nOpção: ");
    String input = new Scanner(System.in).nextLine().trim();

    // Se o usuário quiser retornar
    if (input.equalsIgnoreCase("R")) {
        return null;
    }

    // Verifica se o input é número
    if (input.matches("\\d+")) {
        int opcao = Integer.parseInt(input);

        if (opcao >= 1 && opcao <= produtos.size()) {
            Produto selecionado = produtos.get(opcao - 1);

            // Mostrar detalhes do produto antes de retornar
            System.out.println("\nProduto selecionado:");
            System.out.println("Nome: " + selecionado.getNome());
            System.out.println("GTIN: " + selecionado.getGtin());
            System.out.println("Descrição: " + selecionado.getDescricao());

            return selecionado;
        } else {
            System.out.println("\nErro: número inválido! Escolha um número entre 1 e " + produtos.size() + ".");
            return null;
        }
    }

    System.out.println("\nErro: opção inválida!");
    return null;
}

    private int lerQuantidadeProduto() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nInforme a quantidade: ");
        return sc.nextInt();
    }

    private String lerObservacaoProduto() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Observações (opcional): ");
        return sc.nextLine();
    }

    /**
     * Realiza o processo de alteração de uma lista existente.
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
     * Realiza o processo de exclusão de uma lista.
     * 
     * @param lista A lista a ser excluída.
     * @return `true` se a lista foi excluída com sucesso, `false` caso contrário.
     */
    private boolean excluirLista(Lista lista) {
        if (visaoLista.confirmarExclusao(lista.getNome())) {
            try {
                if (crudLista.delete(lista.getID())) {
                    visaoUsuario.mostrarMensagem("\nLista \"" + lista.getNome() + "\" excluída com sucesso.");
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
     * Fecha as ligações com os ficheiros de dados geridos por este controlador.
     */
    public void close() throws Exception {
        // Fecha os CRUDs que foram abertos aqui
        if (crudListaProduto != null) {
            crudListaProduto.close();
        }
        if (crudProduto != null) {
            crudProduto.close();
        }

        // Fecha o CRUD de listas (já existente)
        crudLista.close();
    }

}

package view;

import java.util.List;
import java.util.Scanner;
import model.Lista;
import model.Produto;

/**
 * Responsável por toda a interação com o utilizador (entrada e saída)
 * relacionada à entidade {@link Produto}.
 * Inclui menus para listagem, cadastro, busca e visualização de detalhes.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class VisaoProduto {

    /**
     * Objeto Scanner para ler a entrada do utilizador a partir do console.
     */
    private Scanner teclado;

    /**
     * Construtor da VisaoProduto.
     * Inicializa o {@link Scanner} para ler a entrada do utilizador (System.in).
     */
    public VisaoProduto() {
        this.teclado = new Scanner(System.in);
    }

    /**
     * Apresenta o menu principal de gestão de produtos.
     * Oferece opções para Buscar (1), Listar (2), Cadastrar (3) ou Retornar (R).
     *
     * @return A {@code String} contendo a opção digitada pelo utilizador
     * (ex: "1", "2", "r").
     */
    public String menuPrincipalProdutos() {
        System.out.println("\n-----------------");
        System.out.println("> Início > Produtos");
        System.out.println("\n(1) Buscar produtos por GTIN");
        System.out.println("(2) Listar todos os produtos");
        System.out.println("(3) Cadastrar um novo produto");
        System.out.println("\n(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
        return teclado.nextLine().toLowerCase();
    }

    /**
     * Exibe uma lista paginada de produtos.
     * Mostra os produtos da página atual e os controlos de navegação
     * (Próxima 'P', Anterior 'A', Retornar 'R').
     *
     * @param produtos A {@code List<Produto>} contendo os itens da página
     * atual.
     * @param paginaAtual O número {@code int} da página atual.
     * @param totalPaginas O número {@code int} total de páginas.
     * @return A {@code String} com a opção do utilizador (número do produto,
     * 'p', 'a', 'r').
     */
    public String mostrarListagemPaginada(List<Produto> produtos, int paginaAtual, int totalPaginas) {
        System.out.println("\n-----------------");
        System.out.println("> Início > Produtos > Listagem");
        System.out.println("\nPágina " + paginaAtual + " de " + totalPaginas + "\n");

        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado nesta página.");
        } else {
            for (int i = 0; i < produtos.size(); i++) {
                Produto p = produtos.get(i);
                int numeroOpcao = (i + 1) % 10;
                String situacao = p.isAtivo() ? "" : " (INATIVADO)";
                System.out.println("(" + numeroOpcao + ") " + p.getNome() + situacao);
            }
        }

        System.out.println();
        if (paginaAtual > 1) {
            System.out.println("(A) Página anterior");
        }
        if (paginaAtual < totalPaginas) {
            System.out.println("(P) Próxima página");
        }
        System.out.println("\n(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");

        return teclado.nextLine().toLowerCase();
    }

    /**
     * Exibe os prompts para o cadastro de um novo produto e lê os dados
     * (GTIN, Nome, Descrição).
     * O produto é pré-configurado como 'ativo'.
     *
     * @return Um novo objeto {@link Produto} preenchido com os dados
     * inseridos (ID -1, ativo=true).
     */
    public Produto lerDadosNovoProduto() {
        System.out.println("\n--- Cadastro de Novo Produto ---");
        System.out.print("GTIN (código de barras): ");
        String gtin = teclado.nextLine();
        System.out.print("Nome do produto: ");
        String nome = teclado.nextLine();
        System.out.print("Descrição: ");
        String descricao = teclado.nextLine();

        return new Produto(-1, gtin, nome, descricao, true);
    }

    /**
     * Solicita ao utilizador que digite um GTIN para realizar uma busca.
     *
     * @return A {@code String} contendo o GTIN digitado.
     */
    public String lerGtinBusca() {
        System.out.print("\nDigite o GTIN do produto que deseja procurar: ");
        return teclado.nextLine();
    }

    /**
     * Exibe os detalhes de um produto específico, incluindo informações
     * de consulta cruzada (em quais listas ele aparece).
     * Apresenta o menu de ações (Alterar, Inativar/Reativar).
     *
     * @param produto O {@link Produto} cujos detalhes serão exibidos.
     * @param listasDoUsuario Uma {@code List<Lista>} das listas do usuário
     * atual que contêm este produto.
     * @param countOutrasListas Um {@code int} com a contagem de listas de
     * *outros* usuários que contêm este produto.
     * @return A {@code String} com a opção do menu de ação (ex: "1", "2",
     * "r").
     */
    public String mostrarDetalhesProduto(Produto produto, List<Lista> listasDoUsuario, int countOutrasListas) {
        System.out.println("\n-----------------");
        System.out.println("> Início > Produtos > Listagem > " + produto.getNome());

        System.out.printf("\n%-11s: %s", "NOME", produto.getNome());
        System.out.printf("\n%-11s: %s", "GTIN-13", produto.getGtin());
        System.out.printf("\n%-11s: %s\n", "DESCRIÇÃO", produto.getDescricao());

        System.out.println("\nAparece nas minhas listas:");
        if (listasDoUsuario.isEmpty()) {
            System.out.println("- Nenhuma");
        } else {
            for (Lista l : listasDoUsuario) {
                System.out.println("- " + l.getNome());
            }
        }

        if (countOutrasListas > 0) {
            System.out.println("\nAparece também em mais " + countOutrasListas + " lista(s) de outras pessoas.");
        }

        System.out.println("\n(1) Alterar os dados do produto");
        if (produto.isAtivo()) {
            System.out.println("(2) Inativar o produto");
        } else {
            System.out.println("(2) Reativar o produto");
        }
        System.out.println("\n(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
        return teclado.nextLine().toLowerCase();
    }

    /**
     * Exibe os prompts para a alteração de um produto existente (Nome,
     * Descrição).
     * Mostra os valores atuais e permite ao utilizador mantê-los
     * (pressionando Enter).
     *
     * @param produtoAtual O objeto {@link Produto} original, usado para exibir
     * os dados atuais.
     * @return Um novo objeto {@link Produto} (com o mesmo ID, GTIN e status
     * 'ativo') contendo os dados atualizados.
     */
    public Produto lerDadosAlteracao(Produto produtoAtual) {
        System.out.println("\n--- Alterar Produto (deixe em branco para manter) ---");

        System.out.print("Novo Nome (" + produtoAtual.getNome() + "): ");
        String nome = teclado.nextLine();
        if (nome.isEmpty())
            nome = produtoAtual.getNome();

        System.out.print("Nova Descrição (" + produtoAtual.getDescricao() + "): ");
        String descricao = teclado.nextLine();
        if (descricao.isEmpty())
            descricao = produtoAtual.getDescricao();

        return new Produto(produtoAtual.getID(), produtoAtual.getGtin(), nome, descricao, produtoAtual.isAtivo());
    }

    /**
     * Solicita uma confirmação genérica (S/N) ao utilizador antes de
     * executar uma ação.
     *
     * @param acao A {@code String} descrevendo a ação (ex: "inativar",
     * "reativar").
     * @param nomeProduto O {@code String} com o nome do produto (para o
     * prompt).
     * @return {@code true} se o utilizador digitar "s" (ignorando
     * maiúsculas/minúsculas), {@code false} caso contrário.
     */
    public boolean confirmarAcao(String acao, String nomeProduto) {
        System.out.print(
                "\nTem a certeza que deseja " + acao + " o produto \"" + nomeProduto + "\"? (S/N): ");
        return teclado.nextLine().equalsIgnoreCase("s");
    }
}
package view;

import java.util.List;
import java.util.Scanner;
import model.Lista;
import model.Produto;

/**
 * Responsável por toda a interação com o utilizador (entrada e saída)
 * relacionada à entidade {@link Produto}.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class VisaoProduto {

    private Scanner teclado;

    /**
     * Construtor da VisaoProduto.
     * Inicializa o {@link Scanner} para ler a entrada do utilizador,
     * configurado para a codificação UTF-8 para suportar acentuação.
     */
    public VisaoProduto() {
        try {
            this.teclado = new Scanner(System.in, "UTF-8");
        } catch(Exception e) {
            System.err.println("Aviso: Codificação UTF-8 não suportada.");
            this.teclado = new Scanner(System.in);
        }
    }

    /**
     * Apresenta o menu principal de gestão de produtos.
     * @return A {@code String} contendo a opção digitada pelo utilizador.
     */
    public String menuPrincipalProdutos() {
        System.out.println("\n-----------------");
        System.out.println("> Início > Produtos");
        System.out.println("\n(1) Buscar produtos por GTIN");
        System.out.println("(2) Listar todos os produtos");
        System.out.println("(3) Cadastrar um novo produto");
        System.out.println("\n(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
        return teclado.nextLine().toLowerCase().trim();
    }

    /**
     * Exibe uma lista paginada de produtos.
     * @param produtos A {@code List<Produto>} contendo os itens da página atual.
     * @param paginaAtual O número {@code int} da página atual.
     * @param totalPaginas O número {@code int} total de páginas.
     * @return A {@code String} com a opção do utilizador.
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
                System.out.println("(" + (i + 1) + ") " + p.getNome());
            }
        }
        System.out.println();
        if (paginaAtual > 1) System.out.println("(A) Página anterior");
        if (paginaAtual < totalPaginas) System.out.println("(P) Próxima página");
        System.out.println("\n(R) Retornar ao menu anterior");
        System.out.print("\nDigite o número do produto para ver detalhes, ou uma opção de navegação: ");
        return teclado.nextLine().toLowerCase().trim();
    }

    /**
     * Exibe os prompts para o cadastro de um novo produto e lê os dados.
     * @return Um novo objeto {@link Produto} preenchido com os dados inseridos.
     */
    public Produto lerDadosNovoProduto() {
        System.out.println("\n--- Cadastro de Novo Produto ---");
        System.out.print("GTIN (código de barras): ");
        String gtin = teclado.nextLine().trim();
        System.out.print("Nome do produto: ");
        String nome = teclado.nextLine().trim();
        System.out.print("Descrição: ");
        String descricao = teclado.nextLine().trim();
        return new Produto(-1, gtin, nome, descricao, true);
    }

    /**
     * Solicita ao utilizador que digite um GTIN para realizar uma busca.
     * @return A {@code String} contendo o GTIN digitado.
     */
    public String lerGtinBusca() {
        System.out.print("\nDigite o GTIN do produto que deseja procurar: ");
        return teclado.nextLine().trim();
    }

    /**
     * Exibe os detalhes de um produto específico, incluindo a consulta cruzada.
     * @param produto O {@link Produto} cujos detalhes serão exibidos.
     * @param listasDoUsuario Uma {@code List<Lista>} das listas do usuário atual.
     * @param countOutrasListas Um {@code int} com a contagem de listas de outros usuários.
     * @return A {@code String} com a opção do menu de ação.
     */
    public String mostrarDetalhesProduto(Produto produto, List<Lista> listasDoUsuario, int countOutrasListas) {
        System.out.println("\n-----------------");
        System.out.println("> Início > Produtos > " + produto.getNome());
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
        return teclado.nextLine().toLowerCase().trim();
    }

    /**
     * Exibe os prompts para a alteração de um produto existente.
     * @param produtoAtual O objeto {@link Produto} original.
     * @return Um novo objeto {@link Produto} contendo os dados atualizados.
     */
    public Produto lerDadosAlteracao(Produto produtoAtual) {
        System.out.println("\n--- Alterar Produto (deixe em branco para manter) ---");
        System.out.print("Novo Nome (" + produtoAtual.getNome() + "): ");
        String nome = teclado.nextLine();
        if (nome.isEmpty()) nome = produtoAtual.getNome();
        System.out.print("Nova Descrição (" + produtoAtual.getDescricao() + "): ");
        String descricao = teclado.nextLine();
        if (descricao.isEmpty()) descricao = produtoAtual.getDescricao();
        return new Produto(produtoAtual.getID(), produtoAtual.getGtin(), nome, descricao, produtoAtual.isAtivo());
    }

    /**
     * Solicita uma confirmação genérica (S/N) ao utilizador.
     * @param acao A {@code String} descrevendo a ação (ex: "inativar").
     * @param nomeProduto O {@code String} com o nome do produto.
     * @return {@code true} se o utilizador digitar "s", {@code false} caso contrário.
     */
    public boolean confirmarAcao(String acao, String nomeProduto) {
        System.out.print("\nTem a certeza que deseja " + acao + " o produto \"" + nomeProduto + "\"? (S/N): ");
        return teclado.nextLine().equalsIgnoreCase("s");
    }

    /**
     * Solicita uma confirmação de alto risco, avisando sobre a remoção em cascata.
     * @param nomeProduto O nome do produto.
     * @param numListas O número de listas das quais o produto será removido.
     * @return true se o utilizador confirmar (S/s), false caso contrário.
     */
    public boolean confirmarInativacaoEmUso(String nomeProduto, int numListas) {
        System.out.println("\nATENÇÃO: O produto \"" + nomeProduto + "\" está atualmente em " + numListas + " lista(s).");
        System.out.print("Inativá-lo irá removê-lo de TODAS estas listas. Deseja continuar? (S/N): ");
        return teclado.nextLine().equalsIgnoreCase("s");
    }
}
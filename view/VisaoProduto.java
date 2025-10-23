package view;

import java.util.List;
import java.util.Scanner;
import model.Produto;

/**
 * A classe 'VisaoProduto' é responsável por toda a interação com o utilizador
 * relacionada à gestão de produtos.
 * Inclui menus para listagem, cadastro, busca e visualização de detalhes de
 * produtos.
 */
public class VisaoProduto {

    private Scanner teclado;

    public VisaoProduto() {
        this.teclado = new Scanner(System.in);
    }

    /**
     * Apresenta o menu principal de gestão de produtos e lê a opção do
     * utilizador.
     * 
     * @return A opção escolhida pelo utilizador.
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
     * Exibe uma "fatia" paginada da lista de produtos.
     * Mostra os produtos da página atual e controlos de navegação.
     * 
     * @param produtos     A lista de produtos a ser exibida na página atual.
     * @param paginaAtual  O número da página atual.
     * @param totalPaginas O número total de páginas.
     * @return A opção do utilizador (um número de produto, 'p' para próxima, 'a'
     *         para anterior, 'r' para retornar).
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
     * Lê os dados de um novo produto a ser cadastrado.
     * 
     * @return Um objeto Produto preenchido com os dados inseridos.
     */
    public Produto lerDadosNovoProduto() {
        System.out.println("\n--- Cadastro de Novo Produto ---");
        System.out.print("GTIN (código de barras): ");
        String gtin = teclado.nextLine();
        System.out.print("Nome do produto: ");
        String nome = teclado.nextLine();
        System.out.print("Descrição: ");
        String descricao = teclado.nextLine();

        // Um novo produto é sempre criado como 'ativo'
        return new Produto(-1, gtin, nome, descricao, true);
    }

    /**
     * Pede ao utilizador que digite um GTIN para busca.
     * 
     * @return O GTIN inserido pelo utilizador.
     */
    public String lerGtinBusca() {
        System.out.print("\nDigite o GTIN do produto que deseja procurar: ");
        return teclado.nextLine();
    }

    /**
     * Mostra os detalhes de um produto e o menu de ações.
     * 
     * @param produto O produto cujos detalhes serão exibidos.
     * @return A opção de ação escolhida pelo utilizador.
     */
    public String mostrarDetalhesProduto(Produto produto) {
        System.out.println("\n-----------------");
        System.out.println("> Início > Produtos > Listagem > " + produto.getNome());

        System.out.printf("\n%-11s: %s", "NOME", produto.getNome());
        System.out.printf("\n%-11s: %s", "GTIN-13", produto.getGtin());
        System.out.printf("\n%-11s: %s\n", "DESCRIÇÃO", produto.getDescricao());

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
     * Lê os novos dados para a alteração de um produto existente.
     * 
     * @param produtoAtual O produto antes da alteração.
     * @return Um objeto Produto com os dados atualizados.
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

        // GTIN e estado de ativação não são alterados aqui.
        return new Produto(produtoAtual.getID(), produtoAtual.getGtin(), nome, descricao, produtoAtual.isAtivo());
    }

    /**
     * Pede uma confirmação genérica ao utilizador.
     * 
     * @param acao        A ação a ser confirmada (ex: "inativar", "reativar").
     * @param nomeProduto O nome do produto em questão.
     * @return true se o utilizador confirmar (S/s), false caso contrário.
     */
    public boolean confirmarAcao(String acao, String nomeProduto) {
        System.out.print(
                "\nTem a certeza que deseja " + acao + " o produto \"" + nomeProduto + "\"? (S/N): ");
        return teclado.nextLine().equalsIgnoreCase("s");
    }
}

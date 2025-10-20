package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import model.Lista;
import model.ListaProduto;
import model.Produto;

/**
 * Responsável por toda a interação com o utilizador (entrada e saída)
 * relacionada à entidade {@link Lista} e à gestão dos produtos associados
 * a ela ({@link ListaProduto}).
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.5
 */
public class VisaoLista {

    private Scanner teclado;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Construtor da VisaoLista.
     * Inicializa o {@link Scanner} para ler a entrada do utilizador (System.in),
     * tentando usar a codificação "UTF-8" para suportar acentuação.
     */
    public VisaoLista() {
        try {
            this.teclado = new Scanner(System.in, "UTF-8");
        } catch (Exception e) {
            System.err.println("Aviso: A codificação UTF-8 não é suportada. Acentos podem não funcionar.");
            this.teclado = new Scanner(System.in);
        }
    }

    // --- MÉTODOS DE VISUALIZAÇÃO E MENUS ---

    /**
     * Exibe o menu principal "Minhas Listas".
     * Mostra todas as listas de um utilizador e oferece opções para
     * selecionar uma (pelo número), criar uma nova (N) ou retornar (R).
     *
     * @param listas A {@code List<Lista>} pertencente ao utilizador.
     * @param nomeUsuario O nome do utilizador logado (para saudação).
     * @return A {@code String} contendo a opção digitada pelo utilizador
     * (ex: "1", "n", "r").
     */
    public String mostrarListas(List<Lista> listas, String nomeUsuario) {
        System.out.println("\n-----------------");
        System.out.println("> Início > Minhas Listas");
        System.out.println("\nListas de " + nomeUsuario + ":");

        if (listas.isEmpty()) {
            System.out.println("Nenhuma lista encontrada.");
        } else {
            for (int i = 0; i < listas.size(); i++) {
                Lista l = listas.get(i);
                String dataLimiteStr = l.getDataLimite() != null ? " - " + dtf.format(l.getDataLimite()) : "";
                System.out.println("(" + (i + 1) + ") " + l.getNome() + dataLimiteStr);
            }
        }

        System.out.println("\n(N) Nova lista");
        System.out.println("(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
        return teclado.nextLine();
    }

    /**
     * Exibe os detalhes completos de uma {@link Lista} específica.
     * Se {@code mostrarMenuGestao} for verdadeiro, exibe também as opções
     * de gestão (gerir produtos, alterar, excluir).
     *
     * @param lista A {@link Lista} cujos detalhes serão exibidos.
     * @param nomeProprietario O nome do proprietário (ou "Pública") para
     * contextualização.
     * @param mostrarMenuGestao {@code true} para exibir o menu de gestão,
     * {@code false} para apenas visualizar.
     * @return A {@code String} com a opção do utilizador (se o menu for
     * exibido), ou uma string vazia caso contrário.
     */
    public String mostrarDetalhesLista(Lista lista, String nomeProprietario, boolean mostrarMenuGestao) {
        System.out.println("\n-----------------");
        System.out.println("> Início > Minhas Listas > " + lista.getNome());

        System.out.println("\nProprietário: " + nomeProprietario);

        System.out.println("\nCÓDIGO: " + lista.getCodigoCompartilhavel());
        System.out.println("NOME: " + lista.getNome());
        System.out.println("DESCRIÇÃO: " + lista.getDescricao());
        System.out.println("DATA DE CRIAÇÃO: " + dtf.format(lista.getDataCriacao()));
        String dataLimiteStr = lista.getDataLimite() != null ? dtf.format(lista.getDataLimite()) : "Não definida";
        System.out.println("DATA LIMITE: " + dataLimiteStr);

        if (mostrarMenuGestao) {
            System.out.println("\n(1) Gerir produtos da lista");
            System.out.println("(2) Alterar dados da lista");
            System.out.println("(3) Excluir lista");
            System.out.println("\n(R) Retornar ao menu anterior");
            System.out.print("\nOpção: ");
            return teclado.nextLine().toLowerCase();
        }
        return "";
    }

    /**
     * Exibe o menu de gestão de produtos dentro de uma lista específica.
     * Lista os produtos atuais e oferece opções para selecionar um (pelo
     * número), acrescentar um novo (A) ou retornar (R).
     *
     * @param produtosNaLista A {@code List<Pair<Produto, ListaProduto>>}
     * contendo os produtos a serem exibidos.
     * @param nomeLista O nome da lista (para o cabeçalho do menu).
     * @return A {@code String} com a opção digitada pelo utilizador.
     */
    public String menuGerenciarProdutosDaLista(List<Pair<Produto, ListaProduto>> produtosNaLista, String nomeLista) {
        System.out.println("\n-----------------");
        System.out.println("> Início > Minhas Listas > " + nomeLista + " > Produtos");
        
        if (produtosNaLista.isEmpty()) {
            System.out.println("\nNenhum produto nesta lista ainda.");
        } else {
            for (int i = 0; i < produtosNaLista.size(); i++) {
                Produto p = produtosNaLista.get(i).first;
                ListaProduto lp = produtosNaLista.get(i).second;
                System.out.println("(" + (i + 1) + ") " + p.getNome() + " (x" + lp.getQuantidade() + ")");
            }
        }
        
        System.out.println("\n(A) Acrescentar produto");
        System.out.println("(R) Retornar ao menu anterior");
        System.out.print("\nDigite o número do produto para gerir, ou uma opção: ");
        return teclado.nextLine().toLowerCase().trim();
    }

    /**
     * Exibe o submenu para escolher o método de adição de produto à lista
     * (ex: por GTIN, por listagem).
     *
     * @return A {@code String} com a opção digitada pelo utilizador.
     */
    public String menuAcrescentarProduto() {
        System.out.println("\n-----------------");
        System.out.println("> ... > Produtos > Acrescentar produto");
        System.out.println("\n(1) Procurar produtos por GTIN");
        System.out.println("(2) Listar todos os produtos");
        System.out.println("\n(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
        return teclado.nextLine().toLowerCase().trim();
    }

    /**
     * Exibe os detalhes de um produto específico *dentro* de uma lista,
     * incluindo sua quantidade e observações.
     * Mostra o menu para gerir este item (alterar qtd, alterar obs, remover).
     *
     * @param produto O objeto {@link Produto} (para nome, gtin, descrição).
     * @param listaProduto O objeto {@link ListaProduto} (para qtd, obs).
     * @return A {@code String} com a opção digitada pelo utilizador.
     */
    public String mostrarDetalhesProdutoNaLista(Produto produto, ListaProduto listaProduto) {
        System.out.println("\n-----------------");
        System.out.println("> ... > Produtos > " + produto.getNome());
        System.out.println("\nNOME.......: " + produto.getNome());
        System.out.println("GTIN-13....: " + produto.getGtin());
        System.out.println("DESCRIÇÃO..: " + produto.getDescricao());
        System.out.println("QUANTIDADE.: " + listaProduto.getQuantidade());
        System.out.println("OBSERVAÇÕES: " + (listaProduto.getObservacoes().isEmpty() ? "Nenhuma" : listaProduto.getObservacoes()));
        
        System.out.println("\n(1) Alterar a quantidade");
        System.out.println("(2) Alterar as observações");
        System.out.println("(3) Remover o produto desta lista");
        System.out.println("\n(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
        return teclado.nextLine().toLowerCase().trim();
    }

    // --- MÉTODOS DE LEITURA DE DADOS ---

    /**
     * Solicita ao utilizador que digite o código de partilha de uma lista.
     *
     * @return A {@code String} contendo o código digitado, após remoção de
     * espaços (trim).
     */
    public String pedirCodigo() {
        System.out.print("\nDigite o código da lista que deseja procurar: ");
        return teclado.nextLine().trim();
    }

    /**
     * Exibe os prompts para a criação de uma nova lista e lê os dados
     * (Nome, Descrição, Data Limite).
     * A data de criação é definida como {@code LocalDate.now()}.
     *
     * @return Um novo objeto {@link Lista} preenchido com os dados
     * inseridos (IDs são -1, dataCriação é now()).
     */
    public Lista lerDadosNovaLista() {
        System.out.println("\n--- Nova Lista de Presentes ---");
        System.out.print("Nome da lista: ");
        String nome = teclado.nextLine();
        System.out.print("Descrição detalhada: ");
        String descricao = teclado.nextLine();
        LocalDate dataLimite = lerDataOpcional("Data limite (dd/mm/aaaa, opcional): ");
        return new Lista(-1, -1, nome, descricao, LocalDate.now(), dataLimite, "");
    }

    /**
     * Exibe os prompts para a alteração de uma lista existente.
     * Mostra os valores atuais e permite ao utilizador mantê-los
     * (pressionando Enter) ou inserir novos.
     *
     * @param listaAtual O objeto {@link Lista} original, usado para exibir
     * os dados atuais.
     * @return Um novo objeto {@link Lista} (com IDs -1) contendo os dados
     * atualizados.
     */
    public Lista lerDadosAlteracaoLista(Lista listaAtual) {
        System.out.println("\n--- Alterar Lista (deixe em branco para manter o valor atual) ---");
        
        System.out.print("Novo Nome (" + listaAtual.getNome() + "): ");
        String nome = teclado.nextLine();
        if (nome.isEmpty()) nome = listaAtual.getNome();

        System.out.print("Nova Descrição (" + listaAtual.getDescricao() + "): ");
        String descricao = teclado.nextLine();
        if (descricao.isEmpty()) descricao = listaAtual.getDescricao();

        String promptData = listaAtual.getDataLimite() != null ? dtf.format(listaAtual.getDataLimite()) : "Não definida";
        LocalDate dataLimite = lerDataOpcionalComManutencao("Nova Data Limite (" + promptData + "): ", listaAtual.getDataLimite());

        return new Lista(-1, -1, nome, descricao, null, dataLimite, "");
    }
    
    /**
     * Solicita ao utilizador que digite o GTIN-13 de um produto.
     *
     * @return A {@code String} contendo o GTIN digitado, após remoção de
     * espaços (trim).
     */
    public String pedirGtinParaAdicionar() {
        System.out.print("\nDigite o GTIN-13 do produto a adicionar: ");
        return teclado.nextLine().trim();
    }

    /**
     * Solicita ao utilizador uma nova quantidade para um item da lista.
     * Valida a entrada para garantir que seja um número inteiro positivo.
     *
     * @param atual A quantidade atual (para exibir no prompt).
     * @return O {@code int} contendo a nova quantidade válida.
     */
    public int pedirNovaQuantidade(int atual) {
        while (true) {
            System.out.print("Nova quantidade (atual: " + atual + "): ");
            try {
                int novaQtd = Integer.parseInt(teclado.nextLine().trim());
                if (novaQtd > 0) return novaQtd;
                else System.out.println("ERRO: A quantidade deve ser maior que zero.");
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Digite um número válido.");
            }
        }
    }

    /**
     * Solicita ao utilizador novas observações para um item da lista.
     *
     * @param atual As observações atuais (para exibir no prompt).
     * @return A {@code String} contendo as novas observações.
     */
    public String pedirNovasObservacoes(String atual) {
        System.out.print("Novas observações (atual: \"" + atual + "\"): ");
        return teclado.nextLine();
    }

    // --- MÉTODOS DE CONFIRMAÇÃO ---

    /**
     * Solicita confirmação (S/N) do utilizador antes de excluir uma lista.
     *
     * @param nomeLista O nome da lista (para exibir no prompt de confirmação).
     * @return {@code true} se o utilizador digitar "s" (ignorando
     * maiúsculas/minúsculas), {@code false} caso contrário.
     */
    public boolean confirmarExclusao(String nomeLista) {
        System.out.print("\nTem a certeza que deseja excluir a lista \"" + nomeLista + "\"? (S/N): ");
        return teclado.nextLine().equalsIgnoreCase("s");
    }
    
    /**
     * Solicita confirmação (S/N) do utilizador antes de remover um produto
     * de uma lista.
     *
     * @param nomeProduto O nome do produto (para exibir no prompt).
     * @return {@code true} se o utilizador digitar "s" (ignorando
     * maiúsculas/minúsculas), {@code false} caso contrário.
     */
    public boolean confirmarRemocaoProduto(String nomeProduto) {
        System.out.print("\nTem a certeza que deseja remover \"" + nomeProduto + "\" desta lista? (S/N): ");
        return teclado.nextLine().trim().equalsIgnoreCase("s");
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * Método auxiliar para ler uma data opcional (dd/MM/yyyy) do utilizador.
     * Continua a pedir até que o formato seja válido ou a entrada seja vazia.
     *
     * @param prompt A mensagem a ser exibida ao utilizador.
     * @return O {@link LocalDate} lido, ou {@code null} se a entrada for
     * vazia.
     */
    private LocalDate lerDataOpcional(String prompt) {
        while (true) {
            System.out.print(prompt);
            String dataStr = teclado.nextLine();
            if (dataStr.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(dataStr, dtf);
            } catch (DateTimeParseException e) {
                System.out.println("ERRO: Formato de data inválido! Use dd/mm/aaaa.");
            }
        }
    }

    /**
     * Método auxiliar para ler uma data opcional durante uma alteração.
     * Se o utilizador pressionar Enter (entrada vazia), retorna o
     * {@code valorAntigo}.
     * Se o formato for inválido, também retorna o {@code valorAntigo}.
     *
     * @param prompt A mensagem a ser exibida ao utilizador.
     * @param valorAntigo O {@link LocalDate} a ser retornado se a entrada
     * for vazia.
     * @return O {@link LocalDate} lido (novo) ou o {@code valorAntigo}.
     */
    private LocalDate lerDataOpcionalComManutencao(String prompt, LocalDate valorAntigo) {
        System.out.print(prompt);
        String dataStr = teclado.nextLine();
        if (dataStr.isEmpty()) {
            return valorAntigo;
        }
        try {
            return LocalDate.parse(dataStr, dtf);
        } catch (DateTimeParseException e) {
            System.out.println("ERRO: Formato inválido! A data não será alterada.");
            return valorAntigo;
        }
    }
}
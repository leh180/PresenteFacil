package view;

import java.util.Scanner;
import model.Usuario;

/**
 * Responsável por toda a interação com o utilizador (entrada e saída)
 * relacionada à entidade {@link Usuario}.
 * Inclui menus de login, cadastro, gerenciamento de perfil e métodos
 * utilitários de exibição de mensagens.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.5
 */
public class VisaoUsuario {

    private Scanner teclado;

    /**
     * Construtor da VisaoUsuario.
     * Inicializa o {@link Scanner} para ler a entrada do utilizador (System.in).
     */
    public VisaoUsuario() {
        this.teclado = new Scanner(System.in);
    }

    // --- MÉTODOS DE EXIBIÇÃO DE MENSAGENS ---

    /**
     * Exibe uma mensagem de texto simples no console.
     *
     * @param msg A {@code String} a ser exibida.
     */
    public void mostrarMensagem(String msg) {
        System.out.println(msg);
    }

    // --- MÉTODOS DE MENU E LEITURA DE DADOS ---

    /**
     * Exibe o menu inicial da aplicação (antes do login).
     * Oferece opções de Login (1), Novo usuário (2) ou Sair (S).
     *
     * @return A {@code String} contendo a opção digitada pelo utilizador
     * (ex: "1", "2", "s").
     */
    public String menuInicial() {
        System.out.println("\n-----------------");
        System.out.println("PresenteFácil 2.0");
        System.out.println("-----------------");
        System.out.println("\n(1) Login");
        System.out.println("(2) Novo usuário");
        System.out.println("\n(S) Sair");
        System.out.print("\nOpção: ");
        return teclado.nextLine().toLowerCase();
    }
    
    /**
     * Exibe o cabeçalho do menu de Login e chama o método de leitura de
     * credenciais.
     *
     * @return Um array de {@code String[2]} onde [0] é o e-mail e [1] é a senha.
     */
    public String[] menuLogin() {
        System.out.println("\n--- Login ---");
        return pedirLogin();
    }
    
    /**
     * Exibe o cabeçalho do menu de Cadastro e chama o método de leitura
     * dos dados do novo usuário.
     *
     * @return Um novo objeto {@link Usuario} preenchido com os dados
     * inseridos (ID -1).
     */
    public Usuario menuCadastro() {
        System.out.println("\n--- Cadastro de Novo Usuário ---");
        return lerDadosNovoUsuario();
    }

    /**
     * Solicita e lê o e-mail e a senha do utilizador.
     *
     * @return Um array de {@code String[2]} onde [0] é o e-mail e [1] é a senha.
     */
    public String[] pedirLogin() {
        String[] dados = new String[2];
        System.out.print("\nE-mail: ");
        dados[0] = teclado.nextLine();
        System.out.print("Senha: ");
        dados[1] = teclado.nextLine();
        return dados;
    }

    /**
     * Exibe os prompts para o cadastro de um novo usuário e lê todos os
     * dados necessários (Nome, E-mail, Senha, Pergunta, Resposta).
     *
     * @return Um novo objeto {@link Usuario} preenchido com os dados
     * inseridos (ID -1).
     */
    public Usuario lerDadosNovoUsuario() {
        System.out.print("Nome: ");
        String nome = teclado.nextLine();
        System.out.print("E-mail: ");
        String email = teclado.nextLine();
        System.out.print("Senha: ");
        String senha = teclado.nextLine();
        System.out.print("Pergunta Secreta: ");
        String pergunta = teclado.nextLine();
        System.out.print("Resposta Secreta: ");
        String resposta = teclado.nextLine();

        return new Usuario(-1, nome, email, senha, pergunta, resposta);
    }
    
    /**
     * Exibe o menu de gerenciamento de perfil do usuário ("Meus Dados").
     * Mostra os dados atuais e oferece opções para Alterar (1), Excluir (2)
     * ou Retornar (R).
     *
     * @param usuario O {@link Usuario} logado, cujos dados serão exibidos.
     * @return A {@code String} contendo a opção digitada pelo utilizador.
     */
    public String menuMeusDados(Usuario usuario) {
        System.out.println("\n-----------------");
        System.out.println("> Início > Meus Dados");
        System.out.println("\nDados Atuais:");
        System.out.println(usuario.toString());
        System.out.println("\n(1) Alterar os meus dados");
        System.out.println("(2) Excluir a minha conta");
        System.out.println("\n(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
        return teclado.nextLine().toLowerCase();
    }
    
    /**
     * Exibe os prompts para a alteração dos dados de um usuário (Nome, E-mail).
     * Mostra os valores atuais e permite ao utilizador mantê-los
     * (pressionando Enter).
     *
     * @param usuarioAtual O objeto {@link Usuario} original, usado para exibir
     * os dados atuais.
     * @return Um novo objeto {@link Usuario} (com o mesmo ID) contendo os dados
     * atualizados. Senha, pergunta e resposta são passadas como vazias.
     */
    public Usuario lerDadosAlteracaoUsuario(Usuario usuarioAtual) {
        System.out.println("\n--- Alteração de Dados (deixe em branco para manter o valor atual) ---");
        
        System.out.print("Novo Nome (" + usuarioAtual.getNome() + "): ");
        String nome = teclado.nextLine();
        if (nome.isEmpty()) nome = usuarioAtual.getNome();

        System.out.print("Novo E-mail (" + usuarioAtual.getEmail() + "): ");
        String email = teclado.nextLine();
        if (email.isEmpty()) email = usuarioAtual.getEmail();
        
        // Retorna um usuário com os dados novos, mas sem alterar senha/pergunta.
        return new Usuario(usuarioAtual.getID(), nome, email, "", "", "");
    }
    
    /**
     * Solicita um novo nome ao utilizador, mostrando o nome atual.
     * Se a entrada for vazia, retorna o nome atual.
     *
     * @param nomeAtual O nome (String) atual.
     * @return A {@code String} com o novo nome, ou o nome atual se a
     * entrada for vazia.
     */
    public String pedirNovoNome(String nomeAtual) {
        System.out.print("Novo Nome (" + nomeAtual + "): ");
        String nome = teclado.nextLine();
        return nome.isEmpty() ? nomeAtual : nome;
    }

    /**
     * Solicita confirmação (S/N) do utilizador antes de excluir a
     * própria conta.
     *
     * @param nomeUsuario O nome do usuário (para exibir no prompt de
     * confirmação).
     * @return {@code true} se o utilizador digitar "s" (ignorando
     * maiúsculas/minúsculas), {@code false} caso contrário.
     */
    public boolean confirmarExclusao(String nomeUsuario) {
        System.out.print("\nATENÇÃO! Tem a certeza que deseja excluir permanentemente a sua conta \"" + nomeUsuario + "\"? (S/N): ");
        return teclado.nextLine().equalsIgnoreCase("s");
    }

    /**
     * Pausa a execução do console e aguarda que o utilizador
     * pressione Enter para continuar.
     */
    public void pausa() {
        System.out.print("\nPressione Enter para continuar...");
        teclado.nextLine();
    }
}
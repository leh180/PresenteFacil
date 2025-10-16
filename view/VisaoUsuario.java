package view;

import java.util.Scanner;
import model.Usuario;

public class VisaoUsuario {

    private Scanner teclado;

    public VisaoUsuario() {
        this.teclado = new Scanner(System.in);
    }

    // --- MÉTODOS DE EXIBIÇÃO DE MENSAGENS ---

    public void mostrarMensagem(String msg) {
        System.out.println(msg);
    }

    // --- MÉTODOS DE MENU E LEITURA DE DADOS ---

    public String menuInicial() {
        System.out.println("\n-----------------");
        System.out.println("PresenteFácil 1.0");
        System.out.println("-----------------");
        System.out.println("\n(1) Login");
        System.out.println("(2) Novo usuário");
        System.out.println("\n(S) Sair");
        System.out.print("\nOpção: ");
        return teclado.nextLine().toLowerCase();
    }
    
    public String[] menuLogin() {
        System.out.println("\n--- Login ---");
        return pedirLogin();
    }
    
    public Usuario menuCadastro() {
        System.out.println("\n--- Cadastro de Novo Usuário ---");
        return lerDadosNovoUsuario();
    }

    public String[] pedirLogin() {
        String[] dados = new String[2];
        System.out.print("\nE-mail: ");
        dados[0] = teclado.nextLine();
        System.out.print("Senha: ");
        dados[1] = teclado.nextLine();
        return dados;
    }

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
    
    public Usuario lerDadosAlteracaoUsuario(Usuario usuarioAtual) {
        System.out.println("\n--- Alteração de Dados (deixe em branco para manter o valor atual) ---");
        
        System.out.print("Novo Nome (" + usuarioAtual.getNome() + "): ");
        String nome = teclado.nextLine();
        if (nome.isEmpty()) nome = usuarioAtual.getNome();

        System.out.print("Novo E-mail (" + usuarioAtual.getEmail() + "): ");
        String email = teclado.nextLine();
        if (email.isEmpty()) email = usuarioAtual.getEmail();
        
        return new Usuario(usuarioAtual.getID(), nome, email, "", "", "");
    }
    
    public String pedirNovoNome(String nomeAtual) {
        System.out.print("Novo Nome (" + nomeAtual + "): ");
        String nome = teclado.nextLine();
        return nome.isEmpty() ? nomeAtual : nome;
    }

    public boolean confirmarExclusao(String nomeUsuario) {
        System.out.print("\nATENÇÃO! Tem a certeza que deseja excluir permanentemente a sua conta \"" + nomeUsuario + "\"? (S/N): ");
        return teclado.nextLine().equalsIgnoreCase("s");
    }

    public void pausa() {
        System.out.print("\nPressione Enter para continuar...");
        teclado.nextLine();
    }
}


package controller;
import java.util.*;
import model.CRUDLista;
import model.CRUDUsuario;
import model.Usuario;
import view.VisaoUsuario;

/**
 * A classe 'ControlePrincipal' é o ponto de entrada da aplicação.
 * Ela é responsável por orquestrar o fluxo principal do sistema,
 * gerindo o login, a criação de utilizadores e o acesso aos menus
 * de funcionalidades após a autenticação.
*/
public class ControlePrincipal {

    // ------------------------------------------ Atributos da Classe ------------------------------------------

    private VisaoUsuario visaoUsuario;
    private ControleUsuario controleUsuario;
    private ControleLista controleLista;
    private Usuario usuarioLogado;
    private Scanner teclado;

    // ------------------------------------------ Construtor ------------------------------------------

    public ControlePrincipal() throws Exception {
        this.visaoUsuario = new VisaoUsuario();
        
        // As instâncias de CRUD são criadas UMA SÓ VEZ aqui
        CRUDUsuario crudUsuario = new CRUDUsuario();
        CRUDLista crudLista = new CRUDLista();
        
        // E são injetadas nos outros controladores
        this.controleUsuario = new ControleUsuario(crudUsuario, crudLista);
        this.controleLista = new ControleLista(crudLista);
        
        this.usuarioLogado = null;
        this.teclado = new Scanner(System.in);
    }

    // ------------------------------------------ Métodos de Menu ------------------------------------------

    /**
     * Inicia o loop principal da aplicação para utilizadores não autenticados.
     * Apresenta o menu inicial e gere as ações de login, cadastro ou saída.
     * @throws Exception se ocorrer um erro de I/O nos ficheiros.
     */
    public void iniciar() throws Exception {
        String opcao;
        do {
            opcao = visaoUsuario.menuInicial();
            switch(opcao) {
                case "1":
                    usuarioLogado = controleUsuario.login();
                    if(usuarioLogado != null) {
                        menuLogado();
                    } else {
                        visaoUsuario.pausa();
                    }
                    break;
                case "2":
                    controleUsuario.criarNovoUsuario();
                    visaoUsuario.pausa();
                    break;
                case "s":
                    visaoUsuario.mostrarMensagem("Até breve!");
                    break;
                default:
                    visaoUsuario.mostrarMensagem("Opção inválida!");
                    visaoUsuario.pausa();
                    break;
            }
        } while (!opcao.equals("s"));
        
        controleUsuario.close();
        controleLista.close();
        teclado.close();
    }

    /**
     * Gere o menu principal para um utilizador que está autenticado.
     * Este método é chamado após um login bem-sucedido e continua em loop
     * até que o utilizador faça logout ou apague a sua conta.
     * @throws Exception se ocorrer um erro de I/O nos ficheiros.
     */
    private void menuLogado() throws Exception {
        String opcao;
        
        do {
            System.out.println("\n-----------------");
            System.out.println("> Início");
            System.out.println("\nBem-vindo(a), " + usuarioLogado.getNome() + "!");
            System.out.println("\n(1) Meus dados");
            System.out.println("(2) Minhas listas");
            System.out.println("(3) Produtos (Não implementado no TP1)");
            System.out.println("(4) Procurar lista por código");
            System.out.println("\n(S) Sair (Logout)");
            System.out.print("\nOpção: ");
            
            opcao = teclado.nextLine().toLowerCase();
            boolean contaExcluida = false;

            switch (opcao) {
                case "1":
                    if (controleUsuario.menuMeusDados(usuarioLogado)) {
                        contaExcluida = true;
                    }
                    break;
                case "2":
                    controleLista.menuMinhasListas(usuarioLogado);
                    break;
                case "3":
                    visaoUsuario.mostrarMensagem("Esta funcionalidade será implementada no próximo trabalho prático.");
                    visaoUsuario.pausa();
                    break;
                case "4":
                    controleLista.menuProcurarLista();
                    break;
                case "s":
                    usuarioLogado = null;
                    visaoUsuario.mostrarMensagem("Logout efetuado.");
                    break;
                default:
                    visaoUsuario.mostrarMensagem("Opção inválida!");
                    visaoUsuario.pausa();
                    break;
            }
            if(contaExcluida) {
                usuarioLogado = null;
                break;
            }

        } while (usuarioLogado != null && !opcao.equals("s"));
    }
}


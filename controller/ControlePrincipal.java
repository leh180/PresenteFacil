package controller;

import java.util.Scanner;
import model.CRUDLista;
import model.CRUDListaProduto;
import model.CRUDProduto;
import model.CRUDUsuario;
import model.Usuario;
import view.VisaoUsuario;

/**
 * Ponto de entrada e orquestrador principal da aplicação. Esta classe gerencia o
 * ciclo de vida da aplicação, os menus principais (inicial e logado) e a
 * inicialização e finalização de todos os outros controladores e serviços de
 * acesso a dados (CRUDs).
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.5
 */
public class ControlePrincipal {

    // --- Atributos ---
    private VisaoUsuario visaoUsuario;
    private ControleUsuario controleUsuario;
    private ControleLista controleLista;
    private ControleProduto controleProduto;
    private Usuario usuarioLogado;
    
    // --- Instâncias de CRUD (Centralizadas) ---
    private CRUDUsuario crudUsuario;
    private CRUDLista crudLista;
    private CRUDProduto crudProduto;
    private CRUDListaProduto crudListaProduto;

    /**
     * Construtor da classe ControlePrincipal.
     * Inicializa de forma centralizada todas as camadas da aplicação,
     * incluindo as classes de acesso a dados (CRUD), os controladores de
     * sub-módulos e as classes de visão. Este método utiliza injeção de
     * dependência para fornecer aos controladores as instâncias de CRUD
     * necessárias.
     *
     * @throws Exception se ocorrer um erro na inicialização de qualquer um dos
     * componentes CRUD (por exemplo, erro ao acessar os
     * arquivos de banco de dados).
     */
    public ControlePrincipal() throws Exception {
        this.crudUsuario = new CRUDUsuario();
        this.crudLista = new CRUDLista();
        this.crudProduto = new CRUDProduto();
        this.crudListaProduto = new CRUDListaProduto();
        
        this.visaoUsuario = new VisaoUsuario();
        this.controleUsuario = new ControleUsuario(crudUsuario, crudLista);
        this.controleLista = new ControleLista(crudLista, crudProduto, crudListaProduto, crudUsuario);
        this.controleProduto = new ControleProduto(crudProduto, crudLista, crudListaProduto);
        
        this.usuarioLogado = null;
    }

    /**
     * Inicia e gerencia o loop principal da aplicação para usuários não
     * autenticados. Exibe o menu inicial com opções de login, cadastro ou
     * saída. Ao final da execução (quando o usuário escolhe sair), este método
     * é responsável por fechar todas as conexões e recursos abertos, como
     * arquivos de banco de dados e o Scanner.
     *
     * @throws Exception se ocorrer um erro durante o fechamento dos recursos CRUD.
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
        
        crudUsuario.close();
        crudLista.close();
        crudProduto.close();
        crudListaProduto.close();
    }

    /**
     * Gerencia o menu principal para um usuário que está autenticado. Exibe as
     * opções disponíveis para o usuário logado, como gerenciamento de dados
     * pessoais, listas de compras e produtos. O loop continua até que o usuário
     * faça logout ou exclua sua conta.
     *
     * @throws Exception pode ser lançada pelos métodos dos sub-controladores
     * chamados dentro do menu.
     */
    private void menuLogado() throws Exception {
        String opcao;
        Scanner teclado = new Scanner(System.in); // Scanner local para este menu
        
        do {
            System.out.println("\n-----------------");
            System.out.println("> Início");
            System.out.println("\nBem-vindo(a), " + usuarioLogado.getNome() + "!");
            System.out.println("\n(1) Meus dados");
            System.out.println("(2) Minhas listas");
            System.out.println("(3) Produtos");
            System.out.println("(4) Procurar lista por código");
            System.out.println("\n(S) Sair (Logout)");
            System.out.print("\nOpção: ");
            
            opcao = teclado.nextLine().toLowerCase().trim();
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
                    controleProduto.menuPrincipal(usuarioLogado);
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
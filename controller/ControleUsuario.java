package controller;

import model.CRUDLista;
import model.CRUDUsuario;
import model.Usuario;
import view.VisaoUsuario;

/**
 * Gerencia toda a lógica de negócio relacionada aos usuários, como autenticação,
 * cadastro e gerenciamento de perfil. Atua como mediador entre a camada de
 * persistência de dados (CRUD) e a camada de interface com o usuário (Visao).
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.0
 */
public class ControleUsuario {

    private CRUDUsuario crudUsuario;
    private CRUDLista crudLista;
    private VisaoUsuario visaoUsuario;

    /**
     * Construtor padrão que inicializa suas próprias instâncias de CRUD.
     *
     * @throws Exception se ocorrer um erro na inicialização dos componentes CRUD.
     */
    public ControleUsuario() throws Exception {
        this.crudUsuario = new CRUDUsuario();
        this.crudLista = new CRUDLista();
        this.visaoUsuario = new VisaoUsuario();
    }

    /**
     * Construtor que implementa o padrão de Injeção de Dependência.
     * Recebe instâncias de CRUD já inicializadas por um controlador principal.
     *
     * @param crudUsuario Instância de CRUDUsuario para operações com usuários.
     * @param crudLista Instância de CRUDLista para verificar listas do usuário.
     */
    public ControleUsuario(CRUDUsuario crudUsuario, CRUDLista crudLista) {
        this.crudUsuario = crudUsuario;
        this.crudLista = crudLista;
        this.visaoUsuario = new VisaoUsuario();
    }

    /**
     * Gerencia o processo de login de um usuário. Solicita e-mail e senha,
     * busca o usuário no banco de dados e valida as credenciais.
     *
     * @return O objeto {@code Usuario} se o login for bem-sucedido; caso contrário,
     * retorna {@code null}.
     */
    public Usuario login() {
        String[] dados = visaoUsuario.menuLogin();
        String email = dados[0];
        String senha = dados[1];

        try {
            Usuario u = crudUsuario.readByEmail(email);
            if (u != null && u.validarSenha(senha)) {
                visaoUsuario.mostrarMensagem("Login bem-sucedido!");
                return u;
            } else {
                visaoUsuario.mostrarMensagem("E-mail ou senha inválidos.");
                return null;
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("ERRO ao tentar fazer login: " + e.getMessage());
            return null;
        }
    }

    /**
     * Orquestra o processo de criação de um novo usuário. Solicita os dados,
     * verifica se o e-mail já está em uso e, se não estiver, realiza o cadastro.
     */
    public void criarNovoUsuario() {
        try {
            Usuario novoUsuario = visaoUsuario.menuCadastro();
            if (crudUsuario.readByEmail(novoUsuario.getEmail()) != null) {
                visaoUsuario.mostrarMensagem("ERRO: O e-mail \"" + novoUsuario.getEmail() + "\" já está em uso.");
            } else {
                int id = crudUsuario.create(novoUsuario);
                visaoUsuario.mostrarMensagem(
                        "Utilizador \"" + novoUsuario.getNome() + "\" criado com sucesso! (ID: " + id + ")");
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("ERRO ao criar o utilizador: " + e.getMessage());
        }
    }

    /**
     * Ponto de entrada e loop principal para o menu de gerenciamento de perfil do
     * usuário. Oferece opções para alterar dados ou excluir a própria conta.
     *
     * @param usuarioLogado O usuário que está com a sessão ativa.
     * @return {@code true} se a conta do usuário foi excluída durante a execução
     * deste menu, {@code false} caso contrário.
     */
    public boolean menuMeusDados(Usuario usuarioLogado) {
        String opcao;
        do {
            opcao = visaoUsuario.menuMeusDados(usuarioLogado);

            switch (opcao) {
                case "1":
                    alterarMeusDados(usuarioLogado);
                    break;
                case "2":
                    if (excluirMinhaConta(usuarioLogado)) {
                        usuarioLogado = null;
                        return true;
                    }
                    break;
                case "r":
                    break;
                default:
                    visaoUsuario.mostrarMensagem("Opção inválida!");
                    break;
            }

        } while (!opcao.equalsIgnoreCase("r"));

        return false;
    }

    /**
     * Gerencia o processo de alteração dos dados cadastrais (nome e e-mail) de um
     * usuário.
     *
     * @param usuario O objeto do usuário a ser alterado.
     */
    private void alterarMeusDados(Usuario usuario) {
        try {
            Usuario dadosAlterados = visaoUsuario.lerDadosAlteracaoUsuario(usuario);
            usuario.setNome(dadosAlterados.getNome());
            usuario.setEmail(dadosAlterados.getEmail());

            if (crudUsuario.update(usuario)) {
                visaoUsuario.mostrarMensagem("Dados alterados com sucesso!");
            } else {
                visaoUsuario.mostrarMensagem("Falha ao alterar os dados.");
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("ERRO ao alterar os dados: " + e.getMessage());
        }
        visaoUsuario.pausa();
    }

    /**
     * Gerencia o processo de exclusão da conta de um usuário. Aplica a regra de
     * negócio que impede a exclusão se o usuário possuir listas associadas.
     *
     * @param usuario O objeto do usuário a ser excluído.
     * @return {@code true} se a conta foi excluída com sucesso, {@code false} caso
     * contrário.
     */
    private boolean excluirMinhaConta(Usuario usuario) {
        try {
            if (crudLista.readAllByUser(usuario.getID()).size() > 0) {
                visaoUsuario.mostrarMensagem(
                        "ERRO: Não é possível excluir a sua conta porque existem listas de presentes associadas a ela.");
                visaoUsuario.pausa();
                return false;
            }

            if (visaoUsuario.confirmarExclusao(usuario.getNome())) {
                if (crudUsuario.delete(usuario.getID())) {
                    visaoUsuario.mostrarMensagem("Conta excluída com sucesso.");
                    visaoUsuario.pausa();
                    return true;
                } else {
                    visaoUsuario.mostrarMensagem("Falha ao excluir a conta.");
                }
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("ERRO ao excluir a conta: " + e.getMessage());
        }
        visaoUsuario.pausa();
        return false;
    }

    /**
     * Fecha as conexões com os arquivos de dados gerenciados por este controlador.
     *
     * @throws Exception se ocorrer um erro ao fechar o recurso.
     */
    public void close() throws Exception {
        crudUsuario.close();
    }
}
package controller;

import model.*;
import view.VisaoUsuario;

/**
 * A classe 'ControleUsuario' é responsável por gerenciar toda a lógica de
 * negócio
 * relacionada aos usuários, como autenticação, cadastro e gerenciamento de
 * perfil.
 * Ela atua como um mediador entre as classes de persistência (CRUD) e a
 * interface
 * com o usuário (VisaoUsuario).
 */
public class ControleUsuario {

    // ------------------------------------------ Atributos da Classe ------------------------------------------

    private CRUDUsuario crudUsuario;
    private CRUDLista crudLista; // Necessário para verificar se o utilizador tem listas
    private VisaoUsuario visaoUsuario;

    // ------------------------------------------ Construtores ------------------------------------------

    public ControleUsuario() throws Exception {
        this.crudUsuario = new CRUDUsuario();
        this.crudLista = new CRUDLista();
        this.visaoUsuario = new VisaoUsuario();
    }

    public ControleUsuario(CRUDUsuario crudUsuario, CRUDLista crudLista) {
        this.crudUsuario = crudUsuario;
        this.crudLista = crudLista;
        this.visaoUsuario = new VisaoUsuario();
    }

    // ------------------------------------------ Métodos de Autenticação e Cadastro ------------------------------------------

    /**
     * Gerencia o processo de login de um utilizador.
     * Este método solicita o e-mail e a senha ao usuário, busca o usuário
     * no sistema e valida as credenciais.
     * @return O objeto Usuario se o login for bem-sucedido, caso contrário, null.
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
     * Gerencia o processo de criação de um novo utilizador.
     * Solicita os dados do novo usuário e verifica se o e-mail já está em uso
     * antes de tentar salvar no sistema.
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

    // ------------------------------------------ Métodos de Gerenciamento de Perfil ------------------------------------------

    /**
     * Menu para um utilizador gerenciar os seus próprios dados.
     * Este método é o ponto de entrada para a gestão do perfil,
     * oferecendo opções para alterar dados ou excluir a conta.
     * @param usuarioLogado O utilizador que está com a sessão ativa.
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
     * Gerencia o processo de alteração dos dados de um usuário.
     * Pede os novos dados e atualiza o objeto no sistema.
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
     * Gerencia o processo de exclusão de uma conta de usuário.
     * Antes de excluir, verifica se há listas de presentes associadas.
     * Se não houver, pede uma confirmação final e realiza a exclusão.
     * @param usuario O objeto do usuário a ser excluído.
     * @return `true` se a conta foi excluída com sucesso, `false` caso contrário.
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
     * Fecha as ligações com os ficheiros de dados geridos por este controlador.
     */
    public void close() throws Exception {
        crudUsuario.close();
    }
}

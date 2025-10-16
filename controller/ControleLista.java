package controller;

import java.util.*;
import model.*;
import view.*;

/**
 * A classe 'ControleLista' é responsável por gerir toda a lógica de negócio
 * relacionada às listas, atuando como o intermediário entre as classes de modelo (dados)
 * e as classes de visão (interface com o utilizador).
 */
public class ControleLista {

    // ------------------------------------------ Atributos da Classe ------------------------------------------
    
    private CRUDLista crudLista;
    private VisaoLista visaoLista;
    private VisaoUsuario visaoUsuario;

    // ------------------------------------------ Construtor ------------------------------------------
    
    /**
     * Construtor que recebe a instância de CRUDLista (Injeção de Dependência).
     * Garante que toda a aplicação partilhe a mesma ligação com os ficheiros.
     * @param crudLista A instância de CRUD para as listas.
     */
    public ControleLista(CRUDLista crudLista) {
        this.crudLista = crudLista;
        this.visaoLista = new VisaoLista();
        this.visaoUsuario = new VisaoUsuario();
    }

    // ------------------------------------------ Métodos de Menu ------------------------------------------
    
    /**
     * Menu principal para um utilizador gerir as suas próprias listas.
     * Este método é o ponto de entrada para a gestão de listas pessoais.
     * Ele lê todas as listas do utilizador, exibe-as de forma ordenada e
     * direciona para as ações de criação ou gestão de uma lista específica.
     * @param usuarioLogado O utilizador que está com a sessão ativa.
     */
    public void menuMinhasListas(Usuario usuarioLogado) {
        String opcao;
        do {
            try {
                List<Lista> minhasListas = crudLista.readAllByUser(usuarioLogado.getID());
                // Ordena as listas por ordem alfabética do nome, ignorando maiúsculas/minúsculas.
                Collections.sort(minhasListas, Comparator.comparing(Lista::getNome, String.CASE_INSENSITIVE_ORDER));
                
                // Lê a opção do utilizador e converte-a imediatamente para minúsculas.
                opcao = visaoLista.mostrarListas(minhasListas, usuarioLogado.getNome()).toLowerCase();
                
                // Agora, todas as comparações são feitas com a versão em minúsculas.
                if (opcao.equals("n")) {
                    criarNovaLista(usuarioLogado);
                } else if (!opcao.equals("r")) {
                    try {
                        int indice = Integer.parseInt(opcao) - 1;
                        if (indice >= 0 && indice < minhasListas.size()) {
                            menuDetalhesLista(minhasListas.get(indice), usuarioLogado);
                        } else {
                            visaoUsuario.mostrarMensagem("\nERRO: Opção numérica inválida!");
                            visaoUsuario.pausa();
                        }
                    } catch (NumberFormatException e) {
                        visaoUsuario.mostrarMensagem("\nERRO: Opção inválida! Tente novamente.");
                        visaoUsuario.pausa();
                    }
                }
            } catch (Exception e) {
                visaoUsuario.mostrarMensagem("\nERRO ao gerir as listas: " + e.getMessage());
                e.printStackTrace();
                opcao = "r"; // Força a saída em caso de erro grave.
            }
        } while (!opcao.equals("r")); // A comparação é feita com 'r' minúsculo.
    }

    /**
     * Menu para ver os detalhes de uma lista específica e geri-la.
     * @param lista A lista selecionada.
     * @param usuarioLogado O utilizador dono da lista.
     */
    private void menuDetalhesLista(Lista lista, Usuario usuarioLogado) {
        String opcao;
        do {
            // A CORREÇÃO ESTÁ AQUI: Adicionamos .trim() para remover espaços
            // e garantimos que a conversão para minúsculas acontece sempre.
            opcao = visaoLista.mostrarDetalhesLista(lista, usuarioLogado.getNome(), true).trim().toLowerCase();
            
            switch (opcao) {
                case "1":
                    visaoUsuario.mostrarMensagem("\nFuncionalidade a ser implementada no Trabalho Prático 2.");
                    visaoUsuario.pausa();
                    break;
                case "2":
                    alterarLista(lista);
                    break;
                case "3":
                    if (excluirLista(lista)) {
                        return; // Sai imediatamente do método se a lista for excluída
                    }
                    break;
                case "r":
                    // Não faz nada, apenas permite que a condição do loop termine a execução
                    break;
                default:
                    visaoUsuario.mostrarMensagem("\nOpção inválida!");
                    visaoUsuario.pausa();
                    break;
            }

        } while (!opcao.equals("r")); // A condição de saída do loop está correta
    }

    /**
     * Fluxo para procurar uma lista pública usando o seu código.
     */
    public void menuProcurarLista() {
        String codigo = visaoLista.pedirCodigo();
        try {
            Lista lista = crudLista.readByCodigo(codigo);
            if (lista != null) {
                visaoLista.mostrarDetalhesLista(lista, "Não revelado", false);
            } else {
                visaoUsuario.mostrarMensagem("\nNenhuma lista encontrada com o código \"" + codigo + "\".");
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao procurar a lista: " + e.getMessage());
        }
        visaoUsuario.pausa();
    }

    // ------------------------------------------ Métodos de Ação (CRUD) ------------------------------------------

    /**
     * Realiza o processo de criação de uma nova lista.
     * @param usuarioLogado O utilizador que está a criar a lista.
     */
    private void criarNovaLista(Usuario usuarioLogado) {
        try {
            Lista novaLista = visaoLista.lerDadosNovaLista();
            novaLista.setIdUsuario(usuarioLogado.getID());
            
            int id = crudLista.create(novaLista);
            visaoUsuario.mostrarMensagem("\nLista \"" + novaLista.getNome() + "\" criada com sucesso! (ID: " + id + ")");
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao criar a lista: " + e.getMessage());
        }
        visaoUsuario.pausa();
    }

    /**
     * Realiza o processo de alteração de uma lista existente.
     * @param lista A lista a ser alterada.
     */
    private void alterarLista(Lista lista) {
        try {
            Lista dadosAlterados = visaoLista.lerDadosAlteracaoLista(lista);
            
            lista.setNome(dadosAlterados.getNome());
            lista.setDescricao(dadosAlterados.getDescricao());
            lista.setDataLimite(dadosAlterados.getDataLimite());

            if (crudLista.update(lista)) {
                visaoUsuario.mostrarMensagem("\nLista alterada com sucesso!");
            } else {
                visaoUsuario.mostrarMensagem("\nFalha ao alterar a lista.");
            }
        } catch (Exception e) {
            visaoUsuario.mostrarMensagem("\nERRO ao alterar a lista: " + e.getMessage());
        }
        visaoUsuario.pausa();
    }

    /**
     * Realiza o processo de exclusão de uma lista.
     * @param lista A lista a ser excluída.
     * @return `true` se a lista foi excluída com sucesso, `false` caso contrário.
     */
    private boolean excluirLista(Lista lista) {
        if (visaoLista.confirmarExclusao(lista.getNome())) {
            try {
                if (crudLista.delete(lista.getID())) {
                    visaoUsuario.mostrarMensagem("\nLista \"" + lista.getNome() + "\" excluída com sucesso.");
                    visaoUsuario.pausa();
                    return true;
                } else {
                    visaoUsuario.mostrarMensagem("\nFalha ao excluir a lista.");
                }
            } catch (Exception e) {
                visaoUsuario.mostrarMensagem("\nERRO ao excluir a lista: " + e.getMessage());
            }
        }
        visaoUsuario.pausa();
        return false;
    }

    /**
     * Fecha as ligações com os ficheiros de dados geridos por este controlador.
     */
    public void close() throws Exception {
        crudLista.close();
    }
}


package view;

import java.time.LocalDate;
import java.time.format.*;
import java.util.*;
import model.Lista;

public class VisaoLista {

    private Scanner teclado;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VisaoLista() {
        this.teclado = new Scanner(System.in);
    }

    /**
     * Mostra todas as listas de um utilizador e retorna a sua escolha.
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
                System.out.print("(" + (i + 1) + ") " + l.getNome() + dataLimiteStr);
                System.out.println(" - codigo: " + l.getCodigoCompartilhavel());
            }
        }

        System.out.println("\n(N) Nova lista");
        System.out.println("(R) Retornar ao menu anterior");
        System.out.print("\nOpção: ");
        return teclado.nextLine();
    }

    /**
     * Mostra os detalhes de uma lista e, opcionalmente, o menu de gestão.
     */
    public String mostrarDetalhesLista(Lista lista, String nomeProprietario, boolean mostrarMenuGestao) {
        System.out.println("\n-----------------");
        System.out.println("> Início > Minhas Listas > " + lista.getNome());
        System.out.println("\nProprietário: " + nomeProprietario);
        System.out.println("CÓDIGO: " + lista.getCodigoCompartilhavel());
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
        return ""; // Se não for para mostrar o menu, retorna uma string vazia
    }

    /**
     * Pede ao utilizador para digitar o código de uma lista.
     */
    public String pedirCodigo() {
        System.out.print("\nDigite o código da lista que deseja procurar: ");
        return teclado.nextLine();
    }

    /**
     * Pede e lê os dados para a criação de uma nova lista.
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
     * Pede e lê os dados para a alteração de uma lista existente.
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
        LocalDate dataLimite = lerDataOpcional("Nova Data Limite (" + promptData + "): ");
        if (dataLimite == null) dataLimite = listaAtual.getDataLimite();

        return new Lista(-1, -1, nome, descricao, null, dataLimite, "");
    }

    /**
     * Pede a confirmação do utilizador para excluir uma lista.
     */
    public boolean confirmarExclusao(String nomeLista) {
        System.out.print("\nTem a certeza que deseja excluir a lista \"" + nomeLista + "\"? (S/N): ");
        return teclado.nextLine().equalsIgnoreCase("s");
    }

    // Método auxiliar para ler uma data opcional
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
}


import controller.ControlePrincipal;

/**
 * Ponto de entrada (Main class) para a aplicação 'PresenteFácil'.
 * A sua única responsabilidade é instanciar o {@link controller.ControlePrincipal}
 * e invocar o método {@code iniciar()}, tratando quaisquer exceções fatais
 * que possam ocorrer durante a inicialização.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.5
 */
public class Principal {
    
    /**
     * Método principal (main) que serve como ponto de entrada para a execução
     * do programa.
     * <p>
     * Instancia o {@link controller.ControlePrincipal} e chama {@code cp.iniciar()}.
     * Possui um bloco try-catch mestre para capturar e reportar
     * quaisquer exceções fatais (como erros de I/O na inicialização dos
     * arquivos) que não foram tratadas nos níveis inferiores.
     *
     * @param args Argumentos da linha de comando (não utilizados por esta
     * aplicação).
     */
    public static void main(String[] args) {
        try {
            ControlePrincipal cp = new ControlePrincipal();
            cp.iniciar();
        } catch (Exception e) {
            System.err.println("\nOcorreu um erro fatal no sistema!");
            e.printStackTrace();
        }
    }
}
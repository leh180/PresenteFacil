import controller.ControlePrincipal;

public class Principal {
    public static void main(String[] args) {
        try {
            // Cria uma instância do controle principal e inicia o sistema.
            ControlePrincipal cp = new ControlePrincipal();
            cp.iniciar();
        } catch (Exception e) {
            System.err.println("\nOcorreu um erro fatal no sistema!");
            e.printStackTrace();
        }
    }
}

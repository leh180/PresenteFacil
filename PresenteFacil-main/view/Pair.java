package view;

/**
 * Uma classe de utilidade genérica para armazenar um par de objetos imutável.
 * É usada para agrupar dados relacionados (como um Produto e a sua
 * associação ListaProduto) para serem passados entre as camadas
 * do controlador e da visão.
 *
 * @param <A> O tipo do primeiro objeto no par.
 * @param <B> O tipo do segundo objeto no par.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class Pair<A, B> {
    
    /**
     * O primeiro objeto do par.
     */
    public final A first;
    
    /**
     * O segundo objeto do par.
     */
    public final B second;

    /**
     * Construtor para criar um novo par de objetos.
     *
     * @param first O primeiro objeto (valor para 'first').
     * @param second O segundo objeto (valor para 'second').
     */
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }
}
package model;

import bib.RegistroArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Representa um par (Chave, Valor) para ser usado no índice de Árvore B+.
 * Esta classe específica associa um {@code int} (idProduto) ao ID (inteiro)
 * do registro principal da {@link ListaProduto}.
 * É usado para buscar eficientemente todas as listas que contêm um produto.
 *
 * Implementa {@link RegistroArvoreBMais} para serialização e comparação.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class ParIdProdutoListaProduto implements RegistroArvoreBMais<ParIdProdutoListaProduto> {

    private int idProduto; // Chave de busca (ID do Produto)
    private int idListaProduto; // Valor (ID do registro ListaProduto)
    private final short TAMANHO = 8;

    // --- Construtores ---

    /**
     * Construtor padrão.
     * Inicializa ambos os IDs como -1.
     */
    public ParIdProdutoListaProduto() {
        this(-1, -1);
    }

    /**
     * Construtor de busca.
     * Usado para criar um objeto de busca para a Árvore B+, onde apenas
     * a chave (idProduto) é relevante.
     *
     * @param idProduto O ID do produto (chave) a ser buscado.
     */
    public ParIdProdutoListaProduto(int idProduto) {
        this(idProduto, -1);
    }

    /**
     * Construtor completo.
     *
     * @param idProduto O ID do produto (chave).
     * @param idListaProduto O ID do registro de associação (valor).
     */
    public ParIdProdutoListaProduto(int idProduto, int idListaProduto) {
        this.idProduto = idProduto;
        this.idListaProduto = idListaProduto;
    }

    // --- Getters e setters ---

    /**
     * Obtém o ID do produto (chave).
     *
     * @return O ID (inteiro) do produto.
     */
    public int getIdProduto() {
        return idProduto;
    }

    /**
     * Define o ID do produto (chave).
     *
     * @param idProduto O novo ID (inteiro) do produto.
     */
    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    /**
     * Obtém o ID do registro ListaProduto (valor).
     *
     * @return O ID (inteiro) do registro ListaProduto.
     */
    public int getIdListaProduto() {
        return idListaProduto;
    }

    /**
     * Define o ID do registro ListaProduto (valor).
     *
     * @param idListaProduto O novo ID (inteiro) do registro ListaProduto.
     */
    public void setIdListaProduto(int idListaProduto) {
        this.idListaProduto = idListaProduto;
    }

    // --- Métodos da Interface RegistroArvoreBMais ---

    /**
     * Cria uma cópia profunda (clone) deste objeto.
     *
     * @return Um novo objeto {@code ParIdProdutoListaProduto} com os mesmos
     * valores.
     */
    @Override
    public ParIdProdutoListaProduto clone() {
        return new ParIdProdutoListaProduto(this.idProduto, this.idListaProduto);
    }

    /**
     * Retorna o tamanho fixo deste registro em bytes.
     *
     * @return O tamanho (short) do registro (8 bytes).
     */
    @Override
    public short size() {
        return this.TAMANHO;
    }

    /**
     * Compara este objeto com outro {@code ParIdProdutoListaProduto}.
     * A ordenação é feita primeiro pelo {@code idProduto} (chave principal)
     * e, em caso de empate, pelo {@code idListaProduto} (usado como
     * critério de desempate).
     *
     * @param outro O outro objeto {@code ParIdProdutoListaProduto} a ser
     * comparado.
     * @return Um valor negativo se este objeto for menor, zero se for igual,
     * ou um valor positivo se for maior que o outro.
     */
    @Override
    public int compareTo(ParIdProdutoListaProduto outro) {
        if (this.idProduto < outro.idProduto) return -1;
        if (this.idProduto > outro.idProduto) return 1;

        if (this.idListaProduto == -1 || outro.idListaProduto == -1) {
            return 0;
        }

        return Integer.compare(this.idListaProduto, outro.idListaProduto);
    }

    /**
     * Serializa o objeto (idProduto e idListaProduto) para um array de bytes
     * de tamanho fixo.
     *
     * @return O array de bytes ({@code byte[]}) representando o objeto.
     * @throws IOException se ocorrer um erro durante a escrita nos streams.
     */
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.idProduto);
        dos.writeInt(this.idListaProduto);
        return baos.toByteArray();
    }

    /**
     * Desserializa um array de bytes, preenchendo os atributos deste objeto
     * (idProduto e idListaProduto).
     *
     * @param vb O array de bytes ({@code byte[]}) lido do arquivo.
     * @throws IOException se ocorrer um erro during a leitura dos streams.
     */
    @Override
    public void fromByteArray(byte[] vb) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        this.idProduto = dis.readInt();
        this.idListaProduto = dis.readInt();
    }

    /**
     * Gera uma representação em String do par, útil para depuração.
     *
     * @return Uma String formatada com o idProduto e o idListaProduto.
     */
    @Override
    public String toString() {
        return "(" + idProduto + ", " + idListaProduto + ")";
    }
}
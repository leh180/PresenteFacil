package model;

import bib.RegistroArvoreBMais;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Representa um par (Chave, Valor) para ser usado no índice de Árvore B+.
 * Esta classe específica associa um {@code int} (idLista) ao ID (inteiro)
 * do registro principal da {@link ListaProduto}.
 * É usado para buscar eficientemente todos os produtos de uma lista.
 *
 * Implementa {@link RegistroArvoreBMais} para serialização e comparação.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class ParIdListaProduto implements RegistroArvoreBMais<ParIdListaProduto> {

    private int idLista; // Chave de busca (ID da Lista)
    private int idListaProduto; // Valor (ID do registro ListaProduto)
    
    /**
     * Tamanho fixo do registro em bytes.
     * (4 bytes para idLista) + (4 bytes para idListaProduto).
     * Total: 8 bytes.
     */
    private final short TAMANHO = 8;

    // --- Construtores ---

    /**
     * Construtor padrão.
     * Inicializa ambos os IDs como -1.
     */
    public ParIdListaProduto() {
        this(-1, -1);
    }

    /**
     * Construtor de busca.
     * Usado para criar um objeto de busca para a Árvore B+, onde apenas
     * a chave (idLista) é relevante.
     *
     * @param idLista O ID da lista (chave) a ser buscada.
     */
    public ParIdListaProduto(int idLista) {
        this(idLista, -1);
    }

    /**
     * Construtor completo.
     *
     * @param idLista O ID da lista (chave).
     * @param idListaProduto O ID do registro de associação (valor).
     */
    public ParIdListaProduto(int idLista, int idListaProduto) {
        this.idLista = idLista;
        this.idListaProduto = idListaProduto;
    }

    // --- Getters e setters ---

    /**
     * Obtém o ID da lista (chave).
     *
     * @return O ID (inteiro) da lista.
     */
    public int getIdLista() {
        return idLista;
    }

    /**
     * Define o ID da lista (chave).
     *
     * @param idLista O novo ID (inteiro) da lista.
     */
    public void setIdLista(int idLista) {
        this.idLista = idLista;
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
     * @return Um novo objeto {@code ParIdListaProduto} com os mesmos valores.
     */
    @Override
    public ParIdListaProduto clone() {
        return new ParIdListaProduto(this.idLista, this.idListaProduto);
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
     * Compara este objeto com outro {@code ParIdListaProduto}.
     * A ordenação é feita primeiro pelo {@code idLista} (chave principal)
     * e, em caso de empate, pelo {@code idListaProduto} (usado como
     * critério de desempate).
     *
     * @param outro O outro objeto {@code ParIdListaProduto} a ser comparado.
     * @return Um valor negativo se este objeto for menor, zero se for igual,
     * ou um valor positivo se for maior que o outro.
     */
    @Override
    public int compareTo(ParIdListaProduto outro) {
        if (this.idLista != outro.idLista) {
            return Integer.compare(this.idLista, outro.idLista);
        } else {
            return Integer.compare(this.idListaProduto, outro.idListaProduto);
        }
    }

    /**
     * Serializa o objeto (idLista e idListaProduto) para um array de bytes
     * de tamanho fixo.
     *
     * @return O array de bytes ({@code byte[]}) representando o objeto.
     * @throws IOException se ocorrer um erro durante a escrita nos streams.
     */
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.idLista);
        dos.writeInt(this.idListaProduto);
        return baos.toByteArray();
    }

    /**
     * Desserializa um array de bytes, preenchendo os atributos deste objeto
     * (idLista e idListaProduto).
     *
     * @param vb O array de bytes ({@code byte[]}) lido do arquivo.
     * @throws IOException se ocorrer um erro during a leitura dos streams.
     */
    @Override
    public void fromByteArray(byte[] vb) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        this.idLista = dis.readInt();
        this.idListaProduto = dis.readInt();
    }

    /**
     * Gera uma representação em String do par, útil para depuração.
     *
     * @return Uma String formatada com o idLista e o idListaProduto.
     */
    @Override
    public String toString() {
        return "(" + idLista + ", " + idListaProduto + ")";
    }
}
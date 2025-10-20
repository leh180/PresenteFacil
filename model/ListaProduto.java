package model;

import bib.Entidade;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Representa a entidade associativa N:N entre {@link Lista} e {@link Produto}.
 * Esta classe armazena não apenas as chaves estrangeiras, mas também
 * atributos próprios do relacionamento, como a quantidade e observações
 * de um produto específico dentro de uma lista.
 * Implementa a interface {@link Entidade} para serialização/desserialização.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class ListaProduto implements Entidade {

    // --- Atributos ---
    private int id;
    private int idLista;
    private int idProduto;
    private int quantidade;
    private String observacoes;

    // --- Construtores ---
    
    /**
     * Construtor padrão.
     * Inicializa uma associação com valores padrão (IDs -1, quantidade 1, obs vazia).
     */
    public ListaProduto() { 
        this(-1, -1, -1, 1, "");
    }

    /**
     * Construtor para criar uma nova associação com quantidade padrão.
     *
     * @param idLista O ID (chave estrangeira) da Lista.
     * @param idProduto O ID (chave estrangeira) do Produto.
     */
    public ListaProduto(int idLista, int idProduto) {
        this(-1, idLista, idProduto, 1, "");
    }

    /**
     * Construtor completo.
     *
     * @param id O ID único da associação (geralmente definido pelo CRUD).
     * @param idLista O ID (chave estrangeira) da Lista.
     * @param idProduto O ID (chave estrangeira) do Produto.
     * @param quantidade A quantidade do produto nesta lista.
     * @param observacoes Observações/notas sobre este item na lista.
     */
    public ListaProduto(int id, int idLista, int idProduto, int quantidade, String observacoes) { 
        this.id = id;
        this.idLista = idLista;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.observacoes = observacoes;
    }

    //--- Getters e Setters ---
    
    /**
     * Obtém o ID único da entidade.
     *
     * @return O ID (inteiro) da associação.
     */
    @Override
    public int getID() {
        return this.id;
    }

    /**
     * Define o ID único da entidade.
     *
     * @param id O novo ID (inteiro) da associação.
     */
    @Override
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Obtém o ID da Lista à qual esta associação pertence.
     *
     * @return O ID (inteiro) da Lista.
     */
    public int getIdLista() {
        return idLista;
    }

    /**
     * Define o ID da Lista à qual esta associação pertence.
     *
     * @param idLista O novo ID (inteiro) da Lista.
     */
    public void setIdLista(int idLista) {
        this.idLista = idLista;
    }

    /**
     * Obtém o ID do Produto ao qual esta associação se refere.
     *
     * @return O ID (inteiro) do Produto.
     */
    public int getIdProduto() {
        return idProduto;
    }

    /**
     * Define o ID do Produto ao qual esta associação se refere.
     *
     * @param idProduto O novo ID (inteiro) do Produto.
     */
    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    /**
     * Obtém a quantidade deste produto na lista.
     *
     * @return A quantidade (inteiro).
     */
    public int getQuantidade() {
        return quantidade;
    }

    /**
     * Define a quantidade deste produto na lista.
     *
     * @param quantidade A nova quantidade (inteiro).
     */
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    /**
     * Obtém as observações para este item na lista.
     *
     * @return As observações (String).
     */
    public String getObservacoes() {
        return observacoes;
    }

    /**
     * Define as observações para este item na lista.
     *
     * @param observacoes As novas observações (String).
     */
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    // --- Métodos da Interface Entidade ---

    /**
     * Serializa o objeto ListaProduto para um array de bytes, para persistência
     * em arquivo.
     *
     * @return Um array de bytes ({@code byte[]}) representando o objeto.
     * @throws Exception se ocorrer um erro durante a escrita nos streams.
     */
    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeInt(this.idLista);
        dos.writeInt(this.idProduto);
        dos.writeInt(this.quantidade);
        dos.writeUTF(this.observacoes);
        return baos.toByteArray();
    }

    /**
     * Desserializa um array de bytes, preenchendo os atributos deste objeto
     * ListaProduto.
     *
     * @param vb O array de bytes ({@code byte[]}) lido do arquivo.
     * @throws Exception se ocorrer um erro during a leitura dos streams
     * (ex: EOF, formato inválido).
     */
    @Override
    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.idLista = dis.readInt();
        this.idProduto = dis.readInt();
        this.quantidade = dis.readInt();
        this.observacoes = dis.readUTF();
    }

    /**
     * Gera uma representação em String do objeto ListaProduto, útil para
     * depuração.
     *
     * @return Uma String formatada com todos os atributos da associação.
     */
    @Override
    public String toString() {
        return "ListaProduto [ID=" + id + ", ID Lista=" + idLista + ", ID Produto=" + idProduto + 
               ", Quantidade=" + quantidade + ", Observações=" + observacoes + "]";
    }
}
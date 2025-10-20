package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import bib.RegistroHashExtensivel;

/**
 * Representa um par (Chave, Valor) para ser usado no índice de Hash Extensível.
 * Esta classe específica associa um {@code String} (código compartilhável da
 * lista)
 * ao ID (inteiro) do registro principal da {@link Lista}.
 * Implementa {@link RegistroHashExtensivel} para serialização de tamanho fixo.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.0
 */
public class ParCodigoId implements RegistroHashExtensivel {

    private String codigo;
    private int id;
    
    /**
     * Tamanho fixo do registro em bytes.
     * (4 bytes para o ID) + (10 caracteres * 2 bytes/char = 20 bytes para o código).
     * Total: 24 bytes.
     */
    private final short TAMANHO = 24;

    /**
     * Construtor padrão.
     * Inicializa o par com um código vazio e ID -1.
     */
    public ParCodigoId() {
        this("", -1);
    }

    /**
     * Construtor completo.
     *
     * @param codigo O código (chave) a ser armazenado (ex: código da lista).
     * @param id O ID (valor) do registro principal associado ao código.
     */
    public ParCodigoId(String codigo, int id) {
        this.codigo = codigo;
        this.id = id;
    }

    /**
     * Obtém o código (chave) do par.
     *
     * @return O código (String).
     */
    public String getCodigo() { return this.codigo; }
    
    /**
     * Obtém o ID (valor) do par.
     *
     * @return O ID (inteiro).
     */
    public int getId() { return this.id; }

    /**
     * Gera o código hash para este registro.
     * O hash é baseado no {@code codigo} (String), que é a chave do índice.
     *
     * @return O valor do hash (inteiro) do código.
     */
    @Override
    public int hashCode() {
        return this.codigo.hashCode();
    }

    /**
     * Retorna o tamanho fixo deste registro em bytes.
     *
     * @return O tamanho (short) do registro (24 bytes).
     */
    @Override
    public short size() {
        return TAMANHO;
    }

    /**
     * Serializa o objeto (ID e código) para um array de bytes de tamanho fixo.
     * O código é preenchido ou truncado para 10 caracteres.
     *
     * @return O array de bytes ({@code byte[]}) representando o objeto.
     * @throws IOException se ocorrer um erro durante a escrita nos streams.
     */
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        // Escreve a string com tamanho fixo (10 chars) para garantir consistência
        dos.writeChars(String.format("%-10.10s", this.codigo));
        return baos.toByteArray();
    }

    /**
     * Desserializa um array de bytes, preenchendo os atributos deste objeto
     * (ID e código).
     * Lê 10 caracteres fixos e remove o preenchimento (trim).
     *
     * @param ba O array de bytes ({@code byte[]}) lido do arquivo.
     * @throws IOException se ocorrer um erro during a leitura dos streams.
     */
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        char[] codigoChars = new char[10];
        for (int i = 0; i < 10; i++) {
            codigoChars[i] = dis.readChar();
        }
        this.codigo = new String(codigoChars).trim();
    }
}
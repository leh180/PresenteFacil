package model;

import bib.RegistroHashExtensivel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Representa um par (Chave, Valor) para ser usado no índice de Hash Extensível.
 * Esta classe específica associa um {@code String} (GTIN do produto)
 * ao ID (inteiro) do registro principal do {@link Produto}.
 * Implementa {@link RegistroHashExtensivel} para serialização de tamanho fixo.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class ParGtinId implements RegistroHashExtensivel {

    private String gtin;
    private int id;
    public final short SIZE = 44;

    /**
     * Construtor padrão.
     * Inicializa o par com um GTIN vazio e ID -1.
     */
    public ParGtinId() {
        this.gtin = "";
        this.id = -1;
    }

    /**
     * Construtor completo.
     *
     * @param gtin O GTIN (chave) a ser armazenado.
     * @param id O ID (valor) do registro principal associado ao GTIN.
     */
    public ParGtinId(String gtin, int id) {
        this.gtin = gtin;
        this.id = id;
    }

    /**
     * Gera o código hash para este registro.
     * O hash é baseado no {@code gtin} (String), que é a chave do índice.
     *
     * @return O valor do hash (inteiro) do GTIN.
     */
    @Override
    public int hashCode() {
        return this.gtin.hashCode();
    }

    /**
     * Define o ID (valor) do par.
     *
     * @param id O novo ID (inteiro).
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Obtém o ID (valor) do par.
     *
     * @return O ID (inteiro).
     */
    public int getID() {
        return this.id;
    }

    /**
     * Define o GTIN (chave) do par.
     *
     * @param gtin O novo GTIN (String).
     */
    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    /**
     * Obtém o GTIN (chave) do par.
     *
     * @return O GTIN (String).
     */
    public String getGtin() {
        return this.gtin;
    }

    /**
     * Retorna o tamanho fixo deste registro em bytes.
     *
     * @return O tamanho (short) do registro (44 bytes).
     */
    @Override
    public short size() {
        return SIZE;
    }

    /**
     * Serializa o objeto (ID e GTIN) para um array de bytes de tamanho fixo.
     * O GTIN é preenchido ou truncado para 20 caracteres.
     *
     * @return O array de bytes ({@code byte[]}) representando o objeto.
     * @throws IOException se ocorrer um erro durante a escrita nos streams.
     */
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeChars(String.format("%-20.20s", this.gtin));
        return baos.toByteArray();
    }

    /**
     * Desserializa um array de bytes, preenchendo os atributos deste objeto
     * (ID e GTIN).
     * Lê 20 caracteres fixos e remove o preenchimento (trim).
     *
     * @param ba O array de bytes ({@code byte[]}) lido do arquivo.
     * @throws IOException se ocorrer um erro during a leitura dos streams.
     */
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        char[] gtinChars = new char[20];
        for (int i = 0; i < 20; i++) {
            gtinChars[i] = dis.readChar();
        }
        this.gtin = new String(gtinChars).trim();
    }

    /**
     * Gera uma representação em String do par, útil para depuração.
     *
     * @return Uma String formatada com o GTIN e o ID.
     */
    @Override
    public String toString() {
        return "Par(gtin=" + gtin + ", id=" + id + ")";
    }
}
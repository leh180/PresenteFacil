package model;

import bib.RegistroHashExtensivel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Representa um par (Chave, Valor) para ser usado no índice de Hash Extensível.
 * Esta classe específica associa um {@code String} (e-mail do usuário)
 * ao ID (inteiro) do registro principal do {@link Usuario}.
 * Implementa {@link RegistroHashExtensivel} para serialização de tamanho fixo.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.0
 */
public class ParEmailId implements RegistroHashExtensivel {

    private String email;
    private int id;
    public final short SIZE = 66; 

    /**
     * Construtor padrão.
     * Inicializa o par com um e-mail vazio e ID -1.
     */
    public ParEmailId() {
        this.email = "";
        this.id = -1;
    }

    /**
     * Construtor completo.
     *
     * @param email O e-mail (chave) a ser armazenado.
     * @param id O ID (valor) do registro principal associado ao e-mail.
     */
    public ParEmailId(String email, int id) {
        this.email = email;
        this.id = id;
    }
    
    /**
     * Obtém o ID (valor) do par.
     *
     * @return O ID (inteiro).
     */
    public int getId() {
        return this.id;
    }

    /**
     * Obtém o e-mail (chave) do par.
     *
     * @return O e-mail (String).
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Gera o código hash para este registro.
     * O hash é baseado no {@code email} (String), que é a chave do índice.
     *
     * @return O valor do hash (inteiro) do e-mail.
     */
    @Override
    public int hashCode() {
        return this.email.hashCode();
    }

    /**
     * Retorna o tamanho fixo deste registro em bytes.
     *
     * @return O tamanho (short) do registro (66 bytes).
     */
    @Override
    public short size() {
        return SIZE;
    }

    /**
     * Serializa o objeto (e-mail e ID) para um array de bytes de tamanho fixo.
     * Utiliza {@code writeUTF} para o e-mail e preenche o restante do array
     * com zeros (padding) para atingir o {@code SIZE} definido.
     *
     * @return O array de bytes ({@code byte[]}) representando o objeto, com
     * tamanho fixo.
     * @throws IOException se ocorrer um erro durante a escrita nos streams.
     */
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(this.email);
        dos.writeInt(this.id);
        
        byte[] resultado = baos.toByteArray();
        
        byte[] resultadoFinal = new byte[SIZE];
        System.arraycopy(resultado, 0, resultadoFinal, 0, resultado.length);

        return resultadoFinal;
    }

    /**
     * Desserializa um array de bytes, preenchendo os atributos deste objeto
     * (e-mail e ID).
     * Lê o e-mail usando {@code readUTF} e o ID usando {@code readInt}.
     *
     * @param ba O array de bytes ({@code byte[]}) lido do arquivo.
     * @throws IOException se ocorrer um erro during a leitura dos streams
     * (ex: EOF, formato UTF inválido).
     */
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.email = dis.readUTF();
        this.id = dis.readInt();
    }
    
    /**
     * Gera uma representação em String do par, útil para depuração.
     *
     * @return Uma String formatada com o e-mail e o ID.
     */
    @Override
    public String toString() {
        return "Par(email=" + email + ", id=" + id + ")";
    }
}
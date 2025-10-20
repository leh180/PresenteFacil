package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import bib.RegistroArvoreBMais;

/**
 * Representa um par (Chave, Valor) para ser usado no índice de Árvore B+.
 * Esta classe específica associa um {@code int} (idUsuario) ao ID (inteiro)
 * do registro principal da {@link Lista}.
 * É usado para buscar eficientemente todas as listas de um usuário.
 *
 * Implementa {@link RegistroArvoreBMais} para serialização e comparação.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.0
 */
public class ParUsuarioLista implements RegistroArvoreBMais<ParUsuarioLista> {

    private int idUsuario;
    private int idLista;
    private final short TAMANHO = 8;

    /**
     * Construtor padrão.
     * Inicializa ambos os IDs como -1.
     */
    public ParUsuarioLista() {
        this(-1, -1);
    }

    /**
     * Construtor completo.
     * Usado tanto para criar novos registros de índice quanto para
     * criar objetos de busca (passando idLista = -1).
     *
     * @param idUsuario O ID do usuário (chave).
     * @param idLista O ID da lista (valor/chave secundária).
     */
    public ParUsuarioLista(int idUsuario, int idLista) {
        this.idUsuario = idUsuario;
        this.idLista = idLista;
    }

    /**
     * Obtém o ID do usuário (chave).
     *
     * @return O ID (inteiro) do usuário.
     */
    public int getIdUsuario() { return this.idUsuario; }
    
    /**
     * Obtém o ID da lista (valor/chave secundária).
     *
     * @return O ID (inteiro) da lista.
     */
    public int getIdLista() { return this.idLista; }

    /**
     * Retorna o tamanho fixo deste registro em bytes.
     *
     * @return O tamanho (short) do registro (8 bytes).
     */
    @Override
    public short size() {
        return TAMANHO;
    }

    /**
     * Serializa o objeto (idUsuario e idLista) para um array de bytes
     * de tamanho fixo.
     *
     * @return O array de bytes ({@code byte[]}) representando o objeto.
     * @throws IOException se ocorrer um erro durante a escrita nos streams.
     */
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idUsuario);
        dos.writeInt(idLista);
        return baos.toByteArray();
    }

    /**
     * Desserializa um array de bytes, preenchendo os atributos deste objeto
     * (idUsuario e idLista).
     *
     * @param ba O array de bytes ({@code byte[]}) lido do arquivo.
     * @throws IOException se ocorrer um erro during a leitura dos streams.
     */
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.idUsuario = dis.readInt();
        this.idLista = dis.readInt();
    }

    /**
     * Cria uma cópia profunda (clone) deste objeto.
     *
     * @return Um novo objeto {@code ParUsuarioLista} com os mesmos valores.
     */
    @Override
    public ParUsuarioLista clone() {
        return new ParUsuarioLista(this.idUsuario, this.idLista);
    }

    /**
     * Compara este objeto com outro {@code ParUsuarioLista}.
     * A ordenação é feita primeiro pelo {@code idUsuario} (chave principal).
     * <p>
     * Inclui uma lógica especial para busca: se {@code idLista} deste
     * objeto ou do {@code obj} for -1, o método retorna 0 (igual)
     * contanto que os {@code idUsuario}s sejam os mesmos. Isso permite
     * que a Árvore B+ encontre todos os registros com um {@code idUsuario}
     * específico (busca por chave parcial).
     * <p>
     * Se ambos os {@code idLista} forem válidos (diferentes de -1), eles
     * são usados como critério de desempate.
     *
     * @param obj O outro objeto {@code ParUsuarioLista} a ser comparado.
     * @return Um valor negativo se este objeto for menor, zero se for igual
     * (ou se for uma busca parcial correspondente),
     * ou um valor positivo se for maior que o outro.
     */
    @Override
    public int compareTo(ParUsuarioLista obj) {
        if (this.idUsuario < obj.idUsuario) return -1;
        if (this.idUsuario > obj.idUsuario) return 1;
        
        if (this.idLista == -1 || obj.idLista == -1) {
            return 0; 
        }

        if (this.idLista < obj.idLista) return -1;
        if (this.idLista > obj.idLista) return 1;

        return 0; 
    }

    /**
     * Gera uma representação em String do par, útil para depuração.
     *
     * @return Uma String formatada com o idUsuario e o idLista.
     */
    @Override
    public String toString() {
        return "(" + this.idUsuario + ";" + this.idLista + ")";
    }
}
package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import bib.RegistroArvoreBMais;

public class ParUsuarioLista implements RegistroArvoreBMais<ParUsuarioLista> {

    private int idUsuario;
    private int idLista;
    private final short TAMANHO = 8;

    public ParUsuarioLista() {
        this(-1, -1);
    }

    public ParUsuarioLista(int idUsuario, int idLista) {
        this.idUsuario = idUsuario;
        this.idLista = idLista;
    }

    public int getIdUsuario() { return this.idUsuario; }
    public int getIdLista() { return this.idLista; }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idUsuario);
        dos.writeInt(idLista);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.idUsuario = dis.readInt();
        this.idLista = dis.readInt();
    }

    @Override
    public ParUsuarioLista clone() {
        return new ParUsuarioLista(this.idUsuario, this.idLista);
    }

    /**
     * Compara este objeto com outro. Esta é a lógica central para a busca na Árvore B+.
     */
    @Override
    public int compareTo(ParUsuarioLista obj) {
        // Compara primeiro pela chave primária (idUsuario)
        if (this.idUsuario < obj.idUsuario) return -1;
        if (this.idUsuario > obj.idUsuario) return 1;
        
        // Lógica especial para a busca: se uma das chaves secundárias for -1,
        // significa que estamos a procurar todos os registos com a mesma chave primária.
        // Neste caso, consideramos os objetos "iguais" em termos de chave primária.
        if (this.idLista == -1 || obj.idLista == -1) {
            return 0; 
        }

        // Se ambas as chaves secundárias forem válidas, fazemos a comparação completa.
        if (this.idLista < obj.idLista) return -1;
        if (this.idLista > obj.idLista) return 1;

        return 0; // As chaves primária e secundária são idênticas.
    }

    @Override
    public String toString() {
        return "(" + this.idUsuario + ";" + this.idLista + ")";
    }
}


package model;

import bib.RegistroArvoreBMais;
import java.io.*;

/**
 * Par (idLista, idListaProduto) para indexação na Árvore B+
 * Vusca todos os produtos de uma determinada lista
 * Ordena primeiro por idLista depois por idListaProduto
 */
public class ParIdListaProduto implements RegistroArvoreBMais<ParIdListaProduto> {

    private int idLista; // Chave
    private int idListaProduto; // Valor (Id do registro)
    private final short TAMANHO = 8; // 2 inteiros = 4 + 4 bytes

    // Construtores
    public ParIdListaProduto() { // Vazio
        this(-1, -1);
    }

    public ParIdListaProduto(int idLista) { // Busca lista
        this(idLista, -1);
    }

    public ParIdListaProduto(int idLista, int idListaProduto) { // Completo
        this.idLista = idLista;
        this.idListaProduto = idListaProduto;
    }

    // Getters e setters
    public int getIdLista() {
        return idLista;
    }

    public void setIdLista(int idLista) {
        this.idLista = idLista;
    }

    public int getIdListaProduto() {
        return idListaProduto;
    }

    public void setIdListaProduto(int idListaProduto) {
        this.idListaProduto = idListaProduto;
    }

    // outros métodos (interface pro RegistroArvoreBMais)

    @Override
    public ParIdListaProduto clone() { // Clone do atributo o que permite atribuições independentes
        return new ParIdListaProduto(this.idLista, this.idListaProduto);
    }

    @Override
    public short size() {
        return this.TAMANHO;
    }

    /**
     * Compara este par com outro
     * Ordena primeiro por idLista depois por idListaProduto
     */
    @Override
    public int compareTo(ParIdListaProduto outro) {
        if (this.idLista != outro.idLista) {
            return Integer.compare(this.idLista, outro.idLista);
        } else {
            return Integer.compare(this.idListaProduto, outro.idListaProduto);
        }
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.idLista);
        dos.writeInt(this.idListaProduto);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] vb) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        this.idLista = dis.readInt();
        this.idListaProduto = dis.readInt();
    }

    @Override
    public String toString() {
        return "(" + idLista + ", " + idListaProduto + ")";
    }
}
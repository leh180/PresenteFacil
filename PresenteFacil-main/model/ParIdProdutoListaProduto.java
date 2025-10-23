package model;

import bib.RegistroArvoreBMais;
import java.io.*;

/**
 * Par (idProduto, idListaProduto) para indexação na Árvore B+
 * Busca todas as listas que contêm um determinado produto
 * Ordena primeiro por idProduto e depois por idListaProduto
 */
public class ParIdProdutoListaProduto implements RegistroArvoreBMais<ParIdProdutoListaProduto> {

    private int idProduto;
    private int idListaProduto;
    private final short TAMANHO = 8; // Mesma idéia do ParIdListaProduto

    // Construtores
    public ParIdProdutoListaProduto() {
        this(-1, -1);
    }

    public ParIdProdutoListaProduto(int idProduto) {
        this(idProduto, -1);
    }

    public ParIdProdutoListaProduto(int idProduto, int idListaProduto) {
        this.idProduto = idProduto;
        this.idListaProduto = idListaProduto;
    }

    // Getters e setters
    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getIdListaProduto() {
        return idListaProduto;
    }

    public void setIdListaProduto(int idListaProduto) {
        this.idListaProduto = idListaProduto;
    }

    // Outros métodos (interface pro RegistroArvoreBMais)

    @Override
    public ParIdProdutoListaProduto clone() { // UM clone (permite atribuições independentes)
        return new ParIdProdutoListaProduto(this.idProduto, this.idListaProduto);
    }

    @Override
    public short size() {
        return this.TAMANHO;
    }

    /**
     * Compara este par com outro
     * Ordena primeiro por idProduto e depois por idListaProduto
     */
    @Override
    public int compareTo(ParIdProdutoListaProduto outro) {
        if (this.idProduto != outro.idProduto) {
            return Integer.compare(this.idProduto, outro.idProduto);
        } else {
            return Integer.compare(this.idListaProduto, outro.idListaProduto);
        }
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.idProduto);
        dos.writeInt(this.idListaProduto);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] vb) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        this.idProduto = dis.readInt();
        this.idListaProduto = dis.readInt();
    }

    @Override
    public String toString() {
        return "(" + idProduto + ", " + idListaProduto + ")";
    }
}
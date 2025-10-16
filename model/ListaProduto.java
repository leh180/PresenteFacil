package model;

import bib.Entidade;
import java.io.*;

/**
 * Entidade com associação N:N de lista e produto.
 * Representa um produto em uma lista de compras com quantidade e observações.
 */
public class ListaProduto implements Entidade {

    // Atributos
    private int id;
    private int idLista; // Chave estrangeira para lista
    private int idProduto; // Chave estrangeira pra produto
    private int quantidade;
    private String observacoes;

    // Construtores
    public ListaProduto() { // Vazio 
        this(-1, -1, -1, 1, "");
    }

    public ListaProduto(int idLista, int idProduto) {
        this(-1, idLista, idProduto, 1, "");
    }

    public ListaProduto(int id, int idLista, int idProduto, int quantidade, String observacoes) { // Completo
        this.id = id;
        this.idLista = idLista;
        this.idProduto = idProduto;
        this.quantidade = quantidade;
        this.observacoes = observacoes;
    }

    //Getters e Setters
    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    public int getIdLista() {
        return idLista;
    }

    public void setIdLista(int idLista) {
        this.idLista = idLista;
    }

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    // Outros métodos (Interface de usuário)

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

    @Override
    public String toString() {
        return "ListaProduto [ID=" + id + ", ID Lista=" + idLista + ", ID Produto=" + idProduto + 
               ", Quantidade=" + quantidade + ", Observações=" + observacoes + "]";
    }
}
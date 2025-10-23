//Produto.java (novo):
//Atributos: id, gtin (long), nome, descrição, e um boolean ativo.
//Deve implementar a interface Entidade (toByteArray/fromByteArray).

package model;

import bib.Entidade;
import java.io.*;

public class Produto implements Entidade{

    // --- Atributos ---
    private int id;
    private String gtin;
    private String nome;
    private String descricao;
    private boolean ativo;

    // --- Construtores ---
    public Produto(){
        this(-1,"","","", false);
    }

    /**
     * Construtor principal. Recebe a senha em texto plano e calcula o hash.
     */
    public Produto(int id, String gtin, String nome, String descricao, boolean ativo){
        this.id = id;
        this.gtin = gtin;
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    // --- Getters e Setters ---
    public void setID( int id){ this.id = id;}
    public int getID(){ return this.id;}

    public void setGtin(String gtin){ this.gtin = gtin; }
    public String getGtin(){ return this.gtin; }

    public void setNome(String nome){ this.nome = nome; }
    public String getNome(){ return this.nome; }

    public void setDescricao(String descricao){ this.descricao = descricao; }
    public String getDescricao(){ return this.descricao; }

    public void setAtivo(boolean ativo){ this.ativo = ativo; }
    public boolean isAtivo(){ return this.ativo; }

    // --- Métodos da Interface Entidade ---

    @Override
    public String toString() {
        return "ID: " + this.id +
               "\nGTIN: " + this.gtin +
               "\nNOME: " + this.nome +
               "\nDESCRICAO: " + this.descricao +
               "\nATIVO: " + this.ativo;
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.gtin);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.descricao); 
        dos.writeBoolean(this.ativo);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.gtin = dis.readUTF();
        this.nome = dis.readUTF();
        this.descricao = dis.readUTF(); 
        this.ativo = dis.readBoolean();
    }
}
package model;

import bib.Entidade;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Representa a entidade "Produto" no sistema.
 * Esta classe armazena informações básicas do produto, como GTIN, nome e status.
 * Implementa a interface {@link Entidade} para permitir a
 * serialização/desserialização personalizada para o sistema de arquivos.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 2.0
 */
public class Produto implements Entidade{

    // --- Atributos ---
    private int id;
    private String gtin;
    private String nome;
    private String descricao;
    private boolean ativo;

    // --- Construtores ---
    
    /**
     * Construtor padrão.
     * Inicializa um produto com valores padrão (ID -1, strings vazias, inativo).
     */
    public Produto(){
        this(-1,"","","", false);
    }

    /**
     * Construtor completo.
     *
     * @param id O ID único do produto (geralmente definido pelo CRUD).
     * @param gtin O código GTIN (Global Trade Item Number) do produto.
     * @param nome O nome/título do produto.
     * @param descricao Uma breve descrição do produto.
     * @param ativo O status do produto (true = ativo, false = inativo).
     */
    public Produto(int id, String gtin, String nome, String descricao, boolean ativo){
        this.id = id;
        this.gtin = gtin;
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = ativo;
    }

    // --- Getters e Setters ---
    
    /**
     * Define o ID único da entidade.
     *
     * @param id O novo ID (inteiro) do produto.
     */
    public void setID( int id){ this.id = id;}
    
    /**
     * Obtém o ID único da entidade.
     *
     * @return O ID (inteiro) do produto.
     */
    public int getID(){ return this.id;}

    /**
     * Define o código GTIN do produto.
     *
     * @param gtin O novo GTIN (String).
     */
    public void setGtin(String gtin){ this.gtin = gtin; }
    
    /**
     * Obtém o código GTIN do produto.
     *
     * @return O GTIN (String).
     */
    public String getGtin(){ return this.gtin; }

    /**
     * Define o nome do produto.
     *
     * @param nome O novo nome (String).
     */
    public void setNome(String nome){ this.nome = nome; }
    
    /**
     * Obtém o nome do produto.
     *
     * @return O nome (String).
     */
    public String getNome(){ return this.nome; }

    /**
     * Define a descrição do produto.
     *
     * @param descricao A nova descrição (String).
     */
    public void setDescricao(String descricao){ this.descricao = descricao; }
    
    /**
     * Obtém a descrição do produto.
     *
     * @return A descrição (String).
     */
    public String getDescricao(){ return this.descricao; }

    /**
     * Define o status de ativação do produto.
     *
     * @param ativo {@code true} para ativo, {@code false} para inativo.
     */
    public void setAtivo(boolean ativo){ this.ativo = ativo; }
    
    /**
     * Verifica se o produto está ativo.
     *
     * @return {@code true} se o produto está ativo, {@code false} caso contrário.
     */
    public boolean isAtivo(){ return this.ativo; }

    // --- Métodos da Interface Entidade ---

    /**
     * Gera uma representação em String do objeto Produto, útil para depuração.
     *
     * @return Uma String formatada com todos os atributos do produto.
     */
    @Override
    public String toString() {
        return "ID: " + this.id +
               "\nGTIN: " + this.gtin +
               "\nNOME: " + this.nome +
               "\nDESCRICAO: " + this.descricao +
               "\nATIVO: " + this.ativo;
    }

    /**
     * Serializa o objeto Produto para um array de bytes, para persistência em
     * arquivo.
     *
     * @return Um array de bytes ({@code byte[]}) representando o objeto.
     * @throws Exception se ocorrer um erro durante a escrita nos streams.
     */
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

    /**
     * Desserializa um array de bytes, preenchendo os atributos deste objeto
     * Produto.
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
        this.gtin = dis.readUTF();
        this.nome = dis.readUTF();
        this.descricao = dis.readUTF(); 
        this.ativo = dis.readBoolean();
    }
}
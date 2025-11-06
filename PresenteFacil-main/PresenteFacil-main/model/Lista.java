package model;

import bib.Entidade;
import java.io.*;
import java.time.LocalDate;

/**
 * A classe 'Lista' representa a entidade "Lista de Presentes" no sistema.
 * Ela implementa a interface 'Entidade' para ser compatível com o sistema de arquivos
 * genérico e 'Comparable' para permitir a ordenação alfabética das listas pelo nome.
 */
public class Lista implements Entidade, Comparable<Lista> {

    // ------------------------------------------ Atributos da Classe ------------------------------------------

    private int id;
    private int idUsuario; 
    private String nome;
    private String descricao;
    private LocalDate dataCriacao;
    private LocalDate dataLimite;
    private String codigoCompartilhavel;

    // ------------------------------------------ Construtores ------------------------------------------

    public Lista() {
        this(-1, -1, "", "", null, null, "");
    }

    public Lista(int id, int idUsuario, String nome, String descricao, LocalDate dataCriacao, LocalDate dataLimite, String codigo) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.dataLimite = dataLimite;
        this.codigoCompartilhavel = codigo;
    }

    // ------------------------------------------ Getters e Setters ------------------------------------------

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDate dataLimite) {
        this.dataLimite = dataLimite;
    }

    public String getCodigoCompartilhavel() {
        return codigoCompartilhavel;
    }

    public void setCodigoCompartilhavel(String codigoCompartilhavel) {
        this.codigoCompartilhavel = codigoCompartilhavel;
    }

    // ------------------------------------------ Métodos da Interface Entidade ------------------------------------------

    /**
     * Converte o objeto Lista para um array de bytes para armazenamento em arquivo.
     * @return um array de bytes que representa o objeto.
     * @throws Exception se ocorrer um erro durante a serialização.
     */
    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeInt(this.idUsuario);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.descricao);
        dos.writeUTF(this.codigoCompartilhavel);
        dos.writeLong(this.dataCriacao.toEpochDay()); 
        
        if (this.dataLimite != null) {
            dos.writeBoolean(true);
            dos.writeLong(this.dataLimite.toEpochDay());
        } else {
            dos.writeBoolean(false);
        }

        return baos.toByteArray();
    }

    /**
     * Preenche os atributos do objeto a partir de um array de bytes lido do arquivo.
     * @param vb O array de bytes que representa o objeto.
     * @throws Exception se ocorrer um erro durante a desserialização.
     */
    @Override
    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.idUsuario = dis.readInt();
        this.nome = dis.readUTF();
        this.descricao = dis.readUTF();
        this.codigoCompartilhavel = dis.readUTF();
        this.dataCriacao = LocalDate.ofEpochDay(dis.readLong()); 

        if (dis.readBoolean()) { 
            this.dataLimite = LocalDate.ofEpochDay(dis.readLong());
        } else {
            this.dataLimite = null;
        }
    }

    // ------------------------------------------ Outros Métodos ------------------------------------------

    @Override
    public String toString() {
        return "Lista [ID=" + id + ", ID do Usuário=" + idUsuario + ", Nome=" + nome + ", Descrição=" + descricao
                + ", Data de Criação=" + dataCriacao + ", Data Limite=" + dataLimite + ", Código=" + codigoCompartilhavel + "]";
    }
    
    /**
     * Compara esta lista com outra pelo nome, para ordenação alfabética.
     * Ignora a diferença entre maiúsculas e minúsculas.
     * @param outraLista A outra lista a ser comparada.
     * @return um valor negativo se o nome desta lista vier antes, positivo se vier depois, e zero se forem iguais.
     */
    @Override
    public int compareTo(Lista outraLista) {
        return this.nome.compareToIgnoreCase(outraLista.getNome());
    }
}
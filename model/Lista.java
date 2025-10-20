package model;

import bib.Entidade;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.LocalDate;

/**
 * Representa a entidade "Lista de Presentes" no sistema.
 * Esta classe implementa a interface {@link Entidade} para permitir a
 * serialização/desserialização personalizada para o sistema de arquivos
 * e {@link Comparable} para permitir a ordenação padrão (por nome).
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.0
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

    /**
     * Construtor padrão.
     * Inicializa uma lista com valores padrão (IDs -1, strings vazias, datas nulas).
     */
    public Lista() {
        this(-1, -1, "", "", null, null, "");
    }

    /**
     * Construtor completo.
     *
     * @param id O ID único da lista (geralmente definido pelo CRUD).
     * @param idUsuario O ID do usuário proprietário da lista.
     * @param nome O nome da lista.
     * @param descricao Uma breve descrição da lista.
     * @param dataCriacao A data em que a lista foi criada.
     * @param dataLimite A data limite opcional para a lista (pode ser null).
     * @param codigo O código compartilhável único da lista.
     */
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

    /**
     * Obtém o ID único da entidade.
     *
     * @return O ID (inteiro) da lista.
     */
    @Override
    public int getID() {
        return this.id;
    }

    /**
     * Define o ID único da entidade.
     *
     * @param id O novo ID (inteiro) da lista.
     */
    @Override
    public void setID(int id) {
        this.id = id;
    }
    
    /**
     * Obtém o ID do usuário proprietário da lista.
     *
     * @return O ID (inteiro) do usuário.
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * Define o ID do usuário proprietário da lista.
     *
     * @param idUsuario O novo ID (inteiro) do usuário.
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtém o nome da lista.
     *
     * @return O nome (String) da lista.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome da lista.
     *
     * @param nome O novo nome (String) da lista.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém a descrição da lista.
     *
     * @return A descrição (String) da lista.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Define a descrição da lista.
     *
     * @param descricao A nova descrição (String) da lista.
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Obtém a data de criação da lista.
     *
     * @return A data de criação (LocalDate).
     */
    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    /**
     * Define a data de criação da lista.
     *
     * @param dataCriacao A nova data de criação (LocalDate).
     */
    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    /**
     * Obtém a data limite da lista.
     *
     * @return A data limite (LocalDate), ou {@code null} se não houver.
     */
    public LocalDate getDataLimite() {
        return dataLimite;
    }

    /**
     * Define a data limite da lista.
     *
     * @param dataLimite A nova data limite (LocalDate), ou {@code null}.
     */
    public void setDataLimite(LocalDate dataLimite) {
        this.dataLimite = dataLimite;
    }

    /**
     * Obtém o código compartilhável da lista.
     *
     * @return O código (String) compartilhável.
     */
    public String getCodigoCompartilhavel() {
        return codigoCompartilhavel;
    }

    /**
     * Define o código compartilhável da lista.
     *
     * @param codigoCompartilhavel O novo código (String) compartilhável.
     */
    public void setCodigoCompartilhavel(String codigoCompartilhavel) {
        this.codigoCompartilhavel = codigoCompartilhavel;
    }

    // ------------------------------------------ Métodos da Interface Entidade ------------------------------------------

    /**
     * Serializa o objeto Lista para um array de bytes, para persistência em
     * arquivo.
     * Trata a data limite nula escrevendo um booleano de controle.
     *
     * @return Um array de bytes ({@code byte[]}) representando o objeto.
     * @throws Exception se ocorrer um erro durante a escrita nos streams.
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
     * Desserializa um array de bytes, preenchendo os atributos deste objeto
     * Lista.
     * Lê um booleano de controle para tratar corretamente a data limite (nula
     * ou não).
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

    /**
     * Gera uma representação em String do objeto Lista, útil para depuração.
     *
     * @return Uma String formatada com todos os atributos da lista.
     */
    @Override
    public String toString() {
        return "Lista [ID=" + id + ", ID do Usuário=" + idUsuario + ", Nome=" + nome + ", Descrição=" + descricao
                + ", Data de Criação=" + dataCriacao + ", Data Limite=" + dataLimite + ", Código=" + codigoCompartilhavel + "]";
    }
    
    /**
     * Compara esta lista com outra pelo nome, para ordenação alfabética.
     * A comparação ignora a diferença between maiúsculas e minúsculas.
     *
     * @param outraLista A outra {@link Lista} a ser comparada.
     * @return Um valor negativo se o nome desta lista vier antes,
     * um valor positivo se vier depois, e zero se forem iguais.
     */
    @Override
    public int compareTo(Lista outraLista) {
        return this.nome.compareToIgnoreCase(outraLista.getNome());
    }
}
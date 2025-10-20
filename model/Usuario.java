package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.security.MessageDigest;
import java.util.Base64;
import bib.Entidade;

/**
 * Representa a entidade "Usuário" no sistema.
 * Esta classe armazena dados cadastrais, credenciais seguras (hash de senha)
 * e informações para recuperação de conta.
 * Implementa a interface {@link Entidade} para permitir a
 * serialização/desserialização personalizada para o sistema de arquivos.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.0
 */
public class Usuario implements Entidade {

    // --- Atributos ---
    private int id;
    private String nome;
    private String email;
    private String hashSenha; // Armazena o hash SHA-256 da senha
    private String perguntaSecreta;
    private String respostaSecreta;

    // --- Construtores ---
    
    /**
     * Construtor padrão.
     * Inicializa um usuário com valores padrão (ID -1, strings vazias).
     */
    public Usuario() {
        this(-1, "", "", "", "", "");
    }

    /**
     * Construtor principal.
     * Recebe a senha em texto plano (senhaPlana) e armazena internamente
     * o seu hash SHA-256.
     *
     * @param id O ID único do usuário (geralmente definido pelo CRUD).
     * @param nome O nome completo do usuário.
     * @param email O e-mail do usuário (usado para login).
     * @param senhaPlana A senha em texto plano (não é armazenada).
     * @param pergunta A pergunta secreta para recuperação de conta.
     * @param resposta A resposta para a pergunta secreta.
     */
    public Usuario(int id, String nome, String email, String senhaPlana, String pergunta, String resposta) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perguntaSecreta = pergunta;
        this.respostaSecreta = resposta;
        
        if (senhaPlana != null && !senhaPlana.isEmpty()) {
            try {
                this.hashSenha = gerarHash(senhaPlana);
            } catch (Exception e) {
                System.err.println("ERRO ao gerar hash da senha.");
                this.hashSenha = "";
            }
        } else {
            this.hashSenha = "";
        }
    }

    // --- Getters e Setters ---

    /**
     * Obtém o ID único da entidade.
     *
     * @return O ID (inteiro) do usuário.
     */
    @Override
    public int getID() { return this.id; }

    /**
     * Define o ID único da entidade.
     *
     * @param id O novo ID (inteiro) do usuário.
     */
    @Override
    public void setID(int id) { this.id = id; }

    /**
     * Obtém o nome do usuário.
     *
     * @return O nome (String) do usuário.
     */
    public String getNome() { return this.nome; }
    
    /**
     * Define o nome do usuário.
     *
     * @param nome O novo nome (String) do usuário.
     */
    public void setNome(String nome) { this.nome = nome; }

    /**
     * Obtém o e-mail do usuário.
     *
     * @return O e-mail (String) do usuário.
     */
    public String getEmail() { return this.email; }
    
    /**
     * Define o e-mail do usuário.
     *
     * @param email O novo e-mail (String) do usuário.
     */
    public void setEmail(String email) { this.email = email; }
    
    /**
     * Obtém o hash da senha armazenado.
     *
     * @return O hash da senha (String codificada em Base64).
     */
    public String getHashSenha() { return this.hashSenha; }
    
    /**
     * Obtém a pergunta secreta do usuário.
     *
     * @return A pergunta secreta (String).
     */
    public String getPerguntaSecreta() { return this.perguntaSecreta; }
    
    /**
     * Define a pergunta secreta do usuário.
     *
     * @param perguntaSecreta A nova pergunta secreta (String).
     */
    public void setPerguntaSecreta(String perguntaSecreta) { this.perguntaSecreta = perguntaSecreta; }

    /**
     * Obtém a resposta secreta do usuário.
     *
     * @return A resposta secreta (String).
     */
    public String getRespostaSecreta() { return this.respostaSecreta; }
    
    /**
     * Define a resposta secreta do usuário.
     *
     * @param respostaSecreta A nova resposta secreta (String).
     */
    public void setRespostaSecreta(String respostaSecreta) { this.respostaSecreta = respostaSecreta; }

    // --- Métodos de Negócio ---

    /**
     * Gera um hash SHA-256 para uma senha em texto plano.
     *
     * @param senha A senha em texto plano a ser "hasheada".
     * @return Uma {@code String} representando o hash da senha (codificado em
     * Base64).
     * @throws Exception se o algoritmo SHA-256 não for encontrado ou
     * ocorrer um erro de codificação.
     */
    public static String gerarHash(String senha) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(senha.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hashBytes);
    }
    
    /**
     * Compara uma senha em texto plano com o hash armazenado neste objeto.
     *
     * @param senhaPlana A senha em texto plano a ser verificada.
     * @return {@code true} se a senha corresponder ao hash armazenado,
     * {@code false} caso contrário.
     */
    public boolean validarSenha(String senhaPlana) {
        if (this.hashSenha == null || senhaPlana == null || senhaPlana.isEmpty()) {
            return false;
        }
        try {
            String hashDaSenhaPlana = gerarHash(senhaPlana);
            return this.hashSenha.equals(hashDaSenhaPlana);
        } catch (Exception e) {
            return false;
        }
    }

    // --- Métodos da Interface Entidade ---

    /**
     * Gera uma representação em String do objeto Usuário (ID, Nome, E-mail).
     * Não inclui dados sensíveis como hash de senha.
     *
     * @return Uma String formatada com os dados públicos do usuário.
     */
    @Override
    public String toString() {
        return "ID: " + this.id +
               "\nNome: " + this.nome +
               "\nE-mail: " + this.email;
    }

    /**
     * Serializa o objeto Usuário para um array de bytes, para persistência em
     * arquivo.
     * Inclui todos os atributos, inclusive o hash da senha.
     *
     * @return Um array de bytes ({@code byte[]}) representando o objeto.
     * @throws Exception se ocorrer um erro durante a escrita nos streams.
     */
    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.email);
        dos.writeUTF(this.hashSenha); // Guarda o hash
        dos.writeUTF(this.perguntaSecreta);
        dos.writeUTF(this.respostaSecreta);
        return baos.toByteArray();
    }

    /**
     * Desserializa um array de bytes, preenchendo os atributos deste objeto
     * Usuário.
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
        this.nome = dis.readUTF();
        this.email = dis.readUTF();
        this.hashSenha = dis.readUTF(); // Lê o hash
        this.perguntaSecreta = dis.readUTF();
        this.respostaSecreta = dis.readUTF();
    }
}
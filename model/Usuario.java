package model;

import java.io.*;
import bib.Entidade;
import java.security.MessageDigest;
import java.util.Base64;

public class Usuario implements Entidade {

    // --- Atributos ---
    private int id;
    private String nome;
    private String email;
    private String hashSenha; // O atributo volta a ser para o hash
    private String perguntaSecreta;
    private String respostaSecreta;

    // --- Construtores ---
    public Usuario() {
        this(-1, "", "", "", "", "");
    }

    /**
     * Construtor principal. Recebe a senha em texto plano e calcula o hash.
     */
    public Usuario(int id, String nome, String email, String senhaPlana, String pergunta, String resposta) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perguntaSecreta = pergunta;
        this.respostaSecreta = resposta;
        
        // Se a senha for fornecida, calcula e armazena o seu hash
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

    @Override
    public int getID() { return this.id; }

    @Override
    public void setID(int id) { this.id = id; }

    public String getNome() { return this.nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return this.email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getHashSenha() { return this.hashSenha; }
    
    public String getPerguntaSecreta() { return this.perguntaSecreta; }
    public void setPerguntaSecreta(String perguntaSecreta) { this.perguntaSecreta = perguntaSecreta; }

    public String getRespostaSecreta() { return this.respostaSecreta; }
    public void setRespostaSecreta(String respostaSecreta) { this.respostaSecreta = respostaSecreta; }

    // --- Métodos de Negócio ---

    /**
     * Gera um hash SHA-256 para uma senha.
     * @param senha A senha em texto plano.
     * @return Uma string representando o hash da senha.
     */
    public static String gerarHash(String senha) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(senha.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(hashBytes);
    }
    
    /**
     * Compara uma senha em texto plano com o hash armazenado.
     * @param senhaPlana A senha a ser verificada.
     * @return true se a senha corresponder, false caso contrário.
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

    @Override
    public String toString() {
        return "ID: " + this.id +
               "\nNome: " + this.nome +
               "\nE-mail: " + this.email;
    }

    @Override
    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.email);
        dos.writeUTF(this.hashSenha); // Guarda o hash da senha no ficheiro
        dos.writeUTF(this.perguntaSecreta);
        dos.writeUTF(this.respostaSecreta);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.email = dis.readUTF();
        this.hashSenha = dis.readUTF(); // Lê o hash da senha do ficheiro
        this.perguntaSecreta = dis.readUTF();
        this.respostaSecreta = dis.readUTF();
    }
}


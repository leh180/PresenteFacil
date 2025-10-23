package model;

import bib.RegistroHashExtensivel;
import java.io.*;

// Esta classe armazena o par (email, id) e será o objeto guardado no nosso índice secundário.
public class ParEmailId implements RegistroHashExtensivel {

    private String email;
    private int id;
    // Tamanho fixo em bytes. É crucial para que a HashExtensivel funcione corretamente.
    // O cálculo é: (4 bytes para o ID do tipo int) + (62 bytes reservados para a String do e-mail com seu controle).
    public final short SIZE = 66; 

    public ParEmailId() {
        this.email = "";
        this.id = -1;
    }

    public ParEmailId(String email, int id) {
        this.email = email;
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public int hashCode() {
        // O hashCode do objeto é o hashCode da String do e-mail.
        // É assim que a HashExtensivel vai encontrar o registro no cesto correto.
        return this.email.hashCode();
    }

    @Override
    public short size() {
        return SIZE;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(this.email);
        dos.writeInt(this.id);
        
        // Pega o resultado da escrita
        byte[] resultado = baos.toByteArray();
        
        // Cria um array final com o tamanho fixo e copia os bytes para ele.
        // Isso garante que todos os registros no arquivo de índice tenham o mesmo tamanho,
        // preenchendo com zeros (padding) se o resultado for menor que o tamanho fixo.
        byte[] resultadoFinal = new byte[SIZE];
        System.arraycopy(resultado, 0, resultadoFinal, 0, resultado.length);

        return resultadoFinal;
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.email = dis.readUTF();
        this.id = dis.readInt();
    }
    
    @Override
    public String toString() {
        return "Par(email=" + email + ", id=" + id + ")";
    }
}


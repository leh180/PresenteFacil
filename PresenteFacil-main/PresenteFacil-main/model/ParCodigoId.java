package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import bib.RegistroHashExtensivel;

public class ParCodigoId implements RegistroHashExtensivel {

    private String codigo;
    private int id;
    private final short TAMANHO = 24; // 10 chars (2 bytes cada) + 4 bytes (int)

    public ParCodigoId() {
        this("", -1);
    }

    public ParCodigoId(String codigo, int id) {
        this.codigo = codigo;
        this.id = id;
    }

    public String getCodigo() { return this.codigo; }
    public int getId() { return this.id; }

    @Override
    public int hashCode() {
        return this.codigo.hashCode();
    }

    @Override
    public short size() {
        return TAMANHO;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        // Escreve a string com tamanho fixo para garantir consistência
        dos.writeChars(String.format("%-10.10s", this.codigo));
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        char[] codigoChars = new char[10];
        for (int i = 0; i < 10; i++) {
            codigoChars[i] = dis.readChar();
        }
        this.codigo = new String(codigoChars).trim();
    }
}


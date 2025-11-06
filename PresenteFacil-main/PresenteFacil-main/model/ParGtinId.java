package model;

import bib.RegistroHashExtensivel;
import java.io.*;

// Esta classe armazena o par (gtin, id) e será o objeto guardado no nosso índice secundário.
public class ParGtinId implements RegistroHashExtensivel {

    private String gtin;
    private int id;
    // Tamanho fixo em bytes. É crucial para que a HashExtensivel funcione
    // corretamente.
    // O cálculo é: (4 bytes para o ID do tipo int) + (40 bytes para a String GTIN
    // de 20 caracteres).
    public final short SIZE = 44;

    public ParGtinId() {
        this.gtin = "";
        this.id = -1;
    }

    public ParGtinId(String gtin, int id) {
        this.gtin = gtin;
        this.id = id;
    }

    @Override
    public int hashCode() {
        return this.gtin.hashCode();
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    public String getGtin() {
        return this.gtin;
    }

    @Override
    public short size() {
        return SIZE;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        // Escreve a string com tamanho fixo para garantir consistência
        dos.writeChars(String.format("%-20.20s", this.gtin));
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        char[] gtinChars = new char[20];
        for (int i = 0; i < 20; i++) {
            gtinChars[i] = dis.readChar();
        }
        this.gtin = new String(gtinChars).trim();
    }

    @Override
    public String toString() {
        return "Par(gtin=" + gtin + ", id=" + id + ")";
    }
}

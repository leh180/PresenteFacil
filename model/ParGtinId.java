package model;

import bib.RegistroHashExtensivel;
import java.io.*;

// Esta classe armazena o par (gtin, id) e será o objeto guardado no nosso índice secundário.
public class ParGtinId implements RegistroHashExtensivel {

    private String gtin;
    private int id;
    // Tamanho fixo em bytes. É crucial para que a HashExtensivel funcione corretamente.
    // O cálculo é: (4 bytes para o ID do tipo int) + (4 bytes reservados para o gtin do tipo long).
    public final short SIZE = 8; 

    public ParGtinId(){
        this.gtin = "";
        this.id = -1;
    }

    public ParGtinId( String gtin,int id){
        this.gtin = gtin;
        this.id = id;
    }

    public int codigoHash() {
        // O hashCode do objeto é o hashCode da o long gtin
        // É assim que a HashExtensivel vai encontrar o registro no cesto correto.
        // return (int) (this.gtin); 
        return this.gtin.hashCode();
    }

    public void setID(int id){this.id = id;}
    public int getID(){return  this.id;}

    public void setGtin(String gtin){this.gtin = gtin;}
    public String getGtin(){return this.gtin;}

    @Override
    public short size() {
        return SIZE;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(this.gtin);
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
        this.gtin = dis.readUTF();
        this.id = dis.readInt();
    }
    
    @Override
    public String toString() {
        return "Par(gtin=" + gtin + ", id=" + id + ")";
    }
}

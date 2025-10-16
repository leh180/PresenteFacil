package bib;

import java.io.*;
import java.lang.reflect.Constructor;

public class Arquivo<T extends Entidade> {

    RandomAccessFile arquivo;
    HashExtensivel<ParIDEndereco> indiceDireto;
    String nomeArquivo;
    Constructor<T> construtor;
    int TAM_CABECALHO = 12;

    public Arquivo(String nome, Constructor<T> construtor) throws Exception {
        this.nomeArquivo = nome;
        this.construtor = construtor;
        File d = new File("./dados");
        if (!d.exists()) {
            d.mkdir();
        }
        this.arquivo = new RandomAccessFile("./dados/" + nomeArquivo + ".db", "rw");
        if (arquivo.length() < TAM_CABECALHO) {
            arquivo.writeInt(0); // último ID
            arquivo.writeLong(-1); // ponteiro para a lista de vazios
        }
        indiceDireto = new HashExtensivel<>(ParIDEndereco.class.getConstructor(), 3, "./dados/" + nomeArquivo + ".d.db",
                "./dados/" + nomeArquivo + ".c.db");
    }

    public int create(T entidade) throws Exception {

        // Obtém o novo ID
        arquivo.seek(0);
        int novoId = arquivo.readInt() + 1;
        arquivo.seek(0);
        arquivo.writeInt(novoId);
        entidade.setID(novoId);

        // Grava o registro no fim do arquivo
        byte[] vb = entidade.toByteArray();
        int tam = vb.length;
        long endereco = buscaVazio(tam);
        if (endereco == -1) {
            arquivo.seek(arquivo.length());
            endereco = arquivo.getFilePointer();
            arquivo.writeByte(' ');
            arquivo.writeShort(tam);
            arquivo.write(vb);
        } else {
            arquivo.seek(endereco);
            arquivo.writeByte(' ');
            arquivo.skipBytes(2);
            arquivo.write(vb);
        }
        indiceDireto.create(new ParIDEndereco(novoId, endereco));
        return novoId;
    }

    public T read(int id) throws Exception {
        ParIDEndereco pie = indiceDireto.read(id);
        if (pie == null)
            return null;

        long endereco = pie.getEndereco();
        arquivo.seek(endereco);
        byte lapide = arquivo.readByte();
        int tam = arquivo.readShort();

        if (lapide == ' ') {
            byte[] vb = new byte[tam];
            arquivo.read(vb);

            T entidade = construtor.newInstance();
            entidade.fromByteArray(vb);
            if (entidade.getID() == id)
                return entidade;
        }
        return null;
    }

    public boolean update(T novaEntidade) throws Exception {
        ParIDEndereco pie = indiceDireto.read(novaEntidade.getID());
        if (pie == null)
            return false;
        long endereco = pie.getEndereco();
        arquivo.seek(endereco);

        byte lapide = arquivo.readByte();
        int tam = arquivo.readShort();

        if (lapide == ' ') {
            byte[] vb = new byte[tam];
            arquivo.read(vb);

            T entidade = construtor.newInstance();
            entidade.fromByteArray(vb);
            if (entidade.getID() == novaEntidade.getID()) {

                byte[] vb2 = novaEntidade.toByteArray();
                int tam2 = vb2.length;

                if (tam2 <= tam) {
                    arquivo.seek(endereco + 3);
                    arquivo.write(vb2);
                } else {
                    arquivo.seek(endereco);
                    arquivo.writeByte('*');
                    insereVazio(endereco, tam);

                    long novaPos = buscaVazio(tam2);
                    if (novaPos == -1) {
                        arquivo.seek(arquivo.length());
                        novaPos = arquivo.getFilePointer();
                        arquivo.writeByte(' ');
                        arquivo.writeShort(tam2);
                        arquivo.write(vb2);
                    } else {
                        arquivo.seek(novaPos);
                        arquivo.writeByte(' ');
                        arquivo.skipBytes(2);
                        arquivo.write(vb2);
                    }
                    indiceDireto.update(new ParIDEndereco(novaEntidade.getID(), novaPos));
                }
                return true;
            }
        }
        return false;
    }

    public boolean delete(int id) throws Exception {
        ParIDEndereco pie = indiceDireto.read(id);
        if (pie == null)
            return false;
        long endereco = pie.getEndereco();
        arquivo.seek(endereco);
        byte lapide = arquivo.readByte();
        int tam = arquivo.readShort();

        if (lapide == ' ') {
            byte[] vb = new byte[tam];
            arquivo.read(vb);
            T entidade = construtor.newInstance();
            entidade.fromByteArray(vb);
            if (entidade.getID() == id) {
                arquivo.seek(endereco);
                arquivo.writeByte('*');
                insereVazio(endereco, tam);
                indiceDireto.delete(id);
                return true;
            }
        }
        return false;
    }

    public void close() throws Exception {
        arquivo.close();
    }

    public void insereVazio(long enderecoEspaco, int tamanhoEspaco) throws Exception {
        long anterior = 4;
        long endereco;
        long proximo = -1;
        short tamanho;

        arquivo.seek(anterior); // cabeça da lista
        endereco = arquivo.readLong();

        if (endereco == -1) {
            arquivo.seek(anterior);
            arquivo.writeLong(enderecoEspaco);
            arquivo.seek(enderecoEspaco + 3);
            arquivo.writeLong(-1);
            return;
        } else {
            do {
                arquivo.seek(endereco + 1); // pula o lápide
                tamanho = arquivo.readShort();
                proximo = arquivo.readLong();
                if (tamanhoEspaco < tamanho) {
                    if (anterior == 4)
                        arquivo.seek(anterior);
                    else
                        arquivo.seek(anterior + 3);
                    arquivo.writeLong(enderecoEspaco);
                    arquivo.seek(enderecoEspaco + 3);
                    arquivo.writeLong(endereco);
                    return;
                }
                anterior = endereco;
                endereco = proximo;
            } while (endereco == -1);
            return;
        }

    }

    public long buscaVazio(int tamanhoEspacoNecessario) throws Exception {
        long anterior = 4;
        long endereco;
        long proximo = -1;
        short tamanho;

        arquivo.seek(anterior); // cabeça da lista
        endereco = arquivo.readLong();

        while (endereco != -1) {
            arquivo.seek(endereco + 1);
            tamanho = arquivo.readShort();
            proximo = arquivo.readLong();
            if (tamanhoEspacoNecessario <= tamanho) {
                if (anterior == 4)
                    arquivo.seek(4);
                else
                    arquivo.seek(anterior + 3);
                arquivo.writeLong(proximo);
                return endereco;
            }
            anterior = endereco;
            endereco = proximo;
        }

        return -1;
    }

}

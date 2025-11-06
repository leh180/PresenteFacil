package model;

import bib.Arquivo;
import bib.ElementoLista;
import bib.HashExtensivel;
import bib.ListaInvertida;
import java.io.File;
import java.util.*;

public class CRUDProduto extends Arquivo<Produto> {

    private HashExtensivel<ParGtinId> indiceGtin;
    private ListaInvertida indiceNomes;
    private IndiceInvertido indiceInvertido; // ✅ novo: para TF-IDF

    public CRUDProduto() throws Exception {
        super("produtos", Produto.class.getConstructor());

        File d = new File("data");
        if (!d.exists())
            d.mkdir();

        indiceNomes = new ListaInvertida(4, "data/nomes.dic", "data/nomes.bloc");

        indiceGtin = new HashExtensivel<>(
                ParGtinId.class.getConstructor(),
                4,
                "data/produtos_gtin.diretorio.idx",
                "data/produtos_gtin.cestos.idx");

        // ✅ Instancia o índice invertido com suporte a TF-IDF
        indiceInvertido = new IndiceInvertido(indiceNomes, this);
    }

    @Override
    public int create(Produto produto) throws Exception {
        int id = super.create(produto);
        produto.setID(id);

        // Índice secundário (GTIN)
        indiceGtin.create(new ParGtinId(produto.getGtin(), id));

        // Índice invertido (nome)
        String[] palavras = produto.getNome().toLowerCase().split("\\s+");
        for (String p : palavras) {
            if (p.length() > 0)
                indiceNomes.create(p, new ElementoLista(id, 1));
        }

        return id;
    }

    public Produto readByGtin(String gtin) throws Exception {
        ParGtinId par = indiceGtin.read(gtin.hashCode());
        if (par != null && par.getGtin().equals(gtin)) {
            return super.read(par.getID());
        }
        return null;
    }

    public Produto readByID(int id) throws Exception {
        return super.read(id);
    }

    @Override
    public boolean update(Produto novoProduto) throws Exception {
        Produto produtoAntigo = super.read(novoProduto.getID());
        if (produtoAntigo == null) {
            return false;
        }

        boolean success = super.update(novoProduto);

        if (success) {
            String gtinAntigo = produtoAntigo.getGtin();
            String gtinNovo = novoProduto.getGtin();
            boolean eraAtivo = produtoAntigo.isAtivo();
            boolean ehAtivo = novoProduto.isAtivo();

            // --- Atualização do índice de GTIN ---
            if (!gtinAntigo.equals(gtinNovo)) {
                indiceGtin.delete(gtinAntigo.hashCode());
                indiceGtin.create(new ParGtinId(gtinNovo, novoProduto.getID()));
            }

            if (eraAtivo && !ehAtivo) {
                indiceGtin.delete(gtinAntigo.hashCode());
            } else if (!eraAtivo && ehAtivo) {
                indiceGtin.create(new ParGtinId(gtinNovo, novoProduto.getID()));
            }

            // --- Atualização do índice invertido de nomes ---
            if (!produtoAntigo.getNome().equalsIgnoreCase(novoProduto.getNome())) {
                // Remove as palavras antigas
                String[] antigas = produtoAntigo.getNome().toLowerCase().split("\\s+");
                for (String p : antigas) {
                    if (p.length() > 0)
                        indiceNomes.delete(p, novoProduto.getID());
                }

                // Adiciona as novas palavras
                String[] novas = novoProduto.getNome().toLowerCase().split("\\s+");
                for (String p : novas) {
                    if (p.length() > 0)
                        indiceNomes.create(p, new ElementoLista(novoProduto.getID(), 1));
                }
            }

            return true;
        }
        return false;
    }

    public boolean delete(int id) throws Exception {
        Produto produto = super.read(id);
        if (produto == null || !produto.isAtivo())
            return false;

        // Remove do índice secundário e invertido
        indiceGtin.delete(produto.getGtin().hashCode());

        String[] palavras = produto.getNome().toLowerCase().split("\\s+");
        for (String p : palavras) {
            if (p.length() > 0)
                indiceNomes.delete(p, id);
        }

        // Inativa no arquivo principal
        produto.setAtivo(false);
        return super.update(produto);
    }

    /**
     * Busca produtos por termos, usando o índice invertido com TF-IDF.
     * Retorna produtos ordenados por relevância (peso calculado).
     */
    public List<Produto> buscarPorTermos(List<String> termos) throws Exception {
        if (termos == null || termos.isEmpty())
            return new ArrayList<>();

        String consulta = String.join(" ", termos);

        // ✅ Usa o índice invertido com IDF
        List<Map.Entry<Integer, Double>> ranking = indiceInvertido.buscar(consulta);

        List<Produto> resultados = new ArrayList<>();
        for (Map.Entry<Integer, Double> entrada : ranking) {
            Produto p = super.read(entrada.getKey());
            if (p != null && p.isAtivo())
                resultados.add(p);
        }

        return resultados;
    }

    public ArrayList<Produto> readAllAtivos() throws Exception {
        ArrayList<Produto> produtosAtivos = new ArrayList<>();
        arquivo.seek(TAM_CABECALHO);

        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short tam = arquivo.readShort();
            byte[] dados = new byte[tam];
            arquivo.read(dados);

            if (lapide == ' ') {
                Produto p = (Produto) construtor.newInstance();
                p.fromByteArray(dados);
                if (p.isAtivo()) {
                    produtosAtivos.add(p);
                }
            }
        }
        return produtosAtivos;
    }

    public int totalRegistros() throws Exception {
        int count = 0;
        arquivo.seek(TAM_CABECALHO);

        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short tamanho = arquivo.readShort();

            if (lapide == ' ') {
                count++;
            }

            arquivo.skipBytes(tamanho);
        }

        return count;
    }

    public ArrayList<Produto> readAll() throws Exception {
        ArrayList<Produto> todos = new ArrayList<>();
        arquivo.seek(TAM_CABECALHO);

        while (arquivo.getFilePointer() < arquivo.length()) {
            byte lapide = arquivo.readByte();
            short tam = arquivo.readShort();
            byte[] dados = new byte[tam];
            arquivo.read(dados);

            if (lapide == ' ') {
                Produto p = (Produto) construtor.newInstance();
                p.fromByteArray(dados);
                todos.add(p);
            }
        }
        return todos;
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}

package model;

import bib.ListaInvertida;
import bib.ElementoLista;

import java.text.Normalizer;
import java.util.*;
/**
 * IndiceInvertido que calcula TF x IDF usando a ListaInvertida (bib.ListaInvertida)
 * e o total de registros obtido através do CRUDProduto (herda Arquivo.totalRegistros()).
 *
 * Observações:
 * - Usa ListaInvertida.read(termo) que retorna ElementoLista[] (cada ElementoLista tem id e frequencia).
 * - Usa ListaInvertida.prepararTermos(consulta) para normalizar e tokenizar a consulta.
 */
public class IndiceInvertido {

    private ListaInvertida listaInvertida;
    private CRUDProduto crudProduto;
    private Set<String> stopWords;

    public IndiceInvertido(ListaInvertida listaInvertida, CRUDProduto crudProduto) {
        this.listaInvertida = listaInvertida;
        this.crudProduto = crudProduto;
        this.stopWords = carregarStopWordsBasicas();
    }

    /**
     * Retorna lista de pares (idProduto, peso) ordenada por peso decrescente.
     * Peso = soma sobre termos da (TF(doc,term) * IDF(term)).
     */
    public List<Map.Entry<Integer, Double>> buscar(String consulta) throws Exception {
        if (consulta == null || consulta.trim().isEmpty())
            return Collections.emptyList();

        // 1) tokeniza / normaliza a consulta (usa o util que já existe)
        List<String> termos = ListaInvertida.prepararTermos(consulta);

        // 1.a) remove stop words simples (opcional; enunciado pede stop words)
        List<String> termosFiltrados = new ArrayList<>();
        for (String t : termos) {
            if (!t.isBlank() && !stopWords.contains(t))
                termosFiltrados.add(t);
        }
        if (termosFiltrados.isEmpty()) return Collections.emptyList();

        // 2) total de documentos (produtos) — assume que CRUDProduto/Arquivo implementa totalRegistros()
        int totalProdutos = crudProduto.totalRegistros(); // implemente totalRegistros() em Arquivo ou CRUDProduto

        // 3) acumula pesos TF*IDF por id
        Map<Integer, Double> acumulador = new HashMap<>();

        for (String termo : termosFiltrados) {
            // recupera lista do termo (ElementoLista[] com id e frequencia (TF))
            ElementoLista[] lista = listaInvertida.read(termo);

            int df = lista.length; // nº de documentos que contêm o termo
            if (df == 0) continue;

            // calcula IDF: log(N/df) + 1
            double idf = Math.log((double) totalProdutos / (double) df) + 1.0;

            // para cada elemento (id, tf) multiplica e soma no acumulador
            for (ElementoLista el : lista) {
                int id = el.getId();
                double tf = el.getFrequencia(); // conforme sua classe ElementoLista
                double peso = tf * idf;
                acumulador.put(id, acumulador.getOrDefault(id, 0.0) + peso);
            }
        }

        // 4) ordenar por peso decrescente e retornar lista de entradas
        List<Map.Entry<Integer, Double>> ordenado = new ArrayList<>(acumulador.entrySet());
        ordenado.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return ordenado;
    }

    // Stop words básicas (complemente se quiser)
    private Set<String> carregarStopWordsBasicas() {
        return new HashSet<>(Arrays.asList(
                "de", "da", "do", "e", "a", "o", "as", "os",
                "para", "por", "com", "sem", "um", "uma", "em",
                "no", "na", "nos", "nas", "ao", "aos", "dos", "das",
                "que", "se", "o", "os", "é"
        ));
    }
}

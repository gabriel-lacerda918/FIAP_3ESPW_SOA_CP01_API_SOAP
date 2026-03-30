package br.com.fiap.cliente;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Extrai valores de tags específicas sem dependência de stubs gerados.
 */
public class XmlParser {

    private XmlParser() {}

    /**
     * Extrai o texto de uma tag pelo nome (primeiro resultado).
     */
    public static String extrairTag(String xml, String tagName) {
        try {
            Document doc = parseXml(xml);
            NodeList nodes = doc.getElementsByTagNameNS("*", tagName);
            if (nodes.getLength() == 0) {
                nodes = doc.getElementsByTagName(tagName);
            }
            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent().trim();
            }
        } catch (Exception e) {
            // ignorar erros de parsing silenciosamente para UI limpa
        }
        return "";
    }

    /**
     * Extrai todos os textos de uma tag (varios resultados).
     */
    public static List<String> extrairTags(String xml, String tagName) {
        List<String> resultados = new ArrayList<>();
        try {
            Document doc = parseXml(xml);
            NodeList nodes = doc.getElementsByTagNameNS("*", tagName);
            if (nodes.getLength() == 0) {
                nodes = doc.getElementsByTagName(tagName);
            }
            for (int i = 0; i < nodes.getLength(); i++) {
                resultados.add(nodes.item(i).getTextContent().trim());
            }
        } catch (Exception e) {
            // ignorar
        }
        return resultados;
    }

    /**
     * Extrai os dados de cada elemento <livros> como um objeto simples formatado.
     */
    public static List<String> extrairLivros(String xml) {
        List<String> livros = new ArrayList<>();
        try {
            Document doc = parseXml(xml);
            NodeList nodes = doc.getElementsByTagNameNS("*", "livros");
            if (nodes.getLength() == 0) nodes = doc.getElementsByTagName("livros");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                String isbn = getChildText(el, "isbn");
                String titulo = getChildText(el, "titulo");
                String autor = getChildText(el, "autor");
                String ano = getChildText(el, "anoPublicacao");
                String disp = getChildText(el, "exemplaresDisponiveis");
                String total = getChildText(el, "totalExemplares");

                livros.add(String.format("  ISBN: %-20s | %-45s | Autor: %-25s | Ano: %s | Disponíveis: %s/%s",
                        isbn, titulo, autor, ano, disp, total));
            }
        } catch (Exception e) {
            livros.add("Erro ao parsear resposta: " + e.getMessage());
        }
        return livros;
    }

    /**
     * Extrai os dados de cada elemento <emprestimos> formatado.
     */
    public static List<String> extrairEmprestimos(String xml) {
        List<String> lista = new ArrayList<>();
        try {
            Document doc = parseXml(xml);
            NodeList nodes = doc.getElementsByTagNameNS("*", "emprestimos");
            if (nodes.getLength() == 0) nodes = doc.getElementsByTagName("emprestimos");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                String id = getChildText(el, "id");
                String livro = getChildText(el, "tituloLivro");
                String usuario = getChildText(el, "nomeUsuario");
                String devol = getChildText(el, "dataDevolucaoPrevista");
                String status = getChildText(el, "status");

                lista.add(String.format("  ID: %-12s | Livro: %-40s | Usuário: %-20s | Devolução: %s | Status: %s",
                        id, livro, usuario, devol, status));
            }
        } catch (Exception e) {
            lista.add("Erro ao parsear empréstimos: " + e.getMessage());
        }
        return lista;
    }

    private static String getChildText(Element parent, String childName) {
        NodeList list = parent.getElementsByTagName(childName);
        if (list.getLength() > 0) return list.item(0).getTextContent().trim();
        return "-";
    }

    private static Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }
}

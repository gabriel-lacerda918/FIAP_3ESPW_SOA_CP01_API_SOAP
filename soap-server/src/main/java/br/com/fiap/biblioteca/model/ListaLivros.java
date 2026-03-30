package br.com.fiap.biblioteca.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper para lista de livros - necessário para serialização JAXB de listas.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ListaLivros {

    private List<Livro> livros = new ArrayList<>();
    private int total;
    private String mensagem;

    // Construtor padrão obrigatório para JAXB
    public ListaLivros() {}

    public ListaLivros(List<Livro> livros) {
        this.livros = livros;
        this.total = livros.size();
    }

    public ListaLivros(List<Livro> livros, String mensagem) {
        this.livros = livros;
        this.total = livros.size();
        this.mensagem = mensagem;
    }

    // Getters e Setters
    public List<Livro> getLivros() { return livros; }
    public void setLivros(List<Livro> livros) {
        this.livros = livros;
        this.total = livros != null ? livros.size() : 0;
    }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}

package br.com.fiap.biblioteca.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

/**
 * Modelo que representa um Livro na biblioteca.
 * A anotação @XmlRootElement permite a serialização/deserialização XML via JAXB.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Livro {

    private String isbn;
    private String titulo;
    private String autor;
    private String genero;
    private int anoPublicacao;
    private boolean disponivel;
    private int totalExemplares;
    private int exemplaresDisponiveis;

    // Construtor padrão obrigatório para JAXB
    public Livro() {}

    public Livro(String isbn, String titulo, String autor, String genero,
                 int anoPublicacao, int totalExemplares) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.anoPublicacao = anoPublicacao;
        this.totalExemplares = totalExemplares;
        this.exemplaresDisponiveis = totalExemplares;
        this.disponivel = totalExemplares > 0;
    }

    // Getters e Setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    public int getTotalExemplares() { return totalExemplares; }
    public void setTotalExemplares(int totalExemplares) { this.totalExemplares = totalExemplares; }

    public int getExemplaresDisponiveis() { return exemplaresDisponiveis; }
    public void setExemplaresDisponiveis(int exemplaresDisponiveis) {
        this.exemplaresDisponiveis = exemplaresDisponiveis;
        this.disponivel = exemplaresDisponiveis > 0;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%d) | Disponíveis: %d/%d",
                isbn, titulo, autor, anoPublicacao, exemplaresDisponiveis, totalExemplares);
    }
}

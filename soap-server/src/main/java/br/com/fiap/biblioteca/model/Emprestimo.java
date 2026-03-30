package br.com.fiap.biblioteca.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

/**
 * Modelo que representa um Empréstimo de livro.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Emprestimo {

    private String id;
    private String isbnLivro;
    private String tituloLivro;
    private String nomeUsuario;
    private String cpfUsuario;
    private String dataEmprestimo;
    private String dataDevolucaoPrevista;
    private String dataDevolucaoEfetiva;
    private String status; // ATIVO, DEVOLVIDO, ATRASADO

    // Construtor padrão obrigatório para JAXB
    public Emprestimo() {}

    public Emprestimo(String id, String isbnLivro, String tituloLivro,
                      String nomeUsuario, String cpfUsuario,
                      String dataEmprestimo, String dataDevolucaoPrevista) {
        this.id = id;
        this.isbnLivro = isbnLivro;
        this.tituloLivro = tituloLivro;
        this.nomeUsuario = nomeUsuario;
        this.cpfUsuario = cpfUsuario;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.status = "ATIVO";
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIsbnLivro() { return isbnLivro; }
    public void setIsbnLivro(String isbnLivro) { this.isbnLivro = isbnLivro; }

    public String getTituloLivro() { return tituloLivro; }
    public void setTituloLivro(String tituloLivro) { this.tituloLivro = tituloLivro; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getCpfUsuario() { return cpfUsuario; }
    public void setCpfUsuario(String cpfUsuario) { this.cpfUsuario = cpfUsuario; }

    public String getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(String dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }

    public String getDataDevolucaoPrevista() { return dataDevolucaoPrevista; }
    public void setDataDevolucaoPrevista(String dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public String getDataDevolucaoEfetiva() { return dataDevolucaoEfetiva; }
    public void setDataDevolucaoEfetiva(String dataDevolucaoEfetiva) {
        this.dataDevolucaoEfetiva = dataDevolucaoEfetiva;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Empréstimo[%s] | Livro: %s | Usuário: %s | Status: %s | Devolução: %s",
                id, tituloLivro, nomeUsuario, status, dataDevolucaoPrevista);
    }
}

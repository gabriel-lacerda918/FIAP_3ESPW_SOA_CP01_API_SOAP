package br.com.fiap.biblioteca.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper para lista de empréstimos - necessário para serialização JAXB de listas.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ListaEmprestimos {

    private List<Emprestimo> emprestimos = new ArrayList<>();
    private int total;
    private String mensagem;

    // Construtor padrão obrigatório para JAXB
    public ListaEmprestimos() {}

    public ListaEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
        this.total = emprestimos.size();
    }

    public ListaEmprestimos(List<Emprestimo> emprestimos, String mensagem) {
        this.emprestimos = emprestimos;
        this.total = emprestimos.size();
        this.mensagem = mensagem;
    }

    // Getters e Setters
    public List<Emprestimo> getEmprestimos() { return emprestimos; }
    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
        this.total = emprestimos != null ? emprestimos.size() : 0;
    }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}

package br.com.fiap.biblioteca.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

/**
 * Modelo genérico de resposta para operações do WebService.
 * Encapsula sucesso/falha e uma mensagem descritiva.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RespostaOperacao {

    private boolean sucesso;
    private String mensagem;
    private String codigoErro;

    // Construtor padrão obrigatório para JAXB
    public RespostaOperacao() {}

    public RespostaOperacao(boolean sucesso, String mensagem) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }

    public RespostaOperacao(boolean sucesso, String mensagem, String codigoErro) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.codigoErro = codigoErro;
    }

    // Factory methods para facilitar uso
    public static RespostaOperacao sucesso(String mensagem) {
        return new RespostaOperacao(true, mensagem);
    }

    public static RespostaOperacao erro(String mensagem, String codigo) {
        return new RespostaOperacao(false, mensagem, codigo);
    }

    // Getters e Setters
    public boolean isSucesso() { return sucesso; }
    public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getCodigoErro() { return codigoErro; }
    public void setCodigoErro(String codigoErro) { this.codigoErro = codigoErro; }

    @Override
    public String toString() {
        return String.format("Resposta[sucesso=%s, mensagem='%s', codigo='%s']",
                sucesso, mensagem, codigoErro);
    }
}

package br.com.fiap.cliente;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Utilitário para envio de requisições SOAP via HTTP puro.
 *
 * Abordagem: uso direto de HttpURLConnection para total controle.
 *
 * - Timeouts configurados para evitar bloqueio indefinido
 * - Leitura de erro separada para diagnóstico de falhas HTTP

 */
public class SoapClientHelper {

    private static final String ENDPOINT = "http://localhost:8080/biblioteca";
    private static final String NAMESPACE = "http://biblioteca.fiap.com.br/";
    private static final int CONNECT_TIMEOUT_MS = 5_000;
    private static final int READ_TIMEOUT_MS = 10_000;

    /**
     * Envia uma requisição SOAP e retorna o XML de resposta.
     *
     * @param soapAction  nome da operação (SOAPAction header)
     * @param soapBody    conteúdo do elemento <soapenv:Body>
     * @return XML de resposta como String
     */
    public static String enviar(String soapAction, String soapBody) throws IOException {
        String envelope = construirEnvelope(soapBody);

        URL url = new URL(ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        conn.setRequestProperty("SOAPAction", "\"" + NAMESPACE + soapAction + "\"");

        // Envia request
        try (OutputStream os = conn.getOutputStream()) {
            os.write(envelope.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        // Ler response
        int statusCode = conn.getResponseCode();
        InputStream is = (statusCode == 200) ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                sb.append(linha).append("\n");
            }
        }

        return sb.toString();
    }

    private static String construirEnvelope(String soapBody) {
        return """
                <soapenv:Envelope
                    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                    xmlns:bib="%s">
                    <soapenv:Header/>
                    <soapenv:Body>
                        %s
                    </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(NAMESPACE, soapBody);
    }

    // ──SOAP para cada operação

    public static String corpoBuscarLivroPorIsbn(String isbn) {
        return "<bib:buscarLivroPorIsbn><isbn>" + isbn + "</isbn></bib:buscarLivroPorIsbn>";
    }

    public static String corpoListarLivrosDisponiveis() {
        return "<bib:listarLivrosDisponiveis/>";
    }

    public static String corpoRegistrarEmprestimo(String isbn, String nomeUsuario, String cpfUsuario) {
        return """
                <bib:registrarEmprestimo>
                    <isbn>%s</isbn>
                    <nomeUsuario>%s</nomeUsuario>
                    <cpfUsuario>%s</cpfUsuario>
                </bib:registrarEmprestimo>
                """.formatted(isbn, nomeUsuario, cpfUsuario);
    }

    public static String corpoDevolverLivro(String idEmprestimo) {
        return "<bib:devolverLivro><idEmprestimo>" + idEmprestimo + "</idEmprestimo></bib:devolverLivro>";
    }

    public static String corpoBuscarPorAutor(String autor) {
        return "<bib:buscarPorAutor><autor>" + autor + "</autor></bib:buscarPorAutor>";
    }

    public static String corpoBuscarPorTitulo(String titulo) {
        return "<bib:buscarPorTitulo><titulo>" + titulo + "</titulo></bib:buscarPorTitulo>";
    }

    public static String corpoConsultarEmprestimo(String id) {
        return "<bib:consultarEmprestimo><idEmprestimo>" + id + "</idEmprestimo></bib:consultarEmprestimo>";
    }

    public static String corpoListarEmprestimosPorCpf(String cpf) {
        return "<bib:listarEmprestimosPorCpf><cpfUsuario>" + cpf + "</cpfUsuario></bib:listarEmprestimosPorCpf>";
    }
}

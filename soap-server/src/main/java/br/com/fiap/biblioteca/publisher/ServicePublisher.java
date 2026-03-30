package br.com.fiap.biblioteca.publisher;

import br.com.fiap.biblioteca.service.BibliotecaService;

import javax.xml.ws.Endpoint;

/**
 * Publicador do WebService SOAP da Biblioteca.
 *
 * Responsabilidade: iniciar o servidor HTTP embutido via JAX-WS
 * e publicar o endpoint SOAP em uma URL acessível.
 *
 * Ao executar esta classe, o servidor ficará disponível em:
 *   http://localhost:8080/biblioteca
 *   WSDL: http://localhost:8080/biblioteca?wsdl
 */
public class ServicePublisher {

    private static final String URL = "http://localhost:8080/biblioteca";

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  Sistema de Biblioteca - SOAP WebService");
        System.out.println("  Professor: Salatiel Luz Marinho | FIAP");
        System.out.println("=".repeat(60));
        System.out.println();

        System.out.println("Iniciando publicação do WebService...");

        // Cria servidor HTTP embutido e publica o serviço
        Endpoint endpoint = Endpoint.publish(URL, new BibliotecaService());

        System.out.println();
        System.out.println(" WebService publicado com sucesso!");
        System.out.println();
        System.out.println(" Endpoint : " + URL);
        System.out.println(" WSDL     : " + URL + "?wsdl");
        System.out.println();
        System.out.println("Operações disponíveis:");
        System.out.println("  - buscarLivroPorIsbn(isbn)");
        System.out.println("  - listarLivrosDisponiveis()");
        System.out.println("  - registrarEmprestimo(isbn, nomeUsuario, cpfUsuario)");
        System.out.println("  - devolverLivro(idEmprestimo)");
        System.out.println("  - buscarPorAutor(autor)");
        System.out.println("  - buscarPorTitulo(titulo)");
        System.out.println("  - consultarEmprestimo(idEmprestimo)");
        System.out.println("  - listarEmprestimosPorCpf(cpfUsuario)");
        System.out.println();
        System.out.println("Pressione Ctrl+C para encerrar o servidor.");
        System.out.println("=".repeat(60));

        // Mantém o servidor ativo
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("\nServidor encerrado.");
            endpoint.stop();
        }
    }
}

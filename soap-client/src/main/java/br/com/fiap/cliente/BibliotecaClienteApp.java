package br.com.fiap.cliente;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Aplicação cliente interativa para o Sistema de Biblioteca SOAP.
 *
 * Permite ao usuário:
 *  1. Buscar livro por ISBN
 *  2. Listar livros disponíveis
 *  3. Realizar empréstimo
 *  4. Devolver livro
 *  5. Buscar por autor
 *  6. Buscar por título
 *  7. Consultar empréstimo
 *  8. Listar empréstimos por CPF
 *  0. Sair
 *
 * IMPORTANTE: O servidor (ServicePublisher) deve estar em execução antes de iniciar este cliente.
 */
public class BibliotecaClienteApp {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        cabecalho();
        verificarConexao();

        boolean executando = true;
        while (executando) {
            menu();
            String opcao = scanner.nextLine().trim();
            System.out.println();

            executando = switch (opcao) {
                case "1" -> { buscarPorIsbn();    yield true; }
                case "2" -> { listarDisponiveis(); yield true; }
                case "3" -> { realizarEmprestimo(); yield true; }
                case "4" -> { devolverLivro();    yield true; }
                case "5" -> { buscarPorAutor();   yield true; }
                case "6" -> { buscarPorTitulo();  yield true; }
                case "7" -> { consultarEmprestimo(); yield true; }
                case "8" -> { listarEmprestimosPorCpf(); yield true; }
                case "0" -> { System.out.println("Encerrando cliente. Até logo!"); yield false; }
                default  -> { System.out.println("Opção inválida. Tente novamente."); yield true; }
            };
        }
        scanner.close();
    }

    // ─── Operações

    private static void buscarPorIsbn() {
        System.out.print("Digite o ISBN do livro: ");
        String isbn = scanner.nextLine().trim();
        try {
            String resposta = SoapClientHelper.enviar("buscarLivroPorIsbn",
                    SoapClientHelper.corpoBuscarLivroPorIsbn(isbn));

            String titulo = XmlParser.extrairTag(resposta, "titulo");
            if (titulo.isEmpty()) {
                System.out.println(" Livro não encontrado para ISBN: " + isbn);
            } else {
                System.out.println(" Livro encontrado:");
                imprimirLivroDetalhe(resposta);
            }
        } catch (IOException e) {
            erroConexao(e);
        }
    }

    private static void listarDisponiveis() {
        try {
            String resposta = SoapClientHelper.enviar("listarLivrosDisponiveis",
                    SoapClientHelper.corpoListarLivrosDisponiveis());

            String total = XmlParser.extrairTag(resposta, "total");
            System.out.println(" Livros disponíveis (" + total + " encontrado(s)):");
            separador();
            List<String> livros = XmlParser.extrairLivros(resposta);
            if (livros.isEmpty()) {
                System.out.println("  Nenhum livro disponível no momento.");
            } else {
                livros.forEach(System.out::println);
            }
            separador();
        } catch (IOException e) {
            erroConexao(e);
        }
    }

    private static void realizarEmprestimo() {
        System.out.print("ISBN do livro a emprestar: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Seu nome completo: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Seu CPF (somente números): ");
        String cpf = scanner.nextLine().trim();

        try {
            String resposta = SoapClientHelper.enviar("registrarEmprestimo",
                    SoapClientHelper.corpoRegistrarEmprestimo(isbn, nome, cpf));

            boolean sucesso = Boolean.parseBoolean(XmlParser.extrairTag(resposta, "sucesso"));
            String mensagem = XmlParser.extrairTag(resposta, "mensagem");

            if (sucesso) {
                System.out.println(" " + mensagem);
            } else {
                String codigo = XmlParser.extrairTag(resposta, "codigoErro");
                System.out.println(" " + mensagem + " [" + codigo + "]");
            }
        } catch (IOException e) {
            erroConexao(e);
        }
    }

    private static void devolverLivro() {
        System.out.print("ID do empréstimo a devolver (ex: EMP-1001): ");
        String id = scanner.nextLine().trim();
        try {
            String resposta = SoapClientHelper.enviar("devolverLivro",
                    SoapClientHelper.corpoDevolverLivro(id));

            boolean sucesso = Boolean.parseBoolean(XmlParser.extrairTag(resposta, "sucesso"));
            String mensagem = XmlParser.extrairTag(resposta, "mensagem");

            if (sucesso) {
                System.out.println(" " + mensagem);
            } else {
                System.out.println(" " + mensagem);
            }
        } catch (IOException e) {
            erroConexao(e);
        }
    }

    private static void buscarPorAutor() {
        System.out.print("Nome do autor (parcial aceito): ");
        String autor = scanner.nextLine().trim();
        try {
            String resposta = SoapClientHelper.enviar("buscarPorAutor",
                    SoapClientHelper.corpoBuscarPorAutor(autor));

            String total = XmlParser.extrairTag(resposta, "total");
            System.out.println(" Resultados para autor '" + autor + "' (" + total + " encontrado(s)):");
            separador();
            XmlParser.extrairLivros(resposta).forEach(System.out::println);
            separador();
        } catch (IOException e) {
            erroConexao(e);
        }
    }

    private static void buscarPorTitulo() {
        System.out.print("Título (parcial aceito): ");
        String titulo = scanner.nextLine().trim();
        try {
            String resposta = SoapClientHelper.enviar("buscarPorTitulo",
                    SoapClientHelper.corpoBuscarPorTitulo(titulo));

            String total = XmlParser.extrairTag(resposta, "total");
            System.out.println(" Resultados para título '" + titulo + "' (" + total + " encontrado(s)):");
            separador();
            XmlParser.extrairLivros(resposta).forEach(System.out::println);
            separador();
        } catch (IOException e) {
            erroConexao(e);
        }
    }

    private static void consultarEmprestimo() {
        System.out.print("ID do empréstimo (ex: EMP-1001): ");
        String id = scanner.nextLine().trim();
        try {
            String resposta = SoapClientHelper.enviar("consultarEmprestimo",
                    SoapClientHelper.corpoConsultarEmprestimo(id));

            String empId = XmlParser.extrairTag(resposta, "id");
            if (empId.isEmpty()) {
                System.out.println(" Empréstimo não encontrado: " + id);
            } else {
                System.out.println(" Detalhes do empréstimo:");
                separador();
                System.out.println("  ID           : " + empId);
                System.out.println("  Livro        : " + XmlParser.extrairTag(resposta, "tituloLivro"));
                System.out.println("  ISBN         : " + XmlParser.extrairTag(resposta, "isbnLivro"));
                System.out.println("  Usuário      : " + XmlParser.extrairTag(resposta, "nomeUsuario"));
                System.out.println("  CPF          : " + XmlParser.extrairTag(resposta, "cpfUsuario"));
                System.out.println("  Data Emprést.: " + XmlParser.extrairTag(resposta, "dataEmprestimo"));
                System.out.println("  Devolução    : " + XmlParser.extrairTag(resposta, "dataDevolucaoPrevista"));
                System.out.println("  Status       : " + XmlParser.extrairTag(resposta, "status"));
                separador();
            }
        } catch (IOException e) {
            erroConexao(e);
        }
    }

    private static void listarEmprestimosPorCpf() {
        System.out.print("CPF do usuário: ");
        String cpf = scanner.nextLine().trim();
        try {
            String resposta = SoapClientHelper.enviar("listarEmprestimosPorCpf",
                    SoapClientHelper.corpoListarEmprestimosPorCpf(cpf));

            String total = XmlParser.extrairTag(resposta, "total");
            System.out.println(" Empréstimos do CPF " + cpf + " (" + total + " encontrado(s)):");
            separador();
            List<String> lista = XmlParser.extrairEmprestimos(resposta);
            if (lista.isEmpty()) {
                System.out.println("  Nenhum empréstimo encontrado para este CPF.");
            } else {
                lista.forEach(System.out::println);
            }
            separador();
        } catch (IOException e) {
            erroConexao(e);
        }
    }

    // ───

    private static void imprimirLivroDetalhe(String xml) {
        separador();
        System.out.println("  ISBN           : " + XmlParser.extrairTag(xml, "isbn"));
        System.out.println("  Título         : " + XmlParser.extrairTag(xml, "titulo"));
        System.out.println("  Autor          : " + XmlParser.extrairTag(xml, "autor"));
        System.out.println("  Gênero         : " + XmlParser.extrairTag(xml, "genero"));
        System.out.println("  Ano            : " + XmlParser.extrairTag(xml, "anoPublicacao"));
        System.out.println("  Disponíveis    : " + XmlParser.extrairTag(xml, "exemplaresDisponiveis")
                + "/" + XmlParser.extrairTag(xml, "totalExemplares"));
        separador();
    }

    private static void cabecalho() {
        System.out.println("=".repeat(70));
        System.out.println("     SISTEMA DE BIBLIOTECA — Cliente SOAP");
        System.out.println("   Disciplina: Arquitetura SOA | FIAP");
        System.out.println("=".repeat(70));
        System.out.println();
    }

    private static void menu() {
        System.out.println("\n═══════════════ MENU ═══════════════");
        System.out.println("  [1] Buscar livro por ISBN");
        System.out.println("  [2] Listar livros disponíveis");
        System.out.println("  [3] Realizar empréstimo");
        System.out.println("  [4] Devolver livro");
        System.out.println("  [5] Buscar por autor");
        System.out.println("  [6] Buscar por título");
        System.out.println("  [7] Consultar empréstimo pelo ID");
        System.out.println("  [8] Listar empréstimos por CPF");
        System.out.println("  [0] Sair");
        System.out.println("═════════════════════════════════════");
        System.out.print("Escolha uma opção: ");
    }

    private static void separador() {
        System.out.println("  " + "-".repeat(100));
    }

    private static void verificarConexao() {
        System.out.print("Verificando conexão com o servidor... ");
        try {
            String resp = SoapClientHelper.enviar("listarLivrosDisponiveis",
                    SoapClientHelper.corpoListarLivrosDisponiveis());
            if (resp.contains("Envelope")) {
                System.out.println(" Conectado!");
            } else {
                System.out.println(" Resposta inesperada.");
            }
        } catch (IOException e) {
            System.out.println(" Servidor indisponível!");
            System.out.println("   Certifique-se de que o ServicePublisher está em execução.");
            System.out.println("   Endpoint esperado: http://localhost:8080/biblioteca");
        }
    }

    private static void erroConexao(IOException e) {
        System.out.println(" Erro de comunicação com o servidor: " + e.getMessage());
        System.out.println("   Verifique se o ServicePublisher está rodando.");
    }
}

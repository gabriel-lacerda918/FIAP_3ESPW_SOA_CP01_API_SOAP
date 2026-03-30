package br.com.fiap.biblioteca.service;

import br.com.fiap.biblioteca.model.*;
import br.com.fiap.biblioteca.repository.BibliotecaRepository;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;
import java.util.Optional;

/**
 * WebService SOAP do Sistema de Biblioteca.
 *
 * Operações disponíveis:
 *  1. buscarLivroPorIsbn   — busca um livro pelo ISBN
 *  2. listarLivrosDisponiveis — lista livros com exemplares disponíveis
 *  3. registrarEmprestimo  — realiza o empréstimo de um livro
 *  4. devolverLivro        — registra a devolução de um empréstimo
 *  5. buscarPorAutor       — busca livros por nome do autor
 *  6. buscarPorTitulo      — busca livros por título
 *  7. consultarEmprestimo  — consulta detalhes de um empréstimo pelo ID
 *  8. listarEmprestimosPorCpf — lista empréstimos de um usuário pelo CPF
 *
 * Boas práticas aplicadas:
 * - @WebService com targetNamespace explícito
 * - @WebParam com nomes explícitos nos parâmetros XML
 * - Validação de entrada antes de acessar o repositório
 * - Respostas padronizadas com RespostaOperacao
 */
@WebService(
        serviceName = "BibliotecaService",
        portName = "BibliotecaPort",
        targetNamespace = "http://biblioteca.fiap.com.br/",
        endpointInterface = "br.com.fiap.biblioteca.service.IBibliotecaService"
)
public class BibliotecaService implements IBibliotecaService {

    private final BibliotecaRepository repository = BibliotecaRepository.getInstance();

    // ─── 1. Buscar livro por ISBN ──────────────────────────────────────────────

    @Override
    @WebMethod(operationName = "buscarLivroPorIsbn")
    public Livro buscarLivroPorIsbn(
            @WebParam(name = "isbn") String isbn) {

        System.out.println("[Service] buscarLivroPorIsbn - ISBN: " + isbn);

        if (isbn == null || isbn.trim().isEmpty()) {
            System.out.println("[Service] ISBN inválido recebido.");
            return null;
        }

        Optional<Livro> livro = repository.buscarPorIsbn(isbn);
        livro.ifPresentOrElse(
                l -> System.out.println("[Service] Livro encontrado: " + l.getTitulo()),
                () -> System.out.println("[Service] Livro não encontrado para ISBN: " + isbn)
        );
        return livro.orElse(null);
    }

    // ─── 2. Lista livros

    @Override
    @WebMethod(operationName = "listarLivrosDisponiveis")
    public ListaLivros listarLivrosDisponiveis() {

        System.out.println("[Service] listarLivrosDisponiveis");
        List<Livro> livros = repository.listarDisponiveis();
        System.out.println("[Service] Total de livros disponíveis: " + livros.size());
        return new ListaLivros(livros, "Livros com exemplares disponíveis para empréstimo.");
    }

    // ─── 3. Registra empréstimo

    @Override
    @WebMethod(operationName = "registrarEmprestimo")
    public RespostaOperacao registrarEmprestimo(
            @WebParam(name = "isbn") String isbn,
            @WebParam(name = "nomeUsuario") String nomeUsuario,
            @WebParam(name = "cpfUsuario") String cpfUsuario) {

        System.out.println("[Service] registrarEmprestimo - ISBN: " + isbn + " | Usuário: " + nomeUsuario);

        // Validações de entrada
        if (isbn == null || isbn.trim().isEmpty()) {
            return RespostaOperacao.erro("ISBN é obrigatório.", "ERR_ISBN_VAZIO");
        }
        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            return RespostaOperacao.erro("Nome do usuário é obrigatório.", "ERR_NOME_VAZIO");
        }
        if (cpfUsuario == null || cpfUsuario.trim().isEmpty()) {
            return RespostaOperacao.erro("CPF do usuário é obrigatório.", "ERR_CPF_VAZIO");
        }

        // Verificar se livro existe
        Optional<Livro> livroOpt = repository.buscarPorIsbn(isbn);
        if (livroOpt.isEmpty()) {
            return RespostaOperacao.erro(
                    "Livro com ISBN '" + isbn + "' não encontrado no catálogo.",
                    "ERR_LIVRO_NAO_ENCONTRADO");
        }

        Livro livro = livroOpt.get();
        if (!livro.isDisponivel()) {
            return RespostaOperacao.erro(
                    "Não há exemplares disponíveis de '" + livro.getTitulo() + "' no momento.",
                    "ERR_SEM_EXEMPLARES");
        }

        // Registrar empréstimo
        var emprestimo = repository.registrarEmprestimo(isbn, nomeUsuario, cpfUsuario);
        if (emprestimo == null) {
            return RespostaOperacao.erro("Erro interno ao registrar empréstimo.", "ERR_INTERNO");
        }

        String msg = String.format(
                "Empréstimo registrado com sucesso! ID: %s | Livro: '%s' | Devolução prevista: %s",
                emprestimo.getId(), livro.getTitulo(), emprestimo.getDataDevolucaoPrevista());
        System.out.println("[Service] " + msg);
        return RespostaOperacao.sucesso(msg);
    }

    // ─── 4. Devolução

    @Override
    @WebMethod(operationName = "devolverLivro")
    public RespostaOperacao devolverLivro(
            @WebParam(name = "idEmprestimo") String idEmprestimo) {

        System.out.println("[Service] devolverLivro - ID: " + idEmprestimo);

        if (idEmprestimo == null || idEmprestimo.trim().isEmpty()) {
            return RespostaOperacao.erro("ID do empréstimo é obrigatório.", "ERR_ID_VAZIO");
        }

        boolean devolvido = repository.devolverLivro(idEmprestimo);
        if (!devolvido) {
            return RespostaOperacao.erro(
                    "Empréstimo '" + idEmprestimo + "' não encontrado ou já devolvido.",
                    "ERR_EMPRESTIMO_INVALIDO");
        }

        String msg = "Livro devolvido com sucesso! Empréstimo ID: " + idEmprestimo;
        System.out.println("[Service] " + msg);
        return RespostaOperacao.sucesso(msg);
    }

    // ─── 5. Buscar por autor

    @Override
    @WebMethod(operationName = "buscarPorAutor")
    public ListaLivros buscarPorAutor(
            @WebParam(name = "autor") String autor) {

        System.out.println("[Service] buscarPorAutor - Autor: " + autor);
        List<Livro> livros = repository.buscarPorAutor(autor);
        return new ListaLivros(livros, "Resultados para autor: " + autor);
    }

    // ─── 6. Buscar por título

    @Override
    @WebMethod(operationName = "buscarPorTitulo")
    public ListaLivros buscarPorTitulo(
            @WebParam(name = "titulo") String titulo) {

        System.out.println("[Service] buscarPorTitulo - Título: " + titulo);
        List<Livro> livros = repository.buscarPorTitulo(titulo);
        return new ListaLivros(livros, "Resultados para título: " + titulo);
    }

    // ─── 7. Consultar empréstimo por ID

    @Override
    @WebMethod(operationName = "consultarEmprestimo")
    public Emprestimo consultarEmprestimo(
            @WebParam(name = "idEmprestimo") String idEmprestimo) {

        System.out.println("[Service] consultarEmprestimo - ID: " + idEmprestimo);
        return repository.buscarEmprestimo(idEmprestimo).orElse(null);
    }

    // ─── 8. empréstimos por CPF

    @Override
    @WebMethod(operationName = "listarEmprestimosPorCpf")
    public ListaEmprestimos listarEmprestimosPorCpf(
            @WebParam(name = "cpfUsuario") String cpfUsuario) {

        System.out.println("[Service] listarEmprestimosPorCpf - CPF: " + cpfUsuario);
        var lista = repository.listarEmprestimosPorCpf(cpfUsuario);
        return new ListaEmprestimos(lista, "Empréstimos do usuário com CPF: " + cpfUsuario);
    }
}

package br.com.fiap.biblioteca.repository;

import br.com.fiap.biblioteca.model.Emprestimo;
import br.com.fiap.biblioteca.model.Livro;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Repositório em memória para Livros e Empréstimos.
 *
 * Boas práticas aplicadas:
 * - Separação de responsabilidades (Repository Pattern)
 * - Singleton para garantir estado compartilhado entre chamadas SOAP
 * - Thread-safety com sincronização básica
 * - Dados iniciais realistas para demonstração
 */
public class BibliotecaRepository {

    private static BibliotecaRepository instancia;
    private final Map<String, Livro> catalogoLivros = new LinkedHashMap<>();
    private final Map<String, Emprestimo> emprestimos = new LinkedHashMap<>();
    private final AtomicInteger contadorEmprestimo = new AtomicInteger(1000);
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Construtor privado — padrão Singleton
    private BibliotecaRepository() {
        inicializarCatalogo();
    }

    public static synchronized BibliotecaRepository getInstance() {
        if (instancia == null) {
            instancia = new BibliotecaRepository();
        }
        return instancia;
    }

    // ─── Inicialização do catálogo com dados de exemplo ────────────────────────

    private void inicializarCatalogo() {
        adicionarLivro("978-85-7522-743-0", "Clean Code", "Robert C. Martin",
                "Engenharia de Software", 2008, 3);
        adicionarLivro("978-85-7522-554-2", "Domain-Driven Design", "Eric Evans",
                "Arquitetura de Software", 2003, 2);
        adicionarLivro("978-85-508-0085-2", "O Programador Pragmático", "Andrew Hunt",
                "Engenharia de Software", 1999, 4);
        adicionarLivro("978-65-5452-002-1", "Arquitetura Limpa", "Robert C. Martin",
                "Arquitetura de Software", 2017, 2);
        adicionarLivro("978-85-7522-619-8", "Padrões de Projeto", "Gang of Four",
                "Design Patterns", 1994, 3);
        adicionarLivro("978-85-7868-031-5", "Fundamentos de Banco de Dados", "Ramez Elmasri",
                "Banco de Dados", 2019, 5);
        adicionarLivro("978-85-7652-122-0", "Introdução a Algoritmos", "Thomas H. Cormen",
                "Ciência da Computação", 2009, 2);
        adicionarLivro("978-85-7522-419-4", "Microsserviços Prontos para a Produção", "Susan Fowler",
                "Arquitetura de Software", 2016, 1);

        System.out.println("[Repositório] Catálogo inicializado com " + catalogoLivros.size() + " livros.");
    }

    private void adicionarLivro(String isbn, String titulo, String autor,
                                  String genero, int ano, int exemplares) {
        catalogoLivros.put(isbn, new Livro(isbn, titulo, autor, genero, ano, exemplares));
    }

    // ─── Operações de Livros ───────────────────────────────────────────────────

    public Optional<Livro> buscarPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) return Optional.empty();
        return Optional.ofNullable(catalogoLivros.get(isbn.trim()));
    }

    public List<Livro> listarTodos() {
        return new ArrayList<>(catalogoLivros.values());
    }

    public List<Livro> listarDisponiveis() {
        return catalogoLivros.values().stream()
                .filter(Livro::isDisponivel)
                .collect(Collectors.toList());
    }

    public List<Livro> buscarPorAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) return Collections.emptyList();
        String busca = autor.toLowerCase().trim();
        return catalogoLivros.values().stream()
                .filter(l -> l.getAutor().toLowerCase().contains(busca))
                .collect(Collectors.toList());
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) return Collections.emptyList();
        String busca = titulo.toLowerCase().trim();
        return catalogoLivros.values().stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(busca))
                .collect(Collectors.toList());
    }

    // ─── Operações de Empréstimo ───────────────────────────────────────────────

    public synchronized Emprestimo registrarEmprestimo(String isbn, String nomeUsuario, String cpfUsuario) {
        Livro livro = catalogoLivros.get(isbn);
        if (livro == null || !livro.isDisponivel()) return null;

        String id = "EMP-" + contadorEmprestimo.incrementAndGet();
        LocalDate hoje = LocalDate.now();
        LocalDate devolucao = hoje.plusDays(14);

        Emprestimo emp = new Emprestimo(
                id, isbn, livro.getTitulo(), nomeUsuario, cpfUsuario,
                hoje.format(FORMATO_DATA),
                devolucao.format(FORMATO_DATA)
        );

        livro.setExemplaresDisponiveis(livro.getExemplaresDisponiveis() - 1);
        emprestimos.put(id, emp);
        return emp;
    }

    public synchronized boolean devolverLivro(String idEmprestimo) {
        Emprestimo emp = emprestimos.get(idEmprestimo);
        if (emp == null || !"ATIVO".equals(emp.getStatus())) return false;

        Livro livro = catalogoLivros.get(emp.getIsbnLivro());
        if (livro != null) {
            livro.setExemplaresDisponiveis(livro.getExemplaresDisponiveis() + 1);
        }

        emp.setStatus("DEVOLVIDO");
        emp.setDataDevolucaoEfetiva(LocalDate.now().format(FORMATO_DATA));
        return true;
    }

    public Optional<Emprestimo> buscarEmprestimo(String id) {
        return Optional.ofNullable(emprestimos.get(id));
    }

    public List<Emprestimo> listarEmprestimosPorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) return Collections.emptyList();
        return emprestimos.values().stream()
                .filter(e -> e.getCpfUsuario().equals(cpf.trim()))
                .collect(Collectors.toList());
    }

    public List<Emprestimo> listarEmprestimosAtivos() {
        return emprestimos.values().stream()
                .filter(e -> "ATIVO".equals(e.getStatus()))
                .collect(Collectors.toList());
    }
}

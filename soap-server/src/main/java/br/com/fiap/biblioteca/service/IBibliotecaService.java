package br.com.fiap.biblioteca.service;

import br.com.fiap.biblioteca.model.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Interface (contrato) do WebService de Biblioteca.
 *
 * Boa prática: separar a interface da implementação permite:
 * - Baixo acoplamento
 * - Facilidade de testes (mocks)
 * - Troca de implementação sem alterar clientes
 */
@WebService(targetNamespace = "http://biblioteca.fiap.com.br/")
public interface IBibliotecaService {

    @WebMethod(operationName = "buscarLivroPorIsbn")
    Livro buscarLivroPorIsbn(@WebParam(name = "isbn") String isbn);

    @WebMethod(operationName = "listarLivrosDisponiveis")
    ListaLivros listarLivrosDisponiveis();

    @WebMethod(operationName = "registrarEmprestimo")
    RespostaOperacao registrarEmprestimo(
            @WebParam(name = "isbn") String isbn,
            @WebParam(name = "nomeUsuario") String nomeUsuario,
            @WebParam(name = "cpfUsuario") String cpfUsuario);

    @WebMethod(operationName = "devolverLivro")
    RespostaOperacao devolverLivro(@WebParam(name = "idEmprestimo") String idEmprestimo);

    @WebMethod(operationName = "buscarPorAutor")
    ListaLivros buscarPorAutor(@WebParam(name = "autor") String autor);

    @WebMethod(operationName = "buscarPorTitulo")
    ListaLivros buscarPorTitulo(@WebParam(name = "titulo") String titulo);

    @WebMethod(operationName = "consultarEmprestimo")
    Emprestimo consultarEmprestimo(@WebParam(name = "idEmprestimo") String idEmprestimo);

    @WebMethod(operationName = "listarEmprestimosPorCpf")
    ListaEmprestimos listarEmprestimosPorCpf(@WebParam(name = "cpfUsuario") String cpfUsuario);
}

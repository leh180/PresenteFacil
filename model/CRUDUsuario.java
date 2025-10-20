package model;

import bib.Arquivo;
import bib.HashExtensivel;
import java.io.File;

/**
 * Gerencia todas as operações de persistência (CRUD) para a entidade {@link Usuario}.
 * Estende a classe genérica {@link Arquivo} e mantém um índice secundário
 * (Hash Extensível) baseado no e-mail para otimizar as buscas e o login.
 *
 * @author Ana, Bruno, João, Leticia e Miguel
 * @version 1.0
 */
public class CRUDUsuario extends Arquivo<Usuario> {

    // ------------------------------------------ Atributos da Classe
    // ------------------------------------------

    /**
     * Índice secundário para busca rápida de usuários pelo e-mail.
     */
    private HashExtensivel<ParEmailId> indiceEmail;

    // ------------------------------------------ Construtor
    // ------------------------------------------

    /**
     * Construtor da classe CRUDUsuario.
     * Inicializa o arquivo principal de dados ("usuarios") e o arquivo de
     * índice de e-mail (Hash Extensível).
     *
     * @throws Exception se ocorrer um erro na abertura ou criação dos arquivos
     * de dados ou índice.
     */
    public CRUDUsuario() throws Exception {
        super("usuarios", Usuario.class.getConstructor());

        File d = new File("data");
        if (!d.exists())
            d.mkdir();

        indiceEmail = new HashExtensivel<>(
                ParEmailId.class.getConstructor(),
                4,
                "data/usuarios_email.diretorio.idx",
                "data/usuarios_email.cestos.idx");
    }

    // ------------------------------------------ Métodos Públicos (CRUD)
    // ------------------------------------------

    /**
     * Cria um novo registro de usuário no arquivo principal e atualiza o índice de
     * e-mail.
     *
     * @param usuario O objeto {@link Usuario} a ser persistido (sem ID).
     * @return O ID (inteiro) gerado para o novo usuário.
     * @throws Exception se ocorrer um erro durante a escrita no arquivo principal
     * ou no índice.
     */
    @Override
    public int create(Usuario usuario) throws Exception {
        int id = super.create(usuario);
        usuario.setID(id);

        indiceEmail.create(new ParEmailId(usuario.getEmail(), id));
        return id;
    }

    /**
     * Busca um usuário específico usando seu e-mail.
     * A busca é otimizada pelo índice de Hash e inclui uma verificação
     * anti-colisão (comparando a String e-mail original).
     *
     * @param email O e-mail (String) a ser procurado.
     * @return O objeto {@link Usuario} correspondente, ou {@code null} se não for
     * encontrado ou se houver colisão de hash.
     * @throws Exception se ocorrer um erro during a leitura do índice ou do
     * arquivo principal.
     */
    public Usuario readByEmail(String email) throws Exception {
        ParEmailId par = indiceEmail.read(email.hashCode());

        // Verificação anti-colisão
        if (par != null && par.getEmail().equals(email)) {
            return super.read(par.getId());
        }
        return null;
    }

    /**
     * Atualiza os dados de um usuário no arquivo principal e garante a
     * consistência do índice de e-mail.
     * Se o e-mail for alterado, o índice antigo é removido e o novo é criado.
     *
     * @param novoUsuario O objeto {@link Usuario} contendo os dados atualizados.
     * @return {@code true} se a atualização for bem-sucedida, {@code false}
     * caso contrário.
     * @throws Exception se ocorrer um erro during a atualização dos arquivos
     * ou índices.
     */
    @Override
    public boolean update(Usuario novoUsuario) throws Exception {
        Usuario usuarioAntigo = super.read(novoUsuario.getID());
        if (usuarioAntigo == null) {
            return false;
        }

        // Atualiza no arquivo principal
        if (super.update(novoUsuario)) {
            // Se o e-mail mudou, atualiza o índice
            if (!usuarioAntigo.getEmail().equals(novoUsuario.getEmail())) {
                indiceEmail.delete(usuarioAntigo.getEmail().hashCode());
                indiceEmail.create(new ParEmailId(novoUsuario.getEmail(), novoUsuario.getID()));
            }
            return true;
        }
        return false;
    }

    /**
     * Exclui (logicamente) um usuário do arquivo principal e remove sua
     * respectiva entrada do índice de e-mail.
     *
     * @param id O ID (inteiro) do usuário a ser excluído.
     * @return {@code true} se a exclusão for bem-sucedida, {@code false}
     * caso contrário (ex: usuário não encontrado).
     * @throws Exception se ocorrer um erro during a atualização dos arquivos
     * ou índices.
     */
    @Override
    public boolean delete(int id) throws Exception {
        Usuario u = super.read(id);
        if (u != null) {
            if (super.delete(id)) {
                // Remove do índice
                indiceEmail.delete(u.getEmail().hashCode());
                return true;
            }
        }
        return false;
    }

    /**
     * Fecha as conexões com os arquivos de dados gerenciados por este
     * controlador.
     *
     * @throws Exception se ocorrer um erro ao fechar o recurso.
     */
    @Override
    public void close() throws Exception {
        super.close();
    }
}
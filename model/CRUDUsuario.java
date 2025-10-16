package model;

import bib.Arquivo;
import bib.HashExtensivel;
import java.io.File;

/**
 * A classe CRUDUsuario estende a classe genérica Arquivo e gere todas as
 * operações de persistência para a entidade Usuário.
 * Ela mantém um índice secundário por e-mail (Hash Extensível) para
 * acelerar as buscas e o processo de login.
 */
public class CRUDUsuario extends Arquivo<Usuario> {

    // ------------------------------------------ Atributos da Classe
    // ------------------------------------------

    private HashExtensivel<ParEmailId> indiceEmail;

    // ------------------------------------------ Construtor
    // ------------------------------------------

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
     * Cria um novo utilizador, guardando-o no ficheiro principal e atualizando o
     * índice de e-mail.
     * Sobrescreve o método da classe pai para adicionar a lógica do índice
     * secundário.
     * 
     * @param usuario O objeto Usuário a ser criado.
     * @return O ID gerado para o novo utilizador.
     * @throws Exception se ocorrer um erro durante a escrita nos ficheiros.
     */
    @Override
    public int create(Usuario usuario) throws Exception {
        int id = super.create(usuario);
        usuario.setID(id);

        indiceEmail.create(new ParEmailId(usuario.getEmail(), id));
        return id;
    }

    /**
     * Procura um utilizador pelo seu e-mail, utilizando o índice secundário de
     * hash.
     * Esta operação é otimizada, evitando uma varredura completa do ficheiro
     * principal.
     * 
     * @param email O e-mail a ser procurado.
     * @return O objeto Usuário se encontrado, caso contrário, null.
     * @throws Exception se ocorrer um erro durante a leitura dos ficheiros.
     */
    public Usuario readByEmail(String email) throws Exception {
        ParEmailId par = indiceEmail.read(email.hashCode());

        if (par != null && par.getEmail().equals(email)) {
            return super.read(par.getId());
        }
        return null;
    }

    /**
     * Atualiza os dados de um utilizador, garantindo a consistência do índice de
     * e-mail.
     * 
     * 
     * @param novoUsuario O objeto Usuário com os dados atualizados.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     * @throws Exception se ocorrer um erro durante a escrita nos ficheiros.
     */
    @Override
    public boolean update(Usuario novoUsuario) throws Exception {
        Usuario usuarioAntigo = super.read(novoUsuario.getID());
        if (usuarioAntigo == null) {
            return false;
        }

        if (super.update(novoUsuario)) {
            if (!usuarioAntigo.getEmail().equals(novoUsuario.getEmail())) {
                indiceEmail.delete(usuarioAntigo.getEmail().hashCode());
                indiceEmail.create(new ParEmailId(novoUsuario.getEmail(), novoUsuario.getID()));
            }
            return true;
        }
        return false;
    }

    /**
     * Apaga um utilizador do sistema, incluindo a sua entrada no índice de e-mail.
     * 
     * @return true se a operação for bem-sucedida, false caso contrário.
     * @throws Exception se ocorrer um erro durante a escrita nos ficheiros.
     */
    @Override
    public boolean delete(int id) throws Exception {
        Usuario u = super.read(id);
        if (u != null) {
            if (super.delete(id)) {
                indiceEmail.delete(u.getEmail().hashCode());
                return true;
            }
        }
        return false;
    }

    /**
     * Fecha a ligação com o ficheiro de dados principal (usuarios.db).
     * Este método é essencial para garantir que todas as alterações sejam salvas no
     * disco.
     * 
     * @throws Exception se ocorrer um erro ao fechar o ficheiro.
     */
    @Override
    public void close() throws Exception {
        super.close();
    }
}

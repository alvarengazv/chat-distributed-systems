/**
 * Mantém o nome usado pelo painel existente.
 */
public class DesktopRecepcaoThread implements Runnable {
    private final ServicoDesktop servico = new ServicoDesktop(true);

    public void iniciar() throws java.io.IOException {
        servico.iniciar();
    }

    public void run() {
        try {
            iniciar();
        } catch (java.io.IOException e) {
            RegistroErros.servidor(RegistroErros.DESKTOP, null, null, "DesktopRecepcaoThread:run", e);
        }
    }

    public void fecharServidor() {
        servico.fecharServidor();
    }
}

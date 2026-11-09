/**
 * Mantém o protocolo ObjectOutputStream/readUTF do cliente desktop.
 */
public class DesktopEnvioThread implements Runnable {
    private final ServicoDesktop servico = new ServicoDesktop(false);

    public void iniciar() throws java.io.IOException {
        servico.iniciar();
    }

    public void run() {
        try {
            iniciar();
        } catch (java.io.IOException e) {
            RegistroErros.servidor(RegistroErros.DESKTOP, null, null, "DesktopEnvioThread:run", e);
        }
    }

    public void fecharServidor() {
        servico.fecharServidor();
    }
}

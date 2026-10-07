import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DesktopEnvioThread implements Runnable{
    private volatile boolean paradaManual = false;
    private Socket cliente;
    private volatile ServerSocket emissor;

    @Override
    public void run() {

        while (!paradaManual) {
            try (
                    FileReader fReader = new FileReader(Util.pathRepDesktop);
                    BufferedReader buffer = new BufferedReader(fReader)
            ) {
                StringBuilder msgs = new StringBuilder();

                while (buffer.ready()) {
                    msgs.append(buffer.readLine());
                }

                buffer.close();
                fReader.close();

                emissor = new ServerSocket(Util.portaEnvioDesktop);
                emissor.setReuseAddress(true);
                if (paradaManual) {
                    emissor.close();
                    break;
                }

                cliente = emissor.accept();

                ObjectOutputStream output = new ObjectOutputStream(cliente.getOutputStream());
                output.flush();

                output.writeUTF(msgs.toString());

                output.close();
                cliente.close();
                emissor.close();
            } catch (Exception e) {
                if (!paradaManual) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro em DesktopEnvioThread::run - " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void fecharServidor(){
        this.paradaManual = true;

        try {

            if(cliente != null && !cliente.isClosed()){
                cliente.close();
            }

            if(emissor != null && !emissor.isClosed()){
                emissor.close();
            }
        } catch (Exception e) {
            if(paradaManual) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro em DesktopEnvioThread::fecharServidor - " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DesktopRecepcaoThread implements Runnable{
    private boolean paradaManual = false;
    private Socket cliente;
    private ServerSocket receptor;

    @Override
    public void run() {

        while (!paradaManual) {

            try {
                receptor = new ServerSocket(Util.portaRecepcaoDesktop);
                cliente = receptor.accept();

                ObjectInputStream reader = new ObjectInputStream(cliente.getInputStream());
                String msg = reader.readUTF();

//                JOptionPane.showMessageDialog(null, "Mensagem: " + msg, "Recepção", JOptionPane.INFORMATION_MESSAGE);

                reader.close();
                cliente.close();
                receptor.close();

                FileWriter fWriter = new FileWriter(Util.pathRepDesktop, true);
                fWriter.write(msg + System.lineSeparator());

                fWriter.close();
            } catch (Exception e) {
                if(!paradaManual) {
                    JOptionPane.showMessageDialog(null, "Erro em DesktopRecepcaoThread::run - " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
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

            if(receptor != null && !receptor.isClosed()){
                receptor.close();
            }
        } catch (Exception e) {
            if(paradaManual) {
                JOptionPane.showMessageDialog(null, "Erro em DesktopRecepcaoThread::fecharServidor - " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

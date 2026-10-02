import javax.swing.*;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class FrmChat extends JFrame {
    public String msg;
    private JEditorPane edtConversa;
    private JTextField txtMensagem;
    private JScrollPane scrConversa;
    private JLabel lblMensagem;
    private JLabel lblModo;
    private JComboBox cbModo;
    private JLabel lblEmoji;
    private JComboBox cbEmoji;
    private JButton btnEnviar;
    private JPanel panelChat;

    public void gerarEEnviarMsg() {
        this.msg = "";

        String avatarPath = Util.avatar;
        URL resource = Util.class.getResource("/" + avatarPath.replace("./", ""));
        if (resource != null) {
            this.msg += "<img src='" + resource.toExternalForm() + "' width='20' height='20'>";
        }
        this.msg += "<font color='" + Util.cor + "'>" + Util.nickname + "</font>";

        int emojiSize = 16;

        if(cbModo.getSelectedItem().toString().equals("Fala")){
            this.msg += "<b> Fala: </b>";
            this.msg += txtMensagem.getText();

        } else if(cbModo.getSelectedItem().toString().equals("Grita")){
            this.msg += "<b><u> GRITA: </u></b>";
            this.msg += "<font color='red' size='+1'>" + txtMensagem.getText().toUpperCase() + "</font>";
            emojiSize += 8;
        } else if(cbModo.getSelectedItem().toString().equals("Xinga")){
            this.msg += "<b><u><i><font color='red'> Xinga: </font></i></u></b>";
            this.msg += "<b><font color='red' size='+4'>" + txtMensagem.getText().toUpperCase() + "!!!!!</font><b>";
            emojiSize += 12;
        }

        if(!cbEmoji.getSelectedItem().toString().equals("Nenhum")){
            String emojiPath = "./images/" + cbEmoji.getSelectedItem().toString() + ".png";
            resource = Util.class.getResource("/" + emojiPath.replace("./", ""));
            if (resource != null) {
                this.msg += " <img src='" + resource.toExternalForm() + "' width='" + emojiSize + "' height='" + emojiSize + "'>";
            }
        }
        this.msg += "<br>";

        ArrayList<String> codigos = new ArrayList<String>();
        ArrayList<String> simbolos = new ArrayList<String>();

        codigos.add(":-)");
        simbolos.add("&#128513;");

        codigos.add(";-)");
        simbolos.add("&#128521;");

        codigos.add("<3");
        simbolos.add("&#129294;");

        codigos.add("</3");
        simbolos.add("&#128148;");

        codigos.add("<ok>");
        simbolos.add("&#128076;");

        codigos.add("-_-");
        simbolos.add("&#128529;");

        codigos.add("s2_s2");
        simbolos.add("&#128525;");

        for(int i = 0; i < codigos.size(); i++){
            this.msg = this.msg.replace(codigos.get(i), simbolos.get(i));
        }

        System.out.println(this.msg);

        txtMensagem.setText("");
//        edtConversa.setText(this.msg);

        try {
            Socket cliente = new Socket(Util.serverIpAddress, 6662);
            ObjectOutputStream output = new ObjectOutputStream(cliente.getOutputStream());
            output.flush();

            output.writeUTF(this.msg);

            output.close();
            cliente.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro cliente ao enviar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public FrmChat() {
        panelChat.setBorder(BorderFactory.createTitledBorder("Chat"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panelChat);
        setVisible(true);

        Thread.ofVirtual().start(() -> {
            while (true) {
                try {
                    Socket cliente = new Socket(Util.serverIpAddress, 6661);
                    ObjectInputStream input = new ObjectInputStream(cliente.getInputStream());
                    String msgs = input.readUTF();

                    input.close();
                    cliente.close();

                    HTMLDocument doc = (HTMLDocument) edtConversa.getDocument();
                    HTMLEditorKit kit = (HTMLEditorKit) edtConversa.getEditorKit();

                    edtConversa.setText("");
                    kit.insertHTML(doc, doc.getLength(), msgs, 0, 0, null);
                    edtConversa.setCaretPosition(doc.getLength());

                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao receber mensagens no cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerarEEnviarMsg();
            }
        });

        txtMensagem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    gerarEEnviarMsg();
                }
            }
        });
    }
}

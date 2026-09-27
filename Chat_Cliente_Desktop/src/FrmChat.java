import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.net.URL;

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

    public void gerarMsg() {
        this.msg = "";

        String avatarPath = Util.avatar;
        URL resource = Util.class.getResource("/" + avatarPath.replace("./", ""));
        if (resource != null) {
            System.out.println(resource.toExternalForm());
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

        edtConversa.setText(this.msg);
    }

    FrmChat() {
        panelChat.setBorder(BorderFactory.createTitledBorder("Chat"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panelChat);
        setVisible(true);
        btnEnviar.addKeyListener(new KeyAdapter() {
        });
        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerarMsg();
            }
        });
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        FrmChat frmChat = new FrmChat();
        frmChat.pack();
        frmChat.setLocationRelativeTo(null);
        frmChat.setVisible(true);
    }
}

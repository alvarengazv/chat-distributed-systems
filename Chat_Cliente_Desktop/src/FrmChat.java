import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;

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

        this.msg += "<img src='" + Util.avatar + "' width='20px' height='20px'>";
        this.msg += "<font color='" + Util.cor + "'>" + Util.nickname + "</font>";

        if(cbModo.getSelectedItem().toString().equals("Fala")){
            this.msg += "<b> Fala: </b>";
            this.msg += txtMensagem.getText();

        } else if(cbModo.getSelectedItem().toString().equals("Grita")){
            this.msg += "<b><u><i> GRITA: </i></u></b>";
            this.msg += "<font color='tomato' size='+1'>" + txtMensagem.getText().toUpperCase() + "</font>";

        } else if(cbModo.getSelectedItem().toString().equals("Xinga")){
            this.msg += "<b><u><font color='red'> Xinga: </font></u></b>";
            this.msg += "<b><font color='red' size='+4'>" + txtMensagem.getText().toUpperCase() + "!!!!!</font><b>";

        }

        this.msg += "<img src='./images/" + cbEmoji + ".png' width='10px' height='10px'>";
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

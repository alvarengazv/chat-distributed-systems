import javax.swing.*;
import java.awt.event.*;

public class FrmLogin extends JFrame {
    private JTextField txtNick;
    private JButton btnEntrar;
    private JLabel lblNick;
    private JRadioButton radAzul;
    private JLabel lblCor;
    private JRadioButton radPreto;
    private JRadioButton radVermelho;
    private JPanel panelLogin;
    private JPanel panelEntrar;
    private JLabel lblAvatar;
    private JRadioButton radMenina;
    private JLabel lblMenina;
    private JRadioButton radMenino;
    private JLabel lblMenino;
    private JRadioButton radNaoTem;
    private JLabel lblNaoTem;

    FrmLogin() {
        setContentPane(panelLogin);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        lblMenina.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                radMenina.setSelected(true);
            }
        });

        lblMenino.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                radMenino.setSelected(true);
            }
        });

        lblNaoTem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                radNaoTem.setSelected(true);
            }
        });

        lblMenina.setLabelFor(radMenina);
        lblMenino.setLabelFor(radMenino);
        lblNaoTem.setLabelFor(radNaoTem);
        panelLogin.setBorder(BorderFactory.createTitledBorder("Login"));
        panelLogin.setVisible(true);
        btnEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarLogin();
            }
        });

        txtNick.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    enviarLogin();
                }
            }
        });
    }

    public void enviarLogin(){
        Util.nickname =  txtNick.getText().trim();
        if(!Util.nickname.isEmpty()) {
            Util.nickname = Util.nickname.substring(0, 1).toUpperCase() + Util.nickname.substring(1);
            if (radAzul.isSelected()) {
                Util.cor = "blue";
            } else if (radPreto.isSelected()) {
                Util.cor = "black";
            } else if (radVermelho.isSelected()) {
                Util.cor = "red";
            }

            if (radMenino.isSelected()) {
                Util.avatar = "./images/homem.png";
            } else if (radMenina.isSelected()) {
                Util.avatar = "./images/menina.png";
            } else if (radNaoTem.isSelected()) {
                Util.avatar = "./images/sinal-de-interrogacao.png";
            }

            FrmChat frmChat = new FrmChat();
            frmChat.pack();
            frmChat.setLocationRelativeTo(null);
            frmChat.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(FrmLogin.this,
                    "Por favor, digite um nickname!",
                    "Campos Obrigatórios",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}

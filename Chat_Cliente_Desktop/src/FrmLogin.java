import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        });
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        FrmLogin frmLogin = new FrmLogin();

        frmLogin.setContentPane(frmLogin.panelLogin);
        frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmLogin.pack();
        frmLogin.setLocationRelativeTo(null);
        frmLogin.setVisible(true);
    }
}

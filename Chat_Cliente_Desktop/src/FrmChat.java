import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrmChat extends JFrame {
    public String msg;
    private JEditorPane edtConversa;
    private JTextField txtMensagem;
    private JScrollPane scrConversa;
    private JLabel lblMensagem;
    private JLabel lblModo;
    private JComboBox cbModo;
    private JButton btnEnviar;
    private JPanel panelChat;
    private JPopupMenu mnuEmojis;
    private JButton btnEmojis;
    private String figuraSelecionada;
    private volatile boolean emManutencao = false;

    private static String escaparHtml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String textoHtml(String texto) {
        return textoHtml(texto, false);
    }

    private static String textoHtml(String texto, boolean maiusculas) {
        String convertido = texto.replace("</3", "💔").replace("<3", "❤️")
                .replace("<ok>", "👌").replace(":-)", "😁").replace(";-)", "😉")
                .replace("-_-", "😑").replace("s2_s2", "😍");
        return escaparHtml(maiusculas ? convertido.toUpperCase(Locale.ROOT) : convertido);
    }

    private static String localizarImagens(String html) {
        Pattern padrao = Pattern.compile("(?<=src=['\"])(?:(?:jar:)?file:[^'\"]*/images/|chatimg:)([a-z-]+\\.png)");
        Matcher busca = padrao.matcher(html);
        StringBuffer resultado = new StringBuffer();
        while (busca.find()) {
            URL imagem = Util.class.getResource("/images/" + busca.group(1));
            busca.appendReplacement(resultado, Matcher.quoteReplacement(
                    imagem == null ? busca.group() : imagem.toExternalForm()));
        }
        busca.appendTail(resultado);
        return resultado.toString();
    }

    private void configurarSeletor() {
        JTabbedPane abas = new JTabbedPane();

        JPanel painelEmojis = new JPanel(new GridLayout(0, 6, 4, 4) );
        for (String emoji : new String[]{
                "😀", "😁", "😂", "😊",
                "😍", "😘", "😎",
                "😮", "😢", "😭", "😡", "👍",
                "👎", "❤️"
        }) {
            JButton botao = new JButton(emoji);
            botao.addActionListener(e -> {
                txtMensagem.replaceSelection(emoji);
                mnuEmojis.setVisible(false);
                txtMensagem.requestFocusInWindow();
            });
            painelEmojis.add(botao);
        }

        JPanel painelFiguras = new JPanel(new GridLayout(2, 3, 4, 4));

        for (String nome : new String[]{"coracao", "beijo", "dinheiro", "homem", "menina", "sinal-de-interrogacao"}) {
            URL url = Util.class.getResource("/images/" + nome + ".png");
            if (url == null) continue;

            Image imagem = new ImageIcon(url).getImage()
                    .getScaledInstance(24, 24, Image.SCALE_SMOOTH);

            JButton botao = new JButton(new ImageIcon(imagem));
            botao.setToolTipText(nome);
            botao.addActionListener(e -> {
                mnuEmojis.setVisible(false);
                if (emManutencao) {
                    gerarEEnviarMsg();
                    return;
                }
                figuraSelecionada = nome;
                gerarEEnviarMsg();
            });
            painelFiguras.add(botao);
        }

        abas.addTab("\uD83D\uDE00", criarAreaRolavel(painelEmojis));
        abas.addTab("\uD83D\uDDBC\uFE0F", criarAreaRolavel(painelFiguras));

        mnuEmojis.removeAll();
        mnuEmojis.add(abas);

        btnEmojis.addActionListener(e ->
                mnuEmojis.show(btnEmojis, 0, btnEmojis.getHeight()));
    }

    private JScrollPane criarAreaRolavel(JPanel painel) {
        JScrollPane area = new JScrollPane(painel);
        area.setPreferredSize(new Dimension(300, 180));
        return area;
    }

    public void gerarEEnviarMsg() {
        if(txtMensagem.getText().trim().equals("")){
            return;
        }
        if (emManutencao) {
            JOptionPane.showMessageDialog(this,
                    "Não é possível enviar mensagens enquanto o servidor está em manutenção.",
                    "Servidor em manutenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.msg = "";

        String avatarPath = Util.avatar;
        URL resource = Util.class.getResource("/" + avatarPath.replace("./", ""));
        if (resource != null) {
            this.msg += "<img src='chatimg:" + avatarPath.substring(avatarPath.lastIndexOf('/') + 1)
                    + "' width='20' height='20'>";
        }
        this.msg += "<font color='" + Util.cor + "'>" + escaparHtml(Util.nickname) + "</font>";

        int emojiSize = 16;
        String texto = textoHtml(txtMensagem.getText());

        if(cbModo.getSelectedItem().toString().equals("Fala")){
            this.msg += "<b> Fala: </b>";
            this.msg += texto;

        } else if(cbModo.getSelectedItem().toString().equals("Grita")){
            this.msg += "<b><u> GRITA: </u></b>";
            this.msg += "<font color='red' size='+1'>" + textoHtml(txtMensagem.getText(), true) + "</font>";
            emojiSize += 8;
        } else if(cbModo.getSelectedItem().toString().equals("Xinga")){
            this.msg += "<b><u><i><font color='red'> Xinga: </font></i></u></b>";
            this.msg += "<b><font color='red' size='+4'>" + textoHtml(txtMensagem.getText(), true) + "!!!!!</font></b>";
            emojiSize += 12;
        }

        if (figuraSelecionada != null) {
            URL figura = Util.class.getResource(
                    "/images/" + figuraSelecionada + ".png");

            if (figura != null) {
                this.msg += " <img src='chatimg:" + figuraSelecionada + ".png"
                        + "' width='" + emojiSize
                        + "' height='" + emojiSize + "'>";
            }
        }
        this.msg += "<br>";

        try (Socket cliente = new Socket()) {
            cliente.connect(new InetSocketAddress(Util.serverIpAddress, 6662), 2000);
            try (ObjectOutputStream output = new ObjectOutputStream(cliente.getOutputStream())) {
                output.writeUTF(this.msg);
                output.flush();
            }
            txtMensagem.setText("");
            figuraSelecionada = null;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível enviar a mensagem. O servidor pode estar em manutenção. Tente novamente.",
                    "Envio indisponível", JOptionPane.ERROR_MESSAGE);
        }
    }

    public FrmChat() {
        panelChat.setBorder(BorderFactory.createTitledBorder("Chat"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panelChat);
        configurarSeletor();
        setVisible(true);

        Thread.ofVirtual().start(() -> {
            String historico = "";
            while (true) {
                try (Socket cliente = new Socket()) {
                    cliente.connect(new InetSocketAddress(Util.serverIpAddress, 6661), 2000);
                    cliente.setSoTimeout(2000);
                    try (ObjectInputStream input = new ObjectInputStream(cliente.getInputStream())) {
                        historico = localizarImagens(input.readUTF());
                    }
                    String mensagens = historico;
                    SwingUtilities.invokeLater(() -> {
                        edtConversa.setText("<html>" + mensagens + "</html>");
                        edtConversa.setCaretPosition(edtConversa.getDocument().getLength());
                    });
                    emManutencao = false;
                } catch (IOException e) {
                    if (!emManutencao) {
                        String mensagens = historico;
                        SwingUtilities.invokeLater(() ->
                                edtConversa.setText("<html>" + mensagens + "<b>Em manutenção...</b></html>"));
                    }
                    emManutencao = true;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
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

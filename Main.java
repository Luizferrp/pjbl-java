import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

// Classe abstrata para representar usuários
abstract class Usuario {
    protected String login;
    protected String senha;

    public Usuario(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

    public abstract String saudacao();
}

// Classe de exceção personalizada
class MeuException extends Exception {
    public MeuException(String message) {
        super(message);
    }
}

// Classe para representar um usuário registrado
class UsuarioRegistrado extends Usuario {
    public UsuarioRegistrado(String login, String senha) {
        super(login, senha);
    }

    @Override
    public String saudacao() {
        return "Olá novamente, " + login + "!";
    }
}

// Classe para representar o arquivo CSV
class ArquivoCSV {
    private static final String NOME_ARQUIVO = "usuarios.csv";

    public static void adicionarUsuario(String login, String senha) {
        try {
            FileWriter fileWriter = new FileWriter(NOME_ARQUIVO, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);
            printWriter.println(login + "," + senha);
            printWriter.close();
        } catch (IOException e) {
          try{
            throw new MeuException("Erro ao adicionar usuario usuário no arquivo CSV.");
          } catch (MeuException ex) {
            System.out.println("Erro ao verificar usuário!");
          }
        }
    }

    public static boolean verificarUsuario(String login, String senha) {
        try {
            FileReader fileReader = new FileReader(NOME_ARQUIVO);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String linha;
            while ((linha = bufferedReader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados[0].equals(login) && dados[1].equals(senha)) {
                    bufferedReader.close();
                    return true;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
          try{
          throw new MeuException("Erro ao verificar usuário no arquivo CSV.");
          } catch (MeuException ey) {
            System.out.println("Erro ao verificar Usuario no arquivo csv");
          }
        }
        return false;
    }
}

// Classe para armazenar o histórico de login
class HistoricoLogin {
    private ArrayList<String> registros;

    public HistoricoLogin() {
        registros = new ArrayList<>();
    }

    public void adicionarRegistro(String login) {
        registros.add(login + "," + new Date());
    }

    public void escreverCSV() {
        try {
            FileWriter fileWriter = new FileWriter("historico.csv");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);

            for (String registro : registros) {
                printWriter.println(registro);
            }

            printWriter.close();
        } catch (IOException e) {
          try{
            throw new MeuException("erro ao verificar usuario");
          } catch (MeuException ez) {
            System.out.println("Erro ao verificar usuário!");
          }
        }
    }
}

// Classe principal do aplicativo
class Aplicativo extends JFrame {
    private JTextField textFieldLogin;
    private JPasswordField passwordField;
    private JLabel labelResultado;
    private HistoricoLogin historico;

    public Aplicativo() {
        super("Aplicativo de Login");

        // Configurações da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(new FlowLayout());

        // Componentes da interface
        JLabel labelLogin = new JLabel("Login:");
        textFieldLogin = new JTextField(15);
        JLabel labelSenha = new JLabel("Senha:");
        passwordField = new JPasswordField(15);
        JButton buttonRegistro = new JButton("Registro");
        JButton buttonEntrar = new JButton("Entrar");
        labelResultado = new JLabel("");
        historico = new HistoricoLogin();

        // Adicionando os componentes à janela
        add(labelLogin);
        add(textFieldLogin);
        add(labelSenha);
        add(passwordField);
        add(buttonRegistro);
        add(buttonEntrar);
        add(labelResultado);

        // Listener para o botão de registro
        buttonRegistro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = textFieldLogin.getText();
                String senha = new String(passwordField.getPassword());

                if (login.isEmpty() || senha.isEmpty()) {
                    labelResultado.setText("Preencha todos os campos!");
                } else {
                    ArquivoCSV.adicionarUsuario(login, senha);
                    historico.adicionarRegistro(login);
                    labelResultado.setText("Usuário registrado com sucesso!");
                }
            }
        });

        // Listener para o botão de entrar
        buttonEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = textFieldLogin.getText();
                String senha = new String(passwordField.getPassword());

                if (login.isEmpty() || senha.isEmpty()) {
                    labelResultado.setText("Preencha todos os campos!");
                } else {
                    boolean usuarioExiste = ArquivoCSV.verificarUsuario(login, senha);
                    if (usuarioExiste) {
                        historico.adicionarRegistro(login);
                        historico.escreverCSV();
                        Usuario usuario = new UsuarioRegistrado(login, senha);
                        labelResultado.setText(usuario.saudacao());
                    } else {
                        labelResultado.setText("Usuário não encontrado!");
                    }
                }
            }
        });

        // Exibição da janela
        setVisible(true);
    }

    public static void main(String[] args) {
        new Aplicativo();
    }
}


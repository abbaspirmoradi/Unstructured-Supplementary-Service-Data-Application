package ir.org.acm.ui;

import ir.org.acm.controller.PatternControllerInterface;
import ir.org.acm.controller.ReflectionControllerInterface;
import ir.org.acm.framework.Autowired;
import ir.org.acm.framework.DiContext;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is main form for USSD Application
 */
public class MainForm extends JFrame {

    @Autowired(name = "ReflectionInterface")
    private ReflectionControllerInterface reflectionInterface;

    @Autowired(name = "PatternInterface")
    private PatternControllerInterface patternInterface;

    private final String packageName = "ir.org.acm.controller";

    private JLabel classNameLabel;
    private JComboBox classesComboBox;
    private JLabel methodNameLabel;
    private JComboBox methodsComboBox;
    private JLabel patternLable;
    private JTextArea patternText;


    public MainForm() {
        new DiContext().injectIn(this);
        initUI();
    }

    private void initUI() {
        //initial main form style
        setTitle("USSD code manager");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu(" ");
        menuBar.add(fileMenu);

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(2, 3));
        // gridPanel.setBackground(Color.red);
        Image ussdIcon = new ImageIcon("src/images/ussdIcon.png").getImage();
        setIconImage(ussdIcon);

        //

        List<Class> classes = reflectionInterface.getAllUssdClasses(packageName);
        List<String> classNames = getAllUssdService(classes);
        List<String> methodNames = getAllUssdMethdes(classes);

        classesComboBox = new JComboBox(classNames.toArray());
        methodNameLabel = new JLabel("Method Name:");
        methodsComboBox = new JComboBox(methodNames.toArray());
        patternLable = new JLabel("Pattern :");
        patternText = new JTextArea("", 2, 10);
        JScrollPane scrollPane = new JScrollPane(patternText);
        classNameLabel = new JLabel("Class Name:");

        //initial main form elements


        Border border = BorderFactory.createEtchedBorder();
        Border title = BorderFactory.createTitledBorder(border, "Create Ussd");

        JPanel mainPanel = new JPanel();

        JPanel methodPanel = new JPanel();
        methodPanel.add(methodNameLabel);
        methodPanel.add(methodsComboBox);

        JPanel classPanel = new JPanel();
        classPanel.add(classNameLabel);
        classPanel.add(classesComboBox);

        JPanel patternPanel = new JPanel();
        patternPanel.add(patternLable);
        patternPanel.add(scrollPane);

        mainPanel.add(methodPanel);
        mainPanel.add(classPanel);
        mainPanel.add(patternPanel);
        //  mainPanel.setBackground(Color.lightGray);
        mainPanel.setBorder(title);
        gridPanel.add(mainPanel);

        JPanel submitPanel = new JPanel();
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new SubmitAction());
        submitPanel.add(submitButton);

        add(gridPanel, BorderLayout.CENTER);
        add(submitPanel, BorderLayout.SOUTH);
        pack();

    }

    private List<String> getAllUssdMethdes(List<Class> classes) {

        List<String> methodNames = new ArrayList<>();
        for (Class klass : classes) {
            List<Method> methods = reflectionInterface.getAllUssdMethodes(klass.getName());
            methodNames.addAll(methods.stream().map(Method::getName).collect(Collectors.toList()));
        }
        return methodNames;
    }

    /*
    *
    * */
    private List<String> getAllUssdService(List<Class> classes) {

        List<String> classNames = new ArrayList<>();
        for (Class klass : classes) {
            classNames.add(klass.getName());
        }
        return classNames;
    }


    private static final int port = 12255;
    private static Socket socket;

    public static void main(String[] args) {

        MainForm mainForm = new MainForm();
        mainForm.setVisible(true);
        try {

            int port = 25000;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server Started and listening to the port 25000");

            //Server is running always. This is done using this while(true) loop
            while (true) {
                //Reading the message from the client
                socket = serverSocket.accept();
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String pattern = br.readLine();
                JOptionPane.showMessageDialog(mainForm, "recieved pattern is: " + pattern);

                //send pattern to controller for find matching service
                mainForm.patternInterface.doOperationForReceivedPattern(pattern);

                //Multiplying the number by 2 and forming the return message
                String returnMessage = "operation";
                try {
//                    int numberInIntFormat = Integer.parseInt(pattern);
//                    int returnValue = numberInIntFormat * 2;
//                    returnMessage = String.valueOf(returnValue) + "\n";
                } catch (NumberFormatException e) {
                    //Input was not a number. Sending proper message back to client.
                    returnMessage = "Please send a proper number\n";
                }

                //Sending the response back to the client.
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(returnMessage);
                System.out.println("Message sent to the client is " + returnMessage);
                bw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class SubmitAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            String className = classesComboBox.getSelectedItem().toString();
            String methodName = methodsComboBox.getSelectedItem().toString();
            String pattern = patternText.getText();

        }
    }
}

class  test
{
    public test(){
        System.out.println("3");
    }

    static {
        System.out.println("1");

    }
    {
        System.out.println("2");
    }
    public static void main(String[] args) {
        test tt=new test();
        test tt1=new test();
        System.out.println("4");
    }


}
interface  I{
    default  int tt(){return  tina();}
    default int tina(){return  0;}
    public default     int ttt(){return  4;}
}
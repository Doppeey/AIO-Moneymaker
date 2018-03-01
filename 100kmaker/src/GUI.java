import sun.applet.Main;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GUI {


    public void run(PotatoPicker main) {


        JFrame jFrame = new JFrame("Doppeys AIO Moneymaker");
        jFrame.setSize(250, 130);
        jFrame.setResizable(false);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        jFrame.getContentPane().add(mainPanel);
        JLabel desc = new JLabel("Enter amount of GP to be earned: ");
        mainPanel.add(desc);
        JTextField enterGP = new JTextField();
        mainPanel.add(enterGP);
        JButton goButton = new JButton("Start Script");
        mainPanel.add(goButton);
        jFrame.setVisible(true);
        enterGP.addActionListener(e -> {
            main.targetGP = Integer.parseInt(enterGP.getText());
        });
        goButton.addActionListener(e -> {
            main.startscript = 1;
            main.targetGP = Integer.parseInt(enterGP.getText());
            jFrame.setVisible(false);
        });


    }














}

import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import com.intellij.uiDesigner.core.*;

import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.DepositBox;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.Objects;
import org.osbot.rs07.api.Walking;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.utility.ConditionalSleep;

import javax.swing.*;

@org.osbot.rs07.script.ScriptManifest(name = "Doppeys PotatoPicker 1.0", author = "Doppey", version = 1.0D, info = "Picks potatoes like a boss", logo = "")
public class PotatoPicker extends org.osbot.rs07.script.Script {
    Area WORKPLACE = new Area(new Position(3141, 3270, 0), new Position(3155, 3288, 0));
    Area BANK = new Area(new Position(3092, 3241, 0), new Position(3095, 3245, 0));
    Area GRANDEXCHANGE = new Area(3160, 3487, 3170, 3483);
    private final Image bg = getImage("https://i.imgur.com/MlIJQm8.jpg");
    private int potatoesPicked = 0;
    private int costOfItem = 0;
    private long timeRan;
    private long timeBegan;
    private double gpGained = 0.0D;
    private int totalGpPerHour;
    private int gpPerHour;
    GrandExchange GE = new GrandExchange(this);
    private boolean firstTimeBanking = true;
    private int actualPotatoesPicked = 0;
    private int targetGP = 10000;
    private JPanel content;


    public PotatoPicker() {
        initComponents();
    }

    public void onStart() {
        log("Welcome to Doppeys Potato Picker, if you find any bugs please message me on the forums, enjoy!");
        HashMap<String, Integer> exchangeInfo = getExchangeInfo(1942);
        costOfItem = ((Integer) exchangeInfo.get("selling")).intValue();
        log(Integer.valueOf(costOfItem));
        timeBegan = System.currentTimeMillis();
    }


    public void onExit() {
        log("Thank you for using Doppeys Potato Picker, have a nice day!");
    }


    public int onLoop()
            throws InterruptedException {
        switch (getState()) {
            case "PICKING":
                if (inventory.isEmpty()) {
                    log("Picking..");
                }
                pickPotatoes();
                break;
            case "GOTOWORK":
                log("Going to work...");
                getWalking().webWalk(new Area[]{WORKPLACE});
                break;
            case "BANKING":
                log("Banking...");
                depositBox.open();
                depositBox.depositAll();
                depositBox.close();
                sleep(500);
                if(firstTimeBanking){
                    log("First time banking, checking amount of potatoes in bank!");
                    bank.open();
                    new ConditionalSleep(3000,100) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            if(bank.isOpen()){
                                return true;
                            }
                            return false;
                        }
                    }.sleep();
                    potatoesPicked += bank.getAmount("Potato");
                    sleep(random(500,1000));
                    bank.close();
                    firstTimeBanking = false;

                } else {
                    actualPotatoesPicked += 28;
                }
                break;
            case "GOTOBANK":
                log("Going to bank...");
                getWalking().webWalk(new Area[]{BANK});
                break;
            case "SELLPOTATOES":
                sellStuff();
            case "null": log("NULL LOGGED");
        }

        return 750;
    }

    private void sellStuff()
            throws InterruptedException {
        getBank().open();
        new ConditionalSleep(2000, 300) {
            public boolean condition() throws InterruptedException {
                if (bank.isOpen()) {
                    return true;
                }

                return false;
            }

        }.sleep();
        getBank().enableMode(org.osbot.rs07.api.Bank.BankMode.WITHDRAW_NOTE);
        getBank().withdrawAll("Potato");
        getBank().close();
        new ConditionalSleep(1000, 300) {
            public boolean condition() throws InterruptedException {
                if (!getBank().isOpen()) {
                    return true;
                }
                return false;
            }

        }.sleep();
        getWalking().webWalk(new Area[]{GRANDEXCHANGE});
        new ConditionalSleep(99999, 300) {
            public boolean condition() throws InterruptedException {
                if (GRANDEXCHANGE.contains(myPlayer())) {
                    return true;
                }
                return false;
            }

        }.sleep();
        sleep(random(500,1500));
        log("opening ge");
        GE.openGE();
        new ConditionalSleep(5000,100) {
            @Override
            public boolean condition() throws InterruptedException {
                if(grandExchange.isOpen()){
                    return true;
                }
                return false;
            }
        }.sleep();
        if(!grandExchange.isOpen()){
            GE.openGE();
        }
        sleep(random(500,1500));
        log("Creating offer");
        GE.createSellOffer("Potato", 1, 99999);
        sleep(500);
        log("Confirming");
        grandExchange.confirm();
        sleep(1000);
        log("collecting items");
        GE.collectItems(false);
        new ConditionalSleep(10000,100) {
            @Override
            public boolean condition() throws InterruptedException {
                if(getInventory().getEmptySlotCount() == 27){
                    return true;
                }
                return false;
            }
        }.sleep();
        grandExchange.close();
        sleep(500);

        stop();
    }


    public String getState() {
        if ((potatoesPicked * costOfItem >= targetGP) && (getInventory().isEmpty()))
            return "SELLPOTATOES";
        if ((!getInventory().isFull()) && (!WORKPLACE.contains(myPlayer()))) {
            return "GOTOWORK";
        }
        if ((!getInventory().isFull()) && (WORKPLACE.contains(myPlayer())))
            return "PICKING";
        if ((getInventory().isFull()) && (!BANK.contains(myPlayer())))
            return "GOTOBANK";
        if ((getInventory().isFull()) && (BANK.contains(myPlayer()))) {
            return "BANKING";
        }

        return "null";
    }

    //Potato Picking method
    public void pickPotatoes()
            throws InterruptedException {
        final int inventoryslots = getInventory().getEmptySlotCount();
        ((RS2Object) getObjects().closest(WORKPLACE, new String[]{"Potato"})).interact(new String[]{"Pick"});
        new ConditionalSleep(2000, 100) {
            public boolean condition() throws InterruptedException {
                if (getInventory().getEmptySlotCount() == inventoryslots - 1) {
                    return true;
                }
                return false;
            }
        }.sleep();
    }

    //OSBot Paint
    public void onPaint(Graphics2D g) {
        gpGained = ((actualPotatoesPicked + 28 - inventory.getEmptySlotCount()) * costOfItem);
        gpPerHour = ((int) (gpGained / ((System.currentTimeMillis() - timeBegan) / 3600000.0D)));
        totalGpPerHour = (gpPerHour / 1000);
        DecimalFormat df = new DecimalFormat("#");
        g.drawImage(bg, 1, 337, null);
        g.setColor(Color.yellow);
        timeRan = (System.currentTimeMillis() - timeBegan);
        g.setFont(g.getFont().deriveFont(18.0F));
        g.drawString("Potatoes picked: ", 16, 429);
        g.drawString("" + (potatoesPicked + 28 - inventory.getEmptySlotCount()), 250, 429);
        g.drawString("Money earned: ", 16, 449);
        g.drawString("" + (potatoesPicked + 28 - inventory.getEmptySlotCount()) * costOfItem, 250, 449);
        g.drawString("Time ran:  ", 16, 469);
        g.drawString(ft(timeRan), 250, 469);
        g.drawString("GP/H: ", 16, 489);
        g.drawString(df.format(totalGpPerHour) + " k", 250, 489);
    }

    private Image getImage(String url) {
        try {
            return javax.imageio.ImageIO.read(new URL(url));
        } catch (IOException localIOException) {
        }
        return null;
    }


    //Method that pulls price data from osbuddy servers.
    private HashMap<String, Integer> getExchangeInfo(int id) {
        HashMap<String, Integer> exchangeInfo = new HashMap();
        try {
            URL url = new URL("http://api.rsbuddy.com/grandExchange?a=guidePrice&i=" + id);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
            con.setUseCaches(true);
            BufferedReader br = new BufferedReader(new java.io.InputStreamReader(con.getInputStream()));
            String json = br.readLine();
            br.close();
            json = json.replaceAll("[{}\"]", "");
            String[] items = json.split(",");
            for (String item : items) {
                String[] splitItem = item.split(":");
                exchangeInfo.put(splitItem[0], Integer.valueOf(Integer.parseInt(splitItem[1])));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exchangeInfo;
    }


    //Method for time ran calculation.
    private String ft(long duration) {
        String res = "";
        long days = TimeUnit.MILLISECONDS.toDays(duration);

        long hours = TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));

        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                .toHours(duration));

        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                .toMinutes(duration));
        if (days == 0L) {
            res = hours + ":" + minutes + ":" + seconds;
        } else {
            res = days + ":" + hours + ":" + minutes + ":" + seconds;
        }
        return res;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - lukas doppler
        panel2 = new JPanel();
        label1 = new JLabel();
        textField1 = new JTextField();
        button1 = new JButton();

        //======== panel2 ========
        {

            // JFormDesigner evaluation mark
            panel2.setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                    "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                    javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                    java.awt.Color.red), panel2.getBorder())); panel2.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});


            //---- label1 ----
            label1.setText("Enter amount of gp you want to earn:");
            label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 6f));

            //---- textField1 ----
            textField1.setToolTipText("Amount in GP");

            //---- button1 ----
            button1.setText("GO");

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGroup(panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addComponent(label1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(textField1)))
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(144, 144, 144)
                                .addComponent(button1, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(38, Short.MAX_VALUE))
            );
            panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup()
                    .addGroup(panel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(label1)
                        .addGap(18, 18, 18)
                        .addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(button1)
                        .addContainerGap(20, Short.MAX_VALUE))
            );
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - lukas doppler
    private JPanel panel2;
    private JLabel label1;
    private JTextField textField1;
    private JButton button1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

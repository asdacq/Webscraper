import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.management.ManagementFactory;
import javax.swing.JOptionPane;
import javax.swing.border.Border;


public class UI{
    //private JPanel buttonPanel = new JPanel();
    private JPanel textPanel = new JPanel();
    private JFrame frame = new JFrame("Scraper");
    private JButton add = new JButton("Add");
    private JLabel urlText = new JLabel("Item URL: ");
    private JLabel itemText = new JLabel("Item Name: ");
    private JTextField urlInput = new JTextField( 10);
    private JTextField itemNameInput = new JTextField( 10);
    private Container container = frame.getContentPane();
    private JPanel outputPanel = new JPanel();
    private DefaultListModel model = new DefaultListModel();
    private JList list = new JList(model);
    String data;
    Parser p = new Parser();

    void createUI() {
        readFile();
        frame.setSize(450, 300);

        textPanel.setBorder(BorderFactory.createTitledBorder("Webscraper"));
        textPanel.setPreferredSize(new Dimension(250, 250));
        textPanel.add(urlText);
        textPanel.add(urlInput);
        textPanel.add(itemText);
        textPanel.add(itemNameInput);
        textPanel.add(add);
        container.add(textPanel);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(160, 220));
        outputPanel.add(scrollPane);
        outputPanel.setBorder(BorderFactory.createTitledBorder("List"));
        outputPanel.setPreferredSize(new Dimension(170, 250));
        container.add(outputPanel);

        frame.setLayout(new FlowLayout(0,5,0));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Dialog when button is clicked
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if(urlInput.getText().contains("uniqlo.com") && p.insertItemUniqlo(itemNameInput.getText(), urlInput.getText())) {
                        //p.insertItemUniqlo(itemNameInput.getText(), urlInput.getText());
                        insertItem();
                    }
                    else if(urlInput.getText().contains("gap.com") && p.insertItemBananaGAP(itemNameInput.getText(), urlInput.getText())){
                        //p.insertItemBananaGAP(itemNameInput.getText(), urlInput.getText());
                        insertItem();
                    }
                    else{
                        JOptionPane.showMessageDialog(frame,
                                "URL Error",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                catch(Exception ex){
                    JOptionPane.showMessageDialog(frame,
                            "Check URL Error",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void writeFile(){
        try{
            FileWriter filewriter = new FileWriter("items.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(filewriter);
            for(String items : p.getArrayList()) {
                bufferedWriter.write(items);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        }
        catch (IOException e){
            System.out.println("Creating items.txt file");
        }
    }

    private void readFile(){
        try {
            String line = null;
            FileReader fileReader = new FileReader("items.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                p.getArrayList().add(line);
                model.addElement(line);
            }
            bufferedReader.close();
        }
        catch (IOException e){
            System.out.println("No file found");
        }
    }

    private void insertItem(){
        data = p.getItemName() + " | Price: $" + p.getPrice();
        model.addElement(data);
        writeFile();
        urlInput.setText("");
        itemNameInput.setText("");
    }
}


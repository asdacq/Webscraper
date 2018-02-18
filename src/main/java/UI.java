import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.swing.JOptionPane;
import static java.util.concurrent.TimeUnit.*;



public class UI{
    //private JPanel buttonPanel = new JPanel();
    private JPanel textPanel = new JPanel();
    private JFrame frame = new JFrame("Scraper");
    private JButton add = new JButton("Add");
    private JButton remove = new JButton("Remove");
    private JLabel urlText = new JLabel("Item URL: ");
    private JLabel itemText = new JLabel("Item Name: ");
    private JTextField urlInput = new JTextField( 10);
    private JTextField itemNameInput = new JTextField( 10);
    private Container container = frame.getContentPane();
    private JPanel outputPanel = new JPanel();
    private DefaultListModel model = new DefaultListModel();
    private JList list = new JList(model);
    private Parser p = new Parser();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private List<String> savedList = new ArrayList<String>();

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
        textPanel.add(remove);
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

        refreshItems();

        // Dialog when button is clicked
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if(urlInput.getText().contains("uniqlo.com") && p.insertItemUniqlo(itemNameInput.getText(), urlInput.getText())) {
                        insertItem();
                    }
                    else if(urlInput.getText().contains("gap.com") && p.insertItemBananaGAP(itemNameInput.getText(), urlInput.getText())){
                        insertItem();
                    }
                    else if(urlInput.getText().contains("adidas.com") && p.insertItemAdidas(itemNameInput.getText(), urlInput.getText())){
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
                            "Check URL",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteFromList();
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
            String line;
            FileReader fileReader = new FileReader("items.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                p.getArrayList().add(line);
                // Read items from txt to a list so when refresh, it can fill file with data
                savedList.add(line);
                model.addElement(line.substring(0, line.indexOf("/")-1));
            }
            bufferedReader.close();
        }
        catch (IOException e){
            System.out.println("No file found");
        }
    }

    private void insertItem(){
        String data = p.getItemName() + " | Price: $" + p.getPrice();
        model.addElement(data);
        // Add items to a list so when refresh, it can fill file with data
        savedList.add(p.getArrayList().get(p.getArrayList().size()-1));
        writeFile();
        urlInput.setText("");
        itemNameInput.setText("");
    }
    // Uses a thread to to update UI every 30 minutes
    private void refreshItems(){
        final Runnable runner = new Runnable() {
            public void run() {
                updateListUI();
            }
        };
        final ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(runner, 1, 10, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                future.cancel(true);
            }
        }, 999, DAYS);
    }

    private void eraseAll(){
        p.getArrayList().clear();
        model.clear();
        writeFile();
    }
    // This method allows the items to be added
    private void addItems(){
        for(String s : savedList){
            if( s.contains("uniqlo.com")){
                p.insertItemUniqlo(s.substring(0, s.indexOf("|")-1), s.substring(s.indexOf("/")+1));
            }
            else if(s.contains("gap.com")){
                p.insertItemBananaGAP(s.substring(0, s.indexOf("|")-1), s.substring(s.indexOf("/")+1));
            }
            else { /* do nothing */ }
        }
        writeFile();
        savedList.clear();
        readFile();
    }
    // JPanel is not thread safe, therefore runnable is used to tell a thread what it should do
    private void updateListUI(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                eraseAll();
                addItems();
            }
        });
    }

    private boolean deleteFromList() {
        list.getSelectedIndex();
        model.size();
        int index = list.getSelectedIndex();
        if(index != -1){
            savedList.remove(index);
            eraseAll();
            addItems();
            return true;
        }
        return false;
    }
}


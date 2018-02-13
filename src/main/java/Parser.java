import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.*;
import org.jsoup.select.Elements;
import java.lang.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class Parser {

    private List<String> itemList = new ArrayList<String>();

    public boolean insertItemUniqlo(String itemName, String document) {
        try {
            Document parse = Jsoup.connect(document).get();
            Elements elements = parse.getElementsByTag("script");
            String HTMLString = elements.toString();
            // Get the data after the 'price'
            HTMLString = HTMLString.substring(HTMLString.indexOf("price"));
            // Get the data after the space
            HTMLString = HTMLString.substring(HTMLString.indexOf(' ')+1);
            // Grab just the price
            String price = HTMLString.substring(0, HTMLString.indexOf(','));
            itemList.add(itemName + " | Price: $" +  price + " / " + document);
            return true;
        }
        catch (IOException e) {
            e.getStackTrace();
        }
        return false;
    }

    public boolean insertItemBananaGAP(String itemName, String document){
        try{
            System.out.print(document);
            Document parse = Jsoup.connect(document).get();
            Elements elements = parse.getElementsByTag("h5");
            String HTMLString = elements.toString();
            // Check if there's a markdown price
            if(HTMLString.contains("highlight")){
                HTMLString = HTMLString.substring(HTMLString.indexOf("highlight"));
                HTMLString = HTMLString.substring(HTMLString.indexOf("$")+1);
            }
            else{
                HTMLString = HTMLString.substring(HTMLString.indexOf("$")+1);
            }
            String price = HTMLString.substring(0, HTMLString.indexOf(' '));
            itemList.add(itemName + " | Price: $" +  price + " / " + document);
            return true;
        }
        catch (IOException e){
            e.getStackTrace();
        }
        System.out.print(document);
        return false;
    }

    public String getItemName(){
        String name = itemList.get(itemList.size()-1);
        return name.substring(0, name.indexOf('|')-1);
    }

    public String getPrice(){
        String price = itemList.get(itemList.size()-1);
        return price.substring(price.indexOf('$')+1, price.indexOf('/')-1);
    }

    List<String> getURLList(){
        List<String> temp = new ArrayList<String>();
        for(String s : itemList){
            String url = s.substring(getPrice().indexOf('/')+1);
            temp.add(url);
        }
        return temp;
    }
    List<String> getArrayList(){
        return itemList;
    }
}

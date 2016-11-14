import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

public class Weeper {
    private JPanel pSet = new JPanel();
    private JPanel pDisplay =  new JPanel();
    private JButton[] mines = new JButton[100];
    private JButton confirm = new JButton();
    private JTextArea result = new JTextArea();
    private HashMap<Integer, Integer> map = new HashMap<>();
    private int click = 100;
    public static ImageIcon resize(ImageIcon i) {
        Image ima = i.getImage();
        Image newimg = ima.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }
    public Weeper() {

        
        JFrame frame = new JFrame("Select from μ's");
        frame.setSize(550, 660);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        pSet.setLayout(new GridLayout(2, 10));
        pSet.setBounds(10, 20, 480, 100);
        pSet.setBorder(BorderFactory.createTitledBorder(null, "Good Evening", TitledBorder.CENTER, 0));
        
        pDisplay.setLayout(new GridLayout(10, 10, 10, 10));
        pDisplay.setBounds(0, 130, 500, 500);
        pDisplay.setBorder(BorderFactory.createTitledBorder(null, "Select", TitledBorder.CENTER, 0));
        
        frame.setContentPane(new Container());
        frame.add(pSet);
        frame.add(pDisplay);
        newGame();
        
        
        for (int i = 0; i < 100; i++) {
            mines[i] = new JButton();
            mines[i].setVerticalTextPosition(SwingConstants.BOTTOM);
            mines[i].setHorizontalTextPosition(SwingConstants.CENTER);
            mines[i].setName(new Integer(i).toString());
            mines[i].addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent e){
                    JButton button = (JButton) e.getSource();
                    String name = button.getName();
                    int index = Integer.parseInt(name);
                    if (button.getText().equals("")) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            button.setText("†");
                            if (map.get(Integer.parseInt(button.getName()))==-1) {
                                click--;
                            }
                            System.out.println(click);
                        } else {
                            if (map.get(index) == 0) { // area
                                HashSet<Integer> set = new HashSet<>();
                                release(index, set);
                            } else if (map.get(index) == -1) {
                                disable();
                                return;
                            } else { // show bombs
                                button.setText(map.get(index).toString());
                                click--;
                            }
                            if (click == 0) {
                                result.setText("You Win!");
                                disable();
                            }
                        }
                    } else if (button.getText().equals("†")) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            button.setText("");
                            System.out.println(click);
                            if (map.get(Integer.parseInt(button.getName()))==-1) {
                                click++;
                            }
                            
                        }
                    }
                    
                }
            });
            
            
           
        }
        for (JButton b : mines) {
            pDisplay.add(b);
        }
        confirm = new JButton("Confirm");
        confirm.addActionListener(e-> {
            newGame();
            undisable();
            
        });
        
        pSet.add(result);
        result.setEditable(false);
        pSet.add(confirm);
        frame.setVisible(true);
        
    }
    public static void main(String[] args) throws IOException {
        new Weeper();
        
    }
    private void newGame() {
        for (int i = 0; i < 100; i++) {
            map.put(i, 0);
        }
        int count = 15;
        Random rand = new Random();
        while (count != 0) {
            int n = rand.nextInt(100);
            if (map.get(n) != -1) {
                count--;
                map.put(n, -1);
                store(n);
            }
        }
        click = 100;
    }
    private void disable() {
        for (int i = 0; i < mines.length; i++) {
            mines[i].setText(map.get(i).toString());
            mines[i].setEnabled(false);
        }
    }
    private void undisable() {
        for (int i = 0; i < mines.length; i++) {
            mines[i].setText("");
            mines[i].setEnabled(true);
        }
    }
    private void release(int mine, HashSet<Integer> set) {
        if (map.get(mine) == -1 || mines[mine].getText() != "") {
            return;
        }
        if (!set.contains(mine)) {
            set.add(mine);
            int value = map.get(mine);
            click--;
            if (value != 0) {
                mines[mine].setText(new Integer(value).toString());
                return;
            } else {
                mines[mine].setText("::");
                mines[mine].setEnabled(false);
            }
        }
        if (mine % 10 != 0 && mine / 10 != 0) { // left top
            if (!set.contains(mine - 11)) {
                release(mine - 11, set);
            }
        }
        if (mine / 10 != 0) { // top
            if (!set.contains(mine - 10)) {
                release(mine - 10, set);
            }
        }
        if (mine % 10 != 9 && mine / 10 != 0) { // right top
            if (!set.contains(mine - 9)) {
                release(mine - 9, set);
            }
        }
        if (mine % 10 != 0) { // left
            if (!set.contains(mine - 1)) {
                release(mine - 1, set);
            }
        }
        if (mine % 10 != 9) { // right
            if (!set.contains(mine + 1)) {
                release(mine + 1, set);
            }
        }
        if (mine % 10 != 0 && mine / 10 != 9) { // left bottom
            if (!set.contains(mine + 9)) {
                release(mine + 9, set);
            }
        }
        if (mine / 10 != 9) { // bottom
            if (!set.contains(mine + 10)) {
                release(mine + 10, set);
            }
        }
        if (mine % 10 != 9 && mine / 10 != 9) { // right bottom
            if (!set.contains(mine + 11)) {
                release(mine + 11, set);
            }
        }
    }
    
    private void store(int mine) {
        if (mine % 10 != 0 && mine / 10 != 0) { // left top
            if (map.get(mine - 11) != -1) {
                map.put(mine - 11, map.get(mine - 11) + 1);
            }
        }
        if (mine / 10 != 0) { // top
            if (map.get(mine - 10) != -1) {
                map.put(mine - 10, map.get(mine - 10) + 1);
            }
        }
        if (mine % 10 != 9 && mine / 10 != 0) { // right top
            if (map.get(mine - 9) != -1) {
                map.put(mine - 9, map.get(mine - 9) + 1);
            }
        }
        if (mine % 10 != 0) { // left
            if (map.get(mine - 1) != -1) {
                map.put(mine - 1, map.get(mine - 1) + 1);
            }
        }
        if (mine % 10 != 9) { // left
            if (map.get(mine + 1) != -1) {
                map.put(mine + 1, map.get(mine + 1) + 1);
            }
        }
        if (mine % 10 != 0 && mine / 10 != 9) { // left bottom
            if (map.get(mine + 9) != -1) {
                map.put(mine + 9, map.get(mine + 9) + 1);
            }
        }
        if (mine / 10 != 9) { // bottom
            if (map.get(mine + 10) != -1) {
                map.put(mine + 10, map.get(mine + 10) + 1);
            }
        }
        if (mine % 10 != 9 && mine / 10 != 9) { // right bottom
            if (map.get(mine + 11) != -1) {
                map.put(mine + 11, map.get(mine + 11) + 1);
            }
        }
    }
}

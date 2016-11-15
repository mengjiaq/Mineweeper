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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * MineWeeper-3D
 * @author qmj
 *
 */
public class Mine {

    private int SIZE = 5; // SIZE of the cube
    private int SQUARE_SIZE = SIZE * SIZE;
    private int CUBE_SIZE = SQUARE_SIZE * SIZE;
    private int mark = 0;
    private int COUNT = CUBE_SIZE / 7;

    private int CLICK = CUBE_SIZE - COUNT;
    private int click = CLICK;
    private JPanel pSet = new JPanel();
    private JPanel[] pDisplay =  new JPanel[SIZE];
    private JButton[] mines = new JButton[CUBE_SIZE];
    private JButton confirm = new JButton();
    private JTextField result = new JTextField();
    private HashMap<Integer, Integer> map = new HashMap<>();
    public static ImageIcon reSIZE(ImageIcon i) {
        Image ima = i.getImage();
        Image newimg = ima.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }
    public Mine() {

        result.setHorizontalAlignment(JTextField.CENTER);
        JFrame frame = new JFrame("Mine Weeper");
        frame.setSize(SQUARE_SIZE * 30, 60 * SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        pSet.setLayout(new GridLayout(2, 10));
        pSet.setBounds(0, 20, SQUARE_SIZE * 30, 80);
        pSet.setBorder(BorderFactory.createTitledBorder(null, "Good Evening", TitledBorder.CENTER, 0));
        frame.setContentPane(new Container());
        frame.add(pSet);
        for (int i = 0; i < SIZE; i++) {
            pDisplay[i] = new JPanel();
            pDisplay[i].setLayout(new GridLayout(SIZE, SIZE, 3,3));
            pDisplay[i].setBounds(i * SIZE * 30, 120, SIZE*30, SIZE*30);
            pDisplay[i].setBorder(BorderFactory.createTitledBorder(null, "Select", TitledBorder.CENTER, 0));
            frame.add(pDisplay[i]);
        }
        
        newGame();
        
        for (int i = 0; i < CUBE_SIZE; i++) {
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
                            button.setText("✗");
                            mark++;
                            result.setText("You have marked " + mark + "/" + COUNT+" mine(s)");
                            //System.out.println(click);
                        } else {
                            if (map.get(index) == 0) { // area
                                HashSet<Integer> set = new HashSet<>();
                                release(index, set);
                            } else if (map.get(index) == -1) {
                                disable();
                                result.setText("You Lose!");
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
                    } else if (button.getText().equals("✗") && e.getButton() == MouseEvent.BUTTON3) {
                        button.setText("");
                    }
                    
                }
            });
            
            
           
        }
        for (int z = 0; z < SIZE; z++) {
            for (int i = z * SQUARE_SIZE; i < (z + 1) *SIZE* SIZE; i++) {
                pDisplay[z].add(mines[i]);
            }
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
        new Mine();
        
    }
    private void newGame() {
        click = CLICK;
        mark = 0;
        for (int i = 0; i < CUBE_SIZE; i++) {
            map.put(i, 0);
        }
        
        
        Random rand = new Random();
        int count = COUNT;
        while (count != 0) {
            int n = rand.nextInt(CUBE_SIZE);
            if (map.get(n) != -1) {
                count--;
                map.put(n, -1);
                store(n);
            }
        }
    }
    private void disable() {
        for (int i = 0; i < CUBE_SIZE; i++) {
            if (map.get(i).intValue()==-1) {
                mines[i].setText("✹");
            } else if (map.get(i).intValue()==0){
                mines[i].setText("::");
            }else {
                mines[i].setText(map.get(i).toString());
            }
            
            mines[i].setEnabled(false);
        }
    }
    private void undisable() {
        for (int i = 0; i < CUBE_SIZE; i++) {
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
        if (mine / SQUARE_SIZE > 0 && !set.contains(mine - SQUARE_SIZE)) { // z-axis minus
            release(mine - SQUARE_SIZE, set);
        }
        if (mine / SQUARE_SIZE < SIZE - 1 && !set.contains(mine + SQUARE_SIZE)) { // z-axis plus
            release(mine + SQUARE_SIZE, set);
        }
        if (mine % SQUARE_SIZE >= SIZE && !set.contains(mine - SIZE)) { // y-axis minus
            release(mine - SIZE, set);
        }
        if (mine % SQUARE_SIZE < SIZE * (SIZE - 1) && !set.contains(mine + SIZE)) { // y-axis plus
            release(mine + SIZE, set);
        }
        if (mine % (SIZE) != 0 && !set.contains(mine - 1)) { // x-axis minus
            release(mine - 1, set);
        }
        if (mine % (SIZE) != SIZE - 1 && !set.contains(mine + 1)) { // x-axis plus
            release(mine + 1, set);
        }
    }
    
    private void store(int mine) {
        result.setText("There are " + COUNT + " bombs!");
        int flag = 0;
        if (mine / SQUARE_SIZE > 0) { // z-axis minus
            flag = map.get(mine - SQUARE_SIZE);
            if (flag != -1) {
                map.put(mine - SQUARE_SIZE, flag + 1);
            }
        }
        if (mine / SQUARE_SIZE < SIZE - 1){ // z-axis plus
            flag = map.get(mine + SQUARE_SIZE);
            if (flag != -1) {
                map.put(mine + SQUARE_SIZE, flag + 1);
            }
        }
        if (mine % SQUARE_SIZE >= SIZE) { // y-axis minus
            flag = map.get(mine - SIZE);
            if (flag != -1) {
                map.put(mine - SIZE, flag + 1);
            }
        }
        if (mine % SQUARE_SIZE < SIZE * (SIZE - 1)) { // y-axis plus
            flag = map.get(mine + SIZE);
            if (flag != -1) {
                map.put(mine + SIZE, flag + 1);
            }
        }
        if (mine % (SIZE) != 0) { // x-axis minus
            flag = map.get(mine - 1);
            if (flag != -1) {
                map.put(mine - 1, flag + 1);
            }
        }
        if (mine % (SIZE) != SIZE - 1) { // x-axis plus
            flag = map.get(mine + 1);
            if (flag != -1) {
                map.put(mine + 1, flag + 1);
            }
        }
    }
}

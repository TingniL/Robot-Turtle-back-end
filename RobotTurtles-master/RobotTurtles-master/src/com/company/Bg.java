package com.company;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Bg extends JPanel {
    public void paintComponent(Graphics g){
        try {
            Image img = ImageIO.read(new File("ImgCartes/bg.png"));
            g.drawImage(img, 0, 0, this);
            //Pour une image de fond
            //g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font font = new Font("Arial", Font.BOLD, 50);
        g.setFont(font);
        g.setColor(Color.GREEN);
        g.drawString("ROBOT TURTLES", 95,60);
    }
    public class Fond extends JPanel{
        public void paintComponent(Graphics g){
            try {
                Image img = ImageIO.read(new File("ImgCartes/plateau.jpg"));
                g.drawImage(img, 0, 0, this);
                //Pour une image de fond
                //g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
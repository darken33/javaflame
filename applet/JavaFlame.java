/**
* 03/21/2002 - 11:21:16
*
* JavaFlame - Animation de feu 
* Copyright (C) 2002 Philippe BOUSQUET
* e-mail : Darken@tuxfamily.org
* site : http://darken.tuxfamily.org
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import java.awt.*;
import java.util.*;

/**
* Algorithme de flammes
* 
* On possède une table des couelurs represntants les flames de 64 couleurs
* On possède une tableaux à deux dimentions representants la couleurs des points du graphismes
*
* la ligne du bas est = Noir
* La ligne du bas -1 possede une nombre aléatoires de foyers 
* les autres points sont égals à  col(x,y) = (col(x-1,y+1) + col(x,y+1) + col(x+1,y+1) +col(x,y+2)) /4
*/
public class JavaFlame extends java.applet.Applet implements Runnable
{
        private Thread runner;
        int plan [][];
        int pal [][]=new int [64][3];
        int Ymax;
        int Xmax;
        int Ystart;
        int NbFoy;
        Vector palette = new Vector(); 
        
        // double buffering
        private Graphics off;
        private Image offImg;
        
        public void initPal()
        {
                int j=0;
                // Noir -> Bleu -> Noir (8)
                for (int i=0;i<4;i++)
                {
                  pal[0][0]=0;
                  pal[0][1]=0;
                  pal[0][2]=i*16;
                }
                j=48;
                for (int i=4;i<8;i++)
                {
                  pal[0][0]=0;
                  pal[0][1]=0;
                  pal[0][2]=j;
                  j-=16;
                }
                // couleurs rouge -> jaune (16)
                j=32;
                for (int i=8;i<24;i++)
                {
                        pal[i][0]=j;
                        pal[i][1]=0;
                        pal[i][2]=0;
                        j+=16;
                        if (j>255) j=255;
                }
                // couleurs rouge -> jaune (16)
                j=32;
                for (int i=24;i<40;i++)
                {
                        pal[i][0]=255;
                        pal[i][1]=j;
                        pal[i][2]=0;
                        j+=16;
                        if (j>255) j=255;
                }
                // couleurs rouge -> jaune (16)
                j=255;
                for (int i=40;i<56;i++)
                {
                        pal[i][0]=255;
                        pal[i][1]=j;
                        pal[i][2]=0;
                        j-=32;
                        if (j<0) j=0;
                }
                // couleurs jaune -> blanc (8)
                j=32;
                for (int i=56;i<64;i++)
                {
                        pal[i][0]=255;
                        pal[i][1]=j;
                        pal[i][2]=j;
                        j+=32;
                        if (j>255) j=255;
                }
                for (int i=0;i<64;i++) palette.addElement(new Color(pal[i][0],pal[i][1],pal[i][2]));
        }
        
        public void start()
        {
                if (runner == null)
                {
                        runner = new Thread(this);
                        runner.start();
                }
        }
        
        public void init()
        {
                Xmax=size().width;
                Ymax=(int) size().height;
                Ystart=0;    
                initPal();
                // image for double bffering
                offImg = createImage(size().width,size().height);
                // create a Graphics object from the image
                off = offImg.getGraphics();
                off.setColor(Color.black);
                off.fillRect(0,0,size().width,size().height);
                plan=new int[Xmax][Ymax];
                NbFoy=(int) Xmax/2;
                setBackground(Color.black);
                for (int i=0;i<Xmax;i++)
                {
                        for (int j=0;j<Ymax;j++) plan[i][j]=0;
                }                
        }
        
        public void traceFoyer()
        {
                Random r= new Random();
                for (int i=0;i<Xmax;i++) plan[i][Ymax-2]=0;
                for (int i=0;i<NbFoy;i++)
                {
                        int j=(int) (r.nextFloat() * Xmax);
                        plan[j][Ymax-2]=63;
                }
        }
 
        public void calculeColor(Graphics g)
        {
                int c1,c2,c3,c4;
                for (int i=0;i<Xmax;i++)
                {
                        for (int j=Ymax-3;j>0;j--)
                        {
                             if (i==0) c1=0;
                             else c1=plan[i-1][j+1];
                             c2=plan[i][j+1];
                             if (i==Xmax-1) c3=0;
                             else c3=plan[i+1][j+1];
                             c4=plan[i][j+2];
                             plan[i][j]=(int) (c1+c2+c3+c4)/4;
                             g.setColor((Color) palette.elementAt(plan[i][j]));
                             g.drawLine(i,j+Ystart,i,j+Ystart);
                        }
                }
        }

        public void run()
        {
                Graphics g = getGraphics();
                while (runner != null)
                {
                        paint(off);
                        affiche(g);
			try {Thread.currentThread().sleep(10); }
			catch (InterruptedException e) {}
                }
        }
            
        public void stop()
        {
                runner = null;
        }
        
        public void destroy()
        {
        }
        
        public void paint(Graphics g)
        {
                traceFoyer();
                calculeColor(g);
        }

        public void affiche(Graphics g)
        {
                g.drawImage(offImg, 0, 0, this);
        }
}

import java.util.*;
import java.awt.*;
import java.awt.Toolkit.*;
import java.awt.image.*;
import java.applet.Applet;
import java.awt.event.*;
import java.math.*;
import java.net.URL.*;


class Node {			// Clase Nodo general.
    double x;
    double y;			// posicion (x,y) del applet

    double dx;
    double dy;
    boolean fixed;		// fijo, o no... ?

    Image imagen;		// Tipo de imagen a mostrar.

    String lbl;			// Etiqueta del nodo
}


class Edge {			// Clase Linea de union entre nodos
    int from;
    int to;

    double len;
    int col;			// Color por defecto de la conexion
}


class GraphPanel extends Panel
    implements Runnable, MouseListener, MouseMotionListener {
    IrcMapC2 graph;
    int nnodes;
    Node nodes[] = new Node[20];	// Numero maximo de nodos



    int nedges;
    Edge edges[] = new Edge[40];	// Numero maximo de conexiones

    Thread relaxer;
    boolean stress;
    boolean random;

    GraphPanel(IrcMapC2 graph) {
      this.graph = graph;
      addMouseListener(this);
    }

    int findNode(String lbl) {
        for (int i = 0 ; i < nnodes ; i++) {
            if (nodes[i].lbl.equals(lbl)) {
              return i;
            }
        }
        return addNode(lbl);
    }



    int addNode(String lbl) {
      Node n = new Node();
      n.x = 10 + 380*Math.random();
      n.y = 10 + 380*Math.random();
      n.lbl = lbl;
      nodes[nnodes] = n;
      return nnodes++;
    }


    void addEdge(String from, String to, int len, int col) {
      Edge e = new Edge();
      e.from = findNode(from);
      e.to = findNode(to);
      e.len = len;
      e.col = col;
      edges[nedges++] = e;
    }

    /***
      * Metodo para pintar un pixel en una posicion X,Y del Applet
     ***/
    public void pintarPixel(Graphics g, int x, int y, Color colorpx){
      g.setColor(colorpx);
      g.drawRect(x,y,1,3);
    }

    public void pintarLeyenda(Graphics g, Applet p){
    }

    public void pintarCampo(Graphics g){
      Random numeroaleatorio = new Random();
      for (int i=0; i <= 10; i++){
        int posx = numeroaleatorio.nextInt(200);
        int posy = numeroaleatorio.nextInt(200);
        int col = numeroaleatorio.nextInt(200);
        Color starcolor = new Color(col,col,col);

        pintarPixel(g,posx,posy,starcolor);
      }
    }

    public void run() {
      Thread me = Thread.currentThread();
      while (relaxer == me) {
        relax();
        if (random && (Math.random() < 0.03)) {
          Node n = nodes[(int)(Math.random() * nnodes)];
          if (!n.fixed) {
            n.x += 100*Math.random() - 50;
            n.y += 100*Math.random() - 50;
          }
        }
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          break;
        }
      }
    }

    synchronized void relax() {
      for (int i = 0 ; i < nedges ; i++) {
        Edge e = edges[i];
        double vx = nodes[e.to].x - nodes[e.from].x;
        double vy = nodes[e.to].y - nodes[e.from].y;
        double len = Math.sqrt(vx * vx + vy * vy);
        len = (len == 0) ? .0001 : len;
        double f = (edges[i].len - len) / (len * 3);
        double dx = f * vx;
        double dy = f * vy;
        nodes[e.to].dx += dx;
        nodes[e.to].dy += dy;
        nodes[e.from].dx += -dx;
        nodes[e.from].dy += -dy;
      }
      for (int i = 0 ; i < nnodes ; i++) {
        Node n1 = nodes[i];
        double dx = 0;
        double dy = 0;
        for (int j = 0 ; j < nnodes ; j++) {
          if (i == j) {
            continue;
          }
          Node n2 = nodes[j];
          double vx = n1.x - n2.x;
          double vy = n1.y - n2.y;
          double len = vx * vx + vy * vy;
          if (len == 0) {
            dx += Math.random();
            dy += Math.random();
          } else if (len < 100*100) {
            dx += vx / len;
            dy += vy / len;
          }
        }
        double dlen = dx * dx + dy * dy;
        if (dlen > 0) {
          dlen = Math.sqrt(dlen) / 2;
          n1.dx += dx / dlen;
          n1.dy += dy / dlen;
        }
      }

      Dimension d = getSize();
      for (int i = 0 ; i < nnodes ; i++) {
        Node n = nodes[i];
        if (!n.fixed) {
          n.x += Math.max(-5, Math.min(5, n.dx));
          n.y += Math.max(-5, Math.min(5, n.dy));
        }
        if (n.x < 0) {
          n.x = 0;
        } else if (n.x > d.width) {
          n.x = d.width;
        }
        if (n.y < 0) {
          n.y = 0;
        } else if (n.y > d.height) {
          n.y = d.height;
        }
        n.dx /= 2;
        n.dy /= 2;
      }
      repaint();
    }

    static Node pick;
    static boolean pickfixed;
    Image offscreen;
    Dimension offscreensize;
    Graphics offgraphics;

    final Color fixedColor = Color.red;
    final Color selectColor = Color.pink;
    final Color edgeColor = new Color(200,200,200);
    final Color nodeColor = new Color(250, 220, 100);
    final Color stressColor = Color.darkGray;
    final Color arcColor1 = Color.gray;
    final Color arcColor2 = Color.pink;
    final Color arcColor3 = Color.red;

    final Color lag0 = new Color(255,255,255); 	// Verde Clarito :) SIN LAG.
    final Color lag1 = new Color(0,255,0);		// Verde Normal :D LAG NORMAL.
    final Color lag2 = new Color(255,255,0);		// Amarillo
    final Color lag3 = new Color(255,190,0);		// Naranja
    final Color lag4 = new Color(255,0,0);		// Rojo
    final Color lag5 = new Color(190,0,255);		// Violeta oscuro.



    public void paintNode(Graphics g, Node n, FontMetrics fm) {
	int x = (int)n.x;
	int y = (int)n.y;
	
	g.setColor((n == pick) ? selectColor : (n.fixed ? fixedColor : nodeColor));
	
	int w = fm.stringWidth(n.lbl) + 10;		// Centramos la etiqueta
	int h = fm.getHeight() + 4;
	
	// AQUI VAMOS A IMPRIMIR EL GRAFICO DE CADA NODO.
	if (n.fixed){
		g.drawImage(n.imagen,x-(n.imagen.getHeight(this)/2),y-(n.imagen.getWidth(this)/2),this);
	}else{
		Image imagen = graph.getImage(graph.getCodeBase(),"leafs.gif");
		g.drawImage(imagen,x-(imagen.getHeight(this)/2),y-(imagen.getWidth(this)/2),this);
	}

	

	g.setColor(Color.white);		// Imprimimos la etiqueta del nodo
	g.drawString(n.lbl, x - (w-10)/2, (y - (h-40)/2) + fm.getAscent());
    }






    public synchronized void update(Graphics g) {
		Dimension d = getSize();
		if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
		    offscreen = createImage(d.width, d.height);
	    	offscreensize = d;
	    	if (offgraphics != null) {
		        offgraphics.dispose();
	    	}
	    	offgraphics = offscreen.getGraphics();
	    	offgraphics.setFont(getFont());
		}


		offgraphics.setColor(getBackground());
		offgraphics.fillRect(0, 0, d.width, d.height);
		for (int i = 0 ; i < nedges ; i++) {
	    	Edge e = edges[i];
	    	int x1 = (int)nodes[e.from].x;
	    	int y1 = (int)nodes[e.from].y;
	    	int x2 = (int)nodes[e.to].x;
	    	int y2 = (int)nodes[e.to].y;
	    	int col = e.col;

	    	int len = (int)Math.abs(Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2)) - e.len);
	    		    
//	    	offgraphics.setColor((len < 10) ? arcColor1 : (len < 20 ? arcColor2 : arcColor3)) ;

			// Pasamos a escribir la Leyenda...
			
			int myposx = 100;
			int myposy = d.height / 9;
			
			offgraphics.setColor(lag0);
				offgraphics.fillRect(myposx, d.height - myposy ,10,10);
				offgraphics.drawString("< 2 seg.",myposx + 15, d.height - (myposy-10));
			offgraphics.setColor(lag1);
				offgraphics.fillRect(myposx, d.height - (myposy - 15),10,10);
				offgraphics.drawString("< 4 seg.",myposx + 15, d.height - (myposy-25));
			offgraphics.setColor(lag2);
				offgraphics.fillRect(myposx, d.height - (myposy - 30),10,10);
				offgraphics.drawString("< 6 seg.",myposx + 15, d.height - (myposy-40));

			myposx = d.width - 150;
			
			offgraphics.setColor(lag3);
				offgraphics.fillRect(myposx, d.height - myposy ,10,10);
				offgraphics.drawString("< 8 seg.",myposx + 15, d.height - (myposy-10));
			offgraphics.setColor(lag4);
				offgraphics.fillRect(myposx, d.height - (myposy - 15),10,10);
				offgraphics.drawString("< 10 seg.",myposx + 15, d.height - (myposy-25));
			offgraphics.setColor(lag5);
				offgraphics.fillRect(myposx, d.height - (myposy - 30),10,10);
				offgraphics.drawString("> 10 seg.",myposx + 15, d.height - (myposy-40));
				


			
			if (col < 2){
				offgraphics.setColor(lag0);
			}else if (col < 4){
				offgraphics.setColor(lag1);
			}else if (col < 6){
				offgraphics.setColor(lag2);
			}else if (col < 8){
				offgraphics.setColor(lag3);
			}else if (col <= 10){
				offgraphics.setColor(lag4);
			}else {
				offgraphics.setColor(lag5);
			}

						
			offgraphics.drawLine(x1, y1, x2, y2);
	    	if (stress) {
				String lbl = String.valueOf(len);
				offgraphics.setColor(stressColor);
				offgraphics.drawString(lbl, x1 + (x2-x1)/2, y1 + (y2-y1)/2);
				offgraphics.setColor(edgeColor);
	    	}
		}

		FontMetrics fm = offgraphics.getFontMetrics();
		for (int i = 0 ; i < nnodes ; i++) {
		    paintNode(offgraphics, nodes[i], fm);
		}
		g.drawImage(offscreen, 0, 0, null);
	}

    //1.1 event handling
    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        addMouseMotionListener(this);
		double bestdist = Double.MAX_VALUE;
		int x = e.getX();
		int y = e.getY();
		for (int i = 0 ; i < nnodes ; i++) {
		    Node n = nodes[i];
		    double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
	    	if (dist < bestdist){
				pick = n;
				bestdist = dist;
		    }
		}
		pickfixed = pick.fixed;
		//pick.fixed = true;
		pick.x = x;
		pick.y = y;
		repaint();
		e.consume();
    }

    public void mouseReleased(MouseEvent e) {
        removeMouseMotionListener(this);
        if (pick != null) {
            pick.x = e.getX();
            pick.y = e.getY();
            pick.fixed = pickfixed;
            pick = null;
        }

		pick.x = e.getX();
		pick.y = e.getY();		// Inicialmente vacio

		repaint();
		e.consume();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
		pick.x = e.getX();
		pick.y = e.getY();
		repaint();
		e.consume();
    }
    
    public void mouseDown(MouseEvent e) {
   		pick.x = e.getX();
		pick.y = e.getY();		// Inicialmente vacio
		repaint();	
		e.consume();
    }
    
    public void mouseMoved(MouseEvent e) {
    }

    public void start() {
		relaxer = new Thread(this);
		relaxer.start();
    }

    public void stop() {
		relaxer = null;
    }

}


public class IrcMapC2 extends Applet implements ActionListener, ItemListener {

    GraphPanel panel;
    Panel controlPanel;

    //Button info = new Button("Autor");
    //Button shake = new Button("Shake");
    //Checkbox stress = new Checkbox("Stress");
    //Checkbox random = new Checkbox("Random"); 

    public void init() {
		setLayout(new BorderLayout());
	
		Dimension d = getSize();		// Capturamos el tamaño del applet.
		
		panel = new GraphPanel(this);
		panel.setBackground(Color.BLACK);
		add("Center", panel);
		controlPanel = new Panel();
		add("South", controlPanel);
		

		// Probamos a meter las imagenes en los nodos... :P
		for (int i = 0 ; i < panel.nnodes; i++) {
//			if (!panel.nodes[i].fixed){
	    		panel.nodes[i].imagen = getImage(getCodeBase(),"leafs.gif");	// La imagen! ;D
//	    	}
	    }

 		controlPanel.setBackground(Color.BLACK);

		
		
 	//controlPanel.add(info);
	//controlPanel.add(shake);
	//controlPanel.add(stress);
	//controlPanel.add(random);



 
 
		// RECOJEMOS LOS DATOS DE LAS CONEXIONES...
		String edges = getParameter("leafs");
		for (StringTokenizer t = new StringTokenizer(edges, ",") ; t.hasMoreTokens() ; ) {
	    	String str = t.nextToken();
	    	int i = str.indexOf('-');
	    	int col = 0;
	    	if (i > 0) {
				int len = 50;
				int j = str.indexOf('/');
				int h = str.indexOf('*');
				
				if (j > 0) {
				    len = Integer.valueOf(str.substring(j+1)).intValue();
					str = str.substring(0, j);
				}

				if (h > 0) {		// Sacamos el color
					col = Integer.valueOf(str.substring(h+1)).intValue();
					str = str.substring(0,h);
				}
				panel.addEdge(str.substring(0,i), str.substring(i+1), len, col);
	    	}
	    	
		}

		// RECOJEMOS LOS DATOS DE LOS HUBS...
		String center = getParameter("hubs");
		int i;
		int j;
		int posx;
		int posy;
		String cad;
		Random Raandom = new java.util.Random();
		int aleatorio,aleatorio2;
		
		for (StringTokenizer t = new StringTokenizer(center, ",") ; t.hasMoreTokens() ; ) {
	    	String str = t.nextToken();

	    	i = str.indexOf('-');
			j = str.indexOf('x');			// j es la posicion de la "x"
			
			posx = Integer.valueOf(str.substring(i+1,j)).intValue();	// Posicion X
			
				
			if (posx == 0){
				aleatorio = Raandom.nextInt(d.width);
				posx = aleatorio;
			}

			cad = str.substring(0,i);	// Metemos en "cad" desde 0 hasta "-"
			str = str.substring(j+1);				// str pasa a ser los valores "000x999"
			
			posy = Integer.valueOf(str).intValue();	// Posicion Y
			if (posy == 0){
				aleatorio2 = Raandom.nextInt(d.width);
				posy = aleatorio2;
			}

			
			Node n = panel.nodes[panel.findNode(cad)];
			n.x = posx;
			n.y = posy;
			n.imagen = getImage(getCodeBase(),"hubs.gif");	// La imagen! ;D
	    	n.fixed = true;		// Es un HUB!!! Que le vamos a hacer!!! :D


		}
	}


    public void destroy() {
        remove(panel);
        remove(controlPanel);
    }

    public void start() {
		panel.start();
    }

    public void stop() {
		panel.stop();
    }

    public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
    }

    public void itemStateChanged(ItemEvent e) {
		Object src = e.getSource();
		boolean on = e.getStateChange() == ItemEvent.SELECTED;
	}


    public String getAppletInfo() {
		return "Titulo: IrcMapC2 v.2.0. \nAutor: Elio Rojano <erojano@sinologic.net>";
    }

    public String[][] getParameterInfo() {
		String[][] info = {
		    {"leafs", "nodos", "Lista de conexiones de nodos separados SOLO por comas de la siguiente forma: nodo1-nodo2/distancia*ping  Ej: WEB-IRC/100*3,WEB-CVS/120*7,WEB-PARTY*2,PARTY-2000*4,...  "},
		    {"hubs", "servidores", "Lista de nodos centrales con su posicion en dentro del applet de la forma: <nodo>-<posx>x<posy>  Ej: WEB-100x150,PARTY-200x100,... "}
		};
		return info;
    }

} 
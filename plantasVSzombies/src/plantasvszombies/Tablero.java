/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Tablero.java
 *
 * Created on 23-jun-2011, 19:33:57
 */

package plantasvszombies;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;

/**
 *
 * @author user
 */
public class Tablero extends javax.swing.JFrame {
    // ***** Todas las plantas disponibles para pintarlas *****
    private ImageIcon girasol = new ImageIcon("imagenes/Plantas/Girasol.jpg");
    private ImageIcon guisantes1 = new ImageIcon("imagenes/Plantas/DisparaGuisantes.jpg");
    private ImageIcon repetidora = new ImageIcon("imagenes/Plantas/Repetidora.jpg");
    private ImageIcon guisantes2 = new ImageIcon("imagenes/Plantas/DisparaGuisantes2.jpg");
    private ImageIcon guisantes3 = new ImageIcon("imagenes/Plantas/DisparaGuisantesTriple.jpg");
    private ImageIcon hielaGuisantes = new ImageIcon("imagenes/Plantas/HielaGuisantes.jpg");
    private ImageIcon plantaCarnivora = new ImageIcon("imagenes/Plantas/PlantaCarnivora.jpg");
    private ImageIcon cactus = new ImageIcon("imagenes/Plantas/Cactus.jpg");
    private ImageIcon patatamina = new ImageIcon("imagenes/Plantas/Patatamina.jpg");
    private ImageIcon estrella = new ImageIcon("imagenes/Plantas/Estrella.jpg");
    private ImageIcon nuez = new ImageIcon("imagenes/Plantas/Nuez.jpg");
    private ImageIcon nuezGorda = new ImageIcon("imagenes/Plantas/NuezGorda.jpg");
    private ImageIcon coliflor = new ImageIcon("imagenes/Plantas/Coliflor.png");
    private ImageIcon mazorca = new ImageIcon("imagenes/Plantas/Mazorca.png");
    private ImageIcon melon = new ImageIcon("imagenes/Plantas/Melon.png");
    // ********************************************************
    private planta [][] jardin;       //Tablero para ir poniendo las plantas
    private boolean vieneUnZombie=false;
    private ArrayList plantas = new ArrayList();
    private ArrayList zombies = new ArrayList();
    private zombie zom;
    private double [] vPesos;
    private int [] vNoDominadas;
    private int posfila=0, poscol=0;             //Posiciones para el método Electre
    private int filaZombie,colZombie;                 //Fila y columna por la que viene el zombi
    private matriz Mdec, mNorm, mPond, mConcordancia, mDiscordancia, mDominanciaConc, mDominanciaDis, dominanciaAgregada;
    private elegirZombieQueViene ventana;

    /** Creates new form Tablero */
    public Tablero()
    {
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public Tablero(ArrayList plant, ArrayList zom) //Constructor con arraylist de plantas y zombis elegidos
    {
        jardin = new planta [5][9];
        plantas = plant;
        zombies = zom;
        vPesos= new double[3];                   //Vector de pesos, 0=ataque, 1=defensa 2=coste
        Mdec = new matriz (plantas.size() , 3);    
        mNorm = new matriz (Mdec.getFilas(),Mdec.getColumnas());
        mPond = new matriz (Mdec.getFilas(),Mdec.getColumnas());
        mConcordancia = new matriz (Mdec.getFilas(),Mdec.getFilas());
        mDiscordancia = new matriz (Mdec.getFilas(),Mdec.getFilas());
        mDominanciaConc = new matriz (Mdec.getFilas(),Mdec.getFilas());
        mDominanciaDis = new matriz (Mdec.getFilas(),Mdec.getFilas());
        dominanciaAgregada = new matriz (Mdec.getFilas(), Mdec.getFilas());
        vNoDominadas = new int [plantas.size()];
        if (buscaPlanta("Girasol", plantas)!= null)
            poscol = 2;

        crearMatrizDecision();
        initComponents();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public int getAtaque(int fila)
    {
        boolean salir = false;
        int ataque=0;
        for (int i=0;!salir && i<9;i++){
            if (jardin[fila][i]!= null)
               ataque= ataque + jardin[fila][i].getAtaque();
            else
               salir = true;
        }
        return ataque;
    }

    public void crearMatrizDecision()
    {
        int fila=0;
        ListIterator it = plantas.listIterator();
        while (it.hasNext())
        {
                planta aux = ((planta)it.next());
                Mdec.setElemento (fila, 0, aux.getAtaque());
                Mdec.setElemento (fila, 1, aux.getDefensa());
                Mdec.setElemento (fila, 2, aux.getCoste());
                fila++;
        }
    }
    

    public boolean vieneZombie()
    {
        return vieneUnZombie;
    }

    public planta buscaPlanta(String nombre, ArrayList plantas)
    {
        planta aux=new planta();
        boolean salir=false;
        ListIterator it = plantas.listIterator();
        while (!salir && it.hasNext())
        {
            aux = ((planta)it.next());
            if (aux.getNombre().equals(nombre))
                salir = true;
        }
        if (!salir)
           aux = null;
        return aux;
    }

    public boolean ponerGirasol()
    {
        boolean salir = false;
        for (int i=0;!salir && i<5;i++)
        {
            for (int j=0;j<2 && !salir;j++)
            {
                if (jardin[i][j]== null)
                {
                    jardin[i][j] = buscaPlanta("Girasol", plantas);
                    salir = true;
                }
            }
        }
        return salir;
    }

   public void calcularPesos()
   {
       if (poscol <2) //Si estamos en esta situacion es porque no se han elegido girasoles por lo tanto prima el coste por encima de lo demas
       {
           vPesos[0] = 0.2;
           vPesos[1] = 0.1;
           vPesos[2] = 0.7;
       }
       else
       {
          vPesos[0] = 1 - (poscol/10.0);        //Conforme mas a la derecha esté,menos importante será el ataque [0.8-0.0]
          vPesos[1] = (poscol / 10.0) - 0.2;    //Conforme mas a la derecha esté mas importante será la defensa [0.0-0.7]
          vPesos[2] = 0.2;
       }

   }

   public void calcularPesos(zombie zom)
   {
      if(zom.getDefensa() >= getAtaque(filaZombie))//Si viene un zombie resistente, interesa poner plantas de ataque pero no muy costosas (por si hubiese que poner otra)
      {
          vPesos[0] = 0.6;
          vPesos[1] = 0;
          vPesos[2] = 0.4;
      } 
      else if(getAtaque(filaZombie) <= 2 * zom.getDefensa())//El ataque de nuestras plantas no es mucho mayor que la defensa del zombi, por lo tanto interesa aumentar el ataque o la defensa pero esta vez sin tener en cuenta el coste.
            {
                 vPesos[0] = 0.5;
                 vPesos[1] = 0.4;
                 vPesos[2] = 0.1;
             } else
             {
                  vPesos[0] = 0.4;
                  vPesos[1] = 0.5;
                  vPesos[2] = 0.1;
             }
   }

   void metodoElectre()
   {
       planta ideal=null;
       boolean salir=false;
       calculaNormalizada();
       calculaPonderada ();
       calculaConjuntos ();
       calculaDominancia ();
       ideal = calcularPlanta();
       if (vieneUnZombie){
            jardin[filaZombie][colZombie] = ideal;
            vieneUnZombie = false;
       }
       else{
           while (!salir){
               if (jardin[posfila][poscol] == null){
                   jardin[posfila][poscol] = ideal;
                   salir = true;
               }
               posfila++;
               if (posfila==5){
                  if (poscol < 8){
                     posfila=0;
                     poscol++;
                  }
                  else
                      System.out.println("No puedes poner mas plantas");
              }
          }
      }
   }

   public void calculaNormalizada()
   {
        for (int i = 0; i < Mdec.getColumnas(); i++)
        {
            double sumatoria = 0.0;
            for (int j = 0; j < Mdec.getFilas(); j++)
            {
                double a = Mdec.getElemento(j, i);
                sumatoria = sumatoria + (a*a);
            }
            sumatoria = Math.sqrt(sumatoria);
            for (int j = 0; j < Mdec.getFilas(); j++) 
                mNorm.setElemento(j, i, Mdec.getElemento(j, i)/sumatoria);

        }
    }

   private void calculaPonderada()
   {
        for (int i = 0; i < mNorm.getFilas(); i++)
        {
            for (int j=0; j<mNorm.getColumnas(); j++)
                mPond.setElemento(i, j, mNorm.getElemento(i, j)* vPesos[j]);
       }
    }

    private void calculaConjuntos()
    {
        double sumaPesos, numerador, denominador;
        for (int i = 0; i < Mdec.getFilas(); i++)
        {
            for (int j = i; j < Mdec.getFilas(); j++)
            {
                if (i == j)  //Misma alternativa --> valor negativo
                    mConcordancia.setElemento(i, j, -5);
                else
                {
                    sumaPesos = 0;
                    for (int k = 0; k < Mdec.getColumnas(); k++)
                    {
                        if (Mdec.getElemento(i, k) >= Mdec.getElemento(j, k) && k!=2) //Cooncordancia
                            sumaPesos += vPesos[k];
                        else if (Mdec.getElemento(i, k) <= Mdec.getElemento (j,k) && k==2) //El coste cuanto mas pequeño sea mejor
                                   sumaPesos += vPesos[k];
                    }
                    //Cooncordancia
                    mConcordancia.setElemento(i, j, sumaPesos);
                    mConcordancia.setElemento(j, i, 10-sumaPesos); //Lo que falta para 1
                    //Discordancia
                }
            }
        }
        for(int i=0;i<Mdec.getFilas();i++)
        {
            for(int j=0;j<Mdec.getFilas();j++)
            {
                if (i == j)
                    mDiscordancia.setElemento(i, j, -5);
                else
                {
                    numerador = 0;
                    denominador = 0;
                    for (int k = 0; k < Mdec.getColumnas(); k++)
                    {
                        if (mPond.getElemento(i, k) < mPond.getElemento(j, k) && k!=2)
                        {         //Discordancia
                            if (numerador < Math.abs(mPond.getElemento(i, k) - mPond.getElemento(j, k)))
                                numerador = Math.abs(mPond.getElemento(i, k) - mPond.getElemento(j, k));
                        } else if (mPond.getElemento(i, k) > mPond.getElemento(j, k) && k==2)
                                {
                                  if (numerador < Math.abs(mPond.getElemento(i, k) - mPond.getElemento(j, k)))
                                      numerador = Math.abs(mPond.getElemento(i, k) - mPond.getElemento(j, k));
                                }
                        if(denominador<Math.abs(mPond.getElemento(i, k)- mPond.getElemento(j, k)))
                                denominador = Math.abs(mPond.getElemento(i, k)- mPond.getElemento(j, k));
                    }
                    mDiscordancia.setElemento(i, j, numerador/denominador);
                    }
               }
          }
    }


    private void calculaDominancia() {
        //Primero hayamos el valor umbral C y el umbral d que es la media de los indices de concordancia y discordancia
        double umbralC=0, umbrald=0;
        for (int i = 0; i < mConcordancia.getFilas(); i++) {
            for (int j = 0; j < mConcordancia.getColumnas(); j++) {
                if (mConcordancia.getElemento(i, j) != -5) {
                    umbralC += mConcordancia.getElemento(i, j);
                }

                if (mDiscordancia.getElemento(i, j) != -5) {
                    umbrald += mDiscordancia.getElemento(i, j);
                }
            }
        }
        umbralC = umbralC / ((mConcordancia.getFilas() * mConcordancia.getFilas()) - mConcordancia.getFilas());
        umbrald = umbrald / ((mDiscordancia.getFilas() * mDiscordancia.getFilas()) - mDiscordancia.getFilas());

        //Por ultimo se crean las matrices de dominancia
        for (int i = 0; i < mConcordancia.getFilas(); i++) {
            for (int j = 0; j < mConcordancia.getColumnas(); j++) {
                if (mConcordancia.getElemento(i, j) != -1) {
                    if (mConcordancia.getElemento(i, j) < umbralC) {
                        mDominanciaConc.setElemento(i, j, 0);
                    } else {
                        mDominanciaConc.setElemento(i, j, 1);
                    }
                }

                if (mDiscordancia.getElemento(i, j) != -1) {
                    if (mDiscordancia.getElemento(i, j) > umbrald) {
                        mDominanciaDis.setElemento(i, j, 0);
                    } else {
                        mDominanciaDis.setElemento(i, j, 1);
                    }
                }
                if (i == j) {
                    mDominanciaConc.setElemento(i, j, -5);
                    mDominanciaDis.setElemento(i, j, -5);
                }
            }
        }
    }

    private planta calcularPlanta ()
    {
        boolean salir =false;
        int contador=0;
        planta temporal=null;
        //Calculo la dominancia agregada, miro por filas las alternativas buenas y elijo una al azar
        for (int i=0;i<dominanciaAgregada.getFilas();i++){
            for (int j=0;j<dominanciaAgregada.getColumnas();j++){
                if(mDominanciaConc.getElemento(i, j)==1 && mDominanciaDis.getElemento(i, j)==1){
                    dominanciaAgregada.setElemento(i, j, 1);
                }
                else if (i==j){
                    dominanciaAgregada.setElemento(i, j, -5);
                }
                else
                    dominanciaAgregada.setElemento(i, j, 0);
            }
        }
        //Miro las alternativas no dominadas y las guardo en un vector
        for (int i=0;i<dominanciaAgregada.getFilas();i++){
            salir =false;
            for (int j=0;j<dominanciaAgregada.getColumnas() && !salir;j++){
                   if (dominanciaAgregada.getElemento(j, i)==1)
                       salir = true;
            }
            if (!salir){
                vNoDominadas[contador]=i;
                contador++;
            }
        }
        //Si hay mas de una elegimos una al azar
        System.out.println("Numero de plantas no dominadas: "+contador);
        if (contador>1){
            //numero aleatorio
            int numero = (int) (Math.random()*contador+1)-1;System.out.println("La planta escogida es la nº: "+numero);
            temporal = (planta) plantas.get(vNoDominadas[numero]);
        }
        else if (contador == 1){
            temporal = (planta) plantas.get(vNoDominadas[0]);
        }
           else
               System.out.println("No hay ninguna planta para poner");
        return temporal;
    }



    public void miElectre ()
    {
        planta unaPlanta;
        int i;
        boolean salir, plantaPuesta = false;;
        if (!vieneZombie())
        {
            //Busco si tengo girasoles
            unaPlanta = buscaPlanta ("Girasol", plantas);
            if (unaPlanta!=null)
            {
               if (!ponerGirasol())
               {  //miro las 2 primeras columnas y pongo un girasol
                  calcularPesos();
                  metodoElectre();   //Falta poner planta
               }
            }
            else
            {
                calcularPesos();
                metodoElectre();     //Falta poner Planta
            }
        }
        else
        {
            zom = ventana.getZombiQueViene();
            filaZombie = ventana.getFilaDelZombi();
            System.out.println("Viene el zombi malvado: "+zom.getNombre());
            //Si viene un zombi miro si puedo poner una planta en su fila
            salir = false;
            if (buscaPlanta("Girasol", plantas)!=null)
                i=2;
            else
                i=0;
            while (i<9 && !salir){
                if (jardin[filaZombie][i]==null){
                    salir=true;
                    colZombie = i;
                }
                i++;
            }
  
            if (salir)
            {   //Si salir=true tengo una casilla para poner una planta
                //Primero miro los tipos especiales (globo, minero)
                if (zom.getNombre().equals("ZombieGlobo"))
                {
                   //Busco si tengo cactus
                   unaPlanta = buscaPlanta("Cactus", plantas);
                   if (unaPlanta != null)
                   {
                      salir = false;
                      for (int j=0; !salir && j<i ;j++){
                         if(jardin[filaZombie][j]!= null)
                             if (jardin[filaZombie][j].getNombre().equals("Cactus")){
                                salir=true;
                         }
                      }
                      if (!salir ){ //No hay ningún cactus y hay hueco para una planta
                        jardin[filaZombie][colZombie] = unaPlanta;
                        vieneUnZombie = false;
                        plantaPuesta =true;
                       }
                   }
                }
                else if(zom.getNombre().equals("ZombieMinero"))
                {
                   //Busco si tengo bipetidora
                   unaPlanta = buscaPlanta("Bipetidora", plantas);
                   if (unaPlanta != null)
                   {
                      salir = false;
                      for (int j=0;!salir && j<i;j++){
                         if(jardin[filaZombie][j] != null)
                             if(jardin[filaZombie][j].getNombre().equals("Bipetidora"))
                                salir=true;
                      }
                      if (!salir ){ //No hay ninguna bipetidora y hay hueco para una planta
                        jardin[filaZombie][colZombie] = unaPlanta;
                        vieneUnZombie=false;
                        plantaPuesta = true;
                      }
                      else{

                      }

                   }
                }
                if (!plantaPuesta)
                {
                    calcularPesos(zom);
                    metodoElectre();   
                }
            }
            else{  //Pongo otra planta cualquiera
                vieneUnZombie = false;
                calcularPesos();
                metodoElectre();
            }

        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        botonPonerPlanta = new javax.swing.JButton();
        botonVieneZombie = new javax.swing.JButton();
        panelJardin = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        botonPonerPlanta.setText("Poner Planta");
        botonPonerPlanta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPonerPlantaActionPerformed(evt);
            }
        });

        botonVieneZombie.setText("Viene un Zombie!");
        botonVieneZombie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonVieneZombieActionPerformed(evt);
            }
        });

        panelJardin.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelJardin.setPreferredSize(new java.awt.Dimension(1179, 600));

        javax.swing.GroupLayout panelJardinLayout = new javax.swing.GroupLayout(panelJardin);
        panelJardin.setLayout(panelJardinLayout);
        panelJardinLayout.setHorizontalGroup(
            panelJardinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1177, Short.MAX_VALUE)
        );
        panelJardinLayout.setVerticalGroup(
            panelJardinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 598, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelJardin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(541, 541, 541)
                        .addComponent(botonPonerPlanta)
                        .addGap(18, 18, 18)
                        .addComponent(botonVieneZombie)))
                .addContainerGap(152, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelJardin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonPonerPlanta)
                    .addComponent(botonVieneZombie))
                .addContainerGap(499, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void paint(Graphics g)
    {
        super.paintComponents(g);
        int posicionx = 0, posiciony = 0;
        g = panelJardin.getGraphics();
        // las plantas las voy a poner de 131 * 100 para poder pintarlas todas sin problemas
        for(int i = 0; i < 9; i++)
        {
            for(int j = 0; j < 5; j++)
            {
                if( jardin[j][i] != null )
                {
                    if( jardin[j][i].getNombre().equals("Girasol") )
                    {
                        g.drawImage(girasol.getImage(), posicionx, posiciony, girasol.getIconWidth(), girasol.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("LanzaGuisantes") )
                    {
                        g.drawImage(guisantes1.getImage(), posicionx, posiciony, guisantes1.getIconWidth(), guisantes1.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("Repetidora") )
                    {
                        g.drawImage(repetidora.getImage(), posicionx, posiciony, repetidora.getIconWidth(), repetidora.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("Bipetidora") )
                    {
                        g.drawImage(guisantes2.getImage(), posicionx, posiciony, guisantes2.getIconWidth(), guisantes2.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("Tripitidora") )
                    {
                        g.drawImage(guisantes3.getImage(), posicionx, posiciony, guisantes3.getIconWidth(), guisantes3.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("HielaGuisantes") )
                    {
                        g.drawImage(hielaGuisantes.getImage(), posicionx, posiciony, hielaGuisantes.getIconWidth(), hielaGuisantes.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("Carronivora") )
                    {
                        g.drawImage(plantaCarnivora.getImage(), posicionx, posiciony, plantaCarnivora.getIconWidth(), plantaCarnivora.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("Cactus") )
                    {
                        g.drawImage(cactus.getImage(), posicionx, posiciony, cactus.getIconWidth(), cactus.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("PatataMina") )
                    {
                        g.drawImage(patatamina.getImage(), posicionx, posiciony, patatamina.getIconWidth(), patatamina.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("Frustella") )
                    {
                        g.drawImage(estrella.getImage(), posicionx, posiciony, estrella.getIconWidth(), estrella.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("Nuez") )
                    {
                        g.drawImage(nuez.getImage(), posicionx, posiciony, nuez.getIconWidth(), nuez.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("NuezGorda") )
                    {
                        g.drawImage(nuezGorda.getImage(), posicionx, posiciony, nuezGorda.getIconWidth(), nuezGorda.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("Coltapulta") )
                    {
                        g.drawImage(coliflor.getImage(), posicionx, posiciony, coliflor.getIconWidth(), coliflor.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("Lanzamaiz") )
                    {
                        g.drawImage(mazorca.getImage(), posicionx, posiciony, mazorca.getIconWidth(), mazorca.getIconHeight(), this);
                    }
                    else if( jardin[j][i].getNombre().equals("Melonpulta") )
                    {
                        g.drawImage(melon.getImage(), posicionx, posiciony, melon.getIconWidth(), melon.getIconHeight(), this);
                    }
                }
                posiciony += 115;
            }
            posiciony = 0;
            posicionx += 100;
        }
    }

    private void botonPonerPlantaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPonerPlantaActionPerformed
        miElectre();        // TODO add your handling code here:
        repaint();
    }//GEN-LAST:event_botonPonerPlantaActionPerformed

    private void botonVieneZombieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonVieneZombieActionPerformed
        try {
            ventana = new elegirZombieQueViene(zombies, zom, filaZombie);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Tablero.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Tablero.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Tablero.class.getName()).log(Level.SEVERE, null, ex);
        }
        vieneUnZombie = true;
        
        ventana.setTitle("Elige el zombie que viene");
        ventana.setSize(700,500);
        // ***** Centro la ventana *****
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = ventana.getSize();
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        ventana.setLocation( (screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        // *****************************
        ventana.setVisible(true);
    }//GEN-LAST:event_botonVieneZombieActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Tablero().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonPonerPlanta;
    private javax.swing.JButton botonVieneZombie;
    private javax.swing.JPanel panelJardin;
    // End of variables declaration//GEN-END:variables

}

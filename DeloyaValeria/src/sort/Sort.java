package sort;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

public class Sort{

  int[] numeros;

  public Sort(String archivo, int framerate, String metodo){
    EventQueue.invokeLater(new Runnable(){
      @Override
      public void run(){
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        JFrame frame = new JFrame("Ordenamientos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new Contenedor(archivo, framerate, metodo));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
      }catch(Exception e){
        System.out.println("\t:(");
      }
      }
    });
  }

  public class Contenedor extends JPanel{

    private JLabel etiqueta;

    public Contenedor(String archivo, int framerate, String metodo){
      setLayout(new BorderLayout());
      etiqueta = new JLabel(new ImageIcon(createImage(archivo)));
      add(etiqueta);
      JButton botonOrdenar = new JButton("Ordenar");
      add(botonOrdenar, BorderLayout.SOUTH);
      botonOrdenar.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e){
          BufferedImage imagen = (BufferedImage) ((ImageIcon) etiqueta.getIcon()).getImage();
          new UpdateWorker(imagen, etiqueta, archivo, framerate, metodo).execute();
        }
      });

    }

    public BufferedImage createImage(String archivo){
      BufferedImage imagen = null;
      try{
        imagen = ImageIO.read(new File("resource/"+archivo));
        ataqueHackerman(imagen);
        Graphics2D g = imagen.createGraphics();
        g.dispose();
      }catch(Exception e){
        System.err.println("(-)\tAsegurate de estar en el directorio 'src'");
        System.err.println("\ty de haber escrito bien el nombre de imagen (la cual debe estar en la carpeta resource)");
      }
      return imagen;
    }

    public void ataqueHackerman(BufferedImage imagen){
      int length = imagen.getHeight()*imagen.getWidth();
      numeros = new int[length];
      for(int i = 0; i < numeros.length; i++)
        numeros[i] = i;
      Random r = new Random();
      for(int i = 0; i < length; i++){
        int j = r.nextInt(length);
        swapImagen(imagen, i, j);
      }
    }

    public void swapImagen(BufferedImage imagen, int i, int j){
      int colI = i%imagen.getWidth();
      int renI = i/imagen.getWidth();
      int colJ = j%imagen.getWidth();
      int renJ = j/imagen.getWidth();
      int aux = imagen.getRGB(colI, renI);
      imagen.setRGB(colI, renI, imagen.getRGB(colJ, renJ));
      imagen.setRGB(colJ, renJ, aux);
      aux = numeros[i];
      numeros[i] = numeros[j];
      numeros[j] = aux;
    }

  }

  public class UpdateWorker extends SwingWorker<BufferedImage, BufferedImage>{

    private BufferedImage referencia;
    private BufferedImage copia;
    private JLabel target;
    int framerate;
    int n;
    String metodo;
    int iteracion;

    public UpdateWorker(BufferedImage master, JLabel target, String archivo, int speed, String algoritmo){
      this.target = target;
      try{
        referencia = ImageIO.read(new File("resource/"+archivo));
        copia = master;
        n = copia.getHeight()*copia.getWidth();
      }catch(Exception e){
        System.err.println(":c Esto no deberia ocurrir");
      }
      framerate = speed; // Indica cada cuantas iteraciones se actualizara la imagen
      metodo = algoritmo;
      iteracion = 0;
    }

    public BufferedImage updateImage(){
      Graphics2D g = copia.createGraphics();
      g.drawImage(copia, 0, 0, null);
      g.dispose();
      return copia;
    }

    @Override
    protected void process(List<BufferedImage> chunks){
      target.setIcon(new ImageIcon(chunks.get(chunks.size() - 1)));
    }

    public void update(){
      for(int i = 0; i < n; i++){
        int indiceDeOriginal = numeros[i];
        int colOriginal = indiceDeOriginal%copia.getWidth();
        int renOriginal = indiceDeOriginal/copia.getWidth();
        int colI = i%copia.getWidth();
        int renI = i/copia.getWidth();
        copia.setRGB(colI, renI, referencia.getRGB(colOriginal, renOriginal));
      }
      publish(updateImage());
    }

    @Override
    protected BufferedImage doInBackground() throws Exception{
      if(metodo.equals("bubble"))
        bubbleSort();
      if(metodo.equals("selection"))
        selectionSort();
      if(metodo.equals("insertion"))
        insertionSort();
      if(metodo.equals("merge"))
        mergeSort();
      if(metodo.equals("quick"))
        quickSort();
      update();
      return null;
    }

    private void bubbleSort(){
      for(int i = 0; i < n-1; i++){
        for(int j = 0; j < n-i-1; j++){
          if(numeros[j] > numeros[j+1])
          swap(j, j+1);
        }
        if(iteracion%framerate == 0) update(); // Actualizamos la interfaz grafica solo si han pasado el numero de iteraciones deseadas
        iteracion = (iteracion+1)%framerate; // Aumentamos el numero de iteraciones
      }
    }

    /** 
     * Ordenamiento con Selection Sort.
    */
    private void selectionSort(){
      int tamanoArr = numeros.length;
      int i = 0;
      int elemMinimo = 0;
      
      while(i < tamanoArr-1){
        elemMinimo = i;
        
        for(int j = i+1; j < tamanoArr; j++)
          if (numeros[j] < numeros[elemMinimo])
            elemMinimo = j;

        swap(elemMinimo,i);

        if(iteracion%framerate == 0) update();
          iteracion = (iteracion+1)%framerate;
        
        i++;
      }
    }

    /** 
     * Ordenamiento con Insertion Sort.
    */
    private void insertionSort(){
      int tamanoArr = numeros.length;
      int auxiliar = 0;
      int j = 0;

      for (int i = 1; i < tamanoArr; i++) {
        auxiliar = numeros[i];
        
        for (j = i-1; (j >= 0) && (numeros[j] > auxiliar); j--) {
          numeros[j+1] = numeros[j];
        }

        numeros[j+1] = auxiliar;
        if(iteracion%framerate == 0) update();
          iteracion = (iteracion+1)%framerate;  
      }    
    } 

    // ** MERGE SORT **

    /** 
     * Ordenamiento con Merge Sort.
    */
    private void mergeSort(){
      auxDivide(0,numeros.length-1);
    }

    /**
     * Metodo auxiliar para el ordenamiento con Merge Sort.
    */
    private void auxDivide(int izquierda, int derecha){
      if(izquierda < derecha){
        int mitad = (izquierda + derecha) / 2;
        auxDivide(izquierda,mitad);
        auxDivide(mitad+1,derecha);
        auxMezcla(izquierda,mitad,mitad+1,derecha);
      }
    }

    /**
     * Metodo auxiliar para el ordenamiento con Merge Sort.
    */
    private void auxMezcla(int izq1, int izq2, int der1, int der2){
      int a = izq1;
      int b = der1;
      int c = 0;
      int [] aux = new int[der2-izq1+1];
      
      while((a <= izq2) && (b <= der2)){
        
        if(numeros[a] <= numeros[b]){
          aux[c] = numeros[a];
          a++;
          c++;
          
          if(iteracion%framerate == 0) update();
            iteracion = (iteracion+1)%framerate;
        
        } else {
          aux[c] = numeros[b];
          b++;
          c++;

          if(iteracion%framerate == 0) update();
            iteracion = (iteracion+1)%framerate;
        } 
      }

      for(  ;a <= izq2; a++,c++){
        aux[c] = numeros[a];

        if(iteracion%framerate == 0) update();
          iteracion = (iteracion+1)%framerate;
      }

      for(  ;b <= der2; b++,c++){
        aux[c] = numeros[b];

        if(iteracion%framerate == 0) update();
          iteracion = (iteracion+1)%framerate;
      }

      for(a = 0; a < aux.length; a++)
        numeros[a+izq1] = aux[a];

      if(iteracion%framerate == 0) update();
        iteracion = (iteracion+1)%framerate;
    }

    // ** QUICK SORT **

    /**
     * Metodo auxiliar para el ordenamiento con Quick Sort.
    */
    public int particion(int inicio,int fin){
      int indice = inicio-1;

      while(inicio <= fin){
        if(numeros[inicio] < numeros[fin]){
          indice++;
          swap(indice,inicio);
        }
        inicio++;
      }

      swap(indice+1,fin);
      return indice+1;
    }

    /**
     * Metodo auxiliar para el ordenamiento con Quicksort.
    */
    public void quickAuxiliar(int inicio, int fin) {
      if(inicio < fin){
        int aux= particion(inicio,fin);
        
        quickAuxiliar(inicio,aux-1);
        quickAuxiliar(aux+1,fin);
        
        if(iteracion%framerate == 0) update();
          iteracion = (iteracion+1)%framerate; 
      }
    }

    /** 
     * Ordenamiento con Quick Sort.
    */
    private void quickSort(){
      //Mandamos a llamar metodo auxiliar
      quickAuxiliar(0,numeros.length-1); 
    }

    public void swap(int i, int j){
      int aux = numeros[i];
      numeros[i] = numeros[j];
      numeros[j] = aux;
    }
  }
}

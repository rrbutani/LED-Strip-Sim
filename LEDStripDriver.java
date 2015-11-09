import javax.swing.JFrame;
import java.util.Arrays;

public class LEDStripDriver
{
    private static LEDStripSim display;
    private static JFrame frame;
    
    private static int numStrips = 2;
    private static int numPixels   = 120;
    
    private static double [][][] strips;
    
    public static void main(String [] args)
    {
        setupDisplay();
        startupSequence();
        rainbow();
        
        //pulse();
    }
    
    public static void setupDisplay()
    {
        System.out.println("Setting up local vars...");
        
        strips = new double[numStrips][numPixels][4];
        
        System.out.println("Starting display...");
        
        display = new LEDStripSim(numStrips, numPixels, 4, true);
        display.init();
        
        frame=new JFrame("LED Display");
        frame.add(display);
        
        frame.setSize(display.wX, display.wY + 20); //20 bc JFrames are bad
        frame.setVisible(true);
        
        for(int strip = 0; strip < strips.length; strip++)
        {
            for(int pixel = 0; pixel < strips[strip].length; pixel++)
            {
                for(int part = 0; part < strips[strip][pixel].length; part++)
                {
                    strips[strip][pixel][part] = 0.0;
                }
            }
        }
        showStrips();
    }
    
    public static void startupSequence()
    {
        for(int pixelNum = 0; pixelNum < strips[0].length; pixelNum++)
        {
            for(int strip = 0; strip < strips.length; strip++)
            {
                strips[strip][pixelNum][0] = 148.0; 
                strips[strip][pixelNum][1] = 19.0;
                strips[strip][pixelNum][2] = 191.0;
                strips[strip][pixelNum][3] = (int)((double)((double)pixelNum/(double)strips[0].length)*230) + 25;
                System.out.println("On pixel #" + pixelNum + ": " + strips[strip][pixelNum][3]);
            }
            try
            {
                showStrips();
                Thread.sleep(6); //50 updates a second
            } catch (InterruptedException e) {
                // recommended because catching InterruptedException clears interrupt flag
                Thread.currentThread().interrupt();
                // you probably want to quit if the thread is interrupted
                return;
            }
        }
    }
    
    public static void pulse()
    {
        for(int t = 0; t < 1080; t++)
        {
            for(int pixelNum = 0; pixelNum < strips[0].length; pixelNum++)
            {
                for(int strip = 0; strip < strips.length; strip++)
                {
                    strips[strip][pixelNum][3] = (int)Math.abs(Math.sin(Math.toRadians(t)) * (double)255);
                }
            }
            try
            {
                showStrips();
                Thread.sleep(8);
            } catch (InterruptedException e) {
                // recommended because catching InterruptedException clears interrupt flag
                Thread.currentThread().interrupt();
                // you probably want to quit if the thread is interrupted
                return;
            }
        }
    }
    
    public static void rainbow()
    {
        for(int j = 0; j < 256 * 5; j++)
        {
            for(int strip = 0; strip < strips.length; strip++)
            {
                for(int pixel = 0; pixel < strips[strip].length; pixel++)
                {
                    strips[strip][pixel] = colorWheel( ( (pixel * 256 / strips[strip].length) + j) & 255);
                }
            }
            
            try
            {
                showStrips();
                Thread.sleep(8);
            } catch (InterruptedException e) {
                // recommended because catching InterruptedException clears interrupt flag
                Thread.currentThread().interrupt();
                // you probably want to quit if the thread is interrupted
                return;
            }
        }
    }
    
    public static double [] colorWheel(int pos)
    {
        if(pos < 85)
            return new double[]{pos * 3.0, 255 - pos * 3.0, 0.0, 255.0};
        else if(pos < 170)
        {
            pos -= 85;
            return new double[]{255 - pos * 3.0, 0.0, pos * 3.0, 255.0};
        }
        else
        {
            pos -= 170;
            return new double[]{0.0, pos * 3.0, 255 - pos * 3.0, 255.0};
        }
    }
    //Display Operation Methods
    public static void showStrips()
    {
        //display.updateDisplay(strips);
//         for(int pixel = 0; pixel < 3/*diffed[0].length*/; pixel++)
//         {
//             System.out.println("Original @0," + pixel + ": " + strips[0][pixel][0] + "," + strips[0][pixel][1] + "," + strips[0][pixel][2] + "," + strips[0][pixel][3]);
// //             System.out.println("DIFFED v @0," + pixel + ": " + diffed[0][pixel][0] + "," + diffed[0][pixel][1] + "," + diffed[0][pixel][2] + "," + diffed[0][pixel][3] + "!!");
//         }
        
        
//         double [][][] diffed = display.updateDisplay2(strips, true);

display.updateDisplay(strips, true);
        
//         for(int pixel = 0; pixel < 3/*diffed[0].length*/; pixel++)
//         {
//             System.out.println("Original @0," + pixel + ": " + strips[0][pixel][0] + "," + strips[0][pixel][1] + "," + strips[0][pixel][2] + "," + strips[0][pixel][3]);
//             System.out.println("DIFFED v @0," + pixel + ": " + diffed[0][pixel][0] + "," + diffed[0][pixel][1] + "," + diffed[0][pixel][2] + "," + diffed[0][pixel][3] + "!!");
//         }
    }
}
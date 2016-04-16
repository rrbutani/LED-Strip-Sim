import javax.swing.JFrame;
import java.util.Arrays;
import java.util.Random;

public class LEDStripDriver
{
    private static LEDStripSim display;
    private static JFrame frame;
    
    private static int numStrips = 10;
    private static int numPixels   = 60;
    
    private static double [][][] strips;
    
    public static void main(String [] args)
    {
        setupDisplay();
        startupSequence();

        sparkle(122.0, 10.0, 12.0, 50.0, 255.0, 13.0, 5, false);

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

    public static void sparkle(double r, double g, double b, double minA, double maxA, double maxStep, int deltaLongevity, boolean rainbow)
    {
        minA = Math.max(minA, 0.0);
        maxA = Math.min(maxA, 255.0);

        Random rand = new Random();
        double [][] deltas = new double[strips.length][strips[0].length];

        //Set initial values:
        for(int strip = 0; strip < strips.length; strip++)
        {
            for(int pixelNum = 0; pixelNum < strips[strip].length; pixelNum++)
            {
                strips[strip][pixelNum][0] = r;
                strips[strip][pixelNum][1] = g;
                strips[strip][pixelNum][2] = b;
                strips[strip][pixelNum][3] = rand.nextDouble() * (maxA * 0.8);

                deltas[strip][pixelNum] = ((rand.nextDouble() * 2) - 1.0) * maxStep;
            }
        }

        //If rainbow:
        int j = 0;

        while(true)
        {
            for(int strip = 0; strip < strips.length; strip++)
            {
                for(int pixelNum = 0; pixelNum < strips[strip].length; pixelNum++)
                {
                    if(rand.nextInt(deltaLongevity) == 0)
                        deltas[strip][pixelNum] = ((rand.nextDouble() * 2) - 1.0) * maxStep;

                    System.out.println(deltas[strip][pixelNum]);

                    strips[strip][pixelNum][3] += deltas[strip][pixelNum];
                    strips[strip][pixelNum][3] = Math.min(Math.max(minA, strips[strip][pixelNum][3]), maxA);
                    System.out.println("LED " + strip + ":" + pixelNum + ".A set to " + strips[strip][pixelNum][3]);

                    if(rainbow)
                    {
                       strips[strip][pixelNum] = colorWheel( (((pixelNum * 256 / strips[strip].length) + j) & 255), strips[strip][pixelNum][3] );
                    }
                    else
                    {
                        strips[strip][pixelNum][0] = r;
                        strips[strip][pixelNum][1] = g;
                        strips[strip][pixelNum][2] = b;
                    }
                }
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

            j++;
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

    public static void brightnessSet(double brightness, double scale, double min)
    {
        for(int strip = 0; strip < strips.length; strip++)
        {
            for(int pixel = 0; pixel < strips[strip].length; pixel++)
            {
                strips[strip][pixel][3] = (brightness/scale)*(255-min) + min;
            }
        }
    }

    public static void brightnessSet_StripRange(double brightness, double scale, double min, int start, int stop)
    {
        for(int strip = start; strip < stop; strip++)
        {
            for(int pixel = 0; pixel < strips[strip].length; pixel++)
            {
                strips[strip][pixel][3] = (brightness/scale)*(255-min) + min;
            }
        }
    }

    public static void colorSet_StripRange(double brightness, double scale, double min, int start, int stop)
    {
        for(int strip = start; strip < stop; strip++)
        {
            for(int pixel = 0; pixel < strips[strip].length; pixel++)
            {
                strips[strip][pixel] = colorWheel((int)((brightness/scale)*(255-min) + min));
            }
        }
    }

    public static void numberSet_StripRange(double brightness, double scale, double min, int start, int stop)
    {
        for(int strip = start; strip < stop; strip++)
        {
            for(int pixel = 0; pixel < ((int)((brightness/scale)*(strips[strip].length-min) + min)); pixel++)
            {
                strips[strip][pixel][3] = 255.0;
            }
            for(int pixel2 = ((int)((brightness/scale)*(strips[strip].length-min) + min)); pixel2 < strips[strip].length; pixel2++)
            {
                strips[strip][pixel2][3] = 0;
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

    public static double [] colorWheel(int pos, double brightness)
    {
        if(pos < 85)
            return new double[]{pos * 3.0, 255 - pos * 3.0, 0.0, brightness};
        else if(pos < 170)
        {
            pos -= 85;
            return new double[]{255 - pos * 3.0, 0.0, pos * 3.0, brightness};
        }
        else
        {
            pos -= 170;
            return new double[]{0.0, pos * 3.0, 255 - pos * 3.0, brightness};
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

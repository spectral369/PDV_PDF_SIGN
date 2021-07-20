/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spectral369.pdfsign;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author spectral369
 */
public class PDFTextLocator extends PDFTextStripper {

    private String key_string;
    private float[] x;
    private float[] y;
    int i = 0;

    List<TextPosition> initNr = null;
    List<TextPosition> endNr = null;
    List<TextPosition> dateStart1 = null;
    List<TextPosition> dateEnd1 = null;
    List<TextPosition> yearStart = null;
    List<TextPosition> yearEnd = null;

    List<TextPosition> topPrenumeStart = null;
    List<TextPosition> topNumeStart = null;
    List<TextPosition> topFunctiaStart = null;

    public PDFTextLocator() throws IOException {
        x = new float[2];
        y = new float[2];
        initNr = new LinkedList<>();
        endNr = new LinkedList<>();
        dateStart1 = new LinkedList<>();
        dateEnd1 = new LinkedList<>();
        yearStart = new LinkedList<>();
        yearEnd = new LinkedList<>();

        topPrenumeStart = new LinkedList<>();
        topNumeStart = new LinkedList<>();
        topFunctiaStart =  new LinkedList<>();

    }

    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
        super.writeString(string, textPositions);
        //  System.out.println(string);
        if (string.contains("Nr.")) {
            // System.out.println("FOUND");
            int size = textPositions.size();
            // System.out.println("size: " + size + "\n");
            //initNr.add(textPositions.get(5));

            for (TextPosition pos : textPositions) {
                    if (pos.toString().matches("^\\w+( +\\w+)*$")) {
                   StringBuilder sb =  new StringBuilder(pos.toString());
                    sb.append(textPositions.get(textPositions.indexOf(pos)+1));
                  //  System.out.println(sb.toString());
                    if (sb.toString().matches("r.*")) {
                       initNr.add(textPositions.get(textPositions.indexOf(pos)+3)); 
                    }
                    }
                //   System.out.print(pos + " ");
                if (pos.toString().contains("/")) {
                    endNr.add(textPositions.get(textPositions.indexOf(pos)));
                    dateStart1.add(textPositions.get(textPositions.indexOf(pos) + 1));
                }
                if (pos.toString().contains("0")) {
                    dateEnd1.add(textPositions.get(textPositions.indexOf(pos) - 2));
                    yearStart.add(textPositions.get(textPositions.indexOf(pos) + 1));
                    yearStart.add(textPositions.get(textPositions.indexOf(pos)));//+until the end  
                }

                //detect nume+prenume
            }

            // if(x == -1) {
            x[i] = initNr.get(0).getXDirAdj();
            y[i] = initNr.get(0).getYDirAdj() + 6;
            i++;
            //}       
        }
        if (string.contains("Prenumele")) {
            //     System.out.println("FOUND");//x2
           // System.out.println(string);
            for (TextPosition pos : textPositions) {
            
                if (pos.toString().matches("^\\w+( +\\w+)*$")) {
                    StringBuilder sb =  new StringBuilder(pos.toString());
                    sb.append(textPositions.get(textPositions.indexOf(pos)+1));
                    //System.out.println(sb.toString());
                    if (sb.toString().matches("le*")) {
                        topPrenumeStart.add(textPositions.get(textPositions.indexOf(pos)));
                    }

                }

            }
        }
        //numele
          if (string.contains("Numele")) {
            //     System.out.println("FOUND");//x2
           // System.out.println(string);
            for (TextPosition pos : textPositions) {
            
                if (pos.toString().matches("^\\w+( +\\w+)*$")) {
                    StringBuilder sb =  new StringBuilder(pos.toString());
                    sb.append(textPositions.get(textPositions.indexOf(pos)+1));
                  //  System.out.println(sb.toString());
                    if (sb.toString().matches("le*")) {
                        topNumeStart.add(textPositions.get(textPositions.indexOf(pos)));
                    }

                }

            }
        }
          //functia
        if (string.contains("Func")) {
            //     System.out.println("FOUND");//x2
           // System.out.println(string);
            for (TextPosition pos : textPositions) {
                if (pos.toString().matches("^\\w+( +\\w+)*$")) {
                    StringBuilder sb =  new StringBuilder(pos.toString());
                    sb.append(textPositions.get(textPositions.indexOf(pos)+1));
                  //  System.out.println(sb.toString());
                    if (sb.toString().matches("ia*")) {
                      topFunctiaStart.add(textPositions.get(textPositions.indexOf(pos)));
                    }

                }

            }
        }    
          
          
          
    }

    public List<TextPosition> getTopFunctiaStart() {
        return topFunctiaStart;
    }

    public List<TextPosition> getTopPrenumeStart() {
        return topPrenumeStart;
    }

    public List<TextPosition> getTopNumeStart() {
        return topNumeStart;
    }

    public List<TextPosition> getInitNr() {
        return initNr;
    }

    public List<TextPosition> getEndNr() {
        return endNr;
    }

    public List<TextPosition> getDateStart1() {
        return dateStart1;
    }

    public List<TextPosition> getDateEnd1() {
        return dateEnd1;
    }

    public List<TextPosition> getYearStart() {
        return yearStart;
    }

    public List<TextPosition> getYearEnd() {
        return yearEnd;
    }

    public float[] getXCoordonatesTest() {
        return x;
    }

    public float[] getYCoordonatesTest() {
        return y;
    }

}

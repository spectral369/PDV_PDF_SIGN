package com.spectral369.pdfsign;

import com.dansoftware.pdfdisplayer.PDFDisplayer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.fontbox.util.autodetect.UnixFontDirFinder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PrimaryController {

    float x, y, x2, y2;
    Node test = null;
    GraphicsContext gc = null;
    double scale2 = 0;
    Rectangle selection = new Rectangle();
    private boolean isDrawn = false;
    PDFTextLocator stripper = null;
    PDDocument document = null;
    ScrollBar scrollBar = null;
    double scrolled = 0;
    boolean isloaded = false;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button load;
    @FXML
    private Canvas display;
    @FXML
    AnchorPane root;
    @FXML
    ButtonBar bbar;
    @FXML
    Button botdec;
    @FXML
    Button topdec;
    

    @FXML
    private void onLoad() throws IOException {
        File file = new File("/home/spectral369/cvc.pdf");

        try {
            document = PDDocument.load(file);
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            display.setWidth(document.getPage(0).getMediaBox().getWidth());
            display.setHeight(document.getPage(0).getMediaBox().getHeight());

            PDDocumentInformation pdd = document.getDocumentInformation();
            pdd.setAuthor("Spectral369");
            pdd.setCreationDate(Calendar.getInstance());

            BufferedImage bi = pdfRenderer.renderImage(0);
            Image image = SwingFXUtils.toFXImage(bi, null);
            gc = display.getGraphicsContext2D();
            // var scale = Math.min(display.getWidth() / document.getPage(0).getMediaBox().getWidth(), display.getHeight() / document.getPage(0).getMediaBox().getHeight());
            scale2 = Math.min(display.getWidth() / document.getPage(0).getMediaBox().getWidth(), display.getHeight() / document.getPage(0).getMediaBox().getHeight());
            var x = (display.getWidth() / 2) - (document.getPage(0).getMediaBox().getWidth() / 2) * scale2;
            var y = ((display.getHeight() + 20) / 2) - (document.getPage(0).getMediaBox().getHeight() / 2) * scale2;

            System.out.println("width1: " + x + " height1: " + y);
            gc.drawImage(scale(image, display.getWidth(), display.getHeight(), true), 0, 10);
            // gc.drawImage(image, x, y, document.getPage(0).getMediaBox().getWidth() * scale2, document.getPage(0).getMediaBox().getHeight() * scale2);

            ProgressBar progressBar = new ProgressBar();
            progressBar.setPrefWidth(500);
            progressBar.setProgress(0);

            PDFDisplayer pdfDisplayer = new PDFDisplayer();
            pdfDisplayer.loadPDF(file);

            //set the process listener of it 
            Platform.runLater(() -> {
                progressBar.setProgress(Timeline.INDEFINITE);
            });

            // pdfDisplayer.loadPDF(init());
            Platform.runLater(() -> {
                progressBar.setProgress(0);
            });
            scrollPane.requestFocus();

            //scroll
            scrollPane.setFitToWidth(true);

            scrollPane.getContent().setOnScroll(scrollEvent -> {
                double deltaY = scrollEvent.getDeltaY() * 0.01;
                scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
                scrolled = scrollEvent.getDeltaY();//   scrollPane.getVvalue();

            });

            //
            stripper = new PDFTextLocator();
            stripper.setSortByPosition(true);
            stripper.getText(document);
            
            isloaded = true;
            botdec.setDisable(!isloaded);
            topdec.setDisable(!isloaded);
        } catch (IOException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
            isloaded = false;
        }
    }

    @FXML
    public void onBottomLocDec() {
             GraphicsContext gr = display.getGraphicsContext2D();
            gr.beginPath();
            gc.setStroke(Color.GREEN);
            gr.setLineWidth(5);
                gc.setStroke(Color.DARKBLUE);

            gc.strokeLine(stripper.getInitNr().get(stripper.getInitNr().size() - 1).getXDirAdj(), stripper.getInitNr().get(stripper.getInitNr().size() - 1).getYDirAdj() + 6,
                    stripper.getEndNr().get(stripper.getEndNr().size() - 1).getXDirAdj(),
                    stripper.getInitNr().get(stripper.getInitNr().size() - 1).getYDirAdj() + 6);

            //date
            gc.setStroke(Color.BLUE);
            gc.strokeLine(stripper.getDateStart1().get(stripper.getDateStart1().size() - 1).getXDirAdj() + 3, stripper.getDateStart1().get(stripper.getDateStart1().size() - 1).getYDirAdj() + 6,
                    stripper.getDateEnd1().get(stripper.getDateEnd1().size() - 1).getXDirAdj(),
                    stripper.getDateEnd1().get(stripper.getDateEnd1().size() - 1).getYDirAdj() + 6);

            ///year
            gc.setStroke(Color.YELLOW);
            gc.strokeLine(stripper.yearStart.get(stripper.getYearStart().size() - 1).getXDirAdj() + 10, stripper.yearStart.get(stripper.getYearStart().size() - 1).getYDirAdj() + 6,
                    stripper.getYearStart().get(stripper.getYearStart().size() - 1).getXDirAdj() + 22,
                    stripper.getYearStart().get(stripper.getYearStart().size() - 1).getYDirAdj() + 6);

            //prenume
            gc.setStroke(Color.GREEN);
            gc.strokeLine(stripper.getTopPrenumeStart().get(stripper.getTopPrenumeStart().size() - 1).getXDirAdj() + 15, stripper.getTopPrenumeStart().get(stripper.getTopNumeStart().size() - 1).getYDirAdj() + 6,
                    stripper.getTopPrenumeStart().get(stripper.getTopPrenumeStart().size() - 1).getXDirAdj() + 92,
                    stripper.getTopPrenumeStart().get(stripper.getTopPrenumeStart().size() - 1).getYDirAdj() + 6);

            //nume
            gc.setStroke(Color.BLUEVIOLET);
            gc.strokeLine(stripper.getTopNumeStart().get(stripper.getTopNumeStart().size() - 1).getXDirAdj() + 15, stripper.getTopNumeStart().get(stripper.getTopNumeStart().size() - 1).getYDirAdj() + 6,
                    stripper.getTopNumeStart().get(stripper.getTopNumeStart().size() - 1).getXDirAdj() + 105,
                    stripper.getTopNumeStart().get(stripper.getTopNumeStart().size() - 1).getYDirAdj() + 6);

            //functia
            gc.setStroke(Color.DARKBLUE);
            gc.strokeLine(stripper.getTopFunctiaStart().get(stripper.getTopFunctiaStart().size() - 1).getXDirAdj() + 15, stripper.getTopFunctiaStart().get(stripper.getTopFunctiaStart().size() - 1).getYDirAdj() + 6,
                    stripper.getTopFunctiaStart().get(stripper.getTopFunctiaStart().size() - 1).getXDirAdj() + 105,
                    stripper.getTopFunctiaStart().get(stripper.getTopFunctiaStart().size() - 1).getYDirAdj() + 6);

            //bot signature
            gc.rect(502, 731, 75, 60);
            gc.stroke();

            //end bot signature
            gr.save();
            gr.closePath();
    }

    @FXML
    public void onTopLocDec() {

       // try {

            //  stripper = new PDFTextLocator();
            // stripper.setSortByPosition(true);
            //   stripper.getText(document);
            GraphicsContext gr = display.getGraphicsContext2D();
            gr.beginPath();
            gc.setStroke(Color.GREEN);
            gr.setLineWidth(5);
            //nr
            gc.strokeLine(stripper.getInitNr().get(0).getXDirAdj(), stripper.getInitNr().get(0).getYDirAdj() + 6,
                    stripper.getEndNr().get(0).getXDirAdj(),
                    stripper.getInitNr().get(0).getYDirAdj() + 6);
            //date
            gc.setStroke(Color.BLUE);
            gc.strokeLine(stripper.getDateStart1().get(0).getXDirAdj() + 3, stripper.getDateStart1().get(0).getYDirAdj() + 6,
                    stripper.getDateEnd1().get(0).getXDirAdj(),
                    stripper.getDateEnd1().get(0).getYDirAdj() + 6);
            ///year
            gc.setStroke(Color.YELLOW);
            gc.strokeLine(stripper.yearStart.get(0).getXDirAdj() + 3, stripper.yearStart.get(0).getYDirAdj() + 6,
                    stripper.getYearStart().get(0).getXDirAdj() + 15,
                    stripper.getYearStart().get(0).getYDirAdj() + 6);

            gc.setStroke(Color.GREEN);

            gc.strokeLine(stripper.getTopPrenumeStart().get(0).getXDirAdj() + 15, stripper.getTopPrenumeStart().get(0).getYDirAdj() + 6,
                    stripper.getTopPrenumeStart().get(0).getXDirAdj() + 85,
                    stripper.getTopPrenumeStart().get(0).getYDirAdj() + 6);

            gc.setStroke(Color.BLUEVIOLET);

            gc.strokeLine(stripper.getTopNumeStart().get(0).getXDirAdj() + 15, stripper.getTopNumeStart().get(0).getYDirAdj() + 6,
                    stripper.getTopNumeStart().get(0).getXDirAdj() + 95,
                    stripper.getTopNumeStart().get(0).getYDirAdj() + 6);

            gc.setStroke(Color.DARKBLUE);
            gc.strokeLine(stripper.getTopFunctiaStart().get(0).getXDirAdj() + 15, stripper.getTopFunctiaStart().get(0).getYDirAdj() + 6,
                    stripper.getTopFunctiaStart().get(0).getXDirAdj() + 95,
                    stripper.getTopFunctiaStart().get(0).getYDirAdj() + 6);
//end top
//start mid signature
            gc.rect(220, 110, 195, 60);

            gc.stroke();

//end mid signature

            gr.save();
            gr.closePath();

      /*  } catch (IOException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }


    @FXML
    public void onSettings(){
        
    }

    public float pt2mm(float pt, float dpi) {
        return pt * 25.4f / dpi;
    }

    public void setStartPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setEndPoint(int x, int y) {
        x2 = (x);
        y2 = (y);
    }

    public void paintComponent(GraphicsContext g) {

        drawPerfectRect(g, x, y, x2, y2);
    }

    public void drawPerfectRect(GraphicsContext g, double x, double y, double x2, double y2) {
        double px = Math.min(x, x2);
        double py = Math.min(y, y2);
        double pw = Math.abs(x - x2);
        double ph = Math.abs(y - y2);

        g.beginPath();
        g.setFill(Color.BLUE);
        g.rect(px, py, pw, ph);
        g.setLineWidth(2);
        g.setStroke(Color.BLACK);
        g.stroke();
        g.save();
        g.closePath();
        //  display.getGraphicsContext2D().fillRect(px, py, pw, ph);
    }

    @FXML
    public void onMouseDragged(MouseEvent e) {

        double px = Math.min(x, e.getX());
        double py = Math.min(y, e.getY());
        //  double pw = Math.abs(x - e.getX());
        //   double ph = Math.abs(y - e.getY());

        if (isDrawn) {

            if (scrolled < 0) {
                py += (scrolled) + 10;

            } else {
                py += bbar.getHeight();

            }

            adjust(e.getSceneX(),//px
                    e.getSceneY(),//py
                    px,
                    py,
                    selection);
        }

    }

    @FXML
    public void onMousePressed(MouseEvent e) {
        setStartPoint((float) e.getX(), (float) e.getY());
        // System.out.println("start1: " + x);
        // System.out.println("start1: " + y);

        if (!isDrawn) {

            selection.setX(x);
            selection.setY(y);
            selection.setFill(null); // transparent 
            selection.setStroke(Color.BLACK); // border
            selection.getStrokeDashArray().add(10.0);
            root.getChildren().add(selection);
            isDrawn = true;
        }
    }

    @FXML
    public void onMouseReleased(MouseEvent e) {
        setEndPoint((int) e.getX(), (int) e.getY());
        // System.out.println("end2: " + x2);
        //  System.out.println("end2: " + y2);
        paintComponent(gc);

        root.getChildren().remove(selection);
        selection.setWidth(0);
        selection.setHeight(0);
        isDrawn = false;
        writeShit01(gc);

    }

    public Image scale(Image source, double targetWidth, double targetHeight, boolean preserveRatio) {
        ImageView imageView = new ImageView(source);
        imageView.setPreserveRatio(preserveRatio);
        imageView.setFitWidth(targetWidth);
        System.out.println("width: " + targetWidth + " height: " + targetHeight);
        imageView.setFitHeight(targetHeight);
        return imageView.snapshot(null, null);
    }

    void writeShit01(GraphicsContext gc) {
        try {

            String toWrite = "4060";

            float midy = ((y2 - y) / 2) + 6f;// half of text size!!!
            gc.fillText(toWrite, x, y + midy);

            gc.save();

            PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(0), PDPageContentStream.AppendMode.APPEND, true, true);
          /*  UnixFontDirFinder f =  new UnixFontDirFinder();
            for(File dd: f.find())
                System.out.println(dd.getAbsolutePath());*/
            PDFont pdfont =  PDType0Font.load(document, new File("/usr/share/fonts/truetype/msttcorefonts/Georgia_Bold.ttf"));
            
            contentStream.setFont(pdfont, 12f);
         //  contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12f);
           
            contentStream.beginText();
            
            float heightS = document.getPage(0).getMediaBox().getHeight();
            // contentStream.newLineAtOffset(stripper.getInitNr().get(0).getXDirAdj(),heightS-stripper.getEndNr().get(0).getY());
            contentStream.newLineAtOffset(x, heightS - y);
            contentStream.showText(toWrite);
            contentStream.endText();
            contentStream.close();
            document.save(new File("new.pdf"));

        } catch (IOException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void adjust(double starting_point_x, double starting_point_y, double ending_point_x, double ending_point_y, Rectangle given_rectangle) {
        given_rectangle.setX(starting_point_x);
        given_rectangle.setY(starting_point_y);
        given_rectangle.setWidth(ending_point_x - starting_point_x);
        given_rectangle.setHeight(ending_point_y - starting_point_y);

        if (given_rectangle.getWidth() < 0) {
            given_rectangle.setWidth(-given_rectangle.getWidth());
            given_rectangle.setX(given_rectangle.getX() - given_rectangle.getWidth());
        }

        if (given_rectangle.getHeight() < 0) {
            given_rectangle.setHeight(-given_rectangle.getHeight());
            given_rectangle.setY(given_rectangle.getY() - given_rectangle.getHeight());
        }
    }

}

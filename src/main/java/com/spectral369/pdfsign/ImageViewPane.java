/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spectral369.pdfsign;

/**
 *
 * @author spectral369
 */

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * a resizable Pane that shows an image
 * 
 * @author wf
 *
 */
public class ImageViewPane extends AnchorPane {
  protected static Logger LOGGER = Logger.getLogger("com.bitplan.javafx");
  
  public static boolean debug = false;
  private ObjectProperty<ImageView> imageViewProperty = new SimpleObjectProperty<>();
  private BorderPane imageBorder;
  private boolean showBorder = false;
  private ObservableDoubleValue bindWidthProperty;
  private ObservableDoubleValue bindHeightProperty;
  double imageScaleX;
  double imageScaleY;

  public ObjectProperty<ImageView> imageViewProperty() {
    return imageViewProperty;
  }

  public ImageView getImageView() {
    return imageViewProperty.get();
  }

  public void setImageView(ImageView imageView) {
    this.imageViewProperty.set(imageView);
  }

  public BorderPane getImageBorder() {
    return imageBorder;
  }

  public void setImageBorder(BorderPane imageBorder) {
    this.imageBorder = imageBorder;
  }

  public boolean isShowBorder() {
    return showBorder;
  }

  public void setShowBorder(boolean showBorder) {
    this.showBorder = showBorder;
    getImageBorder().setVisible(showBorder);
  }

  /**
   * create an imageView Pane for the given ImageView
   * 
   * @param imageView
   */
  public ImageViewPane(ImageView imageView) {
    this.setImageView(imageView);
    imageView.setSmooth(true);
    imageView.setCache(true);
    imageView.setPreserveRatio(true);
    getChildren().add(imageView);
    StackPane.setAlignment(imageView, Pos.CENTER);
    initBorder();
    imageView.fitWidthProperty().addListener((obs, oldW, newW) -> {
      Image image = imageViewProperty.get().getImage();
      if (image != null) {
        imageScaleX = newW.doubleValue() / image.getWidth();
        scaleBorder(image);
      }
    });
    imageView.fitHeightProperty().addListener((obs, oldH, newH) -> {
      Image image = imageViewProperty.get().getImage();
      if (image != null) {
        imageScaleY = newH.doubleValue() / image.getHeight();
        scaleBorder(image);
      }
    });

    imageViewProperty.addListener((obs, oldIV, newIV) -> {
      if (oldIV != null) {
        getChildren().remove(oldIV);
      }
      if (newIV != null) {
        getChildren().add(newIV);
        bindSize();
      }
      if (getImageBorder() != null) {
        getImageBorder().toFront();
      }
    });
  }

  /**
   * initialize the Border
   */
  public void initBorder() {
    this.setImageBorder(new BorderPane());
    this.getChildren().add(getImageBorder());
  }

  /**
   * bind my size with the given properties
   * 
   * @param widthProperty
   * @param heightProperty
   */
  public void bindSize(ObservableDoubleValue widthProperty,
      ObservableDoubleValue heightProperty) {
    this.bindWidthProperty = widthProperty;
    this.bindHeightProperty = heightProperty;
    bindSize();
  }

  /**
   * bind my Sizes
   */
  public void bindSize() {
    if (bindWidthProperty != null && bindHeightProperty != null) {
      getImageView().fitWidthProperty().bind(bindWidthProperty);
      getImageView().fitHeightProperty().bind(bindHeightProperty);
      // imageBorder.prefWidthProperty().bind(getImageView().fitWidthProperty());
      // imageBorder.prefHeightProperty().bind(getImageView().fitHeightProperty());
    }
  }

  /**
   * bind my size to the stage's size
   * 
   * @param stage
   */
  public void bindSize(Stage stage) {
    bindSize(stage.widthProperty(), stage.heightProperty());
  }

  /**
   * bind my size to the given region
   * 
   * @param pane
   */
  public void bindSize(Region pane) {
    bindSize(pane.widthProperty(), pane.heightProperty());
  }

  public void scaleBorder(Image image) {
    if (getImageBorder()!=null) {
      if (imageScaleX>imageScaleY) {
        getImageBorder().setPrefHeight(image.getHeight()*imageScaleY);
        getImageBorder().setPrefWidth(image.getWidth()*imageScaleY);
      } else {
        getImageBorder().setPrefHeight(image.getHeight()*imageScaleX);
        getImageBorder().setPrefWidth(image.getWidth()*imageScaleX);
      }
    }
    if (debug)
      LOGGER.log(Level.INFO,String.format("%.1f x %.1f", imageScaleX, imageScaleY));
  }
}
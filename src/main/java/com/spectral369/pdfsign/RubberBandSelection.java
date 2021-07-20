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
/**
 *
 * This file is part of the https://github.com/BITPlan/com.bitplan.javafx open source project
 *
 * The copyright and license below holds true
 * for all files except
 *    - the ones in the stackoverflow package
 *    - SwingFXUtils.java from Oracle which is provided e.g. for the raspberry env
 *
 * Copyright 2017-2018 BITPlan GmbH https://github.com/BITPlan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *  You may obtain a copy of the License at
 *
 *  http:www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

/**
 * RubberBandSelection
 * 
 * @author Roland see
 *         https://stackoverflow.com/questions/30295071/how-to-create-stackpane-on-the-drawn-rectangle-area
 * 
 * @author wf
 *
 */
public class RubberBandSelection {
public class Selection {
		Node node;
		Bounds relativeBounds;

		/**
		 * create a selection from the given
		 * 
		 * @param node
		 * @param rect 
		 */
		public Selection(Node node, Rectangle rect) {
			Bounds pB = parent.getBoundsInParent();
			this.node = node;
			double rx = rect.getX() / pB.getWidth();
			double ry = rect.getY() / pB.getHeight();
			double rw = rect.getWidth() / pB.getWidth();
			double rh = rect.getHeight() / pB.getHeight();
			relativeBounds = new BoundingBox(rx, ry, rw, rh);
		}

		/**
		 * get a string with my relative positions in percent
		 * @return String with x,y,w,h formatted with 
		 */
		public String asPercent() {
			String pStr = String.format("x: %4.1f%% y: %4.1f%% w: %4.1f%% h: %4.1f%%", relativeBounds.getMinX()*100,
					relativeBounds.getMinY()*100, relativeBounds.getHeight()*100, relativeBounds.getWidth()*100);
			return pStr;
		}
	}

	/**
	 * 
	 */
	final DragContext dragContext = new DragContext();
	Rectangle rect;
	Map<Node, Selection> selected = new HashMap<Node, Selection>();

	Parent parent;
	private boolean selectButton = false;

	public boolean isSelectButton() {
		return selectButton;
	}

	public void setSelectButton(boolean selectButton) {
		this.selectButton = selectButton;
	}

	/**
	 * construct me for the given
	 * 
	 * @param parent
	 */
	public RubberBandSelection(Parent parent) {

		this.parent = parent;

		rect = new Rectangle(0, 0, 0, 0);
		rect.setStroke(Color.BLUE);
		rect.setStrokeWidth(1);
		rect.setStrokeLineCap(StrokeLineCap.ROUND);
		rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));

		parent.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
		parent.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
		parent.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
	}

	EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			dragContext.mouseAnchorX = event.getSceneX();
			dragContext.mouseAnchorY = event.getSceneY();

			rect.setX(dragContext.mouseAnchorX);
			rect.setY(dragContext.mouseAnchorY);
			rect.setWidth(0);
			rect.setHeight(0);
			add(rect,rect,false);
		}
	};

	EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {

			// get coordinates
			double x = rect.getX();
			double y = rect.getY();
			double w = rect.getWidth();
			double h = rect.getHeight();

			if (isSelectButton()) {

				// create button
				Button button = new Button();
				button.setDefaultButton(false);
				button.setPrefSize(w, h);
				button.setText("");
				button.setStyle("-fx-border-color: blue;-fx-background-color: rgba(0,0,0, 0.3);");
				button.setLayoutX(x);
				button.setLayoutY(y);
				button.setOnAction(e -> {
					remove((Node) e.getSource(),true);
				});
				add(button,rect,true);

			} else {
				// create rectangle
				Rectangle node = new Rectangle(0, 0, w, h);
				node.setStroke(Color.BLACK);
				node.setFill(Color.BLACK.deriveColor(0, 0, 0, 0.3));
				node.setLayoutX(x);
				node.setLayoutY(y);
				add(node,node,false);
			}

			// remove rubberband
			rect.setX(0);
			rect.setY(0);
			rect.setWidth(0);
			rect.setHeight(0);

			remove(rect,false);

		}
	};

	EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {

			double offsetX = event.getSceneX() - dragContext.mouseAnchorX;
			double offsetY = event.getSceneY() - dragContext.mouseAnchorY;

			if (offsetX > 0)
				rect.setWidth(offsetX);
			else {
				rect.setX(event.getSceneX());
				rect.setWidth(dragContext.mouseAnchorX - rect.getX());
			}

			if (offsetY > 0) {
				rect.setHeight(offsetY);
			} else {
				rect.setY(event.getSceneY());
				rect.setHeight(dragContext.mouseAnchorY - rect.getY());
			}
		}
	};

	private final class DragContext {

		public double mouseAnchorX;
		public double mouseAnchorY;

	}

	public ObservableList<Node> getChildren() {
		if (parent instanceof Pane) {
			return ((Pane) parent).getChildren();
		} else if (parent instanceof Group) {
			return ((Group) parent).getChildren();
		} else {
			return parent.getChildrenUnmodifiable();
		}
	}

	/**
	 * add the given node
	 * 
	 * @param node
	 * @param rect 
	 * @param remember
	 */
	protected void add(Node node, Rectangle rect, boolean remember) {
		getChildren().add(node);
		if (remember)
			this.selected.put(node, new Selection(node,rect));
	}

	/**
	 * remove the given node
	 * 
	 * @param node
	 */
	protected void remove(Node node, boolean remember) {
		getChildren().remove(node);
		if (remember)
			this.selected.remove(node);
	}
}
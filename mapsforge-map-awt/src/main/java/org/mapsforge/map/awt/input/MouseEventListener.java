/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2015 devemux86
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mapsforge.map.awt.input;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.awt.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.util.MapViewProjection;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

public class MouseEventListener extends MouseAdapter {
	private final MapViewPosition mapViewPosition;
	private final MapViewProjection projection;
	private final MapView mapView;

	private Point lastDragPoint;

	public MouseEventListener(MapView mapView) {
		this.mapView = mapView;
		this.projection = new MapViewProjection(mapView);
		this.mapViewPosition = mapView.getModel().mapViewPosition;
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
		if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
			Point point = mouseEvent.getPoint();
			if (this.lastDragPoint != null) {
				int moveHorizontal = point.x - this.lastDragPoint.x;
				int moveVertical = point.y - this.lastDragPoint.y;
				this.mapViewPosition.moveCenter(moveHorizontal, moveVertical);
			}
			this.lastDragPoint = point;

		}
		if (SwingUtilities.isRightMouseButton(mouseEvent)) {
			Point point = mouseEvent.getPoint();
			if (this.lastDragPoint != null) {
				int moveHorizontal = point.x - this.lastDragPoint.x;
				int moveVertical = point.y - this.lastDragPoint.y;
				org.mapsforge.core.model.Point dragXY = new org.mapsforge.core.model.Point(moveHorizontal,
						moveVertical);
				org.mapsforge.core.model.Point tapXY = new org.mapsforge.core.model.Point(mouseEvent.getX(),
						mouseEvent.getY());
				LatLong tapLatLong = projection.fromPixels(tapXY.x, tapXY.y);
				if (tapLatLong != null) {
					for (int i = this.mapView.getLayerManager().getLayers().size() - 1; i >= 0; --i) {
						Layer layer = this.mapView.getLayerManager().getLayers().get(i);
						org.mapsforge.core.model.Point layerXY = projection.toPixels(layer.getPosition());
						if (layer.onDragged(tapLatLong, layerXY, tapXY, dragXY)) {
							break;
						}
					}
				}
			}
			this.lastDragPoint = point;

		}
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
			this.lastDragPoint = mouseEvent.getPoint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
		this.lastDragPoint = null;
		for (int i = this.mapView.getLayerManager().getLayers().size() - 1; i >= 0; --i) {
			Layer layer = this.mapView.getLayerManager().getLayers().get(i);
			org.mapsforge.core.model.Point layerXY = projection.toPixels(layer.getPosition());
			layer.onMouseRelased();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
		byte zoomLevelDiff = (byte) -mouseWheelEvent.getWheelRotation();
		this.mapViewPosition.zoom(zoomLevelDiff);
	}

	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		org.mapsforge.core.model.Point tapXY = new org.mapsforge.core.model.Point(e.getX(), e.getY());
		LatLong tapLatLong = projection.fromPixels(tapXY.x, tapXY.y);
		if (tapLatLong != null) {
			for (int i = this.mapView.getLayerManager().getLayers().size() - 1; i >= 0; --i) {
				Layer layer = this.mapView.getLayerManager().getLayers().get(i);
				org.mapsforge.core.model.Point layerXY = projection.toPixels(layer.getPosition());
				if (layer.onTap(tapLatLong, layerXY, tapXY)) {
					break;
				}
			}
		}
	}
}

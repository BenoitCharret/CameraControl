package com.axioConsulting.cameracontrol.app.item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by benoit on 31/08/15.
 */
public class HostItems {
		private static List<HostItem> items;
		private static Map<String,HostItem> mapItems;

		public static List<HostItem> getItems() {
				return items;
		}

		public static void setItems(List<HostItem> items) {
				HostItems.items = items;
				mapItems=new LinkedHashMap<String, HostItem>();
				for (HostItem hostItem: items){
						mapItems.put(hostItem.getId(),hostItem);
				}
		}

		public static Map<String, HostItem> getMapItems() {
				return mapItems;
		}
}

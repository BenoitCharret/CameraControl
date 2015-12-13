package com.axioConsulting.cameracontrol.app.item;

/**
 * Created by benoit on 31/08/15.
 */
public class HostItem {
		private String id;
		private String name;

		public HostItem(String id,String name){
				this.id=id;
				this.name=name;
		}

		public String getId() {
				return id;
		}

		public void setId(String id) {
				this.id = id;
		}

		public String getName() {
				return name;
		}

		public void setName(String name) {
				this.name = name;
		}

		@Override public String toString() {
				return name;
		}
}

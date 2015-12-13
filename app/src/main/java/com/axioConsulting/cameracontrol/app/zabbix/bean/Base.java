package com.axioConsulting.cameracontrol.app.zabbix.bean;

/**
 * Created by benoit on 30/08/15.
 */
public abstract class Base {

		private String jsonrpc;

		private Integer id;

		public String getJsonrpc() {
				return jsonrpc;
		}

		public void setJsonrpc(String jsonrpc) {
				this.jsonrpc = jsonrpc;
		}

		public Integer getId() {
				return id;
		}

		public void setId(Integer id) {
				this.id = id;
		}
}

package com.axioConsulting.cameracontrol.app.zabbix.bean;

/**
 * Created by benoit on 30/08/15.
 */
public class HostGroup {
		private Integer groupid;

		private String name;

		private String internal;

		private String flags;

		public Integer getGroupid() {
				return groupid;
		}

		public void setGroupid(Integer groupid) {
				this.groupid = groupid;
		}

		public String getName() {
				return name;
		}

		public void setName(String name) {
				this.name = name;
		}

		public String getInternal() {
				return internal;
		}

		public void setInternal(String internal) {
				this.internal = internal;
		}

		public String getFlags() {
				return flags;
		}

		public void setFlags(String flags) {
				this.flags = flags;
		}
}

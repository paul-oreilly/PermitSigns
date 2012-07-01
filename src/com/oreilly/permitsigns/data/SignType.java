package com.oreilly.permitsigns.data;

public enum SignType {
	UNDEFINED, SALEPREVIEW, SALE, ROLE, MONITOR;

	static public SignType fromString(String data, String errorLocation) {
		data = data.toLowerCase().trim();
		if (data.contentEquals("preview"))
			return SALEPREVIEW;
		if (data.contentEquals("sale"))
			return SALE;
		if (data.contentEquals("role"))
			return ROLE;
		if (data.contentEquals("monitor"))
			return MONITOR;
		return UNDEFINED;
	}

	@Override
	public String toString() {
		switch (this) {
		case UNDEFINED:
			return "undefined";
		case SALEPREVIEW:
			return "preview";
		case SALE:
			return "sale";
		case ROLE:
			return "role";
		case MONITOR:
			return "monitor";
		}
		return "undefined";
	}
}

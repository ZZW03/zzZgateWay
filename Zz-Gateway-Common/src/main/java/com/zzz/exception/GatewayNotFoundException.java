package com.zzz.exception;


import com.zzz.constant.ResponseCode;

public class GatewayNotFoundException extends GatewayBaseException {

	private static final long serialVersionUID = -5534700534739261761L;

	public GatewayNotFoundException(ResponseCode code) {
		super(code.getMessage(), code);
	}

	public GatewayNotFoundException(Throwable cause, ResponseCode code) {
		super(code.getMessage(), cause, code);
	}
	
}

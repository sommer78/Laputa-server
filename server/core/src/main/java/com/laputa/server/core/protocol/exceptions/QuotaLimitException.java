package com.laputa.server.core.protocol.exceptions;

import com.laputa.server.core.protocol.enums.Response;

public class QuotaLimitException extends BaseServerException {

	public QuotaLimitException(String message) {
		super(message, Response.QUOTA_LIMIT);
	}

}

package com.linagora.android.linshare.network

data class Port(val portNumber: Int) {
    init {
        require(VALID_PORT_RANGE.contains(portNumber))
    }

    companion object {
        private const val MAX_PORT_VALUE = 65535
        val VALID_PORT_RANGE = 1.rangeTo(MAX_PORT_VALUE)
    }
}

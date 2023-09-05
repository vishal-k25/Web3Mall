package com.example.web3mall.activities

class SignUtils {
    companion object {
        fun getPersonalSignBody(account: String): String {
            val msg = "My email is john@doe.com - ${System.currentTimeMillis()}".encodeToByteArray()
                .joinToString(separator = "", prefix = "0x") { eachByte -> "%02x".format(eachByte) }
            return "[\"$msg\", \"$account\"]"
        }
    }
}
package com.solar.hungnb.demovpn.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import com.solar.hungnb.demovpn.R
import de.blinkt.openvpn.core.ConfigParser
import de.blinkt.openvpn.core.ProfileManager
import de.blinkt.openvpn.core.VPNLaunchHelper
import java.io.IOException
import java.io.StringReader


object CommonUtils {
    @JvmStatic
    fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun isRecentActivity(className: String): Boolean {
        val RECENT_ACTIVITY: String
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            RECENT_ACTIVITY = "com.android.systemui.recents.RecentsActivity"
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            RECENT_ACTIVITY = "com.android.systemui.recent.RecentsActivity"
        } else {
            RECENT_ACTIVITY = "com.android.internal.policy.impl.RecentApplicationDialog";
        }
        if (RECENT_ACTIVITY.equals(className, true)) {
            return true
        }

        return false
    }

    @JvmStatic
    fun startDefaultVpn(context: Context){
        val cp = ConfigParser()
        try {
            cp.parseConfig(StringReader(getConfig()))
            val profile = cp.convertProfile()
            val needPW = profile.needUserPWInput(false)
            if (needPW == R.string.password) {
                profile.mUsername = "openvpn"
                profile.mPassword = "9V9m6lMbVDwN"
            }
            val checkProfile = profile.checkProfile(context)
            if (checkProfile != R.string.no_error_found) {
                Toast.makeText(context, checkProfile, Toast.LENGTH_SHORT).show()
                return
            }
            ProfileManager.setTemporaryProfile(profile)
            VPNLaunchHelper.startOpenVpn(profile, context.applicationContext)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ConfigParser.ConfigParseError) {
            e.printStackTrace()
        }
    }

    private fun getConfig(): String {
        return "# Automatically generated OpenVPN client config file\n" +
                "# Generated on Mon Dec 10 01:17:06 2018 by SolarVPN1\n" +
                "\n" +
                "# Default Cipher\n" +
                "cipher AES-256-CBC\n" +
                "# Note: this config file contains inline private keys\n" +
                "#       and therefore should be kept confidential!\n" +
                "# Note: this configuration is user-locked to the username below\n" +
                "# OVPN_ACCESS_SERVER_USERNAME=openvpn\n" +
                "# Define the profile name of this particular configuration file\n" +
                "# OVPN_ACCESS_SERVER_PROFILE=openvpn@45.76.202.150\n" +
                "# OVPN_ACCESS_SERVER_CLI_PREF_ALLOW_WEB_IMPORT=True\n" +
                "# OVPN_ACCESS_SERVER_CLI_PREF_BASIC_CLIENT=False\n" +
                "# OVPN_ACCESS_SERVER_CLI_PREF_ENABLE_CONNECT=True\n" +
                "# OVPN_ACCESS_SERVER_CLI_PREF_ENABLE_XD_PROXY=True\n" +
                "# OVPN_ACCESS_SERVER_WSHOST=45.76.202.150:443\n" +
                "# OVPN_ACCESS_SERVER_WEB_CA_BUNDLE_START\n" +
                "# -----BEGIN CERTIFICATE-----\n" +
                "# MIIDBDCCAeygAwIBAgIEW/43DzANBgkqhkiG9w0BAQsFADA7MTkwNwYDVQQDDDBP\n" +
                "# cGVuVlBOIFdlYiBDQSAyMDE4LjExLjI4IDA2OjM0OjU1IFVUQyBTb2xhclZQTjEw\n" +
                "# HhcNMTgxMTIxMDYzNDU1WhcNMjgxMTI1MDYzNDU1WjA7MTkwNwYDVQQDDDBPcGVu\n" +
                "# VlBOIFdlYiBDQSAyMDE4LjExLjI4IDA2OjM0OjU1IFVUQyBTb2xhclZQTjEwggEi\n" +
                "# MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC99TcqcrT2thr1IQnZM2sXKHOd\n" +
                "# c0DyASvdZb4Og64Y4Elp7Qc9BCAQN5PYeztLzcQpl/S9jA6XEeEu1O+qt2THpgzu\n" +
                "# s43W4bx8teZ9FA4nwNK4qjZKq47Tp35gicIEwQzOfixAoyOjKrKeBwyZ9m7p+isQ\n" +
                "# ckJo2OND4bSceN5ANpcYreyyREIE5/5Rrsq1jqnKPuAH742ZMhmFcG+jKNo3C8o9\n" +
                "# mw79p7wHA+ahd7VSFRfeaN6JS3vLLeHk+1ROJ2PfmdQvgRk68pVvS7s4k2RhnSJo\n" +
                "# h3ghZdSKg0CxLWn1dOdsrvPiLfiyz2lZ00F+n1XdHD9UkQfsvwxvbEkmYZZVAgMB\n" +
                "# AAGjEDAOMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAH9ccjOyqajj\n" +
                "# +qD1MLBe/5FsOCy+AwjOCVOA3N4x3ABAhFSoD/nRNlFD2L5aMcpFHw4qk/JOjkAM\n" +
                "# OMGt7YvdtANT0Re8niYcX0h5RBethRhmfNlwgcCIWvfZDWLDzvDZkFhBPNMZz+e9\n" +
                "# hNnUAGbGijdpPNjhwe/WdOoFPPl28gZXV3m0Hxr++IWRiMrzAeGqI3/rF5QHbXBQ\n" +
                "# K+zO+csDCx2DeYQnEHSJFgV5D6DyBqZqjFAuMukYLdG8ny/i0Oz/tRX4RnmEzm4v\n" +
                "# KwBz5haWDRNpHzT0ZUwmJhTUNn0gwMFytESBZDbP+1I1XZTja8NQngkpzVz09hBX\n" +
                "# EAZZ5h4a1fg=\n" +
                "# -----END CERTIFICATE-----\n" +
                "# OVPN_ACCESS_SERVER_WEB_CA_BUNDLE_STOP\n" +
                "# OVPN_ACCESS_SERVER_IS_OPENVPN_WEB_CA=1\n" +
                "# OVPN_ACCESS_SERVER_ORGANIZATION=OpenVPN, Inc.\n" +
                "setenv FORWARD_COMPATIBLE 1\n" +
                "client\n" +
                "server-poll-timeout 4\n" +
                "nobind\n" +
                "remote 45.76.202.150 1194 udp\n" +
                "remote 45.76.202.150 1194 udp\n" +
                "remote 45.76.202.150 443 tcp\n" +
                "remote 45.76.202.150 1194 udp\n" +
                "remote 45.76.202.150 1194 udp\n" +
                "remote 45.76.202.150 1194 udp\n" +
                "remote 45.76.202.150 1194 udp\n" +
                "remote 45.76.202.150 1194 udp\n" +
                "dev tun\n" +
                "dev-type tun\n" +
                "ns-cert-type server\n" +
                "setenv opt tls-version-min 1.0 or-highest\n" +
                "reneg-sec 604800\n" +
                "sndbuf 100000\n" +
                "rcvbuf 100000\n" +
                "auth-user-pass\n" +
                "# NOTE: LZO commands are pushed by the Access Server at connect time.\n" +
                "# NOTE: The below line doesn't disable LZO.\n" +
                "comp-lzo no\n" +
                "verb 3\n" +
                "setenv PUSH_PEER_INFO\n" +
                "\n" +
                "<ca>\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIICuDCCAaCgAwIBAgIEW/43DjANBgkqhkiG9w0BAQsFADAVMRMwEQYDVQQDDApP\n" +
                "cGVuVlBOIENBMB4XDTE4MTEyMTA2MzQ1NFoXDTI4MTEyNTA2MzQ1NFowFTETMBEG\n" +
                "A1UEAwwKT3BlblZQTiBDQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB\n" +
                "ANrgjeC6k/P8b36YNqsZIfRJkZuX9RLp+6Ap6TSBanP3Dwf4AM2ivn0dSgYRQL1d\n" +
                "RGiF65fUkqJJQUak9E2bm6Qd5yYyl7AqG4SpUcg8J1Bokf1PQpr33omKCx7nUlM8\n" +
                "LkKo70iVbeyIhxIFdDxqZKy9Ve+FHQF1YbdyifL49ihseJnKeXfnoCEvktqP9qSe\n" +
                "FSKKrosvo2aDX+3YQ+6TSjOX25Ln0KcHma9odTj0mZ8ygzKqrXO0kviNI9W4gUaq\n" +
                "y5rnJqwmTV9lhptiqvdWVSYtkdO3Vjo2TvemufpDHe4q/cobCWeta8R+rFXGNz+r\n" +
                "L8ljZvTtOYHo4GbJc8emxzMCAwEAAaMQMA4wDAYDVR0TBAUwAwEB/zANBgkqhkiG\n" +
                "9w0BAQsFAAOCAQEAqaq9rydHXT6+qE830o6Jrx+iy3RxUOZdvlXfXMbPXOLLl0IL\n" +
                "oSo8pt19cn5BA/QIvAkXjzBlVbxTdI+CIDU/rGbgWtwO9FusrwbyoQcn074dw0vT\n" +
                "g89ec0dnXcO1GG8gmH4chK1Kicdt3sVp7tPknuaVROeYw2MyEtdkFO1fnr2vmGjq\n" +
                "8Srrc1Z4q+BYXtFO4IWEeTZh5YY+6kIGXT6hJH43v4mTwmCBzJsf18iqhp2nm0iN\n" +
                "LcKqTUnG5550/isHHSX3Tl2UnTq19AnkJLA3yO0SqYlpepQHKARDlxxzV4n/rjVf\n" +
                "053N51vOYsdYMLHlFoHiLtXvuYDuHtabHCc+PQ==\n" +
                "-----END CERTIFICATE-----\n" +
                "</ca>\n" +
                "\n" +
                "<cert>\n" +
                "-----BEGIN CERTIFICATE-----\n" +
                "MIICwjCCAaqgAwIBAgIBAjANBgkqhkiG9w0BAQsFADAVMRMwEQYDVQQDDApPcGVu\n" +
                "VlBOIENBMB4XDTE4MTEyMTA4NDEwOFoXDTI4MTEyNTA4NDEwOFowEjEQMA4GA1UE\n" +
                "AwwHb3BlbnZwbjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAONVnc7A\n" +
                "7Xrun6B6UvKEj27oEmB3Fhn+vDXXhmhRagagH8yUmusLohwrAFYAlHLH1UQqHmCA\n" +
                "aNTdXikkxSkxm99xf57bX6X9720IQ0nCX/YUpA44iV8oUxC5PPwSVyES7iDPiRAf\n" +
                "WnkB2eAzDKMXziH0nXM6wHjAj+yQsTlC8fzPdUSWOb+3dwdttX5WSADu5M/CgJrs\n" +
                "vS63r+kRveU1IFeOyWGrRhXaUKbxOzr0ahNIRdB9qlKJBgoG+c3uuIpXZ43hd/rL\n" +
                "8Ff8xDEBdsNdijTZFOH6TjUpv57gxs7yGqoRL9/K3Y3OiDocsClDWbzT3Qks1baW\n" +
                "038XUEZNAPpUq1kCAwEAAaMgMB4wCQYDVR0TBAIwADARBglghkgBhvhCAQEEBAMC\n" +
                "B4AwDQYJKoZIhvcNAQELBQADggEBAAnRl/FMRf+b+GwhaMmlgWQhoiuWDPHcA2cv\n" +
                "Jxn/xZVZJ6oIUUKi5l2vnDvbvgI64rwMVkJU9jc5zQxxKzeD1T7R91c6macCIfeJ\n" +
                "a1DIiSV9kdleW2KihH89RW3uIEZFN+xD2QCk5EH6p5Ub5Drn+5bVZiaJuwYmKsVe\n" +
                "Jvmr/Qu3Isz3ZM1S/qZGV/AgIUlxQ2llrXGY41LIHeObudF7UaVE43cyrH7BFfRR\n" +
                "wtHJPs4aDPzd/C8UusXE4Bo7pVQxoBrDxFAGJmgBHmkYku6UP2YGEaNnItE6ekaZ\n" +
                "MQTtcAW3S/sTILsVpulQscNFsyy47sq9eEnE0ACzF2WKTl4V9k8=\n" +
                "-----END CERTIFICATE-----\n" +
                "</cert>\n" +
                "\n" +
                "<key>\n" +
                "-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDjVZ3OwO167p+g\n" +
                "elLyhI9u6BJgdxYZ/rw114ZoUWoGoB/MlJrrC6IcKwBWAJRyx9VEKh5ggGjU3V4p\n" +
                "JMUpMZvfcX+e21+l/e9tCENJwl/2FKQOOIlfKFMQuTz8ElchEu4gz4kQH1p5Adng\n" +
                "MwyjF84h9J1zOsB4wI/skLE5QvH8z3VEljm/t3cHbbV+VkgA7uTPwoCa7L0ut6/p\n" +
                "Eb3lNSBXjslhq0YV2lCm8Ts69GoTSEXQfapSiQYKBvnN7riKV2eN4Xf6y/BX/MQx\n" +
                "AXbDXYo02RTh+k41Kb+e4MbO8hqqES/fyt2Nzog6HLApQ1m8090JLNW2ltN/F1BG\n" +
                "TQD6VKtZAgMBAAECggEBANvnft5364DmAQ14JnKn3iN3ZAdiSsPckO04TbDtSDWe\n" +
                "gQQhn/XL7AV5ZzrKFs8tFNR6NaY9sKpwdFwyUHH3pgMvxDbDmDN2fzsmKgjYeIEP\n" +
                "GF8VO3UMCcX8mcxwZU3+BNFCUCCDY734F2zzlL7TETfxarkDVRm5k79Pe8+bPX/A\n" +
                "z7ISGFhCc1OjT1U9XrTPLnhL8Dr6pu1nos/Jwxi9mLqQEdeWmBpZ/4h+UNObmgMu\n" +
                "5g2A8it+3f1/MtHXsH2LWC2WLlG/W7M5Lq3yk2wBY2s6ON9hzibbUO4lfEySCP6D\n" +
                "ty4nRumuaLG7Us51fRyh6UamRkFAxFu20VI/4a0jpy0CgYEA9WGm7djaYjw/WhxJ\n" +
                "095a1t8kSgSSInSkKD5TFN9V6reID3o7Gtz3d23EQyuWXregB1NDndp9dQ0nQOo9\n" +
                "450ry7hKFdScnSnScLaA4FqQKGHJwBwFEBpVtssJxq/haN0NzYj7RYY6jsyoZWOC\n" +
                "nTal88/ePTodITHv56JQX94RxBMCgYEA7SwJ5O5VxZOTr90ITHBpGWoa1Uq6PJ78\n" +
                "ZwdbovnLg26WdZRc89GV0BEbAHnT+goHqUC4ATNdIJ6/U6Pfmbvkp7T0EFszBacz\n" +
                "tQg9u+z7BAwpPmMiEG0lAC8WuZ9ovI5qJQ9zVFRtXSRADE2E5HYQGgLD5ZrqQAHM\n" +
                "ACHjX/l9yGMCgYBnuYKtsegGdH83IRQYKjrt719QpoP7aqDlngrSnOGdmT07hais\n" +
                "X1GAO3cmCavDmA1ea1T+yhgUPZ4lM587svmLYPh4J+qYCNC68nqh28ZqO4Pj4DRH\n" +
                "rUokPcmBImG/SQoHourMZcQlDt/0E89nk20tFeZUrVcY7BoEKTmefKm33wKBgAx/\n" +
                "662TGhYlpvz3yz4sFn3uZ6eiHpqfTnInox69x4oPxAJCZXu8KgcjP6cQxKlC6hBz\n" +
                "hr8Zc+kKNM81rc0uZ+im0s4h4FiF7WW3H3nODZSzB2FwXBU9i1utH6d8zUpSvylg\n" +
                "3RUutezYwj9jvoND7alRR/3L4lipP5UqNA+U2Jo9AoGAZbyMk0E3vOEUP5ZZfMdi\n" +
                "Ry3ocoo6EwhuPWmoxfCdRjvyppUtAJ6fBxJ+rxLmPt/AiMABg5j3qNJ+mrqlQPKf\n" +
                "J43C9MDwdu6oXCwCEP2EveEwxqCIAo7XsFgZIauo58oK6+Kn+CuKOqrokdTk1psN\n" +
                "NLfAfvPdYHzNSuJKdBWd39I=\n" +
                "-----END PRIVATE KEY-----\n" +
                "</key>\n" +
                "\n" +
                "key-direction 1\n" +
                "<tls-auth>\n" +
                "#\n" +
                "# 2048 bit OpenVPN static key (Server Agent)\n" +
                "#\n" +
                "-----BEGIN OpenVPN Static key V1-----\n" +
                "e45a393f2e7fb5072731bdba080a7329\n" +
                "d671804c018c57b1f632e6c8083cbead\n" +
                "ab85ffedf7acb9253dd55fb430d8edc7\n" +
                "6a46e79c31d51febb97e58279bcb6f13\n" +
                "8504015788edf970b52b010f8faf5b9f\n" +
                "fc0e928f1cdf114b60fadfc62ab96123\n" +
                "1e04e8198d9227d6b15e1412f9ca117c\n" +
                "d853ba66025249de0622be369e571f3c\n" +
                "60c4c051d2d1cb6e0d03f253fe4899a7\n" +
                "660578ea713a427cfd2f2c37ee49e292\n" +
                "9f4a112a25abd067fef340e8dd914d7c\n" +
                "3c59385ec48a26293184e6520e9e9d68\n" +
                "f280010c5c8b47a178dcbcf399d9dce6\n" +
                "88ba2cef2c5f4dbd2008b28faa9bbc58\n" +
                "04b1b7814d0a01909c207113676b45b3\n" +
                "1a1b5f6a6e0182cba4e156bead54b1d0\n" +
                "-----END OpenVPN Static key V1-----\n" +
                "</tls-auth>\n" +
                "\n" +
                "## -----BEGIN RSA SIGNATURE-----\n" +
                "## DIGEST:sha256\n" +
                "## Bc9tvqMk1HJ9J2U1p4qR6hWg1gJu66C96ZzbscPnJ02Aib14KA\n" +
                "## ezm7OSKhzxMmpXw3jqEm/iy24sY9dPAGasjKm1s8R4q31oP9kk\n" +
                "## eyerpTX1mENxuzki/y/32S786KpfDV6DG5OSsvYaQAkwLmcjm0\n" +
                "## X1bdARdeaFHSy6Q1CbvhYJqmBfn8A4aKnQGCIndIW981ZaJN+0\n" +
                "## FmdbGLzaONHv6wL3GObLm+ru92Ex41q+UNKxsjuPkPqCpOv5Js\n" +
                "## dG74Z4MO4+b7qHPrakJp/JdAnLHrsd+xj1G7WF896DD3HIsicK\n" +
                "## 5WUFPuSvvK+y/e3o8JEcQd96FDC6+RjXtICiKZDgQg==\n" +
                "## -----END RSA SIGNATURE-----\n" +
                "## -----BEGIN CERTIFICATE-----\n" +
                "## MIIC7TCCAdWgAwIBAgIEW/43EDANBgkqhkiG9w0BAQsFADA7MTkwNwYDVQQDDDBP\n" +
                "## cGVuVlBOIFdlYiBDQSAyMDE4LjExLjI4IDA2OjM0OjU1IFVUQyBTb2xhclZQTjEw\n" +
                "## HhcNMTgxMTIxMDYzNDU1WhcNMjgxMTI1MDYzNDU1WjAUMRIwEAYDVQQDDAlTb2xh\n" +
                "## clZQTjEwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDdUWMBMVM9/4Ux\n" +
                "## OYzjSk9Ni3OpGon2LNGHwMEPm2s8bfj6vQtMUOCtTrKstHcyDRwYwWvoSt/uhgnH\n" +
                "## 0sZvvVj+PItyYjjA0q3ig+bQ1loB/xmQagXzayZHohWK660UC3sR5LEuuKuV3FpC\n" +
                "## KN40XmIzmhPstwH5SykheomYHSkjr3R7Gw9YUtXyDmVRc5HSgHBxid6DUkh8BY0o\n" +
                "## JHJ7yMN+WZgh31bLQhuhF3OcAvhABFMjgdgQl9UBKGm4ZJ18J6snXo/GDKSPge7r\n" +
                "## nRKLXu2qJLth9xcKQLCNSOuIIFvOQHUtY6YTxpvjBRqtmPzPomm2e+Pdbxmj6HOt\n" +
                "## Wz7FLOHvAgMBAAGjIDAeMAkGA1UdEwQCMAAwEQYJYIZIAYb4QgEBBAQDAgZAMA0G\n" +
                "## CSqGSIb3DQEBCwUAA4IBAQCPViKANeuyPuXPdhOOtj2XlSjffX01OudjH2DTEUYb\n" +
                "## OCr5cjMPXljBQrhl7f/pLOY2TmLzhKGE5ub2Uq9effusAdH1AQ6woq0Ey5aF/BKi\n" +
                "## hFWG/2YLMvfhPZAQ3paAhxYjknWjITRCI9MFGECLMU439+FYe8MnYWExBjyXZWOQ\n" +
                "## +LlqL2jrLlq7FeUYI1ZUeBGeY6FiUObCM5Ba+s5Qiw31v/7L42vxmovvB5kIq5Pa\n" +
                "## TPV5Hfp8rUZXtYVTVCFGw1Cvur60+EW4YjR5pY49jVNbdlTKo9Vl5wR4t+CCj/Qr\n" +
                "## TQhJ9owBhywp+jKFjaiIyAxMt4N6npNzrFoH04ueNi9x\n" +
                "## -----END CERTIFICATE-----\n" +
                "## -----BEGIN CERTIFICATE-----\n" +
                "## MIIDBDCCAeygAwIBAgIEW/43DzANBgkqhkiG9w0BAQsFADA7MTkwNwYDVQQDDDBP\n" +
                "## cGVuVlBOIFdlYiBDQSAyMDE4LjExLjI4IDA2OjM0OjU1IFVUQyBTb2xhclZQTjEw\n" +
                "## HhcNMTgxMTIxMDYzNDU1WhcNMjgxMTI1MDYzNDU1WjA7MTkwNwYDVQQDDDBPcGVu\n" +
                "## VlBOIFdlYiBDQSAyMDE4LjExLjI4IDA2OjM0OjU1IFVUQyBTb2xhclZQTjEwggEi\n" +
                "## MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC99TcqcrT2thr1IQnZM2sXKHOd\n" +
                "## c0DyASvdZb4Og64Y4Elp7Qc9BCAQN5PYeztLzcQpl/S9jA6XEeEu1O+qt2THpgzu\n" +
                "## s43W4bx8teZ9FA4nwNK4qjZKq47Tp35gicIEwQzOfixAoyOjKrKeBwyZ9m7p+isQ\n" +
                "## ckJo2OND4bSceN5ANpcYreyyREIE5/5Rrsq1jqnKPuAH742ZMhmFcG+jKNo3C8o9\n" +
                "## mw79p7wHA+ahd7VSFRfeaN6JS3vLLeHk+1ROJ2PfmdQvgRk68pVvS7s4k2RhnSJo\n" +
                "## h3ghZdSKg0CxLWn1dOdsrvPiLfiyz2lZ00F+n1XdHD9UkQfsvwxvbEkmYZZVAgMB\n" +
                "## AAGjEDAOMAwGA1UdEwQFMAMBAf8wDQYJKoZIhvcNAQELBQADggEBAH9ccjOyqajj\n" +
                "## +qD1MLBe/5FsOCy+AwjOCVOA3N4x3ABAhFSoD/nRNlFD2L5aMcpFHw4qk/JOjkAM\n" +
                "## OMGt7YvdtANT0Re8niYcX0h5RBethRhmfNlwgcCIWvfZDWLDzvDZkFhBPNMZz+e9\n" +
                "## hNnUAGbGijdpPNjhwe/WdOoFPPl28gZXV3m0Hxr++IWRiMrzAeGqI3/rF5QHbXBQ\n" +
                "## K+zO+csDCx2DeYQnEHSJFgV5D6DyBqZqjFAuMukYLdG8ny/i0Oz/tRX4RnmEzm4v\n" +
                "## KwBz5haWDRNpHzT0ZUwmJhTUNn0gwMFytESBZDbP+1I1XZTja8NQngkpzVz09hBX\n" +
                "## EAZZ5h4a1fg=\n" +
                "## -----END CERTIFICATE-----\n"
    }
}
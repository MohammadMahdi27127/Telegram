package ir.daftar.goftogu

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

/**
 * Network helper utilities for checking connectivity and handling errors
 */
object NetworkHelper {
    
    /**
     * Check if device is online/connected to network
     */
    fun isDeviceOnline(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            
            capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            )
        } catch (e: Exception) {
            Log.e("NetworkHelper", "Error checking network status", e)
            false
        }
    }
    
    /**
     * Get user-friendly error message from exception
     */
    fun getErrorMessage(exception: Exception): String {
        return when (exception) {
            is retrofit2.HttpException -> {
                when (exception.code()) {
                    400 -> "درخواست نامعتبر"
                    401 -> "عدم احراز هویت"
                    403 -> "دسترسی ممنوع"
                    404 -> "منبع یافت نشد"
                    500 -> "خطای سرور"
                    else -> "خطای سرور (${exception.code()})"
                }
            }
            is java.net.SocketTimeoutException -> "زمان اتصال به پایان رسید - اتصال اینترنت را بررسی کنید"
            is java.net.ConnectException -> "خطا در اتصال به سرور - اتصال اینترنت را بررسی کنید"
            is java.io.IOException -> "خطا در شبکه - اتصال اینترنت را بررسی کنید"
            else -> "خطای نامشخص: ${exception.message ?: "نامعلوم"}"
        }
    }
}

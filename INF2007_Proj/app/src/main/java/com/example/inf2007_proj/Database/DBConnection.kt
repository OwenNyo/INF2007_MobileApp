package com.example.inf2007_proj.Database

import android.annotation.SuppressLint
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DBConnection {
    var ip = "SQL1002.site4now.net"

    // This is default if you are using JTDS driver.
    var classs = "net.sourceforge.jtds.jdbc.Driver"

    // Name of your database.
    var db = "db_ab6299_inf2007"

    // Username and password are required for security.
    var un = "db_ab6299_inf2007_admin"
    var password = "INF2007mad"
    @SuppressLint("NewApi")

    fun CONN(): Connection? {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn: Connection? = null
        val ConnURL: String
        try {
            Class.forName(classs)
            ConnURL = "jdbc:jtds:sqlserver://$ip;database=$db;user=$un;password=$password;"
            conn = DriverManager.getConnection(ConnURL)
        } catch (se: SQLException) {
            Log.e("error here 1 : ", se.message!!)
        } catch (e: ClassNotFoundException) {
            Log.e("error here 2 : ", e.message!!)
        } catch (e: Exception) {
            Log.e("error here 3 : ", e.message!!)
        }
        return conn
    }
}
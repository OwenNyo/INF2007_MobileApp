package com.example.inf2007_proj.DataManager

import com.example.inf2007_proj.Database.DBConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class LoginDataManager {

    private val connectionClass: DBConnection = DBConnection() //Ensure it's initialized

    suspend fun getLoginStatus(adminNumber: String, password: String): String? {
        return withContext(Dispatchers.IO) {
            var status: String? = null
            val connect = connectionClass.CONN()
            try {
                val query = "SELECT * FROM UserDetails WHERE Username=? AND Password=?"
                val stmt: PreparedStatement = connect!!.prepareStatement(query)
                stmt.setString(1, adminNumber)
                stmt.setString(2, password)
                val rs: ResultSet = stmt.executeQuery()

                if (rs.next()) {
                    status = "Pass"
                }
                rs.close()
                stmt.close()
                connect?.close() //Close connection
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            status
        }
    }
}

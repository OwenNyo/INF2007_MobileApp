package com.example.inf2007_proj.DataManager

import com.example.inf2007_proj.Database.DBConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class SettingsDataManager {

    //Initialize connection class when the object is created
    private val connectionClass: DBConnection = DBConnection()

    // **Fetch user settings (Now using suspend function)**
    suspend fun getSettingsDetail(username: String): Map<String, String?>? {
        return withContext(Dispatchers.IO) {
            var settings: Map<String, String?>? = null
            val connect = connectionClass.CONN()  // Ensures connectionClass is initialized
            try {
                val query = "SELECT Username, Password, ContactNum, EmailAdd, HouseAdd, HousePostal FROM UserDetails WHERE Username=?"
                val stmt: PreparedStatement = connect!!.prepareStatement(query)
                stmt.setString(1, username)
                val rs: ResultSet = stmt.executeQuery()

                if (rs.next()) {
                    settings = mapOf(
                        "Username" to rs.getString("Username"),
                        "Password" to rs.getString("Password"),
                        "ContactNum" to rs.getString("ContactNum"),
                        "EmailAdd" to rs.getString("EmailAdd"),
                        "HouseAdd" to rs.getString("HouseAdd"),
                        "HousePostal" to rs.getString("HousePostal")
                    )
                }
                rs.close()
                stmt.close()
                connect.close()  //Close the connection properly
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            settings
        }
    }

    // **Update user settings (Now using suspend function)**
    suspend fun updateSettingsDetail(
        username: String, password: String, contactNum: String, emailAdd: String, houseAdd: String, housePostal: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            var isSuccess = false
            val connect = connectionClass.CONN()  //Ensures connectionClass is initialized
            try {
                val query = """
                UPDATE UserDetails 
                SET Password=?, ContactNum=?, EmailAdd=?, HouseAdd=?, HousePostal=? 
                WHERE Username=?
                """
                val stmt: PreparedStatement = connect!!.prepareStatement(query)
                stmt.setString(1, password)
                stmt.setString(2, contactNum)
                stmt.setString(3, emailAdd)
                stmt.setString(4, houseAdd)
                stmt.setString(5, housePostal)
                stmt.setString(6, username)

                val rowsUpdated = stmt.executeUpdate()
                isSuccess = rowsUpdated > 0
                stmt.close()
                connect.close()  // Close the connection properly
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            isSuccess
        }
    }
}

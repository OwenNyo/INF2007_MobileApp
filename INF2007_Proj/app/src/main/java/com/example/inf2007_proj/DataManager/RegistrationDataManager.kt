package com.example.inf2007_proj.DataManager

import com.example.inf2007_proj.Database.DBConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class RegistrationDataManager {

    private val connectionClass: DBConnection = DBConnection() //Ensures it's initialized

    suspend fun isUsernameTaken(username: String): Boolean {
        return withContext(Dispatchers.IO) {
            var isTaken = false
            val connect = connectionClass.CONN()
            try {
                val query = "SELECT * FROM UserDetails WHERE Username = ?"
                val stmt: PreparedStatement = connect!!.prepareStatement(query)
                stmt.setString(1, username)
                val rs: ResultSet = stmt.executeQuery()

                if (rs.next()) {
                    isTaken = true
                }
                rs.close()
                stmt.close()
                connect?.close() //Closes connection
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            isTaken
        }
    }

    suspend fun registerUser(
        username: String,
        password: String,
        contactNumber: String,
        emailAddress: String,
        houseAddress: String,
        postalCode: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            var isSuccess = false
            val connect = connectionClass.CONN()
            try {
                val query = "INSERT INTO UserDetails (Username, Password, ContactNum, EmailAdd, HouseAdd, HousePostal) VALUES (?, ?, ?, ?, ?, ?)"
                val stmt: PreparedStatement = connect!!.prepareStatement(query)
                stmt.setString(1, username)
                stmt.setString(2, password)
                stmt.setString(3, contactNumber)
                stmt.setString(4, emailAddress)
                stmt.setString(5, houseAddress)
                stmt.setString(6, postalCode)

                val rowsAffected = stmt.executeUpdate()
                if (rowsAffected > 0) {
                    isSuccess = true
                }
                stmt.close()
                connect?.close() //Closes connection
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            isSuccess
        }
    }
}

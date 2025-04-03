package com.example.inf2007_proj.DataManager

import com.example.inf2007_proj.DataModels.MedicationEntity
import com.example.inf2007_proj.Database.DBConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class MedicationDataManager {

    private val connectionClass: DBConnection = DBConnection()

    fun getMedicationsByUsername(username: String, callback: (List<MedicationEntity>) -> Unit) {
        println("DEBUG: getMedicationsByUsername started for user = $username")
        GlobalScope.launch(Dispatchers.IO) {
            val medications = mutableListOf<MedicationEntity>()
            val connect: Connection? = connectionClass.CONN()
            var stmt: PreparedStatement? = null
            var rs: ResultSet? = null

            try {
                val query = """
                    SELECT * FROM MedicationReminders 
                    WHERE Username = ? 
                    AND CONVERT(DATE, GETDATE()) BETWEEN CONVERT(DATE, Date_Start) AND CONVERT(DATE, Date_End)
                """
                stmt = connect!!.prepareStatement(query)
                stmt.setString(1, username)
                rs = stmt.executeQuery()

                while (rs.next()) {
                    val id = rs.getInt("ReminderID")
                    val username = rs.getString("Username")
                    val med_name = rs.getString("Med_Name")
                    val frequency = rs.getString("Frequency")
                    val dateStart = rs.getString("Date_Start")
                    val dateEnd = rs.getString("Date_End")
                    val firstDoseTime = rs.getString("FirstDoseTime")
                    val secondDoseTime = rs.getString("SecondDoseTime")
                    val thirdDoseTime = rs.getString("ThirdDoseTime")

                    medications.add(
                        MedicationEntity(
                            id = id,
                            username = username,
                            med_name = med_name,
                            frequency = frequency,
                            dateStart = dateStart,
                            dateEnd = dateEnd,
                            firstDoseTime = firstDoseTime,
                            secondDoseTime = secondDoseTime,
                            thirdDoseTime = thirdDoseTime
                        )
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                rs?.close()
                stmt?.close()
                connect?.close()
            }

            withContext(Dispatchers.Main) {
                callback(medications)
            }
        }
    }

    fun insertMedication(medication: MedicationEntity, callback: (Boolean) -> Unit) {
        println("DEBUG: insertMedication started.")
        GlobalScope.launch(Dispatchers.IO) {
            val connect: Connection? = connectionClass.CONN()
            var stmt: PreparedStatement? = null
            var success = false

            try {
                val query = """
                INSERT INTO MedicationReminders 
                (Username, Med_Name, Frequency, Date_Start, Date_End, FirstDoseTime, SecondDoseTime, ThirdDoseTime) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """
                stmt = connect!!.prepareStatement(query)
                stmt.setString(1, medication.username)
                stmt.setString(2, medication.med_name)
                stmt.setString(3, medication.frequency)
                stmt.setString(4, medication.dateStart)
                stmt.setString(5, medication.dateEnd)
                stmt.setString(6, medication.firstDoseTime)
                stmt.setString(7, medication.secondDoseTime)
                stmt.setString(8, medication.thirdDoseTime)

                stmt.executeUpdate()
                success = true
                println("DEBUG: Medication successfully inserted.")
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                stmt?.close()
                connect?.close()
            }

            withContext(Dispatchers.Main) {
                callback(success)
            }
        }
    }

    fun updateMedication(medication: MedicationEntity, callback: (Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val connect: Connection? = connectionClass.CONN()
            var stmt: PreparedStatement? = null
            var success = false

            try {
                val query = """
                UPDATE MedicationReminders 
                SET 
                    Med_Name = ?, 
                    Frequency = ?, 
                    Date_Start = ?, 
                    Date_End = ?, 
                    FirstDoseTime = ?, 
                    SecondDoseTime = ?, 
                    ThirdDoseTime = ? 
                WHERE 
                    ReminderID = ?
                """
                stmt = connect!!.prepareStatement(query)
                stmt.setString(1, medication.med_name)
                stmt.setString(2, medication.frequency)
                stmt.setString(3, medication.dateStart)
                stmt.setString(4, medication.dateEnd)
                stmt.setString(5, medication.firstDoseTime)
                stmt.setString(6, medication.secondDoseTime)
                stmt.setString(7, medication.thirdDoseTime)
                stmt.setInt(8, medication.id)

                stmt.executeUpdate()
                success = true
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                stmt?.close()
                connect?.close()
            }

            withContext(Dispatchers.Main) {
                callback(success)
            }
        }
    }

    fun getTodaysMedications(username: String, callback: (List<MedicationEntity>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val medications = mutableListOf<MedicationEntity>()
            val connect: Connection? = connectionClass.CONN()
            val today = java.time.LocalDate.now().toString()
            val query = """
            SELECT * FROM MedicationReminders 
            WHERE Username = ? 
            AND ? BETWEEN Date_Start AND Date_End
            """
            connect?.use { conn ->
                conn.prepareStatement(query).use { stmt ->
                    stmt.setString(1, username)
                    stmt.setString(2, today)
                    val rs = stmt.executeQuery()
                    while (rs.next()) {
                        medications.add(
                            MedicationEntity(
                                id = rs.getInt("ReminderID"),
                                username = rs.getString("Username"),
                                med_name = rs.getString("Med_Name"),
                                frequency = rs.getString("Frequency"),
                                dateStart = rs.getString("Date_Start"),
                                dateEnd = rs.getString("Date_End"),
                                firstDoseTime = rs.getString("FirstDoseTime"),
                                secondDoseTime = rs.getString("SecondDoseTime"),
                                thirdDoseTime = rs.getString("ThirdDoseTime")
                            )
                        )
                    }
                    rs.close()
                }
            }
            withContext(Dispatchers.Main) { callback(medications) }
        }
    }
    fun deleteMedication(medication: MedicationEntity, callback: (Boolean) -> Unit) {
        println("DEBUG: deleteMedication started for ID = ${medication.id}")
        GlobalScope.launch(Dispatchers.IO) {
            val connect: Connection? = connectionClass.CONN()
            var stmt: PreparedStatement? = null
            var success = false

            try {
                val query = "DELETE FROM MedicationReminders WHERE ReminderID = ?"
                stmt = connect!!.prepareStatement(query)
                stmt.setInt(1, medication.id)

                val rowsAffected = stmt.executeUpdate()
                success = rowsAffected > 0

                if (success) {
                    println("DEBUG: Medication with ID ${medication.id} successfully deleted.")
                } else {
                    println("DEBUG: No record deleted for ID ${medication.id}.")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                stmt?.close()
                connect?.close()
            }

            withContext(Dispatchers.Main) {
                callback(success)
            }
        }
    }

}
